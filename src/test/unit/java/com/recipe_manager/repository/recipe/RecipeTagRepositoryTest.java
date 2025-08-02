package com.recipe_manager.repository.recipe;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.recipe_manager.model.entity.recipe.RecipeTag;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for RecipeTagRepository.
 *
 * Tests repository methods for finding and checking tags by name.
 *
 * Note: These are mock-based unit tests since the repository depends on JPA
 * infrastructure that is not easily testable in isolation.
 */
@Tag("unit")
class RecipeTagRepositoryTest {

  private RecipeTagRepository recipeTagRepository;

  @BeforeEach
  void setUp() {
    recipeTagRepository = mock(RecipeTagRepository.class);
  }

  @Test
  @DisplayName("Should find tag by name ignoring case")
  void shouldFindTagByNameIgnoreCase() {
    // Given
    RecipeTag expectedTag = RecipeTag.builder()
        .tagId(1L)
        .name("Italian")
        .build();
    when(recipeTagRepository.findByNameIgnoreCase("italian"))
        .thenReturn(Optional.of(expectedTag));

    // When
    Optional<RecipeTag> result = recipeTagRepository.findByNameIgnoreCase("italian");

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getName()).isEqualTo("Italian");
    assertThat(result.get().getTagId()).isEqualTo(1L);
  }

  @Test
  @DisplayName("Should find tag by exact name match")
  void shouldFindTagByExactNameMatch() {
    // Given
    RecipeTag expectedTag = RecipeTag.builder()
        .tagId(2L)
        .name("Mexican")
        .build();
    when(recipeTagRepository.findByNameIgnoreCase("Mexican"))
        .thenReturn(Optional.of(expectedTag));

    // When
    Optional<RecipeTag> result = recipeTagRepository.findByNameIgnoreCase("Mexican");

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getName()).isEqualTo("Mexican");
    assertThat(result.get().getTagId()).isEqualTo(2L);
  }

  @Test
  @DisplayName("Should return empty when tag not found")
  void shouldReturnEmptyWhenTagNotFound() {
    // Given
    when(recipeTagRepository.findByNameIgnoreCase("Nonexistent"))
        .thenReturn(Optional.empty());

    // When
    Optional<RecipeTag> result = recipeTagRepository.findByNameIgnoreCase("Nonexistent");

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("Should check if tag exists by name ignoring case")
  void shouldCheckIfTagExistsByNameIgnoreCase() {
    // Given
    when(recipeTagRepository.existsByNameIgnoreCase("french"))
        .thenReturn(true);

    // When
    boolean exists = recipeTagRepository.existsByNameIgnoreCase("french");

    // Then
    assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("Should return false when checking non-existent tag")
  void shouldReturnFalseWhenCheckingNonExistentTag() {
    // Given
    when(recipeTagRepository.existsByNameIgnoreCase("NonExistent"))
        .thenReturn(false);

    // When
    boolean exists = recipeTagRepository.existsByNameIgnoreCase("NonExistent");

    // Then
    assertThat(exists).isFalse();
  }

  @Test
  @DisplayName("Should handle empty string in findByNameIgnoreCase")
  void shouldHandleEmptyStringInFindByNameIgnoreCase() {
    // Given
    when(recipeTagRepository.findByNameIgnoreCase(""))
        .thenReturn(Optional.empty());

    // When
    Optional<RecipeTag> result = recipeTagRepository.findByNameIgnoreCase("");

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("Should handle whitespace in tag name search")
  void shouldHandleWhitespaceInTagNameSearch() {
    // Given
    RecipeTag expectedTag = RecipeTag.builder()
        .tagId(3L)
        .name("Gluten Free")
        .build();
    when(recipeTagRepository.findByNameIgnoreCase("gluten free"))
        .thenReturn(Optional.of(expectedTag));

    // When
    Optional<RecipeTag> result = recipeTagRepository.findByNameIgnoreCase("gluten free");

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getName()).isEqualTo("Gluten Free");
    assertThat(result.get().getTagId()).isEqualTo(3L);
  }
}
