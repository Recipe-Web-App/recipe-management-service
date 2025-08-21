package com.recipe_manager.component_tests.step_service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import com.recipe_manager.util.SecurityUtils;
import org.springframework.test.context.TestPropertySource;

/**
 * Component tests for POST
 * /recipe-management/recipes/{recipeId}/steps/{stepId}/comment endpoint.
 * Tests the actual StepService addComment logic with mocked repository calls.
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
class AddStepCommentComponentTest extends AbstractComponentTest {

  private Recipe testRecipe;
  private RecipeStep testStep;
  private UUID currentUserId;

  @Override
  @BeforeEach
  protected void setUp() {
    super.setUp();
    useRealStepService(); // Use real service with mocked repositories

    currentUserId = UUID.randomUUID();

    // Create test data
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
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("POST /recipes/{recipeId}/steps/{stepId}/comment should add comment successfully")
  void shouldAddStepCommentSuccessfully() throws Exception {
    // Given
    String commentText = "This step was confusing";
    StepComment savedComment = StepComment.builder()
        .commentId(1L)
        .recipeId(123L)
        .step(testStep)
        .userId(currentUserId)
        .commentText(commentText)
        .isPublic(true)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    when(recipeRepository.existsById(123L)).thenReturn(true);
    when(recipeStepRepository.findByStepIdAndRecipeRecipeId(1L, 123L)).thenReturn(Optional.of(testStep));
    when(stepCommentRepository.save(any(StepComment.class))).thenReturn(savedComment);

    String requestBody = """
        {
          "comment": "%s",
          "isPublic": true
        }
        """.formatted(commentText);

    // When & Then
    try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
      mockedSecurity.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);

      mockMvc.perform(post("/recipe-management/recipes/123/steps/1/comment")
          .contentType(MediaType.APPLICATION_JSON)
          .content(requestBody))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.commentId").value(1))
          .andExpect(jsonPath("$.recipeId").value(123))
          .andExpect(jsonPath("$.stepId").value(1))
          .andExpect(jsonPath("$.userId").value(currentUserId.toString()))
          .andExpect(jsonPath("$.commentText").value(commentText))
          .andExpect(jsonPath("$.isPublic").value(true))
          .andExpect(jsonPath("$.createdAt").exists())
          .andExpect(jsonPath("$.updatedAt").exists())
          .andExpect(header().exists("X-Request-ID"));
    }
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("POST /recipes/{recipeId}/steps/{stepId}/comment should add private comment successfully")
  void shouldAddPrivateStepCommentSuccessfully() throws Exception {
    // Given
    String commentText = "Private note about this step";
    StepComment savedComment = StepComment.builder()
        .commentId(2L)
        .recipeId(123L)
        .step(testStep)
        .userId(currentUserId)
        .commentText(commentText)
        .isPublic(false)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    when(recipeRepository.existsById(123L)).thenReturn(true);
    when(recipeStepRepository.findByStepIdAndRecipeRecipeId(1L, 123L)).thenReturn(Optional.of(testStep));
    when(stepCommentRepository.save(any(StepComment.class))).thenReturn(savedComment);

    String requestBody = """
        {
          "comment": "%s",
          "isPublic": false
        }
        """.formatted(commentText);

    // When & Then
    try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
      mockedSecurity.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);

      mockMvc.perform(post("/recipe-management/recipes/123/steps/1/comment")
          .contentType(MediaType.APPLICATION_JSON)
          .content(requestBody))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.commentId").value(2))
          .andExpect(jsonPath("$.recipeId").value(123))
          .andExpect(jsonPath("$.stepId").value(1))
          .andExpect(jsonPath("$.userId").value(currentUserId.toString()))
          .andExpect(jsonPath("$.commentText").value(commentText))
          .andExpect(jsonPath("$.isPublic").value(false))
          .andExpect(jsonPath("$.createdAt").exists())
          .andExpect(jsonPath("$.updatedAt").exists())
          .andExpect(header().exists("X-Request-ID"));
    }
  }

  @Test
  @Tag("error-processing")
  @DisplayName("POST /recipes/{recipeId}/steps/{stepId}/comment should return 404 for non-existent recipe")
  void shouldHandleNotFoundForNonExistentRecipe() throws Exception {
    // Given
    when(recipeRepository.existsById(999L)).thenReturn(false);

    String requestBody = """
        {
          "comment": "Test comment",
          "isPublic": true
        }
        """;

    // When & Then
    mockMvc.perform(post("/recipe-management/recipes/999/steps/1/comment")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
        .andExpect(status().isNotFound())
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("POST /recipes/{recipeId}/steps/{stepId}/comment should return 404 for non-existent step")
  void shouldHandleNotFoundForNonExistentStep() throws Exception {
    // Given
    when(recipeRepository.existsById(123L)).thenReturn(true);
    when(recipeStepRepository.findByStepIdAndRecipeRecipeId(999L, 123L)).thenReturn(Optional.empty());

    String requestBody = """
        {
          "comment": "Test comment",
          "isPublic": true
        }
        """;

    // When & Then
    mockMvc.perform(post("/recipe-management/recipes/123/steps/999/comment")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
        .andExpect(status().isNotFound())
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("POST /recipes/{recipeId}/steps/{stepId}/comment should return 400 for invalid request")
  void shouldHandleInvalidRequest() throws Exception {
    // Given - empty comment text
    String requestBody = """
        {
          "comment": "",
          "isPublic": true
        }
        """;

    // When & Then
    mockMvc.perform(post("/recipe-management/recipes/123/steps/1/comment")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("POST /recipes/{recipeId}/steps/{stepId}/comment should return 400 for missing comment field")
  void shouldHandleMissingCommentField() throws Exception {
    // Given - missing comment field
    String requestBody = """
        {
          "isPublic": true
        }
        """;

    // When & Then
    mockMvc.perform(post("/recipe-management/recipes/123/steps/1/comment")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(header().exists("X-Request-ID"));
  }
}
