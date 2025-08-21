package com.recipe_manager.component_tests.step_service;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
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
import org.springframework.test.context.TestPropertySource;

/**
 * Component tests for GET /recipe-management/recipes/{recipeId}/steps/{stepId}/comment endpoint.
 * Tests the actual StepService getStepComments logic with mocked repository calls.
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
class GetStepCommentsComponentTest extends AbstractComponentTest {

  private Recipe testRecipe;
  private RecipeStep testStep;
  private UUID userId1;
  private UUID userId2;
  private List<StepComment> stepComments;

  @Override
  @BeforeEach
  protected void setUp() {
    super.setUp();
    useRealStepService(); // Use real service with mocked repositories

    userId1 = UUID.randomUUID();
    userId2 = UUID.randomUUID();

    testRecipe = Recipe.builder()
        .recipeId(123L)
        .userId(userId1)
        .title("Test Recipe")
        .description("A test recipe")
        .build();

    testStep = RecipeStep.builder()
        .stepId(1L)
        .recipe(testRecipe)
        .stepNumber(1)
        .instruction("Mix ingredients")
        .build();

    LocalDateTime now = LocalDateTime.now();
    stepComments = Arrays.asList(
        StepComment.builder()
            .commentId(1L)
            .recipeId(123L)
            .step(testStep)
            .userId(userId1)
            .commentText("Great step, very clear instructions!")
            .isPublic(true)
            .createdAt(now.minusHours(2))
            .updatedAt(now.minusHours(2))
            .build(),
        StepComment.builder()
            .commentId(2L)
            .recipeId(123L)
            .step(testStep)
            .userId(userId2)
            .commentText("This step helped me a lot")
            .isPublic(true)
            .createdAt(now.minusHours(1))
            .updatedAt(now.minusHours(1))
            .build(),
        StepComment.builder()
            .commentId(3L)
            .recipeId(123L)
            .step(testStep)
            .userId(userId1)
            .commentText("Private note for myself")
            .isPublic(false)
            .createdAt(now.minusMinutes(30))
            .updatedAt(now.minusMinutes(30))
            .build()
    );
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("GET /recipes/{recipeId}/steps/{stepId}/comment should return all comments for step")
  void shouldGetStepCommentsSuccessfully() throws Exception {
    // Given
    when(recipeRepository.existsById(123L)).thenReturn(true);
    when(recipeStepRepository.findByStepIdAndRecipeRecipeId(1L, 123L)).thenReturn(Optional.of(testStep));
    when(stepCommentRepository.findByRecipeIdAndStepStepIdOrderByCreatedAtAsc(123L, 1L))
        .thenReturn(stepComments);

    // When & Then
    mockMvc.perform(get("/recipe-management/recipes/123/steps/1/comment"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.recipeId").value(123))
        .andExpect(jsonPath("$.stepId").value(1))
        .andExpect(jsonPath("$.comments").isArray())
        .andExpect(jsonPath("$.comments.length()").value(3))
        .andExpect(jsonPath("$.comments[0].commentId").value(1))
        .andExpect(jsonPath("$.comments[0].commentText").value("Great step, very clear instructions!"))
        .andExpect(jsonPath("$.comments[0].userId").value(userId1.toString()))
        .andExpect(jsonPath("$.comments[0].isPublic").value(true))
        .andExpect(jsonPath("$.comments[1].commentId").value(2))
        .andExpect(jsonPath("$.comments[1].commentText").value("This step helped me a lot"))
        .andExpect(jsonPath("$.comments[1].userId").value(userId2.toString()))
        .andExpect(jsonPath("$.comments[1].isPublic").value(true))
        .andExpect(jsonPath("$.comments[2].commentId").value(3))
        .andExpect(jsonPath("$.comments[2].commentText").value("Private note for myself"))
        .andExpect(jsonPath("$.comments[2].userId").value(userId1.toString()))
        .andExpect(jsonPath("$.comments[2].isPublic").value(false))
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("GET /recipes/{recipeId}/steps/{stepId}/comment should return empty list when no comments exist")
  void shouldReturnEmptyListWhenNoCommentsExist() throws Exception {
    // Given
    when(recipeRepository.existsById(123L)).thenReturn(true);
    when(recipeStepRepository.findByStepIdAndRecipeRecipeId(1L, 123L)).thenReturn(Optional.of(testStep));
    when(stepCommentRepository.findByRecipeIdAndStepStepIdOrderByCreatedAtAsc(123L, 1L))
        .thenReturn(Arrays.asList());

    // When & Then
    mockMvc.perform(get("/recipe-management/recipes/123/steps/1/comment"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.recipeId").value(123))
        .andExpect(jsonPath("$.stepId").value(1))
        .andExpect(jsonPath("$.comments").isArray())
        .andExpect(jsonPath("$.comments.length()").value(0))
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("GET /recipes/{recipeId}/steps/{stepId}/comment should return 404 for non-existent recipe")
  void shouldHandleNotFoundForNonExistentRecipe() throws Exception {
    // Given
    when(recipeRepository.existsById(999L)).thenReturn(false);

    // When & Then
    mockMvc.perform(get("/recipe-management/recipes/999/steps/1/comment"))
        .andExpect(status().isNotFound())
        .andExpect(header().exists("X-Request-ID"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("GET /recipes/{recipeId}/steps/{stepId}/comment should return 404 for non-existent step")
  void shouldHandleNotFoundForNonExistentStep() throws Exception {
    // Given
    when(recipeRepository.existsById(123L)).thenReturn(true);
    when(recipeStepRepository.findByStepIdAndRecipeRecipeId(999L, 123L)).thenReturn(Optional.empty());

    // When & Then
    mockMvc.perform(get("/recipe-management/recipes/123/steps/999/comment"))
        .andExpect(status().isNotFound())
        .andExpect(header().exists("X-Request-ID"));
  }
}
