package com.recipe_manager.model.dto.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.recipe_manager.model.dto.recipe.RecipeIngredientDto;
import com.recipe_manager.model.enums.IngredientUnit;

/**
 * Unit tests for RecipeIngredientsResponse DTO.
 */
@Tag("unit")
class RecipeIngredientsResponseTest {

  @Test
  @Tag("standard-processing")
  @DisplayName("Should create RecipeIngredientsResponse with all fields")
  void shouldCreateRecipeIngredientsResponseWithAllFields() {
    // Given
    Long recipeId = 123L;
    RecipeIngredientDto ingredient1 = RecipeIngredientDto.builder()
        .recipeId(recipeId)
        .ingredientId(1L)
        .ingredientName("Salt")
        .quantity(new BigDecimal("1.5"))
        .unit(IngredientUnit.TSP)
        .isOptional(false)
        .build();

    RecipeIngredientDto ingredient2 = RecipeIngredientDto.builder()
        .recipeId(recipeId)
        .ingredientId(2L)
        .ingredientName("Pepper")
        .quantity(new BigDecimal("0.5"))
        .unit(IngredientUnit.TSP)
        .isOptional(true)
        .build();

    List<RecipeIngredientDto> ingredients = Arrays.asList(ingredient1, ingredient2);

    // When
    RecipeIngredientsResponse response = RecipeIngredientsResponse.builder()
        .recipeId(recipeId)
        .ingredients(ingredients)
        .totalCount(ingredients.size())
        .build();

    // Then
    assertThat(response.getRecipeId()).isEqualTo(recipeId);
    assertThat(response.getIngredients()).hasSize(2);
    assertThat(response.getIngredients()).containsExactly(ingredient1, ingredient2);
    assertThat(response.getTotalCount()).isEqualTo(2);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should create empty RecipeIngredientsResponse")
  void shouldCreateEmptyRecipeIngredientsResponse() {
    // Given
    Long recipeId = 123L;

    // When
    RecipeIngredientsResponse response = RecipeIngredientsResponse.builder()
        .recipeId(recipeId)
        .ingredients(Arrays.asList())
        .totalCount(0)
        .build();

    // Then
    assertThat(response.getRecipeId()).isEqualTo(recipeId);
    assertThat(response.getIngredients()).isEmpty();
    assertThat(response.getTotalCount()).isEqualTo(0);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should support equality operations")
  void shouldSupportEqualityOperations() {
    // Given
    Long recipeId = 123L;
    List<RecipeIngredientDto> ingredients = Arrays.asList(
        RecipeIngredientDto.builder()
            .recipeId(recipeId)
            .ingredientId(1L)
            .ingredientName("Salt")
            .quantity(new BigDecimal("1.5"))
            .unit(IngredientUnit.TSP)
            .isOptional(false)
            .build()
    );

    RecipeIngredientsResponse response1 = RecipeIngredientsResponse.builder()
        .recipeId(recipeId)
        .ingredients(ingredients)
        .totalCount(1)
        .build();

    RecipeIngredientsResponse response2 = RecipeIngredientsResponse.builder()
        .recipeId(recipeId)
        .ingredients(ingredients)
        .totalCount(1)
        .build();

    // Then
    assertThat(response1).isEqualTo(response2);
    assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
    assertThat(response1.toString()).isNotBlank();
  }
}
