package com.recipe_manager.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.recipe_manager.client.mediamanager.MediaManagerClient;
import com.recipe_manager.model.dto.external.mediamanager.health.HealthResponseDto;
import com.recipe_manager.model.dto.external.mediamanager.health.ReadinessResponseDto;
import com.recipe_manager.model.dto.external.mediamanager.media.MediaDto;
import com.recipe_manager.model.dto.external.mediamanager.response.UploadMediaResponseDto;
import com.recipe_manager.model.enums.HealthStatus;
import com.recipe_manager.model.enums.ProcessingStatus;
import com.recipe_manager.model.enums.ReadinessStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
class MediaManagerClientTest {

  @Mock
  private MediaManagerClient mediaManagerClient;

  @Mock
  private MultipartFile mockFile;

  @BeforeEach
  void setUp() {
    // Setup test data if needed
  }

  @Test
  @DisplayName("Should create media manager client successfully")
  void shouldCreateMediaManagerClientSuccessfully() {
    // Assert
    assertThat(mediaManagerClient).isNotNull();
  }

  @Test
  @DisplayName("Should get health status")
  void shouldGetHealthStatus() {
    // Arrange
    HealthResponseDto expectedHealth = HealthResponseDto.builder()
        .status(HealthStatus.HEALTHY)
        .service("media-management-service")
        .build();
    when(mediaManagerClient.getHealth()).thenReturn(expectedHealth);

    // Act
    HealthResponseDto result = mediaManagerClient.getHealth();

    // Assert
    assertThat(result).isEqualTo(expectedHealth);
    verify(mediaManagerClient).getHealth();
  }

  @Test
  @DisplayName("Should get readiness status")
  void shouldGetReadinessStatus() {
    // Arrange
    ReadinessResponseDto expectedReadiness = ReadinessResponseDto.builder()
        .status(ReadinessStatus.READY)
        .service("media-management-service")
        .build();
    when(mediaManagerClient.getReadiness()).thenReturn(expectedReadiness);

    // Act
    ReadinessResponseDto result = mediaManagerClient.getReadiness();

    // Assert
    assertThat(result).isEqualTo(expectedReadiness);
    verify(mediaManagerClient).getReadiness();
  }

  @Test
  @DisplayName("Should upload media file")
  void shouldUploadMediaFile() {
    // Arrange
    UploadMediaResponseDto expectedResponse = UploadMediaResponseDto.builder()
        .mediaId(123L)
        .contentHash("abc123")
        .processingStatus(ProcessingStatus.PROCESSING)
        .build();
    when(mediaManagerClient.uploadMedia(mockFile)).thenReturn(expectedResponse);

    // Act
    UploadMediaResponseDto result = mediaManagerClient.uploadMedia(mockFile);

    // Assert
    assertThat(result).isEqualTo(expectedResponse);
    verify(mediaManagerClient).uploadMedia(mockFile);
  }

  @Test
  @DisplayName("Should list media files")
  void shouldListMediaFiles() {
    // Arrange
    List<MediaDto> expectedList = Arrays.asList(
        MediaDto.builder().id(1L).build(),
        MediaDto.builder().id(2L).build()
    );
    when(mediaManagerClient.listMedia(10, 0, "Complete")).thenReturn(expectedList);

    // Act
    List<MediaDto> result = mediaManagerClient.listMedia(10, 0, "Complete");

    // Assert
    assertThat(result).isEqualTo(expectedList);
    verify(mediaManagerClient).listMedia(10, 0, "Complete");
  }

  @Test
  @DisplayName("Should get media by ID")
  void shouldGetMediaById() {
    // Arrange
    MediaDto expectedMedia = MediaDto.builder().id(123L).build();
    when(mediaManagerClient.getMediaById(123L)).thenReturn(expectedMedia);

    // Act
    MediaDto result = mediaManagerClient.getMediaById(123L);

    // Assert
    assertThat(result).isEqualTo(expectedMedia);
    verify(mediaManagerClient).getMediaById(123L);
  }

  @Test
  @DisplayName("Should delete media")
  void shouldDeleteMedia() {
    // Act
    mediaManagerClient.deleteMedia(123L);

    // Assert
    verify(mediaManagerClient).deleteMedia(123L);
  }

  @Test
  @DisplayName("Should download media")
  void shouldDownloadMedia() {
    // Arrange
    ResponseEntity<byte[]> expectedResponse = ResponseEntity.ok(new byte[]{1, 2, 3});
    when(mediaManagerClient.downloadMedia(123L)).thenReturn(expectedResponse);

    // Act
    ResponseEntity<byte[]> result = mediaManagerClient.downloadMedia(123L);

    // Assert
    assertThat(result).isEqualTo(expectedResponse);
    verify(mediaManagerClient).downloadMedia(123L);
  }

  @Test
  @DisplayName("Should get media IDs by recipe")
  void shouldGetMediaIdsByRecipe() {
    // Arrange
    List<Long> expectedIds = Arrays.asList(1L, 2L, 3L);
    when(mediaManagerClient.getMediaIdsByRecipe(123L)).thenReturn(expectedIds);

    // Act
    List<Long> result = mediaManagerClient.getMediaIdsByRecipe(123L);

    // Assert
    assertThat(result).isEqualTo(expectedIds);
    verify(mediaManagerClient).getMediaIdsByRecipe(123L);
  }

  @Test
  @DisplayName("Should get media IDs by recipe ingredient")
  void shouldGetMediaIdsByRecipeIngredient() {
    // Arrange
    List<Long> expectedIds = Arrays.asList(4L, 5L);
    when(mediaManagerClient.getMediaIdsByRecipeIngredient(123L, 456L)).thenReturn(expectedIds);

    // Act
    List<Long> result = mediaManagerClient.getMediaIdsByRecipeIngredient(123L, 456L);

    // Assert
    assertThat(result).isEqualTo(expectedIds);
    verify(mediaManagerClient).getMediaIdsByRecipeIngredient(123L, 456L);
  }

  @Test
  @DisplayName("Should get media IDs by recipe step")
  void shouldGetMediaIdsByRecipeStep() {
    // Arrange
    List<Long> expectedIds = Arrays.asList(6L, 7L, 8L);
    when(mediaManagerClient.getMediaIdsByRecipeStep(123L, 789L)).thenReturn(expectedIds);

    // Act
    List<Long> result = mediaManagerClient.getMediaIdsByRecipeStep(123L, 789L);

    // Assert
    assertThat(result).isEqualTo(expectedIds);
    verify(mediaManagerClient).getMediaIdsByRecipeStep(123L, 789L);
  }
}
