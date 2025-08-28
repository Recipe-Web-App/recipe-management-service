package com.recipe_manager.model.dto.media;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Data Transfer Object for IngredientMedia entity. Represents the relationship between recipe
 * ingredients and media with basic media information.
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public final class IngredientMediaDto {
  /** The media ID. */
  @NotNull private Long mediaId;

  /** The recipe ID. */
  @NotNull private Long recipeId;

  /** The ingredient ID. */
  @NotNull private Long ingredientId;

  /** Basic media information. */
  @Valid private MediaSummaryDto media;
}
