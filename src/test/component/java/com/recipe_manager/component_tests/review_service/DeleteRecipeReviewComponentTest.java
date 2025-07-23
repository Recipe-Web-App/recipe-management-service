package com.recipe_manager.component_tests.review_service;

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
 * Component tests for DELETE /recipe-management/recipes/{recipeId}/review
 * endpoint.
 */
@Tag("component")
class DeleteRecipeReviewComponentTest extends AbstractComponentTest {
  @Override
  @BeforeEach
  protected void setUp() {
    super.setUp();
    when(reviewService.deleteReview(anyString())).thenReturn(ResponseEntity.ok("Delete Review - placeholder"));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should delete a review for a valid recipe ID")
  void shouldDeleteRecipeReview() throws Exception {
    mockMvc.perform(delete("/recipe-management/recipes/123/review")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string("Delete Review - placeholder"))
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should return 404 for non-existent recipe ID when deleting review")
  void shouldHandleNotFoundForNonExistentRecipeReviewDelete() throws Exception {
    when(reviewService.deleteReview("nonexistent"))
        .thenThrow(new com.recipe_manager.exception.ResourceNotFoundException("Recipe not found"));
    mockMvc.perform(delete("/recipe-management/recipes/nonexistent/review")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(header().exists("X-Request-ID"));
  }
}
