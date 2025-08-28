package com.recipe_manager.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.recipe_manager.model.dto.media.MediaDto;
import com.recipe_manager.model.dto.request.CreateMediaRequest;
import com.recipe_manager.model.dto.response.CreateMediaResponse;
import com.recipe_manager.service.MediaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** REST controller for media management operations. */
@Slf4j
@RestController
@RequestMapping("/recipe-management")
@RequiredArgsConstructor
public class MediaController {

  /** Default page size for paginated media responses. */
  private static final int DEFAULT_PAGE_SIZE = 20;

  /** The media service for handling media operations. */
  private final MediaService mediaService;

  /**
   * Retrieves all media associated with a specific recipe.
   *
   * @param recipeId the ID of the recipe
   * @param pageable pagination information
   * @return page of media items associated with the recipe
   */
  @GetMapping(value = "/recipes/{recipeId}/media", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Page<MediaDto>> getRecipeMedia(
      @PathVariable("recipeId") final Long recipeId,
      @PageableDefault(size = DEFAULT_PAGE_SIZE) final Pageable pageable) {
    log.info("Request to get media for recipe ID: {}", recipeId);
    final Page<MediaDto> media = mediaService.getMediaByRecipeId(recipeId, pageable);
    return ResponseEntity.ok(media);
  }

  /**
   * Retrieves all media associated with a specific ingredient within a recipe.
   *
   * @param recipeId the ID of the recipe
   * @param ingredientId the ID of the ingredient
   * @param pageable pagination information
   * @return page of media items associated with the ingredient
   */
  @GetMapping(
      value = "/recipes/{recipeId}/ingredients/{ingredientId}/media",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Page<MediaDto>> getIngredientMedia(
      @PathVariable("recipeId") final Long recipeId,
      @PathVariable("ingredientId") final Long ingredientId,
      @PageableDefault(size = DEFAULT_PAGE_SIZE) final Pageable pageable) {
    log.info(
        "Request to get media for recipe ID: {} and ingredient ID: {}", recipeId, ingredientId);
    final Page<MediaDto> media =
        mediaService.getMediaByRecipeAndIngredientId(recipeId, ingredientId, pageable);
    return ResponseEntity.ok(media);
  }

  /**
   * Retrieves all media associated with a specific step within a recipe.
   *
   * @param recipeId the ID of the recipe
   * @param stepId the ID of the step
   * @param pageable pagination information
   * @return page of media items associated with the step
   */
  @GetMapping(
      value = "/recipes/{recipeId}/steps/{stepId}/media",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Page<MediaDto>> getStepMedia(
      @PathVariable("recipeId") final Long recipeId,
      @PathVariable("stepId") final Long stepId,
      @PageableDefault(size = DEFAULT_PAGE_SIZE) final Pageable pageable) {
    log.info("Request to get media for recipe ID: {} and step ID: {}", recipeId, stepId);
    final Page<MediaDto> media = mediaService.getMediaByRecipeAndStepId(recipeId, stepId, pageable);
    return ResponseEntity.ok(media);
  }

  /**
   * Creates new media and associates it with a specific recipe.
   *
   * @param recipeId the ID of the recipe to associate the media with
   * @param file the media file to upload
   * @param originalFilename the original filename
   * @param mediaType the MIME type of the media
   * @param fileSize the file size in bytes
   * @param contentHash optional content hash for integrity checking
   * @return the created media response
   */
  @PostMapping(
      value = "/recipes/{recipeId}/media",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CreateMediaResponse> createRecipeMedia(
      @PathVariable("recipeId") final Long recipeId,
      @RequestParam("file") final MultipartFile file,
      @RequestParam("originalFilename") final String originalFilename,
      @RequestParam("mediaType") final com.recipe_manager.model.enums.MediaType mediaType,
      @RequestParam("fileSize") final Long fileSize,
      @RequestParam(value = "contentHash", required = false) final String contentHash) {

    log.info("Request to create media for recipe ID: {}", recipeId);

    // Build the request object from form parameters
    final CreateMediaRequest request =
        CreateMediaRequest.builder()
            .originalFilename(originalFilename)
            .mediaType(mediaType)
            .fileSize(fileSize)
            .contentHash(contentHash)
            .build();

    final CreateMediaResponse response = mediaService.createRecipeMedia(recipeId, request, file);
    return ResponseEntity.ok(response);
  }
}
