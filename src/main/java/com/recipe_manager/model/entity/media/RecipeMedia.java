package com.recipe_manager.model.entity.media;

import com.recipe_manager.model.entity.recipe.Recipe;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@IdClass(RecipeMediaId.class)
public class RecipeMedia {

  /** The media ID part of the composite primary key. */
  @Id
  @Column(name = "media_id")
  private Long mediaId;

  /** The recipe ID part of the composite primary key. */
  @Id
  @Column(name = "recipe_id")
  private Long recipeId;

  /** The media entity. */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "media_id", insertable = false, updatable = false)
  private Media media;

  /** The recipe entity. */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "recipe_id", insertable = false, updatable = false)
  private Recipe recipe;
}
