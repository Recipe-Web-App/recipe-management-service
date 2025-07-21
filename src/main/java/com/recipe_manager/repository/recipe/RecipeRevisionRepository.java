package com.recipe_manager.repository.recipe;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.recipe_manager.model.entity.recipe.RecipeRevision;
import com.recipe_manager.model.enums.RevisionCategory;
import com.recipe_manager.model.enums.RevisionType;

/**
 * Repository interface for RecipeRevision entity. Provides data access methods for recipe revision
 * operations.
 */
@Repository
public interface RecipeRevisionRepository extends JpaRepository<RecipeRevision, Long> {

  /**
   * Find all revisions for a recipe, ordered by creation date descending.
   *
   * @param recipeId the recipe ID
   * @param pageable pagination information
   * @return page of revisions for the recipe
   */
  Page<RecipeRevision> findByRecipeIdOrderByCreatedAtDesc(Long recipeId, Pageable pageable);

  /**
   * Find revisions by recipe ID and revision category.
   *
   * @param recipeId the recipe ID
   * @param revisionCategory the revision category
   * @param pageable pagination information
   * @return page of revisions matching the criteria
   */
  Page<RecipeRevision> findByRecipeIdAndRevisionCategoryOrderByCreatedAtDesc(
      Long recipeId, RevisionCategory revisionCategory, Pageable pageable);

  /**
   * Find revisions by recipe ID and revision type.
   *
   * @param recipeId the recipe ID
   * @param revisionType the revision type
   * @param pageable pagination information
   * @return page of revisions matching the criteria
   */
  Page<RecipeRevision> findByRecipeIdAndRevisionTypeOrderByCreatedAtDesc(
      Long recipeId, RevisionType revisionType, Pageable pageable);

  /**
   * Find revisions by user ID.
   *
   * @param userId the user ID
   * @param pageable pagination information
   * @return page of revisions by the user
   */
  Page<RecipeRevision> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

  /**
   * Find revisions by user ID and recipe ID.
   *
   * @param userId the user ID
   * @param recipeId the recipe ID
   * @param pageable pagination information
   * @return page of revisions by the user for the recipe
   */
  Page<RecipeRevision> findByUserIdAndRecipeIdOrderByCreatedAtDesc(
      UUID userId, Long recipeId, Pageable pageable);

  /**
   * Count revisions for a recipe.
   *
   * @param recipeId the recipe ID
   * @return the number of revisions for the recipe
   */
  long countByRecipeId(Long recipeId);

  /**
   * Count revisions by user ID.
   *
   * @param userId the user ID
   * @return the number of revisions by the user
   */
  long countByUserId(UUID userId);

  /**
   * Find the latest revision for a recipe.
   *
   * @param recipeId the recipe ID
   * @return the latest revision for the recipe, or null if no revisions exist
   */
  @Query(
      "SELECT rr FROM RecipeRevision rr "
          + "WHERE rr.recipe.recipeId = :recipeId "
          + "ORDER BY rr.createdAt DESC")
  List<RecipeRevision> findLatestRevisionByRecipeId(@Param("recipeId") Long recipeId);

  /**
   * Find revisions by recipe ID and date range.
   *
   * @param recipeId the recipe ID
   * @param startDate the start date (inclusive)
   * @param endDate the end date (inclusive)
   * @param pageable pagination information
   * @return page of revisions in the date range
   */
  @Query(
      "SELECT rr FROM RecipeRevision rr "
          + "WHERE rr.recipe.recipeId = :recipeId "
          + "AND rr.createdAt >= :startDate "
          + "AND rr.createdAt <= :endDate "
          + "ORDER BY rr.createdAt DESC")
  Page<RecipeRevision> findByRecipeIdAndDateRange(
      @Param("recipeId") Long recipeId,
      @Param("startDate") java.time.LocalDateTime startDate,
      @Param("endDate") java.time.LocalDateTime endDate,
      Pageable pageable);
}
