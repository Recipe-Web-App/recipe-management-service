package com.recipe_manager.model.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import com.recipe_manager.model.dto.recipe.RecipeStepDto;
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.entity.recipe.RecipeStep;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

/**
 * Unit tests for RecipeStepMapper.
 */
@Tag("unit")
class RecipeStepMapperTest {
  @Test
  @org.junit.jupiter.api.Tag("unit")
  @DisplayName("Should map RecipeStepDto to RecipeStep entity")
  void shouldMapRecipeStepDtoToEntity() {
    RecipeStepDto dto = RecipeStepDto.builder()
        .stepId(1L)
        .recipeId(123L)
        .stepNumber(1)
        .instruction("Mix all ingredients together")
        .optional(false)
        .timerSeconds(300)
        .createdAt(LocalDateTime.now())
        .build();
    RecipeStep result = mapper.toEntity(dto);
    assertThat(result).isNotNull();
    assertThat(result.getStepNumber()).isEqualTo(1);
    assertThat(result.getInstruction()).isEqualTo("Mix all ingredients together");
    assertThat(result.getOptional()).isEqualTo(false);
    assertThat(result.getTimerSeconds()).isEqualTo(300);
    assertThat(result.getStepId()).isNull();
    assertThat(result.getRecipe()).isNull();
    assertThat(result.getCreatedAt()).isNull();
  }

  @Test
  @DisplayName("Should handle null RecipeStepDto")
  void shouldHandleNullRecipeStepDto() {
    RecipeStep result = mapper.toEntity(null);
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("Should handle null RecipeStep entity")
  void shouldHandleNullRecipeStepEntity() {
    RecipeStepDto result = mapper.toDto(null);
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("Should handle RecipeStep with null recipe")
  void shouldHandleRecipeStepWithNullRecipe() {
    RecipeStep stepWithNullRecipe = RecipeStep.builder()
        .stepId(2L)
        .recipe(null)
        .stepNumber(2)
        .instruction("Bake in oven")
        .optional(true)
        .timerSeconds(1800)
        .createdAt(LocalDateTime.now())
        .build();
    RecipeStepDto result = mapper.toDto(stepWithNullRecipe);
    assertThat(result).isNotNull();
    assertThat(result.getStepId()).isEqualTo(2L);
    assertThat(result.getRecipeId()).isNull();
    assertThat(result.getStepNumber()).isEqualTo(2);
    assertThat(result.getInstruction()).isEqualTo("Bake in oven");
    assertThat(result.getOptional()).isEqualTo(true);
    assertThat(result.getTimerSeconds()).isEqualTo(1800);
    assertThat(result.getCreatedAt()).isNotNull();
  }

  @Test
  @DisplayName("Should map list of RecipeStepDto to list of entities")
  void shouldMapListOfRecipeStepDtoToEntityList() {
    RecipeStepDto dto1 = RecipeStepDto.builder()
        .stepId(1L)
        .recipeId(123L)
        .stepNumber(1)
        .instruction("Mix all ingredients together")
        .optional(false)
        .timerSeconds(300)
        .build();
    RecipeStepDto dto2 = RecipeStepDto.builder()
        .stepId(2L)
        .recipeId(123L)
        .stepNumber(2)
        .instruction("Let it rest for 10 minutes")
        .optional(true)
        .timerSeconds(600)
        .build();
    List<RecipeStepDto> stepDtos = java.util.Arrays.asList(dto1, dto2);
    List<RecipeStep> result = mapper.toEntityList(stepDtos);
    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);
    assertThat(result.get(0).getStepNumber()).isEqualTo(1);
    assertThat(result.get(1).getStepNumber()).isEqualTo(2);
  }

  @Test
  @DisplayName("Should map list of RecipeStep entities to list of DTOs")
  void shouldMapListOfRecipeStepEntitiesToDtoList() {
    Recipe recipe = Recipe.builder().recipeId(123L).build();
    RecipeStep step1 = RecipeStep.builder()
        .stepId(1L)
        .recipe(recipe)
        .stepNumber(1)
        .instruction("Mix all ingredients together")
        .optional(false)
        .timerSeconds(300)
        .createdAt(LocalDateTime.now())
        .build();
    RecipeStep step2 = RecipeStep.builder()
        .stepId(2L)
        .recipe(recipe)
        .stepNumber(2)
        .instruction("Let it rest for 10 minutes")
        .optional(true)
        .timerSeconds(600)
        .createdAt(LocalDateTime.now().minusMinutes(5))
        .build();
    List<RecipeStep> steps = java.util.Arrays.asList(step1, step2);
    List<RecipeStepDto> result = mapper.toDtoList(steps);
    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);
    assertThat(result.get(0).getStepId()).isEqualTo(1L);
    assertThat(result.get(1).getStepId()).isEqualTo(2L);
  }

