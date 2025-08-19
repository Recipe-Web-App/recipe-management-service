package com.recipe_manager.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Request DTO for adding a comment to a recipe step. */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public final class AddStepCommentRequest {
  /** The comment text to add. */
  @NotBlank(message = "Comment text cannot be blank")
  private String comment;

  /** Whether this comment should be public or private. Defaults to true if not specified. */
  @Builder.Default private Boolean isPublic = true;
}
