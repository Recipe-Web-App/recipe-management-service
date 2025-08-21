package com.recipe_manager.model.dto.response;

import java.util.List;

import com.recipe_manager.model.dto.recipe.RecipeRevisionDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Response DTO for step revisions endpoint. Contains revision history for a specific recipe step.
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public final class StepRevisionsResponse {
  /** The recipe ID. */
  private Long recipeId;

  /** The step ID. */
  private Long stepId;

  /** The list of revisions for the step. */
  private List<RecipeRevisionDto> revisions;

  /** The total number of revisions. */
  private Integer totalCount;
}
