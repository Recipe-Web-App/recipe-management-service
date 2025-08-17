package com.recipe_manager.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import com.github.benmanes.caffeine.cache.Caffeine;

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

  /** External services cache configuration. */
  @Valid @NotNull private ExternalServicesCache externalServices = new ExternalServicesCache();

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
}
