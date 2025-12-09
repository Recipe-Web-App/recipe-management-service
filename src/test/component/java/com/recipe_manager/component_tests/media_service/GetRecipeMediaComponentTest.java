package com.recipe_manager.component_tests.media_service;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
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
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;

import com.recipe_manager.component_tests.AbstractComponentTest;
import com.recipe_manager.controller.MediaController;
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
import com.recipe_manager.service.external.mediamanager.MediaManagerService;
import com.recipe_manager.repository.media.IngredientMediaRepository;
import com.recipe_manager.repository.media.MediaRepository;
import com.recipe_manager.repository.media.RecipeMediaRepository;
import com.recipe_manager.repository.media.StepMediaRepository;
import com.recipe_manager.service.MediaService;
import com.recipe_manager.util.SecurityUtils;

/**
 * Component tests for Media endpoints. These tests use real service and mapper implementations but
 * mock repository layer.
 */
@SpringBootTest(classes = {com.recipe_manager.model.mapper.MediaMapperImpl.class})
@TestPropertySource(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:testdb",
      "spring.jpa.hibernate.ddl-auto=none",
      "spring.flyway.enabled=false"
    })
@Tag("component")
class GetRecipeMediaComponentTest extends AbstractComponentTest {

  private MediaService mediaService;
  private MediaController mediaController;

  // Add to base class mocks
  private MediaRepository mediaRepository;
  private RecipeMediaRepository recipeMediaRepository;
  private IngredientMediaRepository ingredientMediaRepository;
  private StepMediaRepository stepMediaRepository;
  private MediaManagerService mediaManagerService;

  @Autowired(required = false)
  private MediaMapper mediaMapper;

  private UUID currentUserId;
  private UUID differentUserId;
  private Long recipeId;
  private Long ingredientId;
  private Long stepId;
  private Recipe recipe;
  private Media media1;
  private Media media2;
  private RecipeMedia recipeMedia1;
  private RecipeMedia recipeMedia2;
  private IngredientMedia ingredientMedia1;
  private StepMedia stepMedia1;

