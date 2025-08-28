package com.recipe_manager.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.Duration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = ExternalServicesConfig.class)
@TestPropertySource(properties = {
    "external.services.recipe-scraper.baseUrl=http://test-scraper:8080",
    "external.services.recipe-scraper.timeout=PT5S",
    "external.services.recipe-scraper.apiKey=test-api-key",
    "external.services.recipe-scraper.shoppingInfoPath=/api/recipe-scraper/{recipeId}/shopping-info",
    "external.services.recipe-scraper.enabled=true",
    "external.services.media-manager.baseUrl=http://test-media:3000",
    "external.services.media-manager.timeout=PT5S",
    "external.services.media-manager.enabled=true",
    "external.services.common.connectTimeout=PT3S",
    "external.services.common.readTimeout=PT10S"
})
class ExternalServicesConfigTest {

  @Autowired
  private ExternalServicesConfig config;

  @Test
  @DisplayName("Should load recipe scraper configuration properties")
  void shouldLoadRecipeScraperConfig() {
    assertAll(
        () -> assertThat(config.getRecipeScraper().getBaseUrl())
            .isEqualTo("http://test-scraper:8080"),
        () -> assertThat(config.getRecipeScraper().getTimeout())
            .isEqualTo(Duration.ofSeconds(5)),
        () -> assertThat(config.getRecipeScraper().getApiKey())
            .isEqualTo("test-api-key"),
        () -> assertThat(config.getRecipeScraper().getShoppingInfoPath())
            .isEqualTo("/api/recipe-scraper/{recipeId}/shopping-info"),
        () -> assertThat(config.getRecipeScraper().getEnabled())
            .isTrue());
  }

  @Test
  @DisplayName("Should load common configuration properties")
  void shouldLoadCommonConfig() {
    assertAll(
        () -> assertThat(config.getCommon().getConnectTimeout())
            .isEqualTo(Duration.ofSeconds(3)),
        () -> assertThat(config.getCommon().getReadTimeout())
            .isEqualTo(Duration.ofSeconds(10)));
  }

  @Test
  @DisplayName("Should load media manager configuration properties")
  void shouldLoadMediaManagerConfig() {
    assertAll(
        () -> assertThat(config.getMediaManager().getBaseUrl())
            .isEqualTo("http://test-media:3000"),
        () -> assertThat(config.getMediaManager().getTimeout())
            .isEqualTo(Duration.ofSeconds(5)),
        () -> assertThat(config.getMediaManager().getEnabled())
            .isTrue());
  }

  @Test
  @DisplayName("Should validate timeout durations")
  void shouldValidateTimeoutDurations() {
    assertAll(
        () -> assertThat(config.getRecipeScraper().getTimeout())
            .isGreaterThan(Duration.ZERO),
        () -> assertThat(config.getMediaManager().getTimeout())
            .isGreaterThan(Duration.ZERO),
        () -> assertThat(config.getCommon().getConnectTimeout())
            .isGreaterThan(Duration.ZERO),
        () -> assertThat(config.getCommon().getReadTimeout())
            .isGreaterThan(Duration.ZERO));
  }
}
