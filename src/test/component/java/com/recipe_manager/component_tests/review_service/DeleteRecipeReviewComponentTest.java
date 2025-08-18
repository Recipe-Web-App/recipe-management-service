package com.recipe_manager.component_tests.review_service;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.recipe_manager.component_tests.AbstractComponentTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;

/**
 * Component tests for DELETE /recipe-management/recipes/{recipeId}/review/{reviewId}
 * endpoint.
 */
@Tag("component")
class DeleteRecipeReviewComponentTest extends AbstractComponentTest {
  @Override
  @BeforeEach
  protected void setUp() {
    super.setUp();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should delete a review for a valid recipe ID and review ID")
  void shouldDeleteRecipeReview() throws Exception {
    doNothing().when(reviewService).deleteReview(eq(123L), eq(1L));

    mockMvc.perform(delete("/recipe-management/recipes/123/review/1")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent())
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should return 404 for non-existent review when deleting")
  void shouldHandleNotFoundForNonExistentReview() throws Exception {
    doThrow(new com.recipe_manager.exception.ResourceNotFoundException("Review not found with ID: 999 for recipe: 123"))
        .when(reviewService).deleteReview(eq(123L), eq(999L));

    mockMvc.perform(delete("/recipe-management/recipes/123/review/999")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should return 403 when user tries to delete another user's review")
  void shouldHandleAccessDeniedForOtherUsersReview() throws Exception {
    doThrow(new AccessDeniedException("User can only delete their own reviews"))
        .when(reviewService).deleteReview(eq(123L), eq(1L));

    mockMvc.perform(delete("/recipe-management/recipes/123/review/1")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andExpect(header().exists("X-Request-ID"));
  }
}