  @Test
  @DisplayName("Should handle null lists")
  void shouldHandleNullLists() {
    assertThat(mapper.toEntityList(null)).isNull();
    assertThat(mapper.toDtoList(null)).isNull();
  }

  @Test
  @DisplayName("Should handle empty lists")
  void shouldHandleEmptyLists() {
    List<RecipeStep> emptyEntityList = mapper.toEntityList(java.util.Arrays.asList());
    assertThat(emptyEntityList).isNotNull().isEmpty();
    List<RecipeStepDto> emptyDtoList = mapper.toDtoList(java.util.Arrays.asList());
    assertThat(emptyDtoList).isNotNull().isEmpty();
  }

  @Test
  @DisplayName("Should handle lists with null elements")
  void shouldHandleListsWithNullElements() {
    RecipeStepDto dto1 = RecipeStepDto.builder()
        .stepId(1L)
        .recipeId(123L)
        .stepNumber(1)
        .instruction("Mix all ingredients together")
        .optional(false)
        .timerSeconds(300)
        .build();
    List<RecipeStepDto> dtosWithNull = java.util.Arrays.asList(dto1, null);
    RecipeStep step1 = RecipeStep.builder()
        .stepId(1L)
        .stepNumber(1)
        .instruction("Mix all ingredients together")
        .optional(false)
        .timerSeconds(300)
        .build();
    List<RecipeStep> entitiesWithNull = java.util.Arrays.asList(step1, null);
    List<RecipeStep> entityResult = mapper.toEntityList(dtosWithNull);
    List<RecipeStepDto> dtoResult = mapper.toDtoList(entitiesWithNull);
    assertThat(entityResult).isNotNull().hasSize(2);
    assertThat(entityResult.get(0)).isNotNull();
    assertThat(entityResult.get(1)).isNull();
    assertThat(dtoResult).isNotNull().hasSize(2);
    assertThat(dtoResult.get(0)).isNotNull();
    assertThat(dtoResult.get(1)).isNull();
  }

  @Test
  @DisplayName("Should handle RecipeStepDto with null optional fields")
  void shouldHandleRecipeStepDtoWithNullOptionalFields() {
    RecipeStepDto dtoWithNulls = RecipeStepDto.builder()
        .stepNumber(3)
        .instruction("Serve hot")
        .optional(null)
        .timerSeconds(null)
        .build();
    RecipeStep result = mapper.toEntity(dtoWithNulls);
    assertThat(result).isNotNull();
    assertThat(result.getStepNumber()).isEqualTo(3);
    assertThat(result.getInstruction()).isEqualTo("Serve hot");
    assertThat(result.getOptional()).isNull();
    assertThat(result.getTimerSeconds()).isNull();
  }

  @Test
  @DisplayName("Should handle RecipeStep with null optional fields")
  void shouldHandleRecipeStepWithNullOptionalFields() {
    RecipeStep stepWithNulls = RecipeStep.builder()
        .stepId(4L)
        .stepNumber(4)
        .instruction("Clean up")
        .optional(null)
        .timerSeconds(null)
        .createdAt(null)
        .build();
    RecipeStepDto result = mapper.toDto(stepWithNulls);
    assertThat(result).isNotNull();
    assertThat(result.getStepId()).isEqualTo(4L);
    assertThat(result.getStepNumber()).isEqualTo(4);
    assertThat(result.getInstruction()).isEqualTo("Clean up");
    assertThat(result.getOptional()).isNull();
    assertThat(result.getTimerSeconds()).isNull();
    assertThat(result.getCreatedAt()).isNull();
  }

  @Test
  @DisplayName("Should handle RecipeStepDto with default optional value")
  void shouldHandleRecipeStepDtoWithDefaultOptionalValue() {
    RecipeStepDto dtoWithDefault = RecipeStepDto.builder()
        .stepNumber(5)
        .instruction("Garnish as desired")
        .build();
    RecipeStep result = mapper.toEntity(dtoWithDefault);
    assertThat(result).isNotNull();
    assertThat(result.getStepNumber()).isEqualTo(5);
    assertThat(result.getInstruction()).isEqualTo("Garnish as desired");
  }

