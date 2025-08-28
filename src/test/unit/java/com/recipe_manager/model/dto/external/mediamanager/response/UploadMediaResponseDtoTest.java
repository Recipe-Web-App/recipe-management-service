package com.recipe_manager.model.dto.external.mediamanager.response;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipe_manager.model.enums.ProcessingStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class UploadMediaResponseDtoTest {

  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should create DTO using builder pattern")
  void shouldCreateDtoUsingBuilder() {
    UploadMediaResponseDto dto = UploadMediaResponseDto.builder()
        .mediaId(123L)
        .contentHash("abcdef1234567890abcdef1234567890abcdef1234567890abcdef1234567890")
        .processingStatus(ProcessingStatus.PROCESSING)
        .uploadUrl("https://example.com/media/123")
        .build();

    assertThat(dto.getMediaId()).isEqualTo(123L);
    assertThat(dto.getContentHash())
        .isEqualTo("abcdef1234567890abcdef1234567890abcdef1234567890abcdef1234567890");
    assertThat(dto.getProcessingStatus()).isEqualTo(ProcessingStatus.PROCESSING);
    assertThat(dto.getUploadUrl()).isEqualTo("https://example.com/media/123");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should serialize and deserialize DTO")
  void shouldSerializeAndDeserialize() throws Exception {
    UploadMediaResponseDto original = UploadMediaResponseDto.builder()
        .mediaId(456L)
        .contentHash("fedcba0987654321fedcba0987654321fedcba0987654321fedcba0987654321")
        .processingStatus(ProcessingStatus.COMPLETE)
        .uploadUrl(null)
        .build();

    String json = objectMapper.writeValueAsString(original);
    assertThat(json).contains("\"media_id\":456");
    assertThat(json).contains("\"content_hash\":\"fedcba0987654321fedcba0987654321fedcba0987654321fedcba0987654321\"");
    assertThat(json).contains("\"processing_status\":\"COMPLETE\"");
    assertThat(json).contains("\"upload_url\":null");

    UploadMediaResponseDto deserialized = objectMapper.readValue(json, UploadMediaResponseDto.class);
    assertThat(deserialized).usingRecursiveComparison().isEqualTo(original);
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should handle null values properly")
  void shouldHandleNullValues() {
    UploadMediaResponseDto dto = UploadMediaResponseDto.builder()
        .mediaId(789L)
        .contentHash("1234567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef")
        .processingStatus(ProcessingStatus.INITIATED)
        .build();

    assertThat(dto.getMediaId()).isEqualTo(789L);
    assertThat(dto.getContentHash()).isNotNull();
    assertThat(dto.getProcessingStatus()).isEqualTo(ProcessingStatus.INITIATED);
    assertThat(dto.getUploadUrl()).isNull();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should properly implement equals and hashCode")
  void shouldImplementEqualsAndHashCode() {
    UploadMediaResponseDto dto1 = UploadMediaResponseDto.builder()
        .mediaId(111L)
        .contentHash("aaaa1111bbbb2222cccc3333dddd4444eeee5555ffff6666aaaa7777bbbb8888")
        .processingStatus(ProcessingStatus.UPLOADING)
        .uploadUrl("https://example.com/upload/111")
        .build();

    UploadMediaResponseDto dto2 = UploadMediaResponseDto.builder()
        .mediaId(111L)
        .contentHash("aaaa1111bbbb2222cccc3333dddd4444eeee5555ffff6666aaaa7777bbbb8888")
        .processingStatus(ProcessingStatus.UPLOADING)
        .uploadUrl("https://example.com/upload/111")
        .build();

    UploadMediaResponseDto differentDto = UploadMediaResponseDto.builder()
        .mediaId(222L)
        .contentHash("bbbb2222cccc3333dddd4444eeee5555ffff6666aaaa7777bbbb8888cccc9999")
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
  @DisplayName("Should support all ProcessingStatus values")
  void shouldSupportAllProcessingStatusValues() {
    for (ProcessingStatus status : ProcessingStatus.values()) {
      UploadMediaResponseDto dto = UploadMediaResponseDto.builder()
          .mediaId(1L)
          .contentHash("test-hash")
          .processingStatus(status)
          .build();

      assertThat(dto.getProcessingStatus()).isEqualTo(status);
    }
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should support toString method")
  void shouldSupportToString() {
    UploadMediaResponseDto dto = UploadMediaResponseDto.builder()
        .mediaId(999L)
        .contentHash("test-hash-value")
        .processingStatus(ProcessingStatus.COMPLETE)
        .uploadUrl("https://cdn.example.com/media/999")
        .build();

    String toString = dto.toString();
    assertThat(toString).contains("UploadMediaResponseDto");
    assertThat(toString).contains("999");
    assertThat(toString).contains("test-hash-value");
    assertThat(toString).contains("COMPLETE");
  }
}
