package com.recipe_manager.model.dto.external.mediamanager.response;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.recipe_manager.model.enums.ProcessingStatus;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class UploadStatusResponseDtoTest {

  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should create DTO using builder pattern with all fields")
  void shouldCreateDtoUsingBuilderWithAllFields() {
    LocalDateTime uploadedAt = LocalDateTime.of(2024, 1, 1, 10, 0, 0);
    LocalDateTime completedAt = LocalDateTime.of(2024, 1, 1, 10, 0, 30);

    UploadStatusResponseDto dto = UploadStatusResponseDto.builder()
        .mediaId(123L)
        .status(ProcessingStatus.COMPLETE)
        .progress(100)
        .errorMessage(null)
        .downloadUrl("https://example.com/download/123")
        .processingTimeMs(30000L)
        .uploadedAt(uploadedAt)
        .completedAt(completedAt)
        .build();

    assertThat(dto.getMediaId()).isEqualTo(123L);
    assertThat(dto.getStatus()).isEqualTo(ProcessingStatus.COMPLETE);
    assertThat(dto.getProgress()).isEqualTo(100);
    assertThat(dto.getErrorMessage()).isNull();
    assertThat(dto.getDownloadUrl()).isEqualTo("https://example.com/download/123");
    assertThat(dto.getProcessingTimeMs()).isEqualTo(30000L);
    assertThat(dto.getUploadedAt()).isEqualTo(uploadedAt);
    assertThat(dto.getCompletedAt()).isEqualTo(completedAt);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should serialize and deserialize DTO")
  void shouldSerializeAndDeserialize() throws Exception {
    UploadStatusResponseDto original = UploadStatusResponseDto.builder()
        .mediaId(456L)
        .status(ProcessingStatus.PROCESSING)
        .progress(65)
        .errorMessage(null)
        .downloadUrl(null)
        .processingTimeMs(null)
        .uploadedAt(LocalDateTime.of(2024, 2, 15, 14, 30, 0))
        .completedAt(null)
        .build();

    String json = objectMapper.writeValueAsString(original);
    assertThat(json).contains("\"media_id\":456");
    assertThat(json).contains("\"status\":\"PROCESSING\"");
    assertThat(json).contains("\"progress\":65");
    assertThat(json).contains("\"error_message\":null");
    assertThat(json).contains("\"download_url\":null");

    UploadStatusResponseDto deserialized = objectMapper.readValue(json, UploadStatusResponseDto.class);
    assertThat(deserialized).usingRecursiveComparison().isEqualTo(original);
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should handle failed status with error message")
  void shouldHandleFailedStatusWithErrorMessage() {
    LocalDateTime uploadedAt = LocalDateTime.of(2024, 3, 1, 9, 0, 0);
    LocalDateTime completedAt = LocalDateTime.of(2024, 3, 1, 9, 0, 5);

    UploadStatusResponseDto dto = UploadStatusResponseDto.builder()
        .mediaId(789L)
        .status(ProcessingStatus.FAILED)
        .progress(null)
        .errorMessage("Corrupted file header detected")
        .downloadUrl(null)
        .processingTimeMs(5000L)
        .uploadedAt(uploadedAt)
        .completedAt(completedAt)
        .build();

    assertThat(dto.getStatus()).isEqualTo(ProcessingStatus.FAILED);
    assertThat(dto.getProgress()).isNull();
    assertThat(dto.getErrorMessage()).isEqualTo("Corrupted file header detected");
    assertThat(dto.getDownloadUrl()).isNull();
    assertThat(dto.getProcessingTimeMs()).isEqualTo(5000L);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle pending status")
  void shouldHandlePendingStatus() {
    UploadStatusResponseDto dto = UploadStatusResponseDto.builder()
        .mediaId(111L)
        .status(ProcessingStatus.INITIATED)
        .progress(null)
        .errorMessage(null)
        .downloadUrl(null)
        .processingTimeMs(null)
        .uploadedAt(null)
        .completedAt(null)
        .build();

    assertThat(dto.getMediaId()).isEqualTo(111L);
    assertThat(dto.getStatus()).isEqualTo(ProcessingStatus.INITIATED);
    assertThat(dto.getProgress()).isNull();
    assertThat(dto.getErrorMessage()).isNull();
    assertThat(dto.getDownloadUrl()).isNull();
    assertThat(dto.getProcessingTimeMs()).isNull();
    assertThat(dto.getUploadedAt()).isNull();
    assertThat(dto.getCompletedAt()).isNull();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should properly implement equals and hashCode")
  void shouldImplementEqualsAndHashCode() {
    LocalDateTime time = LocalDateTime.now();

    UploadStatusResponseDto dto1 = UploadStatusResponseDto.builder()
        .mediaId(222L)
        .status(ProcessingStatus.COMPLETE)
        .progress(100)
        .downloadUrl("https://example.com/222")
        .uploadedAt(time)
        .completedAt(time.plusMinutes(1))
        .build();

    UploadStatusResponseDto dto2 = UploadStatusResponseDto.builder()
        .mediaId(222L)
        .status(ProcessingStatus.COMPLETE)
        .progress(100)
        .downloadUrl("https://example.com/222")
        .uploadedAt(time)
        .completedAt(time.plusMinutes(1))
        .build();

    UploadStatusResponseDto differentDto = UploadStatusResponseDto.builder()
        .mediaId(333L)
        .status(ProcessingStatus.PROCESSING)
        .progress(50)
        .build();

    assertThat(dto1)
        .isEqualTo(dto2)
        .hasSameHashCodeAs(dto2)
        .isNotEqualTo(differentDto)
        .doesNotHaveSameHashCodeAs(differentDto);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should validate progress values")
  void shouldValidateProgressValues() {
    UploadStatusResponseDto dto1 = UploadStatusResponseDto.builder()
        .mediaId(1L)
        .status(ProcessingStatus.PROCESSING)
        .progress(0)
        .build();

    UploadStatusResponseDto dto2 = UploadStatusResponseDto.builder()
        .mediaId(2L)
        .status(ProcessingStatus.PROCESSING)
        .progress(50)
        .build();

    UploadStatusResponseDto dto3 = UploadStatusResponseDto.builder()
        .mediaId(3L)
        .status(ProcessingStatus.COMPLETE)
        .progress(100)
        .build();

    assertThat(dto1.getProgress()).isEqualTo(0);
    assertThat(dto2.getProgress()).isEqualTo(50);
    assertThat(dto3.getProgress()).isEqualTo(100);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should support toString method")
  void shouldSupportToString() {
    UploadStatusResponseDto dto = UploadStatusResponseDto.builder()
        .mediaId(999L)
        .status(ProcessingStatus.COMPLETE)
        .progress(100)
        .downloadUrl("https://cdn.example.com/999")
        .processingTimeMs(2500L)
        .build();

    String toString = dto.toString();
    assertThat(toString).contains("UploadStatusResponseDto");
    assertThat(toString).contains("999");
    assertThat(toString).contains("COMPLETE");
    assertThat(toString).contains("100");
  }
}
