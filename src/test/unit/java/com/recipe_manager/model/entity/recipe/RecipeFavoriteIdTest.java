package com.recipe_manager.model.entity.recipe;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for RecipeFavoriteId entity.
 */
@Tag("unit")
class RecipeFavoriteIdTest {

  @Test
  @DisplayName("Should create RecipeFavoriteId with all-args constructor")
  @Tag("standard-processing")
  void shouldCreateWithAllArgsConstructor() {
    UUID userId = UUID.randomUUID();
    Long recipeId = 42L;
    RecipeFavoriteId id = new RecipeFavoriteId(userId, recipeId);
    assertThat(id.getUserId()).isEqualTo(userId);
    assertThat(id.getRecipeId()).isEqualTo(recipeId);
  }

  @Test
  @DisplayName("Should create RecipeFavoriteId with copy constructor")
  @Tag("standard-processing")
  void shouldCreateWithCopyConstructor() {
    UUID userId = UUID.randomUUID();
    Long recipeId = 99L;
    RecipeFavoriteId original = new RecipeFavoriteId(userId, recipeId);
    RecipeFavoriteId copy = new RecipeFavoriteId(original);
    assertThat(copy).isEqualTo(original);
    assertThat(copy.getUserId()).isEqualTo(userId);
    assertThat(copy.getRecipeId()).isEqualTo(recipeId);
  }

  @Test
  @DisplayName("Should handle null in copy constructor")
  @Tag("error-processing")
  void shouldHandleNullInCopyConstructor() {
    RecipeFavoriteId copy = new RecipeFavoriteId((RecipeFavoriteId) null);
    assertThat(copy.getUserId()).isNull();
    assertThat(copy.getRecipeId()).isNull();
  }

  @Test
  @DisplayName("Equals and hashCode: self, null, different type, different values, all-null fields")
  @Tag("standard-processing")
  void equalsAndHashCodeEdgeCases() {
    UUID userId = UUID.randomUUID();
    Long recipeId = 1L;
    RecipeFavoriteId id1 = new RecipeFavoriteId(userId, recipeId);
    RecipeFavoriteId id2 = new RecipeFavoriteId(userId, recipeId);
    RecipeFavoriteId id3 = new RecipeFavoriteId(UUID.randomUUID(), 2L);
    assertThat(id1).isEqualTo(id1);
    assertThat(id1).isEqualTo(id2);
    assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
    assertThat(id1).isNotEqualTo(id3);
    assertThat(id1.hashCode()).isNotEqualTo(id3.hashCode());
    assertThat(id1).isNotEqualTo(null);
    assertThat(id1).isNotEqualTo(new Object());
    RecipeFavoriteId idNulls1 = new RecipeFavoriteId(null, null);
    RecipeFavoriteId idNulls2 = new RecipeFavoriteId(null, null);
    assertThat(idNulls1).isEqualTo(idNulls2);
    assertThat(idNulls1.hashCode()).isEqualTo(idNulls2.hashCode());
  }
}
