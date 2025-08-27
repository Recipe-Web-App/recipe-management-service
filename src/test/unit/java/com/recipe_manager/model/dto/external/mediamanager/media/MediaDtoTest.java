package com.recipe_manager.model.dto.external.mediamanager.media;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.recipe_manager.model.enums.MediaFormat;
import com.recipe_manager.model.enums.ProcessingStatus;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class MediaDtoTest {

  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should create DTO using builder pattern")
  void shouldCreateDtoUsingBuilder() {
    LocalDateTime uploadedAt = LocalDateTime.of(2024, 1, 15, 10, 30, 0);
    LocalDateTime updatedAt = LocalDateTime.of(2024, 1, 15, 10, 35, 0);

    MediaTypeDto mediaType = MediaTypeDto.builder()
        .image(ImageMediaTypeDto.builder()
            .format(MediaFormat.JPEG)
            .width(1920)
            .height(1080)
            .build())
        .build();

    MediaDto dto = MediaDto.builder()
        .id(123L)
        .contentHash("abcdef1234567890abcdef1234567890abcdef1234567890abcdef1234567890")
        .originalFilename("example.jpg")
        .mediaType(mediaType)
        .fileSize(1048576L)
        .processingStatus(ProcessingStatus.COMPLETE)
        .uploadedAt(uploadedAt)
        .updatedAt(updatedAt)
        .build();

    assertThat(dto.getId()).isEqualTo(123L);
    assertThat(dto.getContentHash())
        .isEqualTo("abcdef1234567890abcdef1234567890abcdef1234567890abcdef1234567890");
    assertThat(dto.getOriginalFilename()).isEqualTo("example.jpg");
    assertThat(dto.getMediaType()).isNotNull();
    assertThat(dto.getFileSize()).isEqualTo(1048576L);
    assertThat(dto.getProcessingStatus()).isEqualTo(ProcessingStatus.COMPLETE);
    assertThat(dto.getUploadedAt()).isEqualTo(uploadedAt);
    assertThat(dto.getUpdatedAt()).isEqualTo(updatedAt);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should serialize and deserialize DTO")
  void shouldSerializeAndDeserialize() throws Exception {
    LocalDateTime uploadedAt = LocalDateTime.of(2024, 2, 20, 14, 15, 30);
    LocalDateTime updatedAt = LocalDateTime.of(2024, 2, 20, 14, 20, 45);

    MediaTypeDto mediaType = MediaTypeDto.builder()
        .video(VideoMediaTypeDto.builder()
            .format(MediaFormat.MP4)
            .width(1280)
            .height(720)
            .durationSeconds(120)
            .build())
        .build();

    MediaDto original = MediaDto.builder()
        .id(456L)
        .contentHash("fedcba9876543210fedcba9876543210fedcba9876543210fedcba9876543210")
        .originalFilename("video.mp4")
        .mediaType(mediaType)
        .fileSize(5242880L)
        .processingStatus(ProcessingStatus.PROCESSING)
        .uploadedAt(uploadedAt)
        .updatedAt(updatedAt)
        .build();

    String json = objectMapper.writeValueAsString(original);
    assertThat(json).contains("\"id\":456");
    assertThat(json).contains("\"content_hash\":\"fedcba9876543210fedcba9876543210fedcba9876543210fedcba9876543210\"");
    assertThat(json).contains("\"original_filename\":\"video.mp4\"");
    assertThat(json).contains("\"file_size\":5242880");
    assertThat(json).contains("\"processing_status\":\"PROCESSING\"");

    MediaDto deserialized = objectMapper.readValue(json, MediaDto.class);
    assertThat(deserialized).usingRecursiveComparison().isEqualTo(original);
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should handle null values properly")
  void shouldHandleNullValues() {
    MediaDto dto = MediaDto.builder()
        .id(789L)
        .contentHash("test-hash")
        .originalFilename("test.txt")
        .processingStatus(ProcessingStatus.INITIATED)
        .build();

    assertThat(dto.getId()).isEqualTo(789L);
    assertThat(dto.getContentHash()).isEqualTo("test-hash");
    assertThat(dto.getOriginalFilename()).isEqualTo("test.txt");
    assertThat(dto.getMediaType()).isNull();
    assertThat(dto.getFileSize()).isNull();
    assertThat(dto.getUploadedAt()).isNull();
    assertThat(dto.getUpdatedAt()).isNull();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should support all ProcessingStatus values")
  void shouldSupportAllProcessingStatusValues() {
    for (ProcessingStatus status : ProcessingStatus.values()) {
      MediaDto dto = MediaDto.builder()
          .id(1L)
          .contentHash("hash")
          .originalFilename("file.txt")
          .processingStatus(status)
          .build();

      assertThat(dto.getProcessingStatus()).isEqualTo(status);
    }
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should properly implement equals and hashCode")
  void shouldImplementEqualsAndHashCode() {
    LocalDateTime time = LocalDateTime.now();
    MediaTypeDto mediaType = MediaTypeDto.builder()
        .image(ImageMediaTypeDto.builder()
            .format(MediaFormat.PNG)
            .width(800)
            .height(600)
            .build())
        .build();

    MediaDto dto1 = MediaDto.builder()
        .id(111L)
        .contentHash("hash-111")
        .originalFilename("image1.png")
        .mediaType(mediaType)
        .fileSize(2048L)
        .processingStatus(ProcessingStatus.COMPLETE)
        .uploadedAt(time)
        .updatedAt(time)
        .build();

    MediaDto dto2 = MediaDto.builder()
        .id(111L)
        .contentHash("hash-111")
        .originalFilename("image1.png")
        .mediaType(mediaType)
        .fileSize(2048L)
        .processingStatus(ProcessingStatus.COMPLETE)
        .uploadedAt(time)
        .updatedAt(time)
        .build();

    MediaDto differentDto = MediaDto.builder()
        .id(222L)
        .contentHash("hash-222")
        .originalFilename("image2.png")
        .processingStatus(ProcessingStatus.FAILED)
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
    MediaDto dto = MediaDto.builder()
        .id(999L)
        .contentHash("test-hash-value")
        .originalFilename("document.pdf")
        .fileSize(1024000L)
        .processingStatus(ProcessingStatus.COMPLETE)
        .build();

    String toString = dto.toString();
    assertThat(toString).contains("MediaDto");
    assertThat(toString).contains("999");
    assertThat(toString).contains("test-hash-value");
    assertThat(toString).contains("document.pdf");
    assertThat(toString).contains("COMPLETE");
  }
}
