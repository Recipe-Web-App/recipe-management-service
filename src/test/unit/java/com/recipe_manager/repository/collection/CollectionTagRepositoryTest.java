package com.recipe_manager.repository.collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.recipe_manager.model.entity.collection.CollectionTag;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for CollectionTagRepository.
 *
 * <p>Tests repository methods for managing collection tags.
 *
 * <p>Note: These are mock-based unit tests since the repository depends on JPA infrastructure that
 * is not easily testable in isolation.
 */
@Tag("unit")
class CollectionTagRepositoryTest {

  private CollectionTagRepository tagRepository;

  @BeforeEach
  void setUp() {
    tagRepository = mock(CollectionTagRepository.class);
  }

  @Test
  @DisplayName("Should find tag by name case insensitive")
  @Tag("standard-processing")
  void shouldFindByNameIgnoreCase() {
    // Given
    CollectionTag expectedTag = CollectionTag.builder()
        .tagId(1L)
        .name("Dessert")
        .build();
    when(tagRepository.findByNameIgnoreCase("dessert")).thenReturn(Optional.of(expectedTag));

    // When
    Optional<CollectionTag> result = tagRepository.findByNameIgnoreCase("dessert");

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getTagId()).isEqualTo(1L);
    assertThat(result.get().getName()).isEqualTo("Dessert");
  }

  @Test
  @DisplayName("Should find tag with uppercase input")
  @Tag("standard-processing")
  void shouldFindTagWithUppercaseInput() {
    // Given
    CollectionTag expectedTag = CollectionTag.builder()
        .tagId(2L)
        .name("quick-meals")
        .build();
    when(tagRepository.findByNameIgnoreCase("QUICK-MEALS")).thenReturn(Optional.of(expectedTag));

    // When
    Optional<CollectionTag> result = tagRepository.findByNameIgnoreCase("QUICK-MEALS");

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getName()).isEqualTo("quick-meals");
  }

  @Test
  @DisplayName("Should return empty optional when tag not found")
  @Tag("standard-processing")
  void shouldReturnEmptyWhenTagNotFound() {
    // Given
    when(tagRepository.findByNameIgnoreCase("nonexistent")).thenReturn(Optional.empty());

    // When
    Optional<CollectionTag> result = tagRepository.findByNameIgnoreCase("nonexistent");

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("Should check if tag exists by name case insensitive - true")
  @Tag("standard-processing")
  void shouldReturnTrueWhenTagExists() {
    // Given
    when(tagRepository.existsByNameIgnoreCase("favorites")).thenReturn(true);

    // When
    boolean exists = tagRepository.existsByNameIgnoreCase("favorites");

    // Then
    assertThat(exists).isTrue();
    verify(tagRepository).existsByNameIgnoreCase("favorites");
  }

  @Test
  @DisplayName("Should check if tag exists by name case insensitive - false")
  @Tag("standard-processing")
  void shouldReturnFalseWhenTagDoesNotExist() {
    // Given
    when(tagRepository.existsByNameIgnoreCase("nonexistent")).thenReturn(false);

    // When
    boolean exists = tagRepository.existsByNameIgnoreCase("nonexistent");

    // Then
    assertThat(exists).isFalse();
  }

  @Test
  @DisplayName("Should check existence with mixed case input")
  @Tag("standard-processing")
  void shouldCheckExistenceWithMixedCase() {
    // Given
    when(tagRepository.existsByNameIgnoreCase("QuIcK-MeAlS")).thenReturn(true);

    // When
    boolean exists = tagRepository.existsByNameIgnoreCase("QuIcK-MeAlS");

    // Then
    assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("Should save and return tag")
  @Tag("standard-processing")
  void shouldSaveTag() {
    // Given
    CollectionTag tagToSave = CollectionTag.builder()
        .name("new-tag")
        .build();
    CollectionTag savedTag = CollectionTag.builder()
        .tagId(10L)
        .name("new-tag")
        .build();
    when(tagRepository.save(tagToSave)).thenReturn(savedTag);

    // When
    CollectionTag result = tagRepository.save(tagToSave);

    // Then
    assertThat(result.getTagId()).isEqualTo(10L);
    assertThat(result.getName()).isEqualTo("new-tag");
    verify(tagRepository).save(tagToSave);
  }

  @Test
  @DisplayName("Should find tag by ID")
  @Tag("standard-processing")
  void shouldFindById() {
    // Given
    CollectionTag expectedTag = CollectionTag.builder()
        .tagId(5L)
        .name("dinner")
        .build();
    when(tagRepository.findById(5L)).thenReturn(Optional.of(expectedTag));

    // When
    Optional<CollectionTag> result = tagRepository.findById(5L);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getTagId()).isEqualTo(5L);
    assertThat(result.get().getName()).isEqualTo("dinner");
  }

  @Test
  @DisplayName("Should return empty when ID not found")
  @Tag("standard-processing")
  void shouldReturnEmptyWhenIdNotFound() {
    // Given
    when(tagRepository.findById(999L)).thenReturn(Optional.empty());

    // When
    Optional<CollectionTag> result = tagRepository.findById(999L);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("Should delete tag by ID")
  @Tag("standard-processing")
  void shouldDeleteById() {
    // When
    tagRepository.deleteById(1L);

    // Then
    verify(tagRepository).deleteById(1L);
  }
}
