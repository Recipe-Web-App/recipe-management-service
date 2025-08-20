package com.recipe_manager.model.dto.revision;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.recipe_manager.model.enums.StepField;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * Revision data model for updating an existing step in a recipe. Contains the field that was
 * changed and both the previous and new values.
 */
@Data
@Jacksonized
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public final class StepUpdateRevision extends AbstractStepRevision {
  /** The field that was changed. */
  @JsonProperty("changedField")
  private StepField changedField;

  /** The previous value before the change. */
  @JsonProperty("previousValue")
  private Object previousValue;

  /** The new value after the change. */
  @JsonProperty("newValue")
  private Object newValue;

  @Override
  public boolean isValid() {
    return hasValidStepData()
        && changedField != null
        && previousValue != null
        && newValue != null
        && !previousValue.equals(newValue);
  }
}
