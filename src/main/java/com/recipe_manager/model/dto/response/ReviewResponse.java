package com.recipe_manager.model.dto.response;

import java.util.List;

import com.recipe_manager.model.dto.review.ReviewDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public final class ReviewResponse {

  /** ID of the recipe these reviews belong to. */
  private Long recipeId;

  /** List of reviews for the recipe. */
  private List<ReviewDto> reviews;
}
