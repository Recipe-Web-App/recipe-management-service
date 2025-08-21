package com.recipe_manager.component_tests.step_service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import com.recipe_manager.component_tests.AbstractComponentTest;
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.entity.recipe.RecipeStep;
import com.recipe_manager.model.entity.recipe.StepComment;
import com.recipe_manager.model.mapper.RecipeStepMapperImpl;
import com.recipe_manager.model.mapper.StepCommentMapperImpl;
import com.recipe_manager.model.mapper.RecipeRevisionMapperImpl;
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
 * Component tests for PUT /recipe-management/recipes/{recipeId}/steps/{stepId}/comment endpoint.
 * Tests the actual StepService editComment logic with mocked repository calls.
 */
@SpringBootTest(classes = {
    RecipeStepMapperImpl.class,
    StepCommentMapperImpl.class,
    RecipeRevisionMapperImpl.class
})
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=none",
    "spring.flyway.enabled=false"
})
@Tag("component")
class EditStepCommentComponentTest extends AbstractComponentTest {

  private Recipe testRecipe;
  private RecipeStep testStep;
  private UUID currentUserId;
  private StepComment existingComment;

  @Override
  @BeforeEach
  protected void setUp() {
    super.setUp();
    useRealStepService(); // Use real service with mocked repositories
    currentUserId = UUID.randomUUID();

    testRecipe = Recipe.builder()
        .recipeId(123L)
        .userId(currentUserId)
        .title("Test Recipe")
        .description("A test recipe")
        .build();

    testStep = RecipeStep.builder()
        .stepId(1L)
        .recipe(testRecipe)
        .stepNumber(1)
        .instruction("Mix ingredients")
        .build();

    existingComment = StepComment.builder()
        .commentId(1L)
        .recipeId(123L)
        .step(testStep)
        .userId(currentUserId)
        .commentText("Original comment")
        .isPublic(true)
        .createdAt(LocalDateTime.now().minusHours(1))
        .updatedAt(LocalDateTime.now().minusHours(1))
        .build();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("PUT /recipes/{recipeId}/steps/{stepId}/comment should edit comment successfully")
  void shouldEditStepCommentSuccessfully() throws Exception {
    // Given
    String updatedCommentText = "Updated comment text";
    StepComment updatedComment = StepComment.builder()
        .commentId(1L)
        .recipeId(123L)
        .step(testStep)
        .userId(currentUserId)
        .commentText(updatedCommentText)
        .isPublic(false) // Changed visibility
        .createdAt(existingComment.getCreatedAt())
        .updatedAt(LocalDateTime.now())
        .build();

    when(recipeRepository.existsById(123L)).thenReturn(true);
    when(recipeStepRepository.findByStepIdAndRecipeRecipeId(1L, 123L)).thenReturn(Optional.of(testStep));
    when(stepCommentRepository.findByCommentIdAndStepStepId(1L, 1L)).thenReturn(Optional.of(existingComment));
    when(stepCommentRepository.save(any(StepComment.class))).thenReturn(updatedComment);

    String requestBody = """
        {
          "commentId": 1,
          "comment": "%s",
          "isPublic": false
        }
        """.formatted(updatedCommentText);

    // When & Then
    try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
      mockedSecurity.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);

      mockMvc.perform(put("/recipe-management/recipes/123/steps/1/comment")
          .contentType(MediaType.APPLICATION_JSON)
          .content(requestBody))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.commentId").value(1))
          .andExpect(jsonPath("$.recipeId").value(123))
          .andExpect(jsonPath("$.stepId").value(1))
          .andExpect(jsonPath("$.userId").value(currentUserId.toString()))
          .andExpect(jsonPath("$.commentText").value(updatedCommentText))
          .andExpect(jsonPath("$.isPublic").value(false))
          .andExpect(jsonPath("$.createdAt").exists())
          .andExpect(jsonPath("$.updatedAt").exists())
          .andExpect(header().exists("X-Request-ID"));
    }
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("PUT /recipes/{recipeId}/steps/{stepId}/comment should only update comment text")
  void shouldUpdateOnlyCommentText() throws Exception {
    // Given - only updating comment text, keeping visibility same
    String updatedCommentText = "Just the text changed";
    StepComment updatedComment = StepComment.builder()
        .commentId(1L)
        .recipeId(123L)
        .step(testStep)
        .userId(currentUserId)
        .commentText(updatedCommentText)
        .isPublic(existingComment.getIsPublic()) // Keep same visibility
        .createdAt(existingComment.getCreatedAt())
        .updatedAt(LocalDateTime.now())
        .build();

    when(recipeRepository.existsById(123L)).thenReturn(true);
    when(recipeStepRepository.findByStepIdAndRecipeRecipeId(1L, 123L)).thenReturn(Optional.of(testStep));
    when(stepCommentRepository.findByCommentIdAndStepStepId(1L, 1L)).thenReturn(Optional.of(existingComment));
    when(stepCommentRepository.save(any(StepComment.class))).thenReturn(updatedComment);

    String requestBody = """
        {
          "commentId": 1,
          "comment": "%s",
          "isPublic": true
        }
        """.formatted(updatedCommentText);

    // When & Then
    try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
      mockedSecurity.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);

