package com.recipe_manager.model.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.recipe_manager.model.dto.recipe.RecipeFavoriteDto;
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.entity.recipe.RecipeFavorite;
import com.recipe_manager.model.entity.recipe.RecipeFavoriteId;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

/**
 * Unit tests for RecipeFavoriteMapper.
 */
@Tag("unit")
class RecipeFavoriteMapperTest {

  private final RecipeFavoriteMapper mapper = Mappers.getMapper(RecipeFavoriteMapper.class);

  @Test
  @Tag("standard-processing")
  @DisplayName("Should map RecipeFavorite entity to RecipeFavoriteDto")
  void shouldMapEntityToDto() {
    UUID userId = UUID.randomUUID();
    LocalDateTime favoritedAt = LocalDateTime.now();

    Recipe recipe = Recipe.builder()
        .recipeId(100L)
        .build();

    RecipeFavoriteId id = RecipeFavoriteId.builder()
        .userId(userId)
        .recipeId(recipe.getRecipeId())
        .build();

    RecipeFavorite entity = RecipeFavorite.builder()
        .id(id)
        .recipe(recipe)
        .favoritedAt(favoritedAt)
        .build();

    RecipeFavoriteDto result = mapper.toDto(entity);

    assertThat(result).isNotNull();
    assertThat(result.getRecipeId()).isEqualTo(100L);
    assertThat(result.getUserId()).isEqualTo(userId);
    assertThat(result.getFavoritedAt()).isEqualTo(favoritedAt);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should map a list of RecipeFavorite entities to RecipeFavoriteDto list")
  void shouldMapEntityListToDto() {
    UUID userId1 = UUID.randomUUID();
    UUID userId2 = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    Recipe recipe1 = Recipe.builder()
        .recipeId(200L)
        .build();

    Recipe recipe2 = Recipe.builder()
        .recipeId(300L)
        .build();

    RecipeFavoriteId id1 = RecipeFavoriteId.builder()
        .userId(userId1)
        .recipeId(recipe1.getRecipeId())
        .build();

    RecipeFavoriteId id2 = RecipeFavoriteId.builder()
        .userId(userId2)
        .recipeId(recipe2.getRecipeId())
        .build();

    RecipeFavorite entity1 = RecipeFavorite.builder()
        .id(id1)
        .recipe(recipe1)
        .favoritedAt(now)
        .build();

    RecipeFavorite entity2 = RecipeFavorite.builder()
        .id(id2)
        .recipe(recipe2)
        .favoritedAt(now.plusHours(1))
        .build();

    List<RecipeFavoriteDto> results = mapper.toDtoList(List.of(entity1, entity2));

    assertThat(results).hasSize(2);
    assertThat(results.get(0).getRecipeId()).isEqualTo(200L);
    assertThat(results.get(0).getUserId()).isEqualTo(userId1);
    assertThat(results.get(0).getFavoritedAt()).isEqualTo(now);
    assertThat(results.get(1).getRecipeId()).isEqualTo(300L);
    assertThat(results.get(1).getUserId()).isEqualTo(userId2);
    assertThat(results.get(1).getFavoritedAt()).isEqualTo(now.plusHours(1));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should handle null recipe in RecipeFavorite entity")
  void shouldHandleNullRecipe() {
    UUID userId = UUID.randomUUID();
    LocalDateTime favoritedAt = LocalDateTime.now();

    RecipeFavoriteId id = RecipeFavoriteId.builder()
        .userId(userId)
        .recipeId(null) // null recipe ID
        .build();

    RecipeFavorite entity = RecipeFavorite.builder()
        .id(id)
        .recipe(null)
        .favoritedAt(favoritedAt)
        .build();

    RecipeFavoriteDto result = mapper.toDto(entity);

    assertThat(result).isNotNull();
    assertThat(result.getRecipeId()).isNull();
    assertThat(result.getUserId()).isEqualTo(userId);
    assertThat(result.getFavoritedAt()).isEqualTo(favoritedAt);
  }

  @Test
  @DisplayName("Should handle null RecipeFavorite entity")
  void shouldHandleNullRecipeFavoriteEntity() {
    RecipeFavoriteDto result = mapper.toDto(null);
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("Should handle RecipeFavorite with null id")
  void shouldHandleRecipeFavoriteWithNullId() {
    LocalDateTime favoritedAt = LocalDateTime.now();
    Recipe recipe = Recipe.builder()
        .recipeId(123L)
        .build();
    RecipeFavorite favoriteWithNullId = RecipeFavorite.builder()
        .id(null)
        .recipe(recipe)
        .favoritedAt(favoritedAt)
        .build();
    RecipeFavoriteDto result = mapper.toDto(favoriteWithNullId);
    assertThat(result).isNotNull();
    assertThat(result.getRecipeId()).isEqualTo(123L);
    assertThat(result.getUserId()).isNull();
    assertThat(result.getFavoritedAt()).isEqualTo(favoritedAt);
  }

  @Test
  @DisplayName("Should handle null list")
  void shouldHandleNullList() {
    List<RecipeFavoriteDto> result = mapper.toDtoList(null);
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("Should handle empty list")
  void shouldHandleEmptyList() {
    List<RecipeFavoriteDto> result = mapper.toDtoList(List.of());
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("Should handle RecipeFavorite with null favoritedAt")
  void shouldHandleRecipeFavoriteWithNullFavoritedAt() {
    UUID userId = UUID.randomUUID();
    Recipe recipe = Recipe.builder()
        .recipeId(123L)
        .build();
    RecipeFavoriteId id = RecipeFavoriteId.builder()
        .userId(userId)
        .recipeId(123L)
        .build();
    RecipeFavorite favoriteWithNullDate = RecipeFavorite.builder()
        .id(id)
        .recipe(recipe)
        .favoritedAt(null)
        .build();
    RecipeFavoriteDto result = mapper.toDto(favoriteWithNullDate);
    assertThat(result).isNotNull();
    assertThat(result.getRecipeId()).isEqualTo(123L);
    assertThat(result.getUserId()).isEqualTo(userId);
    assertThat(result.getFavoritedAt()).isNull();
  }
}
