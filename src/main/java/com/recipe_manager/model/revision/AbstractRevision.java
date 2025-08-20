package com.recipe_manager.model.revision;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.recipe_manager.model.enums.RevisionCategory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Abstract base class for all recipe revision data models. Provides common functionality for
 * tracking changes to recipe components.
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "category")
@JsonSubTypes({
  @JsonSubTypes.Type(value = AbstractIngredientRevision.class, name = "INGREDIENT"),
  @JsonSubTypes.Type(value = AbstractStepRevision.class, name = "STEP")
})
public abstract class AbstractRevision {

  /** The category of this revision (INGREDIENT or STEP). */
  private RevisionCategory category;

  /**
   * Validates that this revision contains all required data.
   *
   * @return true if the revision is valid, false otherwise
   */
  public abstract boolean isValid();
}