      mockMvc.perform(put("/recipe-management/recipes/123/steps/1/comment")
          .contentType(MediaType.APPLICATION_JSON)
          .content(requestBody))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.commentId").value(1))
          .andExpect(jsonPath("$.recipeId").value(123))
          .andExpect(jsonPath("$.stepId").value(1))
          .andExpect(jsonPath("$.userId").value(currentUserId.toString()))
          .andExpect(jsonPath("$.commentText").value(updatedCommentText))
          .andExpect(jsonPath("$.isPublic").value(true))
          .andExpect(jsonPath("$.createdAt").exists())
          .andExpect(jsonPath("$.updatedAt").exists())
          .andExpect(header().exists("X-Request-ID"));
    }
  }

  @Test
  @Tag("error-processing")
  @DisplayName("PUT /recipes/{recipeId}/steps/{stepId}/comment should return 404 for non-existent comment")
  void shouldHandleNotFoundForNonExistentComment() throws Exception {
    // Given
    when(recipeRepository.existsById(123L)).thenReturn(true);
    when(recipeStepRepository.findByStepIdAndRecipeRecipeId(1L, 123L)).thenReturn(Optional.of(testStep));
    when(stepCommentRepository.findByCommentIdAndStepStepId(999L, 1L)).thenReturn(Optional.empty());

    String requestBody = """
        {
          "commentId": 999,
          "comment": "Trying to edit non-existent comment",
          "isPublic": true
        }
        """;

    // When & Then
    try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
      mockedSecurity.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);

      mockMvc.perform(put("/recipe-management/recipes/123/steps/1/comment")
          .contentType(MediaType.APPLICATION_JSON)
          .content(requestBody))
          .andExpect(status().isNotFound())
          .andExpect(header().exists("X-Request-ID"));
    }
  }

  @Test
  @Tag("error-processing")
  @DisplayName("PUT /recipes/{recipeId}/steps/{stepId}/comment should return 403 for unauthorized user")
  void shouldHandleUnauthorizedUserEdit() throws Exception {
    // Given - different user trying to edit
    UUID differentUserId = UUID.randomUUID();

    when(recipeRepository.existsById(123L)).thenReturn(true);
    when(recipeStepRepository.findByStepIdAndRecipeRecipeId(1L, 123L)).thenReturn(Optional.of(testStep));
    when(stepCommentRepository.findByCommentIdAndStepStepId(1L, 1L)).thenReturn(Optional.of(existingComment));

    String requestBody = """
        {
          "commentId": 1,
          "comment": "Unauthorized edit attempt",
          "isPublic": false
        }
        """;

    // When & Then
    try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
      mockedSecurity.when(SecurityUtils::getCurrentUserId).thenReturn(differentUserId);

      mockMvc.perform(put("/recipe-management/recipes/123/steps/1/comment")
          .contentType(MediaType.APPLICATION_JSON)
          .content(requestBody))
          .andExpect(status().isForbidden())
          .andExpect(header().exists("X-Request-ID"));
    }
  }
}
