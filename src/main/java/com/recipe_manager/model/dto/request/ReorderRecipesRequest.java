package com.recipe_manager.model.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Request DTO for batch reordering recipes in a collection. */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public final class ReorderRecipesRequest {
  /** List of recipe order updates. */
  @NotNull(message = "Recipes list is required")
  @NotEmpty(message = "Recipes list must not be empty")
  @Valid
  private List<RecipeOrder> recipes;

  /** Represents a recipe and its new display order. */
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  public static class RecipeOrder {
    /** The recipe ID. */
    @NotNull(message = "Recipe ID is required")
    private Long recipeId;

    /** The new display order. */
    @NotNull(message = "Display order is required")
    @Min(value = 1, message = "Display order must be at least 1")
    private Integer displayOrder;
  }
}
