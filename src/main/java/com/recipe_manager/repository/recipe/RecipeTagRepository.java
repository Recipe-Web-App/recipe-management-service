package com.recipe_manager.repository.recipe;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.recipe_manager.model.entity.recipe.RecipeTag;

/**
 * Repository interface for RecipeTag entity. Provides CRUD operations and custom query methods for
 * recipe tags.
 */
@Repository
public interface RecipeTagRepository extends JpaRepository<RecipeTag, Long> {

  /**
   * Find a tag by its name (case insensitive).
   *
   * @param name the tag name to search for
   * @return Optional containing the tag if found, empty otherwise
   */
  Optional<RecipeTag> findByNameIgnoreCase(String name);

  /**
   * Check if a tag exists by name (case insensitive).
   *
   * @param name the tag name to check
   * @return true if tag exists, false otherwise
   */
  boolean existsByNameIgnoreCase(String name);
}
