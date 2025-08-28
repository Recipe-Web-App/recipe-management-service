package com.recipe_manager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.multipart.MultipartFile;

import com.recipe_manager.exception.ResourceNotFoundException;
import com.recipe_manager.model.dto.external.mediamanager.response.UploadMediaResponseDto;
import com.recipe_manager.model.dto.media.MediaDto;
import com.recipe_manager.model.dto.request.CreateMediaRequest;
import com.recipe_manager.model.dto.response.CreateMediaResponse;
import com.recipe_manager.model.dto.response.DeleteMediaResponse;
import com.recipe_manager.model.entity.media.IngredientMedia;
import com.recipe_manager.model.entity.media.IngredientMediaId;
import com.recipe_manager.model.entity.media.Media;
import com.recipe_manager.model.entity.media.RecipeMedia;
import com.recipe_manager.model.entity.media.StepMedia;
import com.recipe_manager.model.entity.media.StepMediaId;
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.enums.MediaType;
import com.recipe_manager.model.enums.ProcessingStatus;
import com.recipe_manager.model.mapper.MediaMapper;
import com.recipe_manager.repository.media.IngredientMediaRepository;
import com.recipe_manager.repository.media.MediaRepository;
import com.recipe_manager.repository.media.RecipeMediaRepository;
import com.recipe_manager.repository.media.StepMediaRepository;
import com.recipe_manager.repository.recipe.RecipeRepository;
import com.recipe_manager.service.external.mediamanager.MediaManagerService;
import com.recipe_manager.util.SecurityUtils;

