package com.recipe_manager.model.dto.media;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.UUID;

import com.recipe_manager.model.enums.MediaType;
import com.recipe_manager.model.enums.ProcessingStatus;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class MediaDtoTest {

  @Test
  @DisplayName("Should create MediaDto with builder pattern")
  void shouldCreateMediaDtoWithBuilder() {
    // Given
    Long mediaId = 1L;
    UUID userId = UUID.randomUUID();
    MediaType mediaType = MediaType.IMAGE_JPEG;
    String mediaPath = "https://example.com/image.jpg";
    Long fileSize = 1024L;
    String contentHash = "abc123";
    String originalFilename = "image.jpg";
    ProcessingStatus processingStatus = ProcessingStatus.COMPLETE;
    LocalDateTime createdAt = LocalDateTime.now();
    LocalDateTime updatedAt = LocalDateTime.now();

    // When
    MediaDto mediaDto = MediaDto.builder()
        .mediaId(mediaId)
        .userId(userId)
        .mediaType(mediaType)
        .mediaPath(mediaPath)
        .fileSize(fileSize)
        .contentHash(contentHash)
        .originalFilename(originalFilename)
        .processingStatus(processingStatus)
        .createdAt(createdAt)
        .updatedAt(updatedAt)
        .build();

    // Then
    assertThat(mediaDto.getMediaId()).isEqualTo(mediaId);
    assertThat(mediaDto.getUserId()).isEqualTo(userId);
    assertThat(mediaDto.getMediaType()).isEqualTo(mediaType);
    assertThat(mediaDto.getMediaPath()).isEqualTo(mediaPath);
    assertThat(mediaDto.getFileSize()).isEqualTo(fileSize);
    assertThat(mediaDto.getContentHash()).isEqualTo(contentHash);
    assertThat(mediaDto.getOriginalFilename()).isEqualTo(originalFilename);
    assertThat(mediaDto.getProcessingStatus()).isEqualTo(processingStatus);
    assertThat(mediaDto.getCreatedAt()).isEqualTo(createdAt);
    assertThat(mediaDto.getUpdatedAt()).isEqualTo(updatedAt);
  }

  @Test
  @DisplayName("Should create MediaDto with no-args constructor")
  void shouldCreateMediaDtoWithNoArgsConstructor() {
    // When
    MediaDto mediaDto = new MediaDto();

    // Then
    assertThat(mediaDto.getMediaId()).isNull();
    assertThat(mediaDto.getUserId()).isNull();
    assertThat(mediaDto.getMediaType()).isNull();
    assertThat(mediaDto.getMediaPath()).isNull();
    assertThat(mediaDto.getFileSize()).isNull();
    assertThat(mediaDto.getContentHash()).isNull();
    assertThat(mediaDto.getOriginalFilename()).isNull();
    assertThat(mediaDto.getProcessingStatus()).isNull();
    assertThat(mediaDto.getCreatedAt()).isNull();
    assertThat(mediaDto.getUpdatedAt()).isNull();
  }

  @Test
  @DisplayName("Should implement equals and hashCode correctly")
  void shouldImplementEqualsAndHashCodeCorrectly() {
    // Given
    UUID userId = UUID.randomUUID();
    LocalDateTime createdAt = LocalDateTime.now();
    LocalDateTime updatedAt = LocalDateTime.now();

    MediaDto mediaDto1 = MediaDto.builder()
        .mediaId(1L)
        .userId(userId)
        .mediaType(MediaType.IMAGE_JPEG)
        .mediaPath("https://example.com/image.jpg")
        .fileSize(1024L)
        .contentHash("abc123")
        .originalFilename("image.jpg")
        .processingStatus(ProcessingStatus.COMPLETE)
        .createdAt(createdAt)
        .updatedAt(updatedAt)
        .build();

    MediaDto mediaDto2 = MediaDto.builder()
        .mediaId(1L)
        .userId(userId)
        .mediaType(MediaType.IMAGE_JPEG)
        .mediaPath("https://example.com/image.jpg")
        .fileSize(1024L)
        .contentHash("abc123")
        .originalFilename("image.jpg")
        .processingStatus(ProcessingStatus.COMPLETE)
        .createdAt(createdAt)
        .updatedAt(updatedAt)
        .build();

    MediaDto mediaDto3 = MediaDto.builder()
        .mediaId(2L)
        .userId(userId)
        .mediaType(MediaType.IMAGE_JPEG)
        .mediaPath("https://example.com/other.jpg")
        .processingStatus(ProcessingStatus.COMPLETE)
        .build();

    // Then
    assertThat(mediaDto1)
        .isEqualTo(mediaDto2)
        .hasSameHashCodeAs(mediaDto2)
        .isNotEqualTo(mediaDto3);
  }

  @Test
  @DisplayName("Should implement toString correctly")
  void shouldImplementToStringCorrectly() {
    // Given
    MediaDto mediaDto = MediaDto.builder()
        .mediaId(1L)
        .userId(UUID.randomUUID())
        .mediaType(MediaType.IMAGE_JPEG)
        .mediaPath("https://example.com/image.jpg")
        .fileSize(1024L)
        .contentHash("abc123")
        .originalFilename("image.jpg")
        .processingStatus(ProcessingStatus.COMPLETE)
        .build();

    // When
    String toString = mediaDto.toString();

    // Then
    assertThat(toString)
        .contains("MediaDto")
        .contains("mediaId=")
        .contains("userId=")
        .contains("mediaType=")
        .contains("mediaPath=")
        .contains("fileSize=")
        .contains("contentHash=")
        .contains("originalFilename=")
        .contains("processingStatus=");
  }
}
