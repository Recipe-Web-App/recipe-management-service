package com.recipe_manager.component_tests.recipe_service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipe_manager.component_tests.AbstractComponentTest;
import com.recipe_manager.model.dto.request.AddRecipeCommentRequest;
import com.recipe_manager.model.dto.request.EditRecipeCommentRequest;
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.entity.recipe.RecipeComment;
import com.recipe_manager.model.mapper.RecipeCommentMapperImpl;
import com.recipe_manager.model.mapper.RecipeFavoriteMapperImpl;
import com.recipe_manager.model.mapper.RecipeIngredientMapperImpl;
import com.recipe_manager.model.mapper.RecipeMapperImpl;
import com.recipe_manager.model.mapper.RecipeRevisionMapperImpl;
import com.recipe_manager.model.mapper.RecipeStepMapperImpl;
import com.recipe_manager.model.mapper.RecipeTagMapperImpl;
import com.recipe_manager.util.SecurityUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;

/**
 * Component tests for recipe comments endpoints.
 *
 * <p>Tests the actual RecipeService logic with mocked repository calls.
 */
@SpringBootTest(
    classes = {
      RecipeMapperImpl.class,
      RecipeIngredientMapperImpl.class,
      RecipeStepMapperImpl.class,
      RecipeFavoriteMapperImpl.class,
      RecipeRevisionMapperImpl.class,
      RecipeTagMapperImpl.class,
      RecipeCommentMapperImpl.class
    })
@TestPropertySource(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:testdb",
      "spring.jpa.hibernate.ddl-auto=none",
      "spring.flyway.enabled=false"
    })
@Tag("component")
class RecipeCommentsComponentTest extends AbstractComponentTest {

  private UUID currentUserId;
  private UUID otherUserId;
  private Recipe testRecipe;
  private RecipeComment testComment;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  @BeforeEach
  protected void setUp() {
    super.setUp();
    useRealRecipeService();

    currentUserId = UUID.randomUUID();
    otherUserId = UUID.randomUUID();

    testRecipe = Recipe.builder().recipeId(1L).userId(currentUserId).title("Test Recipe").build();

    testComment =
        RecipeComment.builder()
            .commentId(1L)
            .recipe(testRecipe)
            .userId(currentUserId)
            .commentText("Great recipe!")
            .isPublic(true)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
  }

  // ==================== GET /recipes/{recipeId}/comments ====================

