package com.recipe_manager.unit_tests.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.recipe_manager.model.dto.recipe.RecipeStepDto;
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.entity.recipe.RecipeStep;
import com.recipe_manager.model.mapper.RecipeStepMapper;

@org.junit.jupiter.api.Tag("unit")
class RecipeStepMapperTest {

  private final RecipeStepMapper recipeStepMapper = Mappers.getMapper(RecipeStepMapper.class);

  private RecipeStepDto recipeStepDto;
  private RecipeStep recipeStep;
  private Recipe recipe;
  private Long recipeId;
  private LocalDateTime createdAt;

  @BeforeEach
  void setUp() {
    recipeId = 123L;
    createdAt = LocalDateTime.now();

    recipe = Recipe.builder()
        .recipeId(recipeId)
        .build();

    recipeStepDto = RecipeStepDto.builder()
        .stepId(1L)
        .recipeId(recipeId)
        .stepNumber(1)
        .instruction("Mix all ingredients together")
        .optional(false)
        .timerSeconds(300)
        .createdAt(createdAt)
        .build();

    recipeStep = RecipeStep.builder()
        .stepId(1L)
        .recipe(recipe)
        .stepNumber(1)
        .instruction("Mix all ingredients together")
        .optional(false)
        .timerSeconds(300)
        .createdAt(createdAt)
        .build();
  }

  @Test
  @DisplayName("Should map RecipeStepDto to RecipeStep entity")
  void shouldMapRecipeStepDtoToEntity() {
    // Act
    RecipeStep result = recipeStepMapper.toEntity(recipeStepDto);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getStepNumber()).isEqualTo(1);
    assertThat(result.getInstruction()).isEqualTo("Mix all ingredients together");
    assertThat(result.getOptional()).isEqualTo(false);
    assertThat(result.getTimerSeconds()).isEqualTo(300);

