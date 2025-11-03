package com.recipe_manager.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Request DTO for editing a recipe comment. */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public final class EditRecipeCommentRequest {
  /** The updated comment text. */
  @NotBlank(message = "Comment text cannot be blank")
  private String commentText;
}
