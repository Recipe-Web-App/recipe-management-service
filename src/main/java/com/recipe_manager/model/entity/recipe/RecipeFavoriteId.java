package com.recipe_manager.model.entity.recipe;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** Composite primary key for RecipeFavorite entity. */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class RecipeFavoriteId implements Serializable {

  private static final long serialVersionUID = 1L;

  /** The user ID. */
  private UUID userId;

  /** The recipe ID. */
  private Long recipeId;

  /**
   * Copy constructor for RecipeFavoriteId.
   *
   * <p>WARNING: This constructor is for defensive copying only.
   *
   * @param other the RecipeFavoriteId to copy
   */
  public RecipeFavoriteId(final RecipeFavoriteId other) {
    this.userId = other == null ? null : other.userId;
    this.recipeId = other == null ? null : other.recipeId;
  }
}
