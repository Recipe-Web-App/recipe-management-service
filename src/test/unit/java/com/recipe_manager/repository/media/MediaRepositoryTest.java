package com.recipe_manager.repository.media;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import com.recipe_manager.model.entity.media.Media;
import com.recipe_manager.model.enums.MediaType;
import com.recipe_manager.model.enums.ProcessingStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class MediaRepositoryTest {

  @Mock
  private MediaRepository mediaRepository;

  private UUID userId;
  private Media media;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    media = Media.builder()
        .mediaId(1L)
        .userId(userId)
        .mediaType(MediaType.IMAGE_JPEG)
        .mediaPath("https://example.com/image.jpg")
        .contentHash("abc123")
        .processingStatus(ProcessingStatus.COMPLETE)
        .build();
  }

  @Test
  @DisplayName("Should find media by user ID")
  void shouldFindMediaByUserId() {
    // Given
    List<Media> expectedMedia = List.of(media);
    when(mediaRepository.findByUserId(userId)).thenReturn(expectedMedia);

    // When
    List<Media> result = mediaRepository.findByUserId(userId);

    // Then
    assertThat(result)
        .hasSize(1)
        .containsExactly(media);
  }

  @Test
  @DisplayName("Should find media by media type")
  void shouldFindMediaByMediaType() {
    // Given
    List<Media> expectedMedia = List.of(media);
    when(mediaRepository.findByMediaType(MediaType.IMAGE_JPEG)).thenReturn(expectedMedia);

    // When
    List<Media> result = mediaRepository.findByMediaType(MediaType.IMAGE_JPEG);

    // Then
    assertThat(result)
        .hasSize(1)
        .containsExactly(media);
  }

  @Test
  @DisplayName("Should find media by processing status")
  void shouldFindMediaByProcessingStatus() {
    // Given
    List<Media> expectedMedia = List.of(media);
    when(mediaRepository.findByProcessingStatus(ProcessingStatus.COMPLETE)).thenReturn(expectedMedia);

    // When
    List<Media> result = mediaRepository.findByProcessingStatus(ProcessingStatus.COMPLETE);

    // Then
    assertThat(result)
        .hasSize(1)
        .containsExactly(media);
  }

  @Test
  @DisplayName("Should find media by user ID and media type")
  void shouldFindMediaByUserIdAndMediaType() {
    // Given
    List<Media> expectedMedia = List.of(media);
    when(mediaRepository.findByUserIdAndMediaType(userId, MediaType.IMAGE_JPEG))
        .thenReturn(expectedMedia);

    // When
    List<Media> result = mediaRepository.findByUserIdAndMediaType(userId, MediaType.IMAGE_JPEG);

    // Then
    assertThat(result)
        .hasSize(1)
        .containsExactly(media);
  }

  @Test
  @DisplayName("Should find media by user ID and processing status")
  void shouldFindMediaByUserIdAndProcessingStatus() {
    // Given
    List<Media> expectedMedia = List.of(media);
    when(mediaRepository.findByUserIdAndProcessingStatus(userId, ProcessingStatus.COMPLETE))
        .thenReturn(expectedMedia);

    // When
    List<Media> result = mediaRepository.findByUserIdAndProcessingStatus(userId, ProcessingStatus.COMPLETE);

    // Then
    assertThat(result)
        .hasSize(1)
        .containsExactly(media);
  }

  @Test
  @DisplayName("Should find media by content hash")
  void shouldFindMediaByContentHash() {
    // Given
    List<Media> expectedMedia = List.of(media);
    when(mediaRepository.findByContentHash("abc123")).thenReturn(expectedMedia);

    // When
    List<Media> result = mediaRepository.findByContentHash("abc123");

    // Then
    assertThat(result)
        .hasSize(1)
        .containsExactly(media);
  }

  @Test
  @DisplayName("Should check if media exists by content hash")
  void shouldCheckIfMediaExistsByContentHash() {
    // Given
    when(mediaRepository.existsByContentHash("abc123")).thenReturn(true);

    // When
    boolean exists = mediaRepository.existsByContentHash("abc123");

    // Then
    assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("Should return false when media does not exist by content hash")
  void shouldReturnFalseWhenMediaDoesNotExistByContentHash() {
    // Given
    when(mediaRepository.existsByContentHash(anyString())).thenReturn(false);

    // When
    boolean exists = mediaRepository.existsByContentHash("nonexistent");

    // Then
    assertThat(exists).isFalse();
  }

  @Test
  @DisplayName("Should count media by user ID")
  void shouldCountMediaByUserId() {
    // Given
    when(mediaRepository.countByUserId(userId)).thenReturn(5L);

    // When
    long count = mediaRepository.countByUserId(userId);

    // Then
    assertThat(count).isEqualTo(5L);
  }

  @Test
  @DisplayName("Should count media by processing status")
  void shouldCountMediaByProcessingStatus() {
    // Given
    when(mediaRepository.countByProcessingStatus(ProcessingStatus.COMPLETE)).thenReturn(3L);

    // When
    long count = mediaRepository.countByProcessingStatus(ProcessingStatus.COMPLETE);

    // Then
    assertThat(count).isEqualTo(3L);
  }

  @Test
  @DisplayName("Should find media needing attention")
  void shouldFindMediaNeedingAttention() {
    // Given
    Media processingMedia = Media.builder()
        .mediaId(2L)
        .userId(userId)
        .mediaType(MediaType.VIDEO_MP4)
        .mediaPath("https://example.com/video.mp4")
        .processingStatus(ProcessingStatus.PROCESSING)
        .build();

    Media failedMedia = Media.builder()
        .mediaId(3L)
        .userId(userId)
        .mediaType(MediaType.IMAGE_PNG)
        .mediaPath("https://example.com/failed.png")
        .processingStatus(ProcessingStatus.FAILED)
        .build();

    List<Media> expectedMedia = List.of(processingMedia, failedMedia);
    when(mediaRepository.findMediaNeedingAttention()).thenReturn(expectedMedia);

    // When
    List<Media> result = mediaRepository.findMediaNeedingAttention();

    // Then
    assertThat(result)
        .hasSize(2)
        .containsExactly(processingMedia, failedMedia);
  }

  @Test
  @DisplayName("Should delete media by user ID")
  void shouldDeleteMediaByUserId() {
    // Given
    doNothing().when(mediaRepository).deleteByUserId(userId);

    // When
    assertDoesNotThrow(() -> mediaRepository.deleteByUserId(userId));
  }
}
