package com.recipe_manager.unit_tests.model.entity.media;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.recipe_manager.model.entity.media.Media;
import com.recipe_manager.model.enums.MediaType;
import com.recipe_manager.model.enums.ProcessingStatus;

/**
 * Unit tests for Media entity.
 */
@Tag("unit")
class MediaTest {

  @Test
  void testBuilder_AllFields_CreatesCorrectEntity() {
    UUID userId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    Media media = Media.builder()
        .mediaId(1L)
        .userId(userId)
        .mediaType(MediaType.IMAGE_JPEG)
        .mediaPath("/path/to/media.jpg")
        .fileSize(1024L)
        .contentHash("abc123hash")
        .originalFilename("original.jpg")
        .processingStatus(ProcessingStatus.COMPLETE)
        .createdAt(now)
        .updatedAt(now)
        .build();

    assertEquals(1L, media.getMediaId());
    assertEquals(userId, media.getUserId());
    assertEquals(MediaType.IMAGE_JPEG, media.getMediaType());
    assertEquals("/path/to/media.jpg", media.getMediaPath());
    assertEquals(1024L, media.getFileSize());
    assertEquals("abc123hash", media.getContentHash());
    assertEquals("original.jpg", media.getOriginalFilename());
    assertEquals(ProcessingStatus.COMPLETE, media.getProcessingStatus());
    assertEquals(now, media.getCreatedAt());
    assertEquals(now, media.getUpdatedAt());
  }

  @Test
  void testBuilder_RequiredFieldsOnly_CreatesValidEntity() {
    UUID userId = UUID.randomUUID();

    Media media = Media.builder()
        .userId(userId)
        .mediaType(MediaType.IMAGE_PNG)
        .mediaPath("/path/to/image.png")
        .processingStatus(ProcessingStatus.PENDING)
        .build();

    assertNull(media.getMediaId());
    assertEquals(userId, media.getUserId());
    assertEquals(MediaType.IMAGE_PNG, media.getMediaType());
    assertEquals("/path/to/image.png", media.getMediaPath());
    assertNull(media.getFileSize());
    assertNull(media.getContentHash());
    assertNull(media.getOriginalFilename());
    assertEquals(ProcessingStatus.PENDING, media.getProcessingStatus());
    assertNull(media.getCreatedAt());
    assertNull(media.getUpdatedAt());
  }

  @Test
  void testNoArgsConstructor_CreatesEmptyEntity() {
    Media media = new Media();

    assertNull(media.getMediaId());
    assertNull(media.getUserId());
    assertNull(media.getMediaType());
    assertNull(media.getMediaPath());
    assertNull(media.getFileSize());
    assertNull(media.getContentHash());
    assertNull(media.getOriginalFilename());
    assertNull(media.getProcessingStatus());
    assertNull(media.getCreatedAt());
    assertNull(media.getUpdatedAt());
  }

  @Test
  void testAllArgsConstructor_AllFields_CreatesCorrectEntity() {
    UUID userId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    Media media = new Media(
        1L,
        userId,
        MediaType.VIDEO_MP4,
        "/path/to/video.mp4",
        2048L,
        "def456hash",
        "video.mp4",
        ProcessingStatus.PROCESSING,
        now,
        now
    );

    assertEquals(1L, media.getMediaId());
    assertEquals(userId, media.getUserId());
    assertEquals(MediaType.VIDEO_MP4, media.getMediaType());
    assertEquals("/path/to/video.mp4", media.getMediaPath());
    assertEquals(2048L, media.getFileSize());
    assertEquals("def456hash", media.getContentHash());
    assertEquals("video.mp4", media.getOriginalFilename());
    assertEquals(ProcessingStatus.PROCESSING, media.getProcessingStatus());
    assertEquals(now, media.getCreatedAt());
    assertEquals(now, media.getUpdatedAt());
  }

  @Test
  void testGettersAndSetters_AllFields_WorkCorrectly() {
    Media media = new Media();
    UUID userId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    media.setMediaId(1L);
    media.setUserId(userId);
    media.setMediaType(MediaType.IMAGE_GIF);
    media.setMediaPath("/path/to/gif.gif");
    media.setFileSize(512L);
    media.setContentHash("ghi789hash");
    media.setOriginalFilename("animated.gif");
    media.setProcessingStatus(ProcessingStatus.FAILED);
    media.setCreatedAt(now);
    media.setUpdatedAt(now);

    assertEquals(1L, media.getMediaId());
    assertEquals(userId, media.getUserId());
    assertEquals(MediaType.IMAGE_GIF, media.getMediaType());
    assertEquals("/path/to/gif.gif", media.getMediaPath());
    assertEquals(512L, media.getFileSize());
    assertEquals("ghi789hash", media.getContentHash());
    assertEquals("animated.gif", media.getOriginalFilename());
    assertEquals(ProcessingStatus.FAILED, media.getProcessingStatus());
    assertEquals(now, media.getCreatedAt());
    assertEquals(now, media.getUpdatedAt());
  }

