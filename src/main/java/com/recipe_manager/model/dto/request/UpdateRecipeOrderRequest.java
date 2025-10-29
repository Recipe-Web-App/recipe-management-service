package com.recipe_manager.model.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Request DTO for updating the display order of a recipe in a collection. */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public final class UpdateRecipeOrderRequest {
  /** The new display order position. */
  @NotNull(message = "Display order is required")
  @Min(value = 1, message = "Display order must be at least 1")
  private Integer displayOrder;
}
