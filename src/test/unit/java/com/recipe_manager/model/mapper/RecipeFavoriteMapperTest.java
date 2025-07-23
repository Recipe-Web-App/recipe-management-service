package com.recipe_manager.model.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.recipe_manager.model.dto.recipe.RecipeFavoriteDto;
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.entity.recipe.RecipeFavorite;

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

    RecipeFavorite entity = RecipeFavorite.builder()
        .recipe(recipe)
        .userId(userId)
        .favoritedAt(favoritedAt)
        .build();

    RecipeFavoriteDto result = mapper.toDto(entity);

    assertThat(result).isNotNull();
    assertThat(result.getRecipeId()).isEqualTo(100L);
    assertThat(result.getUserId()).isEqualTo(userId);
    assertThat(result.getFavoritedAt()).isEqualTo(favoritedAt);
    // Ignored fields should be null or default values
    assertThat(result.getCreatedAt()).isNull();
    assertThat(result.getUpdatedAt()).isNull();
    assertThat(result.getMedia()).isNotNull().isEmpty(); // MapStruct returns empty list for @Default fields
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

    RecipeFavorite entity1 = RecipeFavorite.builder()
        .recipe(recipe1)
        .userId(userId1)
        .favoritedAt(now)
        .build();

    RecipeFavorite entity2 = RecipeFavorite.builder()
        .recipe(recipe2)
        .userId(userId2)
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

    RecipeFavorite entity = RecipeFavorite.builder()
        .recipe(null)
        .userId(userId)
        .favoritedAt(favoritedAt)
        .build();

    RecipeFavoriteDto result = mapper.toDto(entity);

    assertThat(result).isNotNull();
    assertThat(result.getRecipeId()).isNull();
    assertThat(result.getUserId()).isEqualTo(userId);
    assertThat(result.getFavoritedAt()).isEqualTo(favoritedAt);
  }
}
