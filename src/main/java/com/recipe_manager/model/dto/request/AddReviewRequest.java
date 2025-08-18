package com.recipe_manager.model.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
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
public final class AddReviewRequest {

  /** Rating for the recipe (0.0 to 5.0 scale with 1 decimal place). */
  @NotNull(message = "Rating cannot be null")
  @DecimalMin(value = "0.0", message = "Rating must be at least 0.0")
  @DecimalMax(value = "5.0", message = "Rating must be at most 5.0")
  @Digits(
      integer = 1,
      fraction = 1,
      message = "Rating must have at most 1 integer digit and 1 fractional digit")
  private BigDecimal rating;

  /** Optional comment for the review. */
  private String comment;
}
