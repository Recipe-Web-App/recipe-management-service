package com.recipe_manager.dependency_tests;

import com.intuit.karate.junit5.Karate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;

/**
 * Karate test runner for favorite collection endpoints.
 *
 * <p>Runs integration tests for:
 *
 * <ul>
 *   <li>POST /favorites/collections/{collectionId} - Favorite a collection
 *   <li>DELETE /favorites/collections/{collectionId} - Unfavorite a collection
 *   <li>GET /favorites/collections/{collectionId}/is-favorited - Check if collection is favorited
 *   <li>GET /favorites/collections - Get user's favorite collections
 * </ul>
 */
@Tag("dependency")
class FavoriteKarateRunner {

  @Karate.Test
  @DisplayName("Favorite Collection Endpoint")
  Karate testFavoriteCollection() {
    return Karate.run("feature/favorite/favorite-collection.feature").relativeTo(getClass());
  }

  @Karate.Test
  @DisplayName("Unfavorite Collection Endpoint")
  Karate testUnfavoriteCollection() {
    return Karate.run("feature/favorite/unfavorite-collection.feature").relativeTo(getClass());
  }

  @Karate.Test
  @DisplayName("Is Collection Favorited Endpoint")
  Karate testIsCollectionFavorited() {
    return Karate.run("feature/favorite/is-collection-favorited.feature").relativeTo(getClass());
  }

  @Karate.Test
  @DisplayName("Get Favorite Collections Endpoint")
  Karate testGetFavoriteCollections() {
    return Karate.run("feature/favorite/get-favorite-collections.feature").relativeTo(getClass());
  }
}
