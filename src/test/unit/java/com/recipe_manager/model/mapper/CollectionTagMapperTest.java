package com.recipe_manager.model.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.recipe_manager.model.dto.collection.CollectionTagDto;
import com.recipe_manager.model.entity.collection.CollectionTag;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

/**
 * Unit tests for CollectionTagMapper.
 */
@Tag("unit")
class CollectionTagMapperTest {

  private final CollectionTagMapper mapper = Mappers.getMapper(CollectionTagMapper.class);

  @Test
  @Tag("standard-processing")
  @DisplayName("Should map CollectionTag entity to CollectionTagDto")
  void shouldMapEntityToDto() {
    // Given
    CollectionTag entity = CollectionTag.builder()
        .tagId(1L)
        .name("dessert")
        .build();

    // When
    CollectionTagDto result = mapper.toDto(entity);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getTagId()).isEqualTo(1L);
    assertThat(result.getName()).isEqualTo("dessert");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle null entity")
  void shouldHandleNullEntity() {
    // When
    CollectionTagDto result = mapper.toDto(null);

    // Then
    assertThat(result).isNull();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should map entity with null tagId")
  void shouldMapEntityWithNullTagId() {
    // Given
    CollectionTag entity = CollectionTag.builder()
        .tagId(null)
        .name("quick-meals")
        .build();

    // When
    CollectionTagDto result = mapper.toDto(entity);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getTagId()).isNull();
    assertThat(result.getName()).isEqualTo("quick-meals");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should map entity with null name")
  void shouldMapEntityWithNullName() {
    // Given
    CollectionTag entity = CollectionTag.builder()
        .tagId(2L)
        .name(null)
        .build();

    // When
    CollectionTagDto result = mapper.toDto(entity);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getTagId()).isEqualTo(2L);
    assertThat(result.getName()).isNull();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should map list of entities to list of DTOs")
  void shouldMapEntityListToDtoList() {
    // Given
    CollectionTag entity1 = CollectionTag.builder()
        .tagId(1L)
        .name("dinner")
        .build();
    CollectionTag entity2 = CollectionTag.builder()
        .tagId(2L)
        .name("breakfast")
        .build();
    List<CollectionTag> entities = Arrays.asList(entity1, entity2);

    // When
    List<CollectionTagDto> results = mapper.toDtoList(entities);

    // Then
    assertThat(results).isNotNull();
    assertThat(results).hasSize(2);
    assertThat(results.get(0).getTagId()).isEqualTo(1L);
    assertThat(results.get(0).getName()).isEqualTo("dinner");
    assertThat(results.get(1).getTagId()).isEqualTo(2L);
    assertThat(results.get(1).getName()).isEqualTo("breakfast");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle null list")
  void shouldHandleNullList() {
    // When
    List<CollectionTagDto> result = mapper.toDtoList(null);

    // Then
    assertThat(result).isNull();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle empty list")
  void shouldHandleEmptyList() {
    // When
    List<CollectionTagDto> result = mapper.toDtoList(Collections.emptyList());

    // Then
    assertThat(result).isNotNull().isEmpty();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle list with null elements")
  void shouldHandleListWithNullElements() {
    // Given
    CollectionTag entity = CollectionTag.builder()
        .tagId(1L)
        .name("lunch")
        .build();
    List<CollectionTag> entitiesWithNull = Arrays.asList(entity, null);

    // When
    List<CollectionTagDto> result = mapper.toDtoList(entitiesWithNull);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);
    assertThat(result.get(0)).isNotNull();
    assertThat(result.get(0).getTagId()).isEqualTo(1L);
    assertThat(result.get(1)).isNull();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should map entity with all null fields")
  void shouldMapEntityWithAllNullFields() {
    // Given
    CollectionTag entity = CollectionTag.builder()
        .tagId(null)
        .name(null)
        .build();

    // When
    CollectionTagDto result = mapper.toDto(entity);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getTagId()).isNull();
    assertThat(result.getName()).isNull();
  }
}
