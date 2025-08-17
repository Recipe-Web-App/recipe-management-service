package com.recipe_manager.unit_tests.model.dto.review;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.recipe_manager.model.dto.review.ReviewDto;

@Tag("unit")
class ReviewDtoTest {

  @Test
  @Tag("standard-processing")
  void shouldCreateReviewDtoWithBuilder() {
    final Long reviewId = 1L;
    final Long recipeId = 100L;
    final UUID userId = UUID.randomUUID();
    final BigDecimal rating = new BigDecimal("4.5");
    final String comment = "Great recipe!";
    final LocalDateTime createdAt = LocalDateTime.now();

    final ReviewDto reviewDto = ReviewDto.builder()
        .reviewId(reviewId)
        .recipeId(recipeId)
        .userId(userId)
        .rating(rating)
        .comment(comment)
        .createdAt(createdAt)
        .build();

    assertNotNull(reviewDto);
    assertEquals(reviewId, reviewDto.getReviewId());
    assertEquals(recipeId, reviewDto.getRecipeId());
    assertEquals(userId, reviewDto.getUserId());
    assertEquals(rating, reviewDto.getRating());
    assertEquals(comment, reviewDto.getComment());
    assertEquals(createdAt, reviewDto.getCreatedAt());
  }

  @Test
  @Tag("standard-processing")
  void shouldCreateReviewDtoWithAllArgsConstructor() {
    final Long reviewId = 2L;
    final Long recipeId = 200L;
    final UUID userId = UUID.randomUUID();
    final BigDecimal rating = new BigDecimal("3.0");
    final String comment = "Okay recipe";
    final LocalDateTime createdAt = LocalDateTime.now();

    final ReviewDto reviewDto = new ReviewDto(reviewId, recipeId, userId, rating, comment, createdAt);

    assertNotNull(reviewDto);
    assertEquals(reviewId, reviewDto.getReviewId());
    assertEquals(recipeId, reviewDto.getRecipeId());
    assertEquals(userId, reviewDto.getUserId());
    assertEquals(rating, reviewDto.getRating());
    assertEquals(comment, reviewDto.getComment());
    assertEquals(createdAt, reviewDto.getCreatedAt());
  }

  @Test
  @Tag("standard-processing")
  void shouldCreateReviewDtoWithNoArgsConstructor() {
    final ReviewDto reviewDto = new ReviewDto();

    assertNotNull(reviewDto);
  }

  @Test
  @Tag("standard-processing")
  void shouldSetAndGetFields() {
    final ReviewDto reviewDto = new ReviewDto();
    final Long reviewId = 3L;
    final Long recipeId = 300L;
    final UUID userId = UUID.randomUUID();
    final BigDecimal rating = new BigDecimal("5.0");
    final String comment = "Excellent!";
    final LocalDateTime createdAt = LocalDateTime.now();

    reviewDto.setReviewId(reviewId);
    reviewDto.setRecipeId(recipeId);
    reviewDto.setUserId(userId);
    reviewDto.setRating(rating);
    reviewDto.setComment(comment);
    reviewDto.setCreatedAt(createdAt);

    assertEquals(reviewId, reviewDto.getReviewId());
    assertEquals(recipeId, reviewDto.getRecipeId());
    assertEquals(userId, reviewDto.getUserId());
    assertEquals(rating, reviewDto.getRating());
    assertEquals(comment, reviewDto.getComment());
    assertEquals(createdAt, reviewDto.getCreatedAt());
  }

  @Test
  @Tag("standard-processing")
  void shouldTestEqualsAndHashCode() {
    final Long reviewId = 4L;
    final Long recipeId = 400L;
    final UUID userId = UUID.randomUUID();
    final BigDecimal rating = new BigDecimal("2.5");
    final String comment = "Needs improvement";
    final LocalDateTime createdAt = LocalDateTime.now();

    final ReviewDto reviewDto1 = ReviewDto.builder()
        .reviewId(reviewId)
        .recipeId(recipeId)
        .userId(userId)
        .rating(rating)
        .comment(comment)
        .createdAt(createdAt)
        .build();

    final ReviewDto reviewDto2 = ReviewDto.builder()
        .reviewId(reviewId)
        .recipeId(recipeId)
        .userId(userId)
        .rating(rating)
        .comment(comment)
        .createdAt(createdAt)
        .build();

    final ReviewDto reviewDto3 = ReviewDto.builder()
        .reviewId(5L)
        .recipeId(recipeId)
        .userId(userId)
        .rating(rating)
        .comment(comment)
        .createdAt(createdAt)
        .build();

    assertEquals(reviewDto1, reviewDto2);
    assertEquals(reviewDto1.hashCode(), reviewDto2.hashCode());
    assertNotEquals(reviewDto1, reviewDto3);
    assertNotEquals(reviewDto1.hashCode(), reviewDto3.hashCode());
  }

  @Test
  @Tag("standard-processing")
  void shouldTestToString() {
    final ReviewDto reviewDto = ReviewDto.builder()
        .reviewId(5L)
        .recipeId(500L)
        .userId(UUID.randomUUID())
        .rating(new BigDecimal("1.5"))
        .comment("Poor recipe")
        .createdAt(LocalDateTime.now())
        .build();

    final String toString = reviewDto.toString();

    assertNotNull(toString);
    assertEquals(toString, reviewDto.toString());
  }
}
