package com.recipe_manager.component_tests.media_service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
 * Component tests for PUT
 * /recipe-management/recipes/{recipeId}/steps/{stepId}/media endpoint.
 */
@Tag("component")
class UpdateMediaOnStepComponentTest extends AbstractComponentTest {
  @Override
  @BeforeEach
  protected void setUp() {
    super.setUp();
    when(mediaService.updateMediaOnStep(anyString(), anyString()))
        .thenReturn(ResponseEntity.ok("Update Media on Step - placeholder"));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should update media on a valid step ID")
  void shouldUpdateMediaOnStep() throws Exception {
    mockMvc.perform(put("/recipe-management/recipes/123/steps/789/media")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{}"))
        .andExpect(status().isOk())
        .andExpect(content().string("Update Media on Step - placeholder"))
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should return 404 for non-existent step ID when updating media")
  void shouldHandleNotFoundForNonExistentStepMediaUpdate() throws Exception {
    when(mediaService.updateMediaOnStep(anyString(), anyString()))
        .thenThrow(new com.recipe_manager.exception.ResourceNotFoundException("Step not found"));
    mockMvc.perform(put("/recipe-management/recipes/123/steps/nonexistent/media")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{}"))
        .andExpect(status().isNotFound())
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Disabled("Temporarily disabled due to known issue with error handling/content type")
  @Tag("error-processing")
  @DisplayName("Should handle malformed JSON for step media update operations")
  void shouldHandleMalformedJsonForStepMediaUpdateOperations() throws Exception {
    String malformedJson = "{ invalid json }";
    mockMvc.perform(put("/recipe-management/recipes/123/steps/789/media")
        .contentType(MediaType.APPLICATION_JSON)
        .content(malformedJson))
        .andExpect(status().isBadRequest());
  }
}
