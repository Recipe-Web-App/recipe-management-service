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

  private Long reviewId;

  private Long recipeId;

  private UUID userId;

  private BigDecimal rating;

  private String comment;

  private LocalDateTime createdAt;
}
