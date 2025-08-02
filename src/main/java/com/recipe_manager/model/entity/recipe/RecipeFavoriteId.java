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
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class RecipeFavoriteId implements Serializable {
  /** Serial version UID for ensuring compatibility during serialization. */
  private static final long serialVersionUID = 1L;

  /** The user ID. */
  private UUID userId;

  /** The recipe ID. */
  private Long recipeId;
}
