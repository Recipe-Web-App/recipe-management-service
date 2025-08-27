package com.recipe_manager.model.entity.media;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.entity.recipe.RecipeStep;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for StepMedia entity.
 */
@Tag("unit")
class StepMediaTest {

  @Test
  void testBuilder_AllFields_CreatesCorrectEntity() {
    StepMediaId id = StepMediaId.builder()
        .stepId(1L)
        .mediaId(2L)
        .build();

    Recipe recipe = new Recipe();
    recipe.setRecipeId(1L);

    RecipeStep step = new RecipeStep();
    step.setStepId(1L);

    Media media = new Media();
    media.setMediaId(2L);

    StepMedia stepMedia = StepMedia.builder()
        .id(id)
        .recipe(recipe)
        .step(step)
        .media(media)
        .build();

    assertEquals(id, stepMedia.getId());
    assertEquals(recipe, stepMedia.getRecipe());
    assertEquals(step, stepMedia.getStep());
    assertEquals(media, stepMedia.getMedia());
  }

  @Test
  void testBuilder_IdOnly_CreatesValidEntity() {
    StepMediaId id = StepMediaId.builder()
        .stepId(1L)
        .mediaId(2L)
        .build();

    StepMedia stepMedia = StepMedia.builder()
        .id(id)
        .build();

    assertEquals(id, stepMedia.getId());
    assertNull(stepMedia.getRecipe());
    assertNull(stepMedia.getStep());
    assertNull(stepMedia.getMedia());
  }

  @Test
  void testNoArgsConstructor_CreatesEmptyEntity() {
    StepMedia stepMedia = new StepMedia();

    assertNull(stepMedia.getId());
    assertNull(stepMedia.getRecipe());
    assertNull(stepMedia.getStep());
    assertNull(stepMedia.getMedia());
  }

  @Test
  void testAllArgsConstructor_AllFields_CreatesCorrectEntity() {
    StepMediaId id = StepMediaId.builder()
        .stepId(1L)
        .mediaId(2L)
        .build();

    Recipe recipe = new Recipe();
    recipe.setRecipeId(1L);

    RecipeStep step = new RecipeStep();
    step.setStepId(1L);

    Media media = new Media();
    media.setMediaId(2L);

    StepMedia stepMedia = new StepMedia(id, recipe, step, media);

    assertEquals(id, stepMedia.getId());
    assertEquals(recipe, stepMedia.getRecipe());
    assertEquals(step, stepMedia.getStep());
    assertEquals(media, stepMedia.getMedia());
  }

  @Test
  void testGettersAndSetters_AllFields_WorkCorrectly() {
    StepMediaId id = StepMediaId.builder()
        .stepId(1L)
        .mediaId(2L)
        .build();

    Recipe recipe = new Recipe();
    recipe.setRecipeId(1L);

    RecipeStep step = new RecipeStep();
    step.setStepId(1L);

    Media media = new Media();
    media.setMediaId(2L);

    StepMedia stepMedia = new StepMedia();
    stepMedia.setId(id);
    stepMedia.setRecipe(recipe);
    stepMedia.setStep(step);
    stepMedia.setMedia(media);

    assertEquals(id, stepMedia.getId());
    assertEquals(recipe, stepMedia.getRecipe());
    assertEquals(step, stepMedia.getStep());
    assertEquals(media, stepMedia.getMedia());
  }

  @Test
  void testEquals_SameObjects_ReturnsTrue() {
    StepMediaId id = StepMediaId.builder()
        .stepId(1L)
        .mediaId(2L)
        .build();

    StepMedia stepMedia = StepMedia.builder()
        .id(id)
        .build();

    assertEquals(stepMedia, stepMedia);
  }

  @Test
  void testEquals_IdenticalObjects_ReturnsTrue() {
    StepMediaId id = StepMediaId.builder()
        .stepId(1L)
        .mediaId(2L)
        .build();

    Recipe recipe = new Recipe();
    recipe.setRecipeId(1L);

    RecipeStep step = new RecipeStep();
    step.setStepId(1L);

    Media media = new Media();
    media.setMediaId(2L);

    StepMedia stepMedia1 = StepMedia.builder()
        .id(id)
        .recipe(recipe)
        .step(step)
        .media(media)
        .build();

    StepMedia stepMedia2 = StepMedia.builder()
        .id(id)
        .recipe(recipe)
        .step(step)
        .media(media)
        .build();

    assertEquals(stepMedia1, stepMedia2);
  }

