package com.recipe_manager.model.dto.review;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewDto {

  /** Unique identifier for the review. */
  private Long reviewId;

  /** ID of the recipe being reviewed. */
  private Long recipeId;

  /** ID of the user who created the review. */
  private UUID userId;

  /** Rating given to the recipe (1-5 scale). */
  private BigDecimal rating;

  /** Optional comment text for the review. */
  private String comment;

  /** Timestamp when the review was created. */
  private LocalDateTime createdAt;
}
