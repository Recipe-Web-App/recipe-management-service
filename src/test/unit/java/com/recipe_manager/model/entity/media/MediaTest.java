package com.recipe_manager.model.entity.media;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import com.recipe_manager.model.enums.MediaType;
import com.recipe_manager.model.enums.ProcessingStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/**
 * Unit tests for Media entity.
 */
@Tag("unit")
class MediaTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  @DisplayName("Should create Media with builder pattern")
  void shouldCreateMediaWithBuilder() {
    // Given
    UUID userId = UUID.randomUUID();
    String mediaPath = "https://example.com/image.jpg";
    String contentHash = "abc123def456";
    String originalFilename = "test-image.jpg";

    // When
    Media media = Media.builder()
        .mediaId(1L)
        .userId(userId)
        .mediaType(MediaType.IMAGE_JPEG)
        .mediaPath(mediaPath)
        .fileSize(1024L)
        .contentHash(contentHash)
        .originalFilename(originalFilename)
        .processingStatus(ProcessingStatus.COMPLETE)
        .build();

    // Then
    assertThat(media.getMediaId()).isEqualTo(1L);
    assertThat(media.getUserId()).isEqualTo(userId);
    assertThat(media.getMediaType()).isEqualTo(MediaType.IMAGE_JPEG);
    assertThat(media.getMediaPath()).isEqualTo(mediaPath);
    assertThat(media.getFileSize()).isEqualTo(1024L);
    assertThat(media.getContentHash()).isEqualTo(contentHash);
    assertThat(media.getOriginalFilename()).isEqualTo(originalFilename);
    assertThat(media.getProcessingStatus()).isEqualTo(ProcessingStatus.COMPLETE);
  }

  @Test
  @DisplayName("Should create Media with no-args constructor")
  void shouldCreateMediaWithNoArgsConstructor() {
    // When
    Media media = new Media();

    // Then
    assertThat(media.getMediaId()).isNull();
    assertThat(media.getUserId()).isNull();
    assertThat(media.getMediaType()).isNull();
    assertThat(media.getMediaPath()).isNull();
    assertThat(media.getFileSize()).isNull();
    assertThat(media.getContentHash()).isNull();
    assertThat(media.getOriginalFilename()).isNull();
    assertThat(media.getProcessingStatus()).isNull();
    assertThat(media.getCreatedAt()).isNull();
    assertThat(media.getUpdatedAt()).isNull();
  }

  @Test
  @DisplayName("Should set and get all properties")
  void shouldSetAndGetAllProperties() {
    // Given
    Media media = new Media();
    UUID userId = UUID.randomUUID();
    String mediaPath = "https://example.com/video.mp4";
    String contentHash = "xyz789abc123";
    String originalFilename = "test-video.mp4";
    LocalDateTime now = LocalDateTime.now();

    // When
    media.setMediaId(2L);
    media.setUserId(userId);
    media.setMediaType(MediaType.VIDEO_MP4);
    media.setMediaPath(mediaPath);
    media.setFileSize(2048L);
    media.setContentHash(contentHash);
    media.setOriginalFilename(originalFilename);
    media.setProcessingStatus(ProcessingStatus.PENDING);
    media.setCreatedAt(now);
    media.setUpdatedAt(now);

    // Then
    assertThat(media.getMediaId()).isEqualTo(2L);
    assertThat(media.getUserId()).isEqualTo(userId);
    assertThat(media.getMediaType()).isEqualTo(MediaType.VIDEO_MP4);
    assertThat(media.getMediaPath()).isEqualTo(mediaPath);
    assertThat(media.getFileSize()).isEqualTo(2048L);
    assertThat(media.getContentHash()).isEqualTo(contentHash);
    assertThat(media.getOriginalFilename()).isEqualTo(originalFilename);
    assertThat(media.getProcessingStatus()).isEqualTo(ProcessingStatus.PENDING);
    assertThat(media.getCreatedAt()).isEqualTo(now);
    assertThat(media.getUpdatedAt()).isEqualTo(now);
  }

  @Test
  @DisplayName("Should validate required fields")
  void shouldValidateRequiredFields() {
    // Given
    Media media = Media.builder().build();

    // When
    Set<ConstraintViolation<Media>> violations = validator.validate(media);

    // Then
    assertThat(violations).hasSize(4); // userId, mediaType, mediaPath, processingStatus are required
  }

  @Test
  @DisplayName("Should validate media path max length")
  void shouldValidateMediaPathMaxLength() {
    // Given
    String longPath = "a".repeat(2049); // Exceeds MAX_PATH_LENGTH
    Media media = Media.builder()
        .userId(UUID.randomUUID())
        .mediaType(MediaType.IMAGE_JPEG)
        .mediaPath(longPath)
        .processingStatus(ProcessingStatus.COMPLETE)
        .build();

    // When
    Set<ConstraintViolation<Media>> violations = validator.validate(media);

    // Then
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getPropertyPath().toString()).hasToString("mediaPath");
  }

  @Test
  @DisplayName("Should validate content hash max length")
  void shouldValidateContentHashMaxLength() {
    // Given
    String longHash = "a".repeat(65); // Exceeds MAX_CONTENT_HASH_LENGTH
    Media media = Media.builder()
        .userId(UUID.randomUUID())
        .mediaType(MediaType.IMAGE_JPEG)
        .mediaPath("https://example.com/image.jpg")
        .contentHash(longHash)
        .processingStatus(ProcessingStatus.COMPLETE)
        .build();

    // When
    Set<ConstraintViolation<Media>> violations = validator.validate(media);

    // Then
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getPropertyPath().toString()).hasToString("contentHash");
  }

  @Test
  @DisplayName("Should validate original filename max length")
  void shouldValidateOriginalFilenameMaxLength() {
    // Given
    String longFilename = "a".repeat(256); // Exceeds MAX_ORIGINAL_FILENAME_LENGTH
    Media media = Media.builder()
        .userId(UUID.randomUUID())
        .mediaType(MediaType.IMAGE_JPEG)
        .mediaPath("https://example.com/image.jpg")
        .originalFilename(longFilename)
        .processingStatus(ProcessingStatus.COMPLETE)
        .build();

    // When
    Set<ConstraintViolation<Media>> violations = validator.validate(media);

    // Then
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getPropertyPath().toString()).hasToString("originalFilename");
  }

  @Test
  @DisplayName("Should pass validation with valid data")
  void shouldPassValidationWithValidData() {
    // Given
    Media media = Media.builder()
        .userId(UUID.randomUUID())
        .mediaType(MediaType.IMAGE_JPEG)
        .mediaPath("https://example.com/image.jpg")
        .fileSize(1024L)
        .contentHash("abc123")
        .originalFilename("image.jpg")
        .processingStatus(ProcessingStatus.COMPLETE)
        .build();

    // When
    Set<ConstraintViolation<Media>> violations = validator.validate(media);

    // Then
    assertThat(violations).isEmpty();
  }

  @Test
  @DisplayName("Should implement equals and hashCode correctly")
  void shouldImplementEqualsAndHashCodeCorrectly() {
    // Given
    UUID userId = UUID.randomUUID();
    Media media1 = Media.builder()
        .mediaId(1L)
        .userId(userId)
        .mediaType(MediaType.IMAGE_JPEG)
        .mediaPath("https://example.com/image.jpg")
        .processingStatus(ProcessingStatus.COMPLETE)
        .build();

    Media media2 = Media.builder()
        .mediaId(1L)
        .userId(userId)
        .mediaType(MediaType.IMAGE_JPEG)
        .mediaPath("https://example.com/image.jpg")
        .processingStatus(ProcessingStatus.COMPLETE)
        .build();

    Media media3 = Media.builder()
        .mediaId(2L)
        .userId(userId)
        .mediaType(MediaType.VIDEO_MP4)
        .mediaPath("https://example.com/video.mp4")
        .processingStatus(ProcessingStatus.PENDING)
        .build();

    // Then
    assertThat(media1).isEqualTo(media2);
    assertThat(media1.hashCode()).hasSameHashCodeAs(media2.hashCode());
    assertThat(media1).isNotEqualTo(media3);
  }

  @Test
  @DisplayName("Should implement toString correctly")
  void shouldImplementToStringCorrectly() {
    // Given
    Media media = Media.builder()
        .mediaId(1L)
        .userId(UUID.randomUUID())
        .mediaType(MediaType.IMAGE_JPEG)
        .mediaPath("https://example.com/image.jpg")
        .processingStatus(ProcessingStatus.COMPLETE)
        .build();

    // When
    String toString = media.toString();

    // Then
    assertNotNull(toString);
    assertThat(toString)
        .contains("Media")
        .contains("mediaId=1")
        .contains("mediaType=image/jpeg")
        .contains("processingStatus=COMPLETE");
  }
}
