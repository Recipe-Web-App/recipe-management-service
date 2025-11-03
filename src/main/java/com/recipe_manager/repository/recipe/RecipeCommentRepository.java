package com.recipe_manager.repository.recipe;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.recipe_manager.model.entity.recipe.RecipeComment;

/** Repository interface for RecipeComment entity operations. */
@Repository
public interface RecipeCommentRepository extends JpaRepository<RecipeComment, Long> {

  /**
   * Find all comments for a specific recipe.
   *
   * @param recipeId the recipe ID
   * @return list of comments for the recipe
   */
  @Query(
      "SELECT rc FROM RecipeComment rc WHERE rc.recipe.recipeId = :recipeId ORDER BY rc.createdAt ASC")
  List<RecipeComment> findByRecipeIdOrderByCreatedAtAsc(@Param("recipeId") Long recipeId);

  /**
   * Find all public comments for a specific recipe.
   *
   * @param recipeId the recipe ID
   * @return list of public comments for the recipe
   */
  @Query(
      "SELECT rc FROM RecipeComment rc WHERE rc.recipe.recipeId = :recipeId AND rc.isPublic = true ORDER BY rc.createdAt ASC")
  List<RecipeComment> findPublicByRecipeIdOrderByCreatedAtAsc(@Param("recipeId") Long recipeId);

  /**
   * Find all comments for a specific recipe and user.
   *
   * @param recipeId the recipe ID
   * @param userId the user ID
   * @return list of comments for the recipe and user
   */
  @Query(
      "SELECT rc FROM RecipeComment rc WHERE rc.recipe.recipeId = :recipeId AND rc.userId = :userId ORDER BY rc.createdAt ASC")
  List<RecipeComment> findByRecipeIdAndUserIdOrderByCreatedAtAsc(
      @Param("recipeId") Long recipeId, @Param("userId") UUID userId);

  /**
   * Find a specific comment by ID and recipe ID.
   *
   * @param commentId the comment ID
   * @param recipeId the recipe ID
   * @return optional comment
   */
  @Query(
      "SELECT rc FROM RecipeComment rc WHERE rc.commentId = :commentId AND rc.recipe.recipeId = :recipeId")
  Optional<RecipeComment> findByCommentIdAndRecipeId(
      @Param("commentId") Long commentId, @Param("recipeId") Long recipeId);

  /**
   * Count comments for a specific recipe.
   *
   * @param recipeId the recipe ID
   * @return number of comments
   */
  @Query("SELECT COUNT(rc) FROM RecipeComment rc WHERE rc.recipe.recipeId = :recipeId")
  long countByRecipeId(@Param("recipeId") Long recipeId);

  /**
   * Count public comments for a specific recipe.
   *
   * @param recipeId the recipe ID
   * @return number of public comments
   */
  @Query(
      "SELECT COUNT(rc) FROM RecipeComment rc WHERE rc.recipe.recipeId = :recipeId AND rc.isPublic = true")
  long countPublicByRecipeId(@Param("recipeId") Long recipeId);
}
