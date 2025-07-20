package com.recipe_manager.dependency_tests;

import com.intuit.karate.junit5.Karate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;

@Tag("dependency")
class MediaKarateRunner {
  @Karate.Test
  @DisplayName("Add Media to Recipe Endpoint")
  Karate testAddMediaToRecipe() {
    return Karate.run("feature/media/add-media.feature").relativeTo(getClass());
  }

  @Karate.Test
  @DisplayName("Update Media on Recipe Endpoint")
  Karate testUpdateMediaOnRecipe() {
    return Karate.run("feature/media/update-media.feature").relativeTo(getClass());
  }

  @Karate.Test
  @DisplayName("Delete Media from Recipe Endpoint")
  Karate testDeleteMediaFromRecipe() {
    return Karate.run("feature/media/delete-media.feature").relativeTo(getClass());
  }
}
