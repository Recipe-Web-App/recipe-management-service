package com.recipe_manager.model.entity.recipe;

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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entity representing a user's favorite recipe. Maps to the recipe_favorites table in the database.
 */
@Entity
@Table(name = "recipe_favorites", schema = "recipe_manager")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class RecipeFavorite {
  /** The composite ID for this favorite. */
  @EmbeddedId private RecipeFavoriteId id;

  /** The recipe entity. */
  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("recipeId")
  @JoinColumn(name = "recipe_id", nullable = false)
  private Recipe recipe;

  /** The timestamp when the recipe was favorited. */
  @CreationTimestamp
  @Column(name = "favorited_at")
  private LocalDateTime favoritedAt;
}
