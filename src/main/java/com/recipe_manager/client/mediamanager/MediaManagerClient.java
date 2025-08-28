package com.recipe_manager.client.mediamanager;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.recipe_manager.client.common.FeignClientConfig;
import com.recipe_manager.model.dto.external.mediamanager.health.HealthResponseDto;
import com.recipe_manager.model.dto.external.mediamanager.health.ReadinessResponseDto;
import com.recipe_manager.model.dto.external.mediamanager.media.MediaDto;
import com.recipe_manager.model.dto.external.mediamanager.response.UploadMediaResponseDto;

/**
 * Feign client for media management service. Provides declarative HTTP client interface for
 * interacting with the external media management service.
 */
@FeignClient(
    name = "media-manager",
    url = "${external.services.media-manager.base-url}",
    configuration = FeignClientConfig.class,
    fallback = com.recipe_manager.service.external.mediamanager.MediaManagerFallback.class)
public interface MediaManagerClient {

  /**
   * Health check endpoint for media management service.
   *
   * @return health status with dependency information
   */
  @GetMapping("/health")
  HealthResponseDto getHealth();

  /**
   * Readiness check endpoint for media management service.
   *
   * @return readiness status with dependency information
   */
  @GetMapping("/ready")
  ReadinessResponseDto getReadiness();

  /**
   * Upload a new media file to the system.
   *
   * @param file the file to upload
   * @return upload response with media ID and metadata
   */
  @PostMapping(value = "/media/", consumes = "multipart/form-data")
  UploadMediaResponseDto uploadMedia(@RequestPart("file") MultipartFile file);

  /**
   * Retrieve a list of media files with optional filtering and pagination.
   *
   * @param limit maximum number of items to return (optional)
   * @param offset number of items to skip for pagination (optional)
   * @param status filter by processing status (optional)
   * @return list of media information
   */
  @GetMapping("/media/")
  List<MediaDto> listMedia(
      @RequestParam(value = "limit", required = false) Integer limit,
      @RequestParam(value = "offset", required = false) Integer offset,
      @RequestParam(value = "status", required = false) String status);

  /**
   * Retrieve detailed information about a specific media file.
   *
   * @param id the unique identifier of the media file
   * @return media information
   */
  @GetMapping("/media/{id}")
  MediaDto getMediaById(@PathVariable("id") Long id);

  /**
   * Permanently delete a media file and its associated database record.
   *
   * @param id the unique identifier of the media file to delete
   */
  @DeleteMapping("/media/{id}")
  void deleteMedia(@PathVariable("id") Long id);

  /**
   * Download the actual media file binary data.
   *
   * @param id the unique identifier of the media file
   * @return binary file data with appropriate content type
   */
  @GetMapping("/media/{id}/download")
  ResponseEntity<byte[]> downloadMedia(@PathVariable("id") Long id);

  /**
   * Retrieve media IDs associated with a specific recipe.
   *
   * @param recipeId the unique identifier of the recipe
   * @return list of media IDs
   */
  @GetMapping("/media/recipe/{recipeId}")
  List<Long> getMediaIdsByRecipe(@PathVariable("recipeId") Long recipeId);

  /**
   * Retrieve media IDs associated with a specific ingredient in a recipe.
   *
   * @param recipeId the unique identifier of the recipe
   * @param ingredientId the unique identifier of the ingredient
   * @return list of media IDs
   */
  @GetMapping("/media/recipe/{recipeId}/ingredient/{ingredientId}")
  List<Long> getMediaIdsByRecipeIngredient(
      @PathVariable("recipeId") Long recipeId, @PathVariable("ingredientId") Long ingredientId);

  /**
   * Retrieve media IDs associated with a specific step in a recipe.
   *
   * @param recipeId the unique identifier of the recipe
   * @param stepId the unique identifier of the step
   * @return list of media IDs
   */
  @GetMapping("/media/recipe/{recipeId}/step/{stepId}")
  List<Long> getMediaIdsByRecipeStep(
      @PathVariable("recipeId") Long recipeId, @PathVariable("stepId") Long stepId);
}
