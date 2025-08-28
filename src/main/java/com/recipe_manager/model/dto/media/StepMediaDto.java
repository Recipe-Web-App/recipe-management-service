package com.recipe_manager.model.dto.media;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Data Transfer Object for StepMedia entity. Represents the relationship between recipe steps and
 * media with basic media information.
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public final class StepMediaDto {
  /** The media ID. */
  @NotNull private Long mediaId;

  /** The step ID. */
  @NotNull private Long stepId;

  /** Basic media information. */
  @Valid private MediaSummaryDto media;
}