  @Override
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
    mockMvc = org.springframework.test.web.servlet.setup.MockMvcBuilders
        .standaloneSetup(mediaController)
        .setControllerAdvice(new com.recipe_manager.exception.GlobalExceptionHandler())
        .setCustomArgumentResolvers(new org.springframework.data.web.PageableHandlerMethodArgumentResolver())
        .build();
  }

  private void setupTestData() {
    currentUserId = UUID.randomUUID();
    differentUserId = UUID.randomUUID();
    recipeId = 123L;
    ingredientId = 456L;
    stepId = 789L;

    // Create test recipe
    recipe =
        Recipe.builder()
            .recipeId(recipeId)
            .userId(currentUserId)
            .title("Test Recipe")
            .description("Test recipe description")
            .servings(new BigDecimal("4"))
            .preparationTime(30)
            .cookingTime(45)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    // Create test media entities
    media1 =
        Media.builder()
            .mediaId(1L)
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
            .mediaId(2L)
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

    // Create relationship entities
    recipeMedia1 = RecipeMedia.builder().recipeId(recipeId).mediaId(1L).media(media1).build();
    recipeMedia2 = RecipeMedia.builder().recipeId(recipeId).mediaId(2L).media(media2).build();
    ingredientMedia1 = IngredientMedia.builder()
        .id(IngredientMediaId.builder().ingredientId(ingredientId).mediaId(1L).build())
        .media(media1)
        .build();
    stepMedia1 = StepMedia.builder()
        .id(StepMediaId.builder().stepId(stepId).mediaId(1L).build())
        .media(media1)
        .build();
  }

  @Test
  void getRecipeMedia_Success() throws Exception {
    // Arrange
    List<RecipeMedia> recipeMediaList = Arrays.asList(recipeMedia1, recipeMedia2);
    List<Media> mediaList = Arrays.asList(media1, media2);

    try (MockedStatic<SecurityUtils> securityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
      Mockito.when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
      Mockito.when(recipeMediaRepository.findByRecipeId(recipeId)).thenReturn(recipeMediaList);
      Mockito.when(mediaRepository.findAllById(Arrays.asList(1L, 2L))).thenReturn(mediaList);

      // Act & Assert
      mockMvc
          .perform(get("/recipes/{recipeId}/media", recipeId))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.content").isArray())
          .andExpect(jsonPath("$.content[0].mediaId").value(1))
          .andExpect(jsonPath("$.content[0].originalFilename").value("test1.jpg"))
          .andExpect(jsonPath("$.content[0].mediaType").value("image/jpeg"))
          .andExpect(jsonPath("$.content[0].fileSize").value(1024))
          .andExpect(jsonPath("$.content[0].processingStatus").value("COMPLETE"))
          .andExpect(jsonPath("$.content[1].mediaId").value(2))
          .andExpect(jsonPath("$.content[1].originalFilename").value("test2.mp4"))
          .andExpect(jsonPath("$.content[1].mediaType").value("video/mp4"))
          .andExpect(jsonPath("$.content[1].fileSize").value(2048))
          .andExpect(jsonPath("$.totalElements").value(2))
          .andExpect(jsonPath("$.size").value(20))
          .andExpect(jsonPath("$.number").value(0));
    }
  }

  @Test
  void getRecipeMedia_EmptyResult() throws Exception {
    // Arrange
    try (MockedStatic<SecurityUtils> securityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
      Mockito.when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
      Mockito.when(recipeMediaRepository.findByRecipeId(recipeId))
          .thenReturn(Collections.emptyList());
      Mockito.when(mediaRepository.findAllById(Collections.emptyList()))
          .thenReturn(Collections.emptyList());

      // Act & Assert
      mockMvc
          .perform(get("/recipes/{recipeId}/media", recipeId))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.content").isArray())
          .andExpect(jsonPath("$.content").isEmpty())
          .andExpect(jsonPath("$.totalElements").value(0));
    }
  }

  @Test
  void getRecipeMedia_RecipeNotFound() throws Exception {
    // Arrange
    try (MockedStatic<SecurityUtils> securityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
      Mockito.when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

      // Act & Assert
      mockMvc
          .perform(get("/recipes/{recipeId}/media", recipeId))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.message").value("Recipe with identifier '123' was not found"));
    }
  }

  @Test
  void getRecipeMedia_AccessDenied() throws Exception {
    // Arrange
    recipe.setUserId(differentUserId);

    try (MockedStatic<SecurityUtils> securityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
      Mockito.when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));

      // Act & Assert
      mockMvc
          .perform(get("/recipes/{recipeId}/media", recipeId))
          .andExpect(status().isForbidden())
          .andExpect(
              jsonPath("$.message")
                  .value("You don't have permission to access this resource"));
    }
  }

  @Test
  void getIngredientMedia_Success() throws Exception {
    // Arrange
    List<IngredientMedia> ingredientMediaList = Arrays.asList(ingredientMedia1);
    List<Media> mediaList = Arrays.asList(media1);

    try (MockedStatic<SecurityUtils> securityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
      Mockito.when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
      Mockito.when(ingredientMediaRepository.findByIdIngredientId(ingredientId))
          .thenReturn(ingredientMediaList);
      Mockito.when(mediaRepository.findAllById(Arrays.asList(1L))).thenReturn(mediaList);

      // Act & Assert
      mockMvc
          .perform(
              get(
                  "/recipes/{recipeId}/ingredients/{ingredientId}/media",
                  recipeId,
                  ingredientId))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.content").isArray())
          .andExpect(jsonPath("$.content[0].mediaId").value(1))
          .andExpect(jsonPath("$.content[0].originalFilename").value("test1.jpg"))
          .andExpect(jsonPath("$.content[0].mediaType").value("image/jpeg"))
          .andExpect(jsonPath("$.totalElements").value(1));
    }
  }

  @Test
  void getStepMedia_Success() throws Exception {
    // Arrange
    List<StepMedia> stepMediaList = Arrays.asList(stepMedia1);
    List<Media> mediaList = Arrays.asList(media1);

    try (MockedStatic<SecurityUtils> securityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
      Mockito.when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
      Mockito.when(stepMediaRepository.findByIdStepId(stepId)).thenReturn(stepMediaList);
      Mockito.when(mediaRepository.findAllById(Arrays.asList(1L))).thenReturn(mediaList);

      // Act & Assert
      mockMvc
          .perform(
              get(
                  "/recipes/{recipeId}/steps/{stepId}/media",
                  recipeId,
                  stepId))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.content").isArray())
          .andExpect(jsonPath("$.content[0].mediaId").value(1))
          .andExpect(jsonPath("$.content[0].originalFilename").value("test1.jpg"))
          .andExpect(jsonPath("$.content[0].mediaType").value("image/jpeg"))
          .andExpect(jsonPath("$.totalElements").value(1));
    }
  }

  @Test
  void getRecipeMedia_WithPagination() throws Exception {
    // Arrange
    List<RecipeMedia> recipeMediaList = Arrays.asList(recipeMedia1, recipeMedia2);
    List<Media> mediaList = Arrays.asList(media1, media2);

    try (MockedStatic<SecurityUtils> securityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
      Mockito.when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
      Mockito.when(recipeMediaRepository.findByRecipeId(recipeId)).thenReturn(recipeMediaList);
      Mockito.when(mediaRepository.findAllById(Arrays.asList(1L, 2L))).thenReturn(mediaList);

      // Act & Assert
      mockMvc
          .perform(
              get("/recipes/{recipeId}/media", recipeId)
                  .param("page", "0")
                  .param("size", "1")
                  .param("sort", "mediaId,asc"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.content").isArray())
          .andExpect(jsonPath("$.content").hasJsonPath())
          .andExpect(jsonPath("$.content[0].mediaId").value(1))
          .andExpect(jsonPath("$.totalElements").value(2))
          .andExpect(jsonPath("$.size").value(1))
          .andExpect(jsonPath("$.number").value(0));
    }
  }

  @Test
  void getRecipeMedia_SecurityContextMissing() throws Exception {
    // Arrange
    try (MockedStatic<SecurityUtils> securityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtils
          .when(SecurityUtils::getCurrentUserId)
          .thenThrow(new IllegalStateException("No authenticated user found"));

      // Act & Assert
      mockMvc
          .perform(get("/recipes/{recipeId}/media", recipeId))
          .andExpect(status().isInternalServerError())
          .andExpect(jsonPath("$.message").value("An unexpected error occurred"));
    }
  }

  @Test
  void createRecipeMedia_Success() throws Exception {
    // Arrange
    MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test data".getBytes());

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

    com.recipe_manager.model.dto.external.mediamanager.response.UploadMediaResponseDto uploadResponse =
        com.recipe_manager.model.dto.external.mediamanager.response.UploadMediaResponseDto.builder()
            .mediaId(100L)
            .uploadUrl("/uploads/test.jpg")
            .contentHash("def456")
            .processingStatus(ProcessingStatus.COMPLETE)
            .build();

    try (MockedStatic<SecurityUtils> securityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
      Mockito.when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
      Mockito.when(mediaManagerService.uploadMedia(file)).thenReturn(CompletableFuture.completedFuture(uploadResponse));
      Mockito.when(mediaRepository.save(any(Media.class))).thenReturn(savedMedia);
      Mockito.when(recipeMediaRepository.save(any(RecipeMedia.class))).thenReturn(new RecipeMedia());

      // Act & Assert
      mockMvc
          .perform(multipart("/recipes/{recipeId}/media", recipeId)
              .file(file)
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
    MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test data".getBytes());

    try (MockedStatic<SecurityUtils> securityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
      Mockito.when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

      // Act & Assert
      mockMvc
          .perform(multipart("/recipes/{recipeId}/media", recipeId)
              .file(file)
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
    recipe.setUserId(differentUserId);
    MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test data".getBytes());

    try (MockedStatic<SecurityUtils> securityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
      Mockito.when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));

      // Act & Assert
      mockMvc
          .perform(multipart("/recipes/{recipeId}/media", recipeId)
              .file(file)
              .param("originalFilename", "test.jpg")
              .param("mediaType", "IMAGE_JPEG")
              .param("fileSize", "9"))
          .andExpect(status().isForbidden())
          .andExpect(jsonPath("$.message").value("You don't have permission to access this resource"));
    }
  }
}
