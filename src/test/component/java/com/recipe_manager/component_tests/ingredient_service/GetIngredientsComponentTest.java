package com.recipe_manager.component_tests.ingredient_service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.recipe_manager.component_tests.AbstractComponentTest;
import com.recipe_manager.service.IngredientService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

/**
 * Component tests for GET /recipe-management/recipes/{recipeId}/ingredients
 * endpoint.
 * Tests ingredient retrieval with all I/O mocked.
 */
@Tag("component")
class GetIngredientsComponentTest extends AbstractComponentTest {
  @Mock
  private IngredientService ingredientService;

  @Override
  @BeforeEach
  protected void setUp() {
    MockitoAnnotations.openMocks(this);
    super.setUp();
    when(ingredientService.getIngredients(anyString())).thenReturn(ResponseEntity.ok("Get Ingredients - placeholder"));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should return ingredients for valid recipe ID")
  void shouldReturnIngredientsForValidRecipeId() throws Exception {
    mockMvc.perform(get("/recipe-management/recipes/123/ingredients"))
        .andExpect(status().isOk())
        .andExpect(content().string("Get Ingredients - placeholder"))
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
