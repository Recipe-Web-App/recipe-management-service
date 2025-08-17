package com.recipe_manager.component_tests.review_service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import com.recipe_manager.component_tests.AbstractComponentTest;
import com.recipe_manager.model.dto.request.EditReviewRequest;
import com.recipe_manager.model.dto.review.ReviewDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;

/**
 * Component tests for PUT /recipe-management/recipes/{recipeId}/review/{reviewId}
 * endpoint.
 */
@Tag("component")
class EditRecipeReviewComponentTest extends AbstractComponentTest {
  @Override
  @BeforeEach
  protected void setUp() {
    super.setUp();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should edit a review for a valid recipe ID and review ID")
  void shouldEditRecipeReview() throws Exception {
    ReviewDto reviewDto = ReviewDto.builder()
        .reviewId(1L)
        .recipeId(123L)
        .userId(UUID.randomUUID())
        .rating(java.math.BigDecimal.valueOf(3.5))
        .comment("Updated comment")
        .createdAt(java.time.LocalDateTime.now())
        .build();

    when(reviewService.editReview(eq(123L), eq(1L), any(EditReviewRequest.class))).thenReturn(reviewDto);

    mockMvc.perform(put("/recipe-management/recipes/123/review/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"rating\":3.5,\"comment\":\"Updated comment\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.reviewId").value(1))
        .andExpect(jsonPath("$.recipeId").value(123))
        .andExpect(jsonPath("$.rating").value(3.5))
        .andExpect(jsonPath("$.comment").value("Updated comment"))
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should return 404 for non-existent review when editing")
  void shouldHandleNotFoundForNonExistentReview() throws Exception {
    when(reviewService.editReview(eq(123L), eq(999L), any(EditReviewRequest.class)))
        .thenThrow(new com.recipe_manager.exception.ResourceNotFoundException("Review not found with ID: 999 for recipe: 123"));

    mockMvc.perform(put("/recipe-management/recipes/123/review/999")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"rating\":3.5,\"comment\":\"Updated comment\"}"))
        .andExpect(status().isNotFound())
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should return 403 when user tries to edit another user's review")
  void shouldHandleAccessDeniedForOtherUsersReview() throws Exception {
    when(reviewService.editReview(eq(123L), eq(1L), any(EditReviewRequest.class)))
        .thenThrow(new AccessDeniedException("User can only edit their own reviews"));

    mockMvc.perform(put("/recipe-management/recipes/123/review/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"rating\":3.5,\"comment\":\"Updated comment\"}"))
        .andExpect(status().isForbidden())
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should return 400 for invalid rating increments when editing")
  void shouldHandleInvalidRatingIncrementsWhenEditing() throws Exception {
    when(reviewService.editReview(eq(123L), eq(1L), any(EditReviewRequest.class)))
        .thenThrow(new com.recipe_manager.exception.BusinessException("Rating must be in increments of 0.5 (e.g., 0.0, 0.5, 1.0, 1.5, etc.)"));

    mockMvc.perform(put("/recipe-management/recipes/123/review/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"rating\":2.7,\"comment\":\"Updated comment\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(header().exists("X-Request-ID"));
  }
}
