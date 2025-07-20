package com.recipe_manager.dependency_tests;

import com.intuit.karate.junit5.Karate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;

@Tag("dependency")
class TagKarateRunner {
  @Karate.Test
  @DisplayName("Add Tag Endpoint")
  Karate testAddTag() {
    return Karate.run("feature/tag/add-tag.feature").relativeTo(getClass());
  }

  @Karate.Test
  @DisplayName("Remove Tag Endpoint")
  Karate testRemoveTag() {
    return Karate.run("feature/tag/remove-tag.feature").relativeTo(getClass());
  }

  @Karate.Test
  @DisplayName("Get Tags Endpoint")
  Karate testGetTags() {
    return Karate.run("feature/tag/get-tags.feature").relativeTo(getClass());
  }
}
