package com.recipe_manager.model.entity.ingredient;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Entity representing an ingredient comment. */
@Entity
@Table(name = "ingredient_comments", schema = "recipe_manager")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredientComment {

  /** The unique identifier for the comment. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "comment_id")
  private Long commentId;

  /** The ingredient this comment belongs to. */
  @ManyToOne
  @JoinColumn(name = "ingredient_id", nullable = false)
  private Ingredient ingredient;

  /** The recipe ID this comment belongs to. */
  @Column(name = "recipe_id", nullable = false)
  private Long recipeId;

  /** The user who created this comment. */
  @Column(name = "user_id", nullable = false)
  private UUID userId;

  /** The text content of the comment. */
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
  protected void onCreate() {
    final LocalDateTime now = LocalDateTime.now();
    createdAt = now;
    updatedAt = now;
  }

  /** Update timestamp before updating. */
  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }
}
