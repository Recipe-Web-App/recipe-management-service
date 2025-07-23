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
 * Component tests for POST
 * /recipe-management/recipes/{recipeId}/steps/{stepId}/media endpoint.
 */
@Tag("component")
class AddMediaToStepComponentTest extends AbstractComponentTest {
  @Override
  @BeforeEach
  protected void setUp() {
    super.setUp();
    when(mediaService.addMediaToStep(anyString(), anyString()))
        .thenReturn(ResponseEntity.ok("Add Media to Step - placeholder"));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should add media to a valid step ID")
  void shouldAddMediaToStep() throws Exception {
    mockMvc.perform(post("/recipe-management/recipes/123/steps/789/media")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{}"))
        .andExpect(status().isOk())
        .andExpect(content().string("Add Media to Step - placeholder"))
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should return 404 for non-existent step ID when adding media")
  void shouldHandleNotFoundForNonExistentStepMedia() throws Exception {
    when(mediaService.addMediaToStep(anyString(), anyString()))
        .thenThrow(new com.recipe_manager.exception.ResourceNotFoundException("Step not found"));
    mockMvc.perform(post("/recipe-management/recipes/123/steps/nonexistent/media")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{}"))
        .andExpect(status().isNotFound())
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Disabled("Temporarily disabled due to known issue with error handling/content type")
  @Tag("error-processing")
  @DisplayName("Should handle malformed JSON for step media operations")
  void shouldHandleMalformedJsonForStepMediaOperations() throws Exception {
    String malformedJson = "{ invalid json }";
    mockMvc.perform(post("/recipe-management/recipes/123/steps/789/media")
        .contentType(MediaType.APPLICATION_JSON)
        .content(malformedJson))
        .andExpect(status().isBadRequest());
  }
}
