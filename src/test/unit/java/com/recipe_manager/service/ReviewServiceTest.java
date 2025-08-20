package com.recipe_manager.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.recipe_manager.exception.BusinessException;
import com.recipe_manager.exception.ResourceNotFoundException;
import com.recipe_manager.model.dto.request.AddReviewRequest;
import com.recipe_manager.model.dto.request.EditReviewRequest;
import com.recipe_manager.model.dto.response.ReviewResponse;
import com.recipe_manager.model.dto.review.ReviewDto;
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.entity.review.Review;
import com.recipe_manager.model.mapper.ReviewMapper;
import com.recipe_manager.repository.ReviewRepository;
import com.recipe_manager.repository.recipe.RecipeRepository;
import com.recipe_manager.util.SecurityUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

/**
 * Unit tests for {@link ReviewService}.
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class ReviewServiceTest {

  @Mock
  private ReviewRepository reviewRepository;
  @Mock
  private RecipeRepository recipeRepository;
  @Mock
  private ReviewMapper reviewMapper;

  private ReviewService reviewService;

  private static final Long RECIPE_ID = 1L;
  private static final Long REVIEW_ID = 100L;
  private static final UUID USER_ID = UUID.randomUUID();
  private static final UUID OTHER_USER_ID = UUID.randomUUID();

  @BeforeEach
  void setUp() {
    reviewService = new ReviewService(reviewRepository, recipeRepository, reviewMapper);
  }

  @Nested
  @DisplayName("getReviews")
  class GetReviewsTests {

    @Test
    @Tag("standard-processing")
    @DisplayName("should return reviews for existing recipe")
    void getReviews_shouldReturnReviewsForExistingRecipe() {
      Review review1 = mock(Review.class);
      Review review2 = mock(Review.class);
      List<Review> reviews = Arrays.asList(review1, review2);

      ReviewDto reviewDto1 = mock(ReviewDto.class);
      ReviewDto reviewDto2 = mock(ReviewDto.class);
      when(reviewDto1.getRating()).thenReturn(new BigDecimal("4.5"));
      when(reviewDto2.getRating()).thenReturn(new BigDecimal("3.0"));

      when(reviewRepository.findByRecipeRecipeId(RECIPE_ID)).thenReturn(reviews);
      when(reviewMapper.toDto(review1)).thenReturn(reviewDto1);
      when(reviewMapper.toDto(review2)).thenReturn(reviewDto2);

      ReviewResponse response = reviewService.getReviews(RECIPE_ID);

      assertThat(response.getRecipeId()).isEqualTo(RECIPE_ID);
      assertThat(response.getReviews()).hasSize(2);
      assertThat(response.getReviews().get(0).getRating()).isEqualTo(new BigDecimal("4.5"));
      assertThat(response.getReviews().get(1).getRating()).isEqualTo(new BigDecimal("3.0"));
    }

    @Test
    @Tag("standard-processing")
    @DisplayName("should return empty list for recipe with no reviews")
    void getReviews_shouldReturnEmptyListForRecipeWithNoReviews() {
      when(reviewRepository.findByRecipeRecipeId(RECIPE_ID)).thenReturn(Arrays.asList());

      ReviewResponse response = reviewService.getReviews(RECIPE_ID);

      assertThat(response.getRecipeId()).isEqualTo(RECIPE_ID);
      assertThat(response.getReviews()).isEmpty();
    }
  }

  @Nested
  @DisplayName("addReview")
  class AddReviewTests {

    @Test
    @Tag("standard-processing")
    @DisplayName("should successfully add review with valid data")
    void addReview_shouldSuccessfullyAddReviewWithValidData() {
      AddReviewRequest request = AddReviewRequest.builder()
          .rating(new BigDecimal("4.5"))
          .comment("Excellent recipe!")
          .build();

      Recipe recipe = mock(Recipe.class);
      Review savedReview = mock(Review.class);
      ReviewDto expectedDto = mock(ReviewDto.class);
      when(expectedDto.getRating()).thenReturn(new BigDecimal("4.5"));
      when(expectedDto.getComment()).thenReturn("Excellent recipe!");

      try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
        mockedSecurity.when(SecurityUtils::getCurrentUserId).thenReturn(USER_ID);

        when(reviewRepository.existsByRecipeRecipeIdAndUserId(RECIPE_ID, USER_ID)).thenReturn(false);
        when(recipeRepository.findById(RECIPE_ID)).thenReturn(Optional.of(recipe));
        when(reviewRepository.save(any(Review.class))).thenReturn(savedReview);
        when(reviewMapper.toDto(savedReview)).thenReturn(expectedDto);

        ReviewDto result = reviewService.addReview(RECIPE_ID, request);

        assertThat(result.getRating()).isEqualTo(new BigDecimal("4.5"));
        assertThat(result.getComment()).isEqualTo("Excellent recipe!");
        verify(reviewRepository).save(any(Review.class));
      }
    }

    @Test
    @Tag("error-processing")
    @DisplayName("should throw exception when user already reviewed recipe")
    void addReview_shouldThrowExceptionWhenUserAlreadyReviewedRecipe() {
      AddReviewRequest request = AddReviewRequest.builder()
          .rating(new BigDecimal("4.5"))
          .comment("Excellent recipe!")
          .build();

      try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
        mockedSecurity.when(SecurityUtils::getCurrentUserId).thenReturn(USER_ID);
        when(reviewRepository.existsByRecipeRecipeIdAndUserId(RECIPE_ID, USER_ID)).thenReturn(true);

        assertThatThrownBy(() -> reviewService.addReview(RECIPE_ID, request))
            .isInstanceOf(BusinessException.class)
            .hasMessage("User has already reviewed this recipe");

        verify(reviewRepository, never()).save(any(Review.class));
      }
    }

    @Test
    @Tag("error-processing")
    @DisplayName("should throw exception when recipe not found")
    void addReview_shouldThrowExceptionWhenRecipeNotFound() {
      AddReviewRequest request = AddReviewRequest.builder()
          .rating(new BigDecimal("4.5"))
          .comment("Excellent recipe!")
          .build();

      try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
        mockedSecurity.when(SecurityUtils::getCurrentUserId).thenReturn(USER_ID);
        when(reviewRepository.existsByRecipeRecipeIdAndUserId(RECIPE_ID, USER_ID)).thenReturn(false);
        when(recipeRepository.findById(RECIPE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.addReview(RECIPE_ID, request))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Recipe not found with ID: " + RECIPE_ID);

        verify(reviewRepository, never()).save(any(Review.class));
      }
    }

    @Test
    @Tag("error-processing")
    @DisplayName("should throw exception for invalid rating increments")
    void addReview_shouldThrowExceptionForInvalidRatingIncrements() {
      AddReviewRequest request = AddReviewRequest.builder()
          .rating(new BigDecimal("4.3"))
          .comment("Good recipe!")
          .build();

      try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
        mockedSecurity.when(SecurityUtils::getCurrentUserId).thenReturn(USER_ID);

        assertThatThrownBy(() -> reviewService.addReview(RECIPE_ID, request))
            .isInstanceOf(BusinessException.class)
            .hasMessage("Rating must be in increments of 0.5 (e.g., 0.0, 0.5, 1.0, 1.5, etc.)");

        verify(reviewRepository, never()).existsByRecipeRecipeIdAndUserId(anyLong(), any(UUID.class));
      }
    }
  }

  @Nested
  @DisplayName("editReview")
  class EditReviewTests {

    @Test
    @Tag("standard-processing")
    @DisplayName("should successfully edit review with valid data")
    void editReview_shouldSuccessfullyEditReviewWithValidData() {
      EditReviewRequest request = EditReviewRequest.builder()
          .rating(new BigDecimal("3.5"))
          .comment("Updated comment")
          .build();

      Review existingReview = mock(Review.class);
      when(existingReview.getReviewId()).thenReturn(REVIEW_ID);
      when(existingReview.getUserId()).thenReturn(USER_ID);

      Review updatedReview = mock(Review.class);
      ReviewDto expectedDto = mock(ReviewDto.class);
      when(expectedDto.getRating()).thenReturn(new BigDecimal("3.5"));
      when(expectedDto.getComment()).thenReturn("Updated comment");

      try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
        mockedSecurity.when(SecurityUtils::getCurrentUserId).thenReturn(USER_ID);

        when(reviewRepository.findByRecipeRecipeIdAndUserId(RECIPE_ID, USER_ID))
            .thenReturn(Optional.of(existingReview));
        when(reviewRepository.save(existingReview)).thenReturn(updatedReview);
        when(reviewMapper.toDto(updatedReview)).thenReturn(expectedDto);

        ReviewDto result = reviewService.editReview(RECIPE_ID, REVIEW_ID, request);

        assertThat(result.getRating()).isEqualTo(new BigDecimal("3.5"));
        assertThat(result.getComment()).isEqualTo("Updated comment");
        verify(reviewRepository).save(existingReview);
      }
    }

    @Test
    @Tag("error-processing")
    @DisplayName("should throw exception when review not found")
    void editReview_shouldThrowExceptionWhenReviewNotFound() {
      EditReviewRequest request = EditReviewRequest.builder()
          .rating(new BigDecimal("3.5"))
          .comment("Updated comment")
          .build();

      try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
        mockedSecurity.when(SecurityUtils::getCurrentUserId).thenReturn(USER_ID);
        when(reviewRepository.findByRecipeRecipeIdAndUserId(RECIPE_ID, USER_ID))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.editReview(RECIPE_ID, REVIEW_ID, request))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Review not found with ID: " + REVIEW_ID + " for recipe: " + RECIPE_ID);

        verify(reviewRepository, never()).save(any(Review.class));
      }
    }

    @Test
    @Tag("error-processing")
    @DisplayName("should throw exception when review ID doesn't match")
    void editReview_shouldThrowExceptionWhenReviewIdDoesntMatch() {
      EditReviewRequest request = EditReviewRequest.builder()
          .rating(new BigDecimal("3.5"))
          .comment("Updated comment")
          .build();

      Review existingReview = mock(Review.class);
      when(existingReview.getReviewId()).thenReturn(999L);

      try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
        mockedSecurity.when(SecurityUtils::getCurrentUserId).thenReturn(USER_ID);
        when(reviewRepository.findByRecipeRecipeIdAndUserId(RECIPE_ID, USER_ID))
            .thenReturn(Optional.of(existingReview));

        assertThatThrownBy(() -> reviewService.editReview(RECIPE_ID, REVIEW_ID, request))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Review not found with ID: " + REVIEW_ID + " for recipe: " + RECIPE_ID);

        verify(reviewRepository, never()).save(any(Review.class));
      }
    }

    @Test
    @Tag("error-processing")
    @DisplayName("should throw exception when user doesn't own review")
    void editReview_shouldThrowExceptionWhenUserDoesntOwnReview() {
      EditReviewRequest request = EditReviewRequest.builder()
          .rating(new BigDecimal("3.5"))
          .comment("Updated comment")
          .build();

      Review existingReview = mock(Review.class);
      when(existingReview.getReviewId()).thenReturn(REVIEW_ID);
      when(existingReview.getUserId()).thenReturn(OTHER_USER_ID);

      try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
        mockedSecurity.when(SecurityUtils::getCurrentUserId).thenReturn(USER_ID);
        when(reviewRepository.findByRecipeRecipeIdAndUserId(RECIPE_ID, USER_ID))
            .thenReturn(Optional.of(existingReview));

        assertThatThrownBy(() -> reviewService.editReview(RECIPE_ID, REVIEW_ID, request))
            .isInstanceOf(AccessDeniedException.class)
            .hasMessage("User can only edit their own reviews");

        verify(reviewRepository, never()).save(any(Review.class));
      }
    }

    @Test
    @Tag("error-processing")
    @DisplayName("should throw exception for invalid rating increments")
    void editReview_shouldThrowExceptionForInvalidRatingIncrements() {
      EditReviewRequest request = EditReviewRequest.builder()
          .rating(new BigDecimal("2.7"))
          .comment("Updated comment")
          .build();

      try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
        mockedSecurity.when(SecurityUtils::getCurrentUserId).thenReturn(USER_ID);

        assertThatThrownBy(() -> reviewService.editReview(RECIPE_ID, REVIEW_ID, request))
            .isInstanceOf(BusinessException.class)
            .hasMessage("Rating must be in increments of 0.5 (e.g., 0.0, 0.5, 1.0, 1.5, etc.)");

        verify(reviewRepository, never()).findByRecipeRecipeIdAndUserId(anyLong(), any(UUID.class));
      }
    }
  }

  @Nested
  @DisplayName("deleteReview")
  class DeleteReviewTests {

    @Test
    @Tag("standard-processing")
    @DisplayName("should successfully delete review")
    void deleteReview_shouldSuccessfullyDeleteReview() {
      Review existingReview = mock(Review.class);
      when(existingReview.getReviewId()).thenReturn(REVIEW_ID);
      when(existingReview.getUserId()).thenReturn(USER_ID);

      try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
        mockedSecurity.when(SecurityUtils::getCurrentUserId).thenReturn(USER_ID);
        when(reviewRepository.findByRecipeRecipeIdAndUserId(RECIPE_ID, USER_ID))
            .thenReturn(Optional.of(existingReview));

        reviewService.deleteReview(RECIPE_ID, REVIEW_ID);

        verify(reviewRepository).delete(existingReview);
      }
    }

    @Test
    @Tag("error-processing")
    @DisplayName("should throw exception when review not found")
    void deleteReview_shouldThrowExceptionWhenReviewNotFound() {
      try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
        mockedSecurity.when(SecurityUtils::getCurrentUserId).thenReturn(USER_ID);
        when(reviewRepository.findByRecipeRecipeIdAndUserId(RECIPE_ID, USER_ID))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.deleteReview(RECIPE_ID, REVIEW_ID))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Review not found with ID: " + REVIEW_ID + " for recipe: " + RECIPE_ID);

        verify(reviewRepository, never()).delete(any(Review.class));
      }
    }

    @Test
    @Tag("error-processing")
    @DisplayName("should throw exception when review ID doesn't match")
    void deleteReview_shouldThrowExceptionWhenReviewIdDoesntMatch() {
      Review existingReview = mock(Review.class);
      when(existingReview.getReviewId()).thenReturn(999L);

      try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
        mockedSecurity.when(SecurityUtils::getCurrentUserId).thenReturn(USER_ID);
        when(reviewRepository.findByRecipeRecipeIdAndUserId(RECIPE_ID, USER_ID))
            .thenReturn(Optional.of(existingReview));

        assertThatThrownBy(() -> reviewService.deleteReview(RECIPE_ID, REVIEW_ID))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Review not found with ID: " + REVIEW_ID + " for recipe: " + RECIPE_ID);

        verify(reviewRepository, never()).delete(any(Review.class));
      }
    }

    @Test
    @Tag("error-processing")
    @DisplayName("should throw exception when user doesn't own review")
    void deleteReview_shouldThrowExceptionWhenUserDoesntOwnReview() {
      Review existingReview = mock(Review.class);
      when(existingReview.getReviewId()).thenReturn(REVIEW_ID);
      when(existingReview.getUserId()).thenReturn(OTHER_USER_ID);

      try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
        mockedSecurity.when(SecurityUtils::getCurrentUserId).thenReturn(USER_ID);
        when(reviewRepository.findByRecipeRecipeIdAndUserId(RECIPE_ID, USER_ID))
            .thenReturn(Optional.of(existingReview));

        assertThatThrownBy(() -> reviewService.deleteReview(RECIPE_ID, REVIEW_ID))
            .isInstanceOf(AccessDeniedException.class)
            .hasMessage("User can only delete their own reviews");

        verify(reviewRepository, never()).delete(any(Review.class));
      }
    }
  }

  @Nested
  @DisplayName("validateRatingIncrements")
  class ValidateRatingIncrementsTests {

    @Test
    @Tag("standard-processing")
    @DisplayName("should accept valid rating increments")
    void validateRatingIncrements_shouldAcceptValidRatingIncrements() {
      List<BigDecimal> validRatings = Arrays.asList(
          new BigDecimal("0.0"),
          new BigDecimal("0.5"),
          new BigDecimal("1.0"),
          new BigDecimal("1.5"),
          new BigDecimal("2.0"),
          new BigDecimal("2.5"),
          new BigDecimal("3.0"),
          new BigDecimal("3.5"),
          new BigDecimal("4.0"),
          new BigDecimal("4.5"),
          new BigDecimal("5.0"));

      for (BigDecimal rating : validRatings) {
        AddReviewRequest request = AddReviewRequest.builder()
            .rating(rating)
            .comment("Test comment")
            .build();

        try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
          mockedSecurity.when(SecurityUtils::getCurrentUserId).thenReturn(USER_ID);
          when(reviewRepository.existsByRecipeRecipeIdAndUserId(RECIPE_ID, USER_ID)).thenReturn(false);
          when(recipeRepository.findById(RECIPE_ID)).thenReturn(Optional.of(mock(Recipe.class)));
          when(reviewRepository.save(any(Review.class))).thenReturn(mock(Review.class));
          when(reviewMapper.toDto(any(Review.class))).thenReturn(mock(ReviewDto.class));

          // This should not throw an exception
          reviewService.addReview(RECIPE_ID, request);
        }
      }
    }

    @Test
    @Tag("error-processing")
    @DisplayName("should reject invalid rating increments")
    void validateRatingIncrements_shouldRejectInvalidRatingIncrements() {
      List<BigDecimal> invalidRatings = Arrays.asList(
          new BigDecimal("0.1"),
          new BigDecimal("0.3"),
          new BigDecimal("0.7"),
          new BigDecimal("1.1"),
          new BigDecimal("1.7"),
          new BigDecimal("2.3"),
          new BigDecimal("2.9"),
          new BigDecimal("3.1"),
          new BigDecimal("3.7"),
          new BigDecimal("4.1"),
          new BigDecimal("4.3"),
          new BigDecimal("4.7"),
          new BigDecimal("4.9"));

      for (BigDecimal rating : invalidRatings) {
        AddReviewRequest request = AddReviewRequest.builder()
            .rating(rating)
            .comment("Test comment")
            .build();

        try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
          mockedSecurity.when(SecurityUtils::getCurrentUserId).thenReturn(USER_ID);

          assertThatThrownBy(() -> reviewService.addReview(RECIPE_ID, request))
              .isInstanceOf(BusinessException.class)
              .hasMessage("Rating must be in increments of 0.5 (e.g., 0.0, 0.5, 1.0, 1.5, etc.)");
        }
      }
    }

    @Test
    @Tag("standard-processing")
    @DisplayName("should handle null rating gracefully")
    void validateRatingIncrements_shouldHandleNullRatingGracefully() {
      AddReviewRequest request = AddReviewRequest.builder()
          .rating(null)
          .comment("Test comment")
          .build();

      try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
        mockedSecurity.when(SecurityUtils::getCurrentUserId).thenReturn(USER_ID);
        when(reviewRepository.existsByRecipeRecipeIdAndUserId(RECIPE_ID, USER_ID)).thenReturn(false);
        when(recipeRepository.findById(RECIPE_ID)).thenReturn(Optional.of(mock(Recipe.class)));
        when(reviewRepository.save(any(Review.class))).thenReturn(mock(Review.class));
        when(reviewMapper.toDto(any(Review.class))).thenReturn(mock(ReviewDto.class));

        // This should not throw an exception
        reviewService.addReview(RECIPE_ID, request);
      }
    }
  }

}
