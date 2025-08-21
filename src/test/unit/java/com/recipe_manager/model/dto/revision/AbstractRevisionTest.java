package com.recipe_manager.unit_tests.model.revision;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.recipe_manager.model.dto.revision.AbstractRevision;
import com.recipe_manager.model.enums.RevisionCategory;
import com.recipe_manager.model.enums.RevisionType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/** Unit tests for AbstractRevision class. */
@Tag("unit")
class AbstractRevisionTest {

  private TestRevision testRevision;

  @BeforeEach
  void setUp() {
    testRevision = TestRevision.builder()
        .category(RevisionCategory.INGREDIENT)
        .testData("test-data")
        .valid(true)
        .build();
  }

  @Test
  void shouldCreateRevisionWithCorrectCategory() {
    assertEquals(RevisionCategory.INGREDIENT, testRevision.getCategory());
  }

  @Test
  void shouldReturnNonNullCategory() {
    assertNotNull(testRevision.getCategory());
  }

  @Test
  void shouldReturnCorrectRevisionType() {
    assertEquals(RevisionType.ADD, testRevision.getType());
  }

  @Test
  void shouldSupportSuperBuilderPattern() {
    TestRevision builtRevision = TestRevision.builder()
        .category(RevisionCategory.STEP)
        .testData("builder-data")
        .valid(false)
        .build();

    assertEquals(RevisionCategory.STEP, builtRevision.getCategory());
    assertEquals("builder-data", builtRevision.getTestData());
    assertFalse(builtRevision.isValid());
  }

  @Test
  void shouldCallValidationMethod() {
    assertTrue(testRevision.isValid());

    TestRevision invalidRevision = TestRevision.builder()
        .category(RevisionCategory.INGREDIENT)
        .testData("invalid-data")
        .valid(false)
        .build();

    assertFalse(invalidRevision.isValid());
  }

  @Test
  void shouldSupportEqualsAndHashCode() {
    TestRevision sameRevision = TestRevision.builder()
        .category(RevisionCategory.INGREDIENT)
        .testData("test-data")
        .valid(true)
        .build();

    TestRevision differentRevision = TestRevision.builder()
        .category(RevisionCategory.STEP)
        .testData("different-data")
        .valid(false)
        .build();

    assertEquals(testRevision, sameRevision);
    assertEquals(testRevision.hashCode(), sameRevision.hashCode());
    assertFalse(testRevision.equals(differentRevision));
  }

  /** Test implementation of AbstractRevision for testing purposes. */
  @Data
  @SuperBuilder
  @EqualsAndHashCode(callSuper = true)
  private static class TestRevision extends AbstractRevision {
    private String testData;
    private boolean valid;

    @Override
    public boolean isValid() {
      return valid;
    }

    @Override
    public RevisionType getType() {
      return RevisionType.ADD;
    }
  }
}
