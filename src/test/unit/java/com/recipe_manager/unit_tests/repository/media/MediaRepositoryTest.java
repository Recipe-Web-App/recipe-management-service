package com.recipe_manager.unit_tests.repository.media;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.recipe_manager.model.entity.media.Media;
import com.recipe_manager.model.enums.MediaType;
import com.recipe_manager.model.enums.ProcessingStatus;
import com.recipe_manager.repository.media.MediaRepository;

/**
 * Unit tests for MediaRepository.
 *
 * Tests repository methods for finding media by various criteria.
 *
 * Note: These are mock-based unit tests since the repository depends on JPA
 * infrastructure that is not easily testable in isolation.
 */
@Tag("unit")
class MediaRepositoryTest {

  private MediaRepository mediaRepository;
  private UUID testUserId;
  private LocalDateTime testDateTime;

  @BeforeEach
  void setUp() {
    mediaRepository = mock(MediaRepository.class);
    testUserId = UUID.randomUUID();
    testDateTime = LocalDateTime.now();
  }

  @Test
  @DisplayName("Should find media by user ID")
  void shouldFindMediaByUserId() {
    // Given
    List<Media> expectedMedia = Arrays.asList(
        createTestMedia(1L, testUserId, MediaType.IMAGE_JPEG, ProcessingStatus.COMPLETE),
        createTestMedia(2L, testUserId, MediaType.VIDEO_MP4, ProcessingStatus.PROCESSING)
    );
    when(mediaRepository.findByUserId(testUserId)).thenReturn(expectedMedia);

    // When
    List<Media> result = mediaRepository.findByUserId(testUserId);

    // Then
    assertThat(result).hasSize(2);
    assertThat(result).containsExactlyElementsOf(expectedMedia);
    assertThat(result.get(0).getUserId()).isEqualTo(testUserId);
    assertThat(result.get(1).getUserId()).isEqualTo(testUserId);
  }

