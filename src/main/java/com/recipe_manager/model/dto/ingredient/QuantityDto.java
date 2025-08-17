package com.recipe_manager.model.dto.ingredient;

import com.recipe_manager.model.enums.IngredientUnit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuantityDto {
  /** The numeric amount of the ingredient. */
  private double amount;

  /** The unit of measurement for the ingredient. */
  private IngredientUnit measurement;
}
