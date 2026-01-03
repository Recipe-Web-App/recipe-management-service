package com.recipe_manager.model.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.recipe_manager.model.dto.collection.CollectionFavoriteDto;
import com.recipe_manager.model.entity.collection.CollectionFavorite;
import com.recipe_manager.model.entity.collection.CollectionFavoriteId;
import com.recipe_manager.model.entity.collection.RecipeCollection;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

/** Unit tests for CollectionFavoriteMapper. */
@Tag("unit")
class CollectionFavoriteMapperTest {

  private final CollectionFavoriteMapper mapper = Mappers.getMapper(CollectionFavoriteMapper.class);

  @Test
  @Tag("standard-processing")
  @DisplayName("Should map CollectionFavorite entity to CollectionFavoriteDto")
  void shouldMapEntityToDto() {
    UUID userId = UUID.randomUUID();
    LocalDateTime favoritedAt = LocalDateTime.now();

    RecipeCollection collection = RecipeCollection.builder().collectionId(100L).build();

    CollectionFavoriteId id =
        CollectionFavoriteId.builder()
            .userId(userId)
            .collectionId(collection.getCollectionId())
            .build();

    CollectionFavorite entity =
        CollectionFavorite.builder().id(id).collection(collection).favoritedAt(favoritedAt).build();

    CollectionFavoriteDto result = mapper.toDto(entity);

    assertThat(result).isNotNull();
    assertThat(result.getCollectionId()).isEqualTo(100L);
    assertThat(result.getUserId()).isEqualTo(userId);
    assertThat(result.getFavoritedAt()).isEqualTo(favoritedAt);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should map a list of CollectionFavorite entities to CollectionFavoriteDto list")
  void shouldMapEntityListToDto() {
    UUID userId1 = UUID.randomUUID();
    UUID userId2 = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    RecipeCollection collection1 = RecipeCollection.builder().collectionId(200L).build();

    RecipeCollection collection2 = RecipeCollection.builder().collectionId(300L).build();

    CollectionFavoriteId id1 =
        CollectionFavoriteId.builder()
            .userId(userId1)
            .collectionId(collection1.getCollectionId())
            .build();

    CollectionFavoriteId id2 =
        CollectionFavoriteId.builder()
            .userId(userId2)
            .collectionId(collection2.getCollectionId())
            .build();

    CollectionFavorite entity1 =
        CollectionFavorite.builder().id(id1).collection(collection1).favoritedAt(now).build();

    CollectionFavorite entity2 =
        CollectionFavorite.builder()
            .id(id2)
            .collection(collection2)
            .favoritedAt(now.plusHours(1))
            .build();

    List<CollectionFavoriteDto> results = mapper.toDtoList(List.of(entity1, entity2));

    assertThat(results).hasSize(2);
    assertThat(results.get(0).getCollectionId()).isEqualTo(200L);
    assertThat(results.get(0).getUserId()).isEqualTo(userId1);
    assertThat(results.get(0).getFavoritedAt()).isEqualTo(now);
    assertThat(results.get(1).getCollectionId()).isEqualTo(300L);
    assertThat(results.get(1).getUserId()).isEqualTo(userId2);
    assertThat(results.get(1).getFavoritedAt()).isEqualTo(now.plusHours(1));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should handle null collection in CollectionFavorite entity")
  void shouldHandleNullCollection() {
    UUID userId = UUID.randomUUID();
    LocalDateTime favoritedAt = LocalDateTime.now();

    CollectionFavoriteId id =
        CollectionFavoriteId.builder()
            .userId(userId)
            .collectionId(null) // null collection ID
            .build();

    CollectionFavorite entity =
        CollectionFavorite.builder().id(id).collection(null).favoritedAt(favoritedAt).build();

    CollectionFavoriteDto result = mapper.toDto(entity);

    assertThat(result).isNotNull();
    assertThat(result.getCollectionId()).isNull();
    assertThat(result.getUserId()).isEqualTo(userId);
    assertThat(result.getFavoritedAt()).isEqualTo(favoritedAt);
  }

  @Test
  @DisplayName("Should handle null CollectionFavorite entity")
  void shouldHandleNullCollectionFavoriteEntity() {
    CollectionFavoriteDto result = mapper.toDto(null);
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("Should handle CollectionFavorite with null id")
  void shouldHandleCollectionFavoriteWithNullId() {
    LocalDateTime favoritedAt = LocalDateTime.now();
    RecipeCollection collection = RecipeCollection.builder().collectionId(123L).build();
    CollectionFavorite favoriteWithNullId =
        CollectionFavorite.builder().id(null).collection(collection).favoritedAt(favoritedAt).build();
    CollectionFavoriteDto result = mapper.toDto(favoriteWithNullId);
    assertThat(result).isNotNull();
    assertThat(result.getCollectionId()).isEqualTo(123L);
    assertThat(result.getUserId()).isNull();
    assertThat(result.getFavoritedAt()).isEqualTo(favoritedAt);
  }

  @Test
  @DisplayName("Should handle null list")
  void shouldHandleNullList() {
    List<CollectionFavoriteDto> result = mapper.toDtoList(null);
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("Should handle empty list")
  void shouldHandleEmptyList() {
    List<CollectionFavoriteDto> result = mapper.toDtoList(List.of());
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("Should handle CollectionFavorite with null favoritedAt")
  void shouldHandleCollectionFavoriteWithNullFavoritedAt() {
    UUID userId = UUID.randomUUID();
    RecipeCollection collection = RecipeCollection.builder().collectionId(123L).build();
    CollectionFavoriteId id =
        CollectionFavoriteId.builder().userId(userId).collectionId(123L).build();
    CollectionFavorite favoriteWithNullDate =
        CollectionFavorite.builder().id(id).collection(collection).favoritedAt(null).build();
    CollectionFavoriteDto result = mapper.toDto(favoriteWithNullDate);
    assertThat(result).isNotNull();
    assertThat(result.getCollectionId()).isEqualTo(123L);
    assertThat(result.getUserId()).isEqualTo(userId);
    assertThat(result.getFavoritedAt()).isNull();
  }
}
