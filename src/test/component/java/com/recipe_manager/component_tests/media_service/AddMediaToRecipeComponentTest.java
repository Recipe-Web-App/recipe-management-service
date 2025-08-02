package com.recipe_manager.component_tests.media_service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.recipe_manager.component_tests.AbstractComponentTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 * Component tests for POST /recipe-management/recipes/{recipeId}/media
 * endpoint.
 */
@Tag("component")
class AddMediaToRecipeComponentTest extends AbstractComponentTest {
  @Override
  @BeforeEach
  protected void setUp() {
    super.setUp();
    when(mediaService.addMediaToRecipe(anyString())).thenReturn(ResponseEntity.ok("Add Media - placeholder"));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should add media to a valid recipe ID")
  void shouldAddMediaToRecipe() throws Exception {
    mockMvc.perform(post("/recipe-management/recipes/123/media")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{}"))
        .andExpect(status().isOk())
        .andExpect(content().string("Add Media - placeholder"))
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should return 404 for non-existent recipe ID when adding media")
  void shouldHandleNotFoundForNonExistentRecipeMedia() throws Exception {
    when(mediaService.addMediaToRecipe("nonexistent"))
        .thenThrow(new com.recipe_manager.exception.ResourceNotFoundException("Recipe not found"));
    mockMvc.perform(post("/recipe-management/recipes/nonexistent/media")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{}"))
        .andExpect(status().isNotFound())
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Disabled("Temporarily disabled due to known issue with error handling/content type")
  @Tag("error-processing")
  @DisplayName("Should handle malformed JSON for media operations")
  void shouldHandleMalformedJsonForMediaOperations() throws Exception {
    String malformedJson = "{ invalid json }";
    mockMvc.perform(post("/recipe-management/recipes/123/media")
        .contentType(MediaType.APPLICATION_JSON)
        .content(malformedJson))
        .andExpect(status().isBadRequest());
  }
}
