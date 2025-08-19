package com.recipe_manager.model.dto.response;

import java.util.List;

import com.recipe_manager.model.dto.recipe.RecipeTagDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** Response DTO for tag operations. */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public final class TagResponse {
  /** The recipe ID. */
  private Long recipeId;

  /** The list of tags for the recipe. */
  private List<RecipeTagDto> tags;
}
