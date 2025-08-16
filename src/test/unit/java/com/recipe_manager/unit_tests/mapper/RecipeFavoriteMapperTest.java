package com.recipe_manager.unit_tests.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.recipe_manager.model.dto.recipe.RecipeFavoriteDto;
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.entity.recipe.RecipeFavorite;
import com.recipe_manager.model.entity.recipe.RecipeFavoriteId;
import com.recipe_manager.model.mapper.RecipeFavoriteMapper;

@org.junit.jupiter.api.Tag("unit")
class RecipeFavoriteMapperTest {

  private final RecipeFavoriteMapper recipeFavoriteMapper = Mappers.getMapper(RecipeFavoriteMapper.class);

  private RecipeFavorite recipeFavorite;
  private Recipe recipe;
  private UUID userId;
  private Long recipeId;
  private LocalDateTime favoritedAt;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    recipeId = 123L;
    favoritedAt = LocalDateTime.now();

    recipe = Recipe.builder()
        .recipeId(recipeId)
        .build();

    RecipeFavoriteId id = RecipeFavoriteId.builder()
        .userId(userId)
        .recipeId(recipeId)
        .build();

    recipeFavorite = RecipeFavorite.builder()
        .id(id)
        .recipe(recipe)
        .favoritedAt(favoritedAt)
        .build();
  }

  @Test
  @DisplayName("Should map RecipeFavorite entity to RecipeFavoriteDto")
  void shouldMapRecipeFavoriteEntityToDto() {
    // Act
    RecipeFavoriteDto result = recipeFavoriteMapper.toDto(recipeFavorite);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getRecipeId()).isEqualTo(recipeId);
    assertThat(result.getUserId()).isEqualTo(userId);
    assertThat(result.getFavoritedAt()).isEqualTo(favoritedAt);
  }

  @Test
  @DisplayName("Should handle null RecipeFavorite entity")
  void shouldHandleNullRecipeFavoriteEntity() {
    // Act
    RecipeFavoriteDto result = recipeFavoriteMapper.toDto(null);

    // Assert
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("Should handle RecipeFavorite with null recipe")
  void shouldHandleRecipeFavoriteWithNullRecipe() {
    // Arrange
    RecipeFavorite favoriteWithNullRecipe = RecipeFavorite.builder()
        .id(RecipeFavoriteId.builder().userId(userId).recipeId(recipeId).build())
        .recipe(null)
        .favoritedAt(favoritedAt)
        .build();

    // Act
    RecipeFavoriteDto result = recipeFavoriteMapper.toDto(favoriteWithNullRecipe);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getRecipeId()).isNull();
    assertThat(result.getUserId()).isEqualTo(userId);
    assertThat(result.getFavoritedAt()).isEqualTo(favoritedAt);
  }

  @Test
  @DisplayName("Should handle RecipeFavorite with null id")
  void shouldHandleRecipeFavoriteWithNullId() {
    // Arrange
    RecipeFavorite favoriteWithNullId = RecipeFavorite.builder()
        .id(null)
        .recipe(recipe)
        .favoritedAt(favoritedAt)
        .build();

    // Act
    RecipeFavoriteDto result = recipeFavoriteMapper.toDto(favoriteWithNullId);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getRecipeId()).isEqualTo(recipeId);
    assertThat(result.getUserId()).isNull();
    assertThat(result.getFavoritedAt()).isEqualTo(favoritedAt);
  }

  @Test
  @DisplayName("Should map list of RecipeFavorite entities to list of DTOs")
  void shouldMapListOfRecipeFavoriteEntitiesToDtoList() {
    // Arrange
    UUID userId2 = UUID.randomUUID();
    Long recipeId2 = 456L;
    LocalDateTime favoritedAt2 = LocalDateTime.now().minusDays(1);

    Recipe recipe2 = Recipe.builder()
        .recipeId(recipeId2)
        .build();

    RecipeFavoriteId id2 = RecipeFavoriteId.builder()
        .userId(userId2)
        .recipeId(recipeId2)
        .build();

    RecipeFavorite recipeFavorite2 = RecipeFavorite.builder()
        .id(id2)
        .recipe(recipe2)
        .favoritedAt(favoritedAt2)
        .build();

    List<RecipeFavorite> favorites = Arrays.asList(recipeFavorite, recipeFavorite2);

    // Act
    List<RecipeFavoriteDto> result = recipeFavoriteMapper.toDtoList(favorites);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);

    RecipeFavoriteDto dto1 = result.get(0);
    assertThat(dto1.getRecipeId()).isEqualTo(recipeId);
    assertThat(dto1.getUserId()).isEqualTo(userId);
    assertThat(dto1.getFavoritedAt()).isEqualTo(favoritedAt);

    RecipeFavoriteDto dto2 = result.get(1);
    assertThat(dto2.getRecipeId()).isEqualTo(recipeId2);
    assertThat(dto2.getUserId()).isEqualTo(userId2);
    assertThat(dto2.getFavoritedAt()).isEqualTo(favoritedAt2);
  }

  @Test
  @DisplayName("Should handle null list")
  void shouldHandleNullList() {
    // Act
    List<RecipeFavoriteDto> result = recipeFavoriteMapper.toDtoList(null);

    // Assert
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("Should handle empty list")
  void shouldHandleEmptyList() {
    // Act
    List<RecipeFavoriteDto> result = recipeFavoriteMapper.toDtoList(Arrays.asList());

    // Assert
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("Should handle list with null elements")
  void shouldHandleListWithNullElements() {
    // Arrange
    List<RecipeFavorite> favoritesWithNull = Arrays.asList(recipeFavorite, null);

    // Act
    List<RecipeFavoriteDto> result = recipeFavoriteMapper.toDtoList(favoritesWithNull);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);
    assertThat(result.get(0)).isNotNull();
    assertThat(result.get(0).getRecipeId()).isEqualTo(recipeId);
    assertThat(result.get(1)).isNull();
  }

  @Test
  @DisplayName("Should handle RecipeFavorite with null favoritedAt")
  void shouldHandleRecipeFavoriteWithNullFavoritedAt() {
    // Arrange
    RecipeFavorite favoriteWithNullDate = RecipeFavorite.builder()
        .id(RecipeFavoriteId.builder().userId(userId).recipeId(recipeId).build())
        .recipe(recipe)
        .favoritedAt(null)
        .build();

    // Act
    RecipeFavoriteDto result = recipeFavoriteMapper.toDto(favoriteWithNullDate);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getRecipeId()).isEqualTo(recipeId);
    assertThat(result.getUserId()).isEqualTo(userId);
    assertThat(result.getFavoritedAt()).isNull();
  }
}
