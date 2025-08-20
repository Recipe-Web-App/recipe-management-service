package com.recipe_manager.repository.recipe;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.recipe_manager.model.entity.recipe.RecipeRevision;

/** Repository for managing recipe revision data access. */
@Repository
public interface RecipeRevisionRepository extends JpaRepository<RecipeRevision, Long> {

  /**
   * Find all revisions for a specific recipe.
   *
   * @param recipeId the recipe ID
   * @return list of revisions for the recipe
   */
  @Query(
      "SELECT r FROM RecipeRevision r WHERE r.recipe.recipeId = :recipeId ORDER BY r.createdAt DESC")
  List<RecipeRevision> findByRecipeId(@Param("recipeId") Long recipeId);

  /**
   * Find all revisions made by a specific user.
   *
   * @param userId the user ID
   * @return list of revisions made by the user
   */
  @Query("SELECT r FROM RecipeRevision r WHERE r.userId = :userId ORDER BY r.createdAt DESC")
  List<RecipeRevision> findByUserId(@Param("userId") UUID userId);

  /**
   * Find all revisions for a specific recipe made by a specific user.
   *
   * @param recipeId the recipe ID
   * @param userId the user ID
   * @return list of revisions for the recipe made by the user
   */
  @Query(
      "SELECT r FROM RecipeRevision r WHERE r.recipe.recipeId = :recipeId AND r.userId = :userId ORDER BY r.createdAt DESC")
  List<RecipeRevision> findByRecipeIdAndUserId(
      @Param("recipeId") Long recipeId, @Param("userId") UUID userId);
}
