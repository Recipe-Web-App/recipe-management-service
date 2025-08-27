package com.recipe_manager.model.dto.external.recipescraper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipe_manager.model.dto.ingredient.QuantityDto;
import com.recipe_manager.model.enums.IngredientUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RecipeScraperShoppingDtoTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  @DisplayName("Should create DTO using builder pattern")
  void shouldCreateDtoUsingBuilder() {
    Map<String, IngredientShoppingInfoDto> ingredients = new HashMap<>();
    ingredients.put("salt", IngredientShoppingInfoDto.builder()
        .ingredientName("Salt")
        .quantity(QuantityDto.builder()
            .amount(1.0)
            .measurement(IngredientUnit.TSP)
            .build())
        .estimatedPrice(new BigDecimal("1.99"))
        .build());

    RecipeScraperShoppingDto dto = RecipeScraperShoppingDto.builder()
        .recipeId(123L)
        .ingredients(ingredients)
        .totalEstimatedCost(new BigDecimal("1.99"))
        .build();

    assertThat(dto.getRecipeId()).isEqualTo(123L);
    assertThat(dto.getIngredients()).hasSize(1);
    assertThat(dto.getTotalEstimatedCost())
        .isEqualByComparingTo(new BigDecimal("1.99"));
  }

  @Test
  @DisplayName("Should serialize and deserialize DTO")
  void shouldSerializeAndDeserialize() throws Exception {
    Map<String, IngredientShoppingInfoDto> ingredients = new HashMap<>();
    ingredients.put("salt", IngredientShoppingInfoDto.builder()
        .ingredientName("Salt")
        .quantity(QuantityDto.builder()
            .amount(1.0)
            .measurement(IngredientUnit.TSP)
            .build())
        .estimatedPrice(new BigDecimal("1.99"))
        .build());

    RecipeScraperShoppingDto original = RecipeScraperShoppingDto.builder()
        .recipeId(123L)
        .ingredients(ingredients)
        .totalEstimatedCost(new BigDecimal("1.99"))
        .build();

    String json = objectMapper.writeValueAsString(original);
    RecipeScraperShoppingDto deserialized = objectMapper.readValue(json, RecipeScraperShoppingDto.class);

    assertThat(deserialized)
        .usingRecursiveComparison()
        .isEqualTo(original);
  }

  @Test
  @DisplayName("Should handle null values properly")
  void shouldHandleNullValues() {
    RecipeScraperShoppingDto dto = RecipeScraperShoppingDto.builder()
        .recipeId(123L)
        .build();

    assertThat(dto.getRecipeId()).isEqualTo(123L);
    assertThat(dto.getIngredients()).isNull();
    assertThat(dto.getTotalEstimatedCost()).isNull();
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
