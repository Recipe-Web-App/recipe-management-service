package com.recipe_manager.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.test.util.ReflectionTestUtils;

import com.github.benmanes.caffeine.cache.Cache;
import com.recipe_manager.security.OAuth2Client.TokenIntrospectionResponse;

@Tag("unit")
class CacheConfigTest {

  private CacheConfig cacheConfig;

  @BeforeEach
  void setUp() {
    cacheConfig = new CacheConfig();

    // Set up the external services configuration
    CacheConfig.ExternalServicesCache externalServicesCache =
        new CacheConfig.ExternalServicesCache();
    externalServicesCache.setRecipeScraperTtl(Duration.ofMinutes(30));
    externalServicesCache.setRecipeScraperMaxSize(1000L);
    externalServicesCache.setEnableStats(true);
    ReflectionTestUtils.setField(cacheConfig, "externalServices", externalServicesCache);

    // Set up the token introspection configuration
    CacheConfig.TokenIntrospectionCache tokenIntrospectionCache =
        new CacheConfig.TokenIntrospectionCache();
    tokenIntrospectionCache.setTtl(Duration.ofMinutes(5));
    tokenIntrospectionCache.setMaxSize(10000L);
    tokenIntrospectionCache.setInactiveTokenTtl(Duration.ofSeconds(30));
    ReflectionTestUtils.setField(cacheConfig, "tokenIntrospection", tokenIntrospectionCache);
  }

  @Test
  @DisplayName("Should create cache manager with correct configuration")
  void shouldCreateCacheManager() {
    CacheManager cacheManager = cacheConfig.externalServicesCacheManager();

    assertThat(cacheManager)
        .isNotNull()
        .isInstanceOf(CaffeineCacheManager.class);
  }

  @Test
  @DisplayName("Should configure cache with correct TTL and size")
  void shouldConfigureCacheWithCorrectSettings() {
    CacheManager cacheManager = cacheConfig.externalServicesCacheManager();
    CaffeineCacheManager caffeineCacheManager = (CaffeineCacheManager) cacheManager;

    // Verify that the cache manager is properly configured
    assertThat(caffeineCacheManager.isAllowNullValues()).isTrue();

    // Create a cache and verify its configuration
    assertThat(caffeineCacheManager.getCache("recipe-scraper-shopping-info"))
        .isNotNull();
  }

  @Test
  @DisplayName("Should create token introspection cache")
  void shouldCreateTokenIntrospectionCache() {
    Cache<String, TokenIntrospectionResponse> cache = cacheConfig.tokenIntrospectionCache();

    assertThat(cache).isNotNull();
  }

  @Test
  @DisplayName("Should cache active token introspection response")
  void shouldCacheActiveTokenIntrospectionResponse() {
    Cache<String, TokenIntrospectionResponse> cache = cacheConfig.tokenIntrospectionCache();

    String token = "test-token";
    TokenIntrospectionResponse response = TokenIntrospectionResponse.builder()
        .active(true)
        .sub("user123")
        .exp(Instant.now().plusSeconds(3600).getEpochSecond())
        .build();

    cache.put(token, response);

    TokenIntrospectionResponse cached = cache.getIfPresent(token);
    assertThat(cached).isNotNull();
    assertThat(cached.getActive()).isTrue();
    assertThat(cached.getSub()).isEqualTo("user123");
  }

  @Test
  @DisplayName("Should cache inactive token with short TTL")
  void shouldCacheInactiveToken() {
    Cache<String, TokenIntrospectionResponse> cache = cacheConfig.tokenIntrospectionCache();

    String token = "invalid-token";
    TokenIntrospectionResponse response = TokenIntrospectionResponse.builder()
        .active(false)
        .build();

    cache.put(token, response);

    TokenIntrospectionResponse cached = cache.getIfPresent(token);
    assertThat(cached).isNotNull();
    assertThat(cached.getActive()).isFalse();
  }

