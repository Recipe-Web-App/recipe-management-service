package com.recipe_manager.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** Request DTO for editing a comment on a recipe step. */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public final class EditStepCommentRequest {
  /** The ID of the comment to edit. */
  @NotNull(message = "Comment ID cannot be null")
  private Long commentId;

  /** The new comment text. */
  @NotBlank(message = "Comment text cannot be blank")
  private String comment;
}