  private final RecipeStepMapper mapper = Mappers.getMapper(RecipeStepMapper.class);

  @Test
  @Tag("standard-processing")
  @DisplayName("Should map all simple fields from CreateRecipeStepRequest to RecipeStep")
  void shouldMapAllSimpleFields() {
    RecipeStepDto dto = RecipeStepDto.builder()
        .stepNumber(1)
        .instruction("Mix well")
        .optional(true)
        .timerSeconds(120)
        .build();

    RecipeStep entity = mapper.toEntity(dto);

    assertThat(entity).isNotNull();
    assertThat(entity.getStepNumber()).isEqualTo(dto.getStepNumber());
    assertThat(entity.getInstruction()).isEqualTo(dto.getInstruction());
    assertThat(entity.getOptional()).isEqualTo(Boolean.TRUE);
    assertThat(entity.getTimerSeconds()).isEqualTo(dto.getTimerSeconds());
    // Ignored fields
    assertThat(entity.getRecipe()).isNull();
    assertThat(entity.getStepId()).isNull();
    assertThat(entity.getCreatedAt()).isNull();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should map a list of CreateRecipeStepRequest to RecipeStep list")
  void shouldMapList() {
    RecipeStepDto dto1 = RecipeStepDto.builder().stepNumber(1).instruction("A").build();
    RecipeStepDto dto2 = RecipeStepDto.builder().stepNumber(2).instruction("B").build();
    List<RecipeStep> entities = mapper.toEntityList(List.of(dto1, dto2));
    assertThat(entities).hasSize(2);
    assertThat(entities.get(0).getStepNumber()).isEqualTo(1);
    assertThat(entities.get(1).getStepNumber()).isEqualTo(2);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should map RecipeStep entity to RecipeStepDto")
  void shouldMapEntityToDto() {
    Recipe recipe = Recipe.builder()
        .recipeId(15L)
        .build();

    RecipeStep entity = RecipeStep.builder()
        .stepId(3L)
        .stepNumber(2)
        .instruction("Mix all ingredients thoroughly")
        .optional(false)
        .timerSeconds(300)
        .recipe(recipe)
        .createdAt(LocalDateTime.now())
        .build();

    RecipeStepDto result = mapper.toDto(entity);

    assertThat(result).isNotNull();
    assertThat(result.getStepId()).isEqualTo(3L);
    assertThat(result.getStepNumber()).isEqualTo(2);
    assertThat(result.getInstruction()).isEqualTo("Mix all ingredients thoroughly");
    assertThat(result.getOptional()).isFalse();
    assertThat(result.getTimerSeconds()).isEqualTo(300);
    assertThat(result.getRecipeId()).isEqualTo(15L);
    assertThat(result.getCreatedAt()).isNotNull();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should map a list of RecipeStep entities to RecipeStepDto list")
  void shouldMapEntityListToDto() {
    Recipe recipe = Recipe.builder()
        .recipeId(20L)
        .build();

    RecipeStep entity1 = RecipeStep.builder()
        .stepId(1L)
        .stepNumber(1)
        .instruction("Prepare ingredients")
        .optional(false)
        .timerSeconds(60)
        .recipe(recipe)
        .createdAt(LocalDateTime.now())
        .build();

    RecipeStep entity2 = RecipeStep.builder()
        .stepId(2L)
        .stepNumber(2)
        .instruction("Cook for 30 minutes")
        .optional(true)
        .timerSeconds(1800)
        .recipe(recipe)
        .createdAt(LocalDateTime.now())
        .build();

    List<RecipeStepDto> results = mapper.toDtoList(List.of(entity1, entity2));

    assertThat(results).hasSize(2);
    assertThat(results.get(0).getStepNumber()).isEqualTo(1);
    assertThat(results.get(0).getInstruction()).isEqualTo("Prepare ingredients");
    assertThat(results.get(0).getOptional()).isFalse();
    assertThat(results.get(0).getTimerSeconds()).isEqualTo(60);
    assertThat(results.get(1).getStepNumber()).isEqualTo(2);
    assertThat(results.get(1).getInstruction()).isEqualTo("Cook for 30 minutes");
    assertThat(results.get(1).getOptional()).isTrue();
    assertThat(results.get(1).getTimerSeconds()).isEqualTo(1800);
  }
}