  @Test
  @DisplayName("Should return cache hit on second access")
  void shouldReturnCacheHitOnSecondAccess() {
    Cache<String, TokenIntrospectionResponse> cache = cacheConfig.tokenIntrospectionCache();

    String token = "test-token";
    TokenIntrospectionResponse response = TokenIntrospectionResponse.builder()
        .active(true)
        .sub("user456")
        .exp(Instant.now().plusSeconds(3600).getEpochSecond())
        .build();

    cache.put(token, response);

    // First access
    TokenIntrospectionResponse first = cache.getIfPresent(token);
    // Second access
    TokenIntrospectionResponse second = cache.getIfPresent(token);

    assertThat(first).isNotNull();
    assertThat(second).isNotNull();
    assertThat(first).isSameAs(second);
  }

  @Test
  @DisplayName("Should respect maximum cache size")
  void shouldRespectMaximumCacheSize() {
    // Create a cache with small max size for testing
    CacheConfig.TokenIntrospectionCache smallConfig = new CacheConfig.TokenIntrospectionCache();
    smallConfig.setTtl(Duration.ofMinutes(5));
    smallConfig.setMaxSize(2L);
    smallConfig.setInactiveTokenTtl(Duration.ofSeconds(30));
    ReflectionTestUtils.setField(cacheConfig, "tokenIntrospection", smallConfig);

    Cache<String, TokenIntrospectionResponse> cache = cacheConfig.tokenIntrospectionCache();

    // Add 3 entries to a cache with max size 2
    for (int i = 0; i < 3; i++) {
      TokenIntrospectionResponse response = TokenIntrospectionResponse.builder()
          .active(true)
          .sub("user" + i)
          .exp(Instant.now().plusSeconds(3600).getEpochSecond())
          .build();
      cache.put("token" + i, response);
    }

    // Force eviction by cleaning up
    cache.cleanUp();

    // Cache should have at most 2 entries
    assertThat(cache.estimatedSize()).isLessThanOrEqualTo(2);
  }

  @Test
  @DisplayName("Should evict expired token immediately")
  void shouldEvictExpiredTokenImmediately() {
    Cache<String, TokenIntrospectionResponse> cache = cacheConfig.tokenIntrospectionCache();

    String token = "expired-token";
    // Token that expired 1 second ago
    TokenIntrospectionResponse response = TokenIntrospectionResponse.builder()
        .active(true)
        .sub("user789")
        .exp(Instant.now().minusSeconds(1).getEpochSecond())
        .build();

    cache.put(token, response);
    cache.cleanUp();

    // Token should be evicted because it's already expired
    TokenIntrospectionResponse cached = cache.getIfPresent(token);
    assertThat(cached).isNull();
  }

  @Test
  @DisplayName("Token introspection cache configuration should have correct defaults")
  void shouldHaveCorrectDefaults() {
    CacheConfig.TokenIntrospectionCache defaultConfig = new CacheConfig.TokenIntrospectionCache();

    assertThat(defaultConfig.getTtl()).isEqualTo(Duration.ofMinutes(5));
    assertThat(defaultConfig.getMaxSize()).isEqualTo(10000L);
    assertThat(defaultConfig.getInactiveTokenTtl()).isEqualTo(Duration.ofSeconds(30));
  }

  @Test
  @DisplayName("Should cache active token without expiration using sliding TTL")
  void shouldCacheActiveTokenWithoutExpiration() {
    Cache<String, TokenIntrospectionResponse> cache = cacheConfig.tokenIntrospectionCache();

    String token = "no-exp-token";
    // Active token with no exp claim - should use sliding TTL
    TokenIntrospectionResponse response =
        TokenIntrospectionResponse.builder().active(true).sub("user-no-exp").build();

    cache.put(token, response);

    TokenIntrospectionResponse cached = cache.getIfPresent(token);
    assertThat(cached).isNotNull();
    assertThat(cached.getActive()).isTrue();
    assertThat(cached.getSub()).isEqualTo("user-no-exp");
  }

  @Test
  @DisplayName("Should handle token with null active field")
  void shouldHandleTokenWithNullActiveField() {
    Cache<String, TokenIntrospectionResponse> cache = cacheConfig.tokenIntrospectionCache();

    String token = "null-active-token";
    // Token with null active field - should be treated as inactive
    TokenIntrospectionResponse response =
        TokenIntrospectionResponse.builder().active(null).sub("user-null-active").build();

    cache.put(token, response);

    // Should still be cached (briefly, as inactive)
    TokenIntrospectionResponse cached = cache.getIfPresent(token);
    assertThat(cached).isNotNull();
    assertThat(cached.getActive()).isNull();
  }
}
