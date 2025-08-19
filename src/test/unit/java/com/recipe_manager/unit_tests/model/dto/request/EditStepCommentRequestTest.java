package com.recipe_manager.unit_tests.model.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.recipe_manager.model.dto.request.EditStepCommentRequest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@Tag("unit")
class EditStepCommentRequestTest {

  private EditStepCommentRequest request;
  private Validator validator;

  @BeforeEach
  void setUp() {
    request = EditStepCommentRequest.builder()
        .commentId(1L)
        .comment("Test comment")
        .build();

    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  void testBuilder() {
    EditStepCommentRequest newRequest = EditStepCommentRequest.builder()
        .commentId(2L)
        .comment("Another test comment")
        .build();

    assertThat(newRequest.getCommentId()).isEqualTo(2L);
    assertThat(newRequest.getComment()).isEqualTo("Another test comment");
  }

  @Test
  void testGettersAndSetters() {
    request.setCommentId(5L);
    request.setComment("Updated comment");

    assertThat(request.getCommentId()).isEqualTo(5L);
    assertThat(request.getComment()).isEqualTo("Updated comment");
  }

  @Test
  void testValidRequest() {
    Set<ConstraintViolation<EditStepCommentRequest>> violations = validator.validate(request);

    assertThat(violations).isEmpty();
  }

  @Test
  void testNullCommentIdValidation() {
    request.setCommentId(null);

    Set<ConstraintViolation<EditStepCommentRequest>> violations = validator.validate(request);

    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage()).isEqualTo("Comment ID cannot be null");
  }

  @Test
  void testBlankCommentValidation() {
    request.setComment("");

    Set<ConstraintViolation<EditStepCommentRequest>> violations = validator.validate(request);

    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage()).isEqualTo("Comment text cannot be blank");
  }

  @Test
  void testNullCommentValidation() {
    request.setComment(null);

    Set<ConstraintViolation<EditStepCommentRequest>> violations = validator.validate(request);

    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage()).isEqualTo("Comment text cannot be blank");
  }

  @Test
  void testWhitespaceOnlyCommentValidation() {
    request.setComment("   ");

    Set<ConstraintViolation<EditStepCommentRequest>> violations = validator.validate(request);

    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage()).isEqualTo("Comment text cannot be blank");
  }

  @Test
  void testBothFieldsInvalidValidation() {
    request.setCommentId(null);
    request.setComment("");

    Set<ConstraintViolation<EditStepCommentRequest>> violations = validator.validate(request);

    assertThat(violations).hasSize(2);
  }

  @Test
  void testEqualsAndHashCode() {
    EditStepCommentRequest request1 = EditStepCommentRequest.builder()
        .commentId(1L)
        .comment("Test comment")
        .build();

    EditStepCommentRequest request2 = EditStepCommentRequest.builder()
        .commentId(1L)
        .comment("Test comment")
        .build();

    assertThat(request1).isEqualTo(request2);
    assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
  }

  @Test
  void testToString() {
    String toStringResult = request.toString();

    assertThat(toStringResult).contains("EditStepCommentRequest");
    assertThat(toStringResult).contains("commentId=" + request.getCommentId());
    assertThat(toStringResult).contains("comment=" + request.getComment());
  }

  @Test
  void testNoArgsConstructor() {
    EditStepCommentRequest newRequest = new EditStepCommentRequest();

    assertThat(newRequest.getCommentId()).isNull();
    assertThat(newRequest.getComment()).isNull();
  }

  @Test
  void testAllArgsConstructor() {
    EditStepCommentRequest newRequest = new EditStepCommentRequest(1L, "Test comment");

    assertThat(newRequest.getCommentId()).isEqualTo(1L);
    assertThat(newRequest.getComment()).isEqualTo("Test comment");
  }

  @Test
  void testValidCommentWithSpecialCharacters() {
    request.setComment("Comment with special chars: @#$%^&*()!");

    Set<ConstraintViolation<EditStepCommentRequest>> violations = validator.validate(request);

    assertThat(violations).isEmpty();
  }

  @Test
  void testValidLongComment() {
    String longComment = "This is a very long comment that contains many words and characters "
        + "to test that the validation does not impose unnecessary length restrictions on comments "
        + "as long as they are not blank.";
    request.setComment(longComment);

    Set<ConstraintViolation<EditStepCommentRequest>> violations = validator.validate(request);

    assertThat(violations).isEmpty();
  }

  @Test
  void testValidZeroCommentId() {
    request.setCommentId(0L);

    Set<ConstraintViolation<EditStepCommentRequest>> violations = validator.validate(request);

    assertThat(violations).isEmpty();
  }

  @Test
  void testValidNegativeCommentId() {
    request.setCommentId(-1L);

    Set<ConstraintViolation<EditStepCommentRequest>> violations = validator.validate(request);

    assertThat(violations).isEmpty();
  }
}
