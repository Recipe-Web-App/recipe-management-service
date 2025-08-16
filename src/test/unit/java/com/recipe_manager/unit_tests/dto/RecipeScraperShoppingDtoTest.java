package com.recipe_manager.unit_tests.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.recipe_manager.model.dto.external.recipescraper.IngredientShoppingInfoDto;
import com.recipe_manager.model.dto.external.recipescraper.RecipeScraperShoppingDto;
import com.recipe_manager.model.enums.IngredientUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RecipeScraperShoppingDtoTest {

  @Test
  @DisplayName("Should build RecipeScraperShoppingDto with all fields")
  void shouldBuildWithAllFields() {
    // Arrange
    Long recipeId = 123L;
    BigDecimal totalCost = new BigDecimal("15.99");
    Map<String, IngredientShoppingInfoDto> ingredients = new HashMap<>();
    ingredients.put("salt", IngredientShoppingInfoDto.builder()
        .ingredientName("Salt")
        .quantity(new BigDecimal("1.0"))
        .unit(IngredientUnit.TSP)
        .estimatedPrice(new BigDecimal("2.99"))
        .build());

    // Act
    RecipeScraperShoppingDto dto = RecipeScraperShoppingDto.builder()
        .recipeId(recipeId)
        .ingredients(ingredients)
        .totalEstimatedCost(totalCost)
        .build();

    // Assert
    assertThat(dto.getRecipeId()).isEqualTo(recipeId);
    assertThat(dto.getTotalEstimatedCost()).isEqualByComparingTo(totalCost);
    assertThat(dto.getIngredients()).hasSize(1);
    assertThat(dto.getIngredients().get("salt").getIngredientName()).isEqualTo("Salt");
  }

  @Test
  @DisplayName("Should handle empty ingredients map")
  void shouldHandleEmptyIngredients() {
    // Arrange & Act
    RecipeScraperShoppingDto dto = RecipeScraperShoppingDto.builder()
        .recipeId(123L)
        .ingredients(new HashMap<>())
        .totalEstimatedCost(BigDecimal.ZERO)
        .build();

    // Assert
    assertThat(dto.getIngredients()).isEmpty();
    assertThat(dto.getTotalEstimatedCost()).isEqualByComparingTo(BigDecimal.ZERO);
  }

  @Test
  @DisplayName("Should support toString method")
  void shouldSupportToString() {
    // Arrange & Act
    RecipeScraperShoppingDto dto = RecipeScraperShoppingDto.builder()
        .recipeId(123L)
        .ingredients(new HashMap<>())
        .totalEstimatedCost(BigDecimal.ZERO)
        .build();

    // Assert
    String toString = dto.toString();
    assertThat(toString).contains("RecipeScraperShoppingDto");
    assertThat(toString).contains("123");
  }
}
