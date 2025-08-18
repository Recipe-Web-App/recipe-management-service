package com.recipe_manager.unit_tests.model.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.recipe_manager.model.entity.Review;
import com.recipe_manager.model.entity.recipe.Recipe;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

/**
 * Unit tests for {@link Review} entity.
 */
@Tag("unit")
class ReviewTest {

  private Validator validator;
  private Recipe recipe;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();

    recipe = Recipe.builder()
        .recipeId(1L)
        .userId(UUID.randomUUID())
        .title("Test Recipe")
        .build();
  }

  @Test
  @DisplayName("Should create Review with builder pattern")
  void shouldCreateReviewWithBuilder() {
    UUID userId = UUID.randomUUID();
    BigDecimal rating = new BigDecimal("4.5");
    String comment = "Great recipe!";
    LocalDateTime now = LocalDateTime.now();

    Review review = Review.builder()
        .reviewId(1L)
        .recipe(recipe)
        .userId(userId)
        .rating(rating)
        .comment(comment)
        .createdAt(now)
        .build();

    assertEquals(1L, review.getReviewId());
    assertEquals(recipe, review.getRecipe());
    assertEquals(userId, review.getUserId());
    assertEquals(rating, review.getRating());
    assertEquals(comment, review.getComment());
    assertEquals(now, review.getCreatedAt());
  }

  @Test
  @DisplayName("Should create Review with no-args constructor")
  void shouldCreateReviewWithNoArgsConstructor() {
    Review review = new Review();

    assertNull(review.getReviewId());
    assertNull(review.getRecipe());
    assertNull(review.getUserId());
    assertNull(review.getRating());
    assertNull(review.getComment());
    assertNull(review.getCreatedAt());
  }

  @Test
  @DisplayName("Should create Review with all-args constructor")
  void shouldCreateReviewWithAllArgsConstructor() {
    UUID userId = UUID.randomUUID();
    BigDecimal rating = new BigDecimal("3.0");
    String comment = "Good recipe";
    LocalDateTime now = LocalDateTime.now();

    Review review = new Review(1L, recipe, userId, rating, comment, now);

    assertEquals(1L, review.getReviewId());
    assertEquals(recipe, review.getRecipe());
    assertEquals(userId, review.getUserId());
    assertEquals(rating, review.getRating());
    assertEquals(comment, review.getComment());
    assertEquals(now, review.getCreatedAt());
  }

  @Test
  @DisplayName("Should set and get all properties")
  void shouldSetAndGetAllProperties() {
    Review review = new Review();
    UUID userId = UUID.randomUUID();
    BigDecimal rating = new BigDecimal("2.5");
    String comment = "Average recipe";
    LocalDateTime now = LocalDateTime.now();

    review.setReviewId(2L);
    review.setRecipe(recipe);
    review.setUserId(userId);
    review.setRating(rating);
    review.setComment(comment);
    review.setCreatedAt(now);

    assertEquals(2L, review.getReviewId());
    assertEquals(recipe, review.getRecipe());
    assertEquals(userId, review.getUserId());
    assertEquals(rating, review.getRating());
    assertEquals(comment, review.getComment());
    assertEquals(now, review.getCreatedAt());
  }

  @Test
  @DisplayName("Should validate rating is not null")
  void shouldValidateRatingNotNull() {
    Review review = Review.builder()
        .recipe(recipe)
        .userId(UUID.randomUUID())
        .rating(null)
        .build();

    Set<ConstraintViolation<Review>> violations = validator.validate(review);

    assertNotNull(violations);
    assertEquals(1, violations.size());
    ConstraintViolation<Review> violation = violations.iterator().next();
    assertEquals(NotNull.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
    assertEquals("rating", violation.getPropertyPath().toString());
  }

  @Test
  @DisplayName("Should validate rating minimum value")
  void shouldValidateRatingMinimum() {
    Review review = Review.builder()
        .recipe(recipe)
        .userId(UUID.randomUUID())
        .rating(new BigDecimal("-0.1"))
        .build();

    Set<ConstraintViolation<Review>> violations = validator.validate(review);

    assertNotNull(violations);
    assertEquals(1, violations.size());
    ConstraintViolation<Review> violation = violations.iterator().next();
    assertEquals(DecimalMin.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
  }

  @Test
  @DisplayName("Should validate rating maximum value")
  void shouldValidateRatingMaximum() {
    Review review = Review.builder()
        .recipe(recipe)
        .userId(UUID.randomUUID())
        .rating(new BigDecimal("5.1"))
        .build();

    Set<ConstraintViolation<Review>> violations = validator.validate(review);

    assertNotNull(violations);
    assertEquals(1, violations.size());
    ConstraintViolation<Review> violation = violations.iterator().next();
    assertEquals(DecimalMax.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
  }

  @Test
  @DisplayName("Should validate rating digits format")
  void shouldValidateRatingDigitsFormat() {
    Review review = Review.builder()
        .recipe(recipe)
        .userId(UUID.randomUUID())
        .rating(new BigDecimal("3.99"))
        .build();

    Set<ConstraintViolation<Review>> violations = validator.validate(review);

    assertNotNull(violations);
    assertEquals(1, violations.size());
    ConstraintViolation<Review> violation = violations.iterator().next();
    assertEquals(Digits.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
  }

  @Test
  @DisplayName("Should accept valid rating values")
  void shouldAcceptValidRatingValues() {
    BigDecimal[] validRatings = {
        new BigDecimal("0.0"),
        new BigDecimal("2.5"),
        new BigDecimal("5.0"),
        new BigDecimal("4.9")
    };

    for (BigDecimal rating : validRatings) {
      Review review = Review.builder()
          .recipe(recipe)
          .userId(UUID.randomUUID())
          .rating(rating)
          .build();

      Set<ConstraintViolation<Review>> violations = validator.validate(review);
      assertTrue(violations.isEmpty(), "Rating " + rating + " should be valid");
    }
  }

  @Test
  @DisplayName("Should validate recipe is not null")
  void shouldValidateRecipeNotNull() {
    Review review = Review.builder()
        .recipe(null)
        .userId(UUID.randomUUID())
        .rating(new BigDecimal("4.0"))
        .build();

    Set<ConstraintViolation<Review>> violations = validator.validate(review);

    assertNotNull(violations);
    assertEquals(1, violations.size());
    ConstraintViolation<Review> violation = violations.iterator().next();
    assertEquals(NotNull.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
    assertEquals("recipe", violation.getPropertyPath().toString());
  }

  @Test
  @DisplayName("Should validate userId is not null")
  void shouldValidateUserIdNotNull() {
    Review review = Review.builder()
        .recipe(recipe)
        .userId(null)
        .rating(new BigDecimal("4.0"))
        .build();

    Set<ConstraintViolation<Review>> violations = validator.validate(review);

    assertNotNull(violations);
    assertEquals(1, violations.size());
    ConstraintViolation<Review> violation = violations.iterator().next();
    assertEquals(NotNull.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
    assertEquals("userId", violation.getPropertyPath().toString());
  }

  @Test
  @DisplayName("Should allow null comment")
  void shouldAllowNullComment() {
    Review review = Review.builder()
        .recipe(recipe)
        .userId(UUID.randomUUID())
        .rating(new BigDecimal("4.0"))
        .comment(null)
        .build();

    Set<ConstraintViolation<Review>> violations = validator.validate(review);
    assertTrue(violations.isEmpty());
  }

  @Test
  @DisplayName("Should implement equals and hashCode correctly")
  void shouldImplementEqualsAndHashCodeCorrectly() {
    UUID userId = UUID.randomUUID();
    BigDecimal rating = new BigDecimal("4.0");
    LocalDateTime now = LocalDateTime.now();

    Review review1 = Review.builder()
        .reviewId(1L)
        .recipe(recipe)
        .userId(userId)
        .rating(rating)
        .comment("Great!")
        .createdAt(now)
        .build();

    Review review2 = Review.builder()
        .reviewId(1L)
        .recipe(recipe)
        .userId(userId)
        .rating(rating)
        .comment("Great!")
        .createdAt(now)
        .build();

    Review review3 = Review.builder()
        .reviewId(2L)
        .recipe(recipe)
        .userId(userId)
        .rating(rating)
        .comment("Great!")
        .createdAt(now)
        .build();

    assertEquals(review1, review2);
    assertEquals(review1.hashCode(), review2.hashCode());
    assertNotEquals(review1, review3);
    assertNotEquals(review1.hashCode(), review3.hashCode());
  }

  @Test
  @DisplayName("Should implement toString correctly")
  void shouldImplementToStringCorrectly() {
    Review review = Review.builder()
        .reviewId(1L)
        .userId(UUID.randomUUID())
        .rating(new BigDecimal("4.5"))
        .comment("Excellent!")
        .build();

    String toString = review.toString();

    assertNotNull(toString);
    assertTrue(toString.contains("Review"));
    assertTrue(toString.contains("reviewId=1"));
    assertTrue(toString.contains("rating=4.5"));
    assertTrue(toString.contains("comment=Excellent!"));
    // Recipe should be excluded from toString
    assertTrue(!toString.contains("recipe="));
  }
}
