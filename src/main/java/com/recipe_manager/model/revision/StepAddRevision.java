package com.recipe_manager.model.revision;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * Revision data model for adding a new step to a recipe. Contains all the data needed to track the
 * addition of a step.
 */
@Data
@Jacksonized
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public final class StepAddRevision extends AbstractStepRevision {
  /** The instruction text for the step. */
  @JsonProperty("instruction")
  private String instruction;

  /** Whether the step is optional. */
  @JsonProperty("optional")
  private Boolean optional;

  /** Timer in seconds for this step. */
  @JsonProperty("timerSeconds")
  private Integer timerSeconds;

  @Override
  public boolean isValid() {
    return hasValidStepData()
        && instruction != null
        && !instruction.trim().isEmpty()
        && optional != null;
  }
}
