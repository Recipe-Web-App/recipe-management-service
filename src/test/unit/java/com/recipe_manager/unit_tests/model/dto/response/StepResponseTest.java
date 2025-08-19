package com.recipe_manager.unit_tests.model.dto.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.recipe_manager.model.dto.recipe.RecipeStepDto;
import com.recipe_manager.model.dto.response.StepResponse;

@Tag("unit")
class StepResponseTest {

  private StepResponse response;
  private List<RecipeStepDto> stepDtos;

  @BeforeEach
  void setUp() {
    LocalDateTime now = LocalDateTime.now();

    RecipeStepDto step1 = RecipeStepDto.builder()
        .stepId(1L)
        .recipeId(1L)
        .stepNumber(1)
        .instruction("First step")
        .optional(false)
        .timerSeconds(300)
        .createdAt(now)
        .build();

    RecipeStepDto step2 = RecipeStepDto.builder()
        .stepId(2L)
        .recipeId(1L)
        .stepNumber(2)
        .instruction("Second step")
        .optional(true)
        .timerSeconds(null)
        .createdAt(now)
        .build();

    stepDtos = Arrays.asList(step1, step2);

    response = StepResponse.builder()
        .recipeId(1L)
        .steps(stepDtos)
        .build();
  }

  @Test
  void testBuilder() {
    StepResponse newResponse = StepResponse.builder()
        .recipeId(2L)
        .steps(stepDtos)
        .build();

    assertThat(newResponse.getRecipeId()).isEqualTo(2L);
    assertThat(newResponse.getSteps()).isEqualTo(stepDtos);
  }

  @Test
  void testGettersAndSetters() {
    List<RecipeStepDto> newSteps = Collections.emptyList();

    response.setRecipeId(5L);
    response.setSteps(newSteps);

    assertThat(response.getRecipeId()).isEqualTo(5L);
    assertThat(response.getSteps()).isEqualTo(newSteps);
  }

  @Test
  void testEqualsAndHashCode() {
    StepResponse response1 = StepResponse.builder()
        .recipeId(1L)
        .steps(stepDtos)
        .build();

    StepResponse response2 = StepResponse.builder()
        .recipeId(1L)
        .steps(stepDtos)
        .build();

    assertThat(response1).isEqualTo(response2);
    assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
  }

  @Test
  void testToString() {
    String toStringResult = response.toString();

    assertThat(toStringResult).contains("StepResponse");
    assertThat(toStringResult).contains("recipeId=" + response.getRecipeId());
    assertThat(toStringResult).contains("steps=" + response.getSteps());
  }

  @Test
  void testNoArgsConstructor() {
    StepResponse newResponse = new StepResponse();

    assertThat(newResponse.getRecipeId()).isNull();
    assertThat(newResponse.getSteps()).isNull();
  }

  @Test
  void testAllArgsConstructor() {
    StepResponse newResponse = new StepResponse(1L, stepDtos);

    assertThat(newResponse.getRecipeId()).isEqualTo(1L);
    assertThat(newResponse.getSteps()).isEqualTo(stepDtos);
  }

  @Test
  void testWithEmptySteps() {
    List<RecipeStepDto> emptySteps = Collections.emptyList();

    StepResponse responseWithEmptySteps = StepResponse.builder()
        .recipeId(1L)
        .steps(emptySteps)
        .build();

    assertThat(responseWithEmptySteps.getSteps()).isEmpty();
  }

  @Test
  void testWithNullValues() {
    StepResponse responseWithNulls = StepResponse.builder()
        .recipeId(null)
        .steps(null)
        .build();

    assertThat(responseWithNulls.getRecipeId()).isNull();
    assertThat(responseWithNulls.getSteps()).isNull();
  }

  @Test
  void testNotEqualsWithDifferentRecipeId() {
    StepResponse response1 = StepResponse.builder()
        .recipeId(1L)
        .steps(stepDtos)
        .build();

    StepResponse response2 = StepResponse.builder()
        .recipeId(2L)
        .steps(stepDtos)
        .build();

    assertThat(response1).isNotEqualTo(response2);
  }

  @Test
  void testNotEqualsWithDifferentSteps() {
    StepResponse response1 = StepResponse.builder()
        .recipeId(1L)
        .steps(stepDtos)
        .build();

    StepResponse response2 = StepResponse.builder()
        .recipeId(1L)
        .steps(Collections.emptyList())
        .build();

    assertThat(response1).isNotEqualTo(response2);
  }

  @Test
  void testStepsListAccess() {
    assertThat(response.getSteps()).hasSize(2);
    assertThat(response.getSteps().get(0).getInstruction()).isEqualTo("First step");
    assertThat(response.getSteps().get(1).getInstruction()).isEqualTo("Second step");
  }
}
