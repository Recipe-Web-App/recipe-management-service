package com.recipe_manager.service.external.mediamanager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
class MediaManagerFallbackTest {

  private MediaManagerFallback mediaManagerFallback;

  @Mock
  private MultipartFile mockFile;

  @BeforeEach
  void setUp() {
    mediaManagerFallback = new MediaManagerFallback();
  }

  @Test
  @DisplayName("Should create media manager fallback successfully")
  void shouldCreateMediaManagerFallbackSuccessfully() {
    // Assert
    assertThat(mediaManagerFallback).isNotNull();
  }

  @Test
  @DisplayName("Should return degraded health status")
  void shouldReturnDegradedHealthStatus() {
    // Act
    HealthResponseDto result = mediaManagerFallback.getHealth();

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getStatus()).isEqualTo(HealthStatus.DEGRADED);
    assertThat(result.getService()).isEqualTo("media-management-service");
    assertThat(result.getVersion()).isEqualTo("unknown");
    assertThat(result.getResponseTimeMs()).isEqualTo(0);
    assertThat(result.getChecks().getOverall()).isEqualTo(HealthStatus.DEGRADED);
  }

  @Test
  @DisplayName("Should return not ready readiness status")
  void shouldReturnNotReadyReadinessStatus() {
    // Act
    ReadinessResponseDto result = mediaManagerFallback.getReadiness();

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getStatus()).isEqualTo(ReadinessStatus.NOT_READY);
    assertThat(result.getService()).isEqualTo("media-management-service");
    assertThat(result.getVersion()).isEqualTo("unknown");
    assertThat(result.getResponseTimeMs()).isEqualTo(0);
    assertThat(result.getChecks().getOverall()).isEqualTo(ReadinessStatus.NOT_READY);
  }

  @Test
  @DisplayName("Should return failed upload response")
  void shouldReturnFailedUploadResponse() {
    // Arrange
    when(mockFile.getOriginalFilename()).thenReturn("test.jpg");

    // Act
    UploadMediaResponseDto result = mediaManagerFallback.uploadMedia(mockFile);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getMediaId()).isEqualTo(-1L);
    assertThat(result.getContentHash()).isEqualTo("fallback");
    assertThat(result.getProcessingStatus()).isEqualTo(ProcessingStatus.FAILED);
    assertThat(result.getUploadUrl()).isNull();
  }

  @Test
  @DisplayName("Should return empty media list")
  void shouldReturnEmptyMediaList() {
    // Act
    List<MediaDto> result = mediaManagerFallback.listMedia(10, 0, "Complete");

    // Assert
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("Should return null for get media by ID")
  void shouldReturnNullForGetMediaById() {
    // Act
    MediaDto result = mediaManagerFallback.getMediaById(123L);

    // Assert
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("Should handle delete media gracefully")
  void shouldHandleDeleteMediaGracefully() {
    // Act & Assert - should not throw exception
    mediaManagerFallback.deleteMedia(123L);
  }

  @Test
  @DisplayName("Should return not found for download media")
  void shouldReturnNotFoundForDownloadMedia() {
    // Act
    ResponseEntity<byte[]> result = mediaManagerFallback.downloadMedia(123L);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  @DisplayName("Should return empty list for recipe media IDs")
  void shouldReturnEmptyListForRecipeMediaIds() {
    // Act
    List<Long> result = mediaManagerFallback.getMediaIdsByRecipe(123L);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("Should return empty list for recipe ingredient media IDs")
  void shouldReturnEmptyListForRecipeIngredientMediaIds() {
    // Act
    List<Long> result = mediaManagerFallback.getMediaIdsByRecipeIngredient(123L, 456L);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("Should return empty list for recipe step media IDs")
  void shouldReturnEmptyListForRecipeStepMediaIds() {
    // Act
    List<Long> result = mediaManagerFallback.getMediaIdsByRecipeStep(123L, 789L);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
  }
}
