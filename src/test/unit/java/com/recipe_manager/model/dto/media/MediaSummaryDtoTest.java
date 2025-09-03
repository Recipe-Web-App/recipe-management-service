package com.recipe_manager.model.dto.media;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.recipe_manager.model.dto.media.MediaSummaryDto;
import com.recipe_manager.model.enums.MediaType;
import com.recipe_manager.model.enums.ProcessingStatus;

@Tag("unit")
class MediaSummaryDtoTest {

  private MediaSummaryDto.MediaSummaryDtoBuilder baseDtoBuilder;

  @BeforeEach
  void setUp() {
    baseDtoBuilder = MediaSummaryDto.builder()
        .mediaId(1L)
        .originalFilename("test-image.jpg")
        .mediaType(MediaType.IMAGE_JPEG)
        .fileSize(1024L)
        .processingStatus(ProcessingStatus.COMPLETE);
  }

  @Test
  void shouldCreateMediaSummaryDtoWithAllFields() {
    MediaSummaryDto dto = baseDtoBuilder.build();

    assertAll("MediaSummaryDto fields",
        () -> assertEquals(1L, dto.getMediaId()),
        () -> assertEquals("test-image.jpg", dto.getOriginalFilename()),
        () -> assertEquals(MediaType.IMAGE_JPEG, dto.getMediaType()),
        () -> assertEquals(1024L, dto.getFileSize()),
        () -> assertEquals(ProcessingStatus.COMPLETE, dto.getProcessingStatus())
    );
  }

  @Test
  void shouldCreateMediaSummaryDtoWithMinimalFields() {
    MediaSummaryDto dto = MediaSummaryDto.builder()
        .mediaId(2L)
        .mediaType(MediaType.VIDEO_MP4)
        .processingStatus(ProcessingStatus.INITIATED)
        .build();

    assertAll("Minimal fields",
        () -> assertEquals(2L, dto.getMediaId()),
        () -> assertNull(dto.getOriginalFilename()),
        () -> assertEquals(MediaType.VIDEO_MP4, dto.getMediaType()),
        () -> assertNull(dto.getFileSize()),
        () -> assertEquals(ProcessingStatus.INITIATED, dto.getProcessingStatus())
    );
  }

  @Test
  void shouldSupportAllMediaTypes() {
    for (MediaType mediaType : MediaType.values()) {
      MediaSummaryDto dto = baseDtoBuilder.mediaType(mediaType).build();
      assertEquals(mediaType, dto.getMediaType());
    }
  }

  @Test
  void shouldSupportAllProcessingStatuses() {
    for (ProcessingStatus status : ProcessingStatus.values()) {
      MediaSummaryDto dto = baseDtoBuilder.processingStatus(status).build();
      assertEquals(status, dto.getProcessingStatus());
    }
  }

  @Test
  void shouldImplementEqualsAndHashCode() {
    MediaSummaryDto dto1 = baseDtoBuilder.build();
    MediaSummaryDto dto2 = baseDtoBuilder.build();
    MediaSummaryDto dto3 = baseDtoBuilder.mediaId(2L).build();

    assertAll("Equals and HashCode",
        () -> assertEquals(dto1, dto2),
        () -> assertEquals(dto1.hashCode(), dto2.hashCode()),
        () -> assertNotEquals(dto1, dto3),
        () -> assertNotEquals(dto1.hashCode(), dto3.hashCode())
    );
  }

  @Test
  void shouldImplementToString() {
    MediaSummaryDto dto = baseDtoBuilder.build();
    String toString = dto.toString();

    assertAll("ToString contains key fields",
        () -> assertTrue(toString.contains("mediaId=1")),
        () -> assertTrue(toString.contains("originalFilename=test-image.jpg")),
        () -> assertTrue(toString.contains("mediaType=" + MediaType.IMAGE_JPEG)),
        () -> assertTrue(toString.contains("processingStatus=" + ProcessingStatus.COMPLETE))
    );
  }

  @Test
  void shouldHandleLargeFileSize() {
    long largeFileSize = 1_000_000_000L; // 1GB
    MediaSummaryDto dto = baseDtoBuilder.fileSize(largeFileSize).build();

    assertEquals(largeFileSize, dto.getFileSize());
  }

  @Test
  void shouldHandleSpecialCharactersInFilename() {
    String specialFilename = "test-file_with-special.chars (1).jpg";
    MediaSummaryDto dto = baseDtoBuilder.originalFilename(specialFilename).build();

    assertEquals(specialFilename, dto.getOriginalFilename());
  }
}
