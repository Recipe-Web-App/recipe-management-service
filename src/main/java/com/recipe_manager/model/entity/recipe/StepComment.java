package com.recipe_manager.model.entity.recipe;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a comment on a recipe step. Maps to the step_comments table in the database.
 */
@Entity
@Table(name = "step_comments", schema = "recipe_manager")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StepComment {

  /** The unique identifier for the comment. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "comment_id")
  private Long commentId;

  /** The recipe ID this comment belongs to. */
  @NotNull
  @Column(name = "recipe_id", nullable = false)
  private Long recipeId;

  /** The step this comment belongs to. */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "step_id", nullable = false)
  private RecipeStep step;

  /** The user who created this comment. */
  @NotNull
  @Column(name = "user_id", nullable = false)
  private UUID userId;

  /** The text content of the comment. */
  @NotBlank
  @Column(name = "comment_text", nullable = false, columnDefinition = "TEXT")
  private String commentText;

  /** Whether this comment is public or private. */
  @Column(name = "is_public")
  @Builder.Default
  private Boolean isPublic = true;

  /** When this comment was created. */
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  /** When this comment was last updated. */
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  /** Set timestamps before persisting. */
  @PrePersist
  public void onCreate() {
    final LocalDateTime now = LocalDateTime.now();
    createdAt = now;
    updatedAt = now;
  }

  /** Update timestamp before updating. */
  @PreUpdate
  public void onUpdate() {
    updatedAt = LocalDateTime.now();
  }
}
