package com.recipe_manager.model.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.recipe_manager.model.dto.ingredient.IngredientCommentDto;
import com.recipe_manager.model.entity.ingredient.Ingredient;
import com.recipe_manager.model.entity.ingredient.IngredientComment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

@Tag("unit")
class IngredientCommentMapperTest {

  private IngredientCommentMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = Mappers.getMapper(IngredientCommentMapper.class);
  }

  @Test
  @DisplayName("Should convert IngredientComment entity to DTO")
  @Tag("standard-processing")
  void shouldConvertEntityToDto() {
    // Given
    LocalDateTime now = LocalDateTime.now();
    UUID userId = UUID.randomUUID();

    Ingredient ingredient = Ingredient.builder()
        .ingredientId(123L)
        .name("Salt")
        .build();

    IngredientComment entity = IngredientComment.builder()
        .commentId(1L)
        .ingredient(ingredient)
        .recipeId(456L)
        .userId(userId)
        .commentText("Great ingredient!")
        .isPublic(true)
        .createdAt(now)
        .updatedAt(now)
        .build();

    // When
    IngredientCommentDto dto = mapper.toDto(entity);

    // Then
    assertThat(dto).isNotNull();
    assertThat(dto.getCommentId()).isEqualTo(1L);
    assertThat(dto.getRecipeId()).isEqualTo(456L);
    assertThat(dto.getUserId()).isEqualTo(userId);
    assertThat(dto.getCommentText()).isEqualTo("Great ingredient!");
    assertThat(dto.getIsPublic()).isTrue();
    assertThat(dto.getCreatedAt()).isEqualTo(now);
    assertThat(dto.getUpdatedAt()).isEqualTo(now);
  }

  @Test
  @DisplayName("Should convert IngredientCommentDto to entity")
  @Tag("standard-processing")
  void shouldConvertDtoToEntity() {
    // Given
    LocalDateTime now = LocalDateTime.now();
    UUID userId = UUID.randomUUID();

    IngredientCommentDto dto = IngredientCommentDto.builder()
        .commentId(1L)
        .recipeId(456L)
        .userId(userId)
        .commentText("Great ingredient!")
        .isPublic(true)
        .createdAt(now)
        .updatedAt(now)
        .build();

    // When
    IngredientComment entity = mapper.toEntity(dto);

    // Then
    assertThat(entity).isNotNull();
    assertThat(entity.getCommentId()).isEqualTo(1L);
    assertThat(entity.getRecipeId()).isEqualTo(456L);
    assertThat(entity.getUserId()).isEqualTo(userId);
    assertThat(entity.getCommentText()).isEqualTo("Great ingredient!");
    assertThat(entity.getIsPublic()).isTrue();
    assertThat(entity.getCreatedAt()).isEqualTo(now);
    assertThat(entity.getUpdatedAt()).isEqualTo(now);
  }

  @Test
  @DisplayName("Should convert list of entities to DTOs")
  @Tag("standard-processing")
  void shouldConvertEntityListToDtoList() {
    // Given
    LocalDateTime now = LocalDateTime.now();
    UUID userId1 = UUID.randomUUID();
    UUID userId2 = UUID.randomUUID();

    Ingredient ingredient = Ingredient.builder()
        .ingredientId(123L)
        .name("Salt")
        .build();

    IngredientComment entity1 = IngredientComment.builder()
        .commentId(1L)
        .ingredient(ingredient)
        .recipeId(456L)
        .userId(userId1)
        .commentText("First comment")
        .isPublic(true)
        .createdAt(now)
        .updatedAt(now)
        .build();

    IngredientComment entity2 = IngredientComment.builder()
        .commentId(2L)
        .ingredient(ingredient)
        .recipeId(456L)
        .userId(userId2)
        .commentText("Second comment")
        .isPublic(false)
        .createdAt(now.plusMinutes(1))
        .updatedAt(now.plusMinutes(1))
        .build();

    List<IngredientComment> entities = Arrays.asList(entity1, entity2);

    // When
    List<IngredientCommentDto> dtos = mapper.toDtoList(entities);

    // Then
    assertThat(dtos).isNotNull();
    assertThat(dtos).hasSize(2);

    assertThat(dtos.get(0).getCommentId()).isEqualTo(1L);
    assertThat(dtos.get(0).getCommentText()).isEqualTo("First comment");
    assertThat(dtos.get(0).getIsPublic()).isTrue();
    assertThat(dtos.get(0).getUserId()).isEqualTo(userId1);

    assertThat(dtos.get(1).getCommentId()).isEqualTo(2L);
    assertThat(dtos.get(1).getCommentText()).isEqualTo("Second comment");
    assertThat(dtos.get(1).getIsPublic()).isFalse();
    assertThat(dtos.get(1).getUserId()).isEqualTo(userId2);
  }

  @Test
  @DisplayName("Should handle null entity gracefully")
  @Tag("error-processing")
  void shouldHandleNullEntityGracefully() {
    // When
    IngredientCommentDto dto = mapper.toDto(null);

    // Then
    assertThat(dto).isNull();
  }

  @Test
  @DisplayName("Should handle null DTO gracefully")
  @Tag("error-processing")
  void shouldHandleNullDtoGracefully() {
    // When
    IngredientComment entity = mapper.toEntity(null);

    // Then
    assertThat(entity).isNull();
  }

  @Test
  @DisplayName("Should handle empty list gracefully")
  @Tag("standard-processing")
  void shouldHandleEmptyListGracefully() {
    // When
    List<IngredientCommentDto> dtos = mapper.toDtoList(Collections.emptyList());

    // Then
    assertThat(dtos).isNotNull();
    assertThat(dtos).isEmpty();
  }

  @Test
  @DisplayName("Should handle null list gracefully")
  @Tag("error-processing")
  void shouldHandleNullListGracefully() {
    // When
    List<IngredientCommentDto> dtos = mapper.toDtoList(null);

    // Then
    assertThat(dtos).isNull();
  }

  @Test
  @DisplayName("Should handle entity with null fields")
  @Tag("error-processing")
  void shouldHandleEntityWithNullFields() {
    // Given
    IngredientComment entity = IngredientComment.builder()
        .commentId(null)
        .ingredient(null)
        .recipeId(null)
        .userId(null)
        .commentText(null)
        .isPublic(null)
        .createdAt(null)
        .updatedAt(null)
        .build();

    // When
    IngredientCommentDto dto = mapper.toDto(entity);

    // Then
    assertThat(dto).isNotNull();
    assertThat(dto.getCommentId()).isNull();
    assertThat(dto.getRecipeId()).isNull();
    assertThat(dto.getUserId()).isNull();
    assertThat(dto.getCommentText()).isNull();
    assertThat(dto.getIsPublic()).isNull();
    assertThat(dto.getCreatedAt()).isNull();
    assertThat(dto.getUpdatedAt()).isNull();
  }

  @Test
  @DisplayName("Should handle DTO with null fields")
  @Tag("error-processing")
  void shouldHandleDtoWithNullFields() {
    // Given
    IngredientCommentDto dto = IngredientCommentDto.builder()
        .commentId(null)
        .recipeId(null)
        .userId(null)
        .commentText(null)
        .isPublic(null)
        .createdAt(null)
        .updatedAt(null)
        .build();

    // When
    IngredientComment entity = mapper.toEntity(dto);

    // Then
    assertThat(entity).isNotNull();
    assertThat(entity.getCommentId()).isNull();
    assertThat(entity.getRecipeId()).isNull();
    assertThat(entity.getUserId()).isNull();
    assertThat(entity.getCommentText()).isNull();
    assertThat(entity.getIsPublic()).isNull();
    assertThat(entity.getCreatedAt()).isNull();
    assertThat(entity.getUpdatedAt()).isNull();
  }

  @Test
  @DisplayName("Should preserve all data types correctly")
  @Tag("standard-processing")
  void shouldPreserveAllDataTypesCorrectly() {
    // Given
    LocalDateTime specificTime = LocalDateTime.of(2023, 12, 25, 10, 30, 45);
    UUID specificUuid = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

    Ingredient ingredient = Ingredient.builder()
        .ingredientId(Long.MAX_VALUE)
        .name("Test Ingredient")
        .build();

    IngredientComment entity = IngredientComment.builder()
        .commentId(Long.MAX_VALUE)
        .ingredient(ingredient)
        .recipeId(Long.MIN_VALUE)
        .userId(specificUuid)
        .commentText("Special characters: àáâãäåæçèéêë")
        .isPublic(false)
        .createdAt(specificTime)
        .updatedAt(specificTime.plusHours(2))
        .build();

    // When
    IngredientCommentDto dto = mapper.toDto(entity);

    // Then
    assertThat(dto.getCommentId()).isEqualTo(Long.MAX_VALUE);
    assertThat(dto.getRecipeId()).isEqualTo(Long.MIN_VALUE);
    assertThat(dto.getUserId()).isEqualTo(specificUuid);
    assertThat(dto.getCommentText()).isEqualTo("Special characters: àáâãäåæçèéêë");
    assertThat(dto.getIsPublic()).isFalse();
    assertThat(dto.getCreatedAt()).isEqualTo(specificTime);
    assertThat(dto.getUpdatedAt()).isEqualTo(specificTime.plusHours(2));
  }
}
