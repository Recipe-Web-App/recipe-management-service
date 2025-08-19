package com.recipe_manager.model.dto.recipe;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** DTO representing a step comment. */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StepCommentDto {

  /** The unique identifier for the comment. */
  private Long commentId;

  /** The recipe ID this comment belongs to. */
  private Long recipeId;

  /** The step ID this comment belongs to. */
  private Long stepId;

  /** The ID of the user who created this comment. */
  private UUID userId;

  /** The text content of the comment. */
  private String commentText;

  /** Whether this comment is public or private. */
  private Boolean isPublic;

  /** When this comment was created. */
  private LocalDateTime createdAt;

  /** When this comment was last updated. */
  private LocalDateTime updatedAt;
}
