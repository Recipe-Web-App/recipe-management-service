package com.recipe_manager.component_tests.media_service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;

import com.recipe_manager.component_tests.AbstractComponentTest;
import com.recipe_manager.model.dto.external.mediamanager.response.UploadMediaResponseDto;
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
import com.recipe_manager.controller.MediaController;
import com.recipe_manager.exception.GlobalExceptionHandler;
import com.recipe_manager.model.mapper.MediaMapper;
import com.recipe_manager.model.mapper.MediaMapperImpl;
import com.recipe_manager.repository.media.IngredientMediaRepository;
import com.recipe_manager.repository.media.MediaRepository;
import com.recipe_manager.repository.media.RecipeMediaRepository;
import com.recipe_manager.repository.media.StepMediaRepository;
import com.recipe_manager.service.MediaService;
import com.recipe_manager.service.external.mediamanager.MediaManagerService;
import com.recipe_manager.util.SecurityUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Component tests for Media creation endpoints. These tests use real service and mapper
 * implementations but mock repository and external service layers.
 */
@SpringBootTest(classes = {MediaMapperImpl.class})
@TestPropertySource(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:testdb",
      "spring.jpa.hibernate.ddl-auto=none",
      "spring.flyway.enabled=false"
    })
@Tag("component")
class CreateMediaComponentTest extends AbstractComponentTest {

  private MediaService mediaService;
  private MediaController mediaController;

  // Media-specific repository mocks
  private MediaRepository mediaRepository;
  private RecipeMediaRepository recipeMediaRepository;
  private IngredientMediaRepository ingredientMediaRepository;
  private StepMediaRepository stepMediaRepository;
  private MediaManagerService mediaManagerService;

  @Autowired(required = false)
  private MediaMapper mediaMapper;

  private UUID currentUserId;
  private Long recipeId;
  private Long ingredientId;
  private Long stepId;
  private Recipe recipe;
  private MockMultipartFile mockFile;
  private UploadMediaResponseDto uploadResponse;
  private Media savedMedia;

  @BeforeEach
  protected void setUp() {
    super.setUp();
    useRealMediaService();

    setupTestData();
  }

  private void useRealMediaService() {
    // Mock repositories and external services
    this.mediaRepository = Mockito.mock(MediaRepository.class);
    this.recipeMediaRepository = Mockito.mock(RecipeMediaRepository.class);
    this.ingredientMediaRepository = Mockito.mock(IngredientMediaRepository.class);
    this.stepMediaRepository = Mockito.mock(StepMediaRepository.class);
    this.mediaManagerService = Mockito.mock(MediaManagerService.class);

    if (mediaMapper == null) {
      throw new RuntimeException("MediaMapper not available in this test context");
    }

    // Create real service with mocked repositories and external services
    this.mediaService =
        new MediaService(
            mediaRepository,
            recipeMediaRepository,
            ingredientMediaRepository,
            stepMediaRepository,
            recipeRepository,
            mediaManagerService,
            mediaMapper);

    // Create controller with real service
    this.mediaController = new MediaController(mediaService);

    // Rebuild MockMvc with the MediaController
    mockMvc = MockMvcBuilders
        .standaloneSetup(mediaController)
        .setControllerAdvice(new GlobalExceptionHandler())
        .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
        .build();
  }

  private void setupTestData() {
    currentUserId = UUID.randomUUID();
    recipeId = 123L;
    ingredientId = 456L;
    stepId = 789L;

    // Create test recipe
    recipe =
        Recipe.builder()
            .recipeId(recipeId)
            .userId(currentUserId)
            .title("Test Recipe")
            .build();

    // Create test file
    mockFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test data".getBytes());

    // Create upload response
    uploadResponse =
        UploadMediaResponseDto.builder()
            .mediaId(100L)
            .uploadUrl("/uploads/test.jpg")
            .contentHash("def456")
            .processingStatus(ProcessingStatus.COMPLETE)
            .build();

    // Create saved media
    savedMedia =
        Media.builder()
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
  }

