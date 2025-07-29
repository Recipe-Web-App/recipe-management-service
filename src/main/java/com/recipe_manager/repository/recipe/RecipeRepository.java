package com.recipe_manager.repository.recipe;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.recipe_manager.model.entity.recipe.Recipe;

/** Repository interface for Recipe entity. Provides data access methods for recipe operations. */
@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

  /**
   * Find recipes by user ID.
   *
   * @param userId the user ID
   * @param pageable pagination information
   * @return page of recipes for the user
   */
  Page<Recipe> findByUserId(UUID userId, Pageable pageable);

  /**
   * Find recipes by title containing the given text (case-insensitive).
   *
   * @param title the title text to search for
   * @param pageable pagination information
   * @return page of recipes matching the title
   */
  Page<Recipe> findByTitleContainingIgnoreCase(String title, Pageable pageable);

  /**
   * Find recipes by difficulty level.
   *
   * @param difficulty the difficulty level
   * @param pageable pagination information
   * @return page of recipes with the specified difficulty
   */
  Page<Recipe> findByDifficulty(
      com.recipe_manager.model.enums.DifficultyLevel difficulty, Pageable pageable);

  /**
   * Find recipes by user ID and difficulty level.
   *
   * @param userId the user ID
   * @param difficulty the difficulty level
   * @param pageable pagination information
   * @return page of recipes for the user with the specified difficulty
   */
  Page<Recipe> findByUserIdAndDifficulty(
      UUID userId, com.recipe_manager.model.enums.DifficultyLevel difficulty, Pageable pageable);

  /**
   * Find recipes by preparation time less than or equal to the given time.
   *
   * @param maxPreparationTime the maximum preparation time in minutes
   * @param pageable pagination information
   * @return page of recipes with preparation time <= maxPreparationTime
   */
  Page<Recipe> findByPreparationTimeLessThanEqual(Integer maxPreparationTime, Pageable pageable);

  /**
   * Find recipes by cooking time less than or equal to the given time.
   *
   * @param maxCookingTime the maximum cooking time in minutes
   * @param pageable pagination information
   * @return page of recipes with cooking time <= maxCookingTime
   */
  Page<Recipe> findByCookingTimeLessThanEqual(Integer maxCookingTime, Pageable pageable);

  /**
   * Find recipes by servings greater than or equal to the given number.
   *
   * @param minServings the minimum number of servings
   * @param pageable pagination information
   * @return page of recipes with servings >= minServings
   */
  Page<Recipe> findByServingsGreaterThanEqual(Integer minServings, Pageable pageable);

  /**
   * Find recipes by user ID and title containing the given text (case-insensitive).
   *
   * @param userId the user ID
   * @param title the title text to search for
   * @param pageable pagination information
   * @return page of recipes for the user matching the title
   */
  Page<Recipe> findByUserIdAndTitleContainingIgnoreCase(
      UUID userId, String title, Pageable pageable);

  /**
   * Check if a recipe exists by ID and user ID.
   *
   * @param recipeId the recipe ID
   * @param userId the user ID
   * @return true if the recipe exists for the user, false otherwise
   */
  boolean existsByRecipeIdAndUserId(Long recipeId, UUID userId);

  /**
   * Count recipes by user ID.
   *
   * @param userId the user ID
   * @return the number of recipes for the user
   */
  long countByUserId(UUID userId);

  /**
   * Find all recipe IDs for a user.
   *
   * @param userId the user ID
   * @return list of recipe IDs for the user
   */
  @Query("SELECT r.recipeId FROM Recipe r WHERE r.userId = :userId")
  List<Long> findRecipeIdsByUserId(@Param("userId") UUID userId);

  /**
   * Find recipes with their ingredients and steps by user ID.
   *
   * @param userId the user ID
   * @param pageable pagination information
   * @return page of recipes with ingredients and steps for the user
   */
  @Query(
      "SELECT DISTINCT r FROM Recipe r "
          + "LEFT JOIN FETCH r.recipeIngredients ri "
          + "LEFT JOIN FETCH ri.ingredient "
          + "LEFT JOIN FETCH r.recipeSteps "
          + "WHERE r.userId = :userId")
  Page<Recipe> findByUserIdWithIngredientsAndSteps(@Param("userId") UUID userId, Pageable pageable);

  /**
   * Search recipes based on flexible criteria.
   *
   * @param searchRequest the search criteria
   * @param pageable pagination information
   * @return page of recipes matching the search criteria
   */
  @Query(
      "SELECT DISTINCT r FROM Recipe r "
          + "LEFT JOIN r.recipeIngredients ri "
          + "LEFT JOIN ri.ingredient i "
          + "WHERE (:#{#searchRequest.recipeNameQuery} IS NULL OR "
          + "       LOWER(r.title) LIKE LOWER(CONCAT('%', :#{#searchRequest.recipeNameQuery}, '%')) OR "
          + "       LOWER(r.description) LIKE LOWER(CONCAT('%', :#{#searchRequest.recipeNameQuery}, '%'))) "
          + "AND (:#{#searchRequest.difficulty} IS NULL OR r.difficulty = :#{#searchRequest.difficulty}) "
          + "AND (:#{#searchRequest.maxCookingTime} IS NULL OR r.cookingTime <= :#{#searchRequest.maxCookingTime}) "
          + "AND (:#{#searchRequest.maxPreparationTime} IS NULL OR r.preparationTime <= :#{#searchRequest.maxPreparationTime}) "
          + "AND (:#{#searchRequest.minServings} IS NULL OR r.servings >= :#{#searchRequest.minServings}) "
          + "AND (:#{#searchRequest.maxServings} IS NULL OR r.servings <= :#{#searchRequest.maxServings})")
  Page<Recipe> searchRecipes(
      @Param("searchRequest")
          com.recipe_manager.model.dto.request.SearchRecipesRequest searchRequest,
      Pageable pageable);
}
