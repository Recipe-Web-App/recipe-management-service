package com.recipe_manager.model.dto.recipe;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.recipe_manager.model.dto.media.RecipeIngredientMediaDto;
import com.recipe_manager.model.enums.IngredientUnit;

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
 * Data Transfer Object for RecipeIngredient entity. Used for transferring recipe ingredient data
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
public final class RecipeIngredientDto {
  /** The recipe ID. */
  private Long recipeId;

  /** The ingredient ID. */
  private Long ingredientId;

  /** The ingredient name. */
  private String ingredientName;

  /** The quantity of the ingredient. */
  private BigDecimal quantity;

  /** The unit of measurement. */
  private IngredientUnit unit;

  /** Whether the ingredient is optional. */
  private Boolean isOptional;

  /** Notes about the ingredient. */
  private String notes;

  /** The creation timestamp. */
  private LocalDateTime createdAt;

  /** The last update timestamp. */
  private LocalDateTime updatedAt;

  /** The list of media associated with this ingredient. */
  @Valid @Default private List<RecipeIngredientMediaDto> media = new ArrayList<>();
}
