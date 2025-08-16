package com.recipe_manager.unit_tests.client;

import static org.assertj.core.api.Assertions.assertThat;

import com.recipe_manager.model.dto.external.recipescraper.RecipeScraperShoppingDto;
import com.recipe_manager.service.external.RecipeScraperFallback;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RecipeScraperFallbackTest {

  private RecipeScraperFallback fallback;

  @BeforeEach
  void setUp() {
    fallback = new RecipeScraperFallback();
  }

  @Test
  @DisplayName("Should return empty shopping info from fallback method")
  void shouldReturnEmptyShoppingInfoFromFallback() {
    RecipeScraperShoppingDto result = fallback.getShoppingInfo(123L);

    assertThat(result).isNotNull();
    assertThat(result.getRecipeId()).isEqualTo(123L);
    assertThat(result.getIngredients()).isEmpty();
    assertThat(result.getTotalEstimatedCost()).isEqualByComparingTo(java.math.BigDecimal.ZERO);
  }

  @Test
  @DisplayName("Should handle null recipe ID")
  void shouldHandleNullRecipeId() {
    RecipeScraperShoppingDto result = fallback.getShoppingInfo(null);

    assertThat(result).isNotNull();
    assertThat(result.getRecipeId()).isNull();
    assertThat(result.getIngredients()).isEmpty();
    assertThat(result.getTotalEstimatedCost()).isEqualByComparingTo(java.math.BigDecimal.ZERO);
  }

  @Test
  @DisplayName("Should handle negative recipe ID")
  void shouldHandleNegativeRecipeId() {
    RecipeScraperShoppingDto result = fallback.getShoppingInfo(-1L);

    assertThat(result).isNotNull();
    assertThat(result.getRecipeId()).isEqualTo(-1L);
    assertThat(result.getIngredients()).isEmpty();
    assertThat(result.getTotalEstimatedCost()).isEqualByComparingTo(java.math.BigDecimal.ZERO);
  }

  @Test
  @DisplayName("Should handle very large recipe ID")
  void shouldHandleVeryLargeRecipeId() {
    Long largeId = Long.MAX_VALUE;
    RecipeScraperShoppingDto result = fallback.getShoppingInfo(largeId);

    assertThat(result).isNotNull();
    assertThat(result.getRecipeId()).isEqualTo(largeId);
    assertThat(result.getIngredients()).isEmpty();
    assertThat(result.getTotalEstimatedCost()).isEqualByComparingTo(java.math.BigDecimal.ZERO);
  }
}
