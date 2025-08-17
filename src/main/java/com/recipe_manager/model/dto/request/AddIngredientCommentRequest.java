package com.recipe_manager.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** Request DTO for adding a comment to an ingredient. */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public final class AddIngredientCommentRequest {
  /** The comment text to add. */
  @NotBlank(message = "Comment text cannot be blank")
  private String comment;
}
