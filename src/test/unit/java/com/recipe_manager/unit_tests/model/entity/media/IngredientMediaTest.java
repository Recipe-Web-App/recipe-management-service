package com.recipe_manager.unit_tests.model.entity.media;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.recipe_manager.model.entity.ingredient.Ingredient;
import com.recipe_manager.model.entity.media.IngredientMedia;
import com.recipe_manager.model.entity.media.IngredientMediaId;
import com.recipe_manager.model.entity.media.Media;
import com.recipe_manager.model.entity.recipe.Recipe;

/**
 * Unit tests for IngredientMedia entity.
 */
@Tag("unit")
class IngredientMediaTest {

  @Test
  void testBuilder_AllFields_CreatesCorrectEntity() {
    IngredientMediaId id = IngredientMediaId.builder()
        .recipeId(1L)
        .ingredientId(2L)
        .mediaId(3L)
        .build();

    Recipe recipe = new Recipe();
    recipe.setRecipeId(1L);

    Ingredient ingredient = new Ingredient();
    ingredient.setIngredientId(2L);

    Media media = new Media();
    media.setMediaId(3L);

    IngredientMedia ingredientMedia = IngredientMedia.builder()
        .id(id)
        .recipe(recipe)
        .ingredient(ingredient)
        .media(media)
        .build();

    assertEquals(id, ingredientMedia.getId());
    assertEquals(recipe, ingredientMedia.getRecipe());
    assertEquals(ingredient, ingredientMedia.getIngredient());
    assertEquals(media, ingredientMedia.getMedia());
  }

  @Test
  void testBuilder_IdOnly_CreatesValidEntity() {
    IngredientMediaId id = IngredientMediaId.builder()
        .recipeId(1L)
        .ingredientId(2L)
        .mediaId(3L)
        .build();

    IngredientMedia ingredientMedia = IngredientMedia.builder()
        .id(id)
        .build();

    assertEquals(id, ingredientMedia.getId());
    assertNull(ingredientMedia.getRecipe());
    assertNull(ingredientMedia.getIngredient());
    assertNull(ingredientMedia.getMedia());
  }

  @Test
  void testNoArgsConstructor_CreatesEmptyEntity() {
    IngredientMedia ingredientMedia = new IngredientMedia();

    assertNull(ingredientMedia.getId());
    assertNull(ingredientMedia.getRecipe());
    assertNull(ingredientMedia.getIngredient());
    assertNull(ingredientMedia.getMedia());
  }

  @Test
  void testAllArgsConstructor_AllFields_CreatesCorrectEntity() {
    IngredientMediaId id = IngredientMediaId.builder()
        .recipeId(1L)
        .ingredientId(2L)
        .mediaId(3L)
        .build();

    Recipe recipe = new Recipe();
    recipe.setRecipeId(1L);

    Ingredient ingredient = new Ingredient();
    ingredient.setIngredientId(2L);

    Media media = new Media();
    media.setMediaId(3L);

    IngredientMedia ingredientMedia = new IngredientMedia(id, recipe, ingredient, media);

    assertEquals(id, ingredientMedia.getId());
    assertEquals(recipe, ingredientMedia.getRecipe());
    assertEquals(ingredient, ingredientMedia.getIngredient());
    assertEquals(media, ingredientMedia.getMedia());
  }

  @Test
  void testGettersAndSetters_AllFields_WorkCorrectly() {
    IngredientMediaId id = IngredientMediaId.builder()
        .recipeId(1L)
        .ingredientId(2L)
        .mediaId(3L)
        .build();

    Recipe recipe = new Recipe();
    recipe.setRecipeId(1L);

    Ingredient ingredient = new Ingredient();
    ingredient.setIngredientId(2L);

    Media media = new Media();
    media.setMediaId(3L);

    IngredientMedia ingredientMedia = new IngredientMedia();
    ingredientMedia.setId(id);
    ingredientMedia.setRecipe(recipe);
    ingredientMedia.setIngredient(ingredient);
    ingredientMedia.setMedia(media);

    assertEquals(id, ingredientMedia.getId());
    assertEquals(recipe, ingredientMedia.getRecipe());
    assertEquals(ingredient, ingredientMedia.getIngredient());
    assertEquals(media, ingredientMedia.getMedia());
  }

  @Test
  void testEquals_SameObjects_ReturnsTrue() {
    IngredientMediaId id = IngredientMediaId.builder()
        .recipeId(1L)
        .ingredientId(2L)
        .mediaId(3L)
        .build();

    IngredientMedia ingredientMedia = IngredientMedia.builder()
        .id(id)
        .build();

    assertEquals(ingredientMedia, ingredientMedia);
  }