    // Ignored fields should be null
    assertThat(result.getStepId()).isNull();
    assertThat(result.getRecipe()).isNull();
    assertThat(result.getCreatedAt()).isNull();
  }

  @Test
  @DisplayName("Should handle null RecipeStepDto")
  void shouldHandleNullRecipeStepDto() {
    // Act
    RecipeStep result = recipeStepMapper.toEntity(null);

    // Assert
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("Should map RecipeStep entity to RecipeStepDto")
  void shouldMapRecipeStepEntityToDto() {
    // Act
    RecipeStepDto result = recipeStepMapper.toDto(recipeStep);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getStepId()).isEqualTo(1L);
    assertThat(result.getRecipeId()).isEqualTo(recipeId);
    assertThat(result.getStepNumber()).isEqualTo(1);
    assertThat(result.getInstruction()).isEqualTo("Mix all ingredients together");
    assertThat(result.getOptional()).isEqualTo(false);
    assertThat(result.getTimerSeconds()).isEqualTo(300);
    assertThat(result.getCreatedAt()).isEqualTo(createdAt);
  }

  @Test
  @DisplayName("Should handle null RecipeStep entity")
  void shouldHandleNullRecipeStepEntity() {
    // Act
    RecipeStepDto result = recipeStepMapper.toDto(null);

    // Assert
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("Should handle RecipeStep with null recipe")
  void shouldHandleRecipeStepWithNullRecipe() {
    // Arrange
    RecipeStep stepWithNullRecipe = RecipeStep.builder()
        .stepId(2L)
        .recipe(null)
        .stepNumber(2)
        .instruction("Bake in oven")
        .optional(true)
        .timerSeconds(1800)
        .createdAt(createdAt)
        .build();

    // Act
    RecipeStepDto result = recipeStepMapper.toDto(stepWithNullRecipe);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getStepId()).isEqualTo(2L);
    assertThat(result.getRecipeId()).isNull();
    assertThat(result.getStepNumber()).isEqualTo(2);
    assertThat(result.getInstruction()).isEqualTo("Bake in oven");
    assertThat(result.getOptional()).isEqualTo(true);
    assertThat(result.getTimerSeconds()).isEqualTo(1800);
    assertThat(result.getCreatedAt()).isEqualTo(createdAt);
  }

  @Test
  @DisplayName("Should map list of RecipeStepDto to list of entities")
  void shouldMapListOfRecipeStepDtoToEntityList() {
    // Arrange
    RecipeStepDto stepDto2 = RecipeStepDto.builder()
        .stepId(2L)
        .recipeId(recipeId)
        .stepNumber(2)
        .instruction("Let it rest for 10 minutes")
        .optional(true)
        .timerSeconds(600)
        .build();

    List<RecipeStepDto> stepDtos = Arrays.asList(recipeStepDto, stepDto2);

    // Act
    List<RecipeStep> result = recipeStepMapper.toEntityList(stepDtos);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);

    RecipeStep entity1 = result.get(0);
    assertThat(entity1.getStepNumber()).isEqualTo(1);
    assertThat(entity1.getInstruction()).isEqualTo("Mix all ingredients together");
    assertThat(entity1.getOptional()).isEqualTo(false);
    assertThat(entity1.getTimerSeconds()).isEqualTo(300);

    RecipeStep entity2 = result.get(1);
    assertThat(entity2.getStepNumber()).isEqualTo(2);
    assertThat(entity2.getInstruction()).isEqualTo("Let it rest for 10 minutes");
    assertThat(entity2.getOptional()).isEqualTo(true);
    assertThat(entity2.getTimerSeconds()).isEqualTo(600);
  }

  @Test
  @DisplayName("Should map list of RecipeStep entities to list of DTOs")
  void shouldMapListOfRecipeStepEntitiesToDtoList() {
    // Arrange
    RecipeStep recipeStep2 = RecipeStep.builder()
        .stepId(2L)
        .recipe(recipe)
        .stepNumber(2)
        .instruction("Let it rest for 10 minutes")
        .optional(true)
        .timerSeconds(600)
        .createdAt(createdAt.minusMinutes(5))
        .build();

    List<RecipeStep> steps = Arrays.asList(recipeStep, recipeStep2);

    // Act
    List<RecipeStepDto> result = recipeStepMapper.toDtoList(steps);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);

    RecipeStepDto dto1 = result.get(0);
    assertThat(dto1.getStepId()).isEqualTo(1L);
    assertThat(dto1.getRecipeId()).isEqualTo(recipeId);
    assertThat(dto1.getStepNumber()).isEqualTo(1);
    assertThat(dto1.getInstruction()).isEqualTo("Mix all ingredients together");
    assertThat(dto1.getOptional()).isEqualTo(false);
    assertThat(dto1.getTimerSeconds()).isEqualTo(300);
    assertThat(dto1.getCreatedAt()).isEqualTo(createdAt);

    RecipeStepDto dto2 = result.get(1);
    assertThat(dto2.getStepId()).isEqualTo(2L);
    assertThat(dto2.getRecipeId()).isEqualTo(recipeId);
    assertThat(dto2.getStepNumber()).isEqualTo(2);
    assertThat(dto2.getInstruction()).isEqualTo("Let it rest for 10 minutes");
    assertThat(dto2.getOptional()).isEqualTo(true);
    assertThat(dto2.getTimerSeconds()).isEqualTo(600);
    assertThat(dto2.getCreatedAt()).isEqualTo(createdAt.minusMinutes(5));
  }

  @Test
  @DisplayName("Should handle null lists")
  void shouldHandleNullLists() {
    // Act & Assert
    assertThat(recipeStepMapper.toEntityList(null)).isNull();
    assertThat(recipeStepMapper.toDtoList(null)).isNull();
  }

  @Test
  @DisplayName("Should handle empty lists")
  void shouldHandleEmptyLists() {
    // Act & Assert
    List<RecipeStep> emptyEntityList = recipeStepMapper.toEntityList(Arrays.asList());
    assertThat(emptyEntityList).isNotNull().isEmpty();

    List<RecipeStepDto> emptyDtoList = recipeStepMapper.toDtoList(Arrays.asList());
    assertThat(emptyDtoList).isNotNull().isEmpty();
  }

  @Test
  @DisplayName("Should handle lists with null elements")
  void shouldHandleListsWithNullElements() {
    // Arrange
    List<RecipeStepDto> dtosWithNull = Arrays.asList(recipeStepDto, null);
    List<RecipeStep> entitiesWithNull = Arrays.asList(recipeStep, null);

    // Act
    List<RecipeStep> entityResult = recipeStepMapper.toEntityList(dtosWithNull);
    List<RecipeStepDto> dtoResult = recipeStepMapper.toDtoList(entitiesWithNull);

    // Assert
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
    // Arrange
    RecipeStepDto dtoWithNulls = RecipeStepDto.builder()
        .stepNumber(3)
        .instruction("Serve hot")
        .optional(null)
        .timerSeconds(null)
        .build();

    // Act
    RecipeStep result = recipeStepMapper.toEntity(dtoWithNulls);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getStepNumber()).isEqualTo(3);
    assertThat(result.getInstruction()).isEqualTo("Serve hot");
    assertThat(result.getOptional()).isNull();
    assertThat(result.getTimerSeconds()).isNull();
  }

  @Test
  @DisplayName("Should handle RecipeStep with null optional fields")
  void shouldHandleRecipeStepWithNullOptionalFields() {
    // Arrange
    RecipeStep stepWithNulls = RecipeStep.builder()
        .stepId(4L)
        .recipe(recipe)
        .stepNumber(4)
        .instruction("Clean up")
        .optional(null)
        .timerSeconds(null)
        .createdAt(null)
        .build();

    // Act
    RecipeStepDto result = recipeStepMapper.toDto(stepWithNulls);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getStepId()).isEqualTo(4L);
    assertThat(result.getRecipeId()).isEqualTo(recipeId);
    assertThat(result.getStepNumber()).isEqualTo(4);
    assertThat(result.getInstruction()).isEqualTo("Clean up");
    assertThat(result.getOptional()).isNull();
    assertThat(result.getTimerSeconds()).isNull();
    assertThat(result.getCreatedAt()).isNull();
  }

  @Test
  @DisplayName("Should handle RecipeStepDto with default optional value")
  void shouldHandleRecipeStepDtoWithDefaultOptionalValue() {
    // Arrange
    RecipeStepDto dtoWithDefault = RecipeStepDto.builder()
        .stepNumber(5)
        .instruction("Garnish as desired")
        // optional not set, should use default behavior
        .build();

    // Act
    RecipeStep result = recipeStepMapper.toEntity(dtoWithDefault);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getStepNumber()).isEqualTo(5);
    assertThat(result.getInstruction()).isEqualTo("Garnish as desired");
  }
}
