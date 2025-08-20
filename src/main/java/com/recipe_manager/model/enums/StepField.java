package com.recipe_manager.model.enums;

/**
 * Enum representing the fields of a recipe step that can be modified in a revision. Used for
 * tracking specific field changes in step update revisions.
 */
public enum StepField {
  /** The step number in the recipe sequence. */
  STEP_NUMBER,

  /** The instruction text for the step. */
  INSTRUCTION,

  /** Whether the step is optional in the recipe. */
  OPTIONAL_STATUS,

  /** The timer duration in seconds for the step. */
  TIMER
}
