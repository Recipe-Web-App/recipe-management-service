package com.recipe_manager.model.dto.recipe;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.recipe_manager.model.dto.media.RecipeTagMediaDto;

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
 * Data Transfer Object for RecipeTag entity. Used for transferring recipe tag data between layers.
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class RecipeTagDto {
  /** The recipe ID. */
  private Long recipeId;

  /** The tag ID. */
  private Long tagId;

  /** The tag name. */
  private String name;

  /** The creation timestamp. */
  private LocalDateTime createdAt;

  /** The last update timestamp. */
  private LocalDateTime updatedAt;

  /** The list of media associated with this tag. */
  @Valid @Default private List<RecipeTagMediaDto> media = new ArrayList<>();
}
