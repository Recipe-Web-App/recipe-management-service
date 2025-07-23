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
    // Ignored fields should be null or default values
    assertThat(result.getMedia()).isNotNull().isEmpty(); // MapStruct returns empty list for @Default fields
    assertThat(result.getUpdatedAt()).isNull();
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