/** Unit tests for MediaService. */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class MediaServiceTest {

  @Mock private MediaRepository mediaRepository;
  @Mock private RecipeMediaRepository recipeMediaRepository;
  @Mock private IngredientMediaRepository ingredientMediaRepository;
  @Mock private StepMediaRepository stepMediaRepository;
  @Mock private RecipeRepository recipeRepository;
  @Mock private MediaManagerService mediaManagerService;
  @Mock private MediaMapper mediaMapper;
  @InjectMocks private MediaService mediaService;

  private UUID currentUserId;
  private UUID differentUserId;
  private Long recipeId;
  private Long ingredientId;
  private Long stepId;
  private Long mediaId1;
  private Long mediaId2;
  private Recipe recipe;
  private Media media1;
  private Media media2;
  private MediaDto mediaDto1;
  private MediaDto mediaDto2;
  private Pageable pageable;

  @BeforeEach
  void setUp() {
    currentUserId = UUID.randomUUID();
    differentUserId = UUID.randomUUID();
    recipeId = 123L;
    ingredientId = 456L;
    stepId = 789L;
    mediaId1 = 1L;
    mediaId2 = 2L;
    pageable = PageRequest.of(0, 10);

    // Create test recipe
    recipe =
        Recipe.builder()
            .recipeId(recipeId)
            .userId(currentUserId)
            .title("Test Recipe")
            .build();

    // Create test media entities
    media1 =
        Media.builder()
            .mediaId(mediaId1)
            .userId(currentUserId)
            .mediaType(MediaType.IMAGE_JPEG)
            .mediaPath("/path/to/media1.jpg")
            .fileSize(1024L)
            .contentHash("abc123")
            .originalFilename("test1.jpg")
            .processingStatus(ProcessingStatus.COMPLETE)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    media2 =
        Media.builder()
            .mediaId(mediaId2)
            .userId(currentUserId)
            .mediaType(MediaType.VIDEO_MP4)
            .mediaPath("/path/to/media2.mp4")
            .fileSize(2048L)
            .contentHash("def456")
            .originalFilename("test2.mp4")
            .processingStatus(ProcessingStatus.COMPLETE)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    // Create test media DTOs
    mediaDto1 =
        MediaDto.builder()
            .mediaId(mediaId1)
            .userId(currentUserId)
            .mediaType(MediaType.IMAGE_JPEG)
            .originalFilename("test1.jpg")
            .fileSize(1024L)
            .contentHash("abc123")
            .processingStatus(ProcessingStatus.COMPLETE)
            .createdAt(media1.getCreatedAt())
            .updatedAt(media1.getUpdatedAt())
            .build();

    mediaDto2 =
        MediaDto.builder()
            .mediaId(mediaId2)
            .userId(currentUserId)
            .mediaType(MediaType.VIDEO_MP4)
            .originalFilename("test2.mp4")
            .fileSize(2048L)
            .contentHash("def456")
            .processingStatus(ProcessingStatus.COMPLETE)
            .createdAt(media2.getCreatedAt())
            .updatedAt(media2.getUpdatedAt())
            .build();
  }

  @Test
  void getMediaByRecipeId_Success() {
    // Arrange
    RecipeMedia recipeMedia1 = RecipeMedia.builder()
        .recipeId(recipeId)
        .mediaId(mediaId1)
        .build();
    RecipeMedia recipeMedia2 = RecipeMedia.builder()
        .recipeId(recipeId)
        .mediaId(mediaId2)
        .build();
    List<RecipeMedia> recipeMediaList = Arrays.asList(recipeMedia1, recipeMedia2);
    List<Media> mediaList = Arrays.asList(media1, media2);

    try (MockedStatic<SecurityUtils> securityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
      when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
      when(recipeMediaRepository.findByRecipeId(recipeId)).thenReturn(recipeMediaList);
      when(mediaRepository.findAllById(anyList())).thenReturn(mediaList);
      when(mediaMapper.toDto(media1)).thenReturn(mediaDto1);
      when(mediaMapper.toDto(media2)).thenReturn(mediaDto2);

      // Act
      Page<MediaDto> result = mediaService.getMediaByRecipeId(recipeId, pageable);

      // Assert
      assertNotNull(result);
      assertEquals(2, result.getTotalElements());
      assertEquals(2, result.getContent().size());
      assertEquals(mediaDto1, result.getContent().get(0));
      assertEquals(mediaDto2, result.getContent().get(1));

      verify(recipeRepository).findById(recipeId);
      verify(recipeMediaRepository).findByRecipeId(recipeId);
      verify(mediaRepository).findAllById(Arrays.asList(mediaId1, mediaId2));
    }
  }

  @Test
  void getMediaByRecipeId_RecipeNotFound() {
    // Arrange
    try (MockedStatic<SecurityUtils> securityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
      when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

      // Act & Assert
      ResourceNotFoundException exception =
          assertThrows(
              ResourceNotFoundException.class,
              () -> mediaService.getMediaByRecipeId(recipeId, pageable));

      assertEquals("Recipe with identifier '123' was not found", exception.getMessage());
      verify(recipeRepository).findById(recipeId);
    }
  }

  @Test
  void getMediaByRecipeId_AccessDenied_DifferentUser() {
    // Arrange
    recipe.setUserId(differentUserId);

    try (MockedStatic<SecurityUtils> securityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
      when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));

      // Act & Assert
      AccessDeniedException exception =
          assertThrows(
              AccessDeniedException.class,
              () -> mediaService.getMediaByRecipeId(recipeId, pageable));

      assertEquals("You don't have permission to access this recipe's media", exception.getMessage());
      verify(recipeRepository).findById(recipeId);
    }
  }

  @Test
  void getMediaByRecipeId_NoMedia() {
    // Arrange
    try (MockedStatic<SecurityUtils> securityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
      when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
      when(recipeMediaRepository.findByRecipeId(recipeId)).thenReturn(Collections.emptyList());
      when(mediaRepository.findAllById(anyList())).thenReturn(Collections.emptyList());

      // Act
      Page<MediaDto> result = mediaService.getMediaByRecipeId(recipeId, pageable);

      // Assert
      assertNotNull(result);
      assertEquals(0, result.getTotalElements());
      assertEquals(0, result.getContent().size());
    }
  }

  @Test
  void getMediaByRecipeAndIngredientId_Success() {
    // Arrange
    IngredientMedia ingredientMedia1 = IngredientMedia.builder()
        .id(IngredientMediaId.builder().ingredientId(ingredientId).mediaId(mediaId1).build())
        .build();
    IngredientMedia ingredientMedia2 = IngredientMedia.builder()
        .id(IngredientMediaId.builder().ingredientId(ingredientId).mediaId(mediaId2).build())
        .build();
    List<IngredientMedia> ingredientMediaList = Arrays.asList(ingredientMedia1, ingredientMedia2);
    List<Media> mediaList = Arrays.asList(media1, media2);

    try (MockedStatic<SecurityUtils> securityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
      when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
      when(ingredientMediaRepository.findByIdIngredientId(ingredientId)).thenReturn(ingredientMediaList);
      when(mediaRepository.findAllById(anyList())).thenReturn(mediaList);
      when(mediaMapper.toDto(media1)).thenReturn(mediaDto1);
      when(mediaMapper.toDto(media2)).thenReturn(mediaDto2);

      // Act
      Page<MediaDto> result = mediaService.getMediaByRecipeAndIngredientId(recipeId, ingredientId, pageable);

      // Assert
      assertNotNull(result);
      assertEquals(2, result.getTotalElements());
      assertEquals(2, result.getContent().size());
      assertEquals(mediaDto1, result.getContent().get(0));
      assertEquals(mediaDto2, result.getContent().get(1));

      verify(recipeRepository).findById(recipeId);
      verify(ingredientMediaRepository).findByIdIngredientId(ingredientId);
      verify(mediaRepository).findAllById(Arrays.asList(mediaId1, mediaId2));
    }
  }

  @Test
  void getMediaByRecipeAndIngredientId_RecipeNotFound() {
    // Arrange
    try (MockedStatic<SecurityUtils> securityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
      when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

      // Act & Assert
      ResourceNotFoundException exception =
          assertThrows(
              ResourceNotFoundException.class,
              () -> mediaService.getMediaByRecipeAndIngredientId(recipeId, ingredientId, pageable));

      assertEquals("Recipe with identifier '123' was not found", exception.getMessage());
      verify(recipeRepository).findById(recipeId);
    }
  }

  @Test
  void getMediaByRecipeAndIngredientId_AccessDenied() {
    // Arrange
    recipe.setUserId(differentUserId);

    try (MockedStatic<SecurityUtils> securityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
      when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));

      // Act & Assert
      AccessDeniedException exception =
          assertThrows(
              AccessDeniedException.class,
              () -> mediaService.getMediaByRecipeAndIngredientId(recipeId, ingredientId, pageable));

      assertEquals("You don't have permission to access this recipe's media", exception.getMessage());
    }
  }

  @Test
  void getMediaByRecipeAndStepId_Success() {
    // Arrange
    StepMedia stepMedia1 = StepMedia.builder()
        .id(StepMediaId.builder().stepId(stepId).mediaId(mediaId1).build())
        .build();
    StepMedia stepMedia2 = StepMedia.builder()
        .id(StepMediaId.builder().stepId(stepId).mediaId(mediaId2).build())
        .build();
    List<StepMedia> stepMediaList = Arrays.asList(stepMedia1, stepMedia2);
    List<Media> mediaList = Arrays.asList(media1, media2);

    try (MockedStatic<SecurityUtils> securityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
      when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
      when(stepMediaRepository.findByIdStepId(stepId)).thenReturn(stepMediaList);
      when(mediaRepository.findAllById(anyList())).thenReturn(mediaList);
      when(mediaMapper.toDto(media1)).thenReturn(mediaDto1);
      when(mediaMapper.toDto(media2)).thenReturn(mediaDto2);

      // Act
      Page<MediaDto> result = mediaService.getMediaByRecipeAndStepId(recipeId, stepId, pageable);

      // Assert
      assertNotNull(result);
      assertEquals(2, result.getTotalElements());
      assertEquals(2, result.getContent().size());
      assertEquals(mediaDto1, result.getContent().get(0));
      assertEquals(mediaDto2, result.getContent().get(1));

      verify(recipeRepository).findById(recipeId);
      verify(stepMediaRepository).findByIdStepId(stepId);
      verify(mediaRepository).findAllById(Arrays.asList(mediaId1, mediaId2));
    }
  }

  @Test
  void getMediaByRecipeAndStepId_RecipeNotFound() {
    // Arrange
    try (MockedStatic<SecurityUtils> securityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
      when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

      // Act & Assert
      ResourceNotFoundException exception =
          assertThrows(
              ResourceNotFoundException.class,
              () -> mediaService.getMediaByRecipeAndStepId(recipeId, stepId, pageable));

      assertEquals("Recipe with identifier '123' was not found", exception.getMessage());
      verify(recipeRepository).findById(recipeId);
    }
  }

  @Test
  void getMediaByRecipeAndStepId_AccessDenied() {
    // Arrange
    recipe.setUserId(differentUserId);

    try (MockedStatic<SecurityUtils> securityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
      when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));

      // Act & Assert
      AccessDeniedException exception =
          assertThrows(
              AccessDeniedException.class,
              () -> mediaService.getMediaByRecipeAndStepId(recipeId, stepId, pageable));

      assertEquals("You don't have permission to access this recipe's media", exception.getMessage());
    }
  }

  @Test
  void getMediaByRecipeId_Pagination() {
    // Arrange
    RecipeMedia recipeMedia1 = RecipeMedia.builder()
        .recipeId(recipeId)
        .mediaId(mediaId1)
        .build();
    RecipeMedia recipeMedia2 = RecipeMedia.builder()
        .recipeId(recipeId)
        .mediaId(mediaId2)
        .build();
    List<RecipeMedia> recipeMediaList = Arrays.asList(recipeMedia1, recipeMedia2);
    List<Media> mediaList = Arrays.asList(media1, media2);
    Pageable pageRequest = PageRequest.of(0, 1); // Page size of 1

    try (MockedStatic<SecurityUtils> securityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
      when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
      when(recipeMediaRepository.findByRecipeId(recipeId)).thenReturn(recipeMediaList);
      when(mediaRepository.findAllById(anyList())).thenReturn(mediaList);
      when(mediaMapper.toDto(media1)).thenReturn(mediaDto1);
      when(mediaMapper.toDto(media2)).thenReturn(mediaDto2);

      // Act
      Page<MediaDto> result = mediaService.getMediaByRecipeId(recipeId, pageRequest);

      // Assert
      assertNotNull(result);
      assertEquals(2, result.getTotalElements());
      assertEquals(1, result.getContent().size());
      assertEquals(mediaDto1, result.getContent().get(0));
      assertEquals(0, result.getNumber());
      assertEquals(2, result.getTotalPages());
    }
  }

  @Test
  void getMediaByRecipeId_SecurityContextException() {
    // Arrange
    try (MockedStatic<SecurityUtils> securityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtils
          .when(SecurityUtils::getCurrentUserId)
          .thenThrow(new IllegalStateException("No authenticated user found"));

      // Act & Assert
      IllegalStateException exception =
          assertThrows(
              IllegalStateException.class,
              () -> mediaService.getMediaByRecipeId(recipeId, pageable));

      assertEquals("No authenticated user found", exception.getMessage());
    }
  }

  @Test
  void createRecipeMedia_Success() {
    // Arrange
    MultipartFile file = new MockMultipartFile("test.jpg", "test.jpg", "image/jpeg", "test data".getBytes());
    CreateMediaRequest request = CreateMediaRequest.builder()
        .originalFilename("test.jpg")
        .mediaType(MediaType.IMAGE_JPEG)
        .fileSize(9L)
        .contentHash("abc123")
        .build();

    UploadMediaResponseDto uploadResponse = UploadMediaResponseDto.builder()
        .mediaId(100L)
        .uploadUrl("/uploads/test.jpg")
        .contentHash("def456")
        .processingStatus(ProcessingStatus.COMPLETE)
        .build();

    Media savedMedia = Media.builder()
        .mediaId(100L)
        .userId(currentUserId)
        .mediaType(MediaType.IMAGE_JPEG)
        .mediaPath("/uploads/test.jpg")
        .fileSize(9L)
        .contentHash("abc123")
        .originalFilename("test.jpg")
        .processingStatus(ProcessingStatus.INITIATED)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    try (MockedStatic<SecurityUtils> securityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
      when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
      when(mediaManagerService.uploadMedia(file)).thenReturn(CompletableFuture.completedFuture(uploadResponse));
      when(mediaRepository.save(any(Media.class))).thenReturn(savedMedia);
      when(recipeMediaRepository.save(any(RecipeMedia.class))).thenReturn(new RecipeMedia());

      // Act
      CreateMediaResponse response = mediaService.createRecipeMedia(recipeId, request, file);

      // Assert
      assertNotNull(response);
      assertEquals(100L, response.getMediaId());
      assertEquals("/uploads/test.jpg", response.getUploadUrl());
      assertEquals("def456", response.getContentHash());

      verify(mediaManagerService).uploadMedia(file);
      verify(mediaRepository).save(any(Media.class));
      verify(recipeMediaRepository).save(any(RecipeMedia.class));
    }
  }

  @Test
  void createRecipeMedia_RecipeNotFound() {
    // Arrange
    MultipartFile file = new MockMultipartFile("test.jpg", "test.jpg", "image/jpeg", "test data".getBytes());
    CreateMediaRequest request = CreateMediaRequest.builder()
        .originalFilename("test.jpg")
        .mediaType(MediaType.IMAGE_JPEG)
        .fileSize(9L)
        .build();

    try (MockedStatic<SecurityUtils> securityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
      when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

      // Act & Assert
      ResourceNotFoundException exception =
          assertThrows(
              ResourceNotFoundException.class,
              () -> mediaService.createRecipeMedia(recipeId, request, file));

      assertEquals("Recipe with identifier '123' was not found", exception.getMessage());
    }
  }

  @Test
  void createRecipeMedia_AccessDenied() {
    // Arrange
    Recipe differentUserRecipe = Recipe.builder()
        .recipeId(recipeId)
        .userId(differentUserId)
        .title("Test Recipe")
        .build();

    MultipartFile file = new MockMultipartFile("test.jpg", "test.jpg", "image/jpeg", "test data".getBytes());
    CreateMediaRequest request = CreateMediaRequest.builder()
        .originalFilename("test.jpg")
        .mediaType(MediaType.IMAGE_JPEG)
        .fileSize(9L)
        .build();

    try (MockedStatic<SecurityUtils> securityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
      when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(differentUserRecipe));

      // Act & Assert
      AccessDeniedException exception =
          assertThrows(
              AccessDeniedException.class,
              () -> mediaService.createRecipeMedia(recipeId, request, file));

      assertEquals("You don't have permission to POST this recipe", exception.getMessage());
    }
  }

  @Test
  void createRecipeMedia_SecurityContextException() {
    // Arrange
    MultipartFile file = new MockMultipartFile("test.jpg", "test.jpg", "image/jpeg", "test data".getBytes());
    CreateMediaRequest request = CreateMediaRequest.builder()
        .originalFilename("test.jpg")
        .mediaType(MediaType.IMAGE_JPEG)
        .fileSize(9L)
        .build();

    try (MockedStatic<SecurityUtils> securityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtils
          .when(SecurityUtils::getCurrentUserId)
          .thenThrow(new IllegalStateException("No authenticated user found"));

      // Act & Assert
      IllegalStateException exception =
          assertThrows(
              IllegalStateException.class,
              () -> mediaService.createRecipeMedia(recipeId, request, file));

      assertEquals("No authenticated user found", exception.getMessage());
    }
  }

  @Test
  void createIngredientMedia_Success() {
    // Arrange
    MultipartFile file = new MockMultipartFile("test.jpg", "test.jpg", "image/jpeg", "test data".getBytes());
    CreateMediaRequest request = CreateMediaRequest.builder()
        .originalFilename("test.jpg")
        .mediaType(MediaType.IMAGE_JPEG)
        .fileSize(9L)
        .contentHash("abc123")
        .build();

    UploadMediaResponseDto uploadResponse = UploadMediaResponseDto.builder()
        .mediaId(100L)
        .uploadUrl("/uploads/test.jpg")
        .contentHash("def456")
        .processingStatus(ProcessingStatus.COMPLETE)
        .build();

    Media savedMedia = Media.builder()
        .mediaId(100L)
        .userId(currentUserId)
        .mediaType(MediaType.IMAGE_JPEG)
        .mediaPath("/uploads/test.jpg")
        .fileSize(9L)
        .contentHash("abc123")
        .originalFilename("test.jpg")
        .processingStatus(ProcessingStatus.INITIATED)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    try (MockedStatic<SecurityUtils> securityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
      when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
      when(mediaManagerService.uploadMedia(file)).thenReturn(CompletableFuture.completedFuture(uploadResponse));
      when(mediaRepository.save(any(Media.class))).thenReturn(savedMedia);
      when(ingredientMediaRepository.save(any(IngredientMedia.class))).thenReturn(new IngredientMedia());

      // Act
      CreateMediaResponse response = mediaService.createIngredientMedia(recipeId, ingredientId, request, file);

      // Assert
      assertNotNull(response);
      assertEquals(100L, response.getMediaId());
      assertEquals("/uploads/test.jpg", response.getUploadUrl());
      assertEquals("def456", response.getContentHash());

      verify(mediaManagerService).uploadMedia(file);
      verify(mediaRepository).save(any(Media.class));
      verify(ingredientMediaRepository).save(any(IngredientMedia.class));
    }
  }

  @Test
  void createIngredientMedia_RecipeNotFound() {
    // Arrange
    MultipartFile file = new MockMultipartFile("test.jpg", "test.jpg", "image/jpeg", "test data".getBytes());
    CreateMediaRequest request = CreateMediaRequest.builder()
        .originalFilename("test.jpg")
        .mediaType(MediaType.IMAGE_JPEG)
        .fileSize(9L)
        .build();

    try (MockedStatic<SecurityUtils> securityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
      when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

      // Act & Assert
      ResourceNotFoundException exception =
          assertThrows(
              ResourceNotFoundException.class,
              () -> mediaService.createIngredientMedia(recipeId, ingredientId, request, file));

      assertEquals("Recipe with identifier '123' was not found", exception.getMessage());
    }
  }

  @Test
  void createIngredientMedia_AccessDenied() {
    // Arrange
    Recipe differentUserRecipe = Recipe.builder()
        .recipeId(recipeId)
        .userId(differentUserId)
        .title("Test Recipe")
        .build();

    MultipartFile file = new MockMultipartFile("test.jpg", "test.jpg", "image/jpeg", "test data".getBytes());
    CreateMediaRequest request = CreateMediaRequest.builder()
        .originalFilename("test.jpg")
        .mediaType(MediaType.IMAGE_JPEG)
        .fileSize(9L)
        .build();

    try (MockedStatic<SecurityUtils> securityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
      when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(differentUserRecipe));

      // Act & Assert
      AccessDeniedException exception =
          assertThrows(
              AccessDeniedException.class,
              () -> mediaService.createIngredientMedia(recipeId, ingredientId, request, file));

      assertEquals("You don't have permission to POST this recipe", exception.getMessage());
    }
  }

  @Test
  void createStepMedia_Success() {
    // Arrange
    MultipartFile file = new MockMultipartFile("test.jpg", "test.jpg", "image/jpeg", "test data".getBytes());
    CreateMediaRequest request = CreateMediaRequest.builder()
        .originalFilename("test.jpg")
        .mediaType(MediaType.IMAGE_JPEG)
        .fileSize(9L)
        .contentHash("abc123")
        .build();

    UploadMediaResponseDto uploadResponse = UploadMediaResponseDto.builder()
        .mediaId(100L)
        .uploadUrl("/uploads/test.jpg")
        .contentHash("def456")
        .processingStatus(ProcessingStatus.COMPLETE)
        .build();

    Media savedMedia = Media.builder()
        .mediaId(100L)
        .userId(currentUserId)
        .mediaType(MediaType.IMAGE_JPEG)
        .mediaPath("/uploads/test.jpg")
        .fileSize(9L)
        .contentHash("abc123")
        .originalFilename("test.jpg")
        .processingStatus(ProcessingStatus.INITIATED)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    try (MockedStatic<SecurityUtils> securityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
      when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
      when(mediaManagerService.uploadMedia(file)).thenReturn(CompletableFuture.completedFuture(uploadResponse));
      when(mediaRepository.save(any(Media.class))).thenReturn(savedMedia);
      when(stepMediaRepository.save(any(StepMedia.class))).thenReturn(new StepMedia());

      // Act
      CreateMediaResponse response = mediaService.createStepMedia(recipeId, stepId, request, file);

      // Assert
      assertNotNull(response);
      assertEquals(100L, response.getMediaId());
      assertEquals("/uploads/test.jpg", response.getUploadUrl());
      assertEquals("def456", response.getContentHash());

      verify(mediaManagerService).uploadMedia(file);
      verify(mediaRepository).save(any(Media.class));
      verify(stepMediaRepository).save(any(StepMedia.class));
    }
  }

  @Test
  void createStepMedia_RecipeNotFound() {
    // Arrange
    MultipartFile file = new MockMultipartFile("test.jpg", "test.jpg", "image/jpeg", "test data".getBytes());
    CreateMediaRequest request = CreateMediaRequest.builder()
        .originalFilename("test.jpg")
        .mediaType(MediaType.IMAGE_JPEG)
        .fileSize(9L)
        .build();

    try (MockedStatic<SecurityUtils> securityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
      when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

      // Act & Assert
      ResourceNotFoundException exception =
          assertThrows(
              ResourceNotFoundException.class,
              () -> mediaService.createStepMedia(recipeId, stepId, request, file));

      assertEquals("Recipe with identifier '123' was not found", exception.getMessage());
    }
  }

  @Test
  void createStepMedia_AccessDenied() {
    // Arrange
    Recipe differentUserRecipe = Recipe.builder()
        .recipeId(recipeId)
        .userId(differentUserId)
        .title("Test Recipe")
        .build();

    MultipartFile file = new MockMultipartFile("test.jpg", "test.jpg", "image/jpeg", "test data".getBytes());
    CreateMediaRequest request = CreateMediaRequest.builder()
        .originalFilename("test.jpg")
        .mediaType(MediaType.IMAGE_JPEG)
        .fileSize(9L)
        .build();

    try (MockedStatic<SecurityUtils> securityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
      when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(differentUserRecipe));

      // Act & Assert
      AccessDeniedException exception =
          assertThrows(
              AccessDeniedException.class,
              () -> mediaService.createStepMedia(recipeId, stepId, request, file));

      assertEquals("You don't have permission to POST this recipe", exception.getMessage());
    }
  }

  @Test
  void deleteRecipeMedia_Success() {
    // Arrange
    RecipeMedia recipeMedia = RecipeMedia.builder()
        .recipeId(recipeId)
        .mediaId(mediaId1)
        .build();
    List<RecipeMedia> recipeMediaList = Arrays.asList(recipeMedia);

    try (MockedStatic<SecurityUtils> securityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
      when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
      when(mediaRepository.findById(mediaId1)).thenReturn(Optional.of(media1));
      when(recipeMediaRepository.findByRecipeId(recipeId)).thenReturn(recipeMediaList);
      when(mediaManagerService.deleteMedia(mediaId1)).thenReturn(CompletableFuture.completedFuture(null));

      // Act
      DeleteMediaResponse response = mediaService.deleteRecipeMedia(recipeId, mediaId1);

      // Assert
      assertNotNull(response);
      assertEquals(true, response.isSuccess());
      assertEquals("Media successfully deleted from recipe", response.getMessage());
      assertEquals(mediaId1, response.getMediaId());

      verify(mediaManagerService).deleteMedia(mediaId1);
      verify(recipeMediaRepository).deleteByMediaId(mediaId1);
      verify(mediaRepository).deleteById(mediaId1);
    }
  }

  @Test
  void deleteRecipeMedia_RecipeNotFound() {
    // Arrange
    try (MockedStatic<SecurityUtils> securityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
      when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

      // Act & Assert
      ResourceNotFoundException exception =
          assertThrows(
              ResourceNotFoundException.class,
              () -> mediaService.deleteRecipeMedia(recipeId, mediaId1));

      assertEquals("Recipe with identifier '123' was not found", exception.getMessage());
    }
  }

  @Test
  void deleteRecipeMedia_MediaNotFound() {
    // Arrange
    try (MockedStatic<SecurityUtils> securityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
      when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
      when(mediaRepository.findById(mediaId1)).thenReturn(Optional.empty());

      // Act & Assert
      ResourceNotFoundException exception =
          assertThrows(
              ResourceNotFoundException.class,
              () -> mediaService.deleteRecipeMedia(recipeId, mediaId1));

      assertEquals("Media with identifier '1' was not found", exception.getMessage());
    }
  }

  @Test
  void deleteRecipeMedia_AccessDenied_RecipeNotOwned() {
    // Arrange
    Recipe differentUserRecipe = Recipe.builder()
        .recipeId(recipeId)
        .userId(differentUserId)
        .title("Test Recipe")
        .build();

    try (MockedStatic<SecurityUtils> securityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
      when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(differentUserRecipe));

      // Act & Assert
      AccessDeniedException exception =
          assertThrows(
              AccessDeniedException.class,
              () -> mediaService.deleteRecipeMedia(recipeId, mediaId1));

      assertEquals("You don't have permission to DELETE this recipe", exception.getMessage());
    }
  }

  @Test
  void deleteRecipeMedia_AccessDenied_MediaNotOwned() {
    // Arrange
    Media differentUserMedia = Media.builder()
        .mediaId(mediaId1)
        .userId(differentUserId)
        .mediaType(MediaType.IMAGE_JPEG)
        .mediaPath("/path/to/media1.jpg")
        .fileSize(1024L)
        .contentHash("abc123")
        .originalFilename("test1.jpg")
        .processingStatus(ProcessingStatus.COMPLETE)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    try (MockedStatic<SecurityUtils> securityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
      when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
      when(mediaRepository.findById(mediaId1)).thenReturn(Optional.of(differentUserMedia));

      // Act & Assert
      AccessDeniedException exception =
          assertThrows(
              AccessDeniedException.class,
              () -> mediaService.deleteRecipeMedia(recipeId, mediaId1));

      assertEquals("You don't have permission to DELETE this media", exception.getMessage());
    }
  }

  @Test
  void deleteRecipeMedia_MediaNotAssociatedWithRecipe() {
    // Arrange - empty recipe media list means no association
    List<RecipeMedia> emptyRecipeMediaList = Collections.emptyList();

    try (MockedStatic<SecurityUtils> securityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
      when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
      when(mediaRepository.findById(mediaId1)).thenReturn(Optional.of(media1));
      when(recipeMediaRepository.findByRecipeId(recipeId)).thenReturn(emptyRecipeMediaList);

      // Act & Assert
      ResourceNotFoundException exception =
          assertThrows(
              ResourceNotFoundException.class,
              () -> mediaService.deleteRecipeMedia(recipeId, mediaId1));

      assertEquals("Media with ID 1 is not associated with recipe 123", exception.getMessage());
    }
  }

  @Test
  void deleteIngredientMedia_Success() {
    // Arrange
    IngredientMediaId ingredientMediaId = IngredientMediaId.builder()
        .ingredientId(ingredientId)
        .mediaId(mediaId1)
        .build();

    try (MockedStatic<SecurityUtils> securityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
      when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
      when(mediaRepository.findById(mediaId1)).thenReturn(Optional.of(media1));
      when(ingredientMediaRepository.existsById(ingredientMediaId)).thenReturn(true);
      when(mediaManagerService.deleteMedia(mediaId1)).thenReturn(CompletableFuture.completedFuture(null));

      // Act
      DeleteMediaResponse response = mediaService.deleteIngredientMedia(recipeId, ingredientId, mediaId1);

      // Assert
      assertNotNull(response);
      assertEquals(true, response.isSuccess());
      assertEquals("Media successfully deleted from ingredient", response.getMessage());
      assertEquals(mediaId1, response.getMediaId());

      verify(mediaManagerService).deleteMedia(mediaId1);
      verify(ingredientMediaRepository).deleteByIdMediaId(mediaId1);
      verify(mediaRepository).deleteById(mediaId1);
    }
  }

  @Test
  void deleteStepMedia_Success() {
    // Arrange
    StepMediaId stepMediaId = StepMediaId.builder()
        .stepId(stepId)
        .mediaId(mediaId1)
        .build();

    try (MockedStatic<SecurityUtils> securityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
      when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
      when(mediaRepository.findById(mediaId1)).thenReturn(Optional.of(media1));
      when(stepMediaRepository.existsById(stepMediaId)).thenReturn(true);
      when(mediaManagerService.deleteMedia(mediaId1)).thenReturn(CompletableFuture.completedFuture(null));

      // Act
      DeleteMediaResponse response = mediaService.deleteStepMedia(recipeId, stepId, mediaId1);

      // Assert
      assertNotNull(response);
      assertEquals(true, response.isSuccess());
      assertEquals("Media successfully deleted from step", response.getMessage());
      assertEquals(mediaId1, response.getMediaId());

      verify(mediaManagerService).deleteMedia(mediaId1);
      verify(stepMediaRepository).deleteByIdMediaId(mediaId1);
      verify(mediaRepository).deleteById(mediaId1);
    }
  }
}
