package com.recipe_manager.unit_tests.model.dto.media;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.recipe_manager.model.dto.media.MediaDto;
import com.recipe_manager.model.enums.MediaType;
import com.recipe_manager.model.enums.ProcessingStatus;

@Tag("unit")
class MediaDtoTest {

  private MediaDto.MediaDtoBuilder baseDtoBuilder;
  private UUID testUserId;
  private LocalDateTime testDateTime;

  @BeforeEach
  void setUp() {
    testUserId = UUID.randomUUID();
    testDateTime = LocalDateTime.now();

    baseDtoBuilder = MediaDto.builder()
        .mediaId(1L)
        .userId(testUserId)
        .mediaType(MediaType.IMAGE_JPEG)
        .originalFilename("test-image.jpg")
        .fileSize(1024L)
        .contentHash("abc123")
        .processingStatus(ProcessingStatus.COMPLETE)
        .createdAt(testDateTime)
        .updatedAt(testDateTime);
  }

  @Test
  void shouldCreateMediaDtoWithAllFields() {
    MediaDto dto = baseDtoBuilder.build();

    assertAll("MediaDto fields",
        () -> assertEquals(1L, dto.getMediaId()),
        () -> assertEquals(testUserId, dto.getUserId()),
        () -> assertEquals(MediaType.IMAGE_JPEG, dto.getMediaType()),
        () -> assertEquals("test-image.jpg", dto.getOriginalFilename()),
        () -> assertEquals(1024L, dto.getFileSize()),
        () -> assertEquals("abc123", dto.getContentHash()),
        () -> assertEquals(ProcessingStatus.COMPLETE, dto.getProcessingStatus()),
        () -> assertEquals(testDateTime, dto.getCreatedAt()),
        () -> assertEquals(testDateTime, dto.getUpdatedAt())
    );
  }

  @Test
  void shouldCreateMediaDtoWithRequiredFieldsOnly() {
    MediaDto dto = MediaDto.builder()
        .userId(testUserId)
        .mediaType(MediaType.VIDEO_MP4)
        .processingStatus(ProcessingStatus.INITIATED)
        .build();

    assertAll("Required fields only",
        () -> assertNull(dto.getMediaId()),
        () -> assertEquals(testUserId, dto.getUserId()),
        () -> assertEquals(MediaType.VIDEO_MP4, dto.getMediaType()),
        () -> assertNull(dto.getOriginalFilename()),
        () -> assertNull(dto.getFileSize()),
        () -> assertNull(dto.getContentHash()),
        () -> assertEquals(ProcessingStatus.INITIATED, dto.getProcessingStatus()),
        () -> assertNull(dto.getCreatedAt()),
        () -> assertNull(dto.getUpdatedAt())
    );
  }

  @Test
  void shouldSupportAllMediaTypes() {
    for (MediaType mediaType : MediaType.values()) {
      MediaDto dto = baseDtoBuilder.mediaType(mediaType).build();
      assertEquals(mediaType, dto.getMediaType());
    }
  }

  @Test
  void shouldSupportAllProcessingStatuses() {
    for (ProcessingStatus status : ProcessingStatus.values()) {
      MediaDto dto = baseDtoBuilder.processingStatus(status).build();
      assertEquals(status, dto.getProcessingStatus());
    }
  }

  @Test
  void shouldImplementEqualsAndHashCode() {
    MediaDto dto1 = baseDtoBuilder.build();
    MediaDto dto2 = baseDtoBuilder.build();
    MediaDto dto3 = baseDtoBuilder.mediaId(2L).build();

    assertAll("Equals and HashCode",
        () -> assertEquals(dto1, dto2),
        () -> assertEquals(dto1.hashCode(), dto2.hashCode()),
        () -> assertNotEquals(dto1, dto3),
        () -> assertNotEquals(dto1.hashCode(), dto3.hashCode())
    );
  }

  @Test
  void shouldImplementToString() {
    MediaDto dto = baseDtoBuilder.build();
    String toString = dto.toString();

    assertAll("ToString contains key fields",
        () -> assertTrue(toString.contains("mediaId=1")),
        () -> assertTrue(toString.contains("userId=" + testUserId)),
        () -> assertTrue(toString.contains("mediaType=" + MediaType.IMAGE_JPEG)),
        () -> assertTrue(toString.contains("processingStatus=" + ProcessingStatus.COMPLETE))
    );
  }

  @Test
  void shouldHandleNullValues() {
    MediaDto dto = MediaDto.builder()
        .userId(testUserId)
        .mediaType(MediaType.IMAGE_JPEG)
        .processingStatus(ProcessingStatus.INITIATED)
        .originalFilename(null)
        .contentHash(null)
        .build();

    assertAll("Null values handled",
        () -> assertEquals(testUserId, dto.getUserId()),
        () -> assertNull(dto.getOriginalFilename()),
        () -> assertNull(dto.getContentHash())
    );
  }
}
