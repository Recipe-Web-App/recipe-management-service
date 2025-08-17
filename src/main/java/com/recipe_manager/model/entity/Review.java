package com.recipe_manager.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import com.recipe_manager.model.entity.recipe.Recipe;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entity representing a review for a recipe in the system. Maps to the reviews table in the
 * database.
 */
@Entity
@Table(name = "reviews", schema = "recipe_manager")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"recipe"})
@ToString(exclude = {"recipe"})
public class Review {

  /** The unique ID of the review. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "review_id")
  private Long reviewId;

  /** The recipe this review belongs to. */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "recipe_id", nullable = false)
  @NotNull
  private Recipe recipe;

  /** The user ID of the reviewer. */
  @NotNull
  @Column(name = "user_id", nullable = false)
  private UUID userId;

  /** The rating given by the reviewer. */
  @NotNull(message = "Rating cannot be null")
  @DecimalMin(value = "0.0", message = "Rating must be at least 0.0")
  @DecimalMax(value = "5.0", message = "Rating must be at most 5.0")
  @Digits(
      integer = 1,
      fraction = 1,
      message = "Rating must have at most 1 integer digit and 1 fractional digit")
  @Column(name = "rating", nullable = false, precision = 2, scale = 1)
  private BigDecimal rating;

  /** The optional comment from the reviewer. */
  @Column(name = "comment", columnDefinition = "text")
  private String comment;

  /** The creation timestamp. */
  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;
}
