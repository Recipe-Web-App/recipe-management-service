package com.recipe_manager.model.dto.external.mediamanager.request;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class InitiateUploadRequestDtoTest {

  private ObjectMapper objectMapper;
  private Validator validator;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should create DTO using builder pattern")
  void shouldCreateDtoUsingBuilder() {
    InitiateUploadRequestDto dto = InitiateUploadRequestDto.builder()
        .filename("example.jpg")
        .contentType("image/jpeg")
        .fileSize(1048576L)
        .build();

    assertThat(dto.getFilename()).isEqualTo("example.jpg");
    assertThat(dto.getContentType()).isEqualTo("image/jpeg");
    assertThat(dto.getFileSize()).isEqualTo(1048576L);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should serialize and deserialize DTO")
  void shouldSerializeAndDeserialize() throws Exception {
    InitiateUploadRequestDto original = InitiateUploadRequestDto.builder()
        .filename("test-file.png")
        .contentType("image/png")
        .fileSize(2048576L)
        .build();

    String json = objectMapper.writeValueAsString(original);
    assertThat(json).contains("\"filename\":\"test-file.png\"");
    assertThat(json).contains("\"content_type\":\"image/png\"");
    assertThat(json).contains("\"file_size\":2048576");

    InitiateUploadRequestDto deserialized = objectMapper.readValue(json, InitiateUploadRequestDto.class);
    assertThat(deserialized).usingRecursiveComparison().isEqualTo(original);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should validate valid DTO")
  void shouldValidateValidDto() {
    InitiateUploadRequestDto dto = InitiateUploadRequestDto.builder()
        .filename("valid-file.mp4")
        .contentType("video/mp4")
        .fileSize(10485760L)
        .build();

    Set<ConstraintViolation<InitiateUploadRequestDto>> violations = validator.validate(dto);
    assertThat(violations).isEmpty();
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should reject null filename")
  void shouldRejectNullFilename() {
    InitiateUploadRequestDto dto = InitiateUploadRequestDto.builder()
        .contentType("image/jpeg")
        .fileSize(1024L)
        .build();

    Set<ConstraintViolation<InitiateUploadRequestDto>> violations = validator.validate(dto);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage()).isEqualTo("Filename is required");
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should reject empty filename")
  void shouldRejectEmptyFilename() {
    InitiateUploadRequestDto dto = InitiateUploadRequestDto.builder()
        .filename("")
        .contentType("image/jpeg")
        .fileSize(1024L)
        .build();

    Set<ConstraintViolation<InitiateUploadRequestDto>> violations = validator.validate(dto);
    assertThat(violations).hasSize(2);

    // Check that both expected validation messages are present
    Set<String> messages = violations.stream()
        .map(ConstraintViolation::getMessage)
        .collect(java.util.stream.Collectors.toSet());

    assertThat(messages).contains("Filename is required");
    assertThat(messages).contains("Filename must be between 1 and 255 characters");
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should reject filename exceeding max length")
  void shouldRejectFilenameExceedingMaxLength() {
    String longFilename = "a".repeat(256);
    InitiateUploadRequestDto dto = InitiateUploadRequestDto.builder()
        .filename(longFilename)
        .contentType("image/jpeg")
        .fileSize(1024L)
        .build();

    Set<ConstraintViolation<InitiateUploadRequestDto>> violations = validator.validate(dto);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage())
        .isEqualTo("Filename must be between 1 and 255 characters");
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should reject invalid content type format")
  void shouldRejectInvalidContentType() {
    InitiateUploadRequestDto dto = InitiateUploadRequestDto.builder()
        .filename("test.jpg")
        .contentType("invalid_content_type")
        .fileSize(1024L)
        .build();

    Set<ConstraintViolation<InitiateUploadRequestDto>> violations = validator.validate(dto);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage())
        .isEqualTo("Content type must be a valid MIME type format");
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should reject null file size")
  void shouldRejectNullFileSize() {
    InitiateUploadRequestDto dto = InitiateUploadRequestDto.builder()
        .filename("test.jpg")
        .contentType("image/jpeg")
        .build();

    Set<ConstraintViolation<InitiateUploadRequestDto>> violations = validator.validate(dto);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage()).isEqualTo("File size is required");
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should reject file size below minimum")
  void shouldRejectFileSizeBelowMinimum() {
    InitiateUploadRequestDto dto = InitiateUploadRequestDto.builder()
        .filename("test.jpg")
        .contentType("image/jpeg")
        .fileSize(0L)
        .build();

    Set<ConstraintViolation<InitiateUploadRequestDto>> violations = validator.validate(dto);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage())
        .isEqualTo("File size must be at least 1 byte");
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should reject file size above maximum")
  void shouldRejectFileSizeAboveMaximum() {
    InitiateUploadRequestDto dto = InitiateUploadRequestDto.builder()
        .filename("test.jpg")
        .contentType("image/jpeg")
        .fileSize(52428801L)
        .build();

    Set<ConstraintViolation<InitiateUploadRequestDto>> violations = validator.validate(dto);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage())
        .isEqualTo("File size cannot exceed 50MB (52428800 bytes)");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should accept valid MIME types")
  void shouldAcceptValidMimeTypes() {
    String[] validMimeTypes = {
      "image/jpeg", "image/png", "video/mp4", "audio/mpeg",
      "application/pdf", "text/plain", "application/octet-stream"
    };

    for (String mimeType : validMimeTypes) {
      InitiateUploadRequestDto dto = InitiateUploadRequestDto.builder()
          .filename("test-file")
          .contentType(mimeType)
          .fileSize(1024L)
          .build();

      Set<ConstraintViolation<InitiateUploadRequestDto>> violations = validator.validate(dto);
      assertThat(violations).isEmpty();
    }
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should properly implement equals and hashCode")
  void shouldImplementEqualsAndHashCode() {
    InitiateUploadRequestDto dto1 = InitiateUploadRequestDto.builder()
        .filename("file.jpg")
        .contentType("image/jpeg")
        .fileSize(1024L)
        .build();

    InitiateUploadRequestDto dto2 = InitiateUploadRequestDto.builder()
        .filename("file.jpg")
        .contentType("image/jpeg")
        .fileSize(1024L)
        .build();

    InitiateUploadRequestDto differentDto = InitiateUploadRequestDto.builder()
        .filename("different.png")
        .contentType("image/png")
        .fileSize(2048L)
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
    InitiateUploadRequestDto dto = InitiateUploadRequestDto.builder()
        .filename("test.jpg")
        .contentType("image/jpeg")
        .fileSize(1024L)
        .build();

    String toString = dto.toString();
    assertThat(toString).contains("InitiateUploadRequestDto");
    assertThat(toString).contains("test.jpg");
    assertThat(toString).contains("image/jpeg");
    assertThat(toString).contains("1024");
  }
}
