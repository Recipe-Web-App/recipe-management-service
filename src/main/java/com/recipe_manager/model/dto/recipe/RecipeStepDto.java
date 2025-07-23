package com.recipe_manager.model.dto.recipe;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.recipe_manager.model.dto.media.RecipeStepMediaDto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Data Transfer Object for RecipeStep entity. Used for transferring recipe step data between
 * layers.
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public final class RecipeStepDto {
  /** The step ID. */
  private Long stepId;

  /** The recipe ID. */
  private Long recipeId;

  /** The step number. */
  private Integer stepNumber;

  /** The instruction for this step. */
  private String instruction;

  /** Whether this step is optional. */
  private Boolean optional;

  /** Timer in seconds for this step. */
  private Integer timerSeconds;

  /** The creation timestamp. */
  private LocalDateTime createdAt;

  /** The last update timestamp. */
  private LocalDateTime updatedAt;

  /** The list of media associated with this step. */
  @Valid @Default private List<RecipeStepMediaDto> media = new ArrayList<>();
}