  @Test
  @DisplayName("Should return empty list when user has no media")
  void shouldReturnEmptyListWhenUserHasNoMedia() {
    // Given
    when(mediaRepository.findByUserId(testUserId)).thenReturn(Collections.emptyList());

    // When
    List<Media> result = mediaRepository.findByUserId(testUserId);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("Should find media by media type")
  void shouldFindMediaByMediaType() {
    // Given
    List<Media> expectedMedia = Arrays.asList(
        createTestMedia(1L, testUserId, MediaType.IMAGE_JPEG, ProcessingStatus.COMPLETE),
        createTestMedia(2L, UUID.randomUUID(), MediaType.IMAGE_JPEG, ProcessingStatus.FAILED)
    );
    when(mediaRepository.findByMediaType(MediaType.IMAGE_JPEG)).thenReturn(expectedMedia);

    // When
    List<Media> result = mediaRepository.findByMediaType(MediaType.IMAGE_JPEG);

    // Then
    assertThat(result).hasSize(2);
    assertThat(result).containsExactlyElementsOf(expectedMedia);
    assertThat(result).allMatch(media -> media.getMediaType() == MediaType.IMAGE_JPEG);
  }

  @Test
  @DisplayName("Should find media by processing status")
  void shouldFindMediaByProcessingStatus() {
    // Given
    List<Media> expectedMedia = Arrays.asList(
        createTestMedia(1L, testUserId, MediaType.IMAGE_JPEG, ProcessingStatus.PENDING),
        createTestMedia(2L, UUID.randomUUID(), MediaType.VIDEO_MP4, ProcessingStatus.PENDING)
    );
    when(mediaRepository.findByProcessingStatus(ProcessingStatus.PENDING)).thenReturn(expectedMedia);

    // When
    List<Media> result = mediaRepository.findByProcessingStatus(ProcessingStatus.PENDING);

    // Then
    assertThat(result).hasSize(2);
    assertThat(result).containsExactlyElementsOf(expectedMedia);
    assertThat(result).allMatch(media -> media.getProcessingStatus() == ProcessingStatus.PENDING);
  }

  @Test
  @DisplayName("Should find user's media by media type")
  void shouldFindUserMediaByMediaType() {
    // Given
    List<Media> expectedMedia = Arrays.asList(
        createTestMedia(1L, testUserId, MediaType.VIDEO_MP4, ProcessingStatus.COMPLETE),
        createTestMedia(2L, testUserId, MediaType.VIDEO_MP4, ProcessingStatus.PROCESSING)
    );
    when(mediaRepository.findByUserIdAndMediaType(testUserId, MediaType.VIDEO_MP4))
        .thenReturn(expectedMedia);

    // When
    List<Media> result = mediaRepository.findByUserIdAndMediaType(testUserId, MediaType.VIDEO_MP4);

    // Then
    assertThat(result).hasSize(2);
    assertThat(result).containsExactlyElementsOf(expectedMedia);
    assertThat(result).allMatch(media ->
        media.getUserId().equals(testUserId) && media.getMediaType() == MediaType.VIDEO_MP4);
  }

  @Test
  @DisplayName("Should find user's media by processing status")
  void shouldFindUserMediaByProcessingStatus() {
    // Given
    List<Media> expectedMedia = Arrays.asList(
        createTestMedia(1L, testUserId, MediaType.IMAGE_PNG, ProcessingStatus.FAILED),
        createTestMedia(2L, testUserId, MediaType.VIDEO_WEBM, ProcessingStatus.FAILED)
    );
    when(mediaRepository.findByUserIdAndProcessingStatus(testUserId, ProcessingStatus.FAILED))
        .thenReturn(expectedMedia);

    // When
    List<Media> result = mediaRepository.findByUserIdAndProcessingStatus(testUserId, ProcessingStatus.FAILED);

    // Then
    assertThat(result).hasSize(2);
    assertThat(result).containsExactlyElementsOf(expectedMedia);
    assertThat(result).allMatch(media ->
        media.getUserId().equals(testUserId) && media.getProcessingStatus() == ProcessingStatus.FAILED);
  }

  @Test
  @DisplayName("Should count media by user ID")
  void shouldCountMediaByUserId() {
    // Given
    when(mediaRepository.countByUserId(testUserId)).thenReturn(5L);

    // When
    long count = mediaRepository.countByUserId(testUserId);

    // Then
    assertThat(count).isEqualTo(5L);
  }

  @Test
  @DisplayName("Should return zero count when user has no media")
  void shouldReturnZeroCountWhenUserHasNoMedia() {
    // Given
    when(mediaRepository.countByUserId(testUserId)).thenReturn(0L);

    // When
    long count = mediaRepository.countByUserId(testUserId);

    // Then
    assertThat(count).isZero();
  }

  @Test
  @DisplayName("Should check if media exists for user")
  void shouldCheckIfMediaExistsForUser() {
    // Given
    when(mediaRepository.existsByUserId(testUserId)).thenReturn(true);

    // When
    boolean exists = mediaRepository.existsByUserId(testUserId);

    // Then
    assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("Should return false when checking non-existent user media")
  void shouldReturnFalseWhenCheckingNonExistentUserMedia() {
    // Given
    when(mediaRepository.existsByUserId(testUserId)).thenReturn(false);

    // When
    boolean exists = mediaRepository.existsByUserId(testUserId);

    // Then
    assertThat(exists).isFalse();
  }

  @Test
  @DisplayName("Should handle different media types correctly")
  void shouldHandleDifferentMediaTypesCorrectly() {
    // Given
    List<Media> imageMedia = Arrays.asList(
        createTestMedia(1L, testUserId, MediaType.IMAGE_JPEG, ProcessingStatus.COMPLETE),
        createTestMedia(2L, testUserId, MediaType.IMAGE_PNG, ProcessingStatus.COMPLETE)
    );
    List<Media> videoMedia = Arrays.asList(
        createTestMedia(3L, testUserId, MediaType.VIDEO_MP4, ProcessingStatus.COMPLETE)
    );

    when(mediaRepository.findByUserIdAndMediaType(testUserId, MediaType.IMAGE_JPEG))
        .thenReturn(Arrays.asList(imageMedia.get(0)));
    when(mediaRepository.findByUserIdAndMediaType(testUserId, MediaType.VIDEO_MP4))
        .thenReturn(videoMedia);

    // When
    List<Media> jpegResults = mediaRepository.findByUserIdAndMediaType(testUserId, MediaType.IMAGE_JPEG);
    List<Media> mp4Results = mediaRepository.findByUserIdAndMediaType(testUserId, MediaType.VIDEO_MP4);

    // Then
    assertThat(jpegResults).hasSize(1);
    assertThat(jpegResults.get(0).getMediaType()).isEqualTo(MediaType.IMAGE_JPEG);

    assertThat(mp4Results).hasSize(1);
    assertThat(mp4Results.get(0).getMediaType()).isEqualTo(MediaType.VIDEO_MP4);
  }

  @Test
  @DisplayName("Should handle all processing statuses")
  void shouldHandleAllProcessingStatuses() {
    // Given
    when(mediaRepository.findByProcessingStatus(ProcessingStatus.PENDING))
        .thenReturn(Arrays.asList(createTestMedia(1L, testUserId, MediaType.IMAGE_JPEG, ProcessingStatus.PENDING)));
    when(mediaRepository.findByProcessingStatus(ProcessingStatus.PROCESSING))
        .thenReturn(Arrays.asList(createTestMedia(2L, testUserId, MediaType.IMAGE_JPEG, ProcessingStatus.PROCESSING)));
    when(mediaRepository.findByProcessingStatus(ProcessingStatus.COMPLETE))
        .thenReturn(Arrays.asList(createTestMedia(3L, testUserId, MediaType.IMAGE_JPEG, ProcessingStatus.COMPLETE)));
    when(mediaRepository.findByProcessingStatus(ProcessingStatus.FAILED))
        .thenReturn(Arrays.asList(createTestMedia(4L, testUserId, MediaType.IMAGE_JPEG, ProcessingStatus.FAILED)));

    // When & Then
    for (ProcessingStatus status : ProcessingStatus.values()) {
      List<Media> result = mediaRepository.findByProcessingStatus(status);
      assertThat(result).hasSize(1);
      assertThat(result.get(0).getProcessingStatus()).isEqualTo(status);
    }
  }

  private Media createTestMedia(Long mediaId, UUID userId, MediaType mediaType, ProcessingStatus processingStatus) {
    return Media.builder()
        .mediaId(mediaId)
        .userId(userId)
        .mediaType(mediaType)
        .mediaPath("/test/path/media" + mediaId + getFileExtension(mediaType))
        .fileSize(1024L * mediaId)
        .contentHash("hash" + mediaId)
        .originalFilename("test" + mediaId + getFileExtension(mediaType))
        .processingStatus(processingStatus)
        .createdAt(testDateTime)
        .updatedAt(testDateTime)
        .build();
  }

  private String getFileExtension(MediaType mediaType) {
    switch (mediaType) {
      case IMAGE_JPEG: return ".jpg";
      case IMAGE_PNG: return ".png";
      case VIDEO_MP4: return ".mp4";
      case VIDEO_WEBM: return ".webm";
      default: return ".bin";
    }
  }
}
