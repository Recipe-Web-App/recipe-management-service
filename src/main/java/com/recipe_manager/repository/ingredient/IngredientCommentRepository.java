package com.recipe_manager.repository.ingredient;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.recipe_manager.model.entity.ingredient.IngredientComment;

/** Repository interface for IngredientComment entity operations. */
@Repository
public interface IngredientCommentRepository extends JpaRepository<IngredientComment, Long> {

  /**
   * Find all comments for a specific ingredient.
   *
   * @param ingredientId the ingredient ID
   * @return list of comments for the ingredient
   */
  List<IngredientComment> findByIngredientIngredientIdOrderByCreatedAtAsc(Long ingredientId);

  /**
   * Find all public comments for a specific ingredient.
   *
   * @param ingredientId the ingredient ID
   * @return list of public comments for the ingredient
   */
  List<IngredientComment> findByIngredientIngredientIdAndIsPublicTrueOrderByCreatedAtAsc(
      Long ingredientId);

  /**
   * Find all comments for a specific ingredient and user.
   *
   * @param ingredientId the ingredient ID
   * @param userId the user ID
   * @return list of comments for the ingredient and user
   */
  List<IngredientComment> findByIngredientIngredientIdAndUserIdOrderByCreatedAtAsc(
      Long ingredientId, UUID userId);

  /**
   * Find a specific comment by ID and ingredient ID.
   *
   * @param commentId the comment ID
   * @param ingredientId the ingredient ID
   * @return optional comment
   */
  Optional<IngredientComment> findByCommentIdAndIngredientIngredientId(
      Long commentId, Long ingredientId);

  /**
   * Find comments for ingredients in a specific recipe.
   *
   * @param recipeId the recipe ID
   * @return list of comments for ingredients in the recipe
   */
  @Query(
      "SELECT ic FROM IngredientComment ic "
          + "JOIN ic.ingredient i "
          + "JOIN RecipeIngredient ri ON ri.ingredient = i "
          + "WHERE ri.recipe.recipeId = :recipeId "
          + "ORDER BY i.ingredientId ASC, ic.createdAt ASC")
  List<IngredientComment> findByRecipeId(@Param("recipeId") Long recipeId);

  /**
   * Count comments for a specific ingredient.
   *
   * @param ingredientId the ingredient ID
   * @return number of comments
   */
  long countByIngredientIngredientId(Long ingredientId);
}
