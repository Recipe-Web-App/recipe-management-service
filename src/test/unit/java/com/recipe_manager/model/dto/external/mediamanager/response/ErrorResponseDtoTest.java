package com.recipe_manager.model.dto.external.mediamanager.response;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class ErrorResponseDtoTest {

  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should create DTO using builder pattern")
  void shouldCreateDtoUsingBuilder() {
    Map<String, Object> details = new HashMap<>();
    details.put("field", "fileSize");
    details.put("rejected", 100000000L);
    details.put("maxAllowed", 52428800L);

    ErrorResponseDto dto = ErrorResponseDto.builder()
        .error("Bad Request")
        .message("File size exceeds maximum allowed size")
        .details(details)
        .build();

    assertThat(dto.getError()).isEqualTo("Bad Request");
    assertThat(dto.getMessage()).isEqualTo("File size exceeds maximum allowed size");
    assertThat(dto.getDetails()).isNotNull();
    assertThat(dto.getDetails().get("field")).isEqualTo("fileSize");
    assertThat(dto.getDetails().get("rejected")).isEqualTo(100000000L);
    assertThat(dto.getDetails().get("maxAllowed")).isEqualTo(52428800L);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should serialize and deserialize DTO")
  void shouldSerializeAndDeserialize() throws Exception {
    Map<String, Object> details = new HashMap<>();
    details.put("errorCode", "INVALID_FORMAT");
    details.put("timestamp", "2024-01-01T10:00:00Z");

    ErrorResponseDto original = ErrorResponseDto.builder()
        .error("Not Found")
        .message("Media with ID 123 not found")
        .details(details)
        .build();

    String json = objectMapper.writeValueAsString(original);
    assertThat(json).contains("\"error\":\"Not Found\"");
    assertThat(json).contains("\"message\":\"Media with ID 123 not found\"");
    assertThat(json).contains("\"details\"");
    assertThat(json).contains("\"errorCode\":\"INVALID_FORMAT\"");

    ErrorResponseDto deserialized = objectMapper.readValue(json, ErrorResponseDto.class);
    assertThat(deserialized).usingRecursiveComparison().isEqualTo(original);
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should handle null details")
  void shouldHandleNullDetails() {
    ErrorResponseDto dto = ErrorResponseDto.builder()
        .error("Internal Server Error")
        .message("An unexpected error occurred")
        .details(null)
        .build();

    assertThat(dto.getError()).isEqualTo("Internal Server Error");
    assertThat(dto.getMessage()).isEqualTo("An unexpected error occurred");
    assertThat(dto.getDetails()).isNull();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle empty details map")
  void shouldHandleEmptyDetailsMap() {
    ErrorResponseDto dto = ErrorResponseDto.builder()
        .error("Unauthorized")
        .message("Invalid authentication token")
        .details(new HashMap<>())
        .build();

    assertThat(dto.getError()).isEqualTo("Unauthorized");
    assertThat(dto.getMessage()).isEqualTo("Invalid authentication token");
    assertThat(dto.getDetails()).isNotNull();
    assertThat(dto.getDetails()).isEmpty();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should support various error types")
  void shouldSupportVariousErrorTypes() {
    String[] errorTypes = {
        "Bad Request",
        "Unauthorized",
        "Forbidden",
        "Not Found",
        "Method Not Allowed",
        "Conflict",
        "Unprocessable Entity",
        "Internal Server Error",
        "Bad Gateway",
        "Service Unavailable",
        "Not Implemented"
    };

    for (String errorType : errorTypes) {
      ErrorResponseDto dto = ErrorResponseDto.builder()
          .error(errorType)
          .message("Test message for " + errorType)
          .build();

      assertThat(dto.getError()).isEqualTo(errorType);
    }
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle complex details map")
  void shouldHandleComplexDetailsMap() {
    Map<String, Object> nestedMap = new HashMap<>();
    nestedMap.put("code", "FILE_TOO_LARGE");
    nestedMap.put("maxSize", 52428800L);

    Map<String, Object> details = new HashMap<>();
    details.put("validation", nestedMap);
    details.put("timestamp", System.currentTimeMillis());
    details.put("requestId", "req-123-abc");

    ErrorResponseDto dto = ErrorResponseDto.builder()
        .error("Bad Request")
        .message("Validation failed")
        .details(details)
        .build();

    assertThat(dto.getDetails()).isNotNull();
    assertThat(dto.getDetails()).containsKey("validation");
    assertThat(dto.getDetails()).containsKey("timestamp");
    assertThat(dto.getDetails()).containsKey("requestId");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should properly implement equals and hashCode")
  void shouldImplementEqualsAndHashCode() {
    Map<String, Object> details1 = new HashMap<>();
    details1.put("key", "value");

    ErrorResponseDto dto1 = ErrorResponseDto.builder()
        .error("Not Found")
        .message("Resource not found")
        .details(details1)
        .build();

    Map<String, Object> details2 = new HashMap<>();
    details2.put("key", "value");

    ErrorResponseDto dto2 = ErrorResponseDto.builder()
        .error("Not Found")
        .message("Resource not found")
        .details(details2)
        .build();

    ErrorResponseDto differentDto = ErrorResponseDto.builder()
        .error("Bad Request")
        .message("Invalid request")
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
    Map<String, Object> details = new HashMap<>();
    details.put("errorCode", "TEST_ERROR");

    ErrorResponseDto dto = ErrorResponseDto.builder()
        .error("Test Error")
        .message("This is a test error message")
        .details(details)
        .build();

    String toString = dto.toString();
    assertThat(toString).contains("ErrorResponseDto");
    assertThat(toString).contains("Test Error");
    assertThat(toString).contains("This is a test error message");
  }
}
