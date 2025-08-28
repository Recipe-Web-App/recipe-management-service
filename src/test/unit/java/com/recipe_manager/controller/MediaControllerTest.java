package com.recipe_manager.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.recipe_manager.exception.GlobalExceptionHandler;
import com.recipe_manager.exception.ResourceNotFoundException;
import com.recipe_manager.model.dto.media.MediaDto;
import com.recipe_manager.model.dto.request.CreateMediaRequest;
import com.recipe_manager.model.dto.response.CreateMediaResponse;
import com.recipe_manager.model.enums.MediaType;
import com.recipe_manager.model.enums.ProcessingStatus;
import com.recipe_manager.service.MediaService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

/** Unit tests for MediaController. */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class MediaControllerTest {

  @Mock
  private MediaService mediaService;
  @InjectMocks
  private MediaController mediaController;

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;
  private Long recipeId;
  private Long ingredientId;
  private Long stepId;
  private MediaDto mediaDto1;
  private MediaDto mediaDto2;
  private Page<MediaDto> mediaPage;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(mediaController)
        .setControllerAdvice(new GlobalExceptionHandler())
        .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
        .build();

    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());

    recipeId = 123L;
    ingredientId = 456L;
    stepId = 789L;

    // Create test media DTOs
    mediaDto1 = MediaDto.builder()
        .mediaId(1L)
        .userId(UUID.randomUUID())
        .mediaType(com.recipe_manager.model.enums.MediaType.IMAGE_JPEG)
        .originalFilename("test1.jpg")
        .fileSize(1024L)
        .contentHash("abc123")
        .processingStatus(ProcessingStatus.COMPLETE)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    mediaDto2 = MediaDto.builder()
        .mediaId(2L)
        .userId(UUID.randomUUID())
        .mediaType(com.recipe_manager.model.enums.MediaType.VIDEO_MP4)
        .originalFilename("test2.mp4")
        .fileSize(2048L)
        .contentHash("def456")
        .processingStatus(ProcessingStatus.COMPLETE)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    List<MediaDto> mediaList = Arrays.asList(mediaDto1, mediaDto2);
    mediaPage = new PageImpl<>(mediaList, PageRequest.of(0, 20), 2);
  }

  @Test
  void getRecipeMedia_Success() throws Exception {
    // Arrange
    when(mediaService.getMediaByRecipeId(eq(recipeId), any(Pageable.class)))
        .thenReturn(mediaPage);

    // Act & Assert
    mockMvc
        .perform(get("/recipe-management/recipes/{recipeId}/media", recipeId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content[0].mediaId").value(1))
        .andExpect(jsonPath("$.content[0].originalFilename").value("test1.jpg"))
        .andExpect(jsonPath("$.content[0].mediaType").value("IMAGE_JPEG"))
        .andExpect(jsonPath("$.content[1].mediaId").value(2))
        .andExpect(jsonPath("$.content[1].originalFilename").value("test2.mp4"))
        .andExpect(jsonPath("$.content[1].mediaType").value("VIDEO_MP4"))
        .andExpect(jsonPath("$.totalElements").value(2))
        .andExpect(jsonPath("$.size").value(20))
        .andExpect(jsonPath("$.number").value(0));

    verify(mediaService).getMediaByRecipeId(eq(recipeId), any(Pageable.class));
  }

  @Test
  void getRecipeMedia_EmptyList() throws Exception {
    // Arrange
    Page<MediaDto> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 20), 0);
    when(mediaService.getMediaByRecipeId(eq(recipeId), any(Pageable.class)))
        .thenReturn(emptyPage);

    // Act & Assert
    mockMvc
        .perform(get("/recipe-management/recipes/{recipeId}/media", recipeId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content").isEmpty())
        .andExpect(jsonPath("$.totalElements").value(0));

    verify(mediaService).getMediaByRecipeId(eq(recipeId), any(Pageable.class));
  }

  @Test
  void getRecipeMedia_RecipeNotFound() throws Exception {
    // Arrange
    when(mediaService.getMediaByRecipeId(eq(recipeId), any(Pageable.class)))
        .thenThrow(ResourceNotFoundException.forEntity("Recipe", recipeId));

    // Act & Assert
    mockMvc
        .perform(get("/recipe-management/recipes/{recipeId}/media", recipeId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Recipe with identifier '123' was not found"));

    verify(mediaService).getMediaByRecipeId(eq(recipeId), any(Pageable.class));
  }

  @Test
  void getRecipeMedia_AccessDenied() throws Exception {
    // Arrange
    when(mediaService.getMediaByRecipeId(eq(recipeId), any(Pageable.class)))
        .thenThrow(new AccessDeniedException("You don't have permission to access this recipe's media"));

    // Act & Assert
    mockMvc
        .perform(get("/recipe-management/recipes/{recipeId}/media", recipeId))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.message").value("You don't have permission to access this resource"));

    verify(mediaService).getMediaByRecipeId(eq(recipeId), any(Pageable.class));
  }

  @Test
  void getRecipeMedia_WithPagination() throws Exception {
    // Arrange
    when(mediaService.getMediaByRecipeId(eq(recipeId), any(Pageable.class)))
        .thenReturn(mediaPage);

    // Act & Assert
    mockMvc
        .perform(get("/recipe-management/recipes/{recipeId}/media", recipeId)
            .param("page", "0")
            .param("size", "10")
            .param("sort", "mediaId,desc"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content").isNotEmpty());

    verify(mediaService).getMediaByRecipeId(eq(recipeId), any(Pageable.class));
  }

  @Test
  void getIngredientMedia_Success() throws Exception {
    // Arrange
    when(mediaService.getMediaByRecipeAndIngredientId(
        eq(recipeId), eq(ingredientId), any(Pageable.class)))
        .thenReturn(mediaPage);

    // Act & Assert
    mockMvc
        .perform(
            get(
                "/recipe-management/recipes/{recipeId}/ingredients/{ingredientId}/media",
                recipeId,
                ingredientId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content[0].mediaId").value(1))
        .andExpect(jsonPath("$.content[0].originalFilename").value("test1.jpg"))
        .andExpect(jsonPath("$.content[1].mediaId").value(2))
        .andExpect(jsonPath("$.content[1].originalFilename").value("test2.mp4"))
        .andExpect(jsonPath("$.totalElements").value(2));

    verify(mediaService)
        .getMediaByRecipeAndIngredientId(eq(recipeId), eq(ingredientId), any(Pageable.class));
  }

  @Test
  void getIngredientMedia_EmptyList() throws Exception {
    // Arrange
    Page<MediaDto> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 20), 0);
    when(mediaService.getMediaByRecipeAndIngredientId(
        eq(recipeId), eq(ingredientId), any(Pageable.class)))
        .thenReturn(emptyPage);

    // Act & Assert
    mockMvc
        .perform(
            get(
                "/recipe-management/recipes/{recipeId}/ingredients/{ingredientId}/media",
                recipeId,
                ingredientId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content").isEmpty())
        .andExpect(jsonPath("$.totalElements").value(0));

    verify(mediaService)
        .getMediaByRecipeAndIngredientId(eq(recipeId), eq(ingredientId), any(Pageable.class));
  }

  @Test
  void getIngredientMedia_RecipeNotFound() throws Exception {
    // Arrange
    when(mediaService.getMediaByRecipeAndIngredientId(
        eq(recipeId), eq(ingredientId), any(Pageable.class)))
        .thenThrow(ResourceNotFoundException.forEntity("Recipe", recipeId));

    // Act & Assert
    mockMvc
        .perform(
            get(
                "/recipe-management/recipes/{recipeId}/ingredients/{ingredientId}/media",
                recipeId,
                ingredientId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Recipe with identifier '123' was not found"));
  }

  @Test
  void getIngredientMedia_AccessDenied() throws Exception {
    // Arrange
    when(mediaService.getMediaByRecipeAndIngredientId(
        eq(recipeId), eq(ingredientId), any(Pageable.class)))
        .thenThrow(new AccessDeniedException("You don't have permission to access this recipe's media"));

    // Act & Assert
    mockMvc
        .perform(
            get(
                "/recipe-management/recipes/{recipeId}/ingredients/{ingredientId}/media",
                recipeId,
                ingredientId))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.message").value("You don't have permission to access this resource"));
  }

  @Test
  void getStepMedia_Success() throws Exception {
    // Arrange
    when(mediaService.getMediaByRecipeAndStepId(eq(recipeId), eq(stepId), any(Pageable.class)))
        .thenReturn(mediaPage);

    // Act & Assert
    mockMvc
        .perform(
            get(
                "/recipe-management/recipes/{recipeId}/steps/{stepId}/media",
                recipeId,
                stepId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content[0].mediaId").value(1))
        .andExpect(jsonPath("$.content[0].originalFilename").value("test1.jpg"))
        .andExpect(jsonPath("$.content[1].mediaId").value(2))
        .andExpect(jsonPath("$.content[1].originalFilename").value("test2.mp4"))
        .andExpect(jsonPath("$.totalElements").value(2));

    verify(mediaService).getMediaByRecipeAndStepId(eq(recipeId), eq(stepId), any(Pageable.class));
  }

  @Test
  void getStepMedia_EmptyList() throws Exception {
    // Arrange
    Page<MediaDto> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 20), 0);
    when(mediaService.getMediaByRecipeAndStepId(eq(recipeId), eq(stepId), any(Pageable.class)))
        .thenReturn(emptyPage);

    // Act & Assert
    mockMvc
        .perform(
            get(
                "/recipe-management/recipes/{recipeId}/steps/{stepId}/media",
                recipeId,
                stepId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content").isEmpty())
        .andExpect(jsonPath("$.totalElements").value(0));

    verify(mediaService).getMediaByRecipeAndStepId(eq(recipeId), eq(stepId), any(Pageable.class));
  }

  @Test
  void getStepMedia_RecipeNotFound() throws Exception {
    // Arrange
    when(mediaService.getMediaByRecipeAndStepId(eq(recipeId), eq(stepId), any(Pageable.class)))
        .thenThrow(ResourceNotFoundException.forEntity("Recipe", recipeId));

    // Act & Assert
    mockMvc
        .perform(
            get(
                "/recipe-management/recipes/{recipeId}/steps/{stepId}/media",
                recipeId,
                stepId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Recipe with identifier '123' was not found"));
  }

  @Test
  void getStepMedia_AccessDenied() throws Exception {
    // Arrange
    when(mediaService.getMediaByRecipeAndStepId(eq(recipeId), eq(stepId), any(Pageable.class)))
        .thenThrow(new AccessDeniedException("You don't have permission to access this recipe's media"));

    // Act & Assert
    mockMvc
        .perform(
            get(
                "/recipe-management/recipes/{recipeId}/steps/{stepId}/media",
                recipeId,
                stepId))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.message").value("You don't have permission to access this resource"));
  }

  @Test
  void getStepMedia_WithPagination() throws Exception {
    // Arrange
    when(mediaService.getMediaByRecipeAndStepId(eq(recipeId), eq(stepId), any(Pageable.class)))
        .thenReturn(mediaPage);

    // Act & Assert
    mockMvc
        .perform(
            get("/recipe-management/recipes/{recipeId}/steps/{stepId}/media", recipeId, stepId)
                .param("page", "0")
                .param("size", "5")
                .param("sort", "mediaId,asc"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content").isNotEmpty());

    verify(mediaService).getMediaByRecipeAndStepId(eq(recipeId), eq(stepId), any(Pageable.class));
  }

  @Test
  void getRecipeMedia_InvalidRecipeId() throws Exception {
    // Act & Assert
    mockMvc
        .perform(get("/recipe-management/recipes/invalid/media"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void getIngredientMedia_InvalidIds() throws Exception {
    // Act & Assert
    mockMvc
        .perform(get("/recipe-management/recipes/invalid/ingredients/invalid/media"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void getStepMedia_InvalidIds() throws Exception {
    // Act & Assert
    mockMvc
        .perform(get("/recipe-management/recipes/invalid/steps/invalid/media"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createRecipeMedia_Success() throws Exception {
    // Arrange
    MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test data".getBytes());
    CreateMediaResponse response = CreateMediaResponse.builder()
        .mediaId(100L)
        .uploadUrl("/uploads/test.jpg")
        .contentHash("def456")
        .build();

    when(mediaService.createRecipeMedia(eq(recipeId), any(CreateMediaRequest.class), any(MultipartFile.class)))
        .thenReturn(response);

    // Act & Assert
    mockMvc
        .perform(multipart("/recipe-management/recipes/{recipeId}/media", recipeId)
            .file(file)
            .param("originalFilename", "test.jpg")
            .param("mediaType", "IMAGE_JPEG")
            .param("fileSize", "9")
            .param("contentHash", "abc123"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.mediaId").value(100))
        .andExpect(jsonPath("$.uploadUrl").value("/uploads/test.jpg"))
        .andExpect(jsonPath("$.contentHash").value("def456"));

    verify(mediaService).createRecipeMedia(eq(recipeId), any(CreateMediaRequest.class), any(MultipartFile.class));
  }

  @Test
  void createRecipeMedia_RecipeNotFound() throws Exception {
    // Arrange
    MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test data".getBytes());
    when(mediaService.createRecipeMedia(eq(recipeId), any(CreateMediaRequest.class), any(MultipartFile.class)))
        .thenThrow(ResourceNotFoundException.forEntity("Recipe", recipeId));

    // Act & Assert
    mockMvc
        .perform(multipart("/recipe-management/recipes/{recipeId}/media", recipeId)
            .file(file)
            .param("originalFilename", "test.jpg")
            .param("mediaType", "IMAGE_JPEG")
            .param("fileSize", "9"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Recipe with identifier '123' was not found"));

    verify(mediaService).createRecipeMedia(eq(recipeId), any(CreateMediaRequest.class), any(MultipartFile.class));
  }

  @Test
  void createRecipeMedia_AccessDenied() throws Exception {
    // Arrange
    MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test data".getBytes());
    when(mediaService.createRecipeMedia(eq(recipeId), any(CreateMediaRequest.class), any(MultipartFile.class)))
        .thenThrow(new AccessDeniedException("You don't have permission to add media to this recipe"));

    // Act & Assert
    mockMvc
        .perform(multipart("/recipe-management/recipes/{recipeId}/media", recipeId)
            .file(file)
            .param("originalFilename", "test.jpg")
            .param("mediaType", "IMAGE_JPEG")
            .param("fileSize", "9"))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.message").value("You don't have permission to access this resource"));

    verify(mediaService).createRecipeMedia(eq(recipeId), any(CreateMediaRequest.class), any(MultipartFile.class));
  }

  @Test
  void createRecipeMedia_InvalidRecipeId() throws Exception {
    // Arrange
    MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test data".getBytes());

    // Act & Assert
    mockMvc
        .perform(multipart("/recipe-management/recipes/invalid/media")
            .file(file)
            .param("originalFilename", "test.jpg")
            .param("mediaType", "IMAGE_JPEG")
            .param("fileSize", "9"))
        .andExpect(status().isBadRequest());
  }


  @Test
  void createRecipeMedia_MissingParameters() throws Exception {
    // Arrange
    MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test data".getBytes());

    // Act & Assert - Missing originalFilename
    mockMvc
        .perform(multipart("/recipe-management/recipes/{recipeId}/media", recipeId)
            .file(file)
            .param("mediaType", "IMAGE_JPEG")
            .param("fileSize", "9"))
        .andExpect(status().isBadRequest());
  }
}