  @Test
  @DisplayName("Should successfully retrieve comments for existing recipe")
  void shouldGetCommentsForExistingRecipe() throws Exception {
    // Given
    when(recipeRepository.existsById(1L)).thenReturn(true);
    when(recipeCommentRepository.findByRecipeIdOrderByCreatedAtAsc(1L))
        .thenReturn(Arrays.asList(testComment));

    // When & Then
    mockMvc
        .perform(get("/recipes/1/comments").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(header().exists("X-Request-ID"))
        .andExpect(jsonPath("$.recipeId").value(1))
        .andExpect(jsonPath("$.comments").isArray())
        .andExpect(jsonPath("$.comments[0].commentId").value(1))
        .andExpect(jsonPath("$.comments[0].commentText").value("Great recipe!"))
        .andExpect(jsonPath("$.comments[0].isPublic").value(true));
  }

  @Test
  @DisplayName("Should return empty list when recipe has no comments")
  void shouldReturnEmptyListWhenNoComments() throws Exception {
    // Given
    when(recipeRepository.existsById(1L)).thenReturn(true);
    when(recipeCommentRepository.findByRecipeIdOrderByCreatedAtAsc(1L))
        .thenReturn(Collections.emptyList());

    // When & Then
    mockMvc
        .perform(get("/recipes/1/comments").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(header().exists("X-Request-ID"))
        .andExpect(jsonPath("$.recipeId").value(1))
        .andExpect(jsonPath("$.comments").isArray())
        .andExpect(jsonPath("$.comments").isEmpty());
  }

  @Test
  @DisplayName("Should return 404 when recipe not found for GET comments")
  void shouldReturn404WhenRecipeNotFoundForGet() throws Exception {
    // Given
    when(recipeRepository.findById(999L)).thenReturn(Optional.empty());

    // When & Then
    mockMvc
        .perform(get("/recipes/999/comments").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(header().exists("X-Request-ID"))
        .andExpect(jsonPath("$.error").value("Resource not found"));
  }

  // ==================== POST /recipes/{recipeId}/comments ====================

  @Test
  @DisplayName("Should successfully add comment to recipe")
  void shouldAddCommentToRecipe() throws Exception {
    // Given
    AddRecipeCommentRequest request =
        AddRecipeCommentRequest.builder().commentText("This is a test comment").build();

    RecipeComment savedComment =
        RecipeComment.builder()
            .commentId(2L)
            .recipe(testRecipe)
            .userId(currentUserId)
            .commentText("This is a test comment")
            .isPublic(true)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    when(recipeRepository.findById(1L)).thenReturn(Optional.of(testRecipe));
    when(recipeCommentRepository.save(any(RecipeComment.class))).thenReturn(savedComment);

    try (MockedStatic<SecurityUtils> mockedSecurityUtils =
        Mockito.mockStatic(SecurityUtils.class)) {
      mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);

      // When & Then
      mockMvc
          .perform(
              post("/recipes/1/comments")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isCreated())
          .andExpect(header().exists("X-Request-ID"))
          .andExpect(jsonPath("$.commentId").value(2))
          .andExpect(jsonPath("$.recipeId").value(1))
          .andExpect(jsonPath("$.commentText").value("This is a test comment"))
          .andExpect(jsonPath("$.isPublic").value(true));
    }
  }

  @Test
  @DisplayName("Should add comment with isPublic=false")
  void shouldAddPrivateComment() throws Exception {
    // Given
    AddRecipeCommentRequest request =
        AddRecipeCommentRequest.builder()
            .commentText("Private comment")
            .isPublic(false)
            .build();

    RecipeComment savedComment =
        RecipeComment.builder()
            .commentId(3L)
            .recipe(testRecipe)
            .userId(currentUserId)
            .commentText("Private comment")
            .isPublic(false)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    when(recipeRepository.findById(1L)).thenReturn(Optional.of(testRecipe));
    when(recipeCommentRepository.save(any(RecipeComment.class))).thenReturn(savedComment);

    try (MockedStatic<SecurityUtils> mockedSecurityUtils =
        Mockito.mockStatic(SecurityUtils.class)) {
      mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);

      // When & Then
      mockMvc
          .perform(
              post("/recipes/1/comments")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isCreated())
          .andExpect(header().exists("X-Request-ID"))
          .andExpect(jsonPath("$.commentId").value(3))
          .andExpect(jsonPath("$.isPublic").value(false));
    }
  }

  @Test
  @DisplayName("Should return 404 when adding comment to non-existent recipe")
  void shouldReturn404WhenAddingCommentToNonExistentRecipe() throws Exception {
    // Given
    AddRecipeCommentRequest request =
        AddRecipeCommentRequest.builder().commentText("Test comment").build();

    when(recipeRepository.findById(999L)).thenReturn(Optional.empty());

    try (MockedStatic<SecurityUtils> mockedSecurityUtils =
        Mockito.mockStatic(SecurityUtils.class)) {
      mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);

      // When & Then
      mockMvc
          .perform(
              post("/recipes/999/comments")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isNotFound())
          .andExpect(header().exists("X-Request-ID"))
          .andExpect(jsonPath("$.error").value("Resource not found"));
    }
  }

