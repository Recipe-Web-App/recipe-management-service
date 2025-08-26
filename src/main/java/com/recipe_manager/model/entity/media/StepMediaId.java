package com.recipe_manager.model.entity.media;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** Composite key for the StepMedia entity. */
@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class StepMediaId implements Serializable {
  /** Serial version UID. */
  private static final long serialVersionUID = 1L;

  /** The recipe ID. */
  @Column(name = "recipe_id")
  private Long recipeId;

  /** The step ID. */
  @Column(name = "step_id")
  private Long stepId;

  /** The media ID. */
  @Column(name = "media_id")
  private Long mediaId;
}
