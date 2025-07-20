package com.recipe_manager.dependency_tests;

import com.intuit.karate.junit5.Karate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;

@Tag("dependency")
class IngredientKarateRunner {
  @Karate.Test
  @DisplayName("Get Ingredients Endpoint")
  Karate testGetIngredients() {
    return Karate.run("feature/ingredient/get-ingredients.feature").relativeTo(getClass());
  }

  @Karate.Test
  @DisplayName("Scale Ingredients Endpoint")
  Karate testScaleIngredients() {
    return Karate.run("feature/ingredient/scale-ingredients.feature").relativeTo(getClass());
  }

  @Karate.Test
  @DisplayName("Generate Shopping List Endpoint")
  Karate testGenerateShoppingList() {
    return Karate
        .run("feature/ingredient/generate-shopping-list.feature").relativeTo(getClass());
  }

  @Karate.Test
  @DisplayName("Add Ingredient Comment Endpoint")
  Karate testAddIngredientComment() {
    return Karate
        .run("feature/ingredient/add-ingredient-comment.feature").relativeTo(getClass());
  }

  @Karate.Test
  @DisplayName("Edit Ingredient Comment Endpoint")
  Karate testEditIngredientComment() {
    return Karate
        .run("feature/ingredient/edit-ingredient-comment.feature").relativeTo(getClass());
  }

  @Karate.Test
  @DisplayName("Delete Ingredient Comment Endpoint")
  Karate testDeleteIngredientComment() {
    return Karate
        .run("feature/ingredient/delete-ingredient-comment.feature").relativeTo(getClass());
  }

  @Karate.Test
  @DisplayName("Add Ingredient Media Endpoint")
  Karate testAddIngredientMedia() {
    return Karate.run("feature/ingredient/add-ingredient-media.feature").relativeTo(getClass());
  }

  @Karate.Test
  @DisplayName("Update Ingredient Media Endpoint")
  Karate testUpdateIngredientMedia() {
    return Karate
        .run("feature/ingredient/update-ingredient-media.feature").relativeTo(getClass());
  }

  @Karate.Test
  @DisplayName("Delete Ingredient Media Endpoint")
  Karate testDeleteIngredientMedia() {
    return Karate
        .run("feature/ingredient/delete-ingredient-media.feature").relativeTo(getClass());
  }
}
