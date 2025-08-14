package com.recipe_manager.component_tests.ingredient_service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.recipe_manager.component_tests.AbstractComponentTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 * Component tests for GET /recipe-management/recipes/{recipeId}/ingredients
 * endpoint.
 * Tests ingredient retrieval with all I/O mocked.
 */
@Tag("component")
class GetRecipeIngredientsComponentTest extends AbstractComponentTest {
  @Override
  @BeforeEach
  protected void setUp() {
    super.setUp();
    // Create a mock response with proper DTO structure
    com.recipe_manager.model.dto.response.RecipeIngredientsResponse mockResponse =
        com.recipe_manager.model.dto.response.RecipeIngredientsResponse.builder()
            .recipeId(123L)
            .ingredients(java.util.Arrays.asList())
            .totalCount(0)
            .build();

    when(ingredientService.getIngredients(anyString())).thenReturn(ResponseEntity.ok(mockResponse));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should return ingredients for a valid recipe ID")
  void shouldGetRecipeIngredients() throws Exception {
    mockMvc.perform(get("/recipe-management/recipes/123/ingredients")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should return 404 for non-existent recipe ID")
  void shouldHandleNotFoundForNonExistentRecipeIngredients() throws Exception {
    when(ingredientService.getIngredients("nonexistent"))
        .thenThrow(new com.recipe_manager.exception.ResourceNotFoundException("Recipe not found"));
    mockMvc.perform(get("/recipe-management/recipes/nonexistent/ingredients")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(header().exists("X-Request-ID"));
  }
}
