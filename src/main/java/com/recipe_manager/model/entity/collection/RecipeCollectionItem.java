package com.recipe_manager.model.entity.collection;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import com.recipe_manager.model.entity.recipe.Recipe;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entity representing a recipe item within a collection. Maps to the recipe_collection_items table
 * in the database.
 */
@Entity
@Table(name = "recipe_collection_items", schema = "recipe_manager")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"collection", "recipe"})
@ToString(exclude = {"collection", "recipe"})
public class RecipeCollectionItem {
  /** The composite ID for this collection item. */
  @EmbeddedId private RecipeCollectionItemId id;

  /** The collection this item belongs to. */
  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("collectionId")
  @JoinColumn(name = "collection_id", nullable = false)
  private RecipeCollection collection;

  /** The recipe in this collection. */
  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("recipeId")
  @JoinColumn(name = "recipe_id", nullable = false)
  private Recipe recipe;

  /** The display order of the recipe in the collection. */
  @NotNull
  @Column(name = "display_order", nullable = false)
  private Integer displayOrder;

  /** The user ID who added this recipe to the collection. */
  @NotNull
  @Column(name = "added_by", nullable = false)
  private UUID addedBy;

  /** The timestamp when the recipe was added to the collection. */
  @CreationTimestamp
  @Column(name = "added_at", nullable = false, updatable = false)
  private LocalDateTime addedAt;
}
