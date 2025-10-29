package com.recipe_manager.model.entity.collection;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** Composite primary key for RecipeCollectionItem entity. */
@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class RecipeCollectionItemId implements Serializable {
  /** Serial version UID for ensuring compatibility during serialization. */
  private static final long serialVersionUID = 1L;

  /** The collection ID. */
  @Column(name = "collection_id")
  private Long collectionId;

  /** The recipe ID. */
  @Column(name = "recipe_id")
  private Long recipeId;
}
