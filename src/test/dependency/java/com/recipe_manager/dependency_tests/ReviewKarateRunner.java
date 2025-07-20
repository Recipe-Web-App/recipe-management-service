package com.recipe_manager.dependency_tests;

import com.intuit.karate.junit5.Karate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;

@Tag("dependency")
class ReviewKarateRunner {
  @Karate.Test
  @DisplayName("Get Recipe Reviews Endpoint")
  Karate testGetRecipeReviews() {
    return Karate.run("feature/review/get-review.feature").relativeTo(getClass());
  }

  @Karate.Test
  @DisplayName("Add Recipe Review Endpoint")
  Karate testAddRecipeReview() {
    return Karate.run("feature/review/add-review.feature").relativeTo(getClass());
  }

  @Karate.Test
  @DisplayName("Edit Recipe Review Endpoint")
  Karate testEditRecipeReview() {
    return Karate.run("feature/review/edit-review.feature").relativeTo(getClass());
  }

  @Karate.Test
  @DisplayName("Delete Recipe Review Endpoint")
  Karate testDeleteRecipeReview() {
    return Karate.run("feature/review/delete-review.feature").relativeTo(getClass());
  }
}
