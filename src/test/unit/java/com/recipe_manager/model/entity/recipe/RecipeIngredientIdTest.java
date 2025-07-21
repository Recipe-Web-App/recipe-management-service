package com.recipe_manager.model.entity.recipe;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for RecipeIngredientId entity.
 */
@Tag("unit")
class RecipeIngredientIdTest {

  @Test
  @DisplayName("Should create RecipeIngredientId with all-args constructor")
  @Tag("standard-processing")
  void shouldCreateWithAllArgsConstructor() {
    Long recipeId = 42L;
    Long ingredientId = 99L;
    RecipeIngredientId id = new RecipeIngredientId(recipeId, ingredientId);
    assertThat(id.getRecipeId()).isEqualTo(recipeId);
    assertThat(id.getIngredientId()).isEqualTo(ingredientId);
  }

  @Test
  @DisplayName("Should create RecipeIngredientId with copy constructor")
  @Tag("standard-processing")
  void shouldCreateWithCopyConstructor() {
    Long recipeId = 1L;
    Long ingredientId = 2L;
    RecipeIngredientId original = new RecipeIngredientId(recipeId, ingredientId);
    RecipeIngredientId copy = new RecipeIngredientId(original);
    assertThat(copy).isEqualTo(original);
    assertThat(copy.getRecipeId()).isEqualTo(recipeId);
    assertThat(copy.getIngredientId()).isEqualTo(ingredientId);
  }

  @Test
  @DisplayName("Should handle null in copy constructor")
  @Tag("error-processing")
  void shouldHandleNullInCopyConstructor() {
    RecipeIngredientId copy = new RecipeIngredientId((RecipeIngredientId) null);
    assertThat(copy.getRecipeId()).isNull();
    assertThat(copy.getIngredientId()).isNull();
  }

  @Test
  @DisplayName("Equals and hashCode: self, null, different type, different values, all-null fields")
  @Tag("standard-processing")
  void equalsAndHashCodeEdgeCases() {
    Long recipeId = 1L;
    Long ingredientId = 2L;
    RecipeIngredientId id1 = new RecipeIngredientId(recipeId, ingredientId);
    RecipeIngredientId id2 = new RecipeIngredientId(recipeId, ingredientId);
    RecipeIngredientId id3 = new RecipeIngredientId(3L, 4L);
    assertThat(id1).isEqualTo(id1);
    assertThat(id1).isEqualTo(id2);
    assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
    assertThat(id1).isNotEqualTo(id3);
    assertThat(id1.hashCode()).isNotEqualTo(id3.hashCode());
    assertThat(id1).isNotEqualTo(null);
    assertThat(id1).isNotEqualTo(new Object());
    RecipeIngredientId idNulls1 = new RecipeIngredientId(null, null);
    RecipeIngredientId idNulls2 = new RecipeIngredientId(null, null);
    assertThat(idNulls1).isEqualTo(idNulls2);
    assertThat(idNulls1.hashCode()).isEqualTo(idNulls2.hashCode());
  }
}
