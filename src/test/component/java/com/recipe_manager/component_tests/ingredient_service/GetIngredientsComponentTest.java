package com.recipe_manager.component_tests.ingredient_service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.recipe_manager.component_tests.AbstractComponentTest;
import com.recipe_manager.model.dto.recipe.RecipeIngredientDto;
import com.recipe_manager.model.dto.response.RecipeIngredientsResponse;
import com.recipe_manager.model.enums.IngredientUnit;

/**
 * Component tests for GET /recipe-management/recipes/{recipeId}/ingredients
 * endpoint.
 * Tests ingredient retrieval with all I/O mocked.
 */
@Tag("component")
class GetIngredientsComponentTest extends AbstractComponentTest {

  @Override
  @BeforeEach
  protected void setUp() {
    super.setUp();

    RecipeIngredientDto ingredient1 = RecipeIngredientDto.builder()
        .recipeId(123L)
        .ingredientId(1L)
        .ingredientName("Salt")
        .quantity(new BigDecimal("1.5"))
        .unit(IngredientUnit.TSP)
        .isOptional(false)
        .build();

    RecipeIngredientsResponse mockResponse = RecipeIngredientsResponse.builder()
        .recipeId(123L)
        .ingredients(Arrays.asList(ingredient1))
        .totalCount(1)
        .build();

    when(ingredientService.getIngredients(anyString())).thenReturn(ResponseEntity.ok(mockResponse));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should return ingredients for valid recipe ID")
  void shouldReturnIngredientsForValidRecipeId() throws Exception {
    mockMvc.perform(get("/recipe-management/recipes/123/ingredients")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.recipeId").value(123))
        .andExpect(jsonPath("$.totalCount").value(1))
        .andExpect(jsonPath("$.ingredients[0].ingredientName").value("Salt"))
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should return 404 for non-existent recipe")
  void shouldReturn404ForNonExistentRecipe() throws Exception {
    when(ingredientService.getIngredients("nonexistent"))
        .thenThrow(new com.recipe_manager.exception.ResourceNotFoundException("Recipe not found"));
    mockMvc.perform(get("/recipe-management/recipes/nonexistent/ingredients"))
        .andExpect(status().isNotFound())
        .andExpect(header().exists("X-Request-ID"));
  }
}
