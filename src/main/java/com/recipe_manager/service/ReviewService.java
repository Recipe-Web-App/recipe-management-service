package com.recipe_manager.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.recipe_manager.exception.BusinessException;
import com.recipe_manager.exception.ResourceNotFoundException;
import com.recipe_manager.model.dto.request.AddReviewRequest;
import com.recipe_manager.model.dto.request.EditReviewRequest;
import com.recipe_manager.model.dto.response.ReviewResponse;
import com.recipe_manager.model.dto.review.ReviewDto;
import com.recipe_manager.model.entity.Review;
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.mapper.ReviewMapper;
import com.recipe_manager.repository.ReviewRepository;
import com.recipe_manager.repository.recipe.RecipeRepository;
import com.recipe_manager.util.SecurityUtils;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/** Service for review-related operations. */
@Service
public class ReviewService {

  private final ReviewRepository reviewRepository;
  private final RecipeRepository recipeRepository;
  private final ReviewMapper reviewMapper;

  @Autowired
  @SuppressFBWarnings(
      value = "EI_EXPOSE_REP2",
      justification = "Spring-managed beans are safe to inject and not exposed externally")
  public ReviewService(
      final ReviewRepository reviewRepository,
      final RecipeRepository recipeRepository,
      final ReviewMapper reviewMapper) {
    this.reviewRepository = reviewRepository;
    this.recipeRepository = recipeRepository;
    this.reviewMapper = reviewMapper;
  }

  /**
   * Get all reviews for a recipe.
   *
   * @param recipeId the recipe ID
   * @return response containing all reviews for the recipe
   */
  public ReviewResponse getReviews(final Long recipeId) {
    List<Review> reviews = reviewRepository.findByRecipeRecipeId(recipeId);
    List<ReviewDto> reviewDtos = reviews.stream().map(reviewMapper::toDto).toList();

    return ReviewResponse.builder().recipeId(recipeId).reviews(reviewDtos).build();
  }

  /**
   * Add a review to a recipe.
   *
   * @param recipeId the recipe ID
   * @param request the review request
   * @return the created review
   * @throws ResourceNotFoundException if recipe not found
   * @throws BusinessException if user already reviewed recipe or rating invalid
   */
  @Transactional
  public ReviewDto addReview(final Long recipeId, final AddReviewRequest request) {
    validateRatingIncrements(request.getRating());

    UUID currentUserId = SecurityUtils.getCurrentUserId();

    if (reviewRepository.existsByRecipeRecipeIdAndUserId(recipeId, currentUserId)) {
      throw new BusinessException("User has already reviewed this recipe");
    }

    Recipe recipe =
        recipeRepository
            .findById(recipeId)
            .orElseThrow(
                () -> new ResourceNotFoundException("Recipe not found with ID: " + recipeId));

    Review review =
        Review.builder()
            .recipe(recipe)
            .userId(currentUserId)
            .rating(request.getRating())
            .comment(request.getComment())
            .build();

    Review savedReview = reviewRepository.save(review);
    return reviewMapper.toDto(savedReview);
  }

  /**
   * Edit a review on a recipe.
   *
   * @param recipeId the recipe ID
   * @param reviewId the review ID
   * @param request the edit request
   * @return the updated review
   * @throws ResourceNotFoundException if recipe or review not found
   * @throws AccessDeniedException if user doesn't own the review
   * @throws BusinessException if rating invalid
   */
  @Transactional
  public ReviewDto editReview(
      final Long recipeId, final Long reviewId, final EditReviewRequest request) {
    validateRatingIncrements(request.getRating());

    Review review =
        reviewRepository
            .findByRecipeRecipeIdAndUserId(recipeId, SecurityUtils.getCurrentUserId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Review not found with ID: " + reviewId + " for recipe: " + recipeId));

    if (!review.getReviewId().equals(reviewId)) {
      throw new ResourceNotFoundException(
          "Review not found with ID: " + reviewId + " for recipe: " + recipeId);
    }

    if (!review.getUserId().equals(SecurityUtils.getCurrentUserId())) {
      throw new AccessDeniedException("User can only edit their own reviews");
    }

    review.setRating(request.getRating());
    review.setComment(request.getComment());

    Review savedReview = reviewRepository.save(review);
    return reviewMapper.toDto(savedReview);
  }

  /**
   * Delete a review from a recipe.
   *
   * @param recipeId the recipe ID
   * @param reviewId the review ID
   * @throws ResourceNotFoundException if recipe or review not found
   * @throws AccessDeniedException if user doesn't own the review
   */
  @Transactional
  public void deleteReview(final Long recipeId, final Long reviewId) {
    Review review =
        reviewRepository
            .findByRecipeRecipeIdAndUserId(recipeId, SecurityUtils.getCurrentUserId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Review not found with ID: " + reviewId + " for recipe: " + recipeId));

    if (!review.getReviewId().equals(reviewId)) {
      throw new ResourceNotFoundException(
          "Review not found with ID: " + reviewId + " for recipe: " + recipeId);
    }

    if (!review.getUserId().equals(SecurityUtils.getCurrentUserId())) {
      throw new AccessDeniedException("User can only delete their own reviews");
    }

    reviewRepository.delete(review);
  }

  private void validateRatingIncrements(final BigDecimal rating) {
    if (rating == null) {
      return;
    }

    BigDecimal doubled = rating.multiply(BigDecimal.valueOf(2));
    if (doubled.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) != 0) {
      throw new BusinessException(
          "Rating must be in increments of 0.5 (e.g., 0.0, 0.5, 1.0, 1.5, etc.)");
    }
  }
}