  @Test
  void testEquals_IdenticalObjects_ReturnsTrue() {
    IngredientMediaId id = IngredientMediaId.builder()
        .recipeId(1L)
        .ingredientId(2L)
        .mediaId(3L)
        .build();

    Recipe recipe = new Recipe();
    recipe.setRecipeId(1L);

    Ingredient ingredient = new Ingredient();
    ingredient.setIngredientId(2L);

    Media media = new Media();
    media.setMediaId(3L);

    IngredientMedia ingredientMedia1 = IngredientMedia.builder()
        .id(id)
        .recipe(recipe)
        .ingredient(ingredient)
        .media(media)
        .build();

    IngredientMedia ingredientMedia2 = IngredientMedia.builder()
        .id(id)
        .recipe(recipe)
        .ingredient(ingredient)
        .media(media)
        .build();

    assertEquals(ingredientMedia1, ingredientMedia2);
  }

  @Test
  void testEquals_DifferentId_ReturnsFalse() {
    IngredientMediaId id1 = IngredientMediaId.builder()
        .recipeId(1L)
        .ingredientId(2L)
        .mediaId(3L)
        .build();

    IngredientMediaId id2 = IngredientMediaId.builder()
        .recipeId(4L)
        .ingredientId(2L)
        .mediaId(3L)
        .build();

    IngredientMedia ingredientMedia1 = IngredientMedia.builder()
        .id(id1)
        .build();

    IngredientMedia ingredientMedia2 = IngredientMedia.builder()
        .id(id2)
        .build();

    assertNotEquals(ingredientMedia1, ingredientMedia2);
  }

  @Test
  void testEquals_NullObject_ReturnsFalse() {
    IngredientMediaId id = IngredientMediaId.builder()
        .recipeId(1L)
        .ingredientId(2L)
        .mediaId(3L)
        .build();

    IngredientMedia ingredientMedia = IngredientMedia.builder()
        .id(id)
        .build();

    assertNotEquals(ingredientMedia, null);
  }

  @Test
  void testEquals_DifferentClass_ReturnsFalse() {
    IngredientMediaId id = IngredientMediaId.builder()
        .recipeId(1L)
        .ingredientId(2L)
        .mediaId(3L)
        .build();

    IngredientMedia ingredientMedia = IngredientMedia.builder()
        .id(id)
        .build();

    assertNotEquals(ingredientMedia, "not an ingredient media object");
  }

  @Test
  void testHashCode_IdenticalObjects_ReturnsSameHashCode() {
    IngredientMediaId id = IngredientMediaId.builder()
        .recipeId(1L)
        .ingredientId(2L)
        .mediaId(3L)
        .build();

    IngredientMedia ingredientMedia1 = IngredientMedia.builder()
        .id(id)
        .build();

    IngredientMedia ingredientMedia2 = IngredientMedia.builder()
        .id(id)
        .build();

    assertEquals(ingredientMedia1.hashCode(), ingredientMedia2.hashCode());
  }

  @Test
  void testHashCode_DifferentObjects_ReturnsDifferentHashCode() {
    IngredientMediaId id1 = IngredientMediaId.builder()
        .recipeId(1L)
        .ingredientId(2L)
        .mediaId(3L)
        .build();

    IngredientMediaId id2 = IngredientMediaId.builder()
        .recipeId(4L)
        .ingredientId(2L)
        .mediaId(3L)
        .build();

    IngredientMedia ingredientMedia1 = IngredientMedia.builder()
        .id(id1)
        .build();

    IngredientMedia ingredientMedia2 = IngredientMedia.builder()
        .id(id2)
        .build();

    assertNotEquals(ingredientMedia1.hashCode(), ingredientMedia2.hashCode());
  }

  @Test
  void testToString_ContainsExpectedFields() {
    IngredientMediaId id = IngredientMediaId.builder()
        .recipeId(1L)
        .ingredientId(2L)
        .mediaId(3L)
        .build();

    IngredientMedia ingredientMedia = IngredientMedia.builder()
        .id(id)
        .build();

    String toString = ingredientMedia.toString();

    assertNotNull(toString);
    // Verify key fields are present in toString
    org.junit.jupiter.api.Assertions.assertTrue(toString.contains("id="));
  }

  @Test
  void testEmbeddedId_RelationshipMapping() {
    IngredientMediaId id = IngredientMediaId.builder()
        .recipeId(1L)
        .ingredientId(2L)
        .mediaId(3L)
        .build();

    Recipe recipe = new Recipe();
    recipe.setRecipeId(1L);

    Ingredient ingredient = new Ingredient();
    ingredient.setIngredientId(2L);

    Media media = new Media();
    media.setMediaId(3L);

    IngredientMedia ingredientMedia = IngredientMedia.builder()
        .id(id)
        .recipe(recipe)
        .ingredient(ingredient)
        .media(media)
        .build();

    // Verify that the embedded ID matches the entity relationships
    assertEquals(recipe.getRecipeId(), ingredientMedia.getId().getRecipeId());
    assertEquals(ingredient.getIngredientId(), ingredientMedia.getId().getIngredientId());
    assertEquals(media.getMediaId(), ingredientMedia.getId().getMediaId());
  }
}
