package com.recipe_manager.model.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.recipe_manager.model.dto.review.ReviewDto;
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.entity.review.Review;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

/**
 * Unit tests for {@link ReviewMapper}.
 */
@Tag("unit")
class ReviewMapperTest {

  private ReviewMapper reviewMapper;
  private Recipe recipe;
  private UUID userId;
  private LocalDateTime now;

  @BeforeEach
  void setUp() {
    reviewMapper = Mappers.getMapper(ReviewMapper.class);
    userId = UUID.randomUUID();
    now = LocalDateTime.now();

    recipe = Recipe.builder()
        .recipeId(123L)
        .userId(userId)
        .title("Test Recipe")
        .description("A test recipe")
        .build();
  }

  @Test
  @DisplayName("Should map Review entity to ReviewDto")
  void shouldMapReviewEntityToDto() {
    Review review = Review.builder()
        .reviewId(1L)
        .recipe(recipe)
        .userId(userId)
        .rating(new BigDecimal("4.5"))
        .comment("Great recipe!")
        .createdAt(now)
        .build();

    ReviewDto dto = reviewMapper.toDto(review);

    assertThat(dto).isNotNull();
    assertThat(dto.getReviewId()).isEqualTo(1L);
    assertThat(dto.getRecipeId()).isEqualTo(123L);
    assertThat(dto.getUserId()).isEqualTo(userId);
    assertThat(dto.getRating()).isEqualTo(new BigDecimal("4.5"));
    assertThat(dto.getComment()).isEqualTo("Great recipe!");
    assertThat(dto.getCreatedAt()).isEqualTo(now);
  }

  @Test
  @DisplayName("Should map ReviewDto to Review entity")
  void shouldMapReviewDtoToEntity() {
    ReviewDto dto = ReviewDto.builder()
        .reviewId(2L)
        .recipeId(456L)
        .userId(userId)
        .rating(new BigDecimal("3.0"))
        .comment("OK recipe")
        .createdAt(now)
        .build();

    Review entity = reviewMapper.toEntity(dto);

    assertThat(entity).isNotNull();
    assertThat(entity.getReviewId()).isEqualTo(2L);
    assertThat(entity.getRecipe()).isNull(); // Should be ignored in mapping
    assertThat(entity.getUserId()).isEqualTo(userId);
    assertThat(entity.getRating()).isEqualTo(new BigDecimal("3.0"));
    assertThat(entity.getComment()).isEqualTo("OK recipe");
    assertThat(entity.getCreatedAt()).isNull(); // Should be ignored in mapping
  }

  @Test
  @DisplayName("Should handle null Review entity")
  void shouldHandleNullReviewEntity() {
    ReviewDto dto = reviewMapper.toDto(null);
    assertThat(dto).isNull();
  }

  @Test
  @DisplayName("Should handle null ReviewDto")
  void shouldHandleNullReviewDto() {
    Review entity = reviewMapper.toEntity(null);
    assertThat(entity).isNull();
  }

  @Test
  @DisplayName("Should handle Review with null comment")
  void shouldHandleReviewWithNullComment() {
    Review review = Review.builder()
        .reviewId(3L)
        .recipe(recipe)
        .userId(userId)
        .rating(new BigDecimal("5.0"))
        .comment(null)
        .createdAt(now)
        .build();

    ReviewDto dto = reviewMapper.toDto(review);

    assertThat(dto).isNotNull();
    assertThat(dto.getComment()).isNull();
  }

  @Test
  @DisplayName("Should handle ReviewDto with null comment")
  void shouldHandleReviewDtoWithNullComment() {
    ReviewDto dto = ReviewDto.builder()
        .reviewId(4L)
        .recipeId(789L)
        .userId(userId)
        .rating(new BigDecimal("2.5"))
        .comment(null)
        .createdAt(now)
        .build();

    Review entity = reviewMapper.toEntity(dto);

    assertThat(entity).isNotNull();
    assertThat(entity.getComment()).isNull();
  }

  @Test
  @DisplayName("Should handle Review with null recipe")
  void shouldHandleReviewWithNullRecipe() {
    Review review = Review.builder()
        .reviewId(5L)
        .recipe(null)
        .userId(userId)
        .rating(new BigDecimal("4.0"))
        .comment("Good recipe")
        .createdAt(now)
        .build();

    ReviewDto dto = reviewMapper.toDto(review);

    assertThat(dto).isNotNull();
    assertThat(dto.getRecipeId()).isNull();
    assertThat(dto.getReviewId()).isEqualTo(5L);
    assertThat(dto.getUserId()).isEqualTo(userId);
    assertThat(dto.getRating()).isEqualTo(new BigDecimal("4.0"));
    assertThat(dto.getComment()).isEqualTo("Good recipe");
  }

  @Test
  @DisplayName("Should map all properties correctly in both directions")
  void shouldMapAllPropertiesCorrectlyInBothDirections() {
    // Create original entity
    Review originalEntity = Review.builder()
        .reviewId(10L)
        .recipe(recipe)
        .userId(userId)
        .rating(new BigDecimal("4.8"))
        .comment("Excellent recipe!")
        .createdAt(now)
        .build();

    // Map to DTO
    ReviewDto dto = reviewMapper.toDto(originalEntity);

    // Verify DTO mapping
    assertThat(dto.getReviewId()).isEqualTo(originalEntity.getReviewId());
    assertThat(dto.getRecipeId()).isEqualTo(originalEntity.getRecipe().getRecipeId());
    assertThat(dto.getUserId()).isEqualTo(originalEntity.getUserId());
    assertThat(dto.getRating()).isEqualTo(originalEntity.getRating());
    assertThat(dto.getComment()).isEqualTo(originalEntity.getComment());
    assertThat(dto.getCreatedAt()).isEqualTo(originalEntity.getCreatedAt());

    // Map back to entity (ignoring certain fields)
    Review mappedEntity = reviewMapper.toEntity(dto);

    // Verify entity mapping (only mapped fields)
    assertThat(mappedEntity.getReviewId()).isEqualTo(dto.getReviewId());
    assertThat(mappedEntity.getUserId()).isEqualTo(dto.getUserId());
    assertThat(mappedEntity.getRating()).isEqualTo(dto.getRating());
    assertThat(mappedEntity.getComment()).isEqualTo(dto.getComment());

    // Verify ignored fields
    assertThat(mappedEntity.getRecipe()).isNull();
    assertThat(mappedEntity.getCreatedAt()).isNull();
  }

  @Test
  @DisplayName("Should preserve BigDecimal precision in rating")
  void shouldPreserveBigDecimalPrecisionInRating() {
    BigDecimal preciseRating = new BigDecimal("4.75");

    Review review = Review.builder()
        .reviewId(6L)
        .recipe(recipe)
        .userId(userId)
        .rating(preciseRating)
        .comment("Very good")
        .createdAt(now)
        .build();

    ReviewDto dto = reviewMapper.toDto(review);

    assertThat(dto.getRating()).isEqualTo(preciseRating);
    assertThat(dto.getRating().toString()).isEqualTo("4.75");
  }
}
