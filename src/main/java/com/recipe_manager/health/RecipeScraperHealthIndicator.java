package com.recipe_manager.health;

import java.time.Duration;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import com.recipe_manager.config.ExternalServicesConfig;
import com.recipe_manager.service.external.RecipeScraperService;

/**
 * Health indicator for recipe scraper external service. Monitors the availability and response time
 * of the recipe scraper service.
 */
@Component("recipeScraperHealth")
public final class RecipeScraperHealthIndicator implements HealthIndicator {

  /** Service for recipe scraper operations. */
  @Autowired private RecipeScraperService recipeScraperService;

  /** Configuration for external services. */
  @Autowired private ExternalServicesConfig externalServicesConfig;

  /**
   * Performs health check for the recipe scraper service.
   *
   * @return health status with details about service availability
   */
  @Override
  public Health health() {
    try {
      if (!externalServicesConfig.getRecipeScraper().getEnabled()) {
        return Health.down()
            .withDetail("status", "disabled")
            .withDetail("reason", "Recipe scraper service is disabled in configuration")
            .build();
      }

      // Perform a simple health check
      Instant start = Instant.now();
      boolean isAvailable = recipeScraperService.isServiceAvailable();
      Duration responseTime = Duration.between(start, Instant.now());

      if (isAvailable) {
        return Health.up()
            .withDetail("status", "available")
            .withDetail("responseTime", responseTime.toMillis() + "ms")
            .withDetail("baseUrl", externalServicesConfig.getRecipeScraper().getBaseUrl())
            .withDetail(
                "timeout", externalServicesConfig.getRecipeScraper().getTimeout().toString())
            .build();
      } else {
        return Health.down()
            .withDetail("status", "unavailable")
            .withDetail("reason", "Service check failed")
            .withDetail("responseTime", responseTime.toMillis() + "ms")
            .build();
      }
    } catch (Exception e) {
      return Health.down()
          .withDetail("status", "error")
          .withDetail("error", e.getMessage())
          .withDetail("exception", e.getClass().getSimpleName())
          .build();
    }
  }
}
