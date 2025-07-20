package com.recipe_manager.component_tests.recipe_service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.recipe_manager.component_tests.AbstractComponentTest;
import com.recipe_manager.service.RecipeService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 * Component tests for DELETE /recipe-management/recipes/{recipeId} endpoint.
 * Tests recipe deletion with all I/O mocked.
 */
@Tag("component")
class DeleteRecipeComponentTest extends AbstractComponentTest {
  @Mock
  private RecipeService recipeService;

  @Override
  @BeforeEach
  protected void setUp() {
    MockitoAnnotations.openMocks(this);
    super.setUp();
    when(recipeService.deleteRecipe(anyString())).thenReturn(ResponseEntity.ok("Delete Recipe - placeholder"));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should delete a recipe with valid ID")
  void shouldDeleteRecipe() throws Exception {
    mockMvc.perform(delete("/recipe-management/recipes/123")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string("Delete Recipe - placeholder"))
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should return 404 for non-existent recipe ID when deleting recipe")
  void shouldHandleNotFoundForNonExistentRecipeDelete() throws Exception {
    when(recipeService.deleteRecipe("nonexistent"))
        .thenThrow(new com.recipe_manager.exception.ResourceNotFoundException("Recipe not found"));
    mockMvc.perform(delete("/recipe-management/recipes/nonexistent")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(header().exists("X-Request-ID"));
  }
}