  @Test
  void createRecipeMedia_Success() throws Exception {
    // Arrange
    try (MockedStatic<SecurityUtils> securityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
      when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
      when(mediaManagerService.uploadMedia(any())).thenReturn(CompletableFuture.completedFuture(uploadResponse));
      when(mediaRepository.save(any(Media.class))).thenReturn(savedMedia);
      when(recipeMediaRepository.save(any(RecipeMedia.class))).thenReturn(new RecipeMedia());

      // Act & Assert
      mockMvc
          .perform(
              multipart("/recipes/{recipeId}/media", recipeId)
                  .file(mockFile)
                  .param("originalFilename", "test.jpg")
                  .param("mediaType", "IMAGE_JPEG")
                  .param("fileSize", "9")
                  .param("contentHash", "abc123"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.mediaId").value(100))
          .andExpect(jsonPath("$.uploadUrl").value("/uploads/test.jpg"))
          .andExpect(jsonPath("$.contentHash").value("def456"));
    }
  }

  @Test
  void createRecipeMedia_RecipeNotFound() throws Exception {
    // Arrange
    try (MockedStatic<SecurityUtils> securityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
      when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

      // Act & Assert
      mockMvc
          .perform(
              multipart("/recipes/{recipeId}/media", recipeId)
                  .file(mockFile)
                  .param("originalFilename", "test.jpg")
                  .param("mediaType", "IMAGE_JPEG")
                  .param("fileSize", "9"))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.message").value("Recipe with identifier '123' was not found"));
    }
  }

  @Test
  void createRecipeMedia_AccessDenied() throws Exception {
    // Arrange
    Recipe differentUserRecipe = Recipe.builder()
        .recipeId(recipeId)
        .userId(UUID.randomUUID())
        .title("Test Recipe")
        .build();

    try (MockedStatic<SecurityUtils> securityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
      when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(differentUserRecipe));

      // Act & Assert
      mockMvc
          .perform(
              multipart("/recipes/{recipeId}/media", recipeId)
                  .file(mockFile)
                  .param("originalFilename", "test.jpg")
                  .param("mediaType", "IMAGE_JPEG")
                  .param("fileSize", "9"))
          .andExpect(status().isForbidden())
          .andExpect(jsonPath("$.message").value("You don't have permission to access this resource"));
    }
  }

  @Test
  void createIngredientMedia_Success() throws Exception {
    // Arrange
    try (MockedStatic<SecurityUtils> securityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
      when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
      when(mediaManagerService.uploadMedia(any())).thenReturn(CompletableFuture.completedFuture(uploadResponse));
      when(mediaRepository.save(any(Media.class))).thenReturn(savedMedia);
      when(ingredientMediaRepository.save(any(IngredientMedia.class))).thenReturn(new IngredientMedia());

      // Act & Assert
      mockMvc
          .perform(
              multipart("/recipes/{recipeId}/ingredients/{ingredientId}/media", recipeId, ingredientId)
                  .file(mockFile)
                  .param("originalFilename", "test.jpg")
                  .param("mediaType", "IMAGE_JPEG")
                  .param("fileSize", "9")
                  .param("contentHash", "abc123"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.mediaId").value(100))
          .andExpect(jsonPath("$.uploadUrl").value("/uploads/test.jpg"))
          .andExpect(jsonPath("$.contentHash").value("def456"));
    }
  }

  @Test
  void createIngredientMedia_RecipeNotFound() throws Exception {
    // Arrange
    try (MockedStatic<SecurityUtils> securityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
      when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

      // Act & Assert
      mockMvc
          .perform(
              multipart("/recipes/{recipeId}/ingredients/{ingredientId}/media", recipeId, ingredientId)
                  .file(mockFile)
                  .param("originalFilename", "test.jpg")
                  .param("mediaType", "IMAGE_JPEG")
                  .param("fileSize", "9"))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.message").value("Recipe with identifier '123' was not found"));
    }
  }

  @Test
  void createIngredientMedia_AccessDenied() throws Exception {
    // Arrange
    Recipe differentUserRecipe = Recipe.builder()
        .recipeId(recipeId)
        .userId(UUID.randomUUID())
        .title("Test Recipe")
        .build();

    try (MockedStatic<SecurityUtils> securityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
      when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(differentUserRecipe));

      // Act & Assert
      mockMvc
          .perform(
              multipart("/recipes/{recipeId}/ingredients/{ingredientId}/media", recipeId, ingredientId)
                  .file(mockFile)
                  .param("originalFilename", "test.jpg")
                  .param("mediaType", "IMAGE_JPEG")
                  .param("fileSize", "9"))
          .andExpect(status().isForbidden())
          .andExpect(jsonPath("$.message").value("You don't have permission to access this resource"));
    }
  }

  @Test
  void createStepMedia_Success() throws Exception {
    // Arrange
    try (MockedStatic<SecurityUtils> securityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
      when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
      when(mediaManagerService.uploadMedia(any())).thenReturn(CompletableFuture.completedFuture(uploadResponse));
      when(mediaRepository.save(any(Media.class))).thenReturn(savedMedia);
      when(stepMediaRepository.save(any(StepMedia.class))).thenReturn(new StepMedia());

      // Act & Assert
      mockMvc
          .perform(
              multipart("/recipes/{recipeId}/steps/{stepId}/media", recipeId, stepId)
                  .file(mockFile)
                  .param("originalFilename", "test.jpg")
                  .param("mediaType", "IMAGE_JPEG")
                  .param("fileSize", "9")
                  .param("contentHash", "abc123"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.mediaId").value(100))
          .andExpect(jsonPath("$.uploadUrl").value("/uploads/test.jpg"))
          .andExpect(jsonPath("$.contentHash").value("def456"));
    }
  }

  @Test
  void createStepMedia_RecipeNotFound() throws Exception {
    // Arrange
    try (MockedStatic<SecurityUtils> securityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
      when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

      // Act & Assert
      mockMvc
          .perform(
              multipart("/recipes/{recipeId}/steps/{stepId}/media", recipeId, stepId)
                  .file(mockFile)
                  .param("originalFilename", "test.jpg")
                  .param("mediaType", "IMAGE_JPEG")
                  .param("fileSize", "9"))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.message").value("Recipe with identifier '123' was not found"));
    }
  }

  @Test
  void createStepMedia_AccessDenied() throws Exception {
    // Arrange
    Recipe differentUserRecipe = Recipe.builder()
        .recipeId(recipeId)
        .userId(UUID.randomUUID())
        .title("Test Recipe")
        .build();

    try (MockedStatic<SecurityUtils> securityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
      when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(differentUserRecipe));

      // Act & Assert
      mockMvc
          .perform(
              multipart("/recipes/{recipeId}/steps/{stepId}/media", recipeId, stepId)
                  .file(mockFile)
                  .param("originalFilename", "test.jpg")
                  .param("mediaType", "IMAGE_JPEG")
                  .param("fileSize", "9"))
          .andExpect(status().isForbidden())
          .andExpect(jsonPath("$.message").value("You don't have permission to access this resource"));
    }
  }

  @Test
  void deleteRecipeMedia_Success() throws Exception {
    // Arrange
    Long mediaId = 100L;
    List<RecipeMedia> recipeMediaList = Arrays.asList(
        RecipeMedia.builder().recipeId(recipeId).mediaId(mediaId).build());

    try (MockedStatic<SecurityUtils> securityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
      when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
      when(mediaRepository.findById(mediaId)).thenReturn(Optional.of(savedMedia));
      when(recipeMediaRepository.findByRecipeId(recipeId)).thenReturn(recipeMediaList);
      when(mediaManagerService.deleteMedia(mediaId)).thenReturn(CompletableFuture.completedFuture(null));

      // Act & Assert
      mockMvc
          .perform(delete("/recipes/{recipeId}/media/{mediaId}", recipeId, mediaId))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success").value(true))
          .andExpect(jsonPath("$.message").value("Media successfully deleted from recipe"))
          .andExpect(jsonPath("$.mediaId").value(100));
    }
  }

  @Test
  void deleteIngredientMedia_Success() throws Exception {
    // Arrange
    Long mediaId = 100L;
    IngredientMediaId ingredientMediaId = IngredientMediaId.builder()
        .ingredientId(ingredientId)
        .mediaId(mediaId)
        .build();

    try (MockedStatic<SecurityUtils> securityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
      when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
      when(mediaRepository.findById(mediaId)).thenReturn(Optional.of(savedMedia));
      when(ingredientMediaRepository.existsById(ingredientMediaId)).thenReturn(true);
      when(mediaManagerService.deleteMedia(mediaId)).thenReturn(CompletableFuture.completedFuture(null));

      // Act & Assert
      mockMvc
          .perform(delete("/recipes/{recipeId}/ingredients/{ingredientId}/media/{mediaId}",
                   recipeId, ingredientId, mediaId))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success").value(true))
          .andExpect(jsonPath("$.message").value("Media successfully deleted from ingredient"))
          .andExpect(jsonPath("$.mediaId").value(100));
    }
  }

  @Test
  void deleteStepMedia_Success() throws Exception {
    // Arrange
    Long mediaId = 100L;
    StepMediaId stepMediaId = StepMediaId.builder()
        .stepId(stepId)
        .mediaId(mediaId)
        .build();

    try (MockedStatic<SecurityUtils> securityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
      when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
      when(mediaRepository.findById(mediaId)).thenReturn(Optional.of(savedMedia));
      when(stepMediaRepository.existsById(stepMediaId)).thenReturn(true);
      when(mediaManagerService.deleteMedia(mediaId)).thenReturn(CompletableFuture.completedFuture(null));

      // Act & Assert
      mockMvc
          .perform(delete("/recipes/{recipeId}/steps/{stepId}/media/{mediaId}",
                   recipeId, stepId, mediaId))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success").value(true))
          .andExpect(jsonPath("$.message").value("Media successfully deleted from step"))
          .andExpect(jsonPath("$.mediaId").value(100));
    }
  }

  @Test
  void deleteRecipeMedia_RecipeNotFound() throws Exception {
    // Arrange
    Long mediaId = 100L;

    try (MockedStatic<SecurityUtils> securityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
      when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

      // Act & Assert
      mockMvc
          .perform(delete("/recipes/{recipeId}/media/{mediaId}", recipeId, mediaId))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.message").value("Recipe with identifier '123' was not found"));
    }
  }
}
