package com.recipe_manager.unit_tests.model.dto.request;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.UUID;

import com.recipe_manager.model.dto.request.CreateMediaRequest;
import com.recipe_manager.model.enums.MediaType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@Tag("unit")
class CreateMediaRequestTest {

  private Validator validator;
  private CreateMediaRequest.CreateMediaRequestBuilder baseRequestBuilder;
  private UUID testUserId;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();

    testUserId = UUID.randomUUID();
    baseRequestBuilder = CreateMediaRequest.builder()
        .originalFilename("test-image.jpg")
        .mediaType(MediaType.IMAGE_JPEG)
        .fileSize(1024L)
        .contentHash("abc123");
  }

  @Test
  void shouldCreateValidCreateMediaRequest() {
    CreateMediaRequest request = baseRequestBuilder.build();

    Set<ConstraintViolation<CreateMediaRequest>> violations = validator.validate(request);

    assertTrue(violations.isEmpty());
    assertAll("CreateMediaRequest fields",
        () -> assertEquals("test-image.jpg", request.getOriginalFilename()),
        () -> assertEquals(MediaType.IMAGE_JPEG, request.getMediaType()),
        () -> assertEquals(1024L, request.getFileSize()),
        () -> assertEquals("abc123", request.getContentHash()));
  }

  @Test
  void shouldValidateRequiredFields() {
    CreateMediaRequest request = CreateMediaRequest.builder().build();

    Set<ConstraintViolation<CreateMediaRequest>> violations = validator.validate(request);

    assertFalse(violations.isEmpty());
    assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("originalFilename")));
    assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("mediaType")));
    assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("fileSize")));
  }

  @Test
  void shouldRejectBlankFilename() {
    CreateMediaRequest request = baseRequestBuilder.originalFilename("").build();

    Set<ConstraintViolation<CreateMediaRequest>> violations = validator.validate(request);

    assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("originalFilename")));
  }

  @Test
  void shouldRejectTooLongFilename() {
    String longFilename = "a".repeat(256);
    CreateMediaRequest request = baseRequestBuilder.originalFilename(longFilename).build();

    Set<ConstraintViolation<CreateMediaRequest>> violations = validator.validate(request);

    assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("originalFilename") &&
        v.getMessage().contains("size must be between")));
  }

  @Test
  void shouldRejectNullMediaType() {
    CreateMediaRequest request = baseRequestBuilder.mediaType(null).build();

    Set<ConstraintViolation<CreateMediaRequest>> violations = validator.validate(request);

    assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("mediaType")));
  }

  @Test
  void shouldRejectNonPositiveFileSize() {
    CreateMediaRequest request = baseRequestBuilder.fileSize(0L).build();

    Set<ConstraintViolation<CreateMediaRequest>> violations = validator.validate(request);

    assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("fileSize")));

    request = baseRequestBuilder.fileSize(-1L).build();
    violations = validator.validate(request);

    assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("fileSize")));
  }

  @Test
  void shouldRejectTooLongContentHash() {
    String longHash = "a".repeat(65);
    CreateMediaRequest request = baseRequestBuilder.contentHash(longHash).build();

    Set<ConstraintViolation<CreateMediaRequest>> violations = validator.validate(request);

    assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("contentHash")));
  }

  @Test
  void shouldAllowNullContentHash() {
    CreateMediaRequest request = baseRequestBuilder.contentHash(null).build();

    Set<ConstraintViolation<CreateMediaRequest>> violations = validator.validate(request);

    assertFalse(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("contentHash")));
  }

  @Test
  void shouldSupportAllMediaTypes() {
    for (MediaType mediaType : MediaType.values()) {
      CreateMediaRequest request = baseRequestBuilder.mediaType(mediaType).build();
      Set<ConstraintViolation<CreateMediaRequest>> violations = validator.validate(request);

      assertTrue(violations.isEmpty());
      assertEquals(mediaType, request.getMediaType());
    }
  }

  @Test
  void shouldImplementEqualsAndHashCode() {
    CreateMediaRequest request1 = baseRequestBuilder.build();
    CreateMediaRequest request2 = baseRequestBuilder.build();
    CreateMediaRequest request3 = baseRequestBuilder.fileSize(2048L).build();

    assertAll("Equals and HashCode",
        () -> assertEquals(request1, request2),
        () -> assertEquals(request1.hashCode(), request2.hashCode()),
        () -> assertNotEquals(request1, request3),
        () -> assertNotEquals(request1.hashCode(), request3.hashCode()));
  }

  @Test
  void shouldImplementToString() {
    CreateMediaRequest request = baseRequestBuilder.build();
    String toString = request.toString();

    assertAll("ToString contains key fields",
        () -> assertTrue(toString.contains("originalFilename=test-image.jpg")),
        () -> assertTrue(toString.contains("mediaType=" + MediaType.IMAGE_JPEG)),
        () -> assertTrue(toString.contains("fileSize=1024")));
  }
}
