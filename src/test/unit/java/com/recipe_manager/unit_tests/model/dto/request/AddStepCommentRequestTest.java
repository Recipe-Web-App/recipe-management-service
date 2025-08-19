package com.recipe_manager.unit_tests.model.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import com.recipe_manager.model.dto.request.AddStepCommentRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@Tag("unit")
class AddStepCommentRequestTest {

  private AddStepCommentRequest request;
  private Validator validator;

  @BeforeEach
  void setUp() {
    request = AddStepCommentRequest.builder()
        .comment("Test comment")
        .build();

    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  void testBuilder() {
    AddStepCommentRequest newRequest = AddStepCommentRequest.builder()
        .comment("Another test comment")
        .build();

    assertThat(newRequest.getComment()).isEqualTo("Another test comment");
  }

  @Test
  void testGettersAndSetters() {
    request.setComment("Updated comment");

    assertThat(request.getComment()).isEqualTo("Updated comment");
  }

  @Test
  void testValidRequest() {
    Set<ConstraintViolation<AddStepCommentRequest>> violations = validator.validate(request);

    assertThat(violations).isEmpty();
  }

  @Test
  void testBlankCommentValidation() {
    request.setComment("");

    Set<ConstraintViolation<AddStepCommentRequest>> violations = validator.validate(request);

    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage()).isEqualTo("Comment text cannot be blank");
  }

  @Test
  void testNullCommentValidation() {
    request.setComment(null);

    Set<ConstraintViolation<AddStepCommentRequest>> violations = validator.validate(request);

    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage()).isEqualTo("Comment text cannot be blank");
  }

  @Test
  void testWhitespaceOnlyCommentValidation() {
    request.setComment("   ");

    Set<ConstraintViolation<AddStepCommentRequest>> violations = validator.validate(request);

    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage()).isEqualTo("Comment text cannot be blank");
  }

  @Test
  void testEqualsAndHashCode() {
    AddStepCommentRequest request1 = AddStepCommentRequest.builder()
        .comment("Test comment")
        .build();

    AddStepCommentRequest request2 = AddStepCommentRequest.builder()
        .comment("Test comment")
        .build();

    assertThat(request1).isEqualTo(request2);
    assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
  }

  @Test
  void testToString() {
    String toStringResult = request.toString();

    assertThat(toStringResult).contains("AddStepCommentRequest");
    assertThat(toStringResult).contains("comment=" + request.getComment());
  }

  @Test
  void testNoArgsConstructor() {
    AddStepCommentRequest newRequest = new AddStepCommentRequest();

    assertThat(newRequest.getComment()).isNull();
  }

  @Test
  void testAllArgsConstructor() {
    AddStepCommentRequest newRequest = new AddStepCommentRequest("Test comment", false);

    assertThat(newRequest.getComment()).isEqualTo("Test comment");
    assertThat(newRequest.getIsPublic()).isFalse();
  }

  @Test
  void testValidCommentWithSpecialCharacters() {
    request.setComment("Comment with special chars: @#$%^&*()!");

    Set<ConstraintViolation<AddStepCommentRequest>> violations = validator.validate(request);

    assertThat(violations).isEmpty();
  }

  @Test
  void testValidLongComment() {
    String longComment = "This is a very long comment that contains many words and characters "
        + "to test that the validation does not impose unnecessary length restrictions on comments "
        + "as long as they are not blank.";
    request.setComment(longComment);

    Set<ConstraintViolation<AddStepCommentRequest>> violations = validator.validate(request);

    assertThat(violations).isEmpty();
  }
}
