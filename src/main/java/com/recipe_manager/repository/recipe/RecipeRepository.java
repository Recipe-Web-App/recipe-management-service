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
   * @param recipeNameQuery the recipe name query
   * @param difficulty the difficulty level
   * @param maxCookingTime maximum cooking time
   * @param maxPreparationTime maximum preparation time
   * @param minServings minimum servings
   * @param maxServings maximum servings
   * @param ingredientsList array of ingredient names
   * @param tagsList array of tag names
   * @param pageable pagination information
   * @return page of recipes matching the search criteria
   */
  @Query(
      value =
          "SELECT DISTINCT r.* FROM recipe_manager.recipes r "
              + "WHERE (:recipeNameQuery IS NULL OR "
              + "       LOWER(r.title) LIKE LOWER(CONCAT('%', :recipeNameQuery, '%')) OR "
              + "       LOWER(r.description) LIKE LOWER(CONCAT('%', :recipeNameQuery, '%'))) "
              + "AND (:difficulty IS NULL OR r.difficulty::text = :difficulty) "
              + "AND (:maxCookingTime IS NULL OR r.cooking_time <= :maxCookingTime) "
              + "AND (:maxPreparationTime IS NULL OR r.preparation_time <= :maxPreparationTime) "
              + "AND (:minServings IS NULL OR r.servings >= :minServings) "
              + "AND (:maxServings IS NULL OR r.servings <= :maxServings) "
              + "AND (COALESCE(array_length(:ingredientsList, 1), 0) = 0 OR "
              + "     EXISTS (SELECT 1 FROM recipe_manager.recipe_ingredients ri "
              + "             JOIN recipe_manager.ingredients i ON i.ingredient_id = ri.ingredient_id "
              + "             WHERE ri.recipe_id = r.recipe_id AND LOWER(i.name) = ANY(:ingredientsList))) "
              + "AND (COALESCE(array_length(:tagsList, 1), 0) = 0 OR "
              + "     EXISTS (SELECT 1 FROM recipe_manager.recipe_tag_junction rtj "
              + "             JOIN recipe_manager.recipe_tags rt ON rt.tag_id = rtj.tag_id "
              + "             WHERE rtj.recipe_id = r.recipe_id AND rt.name = ANY(:tagsList)))",
      nativeQuery = true,
      countQuery =
          "SELECT COUNT(DISTINCT r.recipe_id) FROM recipe_manager.recipes r "
              + "WHERE (:recipeNameQuery IS NULL OR "
              + "       LOWER(r.title) LIKE LOWER(CONCAT('%', :recipeNameQuery, '%')) OR "
              + "       LOWER(r.description) LIKE LOWER(CONCAT('%', :recipeNameQuery, '%'))) "
              + "AND (:difficulty IS NULL OR r.difficulty::text = :difficulty) "
              + "AND (:maxCookingTime IS NULL OR r.cooking_time <= :maxCookingTime) "
              + "AND (:maxPreparationTime IS NULL OR r.preparation_time <= :maxPreparationTime) "
              + "AND (:minServings IS NULL OR r.servings >= :minServings) "
              + "AND (:maxServings IS NULL OR r.servings <= :maxServings) "
              + "AND (COALESCE(array_length(:ingredientsList, 1), 0) = 0 OR "
              + "     EXISTS (SELECT 1 FROM recipe_manager.recipe_ingredients ri "
              + "             JOIN recipe_manager.ingredients i ON i.ingredient_id = ri.ingredient_id "
              + "             WHERE ri.recipe_id = r.recipe_id AND LOWER(i.name) = ANY(:ingredientsList))) "
              + "AND (COALESCE(array_length(:tagsList, 1), 0) = 0 OR "
              + "     EXISTS (SELECT 1 FROM recipe_manager.recipe_tag_junction rtj "
              + "             JOIN recipe_manager.recipe_tags rt ON rt.tag_id = rtj.tag_id "
              + "             WHERE rtj.recipe_id = r.recipe_id AND rt.name = ANY(:tagsList)))")
  Page<Recipe> searchRecipes(
      @Param("recipeNameQuery") String recipeNameQuery,
      @Param("difficulty") String difficulty,
      @Param("maxCookingTime") Integer maxCookingTime,
      @Param("maxPreparationTime") Integer maxPreparationTime,
      @Param("minServings") java.math.BigDecimal minServings,
      @Param("maxServings") java.math.BigDecimal maxServings,
      @Param("ingredientsList") String[] ingredientsList,
      @Param("tagsList") String[] tagsList,
      Pageable pageable);
}
