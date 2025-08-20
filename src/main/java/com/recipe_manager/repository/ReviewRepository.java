package com.recipe_manager.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.recipe_manager.model.entity.review.Review;

/** Repository interface for Review entity. Provides data access methods for review operations. */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

  /**
   * Find all reviews for a specific recipe.
   *
   * @param recipeId the recipe ID
   * @return list of reviews for the recipe
   */
  List<Review> findByRecipeRecipeId(Long recipeId);

  /**
   * Find a specific user's review for a recipe.
   *
   * @param recipeId the recipe ID
   * @param userId the user ID
   * @return optional review if found
   */
  Optional<Review> findByRecipeRecipeIdAndUserId(Long recipeId, UUID userId);

  /**
   * Check if a user has already reviewed a specific recipe.
   *
   * @param recipeId the recipe ID
   * @param userId the user ID
   * @return true if the user has reviewed the recipe, false otherwise
   */
  boolean existsByRecipeRecipeIdAndUserId(Long recipeId, UUID userId);

  /**
   * Find all reviews by a specific user.
   *
   * @param userId the user ID
   * @return list of reviews by the user
   */
  List<Review> findByUserId(UUID userId);

  /**
   * Delete all reviews for a specific recipe.
   *
   * @param recipeId the recipe ID
   */
  void deleteByRecipeRecipeId(Long recipeId);
}
