package com.recipe_manager.model.dto.external.notificationservice.response;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class NotificationErrorResponseDtoTest {

  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should create DTO using builder pattern")
  void shouldCreateDtoUsingBuilder() {
    NotificationErrorResponseDto dto = NotificationErrorResponseDto.builder()
        .error("bad_request")
        .message("Invalid request parameters")
        .detail("Recipient IDs must contain at least 1 item")
        .errors(Map.of("recipient_ids", "Array must contain at least 1 item"))
        .build();

    assertThat(dto.getError()).isEqualTo("bad_request");
    assertThat(dto.getMessage()).isEqualTo("Invalid request parameters");
    assertThat(dto.getDetail()).isEqualTo("Recipient IDs must contain at least 1 item");
    assertThat(dto.getErrors()).containsEntry("recipient_ids", "Array must contain at least 1 item");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should serialize and deserialize DTO")
  void shouldSerializeAndDeserialize() throws Exception {
    NotificationErrorResponseDto original = NotificationErrorResponseDto.builder()
        .error("unauthorized")
        .message("Authentication credentials were not provided or are invalid")
        .build();

    String json = objectMapper.writeValueAsString(original);
    assertThat(json).contains("\"error\":\"unauthorized\"");
    assertThat(json).contains("\"message\":\"Authentication credentials were not provided or are invalid\"");

    NotificationErrorResponseDto deserialized = objectMapper.readValue(json, NotificationErrorResponseDto.class);
    assertThat(deserialized).usingRecursiveComparison().isEqualTo(original);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle error with detail field")
  void shouldHandleErrorWithDetail() {
    NotificationErrorResponseDto dto = NotificationErrorResponseDto.builder()
        .error("forbidden")
        .message("You do not have permission to perform this action")
        .detail("Requires notification:admin scope or valid follower relationship")
        .build();

    assertThat(dto.getError()).isEqualTo("forbidden");
    assertThat(dto.getMessage()).isEqualTo("You do not have permission to perform this action");
    assertThat(dto.getDetail()).isEqualTo("Requires notification:admin scope or valid follower relationship");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle validation errors map")
  void shouldHandleValidationErrorsMap() {
    Map<String, Object> validationErrors = Map.of(
        "recipient_ids", "Array must contain at least 1 item",
        "recipe_id", "Invalid UUID format"
    );

    NotificationErrorResponseDto dto = NotificationErrorResponseDto.builder()
        .error("bad_request")
        .message("Invalid request parameters")
        .errors(validationErrors)
        .build();

    assertThat(dto.getErrors()).hasSize(2);
    assertThat(dto.getErrors()).containsEntry("recipient_ids", "Array must contain at least 1 item");
    assertThat(dto.getErrors()).containsEntry("recipe_id", "Invalid UUID format");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle error without optional fields")
  void shouldHandleErrorWithoutOptionalFields() {
    NotificationErrorResponseDto dto = NotificationErrorResponseDto.builder()
        .error("not_found")
        .message("The requested resource was not found")
        .build();

    assertThat(dto.getError()).isEqualTo("not_found");
    assertThat(dto.getMessage()).isEqualTo("The requested resource was not found");
    assertThat(dto.getDetail()).isNull();
    assertThat(dto.getErrors()).isNull();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should properly implement equals and hashCode")
  void shouldImplementEqualsAndHashCode() {
    NotificationErrorResponseDto dto1 = NotificationErrorResponseDto.builder()
        .error("rate_limit_exceeded")
        .message("Rate limit exceeded")
        .detail("Limit: 100 requests per minute")
        .build();

    NotificationErrorResponseDto dto2 = NotificationErrorResponseDto.builder()
        .error("rate_limit_exceeded")
        .message("Rate limit exceeded")
        .detail("Limit: 100 requests per minute")
        .build();

    NotificationErrorResponseDto differentDto = NotificationErrorResponseDto.builder()
        .error("internal_server_error")
        .message("An unexpected error occurred")
        .build();

    assertThat(dto1)
        .isEqualTo(dto2)
        .hasSameHashCodeAs(dto2)
        .isNotEqualTo(differentDto)
        .doesNotHaveSameHashCodeAs(differentDto);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should support toString method")
  void shouldSupportToString() {
    NotificationErrorResponseDto dto = NotificationErrorResponseDto.builder()
        .error("conflict")
        .message("Cannot delete notification while it is being processed")
        .detail("Notification status is 'queued'")
        .build();

    String toString = dto.toString();
    assertThat(toString).contains("NotificationErrorResponseDto");
    assertThat(toString).contains("conflict");
    assertThat(toString).contains("Cannot delete notification while it is being processed");
    assertThat(toString).contains("queued");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should support all-args constructor")
  void shouldSupportAllArgsConstructor() {
    Map<String, Object> errors = Map.of("field", "error");

    NotificationErrorResponseDto dto = new NotificationErrorResponseDto(
        "bad_request",
        "Validation failed",
        "Field validation error",
        errors
    );

    assertThat(dto.getError()).isEqualTo("bad_request");
    assertThat(dto.getMessage()).isEqualTo("Validation failed");
    assertThat(dto.getDetail()).isEqualTo("Field validation error");
    assertThat(dto.getErrors()).isEqualTo(errors);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should support no-args constructor and setters")
  void shouldSupportNoArgsConstructorAndSetters() {
    NotificationErrorResponseDto dto = new NotificationErrorResponseDto();
    dto.setError("unauthorized");
    dto.setMessage("Invalid token");
    dto.setDetail("Token has expired");
    dto.setErrors(Map.of("token", "expired"));

    assertThat(dto.getError()).isEqualTo("unauthorized");
    assertThat(dto.getMessage()).isEqualTo("Invalid token");
    assertThat(dto.getDetail()).isEqualTo("Token has expired");
    assertThat(dto.getErrors()).containsEntry("token", "expired");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle empty errors map")
  void shouldHandleEmptyErrorsMap() {
    NotificationErrorResponseDto dto = NotificationErrorResponseDto.builder()
        .error("bad_request")
        .message("Request failed")
        .errors(Map.of())
        .build();

    assertThat(dto.getErrors()).isEmpty();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should serialize OpenAPI example for bad request")
  void shouldSerializeOpenApiExampleForBadRequest() throws Exception {
    NotificationErrorResponseDto dto = NotificationErrorResponseDto.builder()
        .error("bad_request")
        .message("Invalid request parameters")
        .errors(Map.of("recipient_ids", "Array must contain at least 1 item"))
        .build();

    String json = objectMapper.writeValueAsString(dto);

    assertThat(json).contains("\"error\":\"bad_request\"");
    assertThat(json).contains("\"message\":\"Invalid request parameters\"");
    assertThat(json).contains("\"errors\"");
    assertThat(json).contains("\"recipient_ids\"");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should serialize OpenAPI example for forbidden")
  void shouldSerializeOpenApiExampleForForbidden() throws Exception {
    NotificationErrorResponseDto dto = NotificationErrorResponseDto.builder()
        .error("forbidden")
        .message("You do not have permission to perform this action")
        .detail("Requires notification:admin scope or valid follower relationship")
        .build();

    String json = objectMapper.writeValueAsString(dto);

    assertThat(json).contains("\"error\":\"forbidden\"");
    assertThat(json).contains("\"message\":\"You do not have permission to perform this action\"");
    assertThat(json).contains("\"detail\":\"Requires notification:admin scope or valid follower relationship\"");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should serialize OpenAPI example for rate limit exceeded")
  void shouldSerializeOpenApiExampleForRateLimitExceeded() throws Exception {
    NotificationErrorResponseDto dto = NotificationErrorResponseDto.builder()
        .error("rate_limit_exceeded")
        .message("Rate limit exceeded. Please try again later.")
        .detail("Limit: 100 requests per minute")
        .build();

    String json = objectMapper.writeValueAsString(dto);

    assertThat(json).contains("\"error\":\"rate_limit_exceeded\"");
    assertThat(json).contains("\"message\":\"Rate limit exceeded. Please try again later.\"");
    assertThat(json).contains("\"detail\":\"Limit: 100 requests per minute\"");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle nested error objects in errors map")
  void shouldHandleNestedErrorObjectsInErrorsMap() {
    Map<String, Object> nestedErrors = Map.of(
        "recipient_ids", Map.of(
            "type", "size_constraint",
            "actual_size", 0,
            "required_min", 1
        )
    );

    NotificationErrorResponseDto dto = NotificationErrorResponseDto.builder()
        .error("bad_request")
        .message("Validation failed")
        .errors(nestedErrors)
        .build();

    assertThat(dto.getErrors()).containsKey("recipient_ids");
    @SuppressWarnings("unchecked")
    Map<String, Object> recipientError = (Map<String, Object>) dto.getErrors().get("recipient_ids");
    assertThat(recipientError).containsEntry("type", "size_constraint");
    assertThat(recipientError).containsEntry("actual_size", 0);
    assertThat(recipientError).containsEntry("required_min", 1);
  }
}
