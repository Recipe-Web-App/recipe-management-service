package com.recipe_manager.repository.recipe;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.recipe_manager.model.entity.recipe.RecipeIngredient;
import com.recipe_manager.model.entity.recipe.RecipeIngredientId;

/**
 * Repository interface for RecipeIngredient entity. Provides data access methods for recipe
 * ingredient operations.
 */
@Repository
public interface RecipeIngredientRepository
    extends JpaRepository<RecipeIngredient, RecipeIngredientId> {

  /**
   * Find all ingredients for a specific recipe using Spring Data JPA method naming convention.
   *
   * @param recipeId the recipe ID
   * @return list of recipe ingredients
   */
  List<RecipeIngredient> findByRecipeRecipeId(Long recipeId);
}
