package com.recipe_manager.model.dto.external.recipescraper;

import java.math.BigDecimal;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * External DTO for recipe shopping information from recipe scraper service. Contains pricing
 * information for all ingredients in a recipe.
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public final class RecipeScraperShoppingDto {
  /** The recipe ID. */
  private Long recipeId;

  /** Map of ingredient names to their shopping information. */
  private Map<String, IngredientShoppingInfoDto> ingredients;

  /** Total estimated cost for all ingredients. */
  private BigDecimal totalEstimatedCost;
}
