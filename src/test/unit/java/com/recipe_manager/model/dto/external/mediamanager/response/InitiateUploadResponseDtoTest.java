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
class InitiateUploadResponseDtoTest {

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
    LocalDateTime expiresAt = LocalDateTime.of(2024, 1, 1, 12, 0, 0);

    InitiateUploadResponseDto dto = InitiateUploadResponseDto.builder()
        .mediaId(123L)
        .uploadUrl("https://example.com/upload/abc123?signature=xyz")
        .uploadToken("upload_abc123def456")
        .expiresAt(expiresAt)
        .status(ProcessingStatus.INITIATED)
        .build();

    assertThat(dto.getMediaId()).isEqualTo(123L);
    assertThat(dto.getUploadUrl()).isEqualTo("https://example.com/upload/abc123?signature=xyz");
    assertThat(dto.getUploadToken()).isEqualTo("upload_abc123def456");
    assertThat(dto.getExpiresAt()).isEqualTo(expiresAt);
    assertThat(dto.getStatus()).isEqualTo(ProcessingStatus.INITIATED);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should serialize and deserialize DTO")
  void shouldSerializeAndDeserialize() throws Exception {
    LocalDateTime expiresAt = LocalDateTime.of(2024, 6, 15, 10, 30, 0);

    InitiateUploadResponseDto original = InitiateUploadResponseDto.builder()
        .mediaId(456L)
        .uploadUrl("https://api.example.com/media/upload/xyz789")
        .uploadToken("upload_xyz789abc123")
        .expiresAt(expiresAt)
        .status(ProcessingStatus.INITIATED)
        .build();

    String json = objectMapper.writeValueAsString(original);
    assertThat(json).contains("\"media_id\":456");
    assertThat(json).contains("\"upload_url\":\"https://api.example.com/media/upload/xyz789\"");
    assertThat(json).contains("\"upload_token\":\"upload_xyz789abc123\"");
    assertThat(json).contains("\"expires_at\"");
    assertThat(json).contains("\"status\":\"INITIATED\"");

    InitiateUploadResponseDto deserialized = objectMapper.readValue(json, InitiateUploadResponseDto.class);
    assertThat(deserialized).usingRecursiveComparison().isEqualTo(original);
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should handle null values properly")
  void shouldHandleNullValues() {
    InitiateUploadResponseDto dto = InitiateUploadResponseDto.builder()
        .mediaId(789L)
        .build();

    assertThat(dto.getMediaId()).isEqualTo(789L);
    assertThat(dto.getUploadUrl()).isNull();
    assertThat(dto.getUploadToken()).isNull();
    assertThat(dto.getExpiresAt()).isNull();
    assertThat(dto.getStatus()).isNull();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should properly implement equals and hashCode")
  void shouldImplementEqualsAndHashCode() {
    LocalDateTime time = LocalDateTime.now();

    InitiateUploadResponseDto dto1 = InitiateUploadResponseDto.builder()
        .mediaId(111L)
        .uploadUrl("https://example.com/upload/111")
        .uploadToken("upload_111")
        .expiresAt(time)
        .status(ProcessingStatus.INITIATED)
        .build();

    InitiateUploadResponseDto dto2 = InitiateUploadResponseDto.builder()
        .mediaId(111L)
        .uploadUrl("https://example.com/upload/111")
        .uploadToken("upload_111")
        .expiresAt(time)
        .status(ProcessingStatus.INITIATED)
        .build();

    InitiateUploadResponseDto differentDto = InitiateUploadResponseDto.builder()
        .mediaId(222L)
        .uploadUrl("https://example.com/upload/222")
        .uploadToken("upload_222")
        .expiresAt(time.plusHours(1))
        .status(ProcessingStatus.UPLOADING)
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
    InitiateUploadResponseDto dto = InitiateUploadResponseDto.builder()
        .mediaId(999L)
        .uploadUrl("https://example.com/upload")
        .uploadToken("upload_token_999")
        .expiresAt(LocalDateTime.of(2024, 12, 31, 23, 59, 59))
        .status(ProcessingStatus.INITIATED)
        .build();

    String toString = dto.toString();
    assertThat(toString).contains("InitiateUploadResponseDto");
    assertThat(toString).contains("999");
    assertThat(toString).contains("upload_token_999");
    assertThat(toString).contains("INITIATED");
  }
}
