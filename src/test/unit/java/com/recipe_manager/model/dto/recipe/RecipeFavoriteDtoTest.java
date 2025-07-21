package com.recipe_manager.model.dto.recipe;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.recipe_manager.model.dto.media.RecipeFavoriteMediaDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for RecipeFavoriteDto.
 */
@Tag("unit")
class RecipeFavoriteDtoTest {

  private RecipeFavoriteDto recipeFavoriteDto;
  private UUID userId;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    recipeFavoriteDto = RecipeFavoriteDto.builder()
        .recipeId(1L)
        .userId(userId)
        .favoritedAt(LocalDateTime.now())
        .build();
  }

  @Test
  @DisplayName("Should create recipe favorite DTO with constructor")
  @Tag("standard-processing")
  void shouldCreateRecipeFavoriteDtoWithConstructor() {
    // Given
    List<RecipeFavoriteMediaDto> media = Arrays.asList(new RecipeFavoriteMediaDto());
    LocalDateTime favoritedAt = LocalDateTime.of(2023, 1, 1, 12, 0);
    LocalDateTime createdAt = LocalDateTime.of(2023, 1, 2, 12, 0);
    LocalDateTime updatedAt = LocalDateTime.of(2023, 1, 3, 12, 0);

    // When
    RecipeFavoriteDto dto = new RecipeFavoriteDto(1L, userId, favoritedAt, createdAt, updatedAt, media);

    // Then
    assertThat(dto.getRecipeId()).isEqualTo(1L);
    assertThat(dto.getUserId()).isEqualTo(userId);
    assertThat(dto.getFavoritedAt()).isEqualTo(favoritedAt);
    assertThat(dto.getCreatedAt()).isEqualTo(createdAt);
    assertThat(dto.getUpdatedAt()).isEqualTo(updatedAt);
    assertThat(dto.getMedia()).isEqualTo(media);
  }

  @Test
  @DisplayName("Should create recipe favorite DTO with builder")
  @Tag("standard-processing")
  void shouldCreateRecipeFavoriteDtoWithBuilder() {
    // Then
    assertThat(recipeFavoriteDto.getRecipeId()).isEqualTo(1L);
    assertThat(recipeFavoriteDto.getUserId()).isEqualTo(userId);
    assertThat(recipeFavoriteDto.getFavoritedAt()).isNotNull();
  }

  @Test
  @DisplayName("Should set and get all properties")
  @Tag("standard-processing")
  void shouldSetAndGetAllProperties() {
    // Given
    Long newRecipeId = 2L;
    UUID newUserId = UUID.randomUUID();
    LocalDateTime newFavoritedAt = LocalDateTime.now();
    LocalDateTime newCreatedAt = LocalDateTime.now();
    LocalDateTime newUpdatedAt = LocalDateTime.now();

    // When
    recipeFavoriteDto.setRecipeId(newRecipeId);
    recipeFavoriteDto.setUserId(newUserId);
    recipeFavoriteDto.setFavoritedAt(newFavoritedAt);
    recipeFavoriteDto.setCreatedAt(newCreatedAt);
    recipeFavoriteDto.setUpdatedAt(newUpdatedAt);

    // Then
    assertThat(recipeFavoriteDto.getRecipeId()).isEqualTo(newRecipeId);
    assertThat(recipeFavoriteDto.getUserId()).isEqualTo(newUserId);
    assertThat(recipeFavoriteDto.getFavoritedAt()).isEqualTo(newFavoritedAt);
    assertThat(recipeFavoriteDto.getCreatedAt()).isEqualTo(newCreatedAt);
    assertThat(recipeFavoriteDto.getUpdatedAt()).isEqualTo(newUpdatedAt);
  }

  @Test
  @DisplayName("Should return unmodifiable list for media")
  @Tag("standard-processing")
  void shouldReturnUnmodifiableListForMedia() {
    // Given
    List<RecipeFavoriteMediaDto> media = new ArrayList<>();
    recipeFavoriteDto.setMedia(media);

    // When & Then
    assertThat(recipeFavoriteDto.getMedia()).isUnmodifiable();
  }

  @Test
  @DisplayName("Should defensively copy media list")
  @Tag("standard-processing")
  void shouldDefensivelyCopyMediaList() {
    // Given
    List<RecipeFavoriteMediaDto> originalMedia = new ArrayList<>();
    originalMedia.add(new RecipeFavoriteMediaDto());
    recipeFavoriteDto.setMedia(originalMedia);

    // When
    originalMedia.add(new RecipeFavoriteMediaDto());

    // Then
    assertThat(recipeFavoriteDto.getMedia()).hasSize(1);
  }

  @Test
  @DisplayName("Should handle null media in constructor")
  @Tag("error-processing")
  void shouldHandleNullMediaInConstructor() {
    // When
    RecipeFavoriteDto dto = new RecipeFavoriteDto(1L, userId, LocalDateTime.now(),
        LocalDateTime.now(), LocalDateTime.now(), null);

    // Then
    assertThat(dto.getMedia()).isEmpty();
  }

  @Test
  @DisplayName("Should handle null media in setter")
  @Tag("error-processing")
  void shouldHandleNullMediaInSetter() {
    // Given
    recipeFavoriteDto.setMedia(Arrays.asList(new RecipeFavoriteMediaDto()));

    // When
    recipeFavoriteDto.setMedia(null);

    // Then
    assertThat(recipeFavoriteDto.getMedia()).isEmpty();
  }

  @Test
  @DisplayName("Should return true when comparing equal RecipeFavoriteDto objects")
  @Tag("standard-processing")
  void shouldReturnTrueWhenComparingEqualRecipeFavoriteDtoObjects() {
    // Given
    LocalDateTime now = LocalDateTime.now();
    RecipeFavoriteDto dto1 = new RecipeFavoriteDto(1L, userId, now, now, now, new ArrayList<>());
    RecipeFavoriteDto dto2 = new RecipeFavoriteDto(1L, userId, now, now, now, new ArrayList<>());

    // When & Then
    assertThat(dto1).isEqualTo(dto2);
    assertThat(dto2).isEqualTo(dto1);
  }

  @Test
  @DisplayName("Should return false when comparing different RecipeFavoriteDto objects")
  @Tag("standard-processing")
  void shouldReturnFalseWhenComparingDifferentRecipeFavoriteDtoObjects() {
    // Given
    LocalDateTime now = LocalDateTime.now();
    RecipeFavoriteDto dto1 = new RecipeFavoriteDto(1L, userId, now, now, now, new ArrayList<>());
    RecipeFavoriteDto dto2 = new RecipeFavoriteDto(2L, userId, now, now, now, new ArrayList<>());

    // When & Then
    assertThat(dto1).isNotEqualTo(dto2);
    assertThat(dto2).isNotEqualTo(dto1);
  }

  @Test
  @DisplayName("Should return false when comparing with null")
  @Tag("error-processing")
  void shouldReturnFalseWhenComparingWithNull() {
    // Given
    LocalDateTime now = LocalDateTime.now();
    RecipeFavoriteDto dto = new RecipeFavoriteDto(1L, userId, now, now, now, new ArrayList<>());

    // When & Then
    assertThat(dto).isNotEqualTo(null);
  }

  @Test
  @DisplayName("Should return false when comparing with different type")
  @Tag("error-processing")
  void shouldReturnFalseWhenComparingWithDifferentType() {
    // Given
    LocalDateTime now = LocalDateTime.now();
    RecipeFavoriteDto dto = new RecipeFavoriteDto(1L, userId, now, now, now, new ArrayList<>());
    Object other = new Object();

    // When & Then
    assertThat(dto).isNotEqualTo(other);
  }

  @Test
  @DisplayName("Should return true when comparing with itself")
  @Tag("standard-processing")
  void shouldReturnTrueWhenComparingWithItself() {
    // Given
    LocalDateTime now = LocalDateTime.now();
    RecipeFavoriteDto dto = new RecipeFavoriteDto(1L, userId, now, now, now, new ArrayList<>());

    // When & Then
    assertThat(dto).isEqualTo(dto);
  }

  @Test
  @DisplayName("Should have same hashCode for equal objects")
  @Tag("standard-processing")
  void shouldHaveSameHashCodeForEqualObjects() {
    // Given
    LocalDateTime now = LocalDateTime.now();
    RecipeFavoriteDto dto1 = new RecipeFavoriteDto(1L, userId, now, now, now, new ArrayList<>());
    RecipeFavoriteDto dto2 = new RecipeFavoriteDto(1L, userId, now, now, now, new ArrayList<>());

    // When & Then
    assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
  }

  @Test
  @DisplayName("Should have different hashCode for different objects")
  @Tag("standard-processing")
  void shouldHaveDifferentHashCodeForDifferentObjects() {
    // Given
    LocalDateTime now = LocalDateTime.now();
    RecipeFavoriteDto dto1 = new RecipeFavoriteDto(1L, userId, now, now, now, new ArrayList<>());
    RecipeFavoriteDto dto2 = new RecipeFavoriteDto(2L, userId, now, now, now, new ArrayList<>());

    // When & Then
    assertThat(dto1.hashCode()).isNotEqualTo(dto2.hashCode());
  }

  @Test
  @DisplayName("Should handle null values in equals")
  @Tag("error-processing")
  void shouldHandleNullValuesInEquals() {
    // Given
    LocalDateTime now = LocalDateTime.now();
    RecipeFavoriteDto dto1 = new RecipeFavoriteDto(null, userId, now, now, now, new ArrayList<>());
    RecipeFavoriteDto dto2 = new RecipeFavoriteDto(null, userId, now, now, now, new ArrayList<>());
    RecipeFavoriteDto dto3 = new RecipeFavoriteDto(1L, userId, now, now, now, new ArrayList<>());

    // When & Then
    assertThat(dto1).isEqualTo(dto2);
    assertThat(dto1).isNotEqualTo(dto3);
  }

  @Test
  @DisplayName("Should handle null values in hashCode")
  @Tag("error-processing")
  void shouldHandleNullValuesInHashCode() {
    // Given
    LocalDateTime now = LocalDateTime.now();
    RecipeFavoriteDto dto = new RecipeFavoriteDto(null, userId, now, now, now, new ArrayList<>());

    // When & Then
    assertThat(dto.hashCode()).isNotNull();
  }

  @Test
  @DisplayName("Builder should defensively copy media list")
  @Tag("standard-processing")
  void builderShouldDefensivelyCopyMediaList() {
    List<RecipeFavoriteMediaDto> media = new ArrayList<>();
    media.add(new RecipeFavoriteMediaDto());
    RecipeFavoriteDto dto = RecipeFavoriteDto.builder()
        .media(media)
        .build();
    media.add(new RecipeFavoriteMediaDto());
    assertThat(dto.getMedia()).hasSize(1);
  }

  @Test
  @DisplayName("Builder should handle null media as empty")
  @Tag("error-processing")
  void builderShouldHandleNullMediaAsEmpty() {
    RecipeFavoriteDto dto = RecipeFavoriteDto.builder()
        .media(null)
        .build();
    assertThat(dto.getMedia()).isEmpty();
  }

  @Test
  @DisplayName("toString should include key fields and not be null")
  @Tag("standard-processing")
  void toStringShouldIncludeKeyFields() {
    RecipeFavoriteDto dto = RecipeFavoriteDto.builder()
        .recipeId(1L)
        .userId(UUID.randomUUID())
        .favoritedAt(LocalDateTime.now())
        .build();
    String str = dto.toString();
    assertThat(str).isNotNull();
    assertThat(str).contains("RecipeFavoriteDto");
  }

  @Test
  @DisplayName("Should handle nulls for all fields")
  @Tag("error-processing")
  void shouldHandleNullsForAllFields() {
    RecipeFavoriteDto dto = new RecipeFavoriteDto(null, null, null, null, null, null);
    assertThat(dto.getRecipeId()).isNull();
    assertThat(dto.getUserId()).isNull();
    assertThat(dto.getFavoritedAt()).isNull();
    assertThat(dto.getCreatedAt()).isNull();
    assertThat(dto.getUpdatedAt()).isNull();
    assertThat(dto.getMedia()).isEmpty();
  }
}
