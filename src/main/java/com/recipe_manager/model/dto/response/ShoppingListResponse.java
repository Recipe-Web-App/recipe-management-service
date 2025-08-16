package com.recipe_manager.model.dto.response;

import java.util.List;

import com.recipe_manager.model.dto.shopping.ShoppingListItemDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Response DTO for shopping list endpoint. Contains a list of shopping list items for a specific
 * recipe.
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public final class ShoppingListResponse {
  /** The recipe ID. */
  private Long recipeId;

  /** List of shopping list items for the recipe. */
  private List<ShoppingListItemDto> items;

  /** Total count of shopping list items. */
  private Integer totalCount;

  /** Total estimated cost for all ingredients. */
  private java.math.BigDecimal totalEstimatedCost;
}
