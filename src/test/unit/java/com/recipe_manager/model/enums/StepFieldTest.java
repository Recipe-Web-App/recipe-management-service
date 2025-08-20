package com.recipe_manager.model.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** Unit tests for StepField enum. */
@Tag("unit")
class StepFieldTest {

  @Test
  void shouldHaveCorrectNumberOfValues() {
    StepField[] values = StepField.values();
    assertEquals(4, values.length);
  }

  @Test
  void shouldContainExpectedValues() {
    StepField[] expectedValues = {
        StepField.STEP_NUMBER, StepField.INSTRUCTION, StepField.OPTIONAL_STATUS, StepField.TIMER
    };

    for (StepField expectedValue : expectedValues) {
      assertNotNull(expectedValue);
    }
  }

  @Test
  void shouldHaveCorrectToStringValues() {
    assertEquals("STEP_NUMBER", StepField.STEP_NUMBER.toString());
    assertEquals("INSTRUCTION", StepField.INSTRUCTION.toString());
    assertEquals("OPTIONAL_STATUS", StepField.OPTIONAL_STATUS.toString());
    assertEquals("TIMER", StepField.TIMER.toString());
  }

  @Test
  void shouldSupportValueOfMethod() {
    assertEquals(StepField.STEP_NUMBER, StepField.valueOf("STEP_NUMBER"));
    assertEquals(StepField.INSTRUCTION, StepField.valueOf("INSTRUCTION"));
    assertEquals(StepField.OPTIONAL_STATUS, StepField.valueOf("OPTIONAL_STATUS"));
    assertEquals(StepField.TIMER, StepField.valueOf("TIMER"));
  }

  @Test
  void shouldHaveConsistentOrdinalValues() {
    assertEquals(0, StepField.STEP_NUMBER.ordinal());
    assertEquals(1, StepField.INSTRUCTION.ordinal());
    assertEquals(2, StepField.OPTIONAL_STATUS.ordinal());
    assertEquals(3, StepField.TIMER.ordinal());
  }
}
