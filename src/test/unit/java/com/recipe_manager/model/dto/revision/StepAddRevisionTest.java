package com.recipe_manager.unit_tests.model.revision;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipe_manager.model.dto.revision.StepAddRevision;
import com.recipe_manager.model.enums.RevisionCategory;
import com.recipe_manager.model.enums.RevisionType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** Unit tests for StepAddRevision class. */
@Tag("unit")
class StepAddRevisionTest {

  private StepAddRevision revision;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();

    revision = StepAddRevision.builder()
        .category(RevisionCategory.STEP)
        .type(RevisionType.ADD)
        .stepId(1L)
        .stepNumber(1)
        .instruction("Mix ingredients thoroughly")
        .optional(false)
        .timerSeconds(300)
        .build();
  }

  @Test
  void shouldCreateRevisionWithCorrectCategoryAndType() {
    assertEquals(RevisionCategory.STEP, revision.getCategory());
    assertEquals(RevisionType.ADD, revision.getType());
  }

  @Test
  void shouldSetAllFields() {
    assertEquals(1L, revision.getStepId());
    assertEquals(1, revision.getStepNumber());
    assertEquals("Mix ingredients thoroughly", revision.getInstruction());
    assertEquals(false, revision.getOptional());
    assertEquals(300, revision.getTimerSeconds());
  }

  @Test
  void shouldCreateDefaultRevision() {
    StepAddRevision defaultRevision = StepAddRevision.builder().build();
    assertNull(defaultRevision.getCategory());
    assertNull(defaultRevision.getType());
    assertNull(defaultRevision.getStepId());
    assertNull(defaultRevision.getStepNumber());
    assertNull(defaultRevision.getInstruction());
    assertNull(defaultRevision.getOptional());
    assertNull(defaultRevision.getTimerSeconds());
  }

  @Test
  void shouldValidateCorrectRevision() {
    assertTrue(revision.isValid());
  }

  @Test
  void shouldValidateRevisionWithNullTimer() {
    StepAddRevision revisionWithNullTimer = StepAddRevision.builder()
        .category(RevisionCategory.STEP)
        .type(RevisionType.ADD)
        .stepId(1L)
        .stepNumber(1)
        .instruction("Mix ingredients thoroughly")
        .optional(false)
        .timerSeconds(null)
        .build();

    assertTrue(revisionWithNullTimer.isValid()); // Timer is optional
  }

  @Test
  void shouldInvalidateRevisionWithMissingStepData() {
    StepAddRevision invalidRevision = StepAddRevision.builder()
        .instruction("Valid instruction")
        .optional(false)
        .build();

    assertFalse(invalidRevision.isValid());
  }

  @Test
  void shouldInvalidateRevisionWithNullInstruction() {
    StepAddRevision invalidRevision = StepAddRevision.builder()
        .category(RevisionCategory.STEP)
        .type(RevisionType.ADD)
        .stepId(1L)
        .stepNumber(1)
        .instruction(null)
        .optional(false)
        .build();

    assertFalse(invalidRevision.isValid());
  }

  @Test
  void shouldInvalidateRevisionWithEmptyInstruction() {
    StepAddRevision invalidRevision = StepAddRevision.builder()
        .category(RevisionCategory.STEP)
        .type(RevisionType.ADD)
        .stepId(1L)
        .stepNumber(1)
        .instruction("")
        .optional(false)
        .build();

    assertFalse(invalidRevision.isValid());
  }

  @Test
  void shouldInvalidateRevisionWithBlankInstruction() {
    StepAddRevision invalidRevision = StepAddRevision.builder()
        .category(RevisionCategory.STEP)
        .type(RevisionType.ADD)
        .stepId(1L)
        .stepNumber(1)
        .instruction("   ")
        .optional(false)
        .build();

    assertFalse(invalidRevision.isValid());
  }

  @Test
  void shouldInvalidateRevisionWithNullOptionalFlag() {
    StepAddRevision invalidRevision = StepAddRevision.builder()
        .category(RevisionCategory.STEP)
        .type(RevisionType.ADD)
        .stepId(1L)
        .stepNumber(1)
        .instruction("Mix ingredients thoroughly")
        .optional(null)
        .build();

    assertFalse(invalidRevision.isValid());
  }

  @Test
  void shouldSerializeToJson() throws Exception {
    String json = objectMapper.writeValueAsString(revision);
    assertNotNull(json);
    assertTrue(json.contains("\"stepId\":1"));
    assertTrue(json.contains("\"stepNumber\":1"));
    assertTrue(json.contains("\"instruction\":\"Mix ingredients thoroughly\""));
    assertTrue(json.contains("\"optional\":false"));
    assertTrue(json.contains("\"timerSeconds\":300"));
  }

  @Test
  void shouldDeserializeFromJson() throws Exception {
    String json = objectMapper.writeValueAsString(revision);
    StepAddRevision newRevision = objectMapper.readValue(json, StepAddRevision.class);

    assertEquals(revision.getStepId(), newRevision.getStepId());
    assertEquals(revision.getStepNumber(), newRevision.getStepNumber());
    assertEquals(revision.getInstruction(), newRevision.getInstruction());
    assertEquals(revision.getOptional(), newRevision.getOptional());
    assertEquals(revision.getTimerSeconds(), newRevision.getTimerSeconds());
  }

  @Test
  void shouldSupportSuperBuilderPattern() {
    StepAddRevision builtRevision = StepAddRevision.builder()
        .category(RevisionCategory.STEP)
        .type(RevisionType.ADD)
        .stepId(123L)
        .stepNumber(5)
        .instruction("Bake for 30 minutes")
        .optional(true)
        .timerSeconds(1800)
        .build();

    assertEquals(RevisionCategory.STEP, builtRevision.getCategory());
    assertEquals(RevisionType.ADD, builtRevision.getType());
    assertEquals(123L, builtRevision.getStepId());
    assertEquals(5, builtRevision.getStepNumber());
    assertEquals("Bake for 30 minutes", builtRevision.getInstruction());
    assertTrue(builtRevision.getOptional());
    assertEquals(1800, builtRevision.getTimerSeconds());
    assertTrue(builtRevision.isValid());
  }

  @Test
  void shouldSupportEqualsAndHashCode() {
    StepAddRevision sameRevision = StepAddRevision.builder()
        .category(RevisionCategory.STEP)
        .type(RevisionType.ADD)
        .stepId(1L)
        .stepNumber(1)
        .instruction("Mix ingredients thoroughly")
        .optional(false)
        .timerSeconds(300)
        .build();

    StepAddRevision differentRevision = StepAddRevision.builder()
        .category(RevisionCategory.STEP)
        .type(RevisionType.ADD)
        .stepId(2L)
        .stepNumber(2)
        .instruction("Different instruction")
        .optional(true)
        .timerSeconds(600)
        .build();

    assertEquals(revision, sameRevision);
    assertEquals(revision.hashCode(), sameRevision.hashCode());
    assertFalse(revision.equals(differentRevision));
  }
}
