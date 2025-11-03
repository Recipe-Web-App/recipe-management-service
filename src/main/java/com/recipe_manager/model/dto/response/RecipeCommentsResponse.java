package com.recipe_manager.model.dto.response;

import java.util.List;

import com.recipe_manager.model.dto.recipe.RecipeCommentDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Response DTO for recipe comment operations. */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public final class RecipeCommentsResponse {
  /** The recipe ID. */
  private Long recipeId;

  /** The list of comments for the recipe. */
  private List<RecipeCommentDto> comments;
}
