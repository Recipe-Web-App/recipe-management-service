package com.recipe_manager.unit_tests.model.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.recipe_manager.model.dto.recipe.StepCommentDto;
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.entity.recipe.RecipeStep;
import com.recipe_manager.model.entity.recipe.StepComment;
import com.recipe_manager.model.mapper.StepCommentMapper;

@Tag("unit")
class StepCommentMapperTest {

  private StepCommentMapper stepCommentMapper;

  private StepComment stepComment;
  private StepCommentDto stepCommentDto;
  private RecipeStep recipeStep;
  private Recipe recipe;

  @BeforeEach
  void setUp() {
    stepCommentMapper = Mappers.getMapper(StepCommentMapper.class);

    recipe = Recipe.builder()
        .recipeId(1L)
        .title("Test Recipe")
        .build();

    recipeStep = RecipeStep.builder()
        .stepId(1L)
        .recipe(recipe)
        .stepNumber(1)
        .instruction("Test instruction")
        .build();

    UUID userId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    stepComment = StepComment.builder()
        .commentId(1L)
        .recipeId(1L)
        .step(recipeStep)
        .userId(userId)
        .commentText("Test comment")
        .isPublic(true)
        .createdAt(now)
        .updatedAt(now)
        .build();

    stepCommentDto = StepCommentDto.builder()
        .commentId(1L)
        .recipeId(1L)
        .stepId(1L)
        .userId(userId)
        .commentText("Test comment")
        .isPublic(true)
        .createdAt(now)
        .updatedAt(now)
        .build();
  }

  @Test
  void testToDto() {
    StepCommentDto result = stepCommentMapper.toDto(stepComment);

    assertThat(result).isNotNull();
    assertThat(result.getCommentId()).isEqualTo(stepComment.getCommentId());
    assertThat(result.getRecipeId()).isEqualTo(stepComment.getRecipeId());
    assertThat(result.getStepId()).isEqualTo(stepComment.getStep().getStepId());
    assertThat(result.getUserId()).isEqualTo(stepComment.getUserId());
    assertThat(result.getCommentText()).isEqualTo(stepComment.getCommentText());
    assertThat(result.getIsPublic()).isEqualTo(stepComment.getIsPublic());
    assertThat(result.getCreatedAt()).isEqualTo(stepComment.getCreatedAt());
    assertThat(result.getUpdatedAt()).isEqualTo(stepComment.getUpdatedAt());
  }

  @Test
  void testToDtoWithNullEntity() {
    StepCommentDto result = stepCommentMapper.toDto(null);

    assertThat(result).isNull();
  }

  @Test
  void testToDtoWithMinimalData() {
    StepComment minimal = StepComment.builder()
        .commentId(2L)
        .recipeId(2L)
        .step(recipeStep)
        .userId(UUID.randomUUID())
        .commentText("Minimal comment")
        .build();

    StepCommentDto result = stepCommentMapper.toDto(minimal);

    assertThat(result).isNotNull();
    assertThat(result.getCommentId()).isEqualTo(2L);
    assertThat(result.getRecipeId()).isEqualTo(2L);
    assertThat(result.getStepId()).isEqualTo(1L);
    assertThat(result.getCommentText()).isEqualTo("Minimal comment");
    assertThat(result.getIsPublic()).isTrue(); // @Builder.Default sets this to true
    assertThat(result.getCreatedAt()).isNull();
    assertThat(result.getUpdatedAt()).isNull();
  }

  @Test
  void testToDtoList() {
    RecipeStep step2 = RecipeStep.builder()
        .stepId(2L)
        .recipe(recipe)
        .stepNumber(2)
        .instruction("Another instruction")
        .build();

    StepComment comment2 = StepComment.builder()
        .commentId(2L)
        .recipeId(1L)
        .step(step2)
        .userId(UUID.randomUUID())
        .commentText("Another comment")
        .isPublic(false)
        .build();

    List<StepComment> entities = Arrays.asList(stepComment, comment2);
    List<StepCommentDto> result = stepCommentMapper.toDtoList(entities);

    assertThat(result).hasSize(2);
    assertThat(result.get(0).getCommentId()).isEqualTo(1L);
    assertThat(result.get(0).getStepId()).isEqualTo(1L);
    assertThat(result.get(1).getCommentId()).isEqualTo(2L);
    assertThat(result.get(1).getStepId()).isEqualTo(2L);
  }

  @Test
  void testToDtoListWithEmptyList() {
    List<StepComment> entities = Arrays.asList();
    List<StepCommentDto> result = stepCommentMapper.toDtoList(entities);

    assertThat(result).isEmpty();
  }

  @Test
  void testToDtoListWithNullList() {
    List<StepCommentDto> result = stepCommentMapper.toDtoList(null);

    assertThat(result).isNull();
  }

  @Test
  void testToEntity() {
    StepComment result = stepCommentMapper.toEntity(stepCommentDto);

    assertThat(result).isNotNull();
    assertThat(result.getCommentId()).isEqualTo(stepCommentDto.getCommentId());
    assertThat(result.getRecipeId()).isEqualTo(stepCommentDto.getRecipeId());
    assertThat(result.getStep()).isNull(); // Ignored in mapping
    assertThat(result.getUserId()).isEqualTo(stepCommentDto.getUserId());
    assertThat(result.getCommentText()).isEqualTo(stepCommentDto.getCommentText());
    assertThat(result.getIsPublic()).isEqualTo(stepCommentDto.getIsPublic());
    assertThat(result.getCreatedAt()).isEqualTo(stepCommentDto.getCreatedAt());
    assertThat(result.getUpdatedAt()).isEqualTo(stepCommentDto.getUpdatedAt());
  }

  @Test
  void testToEntityWithNullDto() {
    StepComment result = stepCommentMapper.toEntity(null);

    assertThat(result).isNull();
  }

  @Test
  void testToEntityWithMinimalData() {
    StepCommentDto minimal = StepCommentDto.builder()
        .commentId(3L)
        .recipeId(3L)
        .stepId(3L)
        .userId(UUID.randomUUID())
        .commentText("Minimal DTO comment")
        .build();

    StepComment result = stepCommentMapper.toEntity(minimal);

    assertThat(result).isNotNull();
    assertThat(result.getCommentId()).isEqualTo(3L);
    assertThat(result.getRecipeId()).isEqualTo(3L);
    assertThat(result.getStep()).isNull(); // Ignored in mapping
    assertThat(result.getCommentText()).isEqualTo("Minimal DTO comment");
    assertThat(result.getIsPublic()).isNull();
    assertThat(result.getCreatedAt()).isNull();
    assertThat(result.getUpdatedAt()).isNull();
  }

  @Test
  void testMapperCircularConversion() {
    // Test entity -> dto -> entity conversion
    StepCommentDto dto = stepCommentMapper.toDto(stepComment);
    StepComment entity = stepCommentMapper.toEntity(dto);

    assertThat(entity.getCommentId()).isEqualTo(stepComment.getCommentId());
    assertThat(entity.getRecipeId()).isEqualTo(stepComment.getRecipeId());
    assertThat(entity.getUserId()).isEqualTo(stepComment.getUserId());
    assertThat(entity.getCommentText()).isEqualTo(stepComment.getCommentText());
    assertThat(entity.getIsPublic()).isEqualTo(stepComment.getIsPublic());
    assertThat(entity.getCreatedAt()).isEqualTo(stepComment.getCreatedAt());
    assertThat(entity.getUpdatedAt()).isEqualTo(stepComment.getUpdatedAt());
    // Note: step relationship is not preserved due to @Mapping(target = "step", ignore = true)
  }
}
