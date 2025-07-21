package com.recipe_manager.model.entity.recipe;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for RecipeFavorite entity.
 */
@Tag("unit")
class RecipeFavoriteTest {

  private Recipe recipe;
  private UUID userId;
  private RecipeFavorite recipeFavorite;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    recipe = Recipe.builder()
        .userId(UUID.randomUUID())
        .title("Test Recipe")
        .build();

    recipeFavorite = RecipeFavorite.builder()
        .recipe(recipe)
        .userId(userId)
        .build();
  }

  @Test
  @DisplayName("Should create recipe favorite with builder")
  @Tag("standard-processing")
  void shouldCreateRecipeFavoriteWithBuilder() {
    // Then
    assertThat(recipeFavorite.getRecipe()).isEqualTo(recipe);
    assertThat(recipeFavorite.getUserId()).isEqualTo(userId);
  }

  @Test
  @DisplayName("Should set and get all properties")
  @Tag("standard-processing")
  void shouldSetAndGetAllProperties() {
    // Given
    RecipeFavoriteId id = RecipeFavoriteId.builder()
        .userId(userId)
        .recipeId(1L)
        .build();
    LocalDateTime favoritedAt = LocalDateTime.now();

    // When
    recipeFavorite.setId(id);
    recipeFavorite.setFavoritedAt(favoritedAt);

    // Then
    assertThat(recipeFavorite.getId()).isEqualTo(id);
    assertThat(recipeFavorite.getFavoritedAt()).isEqualTo(favoritedAt);
  }

  @Test
  @DisplayName("Should have correct toString representation")
  @Tag("standard-processing")
  void shouldHaveCorrectToStringRepresentation() {
    // When
    String toString = recipeFavorite.toString();

    // Then
    assertThat(toString).contains("RecipeFavorite");
  }

  @Test
  @DisplayName("Should handle multiple favorites for same recipe")
  @Tag("standard-processing")
  void shouldHandleMultipleFavoritesForSameRecipe() {
    // Given
    UUID user1 = UUID.randomUUID();
    UUID user2 = UUID.randomUUID();
    UUID user3 = UUID.randomUUID();

    RecipeFavorite favorite1 = RecipeFavorite.builder()
        .recipe(recipe)
        .userId(user1)
        .build();

    RecipeFavorite favorite2 = RecipeFavorite.builder()
        .recipe(recipe)
        .userId(user2)
        .build();

    RecipeFavorite favorite3 = RecipeFavorite.builder()
        .recipe(recipe)
        .userId(user3)
        .build();

    // Then
    assertThat(favorite1.getUserId()).isEqualTo(user1);
    assertThat(favorite2.getUserId()).isEqualTo(user2);
    assertThat(favorite3.getUserId()).isEqualTo(user3);
    assertThat(favorite1.getRecipe()).isEqualTo(recipe);
    assertThat(favorite2.getRecipe()).isEqualTo(recipe);
    assertThat(favorite3.getRecipe()).isEqualTo(recipe);
  }

  @Test
  @DisplayName("Should handle multiple favorites for same user")
  @Tag("standard-processing")
  void shouldHandleMultipleFavoritesForSameUser() {
    // Given
    Recipe recipe1 = Recipe.builder()
        .title("Recipe 1")
        .build();
    Recipe recipe2 = Recipe.builder()
        .title("Recipe 2")
        .build();

    RecipeFavorite favorite1 = RecipeFavorite.builder()
        .recipe(recipe1)
        .userId(userId)
        .build();

    RecipeFavorite favorite2 = RecipeFavorite.builder()
        .recipe(recipe2)
        .userId(userId)
        .build();

    // Then
    assertThat(favorite1.getUserId()).isEqualTo(userId);
    assertThat(favorite2.getUserId()).isEqualTo(userId);
    assertThat(favorite1.getRecipe()).isEqualTo(recipe1);
    assertThat(favorite2.getRecipe()).isEqualTo(recipe2);
  }

  @Test
  @DisplayName("Should handle favorite with null favoritedAt")
  @Tag("standard-processing")
  void shouldHandleFavoriteWithNullFavoritedAt() {
    // Given
    RecipeFavorite nullDateFavorite = RecipeFavorite.builder()
        .recipe(recipe)
        .userId(userId)
        .build();

    // Then
    assertThat(nullDateFavorite.getFavoritedAt()).isNull();
  }

  @Test
  @DisplayName("Should handle favorite with specific favoritedAt")
  @Tag("standard-processing")
  void shouldHandleFavoriteWithSpecificFavoritedAt() {
    // Given
    LocalDateTime specificTime = LocalDateTime.of(2024, 1, 15, 14, 30, 0);
    RecipeFavorite specificFavorite = RecipeFavorite.builder()
        .recipe(recipe)
        .userId(userId)
        .favoritedAt(specificTime)
        .build();

    // Then
    assertThat(specificFavorite.getFavoritedAt()).isEqualTo(specificTime);
  }

  @Test
  @DisplayName("Builder should defensively copy id and recipe")
  @Tag("standard-processing")
  void builderShouldDefensivelyCopyIdAndRecipe() {
    RecipeFavoriteId id = new RecipeFavoriteId(UUID.randomUUID(), 123L);
    Recipe recipe = Recipe.builder().userId(UUID.randomUUID()).title("R").build();
    RecipeFavorite fav = RecipeFavorite.builder().id(id).recipe(recipe).userId(userId).build();
    id.setRecipeId(999L);
    recipe.setTitle("Changed");
    assertThat(fav.getId().getRecipeId()).isNotEqualTo(999L);
    assertThat(fav.getRecipe().getTitle()).isNotEqualTo("Changed");
  }

  @Test
  @DisplayName("Getters and setters should defensively copy id and recipe")
  @Tag("standard-processing")
  void gettersAndSettersShouldDefensivelyCopyIdAndRecipe() {
    RecipeFavoriteId id = new RecipeFavoriteId(UUID.randomUUID(), 456L);
    Recipe recipe = Recipe.builder().userId(UUID.randomUUID()).title("R2").build();
    recipeFavorite.setId(id);
    recipeFavorite.setRecipe(recipe);
    id.setRecipeId(888L);
    recipe.setTitle("Changed2");
    assertThat(recipeFavorite.getId().getRecipeId()).isNotEqualTo(888L);
    assertThat(recipeFavorite.getRecipe().getTitle()).isNotEqualTo("Changed2");
  }

  @Test
  @DisplayName("All-args constructor should defensively copy id and recipe")
  @Tag("standard-processing")
  void allArgsConstructorShouldDefensivelyCopyIdAndRecipe() {
    RecipeFavoriteId id = new RecipeFavoriteId(UUID.randomUUID(), 789L);
    Recipe recipe = Recipe.builder().userId(UUID.randomUUID()).title("R3").build();
    RecipeFavorite fav = new RecipeFavorite(id, recipe, userId, LocalDateTime.now());
    id.setRecipeId(777L);
    recipe.setTitle("Changed3");
    assertThat(fav.getId().getRecipeId()).isNotEqualTo(777L);
    assertThat(fav.getRecipe().getTitle()).isNotEqualTo("Changed3");
  }

  @Test
  @DisplayName("Should handle nulls for all fields")
  @Tag("error-processing")
  void shouldHandleNullsForAllFields() {
    RecipeFavorite fav = new RecipeFavorite(null, null, null, null);
    assertThat(fav.getId()).isNull();
    assertThat(fav.getRecipe()).isNull();
    assertThat(fav.getUserId()).isNull();
    assertThat(fav.getFavoritedAt()).isNull();
  }

  @Test
  @DisplayName("Equals and hashCode: self, null, different type, different values, all-null fields")
  @Tag("standard-processing")
  void equalsAndHashCodeEdgeCases() {
    RecipeFavorite fav1 = RecipeFavorite.builder().userId(userId).build();
    RecipeFavorite fav2 = RecipeFavorite.builder().userId(userId).build();
    RecipeFavorite fav3 = RecipeFavorite.builder().userId(UUID.randomUUID()).build();
    assertThat(fav1).isEqualTo(fav1);
    assertThat(fav1).isEqualTo(fav2);
    assertThat(fav1.hashCode()).isEqualTo(fav2.hashCode());
    assertThat(fav1).isNotEqualTo(fav3);
    assertThat(fav1.hashCode()).isNotEqualTo(fav3.hashCode());
    assertThat(fav1).isNotEqualTo(null);
    assertThat(fav1).isNotEqualTo(new Object());
    RecipeFavorite favNulls1 = new RecipeFavorite(null, null, null, null);
    RecipeFavorite favNulls2 = new RecipeFavorite(null, null, null, null);
    assertThat(favNulls1).isEqualTo(favNulls2);
    assertThat(favNulls1.hashCode()).isEqualTo(favNulls2.hashCode());
  }
}