  @Test
  @DisplayName("Should return 400 when comment text is blank")
  void shouldReturn400WhenCommentTextIsBlank() throws Exception {
    // Given
    AddRecipeCommentRequest request = AddRecipeCommentRequest.builder().commentText("").build();

    // When & Then
    mockMvc
        .perform(
            post("/recipes/1/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(header().exists("X-Request-ID"));
  }

  // ==================== PUT /recipes/{recipeId}/comments/{commentId} ====================

  @Test
  @DisplayName("Should successfully edit own comment")
  void shouldEditOwnComment() throws Exception {
    // Given
    EditRecipeCommentRequest request =
        EditRecipeCommentRequest.builder().commentText("Updated comment text").build();

    RecipeComment updatedComment =
        RecipeComment.builder()
            .commentId(1L)
            .recipe(testRecipe)
            .userId(currentUserId)
            .commentText("Updated comment text")
            .isPublic(true)
            .createdAt(testComment.getCreatedAt())
            .updatedAt(LocalDateTime.now())
            .build();

    when(recipeRepository.existsById(1L)).thenReturn(true);
    when(recipeCommentRepository.findByCommentIdAndRecipeId(1L, 1L))
        .thenReturn(Optional.of(testComment));
    when(recipeCommentRepository.save(any(RecipeComment.class))).thenReturn(updatedComment);

    try (MockedStatic<SecurityUtils> mockedSecurityUtils =
        Mockito.mockStatic(SecurityUtils.class)) {
      mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);

      // When & Then
      mockMvc
          .perform(
              put("/recipes/1/comments/1")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isOk())
          .andExpect(header().exists("X-Request-ID"))
          .andExpect(jsonPath("$.commentId").value(1))
          .andExpect(jsonPath("$.commentText").value("Updated comment text"));
    }
  }

  @Test
  @DisplayName("Should return 404 when editing comment on non-existent recipe")
  void shouldReturn404WhenEditingCommentOnNonExistentRecipe() throws Exception {
    // Given
    EditRecipeCommentRequest request =
        EditRecipeCommentRequest.builder().commentText("Updated").build();

    when(recipeRepository.existsById(999L)).thenReturn(false);

    try (MockedStatic<SecurityUtils> mockedSecurityUtils =
        Mockito.mockStatic(SecurityUtils.class)) {
      mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);

      // When & Then
      mockMvc
          .perform(
              put("/recipes/999/comments/1")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isNotFound())
          .andExpect(header().exists("X-Request-ID"));
    }
  }

