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

  @Karate.Test
  @DisplayName("Get Collection By ID Endpoint")
  Karate testGetCollectionById() {
    return Karate.run("feature/collection/get-collection-by-id.feature").relativeTo(getClass());
  }

  @Karate.Test
  @DisplayName("Update Collection Endpoint")
  Karate testUpdateCollection() {
    return Karate.run("feature/collection/update-collection.feature").relativeTo(getClass());
  }
}
