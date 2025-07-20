package com.recipe_manager.dependency_tests;

import com.intuit.karate.junit5.Karate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;

@Tag("dependency")
class RecipeKarateRunner {
  @Karate.Test
  @DisplayName("Get Recipe Endpoint")
  Karate testGetRecipe() {
    return Karate.run("feature/recipe/get-recipe.feature").relativeTo(getClass());
  }

  @Karate.Test
  @DisplayName("Create Recipe Endpoint")
  Karate testCreateRecipe() {
    return Karate.run("feature/recipe/create-recipe.feature").relativeTo(getClass());
  }

  @Karate.Test
  @DisplayName("Update Recipe Endpoint")
  Karate testUpdateRecipe() {
    return Karate.run("feature/recipe/update-recipe.feature").relativeTo(getClass());
  }

  @Karate.Test
  @DisplayName("Delete Recipe Endpoint")
  Karate testDeleteRecipe() {
    return Karate.run("feature/recipe/delete-recipe.feature").relativeTo(getClass());
  }

  @Karate.Test
  @DisplayName("Search Recipes Endpoint")
  Karate testSearchRecipes() {
    return Karate.run("feature/recipe/search-recipes.feature").relativeTo(getClass());
  }
}
