package com.recipe_manager.model.dto.recipe;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.recipe_manager.model.dto.media.RecipeStepMediaDto;

import jakarta.validation.Valid;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Data Transfer Object for RecipeStep entity. Used for transferring recipe step data between
 * layers.
 *
 * <p>WARNING: The all-args constructor has many parameters. Consider using the builder for clarity.
 */
@Getter
@Setter
@NoArgsConstructor
@Builder
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
  @Valid private List<RecipeStepMediaDto> media;

  /** Builder for RecipeStepDto. Use to construct instances with clarity and safety. */
  public static final class RecipeStepDtoBuilder {
    /**
     * Sets the media list.
     *
     * @param media the list of media
     * @return this builder
     */
    public RecipeStepDtoBuilder media(final List<RecipeStepMediaDto> media) {
      this.media = media == null ? new ArrayList<>() : new ArrayList<>(media);
      return this;
    }
  }

  /**
   * Returns an unmodifiable list of media.
   *
   * @return the media list
   */
  public List<RecipeStepMediaDto> getMedia() {
    return Collections.unmodifiableList(media);
  }

  /**
   * Sets the media list.
   *
   * @param media the list of media
   */
  public void setMedia(final List<RecipeStepMediaDto> media) {
    this.media = media != null ? new ArrayList<>(media) : new ArrayList<>();
  }

  /**
   * All-args constructor with defensive copying for mutable fields.
   *
   * <p>WARNING: This constructor has many parameters. Prefer using the builder for clarity.
   *
   * @param stepId the step ID
   * @param recipeId the recipe ID
   * @param stepNumber the step number
   * @param instruction the instruction
   * @param optional whether the step is optional
   * @param timerSeconds the timer in seconds
   * @param createdAt the creation timestamp
   * @param updatedAt the update timestamp
   * @param media the media list
   */
  // CHECKSTYLE:OFF: ParameterNumber
  public RecipeStepDto(
      final Long stepId,
      final Long recipeId,
      final Integer stepNumber,
      final String instruction,
      final Boolean optional,
      final Integer timerSeconds,
      final LocalDateTime createdAt,
      final LocalDateTime updatedAt,
      final List<RecipeStepMediaDto> media) {
    this.stepId = stepId;
    this.recipeId = recipeId;
    this.stepNumber = stepNumber;
    this.instruction = instruction;
    this.optional = optional;
    this.timerSeconds = timerSeconds;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.media = media != null ? new ArrayList<>(media) : new ArrayList<>();
  }

  // CHECKSTYLE:ON: ParameterNumber

  /**
   * Checks equality based on all fields.
   *
   * @param obj the object to compare
   * @return true if equal, false otherwise
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final RecipeStepDto that = (RecipeStepDto) obj;
    return Objects.equals(stepId, that.stepId)
        && Objects.equals(recipeId, that.recipeId)
        && Objects.equals(stepNumber, that.stepNumber)
        && Objects.equals(instruction, that.instruction)
        && Objects.equals(optional, that.optional)
        && Objects.equals(timerSeconds, that.timerSeconds)
        && Objects.equals(createdAt, that.createdAt)
        && Objects.equals(updatedAt, that.updatedAt)
        && Objects.equals(media, that.media);
  }

  /**
   * Computes hash code based on all fields.
   *
   * @return the hash code
   */
  @Override
  public int hashCode() {
    return Objects.hash(
        stepId,
        recipeId,
        stepNumber,
        instruction,
        optional,
        timerSeconds,
        createdAt,
        updatedAt,
        media);
  }
}
