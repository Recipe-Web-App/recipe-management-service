package com.recipe_manager.model.dto.collection;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for recipes within a collection. Combines recipe basic info with collection
 * item metadata (display order, added by, added at).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public final class CollectionRecipeDto {
  /** The recipe ID. */
  private Long recipeId;

  /** The recipe title. */
  private String recipeTitle;

  /** The recipe description (optional). */
  private String recipeDescription;

  /** The display order of the recipe in the collection. */
  private Integer displayOrder;

  /** The user ID who added this recipe to the collection. */
  private UUID addedBy;

  /** The timestamp when the recipe was added to the collection. */
  private LocalDateTime addedAt;
}