  @Test
  void testEquals_SameObjects_ReturnsTrue() {
    Media media = Media.builder()
        .mediaId(1L)
        .userId(UUID.randomUUID())
        .mediaType(MediaType.IMAGE_JPEG)
        .mediaPath("/path/to/image.jpg")
        .processingStatus(ProcessingStatus.COMPLETE)
        .build();

    assertEquals(media, media);
  }

  @Test
  void testEquals_IdenticalObjects_ReturnsTrue() {
    UUID userId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    Media media1 = Media.builder()
        .mediaId(1L)
        .userId(userId)
        .mediaType(MediaType.IMAGE_JPEG)
        .mediaPath("/path/to/image.jpg")
        .fileSize(1024L)
        .contentHash("hash123")
        .originalFilename("image.jpg")
        .processingStatus(ProcessingStatus.COMPLETE)
        .createdAt(now)
        .updatedAt(now)
        .build();

    Media media2 = Media.builder()
        .mediaId(1L)
        .userId(userId)
        .mediaType(MediaType.IMAGE_JPEG)
        .mediaPath("/path/to/image.jpg")
        .fileSize(1024L)
        .contentHash("hash123")
        .originalFilename("image.jpg")
        .processingStatus(ProcessingStatus.COMPLETE)
        .createdAt(now)
        .updatedAt(now)
        .build();

    assertEquals(media1, media2);
  }

  @Test
  void testEquals_DifferentMediaId_ReturnsFalse() {
    UUID userId = UUID.randomUUID();

    Media media1 = Media.builder()
        .mediaId(1L)
        .userId(userId)
        .mediaType(MediaType.IMAGE_JPEG)
        .mediaPath("/path/to/image.jpg")
        .processingStatus(ProcessingStatus.COMPLETE)
        .build();

    Media media2 = Media.builder()
        .mediaId(2L)
        .userId(userId)
        .mediaType(MediaType.IMAGE_JPEG)
        .mediaPath("/path/to/image.jpg")
        .processingStatus(ProcessingStatus.COMPLETE)
        .build();

    assertNotEquals(media1, media2);
  }

  @Test
  void testEquals_NullObject_ReturnsFalse() {
    Media media = Media.builder()
        .mediaId(1L)
        .build();

    assertNotEquals(media, null);
  }

  @Test
  void testEquals_DifferentClass_ReturnsFalse() {
    Media media = Media.builder()
        .mediaId(1L)
        .build();

    assertNotEquals(media, "not a media object");
  }

  @Test
  void testHashCode_IdenticalObjects_ReturnsSameHashCode() {
    UUID userId = UUID.randomUUID();

    Media media1 = Media.builder()
        .mediaId(1L)
        .userId(userId)
        .mediaType(MediaType.IMAGE_JPEG)
        .mediaPath("/path/to/image.jpg")
        .processingStatus(ProcessingStatus.COMPLETE)
        .build();

    Media media2 = Media.builder()
        .mediaId(1L)
        .userId(userId)
        .mediaType(MediaType.IMAGE_JPEG)
        .mediaPath("/path/to/image.jpg")
        .processingStatus(ProcessingStatus.COMPLETE)
        .build();

    assertEquals(media1.hashCode(), media2.hashCode());
  }

  @Test
  void testHashCode_DifferentObjects_ReturnsDifferentHashCode() {
    UUID userId = UUID.randomUUID();

    Media media1 = Media.builder()
        .mediaId(1L)
        .userId(userId)
        .mediaType(MediaType.IMAGE_JPEG)
        .mediaPath("/path/to/image.jpg")
        .processingStatus(ProcessingStatus.COMPLETE)
        .build();

    Media media2 = Media.builder()
        .mediaId(2L)
        .userId(userId)
        .mediaType(MediaType.IMAGE_JPEG)
        .mediaPath("/path/to/image.jpg")
        .processingStatus(ProcessingStatus.COMPLETE)
        .build();

    assertNotEquals(media1.hashCode(), media2.hashCode());
  }

  @Test
  void testToString_ContainsExpectedFields() {
    UUID userId = UUID.randomUUID();

    Media media = Media.builder()
        .mediaId(1L)
        .userId(userId)
        .mediaType(MediaType.IMAGE_JPEG)
        .mediaPath("/path/to/image.jpg")
        .fileSize(1024L)
        .contentHash("hash123")
        .originalFilename("image.jpg")
        .processingStatus(ProcessingStatus.COMPLETE)
        .build();

    String toString = media.toString();

    assertNotNull(toString);
    // Verify key fields are present in toString
    org.junit.jupiter.api.Assertions.assertTrue(toString.contains("mediaId=1"));
    org.junit.jupiter.api.Assertions.assertTrue(toString.contains("userId=" + userId));
    org.junit.jupiter.api.Assertions.assertTrue(toString.contains("mediaType=image/jpeg"));
    org.junit.jupiter.api.Assertions.assertTrue(toString.contains("mediaPath=/path/to/image.jpg"));
    org.junit.jupiter.api.Assertions.assertTrue(toString.contains("processingStatus=COMPLETE"));
  }
}
