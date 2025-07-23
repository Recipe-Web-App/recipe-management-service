package com.recipe_manager.component_tests.review_service;

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
 * Component tests for PUT /recipe-management/recipes/{recipeId}/review
 * endpoint.
 */
@Tag("component")
class EditRecipeReviewComponentTest extends AbstractComponentTest {
  @Override
  @BeforeEach
  protected void setUp() {
    super.setUp();
    when(reviewService.editReview(anyString())).thenReturn(ResponseEntity.ok("Edit Review - placeholder"));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should edit a review for a valid recipe ID")
  void shouldEditRecipeReview() throws Exception {
    mockMvc.perform(put("/recipe-management/recipes/123/review")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{}"))
        .andExpect(status().isOk())
        .andExpect(content().string("Edit Review - placeholder"))
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should return 404 for non-existent recipe ID when editing review")
  void shouldHandleNotFoundForNonExistentRecipeReviewEdit() throws Exception {
    when(reviewService.editReview("nonexistent"))
        .thenThrow(new com.recipe_manager.exception.ResourceNotFoundException("Recipe not found"));
    mockMvc.perform(put("/recipe-management/recipes/nonexistent/review")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{}"))
        .andExpect(status().isNotFound())
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Disabled("Temporarily disabled due to known issue with error handling/content type")
  @Tag("error-processing")
  @DisplayName("Should handle malformed JSON for review edit operations")
  void shouldHandleMalformedJsonForReviewEditOperations() throws Exception {
    String malformedJson = "{ invalid json }";
    mockMvc.perform(put("/recipe-management/recipes/123/review")
        .contentType(MediaType.APPLICATION_JSON)
        .content(malformedJson))
        .andExpect(status().isBadRequest());
  }
}
