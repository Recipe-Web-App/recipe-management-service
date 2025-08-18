package com.recipe_manager.unit_tests.model.dto.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.recipe_manager.model.dto.request.AddReviewRequest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@Tag("unit")
class AddReviewRequestTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  @Tag("standard-processing")
  void shouldCreateAddReviewRequestWithBuilder() {
    final BigDecimal rating = new BigDecimal("4.5");
    final String comment = "Great recipe!";

    final AddReviewRequest request = AddReviewRequest.builder()
        .rating(rating)
        .comment(comment)
        .build();

    assertNotNull(request);
    assertEquals(rating, request.getRating());
    assertEquals(comment, request.getComment());
  }

  @Test
  @Tag("standard-processing")
  void shouldCreateAddReviewRequestWithAllArgsConstructor() {
    final BigDecimal rating = new BigDecimal("3.0");
    final String comment = "Okay recipe";

    final AddReviewRequest request = new AddReviewRequest(rating, comment);

    assertNotNull(request);
    assertEquals(rating, request.getRating());
    assertEquals(comment, request.getComment());
  }

  @Test
  @Tag("standard-processing")
  void shouldCreateAddReviewRequestWithNoArgsConstructor() {
    final AddReviewRequest request = new AddReviewRequest();

    assertNotNull(request);
  }

  @Test
  @Tag("standard-processing")
  void shouldSetAndGetFields() {
    final AddReviewRequest request = new AddReviewRequest();
    final BigDecimal rating = new BigDecimal("5.0");
    final String comment = "Excellent!";

    request.setRating(rating);
    request.setComment(comment);

    assertEquals(rating, request.getRating());
    assertEquals(comment, request.getComment());
  }

  @Test
  @Tag("standard-processing")
  void shouldTestEqualsAndHashCode() {
    final BigDecimal rating = new BigDecimal("2.5");
    final String comment = "Needs improvement";

    final AddReviewRequest request1 = AddReviewRequest.builder()
        .rating(rating)
        .comment(comment)
        .build();

    final AddReviewRequest request2 = AddReviewRequest.builder()
        .rating(rating)
        .comment(comment)
        .build();

    final AddReviewRequest request3 = AddReviewRequest.builder()
        .rating(new BigDecimal("1.5"))
        .comment(comment)
        .build();

    assertEquals(request1, request2);
    assertEquals(request1.hashCode(), request2.hashCode());
    assertNotEquals(request1, request3);
    assertNotEquals(request1.hashCode(), request3.hashCode());
  }

  @Test
  @Tag("standard-processing")
  void shouldTestToString() {
    final AddReviewRequest request = AddReviewRequest.builder()
        .rating(new BigDecimal("1.5"))
        .comment("Poor recipe")
        .build();

    final String toString = request.toString();

    assertNotNull(toString);
    assertEquals(toString, request.toString());
  }

  @Test
  @Tag("standard-processing")
  void shouldValidateValidRatings() {
    final String[] validRatings = {"0.0", "0.5", "1.0", "1.5", "2.0", "2.5", "3.0", "3.5", "4.0", "4.5", "5.0"};

    for (final String ratingStr : validRatings) {
      final AddReviewRequest request = AddReviewRequest.builder()
          .rating(new BigDecimal(ratingStr))
          .comment("Test comment")
          .build();

      final Set<ConstraintViolation<AddReviewRequest>> violations = validator.validate(request);
      assertTrue(violations.isEmpty(), "Rating " + ratingStr + " should be valid");
    }
  }

  @Test
  @Tag("standard-processing")
  void shouldValidateWithoutComment() {
    final AddReviewRequest request = AddReviewRequest.builder()
        .rating(new BigDecimal("4.0"))
        .build();

    final Set<ConstraintViolation<AddReviewRequest>> violations = validator.validate(request);
    assertTrue(violations.isEmpty());
  }

  @Test
  @Tag("error-processing")
  void shouldFailValidationForNullRating() {
    final AddReviewRequest request = AddReviewRequest.builder()
        .comment("Test comment")
        .build();

    final Set<ConstraintViolation<AddReviewRequest>> violations = validator.validate(request);
    assertFalse(violations.isEmpty());
    assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Rating cannot be null")));
  }

  @Test
  @Tag("error-processing")
  void shouldFailValidationForRatingBelowMinimum() {
    final AddReviewRequest request = AddReviewRequest.builder()
        .rating(new BigDecimal("-0.5"))
        .comment("Test comment")
        .build();

    final Set<ConstraintViolation<AddReviewRequest>> violations = validator.validate(request);
    assertFalse(violations.isEmpty());
    assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Rating must be at least 0.0")));
  }

  @Test
  @Tag("error-processing")
  void shouldFailValidationForRatingAboveMaximum() {
    final AddReviewRequest request = AddReviewRequest.builder()
        .rating(new BigDecimal("5.5"))
        .comment("Test comment")
        .build();

    final Set<ConstraintViolation<AddReviewRequest>> violations = validator.validate(request);
    assertFalse(violations.isEmpty());
    assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Rating must be at most 5.0")));
  }

  @Test
  @Tag("error-processing")
  void shouldFailValidationForInvalidDigitFormat() {
    final AddReviewRequest request = AddReviewRequest.builder()
        .rating(new BigDecimal("12.345"))
        .comment("Test comment")
        .build();

    final Set<ConstraintViolation<AddReviewRequest>> violations = validator.validate(request);
    assertFalse(violations.isEmpty());
    assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Rating must have at most 1 integer digit and 1 fractional digit")));
  }
}
