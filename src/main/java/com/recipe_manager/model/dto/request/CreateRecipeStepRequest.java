package com.recipe_manager.model.dto.request;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import jakarta.validation.Valid;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Request DTO for creating a recipe step. Contains step data for recipe creation.
 *
 * <p>WARNING: The all-args constructor has many parameters. Consider using the builder for clarity.
 */
@Getter
@Setter
@NoArgsConstructor
@Builder
@ToString
public final class CreateRecipeStepRequest {

  /** The step number. */
  private Integer stepNumber;

  /** The instruction for this step. */
  private String instruction;

  /** Whether this step is optional. */
  private Boolean optional;

  /** Timer in seconds for this step. */
  private Integer timerSeconds;

  /** The list of media URLs for this step. */
  @Valid private List<String> mediaUrls;

  /** Builder for CreateRecipeStepRequest. Use to construct instances with clarity and safety. */
  public static class CreateRecipeStepRequestBuilder {
    /**
     * Sets the media URLs list.
     *
     * @param mediaUrls the list of media URLs
     * @return this builder
     */
    public CreateRecipeStepRequestBuilder mediaUrls(final List<String> mediaUrls) {
      this.mediaUrls = mediaUrls == null ? new ArrayList<>() : new ArrayList<>(mediaUrls);
      return this;
    }
  }

  /**
   * Returns an unmodifiable list of media URLs.
   *
   * @return the media URLs list
   */
  public List<String> getMediaUrls() {
    return Collections.unmodifiableList(mediaUrls);
  }

  /**
   * Sets the media URLs list.
   *
   * @param mediaUrls the list of media URLs
   */
  public void setMediaUrls(final List<String> mediaUrls) {
    this.mediaUrls = mediaUrls != null ? new ArrayList<>(mediaUrls) : new ArrayList<>();
  }

  /**
   * All-args constructor with defensive copying for mutable fields.
   *
   * <p>WARNING: This constructor has many parameters. Prefer using the builder for clarity.
   *
   * @param stepNumber the step number
   * @param instruction the instruction
   * @param optional whether the step is optional
   * @param timerSeconds the timer in seconds
   * @param mediaUrls the media URLs list
   */
  public CreateRecipeStepRequest(
      final Integer stepNumber,
      final String instruction,
      final Boolean optional,
      final Integer timerSeconds,
      final List<String> mediaUrls) {
    this.stepNumber = stepNumber;
    this.instruction = instruction;
    this.optional = optional;
    this.timerSeconds = timerSeconds;
    this.mediaUrls = mediaUrls != null ? new ArrayList<>(mediaUrls) : new ArrayList<>();
  }

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
    final CreateRecipeStepRequest that = (CreateRecipeStepRequest) obj;
    return Objects.equals(stepNumber, that.stepNumber)
        && Objects.equals(instruction, that.instruction)
        && Objects.equals(optional, that.optional)
        && Objects.equals(timerSeconds, that.timerSeconds)
        && Objects.equals(mediaUrls, that.mediaUrls);
  }

  /**
   * Computes hash code based on all fields.
   *
   * @return the hash code
   */
  @Override
  public int hashCode() {
    return Objects.hash(stepNumber, instruction, optional, timerSeconds, mediaUrls);
  }
}
