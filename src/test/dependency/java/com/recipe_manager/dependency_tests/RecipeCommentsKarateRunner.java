package com.recipe_manager.dependency_tests;

import com.intuit.karate.junit5.Karate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;

/** Karate runner for recipe comments API tests. */
@Tag("dependency")
class RecipeCommentsKarateRunner {

  @Karate.Test
  @DisplayName("Get Recipe Comments")
  Karate getRecipeComments() {
    return Karate.run("feature/recipe/get-recipe-comments.feature").relativeTo(getClass());
  }

  @Karate.Test
  @DisplayName("Add Recipe Comment")
  Karate addRecipeComment() {
    return Karate.run("feature/recipe/add-recipe-comment.feature").relativeTo(getClass());
  }

  @Karate.Test
  @DisplayName("Edit Recipe Comment")
  Karate editRecipeComment() {
    return Karate.run("feature/recipe/edit-recipe-comment.feature").relativeTo(getClass());
  }

  @Karate.Test
  @DisplayName("Delete Recipe Comment")
  Karate deleteRecipeComment() {
    return Karate.run("feature/recipe/delete-recipe-comment.feature").relativeTo(getClass());
  }
}
