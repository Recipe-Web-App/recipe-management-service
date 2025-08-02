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
 * Component tests for GET
 * /recipe-management/recipes/{recipeId}/ingredients/shopping-list endpoint.
 */
@Tag("component")
class GenerateShoppingListComponentTest extends AbstractComponentTest {

  @Override
  @BeforeEach
  protected void setUp() {
    super.setUp();
    when(ingredientService.generateShoppingList(anyString()))
        .thenReturn(ResponseEntity.ok("Shopping List - placeholder"));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should generate shopping list for a valid recipe ID")
  void shouldGenerateShoppingList() throws Exception {
    mockMvc.perform(get("/recipe-management/recipes/123/ingredients/shopping-list")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string("Shopping List - placeholder"))
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should return 404 for non-existent recipe ID when generating shopping list")
  void shouldHandleNotFoundForNonExistentRecipeShoppingList() throws Exception {
    when(ingredientService.generateShoppingList("nonexistent"))
        .thenThrow(new com.recipe_manager.exception.ResourceNotFoundException("Recipe not found"));
    mockMvc.perform(get("/recipe-management/recipes/nonexistent/ingredients/shopping-list")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(header().exists("X-Request-ID"));
  }
}
