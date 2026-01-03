package com.recipe_manager.model.dto.collection;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for CollectionFavorite entity. Used for transferring collection favorite
 * data between layers.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CollectionFavoriteDto {
  /** The collection ID. */
  private Long collectionId;

  /** The user ID. */
  private UUID userId;

  /** The timestamp when the collection was favorited. */
  private LocalDateTime favoritedAt;
}
