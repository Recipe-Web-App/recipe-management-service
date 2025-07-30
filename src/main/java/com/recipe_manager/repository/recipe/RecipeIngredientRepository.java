package com.recipe_manager.repository.recipe;

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

  // All methods removed - not currently used in main application code
  // Only inherits basic CRUD operations from JpaRepository
}
