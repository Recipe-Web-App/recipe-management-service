package com.recipe_manager.component_tests.review_service;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.UUID;

import com.recipe_manager.component_tests.AbstractComponentTest;
import com.recipe_manager.model.dto.response.ReviewResponse;
import com.recipe_manager.model.dto.review.ReviewDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

/**
 * Component tests for GET /recipe-management/recipes/{recipeId}/review
 * endpoint.
 */
@Tag("component")
class GetRecipeReviewsComponentTest extends AbstractComponentTest {
  @Override
  @BeforeEach
  protected void setUp() {
    super.setUp();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should get reviews for a valid recipe ID")
  void shouldGetRecipeReviews() throws Exception {
    ReviewDto reviewDto = ReviewDto.builder()
        .reviewId(1L)
        .recipeId(123L)
        .userId(UUID.randomUUID())
        .rating(java.math.BigDecimal.valueOf(4.5))
        .comment("Great recipe!")
        .createdAt(java.time.LocalDateTime.now())
        .build();

    ReviewResponse response = ReviewResponse.builder()
        .recipeId(123L)
        .reviews(Arrays.asList(reviewDto))
        .build();

    when(reviewService.getReviews(123L)).thenReturn(response);

    mockMvc.perform(get("/recipe-management/recipes/123/review")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.recipeId").value(123))
        .andExpect(jsonPath("$.reviews").isArray())
        .andExpect(jsonPath("$.reviews[0].reviewId").value(1))
        .andExpect(jsonPath("$.reviews[0].rating").value(4.5))
        .andExpect(jsonPath("$.reviews[0].comment").value("Great recipe!"))
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should return empty reviews list for recipe with no reviews")
  void shouldReturnEmptyReviewsForRecipeWithNoReviews() throws Exception {
    ReviewResponse response = ReviewResponse.builder()
        .recipeId(123L)
        .reviews(Arrays.asList())
        .build();

    when(reviewService.getReviews(123L)).thenReturn(response);

    mockMvc.perform(get("/recipe-management/recipes/123/review")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.recipeId").value(123))
        .andExpect(jsonPath("$.reviews").isArray())
        .andExpect(jsonPath("$.reviews").isEmpty())
        .andExpect(header().exists("X-Request-ID"));
  }
}
