package com.recipe_manager.config;

import java.time.Duration;
import java.time.Instant;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.recipe_manager.security.OAuth2Client.TokenIntrospectionResponse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * Configuration for caching external service responses. Provides TTL-based caching with
 * configurable cache sizes and expiration times.
 */
@Configuration
@EnableCaching
@ConfigurationProperties(prefix = "cache")
@Validated
@Data
public class CacheConfig {

  /** Default sliding window TTL in minutes for token introspection cache. */
  private static final int DEFAULT_TTL_MINUTES = 5;

  /** Default maximum cache size for token introspection. */
  private static final long DEFAULT_MAX_SIZE = 10000L;

  /** Default TTL in seconds for inactive (revoked) tokens. */
  private static final int DEFAULT_INACTIVE_TTL_SECONDS = 30;

  /** Number of nanoseconds per second for time conversion. */
  private static final long NANOS_PER_SECOND = 1_000_000_000L;

  /** External services cache configuration. */
  @Valid @NotNull private ExternalServicesCache externalServices = new ExternalServicesCache();

  /** Token introspection cache configuration. */
  @Valid @NotNull
  private TokenIntrospectionCache tokenIntrospection = new TokenIntrospectionCache();

  @Data
  public static class TokenIntrospectionCache {
    /** Sliding window TTL for token introspection cache. */
    @NotNull private Duration ttl = Duration.ofMinutes(DEFAULT_TTL_MINUTES);

    /** Maximum cache size for token introspection. */
    @Positive private long maxSize = DEFAULT_MAX_SIZE;

    /** TTL for inactive (revoked) tokens to prevent hammering auth server. */
    @NotNull private Duration inactiveTokenTtl = Duration.ofSeconds(DEFAULT_INACTIVE_TTL_SECONDS);
  }

  @Data
  public static class ExternalServicesCache {
    /** Recipe scraper cache TTL. */
    @NotNull private Duration recipeScraperTtl;

    /** Maximum cache size for recipe scraper. */
    @Positive private long recipeScraperMaxSize;

    /** Whether to enable cache statistics. */
    @NotNull private Boolean enableStats;
  }

  /**
   * Creates a cache manager for external service responses.
   *
   * @return configured cache manager
   */
  @Bean("externalServicesCacheManager")
  public CacheManager externalServicesCacheManager() {
    CaffeineCacheManager cacheManager = new CaffeineCacheManager();
    cacheManager.setCaffeine(
        Caffeine.newBuilder()
            .maximumSize(externalServices.getRecipeScraperMaxSize())
            .expireAfterWrite(externalServices.getRecipeScraperTtl())
            .recordStats());
    cacheManager.setCacheNames(java.util.List.of("recipe-scraper-shopping-info"));
    cacheManager.setAsyncCacheMode(true); // Enable async cache mode for CompletableFuture support
    return cacheManager;
  }

  /**
   * Creates a cache for token introspection responses with sliding window expiration.
   *
   * <p>The cache uses a custom expiry policy that:
   *
   * <ul>
   *   <li>Refreshes the TTL on each access (sliding window)
   *   <li>Never exceeds the token's actual expiration time
   *   <li>Uses a shorter TTL for inactive/revoked tokens
   * </ul>
   *
   * @return configured cache for token introspection responses
   */
  @Bean("tokenIntrospectionCache")
  public Cache<String, TokenIntrospectionResponse> tokenIntrospectionCache() {
    final long slidingTtlNanos = tokenIntrospection.getTtl().toNanos();
    final long inactiveTtlNanos = tokenIntrospection.getInactiveTokenTtl().toNanos();

    return Caffeine.newBuilder()
        .maximumSize(tokenIntrospection.getMaxSize())
        .expireAfter(
            new Expiry<String, TokenIntrospectionResponse>() {
              @Override
              public long expireAfterCreate(
                  final String key,
                  final TokenIntrospectionResponse response,
                  final long currentTime) {
                return calculateExpiry(response, slidingTtlNanos, inactiveTtlNanos);
              }

              @Override
              public long expireAfterUpdate(
                  final String key,
                  final TokenIntrospectionResponse response,
                  final long currentTime,
                  final long currentDuration) {
                return calculateExpiry(response, slidingTtlNanos, inactiveTtlNanos);
              }

              @Override
              public long expireAfterRead(
                  final String key,
                  final TokenIntrospectionResponse response,
                  final long currentTime,
                  final long currentDuration) {
                // Sliding window: refresh TTL on read, but cap at token expiry
                return calculateExpiry(response, slidingTtlNanos, inactiveTtlNanos);
              }
            })
        .recordStats()
        .build();
  }

  /**
   * Calculates the cache expiry duration for a token introspection response.
   *
   * @param response the introspection response
   * @param slidingTtlNanos the sliding window TTL in nanoseconds
   * @param inactiveTtlNanos the TTL for inactive tokens in nanoseconds
   * @return expiry duration in nanoseconds
   */
  private static long calculateExpiry(
      final TokenIntrospectionResponse response,
      final long slidingTtlNanos,
      final long inactiveTtlNanos) {
    // Inactive tokens get a short TTL to prevent hammering auth server
    if (response.getActive() == null || !response.getActive()) {
      return inactiveTtlNanos;
    }

    // Active tokens: use min(slidingTtl, timeUntilTokenExpiry)
    if (response.getExp() != null) {
      long nowSeconds = Instant.now().getEpochSecond();
      long remainingSeconds = response.getExp() - nowSeconds;
      if (remainingSeconds <= 0) {
        return 0; // Token already expired, evict immediately
      }
      long remainingNanos = remainingSeconds * NANOS_PER_SECOND;
      return Math.min(slidingTtlNanos, remainingNanos);
    }

    // No expiration claim, use sliding TTL
    return slidingTtlNanos;
  }
}
