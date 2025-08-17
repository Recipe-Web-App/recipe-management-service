package com.recipe_manager.component_tests.review_service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import com.recipe_manager.component_tests.AbstractComponentTest;
import com.recipe_manager.model.dto.request.AddReviewRequest;
import com.recipe_manager.model.dto.review.ReviewDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

/**
 * Component tests for POST /recipe-management/recipes/{recipeId}/review
 * endpoint.
 */
@Tag("component")
class AddRecipeReviewComponentTest extends AbstractComponentTest {
  @Override
  @BeforeEach
  protected void setUp() {
    super.setUp();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should add a review to a valid recipe ID")
  void shouldAddRecipeReview() throws Exception {
    ReviewDto reviewDto = ReviewDto.builder()
        .reviewId(1L)
        .recipeId(123L)
        .userId(UUID.randomUUID())
        .rating(java.math.BigDecimal.valueOf(4.5))
        .comment("Great recipe!")
        .createdAt(java.time.LocalDateTime.now())
        .build();

    when(reviewService.addReview(eq(123L), any(AddReviewRequest.class))).thenReturn(reviewDto);

    mockMvc.perform(post("/recipe-management/recipes/123/review")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"rating\":4.5,\"comment\":\"Great recipe!\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.reviewId").value(1))
        .andExpect(jsonPath("$.recipeId").value(123))
        .andExpect(jsonPath("$.rating").value(4.5))
        .andExpect(jsonPath("$.comment").value("Great recipe!"))
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should return 404 for non-existent recipe ID when adding review")
  void shouldHandleNotFoundForNonExistentRecipeReviewAdd() throws Exception {
    when(reviewService.addReview(eq(999L), any(AddReviewRequest.class)))
        .thenThrow(new com.recipe_manager.exception.ResourceNotFoundException("Recipe not found with ID: 999"));

    mockMvc.perform(post("/recipe-management/recipes/999/review")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"rating\":4.5,\"comment\":\"Great recipe!\"}"))
        .andExpect(status().isNotFound())
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should return 400 for invalid rating increments")
  void shouldHandleInvalidRatingIncrements() throws Exception {
    when(reviewService.addReview(eq(123L), any(AddReviewRequest.class)))
        .thenThrow(new com.recipe_manager.exception.BusinessException("Rating must be in increments of 0.5 (e.g., 0.0, 0.5, 1.0, 1.5, etc.)"));

    mockMvc.perform(post("/recipe-management/recipes/123/review")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"rating\":4.3,\"comment\":\"Good recipe!\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should return 400 when user already reviewed recipe")
  void shouldHandleDuplicateReview() throws Exception {
    when(reviewService.addReview(eq(123L), any(AddReviewRequest.class)))
        .thenThrow(new com.recipe_manager.exception.BusinessException("User has already reviewed this recipe"));

    mockMvc.perform(post("/recipe-management/recipes/123/review")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"rating\":4.5,\"comment\":\"Great recipe!\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(header().exists("X-Request-ID"));
  }
}
