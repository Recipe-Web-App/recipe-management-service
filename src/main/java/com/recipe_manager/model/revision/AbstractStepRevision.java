package com.recipe_manager.model.revision;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.recipe_manager.model.enums.RevisionType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Abstract base class for step-related revision data models. Contains common fields and
 * functionality for tracking changes to recipe steps.
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = StepAddRevision.class, name = "ADD"),
  @JsonSubTypes.Type(value = StepUpdateRevision.class, name = "UPDATE"),
  @JsonSubTypes.Type(value = StepDeleteRevision.class, name = "DELETE")
})
public abstract class AbstractStepRevision extends AbstractRevision {

  /** The type of this revision (ADD, UPDATE, or DELETE). */
  private RevisionType type;

  /** The ID of the step being revised. */
  private Long stepId;

  /** The step number in the recipe sequence. */
  private Integer stepNumber;

  /**
   * Validates that the step revision contains required step data.
   *
   * @return true if stepId and stepNumber are not null and stepNumber is positive
   */
  protected boolean hasValidStepData() {
    return stepId != null && stepNumber != null && stepNumber > 0;
  }
}
