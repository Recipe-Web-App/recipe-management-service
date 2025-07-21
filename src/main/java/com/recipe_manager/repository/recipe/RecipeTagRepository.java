package com.recipe_manager.repository.recipe;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.recipe_manager.model.entity.recipe.RecipeTag;

/**
 * Repository interface for RecipeTag entity. Provides data access methods for recipe tag
 * operations.
 */
@Repository
public interface RecipeTagRepository extends JpaRepository<RecipeTag, Long> {

  /**
   * Find a tag by name (case-insensitive).
   *
   * @param name the tag name
   * @return optional containing the tag if found
   */
  Optional<RecipeTag> findByNameIgnoreCase(String name);

  /**
   * Find tags by name containing the given text (case-insensitive).
   *
   * @param name the tag name text to search for
   * @return list of tags matching the name
   */
  List<RecipeTag> findByNameContainingIgnoreCase(String name);

  /**
   * Check if a tag exists by name (case-insensitive).
   *
   * @param name the tag name
   * @return true if the tag exists, false otherwise
   */
  boolean existsByNameIgnoreCase(String name);

  /**
   * Find all tag names.
   *
   * @return list of all tag names
   */
  @Query("SELECT rt.name FROM RecipeTag rt ORDER BY rt.name")
  List<String> findAllTagNames();

  /**
   * Find tags by recipe ID.
   *
   * @param recipeId the recipe ID
   * @return list of tags for the recipe
   */
  @Query(
      "SELECT rt FROM RecipeTag rt "
          + "JOIN rt.recipes r "
          + "WHERE r.recipeId = :recipeId "
          + "ORDER BY rt.name")
  List<RecipeTag> findByRecipeId(@Param("recipeId") Long recipeId);

  /**
   * Find tag names by recipe ID.
   *
   * @param recipeId the recipe ID
   * @return list of tag names for the recipe
   */
  @Query(
      "SELECT rt.name FROM RecipeTag rt "
          + "JOIN rt.recipes r "
          + "WHERE r.recipeId = :recipeId "
          + "ORDER BY rt.name")
  List<String> findTagNamesByRecipeId(@Param("recipeId") Long recipeId);

  /**
   * Find recipes by tag name.
   *
   * @param tagName the tag name
   * @return list of recipes with the specified tag
   */
  @Query("SELECT r FROM Recipe r " + "JOIN r.recipeTags rt " + "WHERE rt.name = :tagName")
  List<com.recipe_manager.model.entity.recipe.Recipe> findRecipesByTagName(
      @Param("tagName") String tagName);
}
