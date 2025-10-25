package com.recipe_manager.dependency_tests;

import com.intuit.karate.junit5.Karate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;

@Tag("dependency")
class CollectionKarateRunner {
  @Karate.Test
  @DisplayName("Get Collections Endpoint")
  Karate testGetCollections() {
    return Karate.run("feature/collection/get-collections.feature").relativeTo(getClass());
  }
}
