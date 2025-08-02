package com.recipe_manager.component_tests.media_service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
 * Component tests for DELETE
 * /recipe-management/recipes/{recipeId}/ingredients/{ingredientId}/media
 * endpoint.
 */
@Tag("component")
class DeleteMediaFromIngredientComponentTest extends AbstractComponentTest {
  @Override
  @BeforeEach
  protected void setUp() {
    super.setUp();
    when(mediaService.deleteMediaFromIngredient(anyString(), anyString()))
        .thenReturn(ResponseEntity.ok("Delete Media from Ingredient - placeholder"));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should delete media from a valid ingredient ID")
  void shouldDeleteMediaFromIngredient() throws Exception {
    mockMvc.perform(delete("/recipe-management/recipes/123/ingredients/456/media")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string("Delete Media from Ingredient - placeholder"))
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should return 404 for non-existent ingredient ID when deleting media")
  void shouldHandleNotFoundForNonExistentIngredientMediaDelete() throws Exception {
    when(mediaService.deleteMediaFromIngredient(anyString(), anyString()))
        .thenThrow(new com.recipe_manager.exception.ResourceNotFoundException("Ingredient not found"));
    mockMvc.perform(delete("/recipe-management/recipes/123/ingredients/nonexistent/media")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(header().exists("X-Request-ID"));
  }
}
