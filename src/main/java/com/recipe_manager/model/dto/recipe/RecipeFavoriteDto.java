package com.recipe_manager.model.dto.recipe;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Data Transfer Object for RecipeFavorite entity. Used for transferring recipe favorite data
 * between layers.
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class RecipeFavoriteDto {
  /** The recipe ID. */
  private Long recipeId;

  /** The user ID. */
  private UUID userId;

  /** The timestamp when the recipe was favorited. */
  private LocalDateTime favoritedAt;
}
