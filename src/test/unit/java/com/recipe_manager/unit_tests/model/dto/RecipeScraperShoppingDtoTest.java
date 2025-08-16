package com.recipe_manager.unit_tests.model.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipe_manager.model.dto.external.recipescraper.IngredientShoppingInfoDto;
import com.recipe_manager.model.dto.external.recipescraper.RecipeScraperShoppingDto;
import com.recipe_manager.model.enums.IngredientUnit;

class RecipeScraperShoppingDtoTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  @DisplayName("Should create DTO using builder pattern")
  void shouldCreateDtoUsingBuilder() {
    Map<String, IngredientShoppingInfoDto> ingredients = new HashMap<>();
    ingredients.put("salt", IngredientShoppingInfoDto.builder()
        .ingredientName("Salt")
        .quantity(new BigDecimal("1.0"))
        .unit(IngredientUnit.TSP)
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
        .quantity(new BigDecimal("1.0"))
        .unit(IngredientUnit.TSP)
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
}
