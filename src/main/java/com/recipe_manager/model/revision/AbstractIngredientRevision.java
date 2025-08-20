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
 * Abstract base class for ingredient-related revision data models. Contains common fields and
 * functionality for tracking changes to recipe ingredients.
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = IngredientAddRevision.class, name = "ADD"),
  @JsonSubTypes.Type(value = IngredientUpdateRevision.class, name = "UPDATE"),
  @JsonSubTypes.Type(value = IngredientDeleteRevision.class, name = "DELETE")
})
public abstract class AbstractIngredientRevision extends AbstractRevision {

  /** The type of this revision (ADD, UPDATE, or DELETE). */
  private RevisionType type;

  /** The ID of the ingredient being revised. */
  private Long ingredientId;

  /** The name of the ingredient being revised. */
  private String ingredientName;

  /**
   * Validates that the ingredient revision contains required ingredient data.
   *
   * @return true if ingredientId and ingredientName are not null
   */
  protected boolean hasValidIngredientData() {
    return ingredientId != null && ingredientName != null && !ingredientName.trim().isEmpty();
  }
}
