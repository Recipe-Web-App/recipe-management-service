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
 * Revision data model for deleting an ingredient from a recipe. Contains all the data of the
 * ingredient that was removed for audit purposes.
 */
@Data
@Jacksonized
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public final class IngredientDeleteRevision extends AbstractIngredientRevision {
  /** The quantity of the ingredient that was deleted. */
  @JsonProperty("quantity")
  private BigDecimal quantity;

  /** The unit of measurement for the ingredient that was deleted. */
  @JsonProperty("unit")
  private IngredientUnit unit;

  /** Whether the ingredient was optional. */
  @JsonProperty("isOptional")
  private Boolean isOptional;

  /** Optional description of the ingredient that was deleted. */
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
