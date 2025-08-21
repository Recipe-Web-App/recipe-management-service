package com.recipe_manager.component_tests.step_service;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.recipe_manager.component_tests.AbstractComponentTest;
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.entity.recipe.RecipeStep;
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
 * Component tests for GET /recipe-management/recipes/{recipeId}/steps endpoint.
 * Tests the actual StepService getSteps logic with mocked repository calls.
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
class GetRecipeStepsComponentTest extends AbstractComponentTest {

  private Recipe testRecipe;
  private RecipeStep step1;
  private RecipeStep step2;

  @Override
  @BeforeEach
  protected void setUp() {
    super.setUp();
    useRealStepService(); // Use real service with mocked repositories

    // Create test data
    testRecipe = Recipe.builder()
        .recipeId(123L)
        .userId(UUID.randomUUID())
        .title("Test Recipe")
        .description("A test recipe")
        .build();

    step1 = RecipeStep.builder()
        .stepId(1L)
        .recipe(testRecipe)
        .stepNumber(1)
        .instruction("Mix ingredients")
        .build();

    step2 = RecipeStep.builder()
        .stepId(2L)
        .recipe(testRecipe)
        .stepNumber(2)
        .instruction("Cook for 20 minutes")
        .build();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("GET /recipes/{recipeId}/steps should return steps for valid recipe ID")
  void shouldGetRecipeStepsSuccessfully() throws Exception {
    // Given
    List<RecipeStep> steps = Arrays.asList(step1, step2);
    when(recipeRepository.existsById(123L)).thenReturn(true);
    when(recipeStepRepository.findByRecipeRecipeIdOrderByStepNumberAsc(123L)).thenReturn(steps);

    // When & Then
    try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
      mockedSecurity.when(SecurityUtils::getCurrentUserId).thenReturn(testRecipe.getUserId());
      mockMvc.perform(get("/recipe-management/recipes/123/steps")
          .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.recipeId").value(123))
          .andExpect(jsonPath("$.steps").isArray())
          .andExpect(jsonPath("$.steps.length()").value(2))
          .andExpect(jsonPath("$.steps[0].stepId").value(1))
          .andExpect(jsonPath("$.steps[0].stepNumber").value(1))
          .andExpect(jsonPath("$.steps[0].instruction").value("Mix ingredients"))
          .andExpect(jsonPath("$.steps[1].stepId").value(2))
          .andExpect(jsonPath("$.steps[1].stepNumber").value(2))
          .andExpect(jsonPath("$.steps[1].instruction").value("Cook for 20 minutes"))
          .andExpect(header().exists("X-Request-ID"));
    }
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("GET /recipes/{recipeId}/steps should return empty steps for recipe with no steps")
  void shouldReturnEmptyStepsWhenNoStepsExist() throws Exception {
    // Given
    when(recipeRepository.existsById(123L)).thenReturn(true);
    when(recipeStepRepository.findByRecipeRecipeIdOrderByStepNumberAsc(123L)).thenReturn(Collections.emptyList());

    // When & Then
    try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
      mockedSecurity.when(SecurityUtils::getCurrentUserId).thenReturn(testRecipe.getUserId());
      mockMvc.perform(get("/recipe-management/recipes/123/steps")
          .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.recipeId").value(123))
          .andExpect(jsonPath("$.steps").isArray())
          .andExpect(jsonPath("$.steps.length()").value(0))
          .andExpect(header().exists("X-Request-ID"));
    }
  }

  @Test
  @Tag("error-processing")
  @DisplayName("GET /recipes/{recipeId}/steps should return 404 for non-existent recipe ID")
  void shouldHandleNotFoundForNonExistentRecipe() throws Exception {
    // Given
    when(recipeRepository.existsById(999L)).thenReturn(false);

    // When & Then
    try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
      mockedSecurity.when(SecurityUtils::getCurrentUserId).thenReturn(UUID.randomUUID());
      mockMvc.perform(get("/recipe-management/recipes/999/steps")
          .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound())
          .andExpect(header().exists("X-Request-ID"));
    }
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("GET /recipes/{recipeId}/steps should handle steps in correct order")
  void shouldReturnStepsInCorrectOrder() throws Exception {
    // Given - steps returned out of order from repository
    RecipeStep step3 = RecipeStep.builder()
        .stepId(3L)
        .recipe(testRecipe)
        .stepNumber(3)
        .instruction("Serve hot")
        .build();

    List<RecipeStep> stepsInOrder = Arrays.asList(step1, step2, step3);
    when(recipeRepository.existsById(123L)).thenReturn(true);
    when(recipeStepRepository.findByRecipeRecipeIdOrderByStepNumberAsc(123L)).thenReturn(stepsInOrder);

    // When & Then
    try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
      mockedSecurity.when(SecurityUtils::getCurrentUserId).thenReturn(testRecipe.getUserId());
      mockMvc.perform(get("/recipe-management/recipes/123/steps")
          .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.recipeId").value(123))
          .andExpect(jsonPath("$.steps").isArray())
          .andExpect(jsonPath("$.steps.length()").value(3))
          .andExpect(jsonPath("$.steps[0].stepNumber").value(1))
          .andExpect(jsonPath("$.steps[1].stepNumber").value(2))
          .andExpect(jsonPath("$.steps[2].stepNumber").value(3))
          .andExpect(jsonPath("$.steps[2].instruction").value("Serve hot"))
          .andExpect(header().exists("X-Request-ID"));
    }
  }
}
