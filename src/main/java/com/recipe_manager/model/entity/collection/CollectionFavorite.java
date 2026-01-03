package com.recipe_manager.model.entity.collection;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a user's favorite collection. Maps to the collection_favorites table in the
 * database.
 */
@Entity
@Table(name = "collection_favorites", schema = "recipe_manager")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CollectionFavorite {
  /** The composite ID for this favorite. */
  @EmbeddedId private CollectionFavoriteId id;

  /** The collection entity. */
  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("collectionId")
  @JoinColumn(name = "collection_id", nullable = false)
  private RecipeCollection collection;

  /** The timestamp when the collection was favorited. */
  @CreationTimestamp
  @Column(name = "favorited_at")
  private LocalDateTime favoritedAt;
}
