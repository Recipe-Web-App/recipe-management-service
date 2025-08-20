package com.recipe_manager.model.revision;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.recipe_manager.model.enums.IngredientUnit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * Revision data model for adding a new ingredient to a recipe. Contains all the data needed to
 * track the addition of an ingredient.
 */
@Data
@Jacksonized
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public final class IngredientAddRevision extends AbstractIngredientRevision {
  /** The quantity of the ingredient added. */
  @JsonProperty("quantity")
  private BigDecimal quantity;

  /** The unit of measurement for the ingredient. */
  @JsonProperty("unit")
  private IngredientUnit unit;

  /** Whether the ingredient is optional. */
  @JsonProperty("isOptional")
  private Boolean isOptional;

  /** Optional description of the ingredient. */
  @JsonProperty("description")
  private String description;

  @Override
  public boolean isValid() {
    return hasValidIngredientData()
        && quantity != null
        && quantity.compareTo(BigDecimal.ZERO) > 0
        && unit != null
        && isOptional != null;
  }
}
