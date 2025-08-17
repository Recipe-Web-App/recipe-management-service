package com.recipe_manager.model.dto.external.recipescraper;

import java.math.BigDecimal;

import com.recipe_manager.model.dto.ingredient.QuantityDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * External DTO for ingredient shopping information from recipe scraper service. Contains pricing
 * and quantity details for a specific ingredient.
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public final class IngredientShoppingInfoDto {
  /** The ingredient name. */
  private String ingredientName;

  /** The quantity of the ingredient. */
  private QuantityDto quantity;

  /** The estimated price for this ingredient. */
  private BigDecimal estimatedPrice;
}
