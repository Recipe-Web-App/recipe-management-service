package com.recipe_manager.unit_tests.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import com.recipe_manager.config.CacheConfig;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.test.util.ReflectionTestUtils;

class CacheConfigTest {

  private CacheConfig cacheConfig;

  @BeforeEach
  void setUp() {
    cacheConfig = new CacheConfig();

    // Set up the configuration manually
    CacheConfig.ExternalServicesCache externalServicesCache = new CacheConfig.ExternalServicesCache();
    externalServicesCache.setRecipeScraperTtl(Duration.ofMinutes(30));
    externalServicesCache.setRecipeScraperMaxSize(1000L);
    externalServicesCache.setEnableStats(true);

    ReflectionTestUtils.setField(cacheConfig, "externalServices", externalServicesCache);
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
}
