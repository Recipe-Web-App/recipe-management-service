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
import com.recipe_manager.model.revision.AbstractStepRevision;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/** Unit tests for AbstractStepRevision class. */
@Tag("unit")
class AbstractStepRevisionTest {

  private TestStepRevision testRevision;

  @BeforeEach
  void setUp() {
    testRevision = TestStepRevision.builder()
        .category(RevisionCategory.STEP)
        .type(RevisionType.ADD)
        .stepId(1L)
        .stepNumber(1)
        .build();
  }

  @Test
  void shouldCreateRevisionWithStepCategory() {
    assertEquals(RevisionCategory.STEP, testRevision.getCategory());
    assertEquals(RevisionType.ADD, testRevision.getType());
  }

  @Test
  void shouldSetStepIdAndNumber() {
    assertEquals(1L, testRevision.getStepId());
    assertEquals(1, testRevision.getStepNumber());
  }

  @Test
  void shouldCreateRevisionWithoutStepDetails() {
    TestStepRevision revision = TestStepRevision.builder()
        .category(RevisionCategory.STEP)
        .type(RevisionType.UPDATE)
        .build();

    assertEquals(RevisionCategory.STEP, revision.getCategory());
    assertEquals(RevisionType.UPDATE, revision.getType());
    assertNull(revision.getStepId());
    assertNull(revision.getStepNumber());
  }

  @Test
  void shouldValidateStepData() {
    assertTrue(testRevision.isValid()); // Uses hasValidStepData internally

    TestStepRevision invalidRevision = TestStepRevision.builder()
        .category(RevisionCategory.STEP)
        .type(RevisionType.ADD)
        .build();
    assertFalse(invalidRevision.isValid());

    invalidRevision.setStepId(1L);
    assertFalse(invalidRevision.isValid());

    invalidRevision.setStepNumber(0);
    assertFalse(invalidRevision.isValid());

    invalidRevision.setStepNumber(-1);
    assertFalse(invalidRevision.isValid());

    invalidRevision.setStepNumber(1);
    assertTrue(invalidRevision.isValid());
  }

  @Test
  void shouldUpdateStepDetails() {
    testRevision.setStepId(2L);
    testRevision.setStepNumber(3);

    assertEquals(2L, testRevision.getStepId());
    assertEquals(3, testRevision.getStepNumber());
  }

  @Test
  void shouldReturnNonNullCategoryAndType() {
    assertNotNull(testRevision.getCategory());
    assertNotNull(testRevision.getType());
  }

  @Test
  void shouldSupportSuperBuilderInheritance() {
    TestStepRevision revision = TestStepRevision.builder()
        .category(RevisionCategory.STEP)
        .type(RevisionType.DELETE)
        .stepId(123L)
        .stepNumber(5)
        .build();

    assertEquals(RevisionCategory.STEP, revision.getCategory());
    assertEquals(RevisionType.DELETE, revision.getType());
    assertEquals(123L, revision.getStepId());
    assertEquals(5, revision.getStepNumber());
    assertTrue(revision.isValid());
  }

  @Test
  void shouldSupportEqualsAndHashCode() {
    TestStepRevision sameRevision = TestStepRevision.builder()
        .category(RevisionCategory.STEP)
        .type(RevisionType.ADD)
        .stepId(1L)
        .stepNumber(1)
        .build();

    TestStepRevision differentRevision = TestStepRevision.builder()
        .category(RevisionCategory.STEP)
        .type(RevisionType.UPDATE)
        .stepId(2L)
        .stepNumber(2)
        .build();

    assertEquals(testRevision, sameRevision);
    assertEquals(testRevision.hashCode(), sameRevision.hashCode());
    assertFalse(testRevision.equals(differentRevision));
  }

  /** Test implementation of AbstractStepRevision for testing purposes. */
  @Data
  @SuperBuilder
  @EqualsAndHashCode(callSuper = true)
  private static class TestStepRevision extends AbstractStepRevision {

    @Override
    public boolean isValid() {
      return hasValidStepData();
    }
  }
}
