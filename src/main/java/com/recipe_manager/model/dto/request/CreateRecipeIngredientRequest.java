package com.recipe_manager.model.dto.request;

import java.math.BigDecimal;

import com.recipe_manager.model.enums.IngredientUnit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** Request DTO for creating a recipe ingredient. Contains ingredient data for recipe creation. */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public final class CreateRecipeIngredientRequest {
  /** The ingredient name. */
  private String ingredientName;

  /** The ingredient ID. */
  private Long ingredientId;

  /** The quantity of the ingredient. */
  private BigDecimal quantity;

  /** The unit of measurement. */
  private IngredientUnit unit;

  /** Whether the ingredient is optional. */
  private Boolean isOptional;

  /** Notes about the ingredient. */
  private String notes;
}
