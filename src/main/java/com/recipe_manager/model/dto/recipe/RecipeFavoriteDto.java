package com.recipe_manager.model.dto.recipe;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.recipe_manager.model.dto.media.RecipeFavoriteMediaDto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
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

  /** The creation timestamp. */
  private LocalDateTime createdAt;

  /** The last update timestamp. */
  private LocalDateTime updatedAt;

  /** The list of media associated with this favorite. */
  @Valid @Default private List<RecipeFavoriteMediaDto> media = new ArrayList<>();
}
