package com.recipe_manager.model.dto.collection;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for RecipeCollectionItem entity. Used for transferring collection item data
 * between layers.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecipeCollectionItemDto {
  /** The collection ID. */
  private Long collectionId;

  /** The recipe ID. */
  private Long recipeId;

  /** The display order of the recipe in the collection. */
  private Integer displayOrder;

  /** The user ID who added this recipe. */
  private UUID addedBy;

  /** The timestamp when the recipe was added. */
  private LocalDateTime addedAt;
}
