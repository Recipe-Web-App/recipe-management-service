package com.recipe_manager.unit_tests.model.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.recipe_manager.model.dto.request.DeleteStepCommentRequest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@Tag("unit")
class DeleteStepCommentRequestTest {

  private DeleteStepCommentRequest request;
  private Validator validator;

  @BeforeEach
  void setUp() {
    request = DeleteStepCommentRequest.builder()
        .commentId(1L)
        .build();

    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  void testBuilder() {
    DeleteStepCommentRequest newRequest = DeleteStepCommentRequest.builder()
        .commentId(2L)
        .build();

    assertThat(newRequest.getCommentId()).isEqualTo(2L);
  }

  @Test
  void testGettersAndSetters() {
    request.setCommentId(5L);

    assertThat(request.getCommentId()).isEqualTo(5L);
  }

  @Test
  void testValidRequest() {
    Set<ConstraintViolation<DeleteStepCommentRequest>> violations = validator.validate(request);

    assertThat(violations).isEmpty();
  }

  @Test
  void testNullCommentIdValidation() {
    request.setCommentId(null);

    Set<ConstraintViolation<DeleteStepCommentRequest>> violations = validator.validate(request);

    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage()).isEqualTo("Comment ID cannot be null");
  }

  @Test
  void testEqualsAndHashCode() {
    DeleteStepCommentRequest request1 = DeleteStepCommentRequest.builder()
        .commentId(1L)
        .build();

    DeleteStepCommentRequest request2 = DeleteStepCommentRequest.builder()
        .commentId(1L)
        .build();

    assertThat(request1).isEqualTo(request2);
    assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
  }

  @Test
  void testToString() {
    String toStringResult = request.toString();

    assertThat(toStringResult).contains("DeleteStepCommentRequest");
    assertThat(toStringResult).contains("commentId=" + request.getCommentId());
  }

  @Test
  void testNoArgsConstructor() {
    DeleteStepCommentRequest newRequest = new DeleteStepCommentRequest();

    assertThat(newRequest.getCommentId()).isNull();
  }

  @Test
  void testAllArgsConstructor() {
    DeleteStepCommentRequest newRequest = new DeleteStepCommentRequest(1L);

    assertThat(newRequest.getCommentId()).isEqualTo(1L);
  }

  @Test
  void testValidZeroCommentId() {
    request.setCommentId(0L);

    Set<ConstraintViolation<DeleteStepCommentRequest>> violations = validator.validate(request);

    assertThat(violations).isEmpty();
  }

  @Test
  void testValidNegativeCommentId() {
    request.setCommentId(-1L);

    Set<ConstraintViolation<DeleteStepCommentRequest>> violations = validator.validate(request);

    assertThat(violations).isEmpty();
  }

  @Test
  void testValidLargeCommentId() {
    request.setCommentId(Long.MAX_VALUE);

    Set<ConstraintViolation<DeleteStepCommentRequest>> violations = validator.validate(request);

    assertThat(violations).isEmpty();
  }

  @Test
  void testNotEqualsWithDifferentCommentId() {
    DeleteStepCommentRequest request1 = DeleteStepCommentRequest.builder()
        .commentId(1L)
        .build();

    DeleteStepCommentRequest request2 = DeleteStepCommentRequest.builder()
        .commentId(2L)
        .build();

    assertThat(request1).isNotEqualTo(request2);
    assertThat(request1.hashCode()).isNotEqualTo(request2.hashCode());
  }
}
