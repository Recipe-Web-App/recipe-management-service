package com.recipe_manager.component_tests.media_service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.recipe_manager.component_tests.AbstractComponentTest;
import com.recipe_manager.service.MediaService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 * Component tests for DELETE
 * /recipe-management/recipes/{recipeId}/steps/{stepId}/media endpoint.
 */
@Tag("component")
class DeleteMediaFromStepComponentTest extends AbstractComponentTest {
  @Mock
  private MediaService mediaService;

  @Override
  @BeforeEach
  protected void setUp() {
    MockitoAnnotations.openMocks(this);
    super.setUp();
    when(mediaService.deleteMediaFromStep(anyString(), anyString()))
        .thenReturn(ResponseEntity.ok("Delete Media from Step - placeholder"));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should delete media from a valid step ID")
  void shouldDeleteMediaFromStep() throws Exception {
    mockMvc.perform(delete("/recipe-management/recipes/123/steps/789/media")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string("Delete Media from Step - placeholder"))
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should return 404 for non-existent step ID when deleting media")
  void shouldHandleNotFoundForNonExistentStepMediaDelete() throws Exception {
    when(mediaService.deleteMediaFromStep(anyString(), anyString()))
        .thenThrow(new com.recipe_manager.exception.ResourceNotFoundException("Step not found"));
    mockMvc.perform(delete("/recipe-management/recipes/123/steps/nonexistent/media")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(header().exists("X-Request-ID"));
  }
}
