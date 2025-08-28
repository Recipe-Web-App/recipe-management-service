package com.recipe_manager.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.recipe_manager.exception.ResourceNotFoundException;
import com.recipe_manager.model.dto.media.MediaDto;
import com.recipe_manager.model.dto.request.CreateMediaRequest;
import com.recipe_manager.model.dto.response.CreateMediaResponse;
import com.recipe_manager.model.entity.media.IngredientMedia;
import com.recipe_manager.model.entity.media.Media;
import com.recipe_manager.model.entity.media.RecipeMedia;
import com.recipe_manager.model.entity.media.StepMedia;
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.enums.ProcessingStatus;
import com.recipe_manager.model.mapper.MediaMapper;
import com.recipe_manager.repository.media.IngredientMediaRepository;
import com.recipe_manager.repository.media.MediaRepository;
import com.recipe_manager.repository.media.RecipeMediaRepository;
import com.recipe_manager.repository.media.StepMediaRepository;
import com.recipe_manager.repository.recipe.RecipeRepository;
import com.recipe_manager.service.external.mediamanager.MediaManagerService;
import com.recipe_manager.util.SecurityUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** Service for managing media operations. */
@Slf4j
@Service
@RequiredArgsConstructor
public class MediaService {

  /** Repository for managing media entities. */
  private final MediaRepository mediaRepository;

  /** Repository for managing recipe-media relationships. */
  private final RecipeMediaRepository recipeMediaRepository;

  /** Repository for managing ingredient-media relationships. */
  private final IngredientMediaRepository ingredientMediaRepository;

  /** Repository for managing step-media relationships. */
  private final StepMediaRepository stepMediaRepository;

  /** Repository for managing recipe entities. */
  private final RecipeRepository recipeRepository;

  /** Service for media manager external service integration. */
  private final MediaManagerService mediaManagerService;

  /** Mapper for converting between media entities and DTOs. */
  private final MediaMapper mediaMapper;

  /**
   * Retrieves all media associated with a specific recipe.
   *
   * @param recipeId the ID of the recipe
   * @param pageable pagination information
   * @return page of media DTOs associated with the recipe
   * @throws ResourceNotFoundException if the recipe is not found
   * @throws AccessDeniedException if the current user doesn't own the recipe
   */
  @Transactional(readOnly = true)
  public Page<MediaDto> getMediaByRecipeId(final Long recipeId, final Pageable pageable) {
    log.debug("Retrieving media for recipe ID: {}", recipeId);

    // Extract current user ID from security context
    final UUID currentUserId = SecurityUtils.getCurrentUserId();

    // Validate recipe exists and user owns it
    final Recipe recipe =
        recipeRepository
            .findById(recipeId)
            .orElseThrow(() -> ResourceNotFoundException.forEntity("Recipe", recipeId));

    if (!recipe.getUserId().equals(currentUserId)) {
      log.warn(
          "User {} attempted to access media for recipe {} owned by {}",
          currentUserId,
          recipeId,
          recipe.getUserId());
      throw new AccessDeniedException("You don't have permission to access this recipe's media");
    }

    // Get all media associations for this recipe
    final List<RecipeMedia> recipeMediaList = recipeMediaRepository.findByRecipeId(recipeId);

    // Extract media IDs and convert to paginated DTOs
    final List<Long> mediaIds =
        recipeMediaList.stream().map(RecipeMedia::getMediaId).collect(Collectors.toList());

    final Page<MediaDto> result = convertMediaIdsToPaginatedDtos(mediaIds, pageable);
    log.debug(
        "Successfully retrieved {} media items for recipe {}",
        result.getContent().size(),
        recipeId);
    return result;
  }

  /**
   * Retrieves all media associated with a specific ingredient within a recipe.
   *
   * @param recipeId the ID of the recipe
   * @param ingredientId the ID of the ingredient
   * @param pageable pagination information
   * @return page of media DTOs associated with the ingredient
   * @throws ResourceNotFoundException if the recipe is not found
   * @throws AccessDeniedException if the current user doesn't own the recipe
   */
  @Transactional(readOnly = true)
  public Page<MediaDto> getMediaByRecipeAndIngredientId(
      final Long recipeId, final Long ingredientId, final Pageable pageable) {
    log.debug("Retrieving media for recipe ID: {} and ingredient ID: {}", recipeId, ingredientId);

    // Extract current user ID from security context
    final UUID currentUserId = SecurityUtils.getCurrentUserId();

    // Validate recipe exists and user owns it
    final Recipe recipe =
        recipeRepository
            .findById(recipeId)
            .orElseThrow(() -> ResourceNotFoundException.forEntity("Recipe", recipeId));

    if (!recipe.getUserId().equals(currentUserId)) {
      log.warn(
          "User {} attempted to access media for recipe {} owned by {}",
          currentUserId,
          recipeId,
          recipe.getUserId());
      throw new AccessDeniedException("You don't have permission to access this recipe's media");
    }

    // Get all media associations for this ingredient
    final List<IngredientMedia> ingredientMediaList =
        ingredientMediaRepository.findByIdIngredientId(ingredientId);

    // Extract media IDs and convert to paginated DTOs
    final List<Long> mediaIds =
        ingredientMediaList.stream()
            .map(im -> im.getId().getMediaId())
            .collect(Collectors.toList());

    final Page<MediaDto> result = convertMediaIdsToPaginatedDtos(mediaIds, pageable);
    log.debug(
        "Successfully retrieved {} media items for ingredient {} in recipe {}",
        result.getContent().size(),
        ingredientId,
        recipeId);
    return result;
  }

