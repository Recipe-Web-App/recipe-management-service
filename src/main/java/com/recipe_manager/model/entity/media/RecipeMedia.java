package com.recipe_manager.model.entity.media;

import com.recipe_manager.model.entity.recipe.Recipe;

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
 * Entity representing the relationship between recipes and media. Maps to the recipe_media table in
 * the database.
 */
@Entity
@Table(name = "recipe_media", schema = "recipe_manager")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class RecipeMedia {
  /** The composite ID for this relationship. */
  @EmbeddedId private RecipeMediaId id;

  /** The media entity. */
  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("mediaId")
  @JoinColumn(name = "media_id", nullable = false)
  private Media media;

  /** The recipe entity. */
  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("recipeId")
  @JoinColumn(name = "recipe_id", nullable = false)
  private Recipe recipe;
}
