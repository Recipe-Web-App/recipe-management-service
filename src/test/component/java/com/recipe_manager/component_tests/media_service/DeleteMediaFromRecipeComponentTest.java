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
 * Component tests for DELETE /recipe-management/recipes/{recipeId}/media
 * endpoint.
 */
@Tag("component")
class DeleteMediaFromRecipeComponentTest extends AbstractComponentTest {
  @Override
  @BeforeEach
  protected void setUp() {
    super.setUp();
    when(mediaService.deleteMediaFromRecipe(anyString())).thenReturn(ResponseEntity.ok("Delete Media - placeholder"));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should delete media from a valid recipe ID")
  void shouldDeleteMediaFromRecipe() throws Exception {
    mockMvc.perform(delete("/recipe-management/recipes/123/media")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string("Delete Media - placeholder"))
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should return 404 for non-existent recipe ID when deleting media")
  void shouldHandleNotFoundForNonExistentRecipeMediaDelete() throws Exception {
    when(mediaService.deleteMediaFromRecipe("nonexistent"))
        .thenThrow(new com.recipe_manager.exception.ResourceNotFoundException("Recipe not found"));
    mockMvc.perform(delete("/recipe-management/recipes/nonexistent/media")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(header().exists("X-Request-ID"));
  }
}
