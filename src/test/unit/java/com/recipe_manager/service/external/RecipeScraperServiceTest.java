package com.recipe_manager.service.external;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import com.recipe_manager.client.recipescraper.RecipeScraperClient;
import com.recipe_manager.config.ExternalServicesConfig;
import com.recipe_manager.model.dto.external.recipescraper.RecipeScraperShoppingDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

@ExtendWith(MockitoExtension.class)
@org.junit.jupiter.api.Tag("unit")
class RecipeScraperServiceTest {

  @Mock
  private RecipeScraperClient recipeScraperClient;

  @Mock
  private ExternalServicesConfig externalServicesConfig;

  @Mock
  private MeterRegistry meterRegistry;

  @Mock
  private Counter callsCounter;

  @Mock
  private Counter failuresCounter;

  @Mock
  private Timer responseTimer;

  @InjectMocks
  private RecipeScraperService recipeScraperService;

  @BeforeEach
  void setUp() {
    // No default setup needed
  }

  @Test
  @DisplayName("Should test fallback method returns correct data")
  void shouldTestFallbackMethod() {
    // Arrange
    RuntimeException exception = new RuntimeException("Service unavailable");

    // Act
    RecipeScraperShoppingDto result = recipeScraperService.getShoppingInfoFallback(123L, exception).join();

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getRecipeId()).isEqualTo(123L);
    assertThat(result.getIngredients()).isEmpty();
    assertThat(result.getTotalEstimatedCost()).isEqualByComparingTo(BigDecimal.ZERO);
  }

  @Test
  @DisplayName("Should check service availability when enabled")
  void shouldCheckServiceAvailabilityWhenEnabled() {
    // Arrange
    ExternalServicesConfig.RecipeScraperConfig enabledConfig = new ExternalServicesConfig.RecipeScraperConfig();
    enabledConfig.setEnabled(true);
    when(externalServicesConfig.getRecipeScraper()).thenReturn(enabledConfig);

    // Act
    boolean isAvailable = recipeScraperService.isServiceAvailable();

    // Assert
    assertThat(isAvailable).isTrue();
  }

  @Test
  @DisplayName("Should check service availability when disabled")
  void shouldCheckServiceAvailabilityWhenDisabled() {
    // Arrange
    ExternalServicesConfig.RecipeScraperConfig disabledConfig = new ExternalServicesConfig.RecipeScraperConfig();
    disabledConfig.setEnabled(false);
    when(externalServicesConfig.getRecipeScraper()).thenReturn(disabledConfig);

    // Act
    boolean isAvailable = recipeScraperService.isServiceAvailable();

    // Assert
    assertThat(isAvailable).isFalse();
  }

  @Test
  @DisplayName("Should handle fallback method directly")
  void shouldHandleFallbackMethodDirectly() {
    // Arrange
    RuntimeException exception = new RuntimeException("Service unavailable");

    // Act
    RecipeScraperShoppingDto result = recipeScraperService.getShoppingInfoFallback(123L, exception).join();

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getRecipeId()).isEqualTo(123L);
    assertThat(result.getIngredients()).isEmpty();
    assertThat(result.getTotalEstimatedCost()).isEqualByComparingTo(BigDecimal.ZERO);
  }

  @Test
  @DisplayName("Should handle fallback with different exception types")
  void shouldHandleFallbackWithDifferentExceptionTypes() {
    // Test with different exception types
    Exception[] exceptions = {
        new RuntimeException("Connection failed"),
        new IllegalStateException("Invalid state"),
        new NullPointerException("Null pointer"),
        new Exception("Generic exception")
    };

    for (Exception exception : exceptions) {
      RecipeScraperShoppingDto result = recipeScraperService.getShoppingInfoFallback(456L, exception).join();

      assertThat(result).isNotNull();
      assertThat(result.getRecipeId()).isEqualTo(456L);
      assertThat(result.getIngredients()).isEmpty();
      assertThat(result.getTotalEstimatedCost()).isEqualByComparingTo(BigDecimal.ZERO);
    }
  }

  @Test
  @DisplayName("Should handle null recipeId in fallback")
  void shouldHandleNullRecipeIdInFallback() {
    // Test with null recipeId
    RecipeScraperShoppingDto resultNullId = recipeScraperService.getShoppingInfoFallback(null,
        new RuntimeException("error")).join();
    assertThat(resultNullId).isNotNull();
    assertThat(resultNullId.getRecipeId()).isNull();
    assertThat(resultNullId.getIngredients()).isEmpty();
    assertThat(resultNullId.getTotalEstimatedCost()).isEqualByComparingTo(BigDecimal.ZERO);
  }

  @Test
  @DisplayName("Should handle metrics initialization gracefully in unit tests")
  void shouldHandleMetricsInitializationGracefullyInUnitTests() {
    // The initMetrics method attempts to register metrics with MeterRegistry.
    // In unit tests with mocked MeterRegistry, the complex metric builder chain
    // cannot be easily mocked without extensive setup. This method is primarily
    // tested in component/integration tests where a real MeterRegistry is
    // available.

    // For unit tests, we verify the method doesn't fail when meterRegistry is null
    RecipeScraperService serviceWithNullRegistry = new RecipeScraperService();
    serviceWithNullRegistry.initMetrics(); // Should not throw

    assertThat(serviceWithNullRegistry).isNotNull();
  }

  @Test
  @DisplayName("Should handle null MeterRegistry in initMetrics")
  void shouldHandleNullMeterRegistryInInitMetrics() {
    // Arrange - create service with null meter registry
    RecipeScraperService serviceWithNullRegistry = new RecipeScraperService();
    // The meterRegistry field will be null by default

    // Act & Assert - should not throw
    serviceWithNullRegistry.initMetrics();
  }

  @Test
  @DisplayName("Should return fallback when service is disabled via getShoppingInfo")
  void shouldReturnFallbackWhenServiceIsDisabledViaGetShoppingInfo() {
    // The getShoppingInfo method has resilience annotations (@CircuitBreaker,
    // @Retry, @TimeLimiter)
    // that require Spring AOP context to work properly. In unit tests, these
    // annotations are ignored,
    // but we can test the service disabled scenario since it's checked early in the
    // method.

    // Arrange
    Long recipeId = 123L;
    ExternalServicesConfig.RecipeScraperConfig config = new ExternalServicesConfig.RecipeScraperConfig();
    config.setEnabled(false);

    when(externalServicesConfig.getRecipeScraper()).thenReturn(config);

    // Act
    RecipeScraperShoppingDto result = recipeScraperService.getShoppingInfo(recipeId).join();

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getRecipeId()).isEqualTo(recipeId);
    assertThat(result.getIngredients()).isEmpty();
    assertThat(result.getTotalEstimatedCost()).isEqualByComparingTo(BigDecimal.ZERO);
    verify(recipeScraperClient, never()).getShoppingInfo(anyLong());
  }

  @Test
  @DisplayName("Should document getShoppingInfo method behavior")
  void shouldDocumentGetShoppingInfoMethodBehavior() {
    // The getShoppingInfo method contains complex logic with resilience annotations
    // (@CircuitBreaker, @Retry, @TimeLimiter) that require Spring AOP context to
    // function properly.
    //
    // Unit tests cannot easily test this method because:
    // 1. Resilience4j annotations require Spring context to work
    // 2. The method uses Timer.recordCallable() which is difficult to mock
    // 3. MDC operations are static and hard to verify in unit tests
    //
    // The method's behavior is comprehensively tested in:
    // - Component tests:
    // src/test/component/.../RecipeScraperServiceComponentTest.java
    // - Integration tests:
    // src/test/integration/.../RecipeScraperClientIntegrationTest.java
    //
    // This unit test class focuses on:
    // - The fallback method (getShoppingInfoFallback)
    // - Service availability checks (isServiceAvailable)
    // - Metrics initialization (initMetrics)

    // Test the service disabled scenario which is checked early in getShoppingInfo
    Long recipeId = 123L;
    ExternalServicesConfig.RecipeScraperConfig config = new ExternalServicesConfig.RecipeScraperConfig();
    config.setEnabled(false);

    when(externalServicesConfig.getRecipeScraper()).thenReturn(config);

    RecipeScraperShoppingDto result = recipeScraperService.getShoppingInfo(recipeId).join();

    // When service is disabled, it returns fallback data
    assertThat(result).isNotNull();
    assertThat(result.getRecipeId()).isEqualTo(recipeId);
    assertThat(result.getIngredients()).isEmpty();
    assertThat(result.getTotalEstimatedCost()).isEqualByComparingTo(BigDecimal.ZERO);
    verify(recipeScraperClient, never()).getShoppingInfo(anyLong());
  }

  @Test
  @DisplayName("Should return correct availability based on configuration")
  void shouldReturnCorrectAvailabilityBasedOnConfiguration() {
    // Test when enabled
    ExternalServicesConfig.RecipeScraperConfig enabledConfig = new ExternalServicesConfig.RecipeScraperConfig();
    enabledConfig.setEnabled(true);
    when(externalServicesConfig.getRecipeScraper()).thenReturn(enabledConfig);

    assertThat(recipeScraperService.isServiceAvailable()).isTrue();

    // Test when disabled
    ExternalServicesConfig.RecipeScraperConfig disabledConfig = new ExternalServicesConfig.RecipeScraperConfig();
    disabledConfig.setEnabled(false);
    when(externalServicesConfig.getRecipeScraper()).thenReturn(disabledConfig);

    assertThat(recipeScraperService.isServiceAvailable()).isFalse();
  }

  @Test
  @DisplayName("Should handle edge cases for service availability")
  void shouldHandleEdgeCasesForServiceAvailability() {
    // Test with null config
    when(externalServicesConfig.getRecipeScraper()).thenReturn(null);

    // This should throw NullPointerException
    try {
      recipeScraperService.isServiceAvailable();
      // If we reach here, the implementation handles null gracefully (unexpected)
      assertThat(true).as("Method should have thrown NullPointerException").isFalse();
    } catch (NullPointerException e) {
      // Expected behavior for null config
      assertThat(e).isInstanceOf(NullPointerException.class);
    }
  }
}
