package com.recipe_manager.repository.recipe;

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