  /**
   * Retrieves all media associated with a specific step within a recipe.
   *
   * @param recipeId the ID of the recipe
   * @param stepId the ID of the step
   * @param pageable pagination information
   * @return page of media DTOs associated with the step
   * @throws ResourceNotFoundException if the recipe is not found
   * @throws AccessDeniedException if the current user doesn't own the recipe
   */
  @Transactional(readOnly = true)
  public Page<MediaDto> getMediaByRecipeAndStepId(
      final Long recipeId, final Long stepId, final Pageable pageable) {
    log.debug("Retrieving media for recipe ID: {} and step ID: {}", recipeId, stepId);

    // Extract current user ID from security context
    final UUID currentUserId = SecurityUtils.getCurrentUserId();

    // Validate recipe exists and user owns it
    final Recipe recipe =
        recipeRepository
            .findById(recipeId)
            .orElseThrow(() -> ResourceNotFoundException.forEntity("Recipe", recipeId));

    if (!recipe.getUserId().equals(currentUserId)) {
      log.warn(
          "User {} attempted to access media for recipe {} owned by {}",
          currentUserId,
          recipeId,
          recipe.getUserId());
      throw new AccessDeniedException("You don't have permission to access this recipe's media");
    }

    // Get all media associations for this step
    final List<StepMedia> stepMediaList = stepMediaRepository.findByIdStepId(stepId);

    // Extract media IDs and convert to paginated DTOs
    final List<Long> mediaIds =
        stepMediaList.stream().map(sm -> sm.getId().getMediaId()).collect(Collectors.toList());

    final Page<MediaDto> result = convertMediaIdsToPaginatedDtos(mediaIds, pageable);
    log.debug(
        "Successfully retrieved {} media items for step {} in recipe {}",
        result.getContent().size(),
        stepId,
        recipeId);
    return result;
  }

  /**
   * Creates new media and associates it with a specific recipe.
   *
   * @param recipeId the ID of the recipe to associate the media with
   * @param request the create media request containing media details
   * @param file the media file to upload
   * @return the created media response
   * @throws ResourceNotFoundException if the recipe is not found
   * @throws AccessDeniedException if the current user doesn't own the recipe
   */
  @Transactional
  public CreateMediaResponse createRecipeMedia(
      final Long recipeId, final CreateMediaRequest request, final MultipartFile file) {
    log.debug("Creating media for recipe ID: {}", recipeId);

    // Extract current user ID from security context
    final UUID currentUserId = SecurityUtils.getCurrentUserId();

    // Validate recipe exists and user owns it
    final Recipe recipe =
        recipeRepository
            .findById(recipeId)
            .orElseThrow(() -> ResourceNotFoundException.forEntity("Recipe", recipeId));

    if (!recipe.getUserId().equals(currentUserId)) {
      log.warn(
          "User {} attempted to create media for recipe {} owned by {}",
          currentUserId,
          recipeId,
          recipe.getUserId());
      throw new AccessDeniedException("You don't have permission to add media to this recipe");
    }

    // Upload media to external media manager service
    final com.recipe_manager.model.dto.external.mediamanager.response.UploadMediaResponseDto
        uploadResponse = mediaManagerService.uploadMedia(file).join();

    // Create local Media entity
    final Media media =
        Media.builder()
            .userId(currentUserId)
            .mediaType(request.getMediaType())
            .mediaPath(uploadResponse.getUploadUrl()) // Use upload URL as media path
            .fileSize(request.getFileSize())
            .contentHash(request.getContentHash())
            .originalFilename(request.getOriginalFilename())
            .processingStatus(ProcessingStatus.INITIATED)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    final Media savedMedia = mediaRepository.save(media);

    // Create recipe-media association
    final RecipeMedia recipeMedia =
        RecipeMedia.builder()
            .recipeId(recipeId)
            .mediaId(savedMedia.getMediaId())
            .media(savedMedia)
            .build();

    recipeMediaRepository.save(recipeMedia);

    // Build response
    final CreateMediaResponse response =
        CreateMediaResponse.builder()
            .mediaId(savedMedia.getMediaId())
            .uploadUrl(uploadResponse.getUploadUrl())
            .contentHash(uploadResponse.getContentHash())
            .build();

    log.debug("Successfully created media {} for recipe {}", savedMedia.getMediaId(), recipeId);
    return response;
  }

  /**
   * Helper method to convert media IDs to paginated MediaDto page.
   *
   * @param mediaIds list of media IDs
   * @param pageable pagination information
   * @return paginated MediaDto page
   */
  private Page<MediaDto> convertMediaIdsToPaginatedDtos(
      final List<Long> mediaIds, final Pageable pageable) {
    final List<Media> mediaList = mediaRepository.findAllById(mediaIds);

    // Convert to DTOs
    final List<MediaDto> mediaDtos =
        mediaList.stream().map(mediaMapper::toDto).collect(Collectors.toList());

    return applyPaginationManually(mediaDtos, pageable);
  }

  /**
   * Helper method to apply pagination manually to a list of DTOs.
   *
   * @param mediaDtos list of media DTOs
   * @param pageable pagination information
   * @return paginated MediaDto page
   */
  private Page<MediaDto> applyPaginationManually(
      final List<MediaDto> mediaDtos, final Pageable pageable) {
    final int start = (int) pageable.getOffset();
    final int end = Math.min(start + pageable.getPageSize(), mediaDtos.size());
    final List<MediaDto> pageContent = mediaDtos.subList(start, end);

    return new PageImpl<>(pageContent, pageable, mediaDtos.size());
  }
}