  @Test
  @DisplayName("Should return 404 when editing non-existent comment")
  void shouldReturn404WhenEditingNonExistentComment() throws Exception {
    // Given
    EditRecipeCommentRequest request =
        EditRecipeCommentRequest.builder().commentText("Updated").build();

    when(recipeRepository.existsById(1L)).thenReturn(true);
    when(recipeCommentRepository.findByCommentIdAndRecipeId(999L, 1L))
        .thenReturn(Optional.empty());

    try (MockedStatic<SecurityUtils> mockedSecurityUtils =
        Mockito.mockStatic(SecurityUtils.class)) {
      mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);

      // When & Then
      mockMvc
          .perform(
              put("/recipes/1/comments/999")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isNotFound())
          .andExpect(header().exists("X-Request-ID"));
    }
  }

  @Test
  @DisplayName("Should return 403 when editing someone else's comment")
  void shouldReturn403WhenEditingSomeoneElsesComment() throws Exception {
    // Given
    EditRecipeCommentRequest request =
        EditRecipeCommentRequest.builder().commentText("Trying to edit").build();

    RecipeComment otherUsersComment =
        RecipeComment.builder()
            .commentId(1L)
            .recipe(testRecipe)
            .userId(otherUserId)
            .commentText("Someone else's comment")
            .isPublic(true)
            .createdAt(LocalDateTime.now())
            .build();

    when(recipeRepository.existsById(1L)).thenReturn(true);
    when(recipeCommentRepository.findByCommentIdAndRecipeId(1L, 1L))
        .thenReturn(Optional.of(otherUsersComment));

    try (MockedStatic<SecurityUtils> mockedSecurityUtils =
        Mockito.mockStatic(SecurityUtils.class)) {
      mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);

      // When & Then
      mockMvc
          .perform(
              put("/recipes/1/comments/1")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isForbidden())
          .andExpect(header().exists("X-Request-ID"))
          .andExpect(jsonPath("$.error").value("Access denied"));
    }
  }

  @Test
  @DisplayName("Should return 400 when editing comment with blank text")
  void shouldReturn400WhenEditingCommentWithBlankText() throws Exception {
    // Given
    EditRecipeCommentRequest request = EditRecipeCommentRequest.builder().commentText("").build();

    // When & Then
    mockMvc
        .perform(
            put("/recipes/1/comments/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(header().exists("X-Request-ID"));
  }

  // ==================== DELETE /recipes/{recipeId}/comments/{commentId} ====================

  @Test
  @DisplayName("Should successfully delete own comment")
  void shouldDeleteOwnComment() throws Exception {
    // Given
    when(recipeRepository.existsById(1L)).thenReturn(true);
    when(recipeCommentRepository.findByCommentIdAndRecipeId(1L, 1L))
        .thenReturn(Optional.of(testComment));
    doNothing().when(recipeCommentRepository).delete(any(RecipeComment.class));

    try (MockedStatic<SecurityUtils> mockedSecurityUtils =
        Mockito.mockStatic(SecurityUtils.class)) {
      mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);

      // When & Then
      mockMvc
          .perform(delete("/recipes/1/comments/1").contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isNoContent())
          .andExpect(header().exists("X-Request-ID"));
    }
  }

  @Test
  @DisplayName("Should return 404 when deleting comment from non-existent recipe")
  void shouldReturn404WhenDeletingCommentFromNonExistentRecipe() throws Exception {
    // Given
    when(recipeRepository.findById(999L)).thenReturn(Optional.empty());

    try (MockedStatic<SecurityUtils> mockedSecurityUtils =
        Mockito.mockStatic(SecurityUtils.class)) {
      mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);

      // When & Then
      mockMvc
          .perform(delete("/recipes/999/comments/1").contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound())
          .andExpect(header().exists("X-Request-ID"));
    }
  }

  @Test
  @DisplayName("Should return 404 when deleting non-existent comment")
  void shouldReturn404WhenDeletingNonExistentComment() throws Exception {
    // Given
    when(recipeRepository.existsById(1L)).thenReturn(true);
    when(recipeCommentRepository.findByCommentIdAndRecipeId(999L, 1L))
        .thenReturn(Optional.empty());

    try (MockedStatic<SecurityUtils> mockedSecurityUtils =
        Mockito.mockStatic(SecurityUtils.class)) {
      mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);

      // When & Then
      mockMvc
          .perform(delete("/recipes/1/comments/999").contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound())
          .andExpect(header().exists("X-Request-ID"));
    }
  }

  @Test
  @DisplayName("Should return 403 when deleting someone else's comment")
  void shouldReturn403WhenDeletingSomeoneElsesComment() throws Exception {
    // Given
    RecipeComment otherUsersComment =
        RecipeComment.builder()
            .commentId(1L)
            .recipe(testRecipe)
            .userId(otherUserId)
            .commentText("Someone else's comment")
            .isPublic(true)
            .createdAt(LocalDateTime.now())
            .build();

    when(recipeRepository.existsById(1L)).thenReturn(true);
    when(recipeCommentRepository.findByCommentIdAndRecipeId(1L, 1L))
        .thenReturn(Optional.of(otherUsersComment));

    try (MockedStatic<SecurityUtils> mockedSecurityUtils =
        Mockito.mockStatic(SecurityUtils.class)) {
      mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);

      // When & Then
      mockMvc
          .perform(delete("/recipes/1/comments/1").contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isForbidden())
          .andExpect(header().exists("X-Request-ID"))
          .andExpect(jsonPath("$.error").value("Access denied"));
    }
  }
}
