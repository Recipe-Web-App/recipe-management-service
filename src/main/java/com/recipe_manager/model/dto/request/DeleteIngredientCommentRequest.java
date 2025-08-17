package com.recipe_manager.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** Request DTO for deleting a comment from an ingredient. */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public final class DeleteIngredientCommentRequest {
  /** The ID of the comment to delete. */
  @NotNull(message = "Comment ID cannot be null")
  private Long commentId;
}
