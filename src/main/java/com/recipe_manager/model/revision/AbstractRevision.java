package com.recipe_manager.model.revision;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.recipe_manager.model.enums.RevisionCategory;
import com.recipe_manager.model.enums.RevisionType;

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

  /** Comment associated with the revision. */
  private String changeComment;

  /** Timestamp indicating when this revision was created. */
  private LocalDateTime createdAt;

  /**
   * Validates that this revision contains all required data.
   *
   * @return true if the revision is valid, false otherwise
   */
  public abstract boolean isValid();

  /**
   * Gets the type of this revision.
   *
   * @return the revision type
   */
  public abstract RevisionType getType();
}
