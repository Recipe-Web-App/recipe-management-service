package com.recipe_manager.repository.ingredient;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
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
}
