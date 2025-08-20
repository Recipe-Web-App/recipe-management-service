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
 * Revision data model for deleting a step from a recipe. Contains all the data of the step that was
 * removed for audit purposes.
 */
@Data
@Jacksonized
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public final class StepDeleteRevision extends AbstractStepRevision {
  /** The instruction text for the step that was deleted. */
  @JsonProperty("instruction")
  private String instruction;

  /** Whether the step was optional. */
  @JsonProperty("optional")
  private Boolean optional;

  /** Timer in seconds for the step that was deleted. */
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
