package com.recipe_manager.unit_tests.model.entity.media;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.recipe_manager.model.entity.media.Media;
import com.recipe_manager.model.entity.media.RecipeMedia;
import com.recipe_manager.model.entity.recipe.Recipe;

/**
 * Unit tests for RecipeMedia entity.
 */
@Tag("unit")
class RecipeMediaTest {

  @Test
  void testBuilder_AllFields_CreatesCorrectEntity() {
    Media media = new Media();
    media.setMediaId(1L);

    Recipe recipe = new Recipe();
    recipe.setRecipeId(2L);

    RecipeMedia recipeMedia = RecipeMedia.builder()
        .mediaId(1L)
        .recipeId(2L)
        .media(media)
        .recipe(recipe)
        .build();

    assertEquals(1L, recipeMedia.getMediaId());
    assertEquals(2L, recipeMedia.getRecipeId());
    assertEquals(media, recipeMedia.getMedia());
    assertEquals(recipe, recipeMedia.getRecipe());
  }

  @Test
  void testBuilder_IdsOnly_CreatesValidEntity() {
    RecipeMedia recipeMedia = RecipeMedia.builder()
        .mediaId(1L)
        .recipeId(2L)
        .build();

    assertEquals(1L, recipeMedia.getMediaId());
    assertEquals(2L, recipeMedia.getRecipeId());
    assertNull(recipeMedia.getMedia());
    assertNull(recipeMedia.getRecipe());
  }

  @Test
  void testNoArgsConstructor_CreatesEmptyEntity() {
    RecipeMedia recipeMedia = new RecipeMedia();

    assertNull(recipeMedia.getMediaId());
    assertNull(recipeMedia.getRecipeId());
    assertNull(recipeMedia.getMedia());
    assertNull(recipeMedia.getRecipe());
  }

  @Test
  void testAllArgsConstructor_AllFields_CreatesCorrectEntity() {
    Media media = new Media();
    media.setMediaId(1L);

    Recipe recipe = new Recipe();
    recipe.setRecipeId(2L);

    RecipeMedia recipeMedia = new RecipeMedia(1L, 2L, media, recipe);

    assertEquals(1L, recipeMedia.getMediaId());
    assertEquals(2L, recipeMedia.getRecipeId());
    assertEquals(media, recipeMedia.getMedia());
    assertEquals(recipe, recipeMedia.getRecipe());
  }

  @Test
  void testGettersAndSetters_AllFields_WorkCorrectly() {
    Media media = new Media();
    media.setMediaId(1L);

    Recipe recipe = new Recipe();
    recipe.setRecipeId(2L);

    RecipeMedia recipeMedia = new RecipeMedia();
    recipeMedia.setMediaId(1L);
    recipeMedia.setRecipeId(2L);
    recipeMedia.setMedia(media);
    recipeMedia.setRecipe(recipe);

    assertEquals(1L, recipeMedia.getMediaId());
    assertEquals(2L, recipeMedia.getRecipeId());
    assertEquals(media, recipeMedia.getMedia());
    assertEquals(recipe, recipeMedia.getRecipe());
  }

  @Test
  void testEquals_SameObjects_ReturnsTrue() {
    RecipeMedia recipeMedia = RecipeMedia.builder()
        .mediaId(1L)
        .recipeId(2L)
        .build();

    assertEquals(recipeMedia, recipeMedia);
  }

  @Test
  void testEquals_IdenticalObjects_ReturnsTrue() {
    Media media = new Media();
    media.setMediaId(1L);

    Recipe recipe = new Recipe();
    recipe.setRecipeId(2L);

    RecipeMedia recipeMedia1 = RecipeMedia.builder()
        .mediaId(1L)
        .recipeId(2L)
        .media(media)
        .recipe(recipe)
        .build();

    RecipeMedia recipeMedia2 = RecipeMedia.builder()
        .mediaId(1L)
        .recipeId(2L)
        .media(media)
        .recipe(recipe)
        .build();

    assertEquals(recipeMedia1, recipeMedia2);
  }

  @Test
  void testEquals_DifferentMediaId_ReturnsFalse() {
    RecipeMedia recipeMedia1 = RecipeMedia.builder()
        .mediaId(1L)
        .recipeId(2L)
        .build();

    RecipeMedia recipeMedia2 = RecipeMedia.builder()
        .mediaId(3L)
        .recipeId(2L)
        .build();

    assertNotEquals(recipeMedia1, recipeMedia2);
  }

  @Test
  void testEquals_DifferentRecipeId_ReturnsFalse() {
    RecipeMedia recipeMedia1 = RecipeMedia.builder()
        .mediaId(1L)
        .recipeId(2L)
        .build();

    RecipeMedia recipeMedia2 = RecipeMedia.builder()
        .mediaId(1L)
        .recipeId(3L)
        .build();

    assertNotEquals(recipeMedia1, recipeMedia2);
  }

  @Test
  void testEquals_NullObject_ReturnsFalse() {
    RecipeMedia recipeMedia = RecipeMedia.builder()
        .mediaId(1L)
        .recipeId(2L)
        .build();

    assertNotEquals(recipeMedia, null);
  }

  @Test
  void testEquals_DifferentClass_ReturnsFalse() {
    RecipeMedia recipeMedia = RecipeMedia.builder()
        .mediaId(1L)
        .recipeId(2L)
        .build();

    assertNotEquals(recipeMedia, "not a recipe media object");
  }

  @Test
  void testHashCode_IdenticalObjects_ReturnsSameHashCode() {
    RecipeMedia recipeMedia1 = RecipeMedia.builder()
        .mediaId(1L)
        .recipeId(2L)
        .build();

    RecipeMedia recipeMedia2 = RecipeMedia.builder()
        .mediaId(1L)
        .recipeId(2L)
        .build();

    assertEquals(recipeMedia1.hashCode(), recipeMedia2.hashCode());
  }

  @Test
  void testHashCode_DifferentObjects_ReturnsDifferentHashCode() {
    RecipeMedia recipeMedia1 = RecipeMedia.builder()
        .mediaId(1L)
        .recipeId(2L)
        .build();

    RecipeMedia recipeMedia2 = RecipeMedia.builder()
        .mediaId(3L)
        .recipeId(2L)
        .build();

    assertNotEquals(recipeMedia1.hashCode(), recipeMedia2.hashCode());
  }

  @Test
  void testToString_ContainsExpectedFields() {
    RecipeMedia recipeMedia = RecipeMedia.builder()
        .mediaId(1L)
        .recipeId(2L)
        .build();

    String toString = recipeMedia.toString();

    assertNotNull(toString);
    // Verify key fields are present in toString
    org.junit.jupiter.api.Assertions.assertTrue(toString.contains("mediaId=1"));
    org.junit.jupiter.api.Assertions.assertTrue(toString.contains("recipeId=2"));
  }

  @Test
  void testCompositeKey_BothIdsRequired() {
    RecipeMedia recipeMedia1 = RecipeMedia.builder()
        .mediaId(1L)
        .recipeId(2L)
        .build();

    RecipeMedia recipeMedia2 = RecipeMedia.builder()
        .mediaId(1L)
        .recipeId(3L)
        .build();

    RecipeMedia recipeMedia3 = RecipeMedia.builder()
        .mediaId(2L)
        .recipeId(2L)
        .build();

    // Different composite keys should not be equal
    assertNotEquals(recipeMedia1, recipeMedia2);
    assertNotEquals(recipeMedia1, recipeMedia3);
    assertNotEquals(recipeMedia2, recipeMedia3);
  }
}
