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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** Entity representing a step in a recipe. Maps to the recipe_steps table in the database. */
@Entity
@Table(name = "recipe_steps", schema = "recipe_manager")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
}
