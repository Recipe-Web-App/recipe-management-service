package com.recipe_manager.model.dto.media;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Data Transfer Object for RecipeFavorite media. Used for transferring recipe favorite media data
 * between layers.
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode
@ToString
public final class RecipeFavoriteMediaDto {
  /** The media ID. */
  private Long mediaId;

  /** The recipe ID. */
  private Long recipeId;

  /** The user ID. */
  private String userId;

  /** The media details. */
  private MediaDto media;
}
