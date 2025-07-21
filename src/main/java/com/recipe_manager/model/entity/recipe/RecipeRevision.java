package com.recipe_manager.model.entity.recipe;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import com.recipe_manager.model.enums.RevisionCategory;
import com.recipe_manager.model.enums.RevisionType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entity representing a revision to a recipe. Maps to the recipe_revisions table in the database.
 *
 * <p>WARNING: The all-args constructor has many parameters. Consider using the builder for clarity.
 */
@Entity
@Table(name = "recipe_revisions", schema = "recipe_manager")
@Getter
@Setter
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class RecipeRevision {

  /** The unique ID of the revision. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "revision_id")
  private Long revisionId;

  /** The recipe entity this revision belongs to. */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "recipe_id", nullable = false)
  private Recipe recipe;

  /** The user ID who made the revision. */
  @NotNull
  @Column(name = "user_id", nullable = false)
  private UUID userId;

  /** The revision category. */
  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "revision_category", nullable = false)
  private RevisionCategory revisionCategory;

  /** The revision type. */
  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "revision_type", nullable = false)
  private RevisionType revisionType;

  /** The previous data in JSON. */
  @NotNull
  @Column(name = "previous_data", columnDefinition = "jsonb", nullable = false)
  private String previousData;

  /** The new data in JSON. */
  @NotNull
  @Column(name = "new_data", columnDefinition = "jsonb", nullable = false)
  private String newData;

  /** The change comment. */
  @Column(name = "change_comment", columnDefinition = "text")
  private String changeComment;

  /** The creation timestamp. */
  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  /**
   * Returns the recipe entity.
   *
   * <p>Override this method with care if you subclass RecipeRevision. Document extension safety.
   *
   * @return the recipe
   */
  public Recipe getRecipe() {
    return recipe == null ? null : new Recipe(recipe);
  }

  /**
   * Sets the recipe entity.
   *
   * <p>Override this method with care if you subclass RecipeRevision. Document extension safety.
   *
   * @param recipe the recipe
   */
  public void setRecipe(final Recipe recipe) {
    this.recipe = recipe == null ? null : new Recipe(recipe);
  }

  /**
   * All-args constructor for RecipeRevision.
   *
   * <p>WARNING: This constructor has many parameters. Prefer using the builder for clarity.
   *
   * @param revisionId the revision ID
   * @param recipe the recipe
   * @param userId the user ID
   * @param revisionCategory the revision category
   * @param revisionType the revision type
   * @param previousData the previous data
   * @param newData the new data
   * @param changeComment the change comment
   * @param createdAt the creation timestamp
   */
  // CHECKSTYLE:OFF: ParameterNumber
  public RecipeRevision(
      final Long revisionId,
      final Recipe recipe,
      final UUID userId,
      final RevisionCategory revisionCategory,
      final RevisionType revisionType,
      final String previousData,
      final String newData,
      final String changeComment,
      final LocalDateTime createdAt) {
    this.revisionId = revisionId;
    this.recipe = recipe == null ? null : new Recipe(recipe);
    this.userId = userId;
    this.revisionCategory = revisionCategory;
    this.revisionType = revisionType;
    this.previousData = previousData;
    this.newData = newData;
    this.changeComment = changeComment;
    this.createdAt = createdAt;
  }

  // CHECKSTYLE:ON: ParameterNumber

  /** Builder for RecipeRevision. Use to construct instances with clarity and safety. */
  public static class RecipeRevisionBuilder {
    /**
     * Sets the recipe.
     *
     * @param recipe the recipe
     * @return this builder
     */
    public RecipeRevisionBuilder recipe(final Recipe recipe) {
      this.recipe = recipe == null ? null : new Recipe(recipe);
      return this;
    }
  }
}
