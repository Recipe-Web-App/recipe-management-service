package com.recipe_manager.unit_tests.model.revision;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipe_manager.model.dto.revision.StepUpdateRevision;
import com.recipe_manager.model.enums.RevisionCategory;
import com.recipe_manager.model.enums.RevisionType;
import com.recipe_manager.model.enums.StepField;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** Unit tests for StepUpdateRevision class. */
@Tag("unit")
class StepUpdateRevisionTest {

  private StepUpdateRevision revision;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();

    revision = StepUpdateRevision.builder()
        .category(RevisionCategory.STEP)
        .type(RevisionType.UPDATE)
        .stepId(1L)
        .stepNumber(1)
        .changedField(StepField.INSTRUCTION)
        .previousValue("Old instruction")
        .newValue("New instruction")
        .build();
  }

  @Test
  void shouldCreateRevisionWithCorrectCategoryAndType() {
    assertEquals(RevisionCategory.STEP, revision.getCategory());
    assertEquals(RevisionType.UPDATE, revision.getType());
  }

  @Test
  void shouldSetAllFields() {
    assertEquals(1L, revision.getStepId());
    assertEquals(1, revision.getStepNumber());
    assertEquals(StepField.INSTRUCTION, revision.getChangedField());
    assertEquals("Old instruction", revision.getPreviousValue());
    assertEquals("New instruction", revision.getNewValue());
  }

  @Test
  void shouldCreateDefaultRevision() {
    StepUpdateRevision defaultRevision = StepUpdateRevision.builder().build();
    assertNull(defaultRevision.getCategory());
    assertNull(defaultRevision.getType());
    assertNull(defaultRevision.getStepId());
    assertNull(defaultRevision.getStepNumber());
    assertNull(defaultRevision.getChangedField());
    assertNull(defaultRevision.getPreviousValue());
    assertNull(defaultRevision.getNewValue());
  }

  @Test
  void shouldValidateCorrectRevision() {
    assertTrue(revision.isValid());
  }

  @Test
  void shouldInvalidateRevisionWithMissingStepData() {
    StepUpdateRevision invalidRevision = StepUpdateRevision.builder()
        .changedField(StepField.INSTRUCTION)
        .previousValue("Old value")
        .newValue("New value")
        .build();

    assertFalse(invalidRevision.isValid());
  }

  @Test
  void shouldInvalidateRevisionWithNullChangedField() {
    StepUpdateRevision invalidRevision = StepUpdateRevision.builder()
        .category(RevisionCategory.STEP)
        .type(RevisionType.UPDATE)
        .stepId(1L)
        .stepNumber(1)
        .changedField(null)
        .previousValue("Old instruction")
        .newValue("New instruction")
        .build();

    assertFalse(invalidRevision.isValid());
  }

  @Test
  void shouldInvalidateRevisionWithNullPreviousValue() {
    StepUpdateRevision invalidRevision = StepUpdateRevision.builder()
        .category(RevisionCategory.STEP)
        .type(RevisionType.UPDATE)
        .stepId(1L)
        .stepNumber(1)
        .changedField(StepField.INSTRUCTION)
        .previousValue(null)
        .newValue("New instruction")
        .build();

    assertFalse(invalidRevision.isValid());
  }

  @Test
  void shouldInvalidateRevisionWithNullNewValue() {
    StepUpdateRevision invalidRevision = StepUpdateRevision.builder()
        .category(RevisionCategory.STEP)
        .type(RevisionType.UPDATE)
        .stepId(1L)
        .stepNumber(1)
        .changedField(StepField.INSTRUCTION)
        .previousValue("Old instruction")
        .newValue(null)
        .build();

    assertFalse(invalidRevision.isValid());
  }

  @Test
  void shouldInvalidateRevisionWithSamePreviousAndNewValue() {
    StepUpdateRevision invalidRevision = StepUpdateRevision.builder()
        .category(RevisionCategory.STEP)
        .type(RevisionType.UPDATE)
        .stepId(1L)
        .stepNumber(1)
        .changedField(StepField.INSTRUCTION)
        .previousValue("Same value")
        .newValue("Same value")
        .build();

    assertFalse(invalidRevision.isValid());
  }

  @Test
  void shouldHandleDifferentFieldTypes() {
    // Test with step number change
    StepUpdateRevision stepNumberRevision = StepUpdateRevision.builder()
        .category(RevisionCategory.STEP)
        .type(RevisionType.UPDATE)
        .stepId(1L)
        .stepNumber(2)
        .changedField(StepField.STEP_NUMBER)
        .previousValue(1)
        .newValue(2)
        .build();
    assertTrue(stepNumberRevision.isValid());

    // Test with optional status change
    StepUpdateRevision optionalRevision = StepUpdateRevision.builder()
        .category(RevisionCategory.STEP)
        .type(RevisionType.UPDATE)
        .stepId(1L)
        .stepNumber(1)
        .changedField(StepField.OPTIONAL_STATUS)
        .previousValue(false)
        .newValue(true)
        .build();
    assertTrue(optionalRevision.isValid());

    // Test with timer change
    StepUpdateRevision timerRevision = StepUpdateRevision.builder()
        .category(RevisionCategory.STEP)
        .type(RevisionType.UPDATE)
        .stepId(1L)
        .stepNumber(1)
        .changedField(StepField.TIMER)
        .previousValue(300)
        .newValue(600)
        .build();
    assertTrue(timerRevision.isValid());
  }

  @Test
  void shouldSerializeToJson() throws Exception {
    String json = objectMapper.writeValueAsString(revision);
    assertNotNull(json);
    assertTrue(json.contains("\"stepId\":1"));
    assertTrue(json.contains("\"stepNumber\":1"));
    assertTrue(json.contains("\"changedField\":\"INSTRUCTION\""));
    assertTrue(json.contains("\"previousValue\":\"Old instruction\""));
    assertTrue(json.contains("\"newValue\":\"New instruction\""));
  }

  @Test
  void shouldDeserializeFromJson() throws Exception {
    String json = objectMapper.writeValueAsString(revision);
    StepUpdateRevision newRevision = objectMapper.readValue(json, StepUpdateRevision.class);

    assertEquals(revision.getStepId(), newRevision.getStepId());
    assertEquals(revision.getStepNumber(), newRevision.getStepNumber());
    assertEquals(revision.getChangedField(), newRevision.getChangedField());
    assertEquals(revision.getPreviousValue(), newRevision.getPreviousValue());
    assertEquals(revision.getNewValue(), newRevision.getNewValue());
  }

  @Test
  void shouldSupportSuperBuilderPattern() {
    StepUpdateRevision builtRevision = StepUpdateRevision.builder()
        .category(RevisionCategory.STEP)
        .type(RevisionType.UPDATE)
        .stepId(123L)
        .stepNumber(5)
        .changedField(StepField.TIMER)
        .previousValue(300)
        .newValue(600)
        .build();

    assertEquals(RevisionCategory.STEP, builtRevision.getCategory());
    assertEquals(RevisionType.UPDATE, builtRevision.getType());
    assertEquals(123L, builtRevision.getStepId());
    assertEquals(5, builtRevision.getStepNumber());
    assertEquals(StepField.TIMER, builtRevision.getChangedField());
    assertEquals(300, builtRevision.getPreviousValue());
    assertEquals(600, builtRevision.getNewValue());
    assertTrue(builtRevision.isValid());
  }

  @Test
  void shouldSupportEqualsAndHashCode() {
    StepUpdateRevision sameRevision = StepUpdateRevision.builder()
        .category(RevisionCategory.STEP)
        .type(RevisionType.UPDATE)
        .stepId(1L)
        .stepNumber(1)
        .changedField(StepField.INSTRUCTION)
        .previousValue("Old instruction")
        .newValue("New instruction")
        .build();

    StepUpdateRevision differentRevision = StepUpdateRevision.builder()
        .category(RevisionCategory.STEP)
        .type(RevisionType.UPDATE)
        .stepId(2L)
        .stepNumber(2)
        .changedField(StepField.TIMER)
        .previousValue(300)
        .newValue(600)
        .build();

    assertEquals(revision, sameRevision);
    assertEquals(revision.hashCode(), sameRevision.hashCode());
    assertFalse(revision.equals(differentRevision));
  }
}
