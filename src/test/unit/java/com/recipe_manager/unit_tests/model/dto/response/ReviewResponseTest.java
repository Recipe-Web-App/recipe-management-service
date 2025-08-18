package com.recipe_manager.unit_tests.model.dto.response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.recipe_manager.model.dto.response.ReviewResponse;
import com.recipe_manager.model.dto.review.ReviewDto;

@Tag("unit")
class ReviewResponseTest {

  @Test
  @Tag("standard-processing")
  void shouldCreateReviewResponseWithBuilder() {
    final Long recipeId = 100L;
    final List<ReviewDto> reviews = createTestReviews();

    final ReviewResponse response = ReviewResponse.builder()
        .recipeId(recipeId)
        .reviews(reviews)
        .build();

    assertNotNull(response);
    assertEquals(recipeId, response.getRecipeId());
    assertEquals(reviews, response.getReviews());
  }

  @Test
  @Tag("standard-processing")
  void shouldCreateReviewResponseWithAllArgsConstructor() {
    final Long recipeId = 200L;
    final List<ReviewDto> reviews = createTestReviews();

    final ReviewResponse response = new ReviewResponse(recipeId, reviews);

    assertNotNull(response);
    assertEquals(recipeId, response.getRecipeId());
    assertEquals(reviews, response.getReviews());
  }

  @Test
  @Tag("standard-processing")
  void shouldCreateReviewResponseWithNoArgsConstructor() {
    final ReviewResponse response = new ReviewResponse();

    assertNotNull(response);
  }

  @Test
  @Tag("standard-processing")
  void shouldSetAndGetFields() {
    final ReviewResponse response = new ReviewResponse();
    final Long recipeId = 300L;
    final List<ReviewDto> reviews = createTestReviews();

    response.setRecipeId(recipeId);
    response.setReviews(reviews);

    assertEquals(recipeId, response.getRecipeId());
    assertEquals(reviews, response.getReviews());
  }

  @Test
  @Tag("standard-processing")
  void shouldTestEqualsAndHashCode() {
    final Long recipeId = 400L;
    final List<ReviewDto> reviews = createTestReviews();

    final ReviewResponse response1 = ReviewResponse.builder()
        .recipeId(recipeId)
        .reviews(reviews)
        .build();

    final ReviewResponse response2 = ReviewResponse.builder()
        .recipeId(recipeId)
        .reviews(reviews)
        .build();

    final ReviewResponse response3 = ReviewResponse.builder()
        .recipeId(500L)
        .reviews(reviews)
        .build();

    assertEquals(response1, response2);
    assertEquals(response1.hashCode(), response2.hashCode());
    assertNotEquals(response1, response3);
    assertNotEquals(response1.hashCode(), response3.hashCode());
  }

  @Test
  @Tag("standard-processing")
  void shouldTestToString() {
    final ReviewResponse response = ReviewResponse.builder()
        .recipeId(500L)
        .reviews(createTestReviews())
        .build();

    final String toString = response.toString();

    assertNotNull(toString);
    assertEquals(toString, response.toString());
  }

  @Test
  @Tag("standard-processing")
  void shouldHandleEmptyReviewsList() {
    final Long recipeId = 600L;
    final List<ReviewDto> emptyReviews = Arrays.asList();

    final ReviewResponse response = ReviewResponse.builder()
        .recipeId(recipeId)
        .reviews(emptyReviews)
        .build();

    assertNotNull(response);
    assertEquals(recipeId, response.getRecipeId());
    assertEquals(emptyReviews, response.getReviews());
    assertEquals(0, response.getReviews().size());
  }

  private List<ReviewDto> createTestReviews() {
    final ReviewDto review1 = ReviewDto.builder()
        .reviewId(1L)
        .recipeId(100L)
        .userId(UUID.randomUUID())
        .rating(new BigDecimal("4.5"))
        .comment("Great recipe!")
        .createdAt(LocalDateTime.now())
        .build();

    final ReviewDto review2 = ReviewDto.builder()
        .reviewId(2L)
        .recipeId(100L)
        .userId(UUID.randomUUID())
        .rating(new BigDecimal("3.0"))
        .comment("Okay recipe")
        .createdAt(LocalDateTime.now())
        .build();

    return Arrays.asList(review1, review2);
  }
}
