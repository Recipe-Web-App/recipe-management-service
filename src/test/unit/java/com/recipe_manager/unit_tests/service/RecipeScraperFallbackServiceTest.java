package com.recipe_manager.unit_tests.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;

import com.recipe_manager.model.dto.external.recipescraper.RecipeScraperShoppingDto;
import com.recipe_manager.service.external.RecipeScraperFallback;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RecipeScraperFallbackServiceTest {

  private RecipeScraperFallback fallbackService;

  @BeforeEach
  void setUp() {
    fallbackService = new RecipeScraperFallback();
  }

  @Test
  @DisplayName("Should return empty shopping info from service fallback")
  void shouldReturnEmptyShoppingInfoFromServiceFallback() {
    RecipeScraperShoppingDto result = fallbackService.getShoppingInfo(123L);

    assertThat(result).isNotNull();
    assertThat(result.getRecipeId()).isEqualTo(123L);
    assertThat(result.getIngredients()).isEmpty();
    assertThat(result.getTotalEstimatedCost()).isEqualByComparingTo(java.math.BigDecimal.ZERO);
  }

  @Test
  @DisplayName("Should handle null recipe ID in service fallback")
  void shouldHandleNullRecipeIdInServiceFallback() {
    RecipeScraperShoppingDto result = fallbackService.getShoppingInfo(null);

    assertThat(result).isNotNull();
    assertThat(result.getRecipeId()).isNull();
    assertThat(result.getIngredients()).isEmpty();
    assertThat(result.getTotalEstimatedCost()).isEqualByComparingTo(java.math.BigDecimal.ZERO);
  }

  @Test
  @DisplayName("Should handle negative recipe ID in service fallback")
  void shouldHandleNegativeRecipeIdInServiceFallback() {
    RecipeScraperShoppingDto result = fallbackService.getShoppingInfo(-1L);

    assertThat(result).isNotNull();
    assertThat(result.getRecipeId()).isEqualTo(-1L);
    assertThat(result.getIngredients()).isEmpty();
    assertThat(result.getTotalEstimatedCost()).isEqualByComparingTo(java.math.BigDecimal.ZERO);
  }

  @Test
  @DisplayName("Should handle very large recipe ID")
  void shouldHandleVeryLargeRecipeId() {
    Long largeId = Long.MAX_VALUE;
    RecipeScraperShoppingDto result = fallbackService.getShoppingInfo(largeId);

    assertThat(result).isNotNull();
    assertThat(result.getRecipeId()).isEqualTo(largeId);
    assertThat(result.getIngredients()).isEmpty();
    assertThat(result.getTotalEstimatedCost()).isEqualByComparingTo(java.math.BigDecimal.ZERO);
  }

  @Test
  @DisplayName("Should use thread-safe empty map")
  void shouldUseThreadSafeEmptyMap() {
    RecipeScraperShoppingDto result = fallbackService.getShoppingInfo(123L);

    assertThat(result.getIngredients()).isSameAs(Collections.emptyMap());
  }

  @Test
  @DisplayName("Should handle zero recipe ID in service fallback")
  void shouldHandleZeroRecipeIdInServiceFallback() {
    RecipeScraperShoppingDto result = fallbackService.getShoppingInfo(0L);

    assertThat(result).isNotNull();
    assertThat(result.getRecipeId()).isEqualTo(0L);
    assertThat(result.getIngredients()).isEmpty();
    assertThat(result.getTotalEstimatedCost()).isEqualByComparingTo(java.math.BigDecimal.ZERO);
  }

  @Test
  @DisplayName("Should use thread-safe singleton empty map")
  void shouldUseThreadSafeSingletonEmptyMap() {
    RecipeScraperShoppingDto result1 = fallbackService.getShoppingInfo(1L);
    RecipeScraperShoppingDto result2 = fallbackService.getShoppingInfo(2L);

    // Should use the same singleton instance for better performance and thread
    // safety
    assertThat(result1.getIngredients()).isSameAs(Collections.emptyMap());
    assertThat(result2.getIngredients()).isSameAs(Collections.emptyMap());
    assertThat(result1.getIngredients()).isSameAs(result2.getIngredients());
  }
}