  @Test
  void testEquals_DifferentId_ReturnsFalse() {
    StepMediaId id1 = StepMediaId.builder()
        .stepId(1L)
        .mediaId(2L)
        .build();

    StepMediaId id2 = StepMediaId.builder()
        .stepId(3L)
        .mediaId(2L)
        .build();

    StepMedia stepMedia1 = StepMedia.builder()
        .id(id1)
        .build();

    StepMedia stepMedia2 = StepMedia.builder()
        .id(id2)
        .build();

    assertNotEquals(stepMedia1, stepMedia2);
  }

  @Test
  void testEquals_NullObject_ReturnsFalse() {
    StepMediaId id = StepMediaId.builder()
        .stepId(1L)
        .mediaId(2L)
        .build();

    StepMedia stepMedia = StepMedia.builder()
        .id(id)
        .build();

    assertNotEquals(stepMedia, null);
  }

  @Test
  void testEquals_DifferentClass_ReturnsFalse() {
    StepMediaId id = StepMediaId.builder()
        .stepId(1L)
        .mediaId(2L)
        .build();

    StepMedia stepMedia = StepMedia.builder()
        .id(id)
        .build();

    assertNotEquals(stepMedia, "not a step media object");
  }

  @Test
  void testHashCode_IdenticalObjects_ReturnsSameHashCode() {
    StepMediaId id = StepMediaId.builder()
        .stepId(1L)
        .mediaId(2L)
        .build();

    StepMedia stepMedia1 = StepMedia.builder()
        .id(id)
        .build();

    StepMedia stepMedia2 = StepMedia.builder()
        .id(id)
        .build();

    assertEquals(stepMedia1.hashCode(), stepMedia2.hashCode());
  }

  @Test
  void testHashCode_DifferentObjects_ReturnsDifferentHashCode() {
    StepMediaId id1 = StepMediaId.builder()
        .stepId(1L)
        .mediaId(2L)
        .build();

    StepMediaId id2 = StepMediaId.builder()
        .stepId(3L)
        .mediaId(2L)
        .build();

    StepMedia stepMedia1 = StepMedia.builder()
        .id(id1)
        .build();

    StepMedia stepMedia2 = StepMedia.builder()
        .id(id2)
        .build();

    assertNotEquals(stepMedia1.hashCode(), stepMedia2.hashCode());
  }

  @Test
  void testToString_ContainsExpectedFields() {
    StepMediaId id = StepMediaId.builder()
        .stepId(1L)
        .mediaId(2L)
        .build();

    StepMedia stepMedia = StepMedia.builder()
        .id(id)
        .build();

    String toString = stepMedia.toString();

    assertNotNull(toString);
    // Verify key fields are present in toString
    org.junit.jupiter.api.Assertions.assertTrue(toString.contains("id="));
  }

  @Test
  void testEmbeddedId_RelationshipMapping() {
    StepMediaId id = StepMediaId.builder()
        .stepId(1L)
        .mediaId(2L)
        .build();

    RecipeStep step = new RecipeStep();
    step.setStepId(1L);

    Media media = new Media();
    media.setMediaId(2L);

    StepMedia stepMedia = StepMedia.builder()
        .id(id)
        .step(step)
        .media(media)
        .build();

    // Verify that the embedded ID matches the entity relationships
    assertEquals(step.getStepId(), stepMedia.getId().getStepId());
    assertEquals(media.getMediaId(), stepMedia.getId().getMediaId());
  }

  @Test
  void testRecipeIdRelationship_SeparateFromCompositeKey() {
    StepMediaId id = StepMediaId.builder()
        .stepId(1L)
        .mediaId(2L)
        .build();

    Recipe recipe = new Recipe();
    recipe.setRecipeId(100L);

    RecipeStep step = new RecipeStep();
    step.setStepId(1L);

    Media media = new Media();
    media.setMediaId(2L);

    StepMedia stepMedia = StepMedia.builder()
        .id(id)
        .recipe(recipe)
        .step(step)
        .media(media)
        .build();

    // The recipe_id is separate from the composite key but still part of the entity
    assertEquals(100L, stepMedia.getRecipe().getRecipeId());
    assertEquals(1L, stepMedia.getId().getStepId());
    assertEquals(2L, stepMedia.getId().getMediaId());
  }
}
