package com.recipe_manager.model.entity.recipe;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entity representing a step in a recipe. Maps to the recipe_steps table in the database.
 *
 * <p>WARNING: The all-args constructor has many parameters. Consider using the builder for clarity.
 */
@Entity
@Table(name = "recipe_steps", schema = "recipe_manager")
@Getter
@Setter
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class RecipeStep {

  /** The unique ID of the step. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "step_id")
  private Long stepId;

  /** The recipe entity this step belongs to. */
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "recipe_id", nullable = false)
  private Recipe recipe;

  /** The step number in the recipe. */
  @NotNull
  @Positive
  @Column(name = "step_number", nullable = false)
  private Integer stepNumber;

  /** The instruction for this step. */
  @NotBlank
  @Column(name = "instruction", columnDefinition = "text", nullable = false)
  private String instruction;

  /** Whether this step is optional. */
  @Column(name = "optional")
  @Builder.Default
  private Boolean optional = false;

  /** Timer in seconds for this step. */
  @Column(name = "timer_seconds")
  private Integer timerSeconds;

  /** The creation timestamp. */
  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  /**
   * Returns the recipe entity.
   *
   * <p>Override this method with care if you subclass RecipeStep. Document extension safety.
   *
   * @return the recipe
   */
  public Recipe getRecipe() {
    return recipe == null ? null : new Recipe(recipe);
  }

  /**
   * Sets the recipe entity.
   *
   * <p>Override this method with care if you subclass RecipeStep. Document extension safety.
   *
   * @param recipe the recipe
   */
  public void setRecipe(final Recipe recipe) {
    this.recipe = recipe == null ? null : new Recipe(recipe);
  }

  /**
   * All-args constructor for RecipeStep.
   *
   * <p>WARNING: This constructor has many parameters. Prefer using the builder for clarity.
   *
   * @param stepId the step ID
   * @param recipe the recipe
   * @param stepNumber the step number
   * @param instruction the instruction
   * @param optional whether the step is optional
   * @param timerSeconds the timer in seconds
   * @param createdAt the creation timestamp
   */
  public RecipeStep(
      final Long stepId,
      final Recipe recipe,
      final Integer stepNumber,
      final String instruction,
      final Boolean optional,
      final Integer timerSeconds,
      final LocalDateTime createdAt) {
    this.stepId = stepId;
    this.recipe = recipe == null ? null : new Recipe(recipe);
    this.stepNumber = stepNumber;
    this.instruction = instruction;
    this.optional = optional;
    this.timerSeconds = timerSeconds;
    this.createdAt = createdAt;
  }

  /** Builder for RecipeStep. Use to construct instances with clarity and safety. */
  public static class RecipeStepBuilder {
    /**
     * Sets the recipe.
     *
     * @param recipe the recipe
     * @return this builder
     */
    public RecipeStepBuilder recipe(final Recipe recipe) {
      this.recipe = recipe == null ? null : new Recipe(recipe);
      return this;
    }
  }
}
