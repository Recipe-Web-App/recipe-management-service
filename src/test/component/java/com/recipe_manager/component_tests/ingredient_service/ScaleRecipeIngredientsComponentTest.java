package com.recipe_manager.component_tests.ingredient_service;

import static org.mockito.ArgumentMatchers.anyFloat;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 * Component tests for GET
 * /recipe-management/recipes/{recipeId}/ingredients/scale endpoint.
 */
@Tag("component")
class ScaleRecipeIngredientsComponentTest extends AbstractComponentTest {
  @Mock
  private IngredientService ingredientService;

  @Override
  @BeforeEach
  protected void setUp() {
    MockitoAnnotations.openMocks(this);
    super.setUp();
    when(ingredientService.scaleIngredients(anyString(), anyFloat()))
        .thenReturn(ResponseEntity.ok("Scale Ingredients - placeholder"));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should scale ingredients for a valid recipe ID and quantity")
  void shouldScaleRecipeIngredients() throws Exception {
    mockMvc.perform(get("/recipe-management/recipes/123/ingredients/scale")
        .param("quantity", "2.5")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string("Scale Ingredients - placeholder"))
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should return 400 for invalid quantity parameter")
  void shouldHandleInvalidQuantityParameter() throws Exception {
    mockMvc.perform(get("/recipe-management/recipes/123/ingredients/scale")
        .param("quantity", "invalid")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should return 400 for missing quantity parameter")
  void shouldHandleMissingQuantityParameter() throws Exception {
    mockMvc.perform(get("/recipe-management/recipes/123/ingredients/scale")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }
}
