package com.recipe_manager.model.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.recipe_manager.model.dto.recipe.RecipeCommentDto;
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.entity.recipe.RecipeComment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

@Tag("unit")
class RecipeCommentMapperTest {

  private RecipeCommentMapper recipeCommentMapper;

  private RecipeComment recipeComment;
  private RecipeCommentDto recipeCommentDto;
  private Recipe recipe;

  @BeforeEach
  void setUp() {
    recipeCommentMapper = Mappers.getMapper(RecipeCommentMapper.class);

    recipe = Recipe.builder().recipeId(1L).title("Test Recipe").build();

    UUID userId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    recipeComment =
        RecipeComment.builder()
            .commentId(1L)
            .recipe(recipe)
            .userId(userId)
            .commentText("Test comment")
            .isPublic(true)
            .createdAt(now)
            .updatedAt(now)
            .build();

    recipeCommentDto =
        RecipeCommentDto.builder()
            .commentId(1L)
            .recipeId(1L)
            .userId(userId)
            .commentText("Test comment")
            .isPublic(true)
            .createdAt(now)
            .updatedAt(now)
            .build();
  }

  @Test
  void testToDto() {
    RecipeCommentDto result = recipeCommentMapper.toDto(recipeComment);

    assertThat(result).isNotNull();
    assertThat(result.getCommentId()).isEqualTo(recipeComment.getCommentId());
    assertThat(result.getRecipeId()).isEqualTo(recipeComment.getRecipe().getRecipeId());
    assertThat(result.getUserId()).isEqualTo(recipeComment.getUserId());
    assertThat(result.getCommentText()).isEqualTo(recipeComment.getCommentText());
    assertThat(result.getIsPublic()).isEqualTo(recipeComment.getIsPublic());
    assertThat(result.getCreatedAt()).isEqualTo(recipeComment.getCreatedAt());
    assertThat(result.getUpdatedAt()).isEqualTo(recipeComment.getUpdatedAt());
  }

  @Test
  void testToDtoWithNullEntity() {
    RecipeCommentDto result = recipeCommentMapper.toDto(null);

    assertThat(result).isNull();
  }

  @Test
  void testToDtoWithMinimalData() {
    RecipeComment minimal =
        RecipeComment.builder()
            .commentId(2L)
            .recipe(recipe)
            .userId(UUID.randomUUID())
            .commentText("Minimal comment")
            .build();

    RecipeCommentDto result = recipeCommentMapper.toDto(minimal);

    assertThat(result).isNotNull();
    assertThat(result.getCommentId()).isEqualTo(2L);
    assertThat(result.getRecipeId()).isEqualTo(1L);
    assertThat(result.getCommentText()).isEqualTo("Minimal comment");
    assertThat(result.getIsPublic()).isTrue(); // @Builder.Default sets this to true
    assertThat(result.getCreatedAt()).isNull();
    assertThat(result.getUpdatedAt()).isNull();
  }

  @Test
  void testToDtoList() {
    Recipe recipe2 = Recipe.builder().recipeId(2L).title("Another Recipe").build();

    RecipeComment comment2 =
        RecipeComment.builder()
            .commentId(2L)
            .recipe(recipe2)
            .userId(UUID.randomUUID())
            .commentText("Another comment")
            .isPublic(false)
            .build();

    List<RecipeComment> entities = Arrays.asList(recipeComment, comment2);
    List<RecipeCommentDto> result = recipeCommentMapper.toDtoList(entities);

    assertThat(result).hasSize(2);
    assertThat(result.get(0).getCommentId()).isEqualTo(1L);
    assertThat(result.get(0).getRecipeId()).isEqualTo(1L);
    assertThat(result.get(1).getCommentId()).isEqualTo(2L);
    assertThat(result.get(1).getRecipeId()).isEqualTo(2L);
  }

  @Test
  void testToDtoListWithEmptyList() {
    List<RecipeComment> entities = Arrays.asList();
    List<RecipeCommentDto> result = recipeCommentMapper.toDtoList(entities);

    assertThat(result).isEmpty();
  }

  @Test
  void testToDtoListWithNullList() {
    List<RecipeCommentDto> result = recipeCommentMapper.toDtoList(null);

    assertThat(result).isNull();
  }

  @Test
  void testToEntity() {
    RecipeComment result = recipeCommentMapper.toEntity(recipeCommentDto);

    assertThat(result).isNotNull();
    assertThat(result.getCommentId()).isEqualTo(recipeCommentDto.getCommentId());
    assertThat(result.getRecipe()).isNull(); // Ignored in mapping
    assertThat(result.getUserId()).isEqualTo(recipeCommentDto.getUserId());
    assertThat(result.getCommentText()).isEqualTo(recipeCommentDto.getCommentText());
    assertThat(result.getIsPublic()).isEqualTo(recipeCommentDto.getIsPublic());
    assertThat(result.getCreatedAt()).isEqualTo(recipeCommentDto.getCreatedAt());
    assertThat(result.getUpdatedAt()).isEqualTo(recipeCommentDto.getUpdatedAt());
  }

  @Test
  void testToEntityWithNullDto() {
    RecipeComment result = recipeCommentMapper.toEntity(null);

    assertThat(result).isNull();
  }

  @Test
  void testToEntityWithMinimalData() {
    RecipeCommentDto minimal =
        RecipeCommentDto.builder()
            .commentId(3L)
            .recipeId(3L)
            .userId(UUID.randomUUID())
            .commentText("Minimal DTO comment")
            .build();

    RecipeComment result = recipeCommentMapper.toEntity(minimal);

    assertThat(result).isNotNull();
    assertThat(result.getCommentId()).isEqualTo(3L);
    assertThat(result.getRecipe()).isNull(); // Ignored in mapping
    assertThat(result.getCommentText()).isEqualTo("Minimal DTO comment");
    assertThat(result.getIsPublic()).isNull();
    assertThat(result.getCreatedAt()).isNull();
    assertThat(result.getUpdatedAt()).isNull();
  }

  @Test
  void testMapperCircularConversion() {
    // Test entity -> dto -> entity conversion
    RecipeCommentDto dto = recipeCommentMapper.toDto(recipeComment);
    RecipeComment entity = recipeCommentMapper.toEntity(dto);

    assertThat(entity.getCommentId()).isEqualTo(recipeComment.getCommentId());
    assertThat(entity.getUserId()).isEqualTo(recipeComment.getUserId());
    assertThat(entity.getCommentText()).isEqualTo(recipeComment.getCommentText());
    assertThat(entity.getIsPublic()).isEqualTo(recipeComment.getIsPublic());
    assertThat(entity.getCreatedAt()).isEqualTo(recipeComment.getCreatedAt());
    assertThat(entity.getUpdatedAt()).isEqualTo(recipeComment.getUpdatedAt());
    // Note: recipe relationship is not preserved due to @Mapping(target = "recipe", ignore = true)
  }
}
