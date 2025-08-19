package com.recipe_manager.component_tests.step_service;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
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
 * Component tests for DELETE /recipe-management/recipes/{recipeId}/steps/{stepId}/comment endpoint.
 * Tests the actual StepService deleteComment logic with mocked repository calls.
 */
@SpringBootTest(classes = {
    RecipeStepMapperImpl.class,
    StepCommentMapperImpl.class
})
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=none",
    "spring.flyway.enabled=false"
})
@Tag("component")
class DeleteStepCommentComponentTest extends AbstractComponentTest {

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
        .commentText("Comment to be deleted")
        .isPublic(true)
        .createdAt(LocalDateTime.now().minusHours(1))
        .updatedAt(LocalDateTime.now().minusHours(1))
        .build();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("DELETE /recipes/{recipeId}/steps/{stepId}/comment should delete comment successfully")
  void shouldDeleteStepCommentSuccessfully() throws Exception {
    // Given
    when(recipeRepository.existsById(123L)).thenReturn(true);
    when(recipeStepRepository.findByStepIdAndRecipeRecipeId(1L, 123L)).thenReturn(Optional.of(testStep));
    when(stepCommentRepository.findByCommentIdAndStepStepId(1L, 1L)).thenReturn(Optional.of(existingComment));
    doNothing().when(stepCommentRepository).delete(existingComment);

    String requestBody = """
        {
          "commentId": 1
        }
        """;

    // When & Then
    try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
      mockedSecurity.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);

      mockMvc.perform(delete("/recipe-management/recipes/123/steps/1/comment")
          .contentType(MediaType.APPLICATION_JSON)
          .content(requestBody))
          .andExpect(status().isNoContent())
          .andExpect(header().exists("X-Request-ID"));
    }
  }

  @Test
  @Tag("error-processing")
  @DisplayName("DELETE /recipes/{recipeId}/steps/{stepId}/comment should return 404 for non-existent recipe")
  void shouldHandleNotFoundForNonExistentRecipe() throws Exception {
    // Given
    when(recipeRepository.existsById(999L)).thenReturn(false);

    String requestBody = """
        {
          "commentId": 1
        }
        """;

    // When & Then
    try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
      mockedSecurity.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);

      mockMvc.perform(delete("/recipe-management/recipes/999/steps/1/comment")
          .contentType(MediaType.APPLICATION_JSON)
          .content(requestBody))
          .andExpect(status().isNotFound())
          .andExpect(header().exists("X-Request-ID"));
    }
  }

  @Test
  @Tag("error-processing")
  @DisplayName("DELETE /recipes/{recipeId}/steps/{stepId}/comment should return 404 for non-existent step")
  void shouldHandleNotFoundForNonExistentStep() throws Exception {
    // Given
    when(recipeRepository.existsById(123L)).thenReturn(true);
    when(recipeStepRepository.findByStepIdAndRecipeRecipeId(999L, 123L)).thenReturn(Optional.empty());

    String requestBody = """
        {
          "commentId": 1
        }
        """;

    // When & Then
    try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
      mockedSecurity.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);

      mockMvc.perform(delete("/recipe-management/recipes/123/steps/999/comment")
          .contentType(MediaType.APPLICATION_JSON)
          .content(requestBody))
          .andExpect(status().isNotFound())
          .andExpect(header().exists("X-Request-ID"));
    }
  }

  @Test
  @Tag("error-processing")
  @DisplayName("DELETE /recipes/{recipeId}/steps/{stepId}/comment should return 404 for non-existent comment")
  void shouldHandleNotFoundForNonExistentComment() throws Exception {
    // Given
    when(recipeRepository.existsById(123L)).thenReturn(true);
    when(recipeStepRepository.findByStepIdAndRecipeRecipeId(1L, 123L)).thenReturn(Optional.of(testStep));
    when(stepCommentRepository.findByCommentIdAndStepStepId(999L, 1L)).thenReturn(Optional.empty());

    String requestBody = """
        {
          "commentId": 999
        }
        """;

    // When & Then
    try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
      mockedSecurity.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);

      mockMvc.perform(delete("/recipe-management/recipes/123/steps/1/comment")
          .contentType(MediaType.APPLICATION_JSON)
          .content(requestBody))
          .andExpect(status().isNotFound())
          .andExpect(header().exists("X-Request-ID"));
    }
  }

  @Test
  @Tag("error-processing")
  @DisplayName("DELETE /recipes/{recipeId}/steps/{stepId}/comment should return 403 for unauthorized user")
  void shouldHandleUnauthorizedUserDelete() throws Exception {
    // Given - different user trying to delete
    UUID differentUserId = UUID.randomUUID();

    when(recipeRepository.existsById(123L)).thenReturn(true);
    when(recipeStepRepository.findByStepIdAndRecipeRecipeId(1L, 123L)).thenReturn(Optional.of(testStep));
    when(stepCommentRepository.findByCommentIdAndStepStepId(1L, 1L)).thenReturn(Optional.of(existingComment));

    String requestBody = """
        {
          "commentId": 1
        }
        """;

    // When & Then
    try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
      mockedSecurity.when(SecurityUtils::getCurrentUserId).thenReturn(differentUserId);

      mockMvc.perform(delete("/recipe-management/recipes/123/steps/1/comment")
          .contentType(MediaType.APPLICATION_JSON)
          .content(requestBody))
          .andExpect(status().isForbidden())
          .andExpect(header().exists("X-Request-ID"));
    }
  }

  @Test
  @Tag("error-processing")
  @DisplayName("DELETE /recipes/{recipeId}/steps/{stepId}/comment should NOT allow recipe owner to delete others' comments")
  void shouldNotAllowRecipeOwnerToDeleteOthersComments() throws Exception {
    // Given - recipe owner trying to delete comment from different user (should fail)
    UUID commentOwnerUserId = UUID.randomUUID();
    StepComment commentFromOtherUser = StepComment.builder()
        .commentId(2L)
        .recipeId(123L)
        .step(testStep)
        .userId(commentOwnerUserId) // Different user's comment
        .commentText("Comment from another user")
        .isPublic(true)
        .createdAt(LocalDateTime.now().minusHours(1))
        .updatedAt(LocalDateTime.now().minusHours(1))
        .build();

    when(recipeRepository.existsById(123L)).thenReturn(true);
    when(recipeStepRepository.findByStepIdAndRecipeRecipeId(1L, 123L)).thenReturn(Optional.of(testStep));
    when(stepCommentRepository.findByCommentIdAndStepStepId(2L, 1L)).thenReturn(Optional.of(commentFromOtherUser));

    String requestBody = """
        {
          "commentId": 2
        }
        """;

    // When & Then - even recipe owner should NOT be able to delete other people's comments
    try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
      mockedSecurity.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId); // Recipe owner

      mockMvc.perform(delete("/recipe-management/recipes/123/steps/1/comment")
          .contentType(MediaType.APPLICATION_JSON)
          .content(requestBody))
          .andExpect(status().isForbidden())
          .andExpect(header().exists("X-Request-ID"));
    }
  }
}
