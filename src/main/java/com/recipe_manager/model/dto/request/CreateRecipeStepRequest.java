package com.recipe_manager.model.dto.request;

import java.util.ArrayList;
import java.util.List;

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

/** Request DTO for creating a recipe step. Contains step data for recipe creation. */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
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
  @Valid @Default private List<String> mediaUrls = new ArrayList<>();
}
