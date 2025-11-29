package com.recipe_manager.dependency_tests;

import com.intuit.karate.junit5.Karate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;

@Tag("dependency")
class UserKarateRunner {

  @Karate.Test
  @DisplayName("Get My Recipes Endpoint")
  Karate testGetMyRecipes() {
    return Karate.run("feature/user/get-my-recipes.feature").relativeTo(getClass());
  }
}
