package com.recipe_manager.model.entity.collection;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Composite primary key for CollectionFavorite entity. */
@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CollectionFavoriteId implements Serializable {
  /** Serial version UID for ensuring compatibility during serialization. */
  private static final long serialVersionUID = 1L;

  /** The user ID. */
  private UUID userId;

  /** The collection ID. */
  private Long collectionId;
}
