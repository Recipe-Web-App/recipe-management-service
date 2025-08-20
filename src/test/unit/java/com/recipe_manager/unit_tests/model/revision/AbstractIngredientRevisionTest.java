package com.recipe_manager.unit_tests.model.revision;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.recipe_manager.model.enums.RevisionCategory;
import com.recipe_manager.model.enums.RevisionType;
import com.recipe_manager.model.revision.AbstractIngredientRevision;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/** Unit tests for AbstractIngredientRevision class. */
@Tag("unit")
class AbstractIngredientRevisionTest {

  private TestIngredientRevision testRevision;

  @BeforeEach
  void setUp() {
    testRevision = TestIngredientRevision.builder()
        .category(RevisionCategory.INGREDIENT)
        .type(RevisionType.ADD)
        .ingredientId(1L)
        .ingredientName("Test Ingredient")
        .build();
  }

  @Test
  void shouldCreateRevisionWithIngredientCategory() {
    assertEquals(RevisionCategory.INGREDIENT, testRevision.getCategory());
    assertEquals(RevisionType.ADD, testRevision.getType());
  }

  @Test
  void shouldSetIngredientIdAndName() {
    assertEquals(1L, testRevision.getIngredientId());
    assertEquals("Test Ingredient", testRevision.getIngredientName());
  }

  @Test
  void shouldCreateRevisionWithoutIngredientDetails() {
    TestIngredientRevision revision = TestIngredientRevision.builder()
        .category(RevisionCategory.INGREDIENT)
        .type(RevisionType.UPDATE)
        .build();

    assertEquals(RevisionCategory.INGREDIENT, revision.getCategory());
    assertEquals(RevisionType.UPDATE, revision.getType());
    assertNull(revision.getIngredientId());
    assertNull(revision.getIngredientName());
  }

  @Test
  void shouldValidateIngredientData() {
    assertTrue(testRevision.isValid()); // Uses hasValidIngredientData internally

    TestIngredientRevision invalidRevision = TestIngredientRevision.builder()
        .category(RevisionCategory.INGREDIENT)
        .type(RevisionType.ADD)
        .build();
    assertFalse(invalidRevision.isValid());

    invalidRevision.setIngredientId(1L);
    assertFalse(invalidRevision.isValid());

    invalidRevision.setIngredientName("");
    assertFalse(invalidRevision.isValid());

    invalidRevision.setIngredientName("   ");
    assertFalse(invalidRevision.isValid());

    invalidRevision.setIngredientName("Valid Name");
    assertTrue(invalidRevision.isValid());
  }

  @Test
  void shouldUpdateIngredientDetails() {
    testRevision.setIngredientId(2L);
    testRevision.setIngredientName("Updated Ingredient");

    assertEquals(2L, testRevision.getIngredientId());
    assertEquals("Updated Ingredient", testRevision.getIngredientName());
  }

  @Test
  void shouldReturnNonNullCategoryAndType() {
    assertNotNull(testRevision.getCategory());
    assertNotNull(testRevision.getType());
  }

  @Test
  void shouldSupportSuperBuilderInheritance() {
    TestIngredientRevision revision = TestIngredientRevision.builder()
        .category(RevisionCategory.INGREDIENT)
        .type(RevisionType.DELETE)
        .ingredientId(123L)
        .ingredientName("Builder Test Ingredient")
        .build();

    assertEquals(RevisionCategory.INGREDIENT, revision.getCategory());
    assertEquals(RevisionType.DELETE, revision.getType());
    assertEquals(123L, revision.getIngredientId());
    assertEquals("Builder Test Ingredient", revision.getIngredientName());
    assertTrue(revision.isValid());
  }

  @Test
  void shouldSupportEqualsAndHashCode() {
    TestIngredientRevision sameRevision = TestIngredientRevision.builder()
        .category(RevisionCategory.INGREDIENT)
        .type(RevisionType.ADD)
        .ingredientId(1L)
        .ingredientName("Test Ingredient")
        .build();

    TestIngredientRevision differentRevision = TestIngredientRevision.builder()
        .category(RevisionCategory.INGREDIENT)
        .type(RevisionType.UPDATE)
        .ingredientId(2L)
        .ingredientName("Different Ingredient")
        .build();

    assertEquals(testRevision, sameRevision);
    assertEquals(testRevision.hashCode(), sameRevision.hashCode());
    assertFalse(testRevision.equals(differentRevision));
  }

  /** Test implementation of AbstractIngredientRevision for testing purposes. */
  @Data
  @SuperBuilder
  @EqualsAndHashCode(callSuper = true)
  private static class TestIngredientRevision extends AbstractIngredientRevision {

    @Override
    public boolean isValid() {
      return hasValidIngredientData();
    }
  }
}
