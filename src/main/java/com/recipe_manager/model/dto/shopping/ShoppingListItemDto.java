package com.recipe_manager.model.dto.shopping;

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

/**
 * Data Transfer Object for shopping list items. Used for transferring shopping list item data
 * between layers.
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public final class ShoppingListItemDto {
  /** The ingredient ID. */
  private Long ingredientId;

  /** The ingredient name. */
  private String ingredientName;

  /** The total quantity needed for this ingredient. */
  private BigDecimal totalQuantity;

  /** The unit of measurement. */
  private IngredientUnit unit;

  /** Whether the ingredient is optional. */
  private Boolean isOptional;

  /** The estimated price for this ingredient. */
  private BigDecimal estimatedPrice;
}
