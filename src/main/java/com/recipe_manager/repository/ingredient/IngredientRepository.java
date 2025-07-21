package com.recipe_manager.repository.ingredient;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.recipe_manager.model.entity.ingredient.Ingredient;

/**
 * Repository interface for Ingredient entity. Provides data access methods for ingredient
 * operations.
 */
@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

  /**
   * Find an ingredient by name (case-insensitive).
   *
   * @param name the ingredient name
   * @return optional containing the ingredient if found
   */
  Optional<Ingredient> findByNameIgnoreCase(String name);

  /**
   * Find ingredients by name containing the given text (case-insensitive).
   *
   * @param name the ingredient name text to search for
   * @param pageable pagination information
   * @return page of ingredients matching the name
   */
  Page<Ingredient> findByNameContainingIgnoreCase(String name, Pageable pageable);

  /**
   * Find ingredients by category.
   *
   * @param category the ingredient category
   * @param pageable pagination information
   * @return page of ingredients in the category
   */
  Page<Ingredient> findByCategory(String category, Pageable pageable);

  /**
   * Find ingredients by category containing the given text (case-insensitive).
   *
   * @param category the ingredient category text to search for
   * @param pageable pagination information
   * @return page of ingredients matching the category
   */
  Page<Ingredient> findByCategoryContainingIgnoreCase(String category, Pageable pageable);

  /**
   * Check if an ingredient exists by name (case-insensitive).
   *
   * @param name the ingredient name
   * @return true if the ingredient exists, false otherwise
   */
  boolean existsByNameIgnoreCase(String name);

  /**
   * Find all ingredient categories.
   *
   * @return list of all ingredient categories
   */
  @Query(
      "SELECT DISTINCT i.category FROM Ingredient i WHERE i.category IS NOT NULL ORDER BY i.category")
  List<String> findAllCategories();

  /**
   * Find ingredients used in recipes by user ID.
   *
   * @param userId the user ID
   * @return list of ingredients used in recipes by the user
   */
  @Query(
      "SELECT DISTINCT i FROM Ingredient i "
          + "JOIN i.recipeIngredients ri "
          + "JOIN ri.recipe r "
          + "WHERE r.userId = :userId "
          + "ORDER BY i.name")
  List<Ingredient> findIngredientsByUserId(@Param("userId") java.util.UUID userId);

  /**
   * Find ingredient names used in recipes by user ID.
   *
   * @param userId the user ID
   * @return list of ingredient names used in recipes by the user
   */
  @Query(
      "SELECT DISTINCT i.name FROM Ingredient i "
          + "JOIN i.recipeIngredients ri "
          + "JOIN ri.recipe r "
          + "WHERE r.userId = :userId "
          + "ORDER BY i.name")
  List<String> findIngredientNamesByUserId(@Param("userId") java.util.UUID userId);

  /**
   * Count ingredients by category.
   *
   * @param category the ingredient category
   * @return the number of ingredients in the category
   */
  long countByCategory(String category);
}
