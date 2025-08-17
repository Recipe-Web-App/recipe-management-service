package com.recipe_manager.model.dto.response;

import java.util.List;

import com.recipe_manager.model.dto.ingredient.IngredientCommentDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** Response DTO for ingredient comment operations. */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public final class IngredientCommentResponse {
  /** The recipe ID. */
  private Long recipeId;

  /** The ingredient ID. */
  private Long ingredientId;

  /** The list of comments for the ingredient. */
  private List<IngredientCommentDto> comments;
}
