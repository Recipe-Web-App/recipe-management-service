package com.recipe_manager.service.external.mediamanager;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.recipe_manager.client.mediamanager.MediaManagerClient;
import com.recipe_manager.model.dto.external.mediamanager.health.HealthResponseDto;
import com.recipe_manager.model.dto.external.mediamanager.health.ReadinessResponseDto;
import com.recipe_manager.model.dto.external.mediamanager.media.MediaDto;
import com.recipe_manager.model.dto.external.mediamanager.response.UploadMediaResponseDto;
import com.recipe_manager.model.enums.HealthStatus;
import com.recipe_manager.model.enums.ProcessingStatus;
import com.recipe_manager.model.enums.ReadinessStatus;

/**
 * Fallback implementation for media manager service. Provides graceful degradation when the media
 * manager service is unavailable, returning appropriate fallback responses instead of failing the
 * request. This implementation is co-located with MediaManagerService for better code organization
 * and maintenance of related functionality.
 */
@Component
public final class MediaManagerFallback implements MediaManagerClient {

  /** Logger for fallback operations. */
  private static final Logger LOGGER = LoggerFactory.getLogger(MediaManagerFallback.class);

  @Override
  public HealthResponseDto getHealth() {
    LOGGER.warn("Media manager service unavailable, using fallback health response");

    return HealthResponseDto.builder()
        .status(HealthStatus.DEGRADED)
        .service("media-management-service")
        .version("unknown")
        .timestamp(java.time.LocalDateTime.now())
        .responseTimeMs(0)
        .checks(HealthResponseDto.HealthChecksDto.builder().overall(HealthStatus.DEGRADED).build())
        .build();
  }

  @Override
  public ReadinessResponseDto getReadiness() {
    LOGGER.warn("Media manager service unavailable, using fallback readiness response");

    return ReadinessResponseDto.builder()
        .status(ReadinessStatus.NOT_READY)
        .service("media-management-service")
        .version("unknown")
        .timestamp(java.time.LocalDateTime.now())
        .responseTimeMs(0)
        .checks(
            ReadinessResponseDto.ReadinessChecksDto.builder()
                .overall(ReadinessStatus.NOT_READY)
                .build())
        .build();
  }

  @Override
  public UploadMediaResponseDto uploadMedia(final MultipartFile file) {
    LOGGER.warn(
        "Media manager service unavailable for upload of file {}, using fallback response",
        file.getOriginalFilename());

    return UploadMediaResponseDto.builder()
        .mediaId(-1L)
        .contentHash("fallback")
        .processingStatus(ProcessingStatus.FAILED)
        .uploadUrl(null)
        .build();
  }

  @Override
  public List<MediaDto> listMedia(final Integer limit, final Integer offset, final String status) {
    LOGGER.warn("Media manager service unavailable for list media, returning empty list");
    return Collections.emptyList();
  }

  @Override
  public MediaDto getMediaById(final Long id) {
    LOGGER.warn("Media manager service unavailable for get media {}, returning null", id);
    return null;
  }

  @Override
  public void deleteMedia(final Long id) {
    LOGGER.warn("Media manager service unavailable for delete media {}, operation ignored", id);
    // No-op - graceful degradation for delete operations
  }

  @Override
  public ResponseEntity<byte[]> downloadMedia(final Long id) {
    LOGGER.warn(
        "Media manager service unavailable for download media {}, returning empty response", id);
    return ResponseEntity.notFound().build();
  }

  @Override
  public List<Long> getMediaIdsByRecipe(final Long recipeId) {
    LOGGER.warn(
        "Media manager service unavailable for recipe {} media IDs, returning empty list",
        recipeId);
    return Collections.emptyList();
  }

  @Override
  public List<Long> getMediaIdsByRecipeIngredient(final Long recipeId, final Long ingredientId) {
    LOGGER.warn(
        "Media manager service unavailable for recipe {} ingredient {} media IDs, returning empty list",
        recipeId,
        ingredientId);
    return Collections.emptyList();
  }

  @Override
  public List<Long> getMediaIdsByRecipeStep(final Long recipeId, final Long stepId) {
    LOGGER.warn(
        "Media manager service unavailable for recipe {} step {} media IDs, returning empty list",
        recipeId,
        stepId);
    return Collections.emptyList();
  }
}
