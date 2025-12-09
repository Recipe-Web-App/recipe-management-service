package com.recipe_manager.health;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.recipe_manager.config.ExternalServicesConfig;
import com.recipe_manager.service.external.RecipeScraperService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.Status;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class RecipeScraperHealthIndicatorTest {

  @Mock
  private RecipeScraperService recipeScraperService;

  @Mock
  private ExternalServicesConfig externalServicesConfig;

  @Mock
  private ExternalServicesConfig.RecipeScraperConfig recipeScraperConfig;

  private RecipeScraperHealthIndicator healthIndicator;

  @BeforeEach
  void setUp() {
    healthIndicator = new RecipeScraperHealthIndicator();
    ReflectionTestUtils.setField(healthIndicator, "recipeScraperService", recipeScraperService);
    ReflectionTestUtils.setField(healthIndicator, "externalServicesConfig", externalServicesConfig);

    // Setup default config behavior
    when(externalServicesConfig.getRecipeScraper()).thenReturn(recipeScraperConfig);
    when(recipeScraperConfig.getEnabled()).thenReturn(true);
  }

  @Test
  @DisplayName("Should return UP status when service is healthy")
  void shouldReturnUpStatus() {
    // Arrange
    when(recipeScraperService.isServiceAvailable()).thenReturn(true);
    when(recipeScraperConfig.getBaseUrl()).thenReturn("http://test-url");
    when(recipeScraperConfig.getTimeout()).thenReturn(java.time.Duration.ofSeconds(5));

    // Act
    Health health = healthIndicator.health();

    // Assert
    assertThat(health.getStatus()).isEqualTo(Status.UP);
    assertThat(health.getDetails())
        .containsEntry("status", "available")
        .containsKey("responseTime")
        .containsKey("baseUrl")
        .containsKey("timeout");
  }

  @Test
  @DisplayName("Should return DOWN status when service is unhealthy")
  void shouldReturnDownStatus() {
    // Arrange
    when(recipeScraperService.isServiceAvailable()).thenReturn(false);

    // Act
    Health health = healthIndicator.health();

    // Assert
    assertThat(health.getStatus()).isEqualTo(Status.DOWN);
    assertThat(health.getDetails())
        .containsEntry("status", "unavailable")
        .containsEntry("reason", "Service check failed")
        .containsKey("responseTime");
  }

  @Test
  @DisplayName("Should handle disabled health checks")
  void shouldHandleDisabledHealthChecks() {
    // Arrange
    when(recipeScraperConfig.getEnabled()).thenReturn(false);

    // Act
    Health health = healthIndicator.health();

    // Assert
    assertThat(health.getStatus()).isEqualTo(Status.DOWN);
    assertThat(health.getDetails())
        .containsEntry("status", "disabled")
        .containsEntry("reason", "Recipe scraper service is disabled in configuration");
  }
}
