package com.recipe_manager.model.dto.recipe;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.recipe_manager.model.dto.media.RecipeRevisionMediaDto;
import com.recipe_manager.model.enums.RevisionCategory;
import com.recipe_manager.model.enums.RevisionType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for RecipeRevisionDto.
 */
@Tag("unit")
class RecipeRevisionDtoTest {

  private RecipeRevisionDto recipeRevisionDto;
  private UUID userId;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    recipeRevisionDto = RecipeRevisionDto.builder()
        .revisionId(1L)
        .recipeId(1L)
        .userId(userId)
        .revisionCategory(RevisionCategory.INGREDIENT)
        .revisionType(RevisionType.ADD)
        .previousData("{}")
        .newData("{\"ingredient\":\"salt\"}")
        .changeComment("Added salt to recipe")
        .build();
  }

  @Test
  @DisplayName("Should create recipe revision DTO with constructor")
  @Tag("standard-processing")
  void shouldCreateRecipeRevisionDtoWithConstructor() {
    // Given
    List<RecipeRevisionMediaDto> media = Arrays.asList(new RecipeRevisionMediaDto());
    LocalDateTime createdAt = LocalDateTime.of(2023, 1, 1, 12, 0);
    LocalDateTime updatedAt = LocalDateTime.of(2023, 1, 2, 12, 0);

    // When
    RecipeRevisionDto dto = new RecipeRevisionDto(1L, 1L, userId, RevisionCategory.INGREDIENT,
        RevisionType.ADD, "{}", "{\"ingredient\":\"salt\"}", "Added salt to recipe",
        createdAt, updatedAt, media);

    // Then
    assertThat(dto.getRevisionId()).isEqualTo(1L);
    assertThat(dto.getRecipeId()).isEqualTo(1L);
    assertThat(dto.getUserId()).isEqualTo(userId);
    assertThat(dto.getRevisionCategory()).isEqualTo(RevisionCategory.INGREDIENT);
    assertThat(dto.getRevisionType()).isEqualTo(RevisionType.ADD);
    assertThat(dto.getPreviousData()).isEqualTo("{}");
    assertThat(dto.getNewData()).isEqualTo("{\"ingredient\":\"salt\"}");
    assertThat(dto.getChangeComment()).isEqualTo("Added salt to recipe");
    assertThat(dto.getCreatedAt()).isEqualTo(createdAt);
    assertThat(dto.getUpdatedAt()).isEqualTo(updatedAt);
    assertThat(dto.getMedia()).isEqualTo(media);
  }

  @Test
  @DisplayName("Should create recipe revision DTO with builder")
  @Tag("standard-processing")
  void shouldCreateRecipeRevisionDtoWithBuilder() {
    // Then
    assertThat(recipeRevisionDto.getRevisionId()).isEqualTo(1L);
    assertThat(recipeRevisionDto.getRecipeId()).isEqualTo(1L);
    assertThat(recipeRevisionDto.getUserId()).isEqualTo(userId);
    assertThat(recipeRevisionDto.getRevisionCategory()).isEqualTo(RevisionCategory.INGREDIENT);
    assertThat(recipeRevisionDto.getRevisionType()).isEqualTo(RevisionType.ADD);
    assertThat(recipeRevisionDto.getPreviousData()).isEqualTo("{}");
    assertThat(recipeRevisionDto.getNewData()).isEqualTo("{\"ingredient\":\"salt\"}");
    assertThat(recipeRevisionDto.getChangeComment()).isEqualTo("Added salt to recipe");
  }

  @Test
  @DisplayName("Should set and get all properties")
  @Tag("standard-processing")
  void shouldSetAndGetAllProperties() {
    // Given
    Long newRevisionId = 2L;
    Long newRecipeId = 2L;
    UUID newUserId = UUID.randomUUID();
    String newPreviousData = "{\"old\":\"data\"}";
    String newNewData = "{\"new\":\"data\"}";
    String newChangeComment = "Updated comment";
    LocalDateTime createdAt = LocalDateTime.now();

    // When
    recipeRevisionDto.setRevisionId(newRevisionId);
    recipeRevisionDto.setRecipeId(newRecipeId);
    recipeRevisionDto.setUserId(newUserId);
    recipeRevisionDto.setPreviousData(newPreviousData);
    recipeRevisionDto.setNewData(newNewData);
    recipeRevisionDto.setChangeComment(newChangeComment);
    recipeRevisionDto.setCreatedAt(createdAt);

    // Then
    assertThat(recipeRevisionDto.getRevisionId()).isEqualTo(newRevisionId);
    assertThat(recipeRevisionDto.getRecipeId()).isEqualTo(newRecipeId);
    assertThat(recipeRevisionDto.getUserId()).isEqualTo(newUserId);
    assertThat(recipeRevisionDto.getPreviousData()).isEqualTo(newPreviousData);
    assertThat(recipeRevisionDto.getNewData()).isEqualTo(newNewData);
    assertThat(recipeRevisionDto.getChangeComment()).isEqualTo(newChangeComment);
    assertThat(recipeRevisionDto.getCreatedAt()).isEqualTo(createdAt);
  }

  @Test
  @DisplayName("Should return unmodifiable list for media")
  @Tag("standard-processing")
  void shouldReturnUnmodifiableListForMedia() {
    // Given
    List<RecipeRevisionMediaDto> media = new ArrayList<>();
    recipeRevisionDto.setMedia(media);

    // When & Then
    assertThat(recipeRevisionDto.getMedia()).isUnmodifiable();
  }

  @Test
  @DisplayName("Should defensively copy media list")
  @Tag("standard-processing")
  void shouldDefensivelyCopyMediaList() {
    // Given
    List<RecipeRevisionMediaDto> originalMedia = new ArrayList<>();
    originalMedia.add(new RecipeRevisionMediaDto());
    recipeRevisionDto.setMedia(originalMedia);

    // When
    originalMedia.add(new RecipeRevisionMediaDto());

    // Then
    assertThat(recipeRevisionDto.getMedia()).hasSize(1);
  }

  @Test
  @DisplayName("Should handle null media in constructor")
  @Tag("error-processing")
  void shouldHandleNullMediaInConstructor() {
    // When
    RecipeRevisionDto dto = new RecipeRevisionDto(1L, 1L, userId, RevisionCategory.INGREDIENT,
        RevisionType.ADD, "{}", "{\"ingredient\":\"salt\"}", "Added salt to recipe",
        LocalDateTime.now(), LocalDateTime.now(), null);

    // Then
    assertThat(dto.getMedia()).isEmpty();
  }

  @Test
  @DisplayName("Should handle null media in setter")
  @Tag("error-processing")
  void shouldHandleNullMediaInSetter() {
    // Given
    recipeRevisionDto.setMedia(Arrays.asList(new RecipeRevisionMediaDto()));

    // When
    recipeRevisionDto.setMedia(null);

    // Then
    assertThat(recipeRevisionDto.getMedia()).isEmpty();
  }

  @Test
  @DisplayName("Should return true when comparing equal RecipeRevisionDto objects")
  @Tag("standard-processing")
  void shouldReturnTrueWhenComparingEqualRecipeRevisionDtoObjects() {
    // Given
    LocalDateTime now = LocalDateTime.now();
    RecipeRevisionDto dto1 = new RecipeRevisionDto(1L, 1L, userId, RevisionCategory.INGREDIENT,
        RevisionType.ADD, "{}", "{\"ingredient\":\"salt\"}", "Added salt to recipe",
        now, now, new ArrayList<>());
    RecipeRevisionDto dto2 = new RecipeRevisionDto(1L, 1L, userId, RevisionCategory.INGREDIENT,
        RevisionType.ADD, "{}", "{\"ingredient\":\"salt\"}", "Added salt to recipe",
        now, now, new ArrayList<>());

    // When & Then
    assertThat(dto1).isEqualTo(dto2);
    assertThat(dto2).isEqualTo(dto1);
  }

  @Test
  @DisplayName("Should return false when comparing different RecipeRevisionDto objects")
  @Tag("standard-processing")
  void shouldReturnFalseWhenComparingDifferentRecipeRevisionDtoObjects() {
    // Given
    LocalDateTime now = LocalDateTime.now();
    RecipeRevisionDto dto1 = new RecipeRevisionDto(1L, 1L, userId, RevisionCategory.INGREDIENT,
        RevisionType.ADD, "{}", "{\"ingredient\":\"salt\"}", "Added salt to recipe",
        now, now, new ArrayList<>());
    RecipeRevisionDto dto2 = new RecipeRevisionDto(2L, 1L, userId, RevisionCategory.INGREDIENT,
        RevisionType.ADD, "{}", "{\"ingredient\":\"salt\"}", "Added salt to recipe",
        now, now, new ArrayList<>());

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
    RecipeRevisionDto dto = new RecipeRevisionDto(1L, 1L, userId, RevisionCategory.INGREDIENT,
        RevisionType.ADD, "{}", "{\"ingredient\":\"salt\"}", "Added salt to recipe",
        now, now, new ArrayList<>());

    // When & Then
    assertThat(dto).isNotEqualTo(null);
  }

  @Test
  @DisplayName("Should return false when comparing with different type")
  @Tag("error-processing")
  void shouldReturnFalseWhenComparingWithDifferentType() {
    // Given
    LocalDateTime now = LocalDateTime.now();
    RecipeRevisionDto dto = new RecipeRevisionDto(1L, 1L, userId, RevisionCategory.INGREDIENT,
        RevisionType.ADD, "{}", "{\"ingredient\":\"salt\"}", "Added salt to recipe",
        now, now, new ArrayList<>());
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
    RecipeRevisionDto dto = new RecipeRevisionDto(1L, 1L, userId, RevisionCategory.INGREDIENT,
        RevisionType.ADD, "{}", "{\"ingredient\":\"salt\"}", "Added salt to recipe",
        now, now, new ArrayList<>());

    // When & Then
    assertThat(dto).isEqualTo(dto);
  }

  @Test
  @DisplayName("Should have same hashCode for equal objects")
  @Tag("standard-processing")
  void shouldHaveSameHashCodeForEqualObjects() {
    // Given
    LocalDateTime now = LocalDateTime.now();
    RecipeRevisionDto dto1 = new RecipeRevisionDto(1L, 1L, userId, RevisionCategory.INGREDIENT,
        RevisionType.ADD, "{}", "{\"ingredient\":\"salt\"}", "Added salt to recipe",
        now, now, new ArrayList<>());
    RecipeRevisionDto dto2 = new RecipeRevisionDto(1L, 1L, userId, RevisionCategory.INGREDIENT,
        RevisionType.ADD, "{}", "{\"ingredient\":\"salt\"}", "Added salt to recipe",
        now, now, new ArrayList<>());

    // When & Then
    assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
  }

  @Test
  @DisplayName("Should have different hashCode for different objects")
  @Tag("standard-processing")
  void shouldHaveDifferentHashCodeForDifferentObjects() {
    // Given
    LocalDateTime now = LocalDateTime.now();
    RecipeRevisionDto dto1 = new RecipeRevisionDto(1L, 1L, userId, RevisionCategory.INGREDIENT,
        RevisionType.ADD, "{}", "{\"ingredient\":\"salt\"}", "Added salt to recipe",
        now, now, new ArrayList<>());
    RecipeRevisionDto dto2 = new RecipeRevisionDto(2L, 1L, userId, RevisionCategory.INGREDIENT,
        RevisionType.ADD, "{}", "{\"ingredient\":\"salt\"}", "Added salt to recipe",
        now, now, new ArrayList<>());

    // When & Then
    assertThat(dto1.hashCode()).isNotEqualTo(dto2.hashCode());
  }

  @Test
  @DisplayName("Should handle null values in equals")
  @Tag("error-processing")
  void shouldHandleNullValuesInEquals() {
    // Given
    LocalDateTime now = LocalDateTime.now();
    RecipeRevisionDto dto1 = new RecipeRevisionDto(null, 1L, userId, RevisionCategory.INGREDIENT,
        RevisionType.ADD, "{}", "{\"ingredient\":\"salt\"}", "Added salt to recipe",
        now, now, new ArrayList<>());
    RecipeRevisionDto dto2 = new RecipeRevisionDto(null, 1L, userId, RevisionCategory.INGREDIENT,
        RevisionType.ADD, "{}", "{\"ingredient\":\"salt\"}", "Added salt to recipe",
        now, now, new ArrayList<>());
    RecipeRevisionDto dto3 = new RecipeRevisionDto(1L, 1L, userId, RevisionCategory.INGREDIENT,
        RevisionType.ADD, "{}", "{\"ingredient\":\"salt\"}", "Added salt to recipe",
        now, now, new ArrayList<>());

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
    RecipeRevisionDto dto = new RecipeRevisionDto(null, 1L, userId, RevisionCategory.INGREDIENT,
        RevisionType.ADD, "{}", "{\"ingredient\":\"salt\"}", "Added salt to recipe",
        now, now, new ArrayList<>());

    // When & Then
    assertThat(dto.hashCode()).isNotNull();
  }

  @Test
  @DisplayName("Builder should defensively copy media list")
  @Tag("standard-processing")
  void builderShouldDefensivelyCopyMediaList() {
    List<RecipeRevisionMediaDto> media = new ArrayList<>();
    media.add(new RecipeRevisionMediaDto());
    RecipeRevisionDto dto = RecipeRevisionDto.builder()
        .media(media)
        .build();
    media.add(new RecipeRevisionMediaDto());
    assertThat(dto.getMedia()).hasSize(1);
  }

  @Test
  @DisplayName("Builder should handle null media as empty")
  @Tag("error-processing")
  void builderShouldHandleNullMediaAsEmpty() {
    RecipeRevisionDto dto = RecipeRevisionDto.builder()
        .media(null)
        .build();
    assertThat(dto.getMedia()).isEmpty();
  }

  @Test
  @DisplayName("toString should include key fields and not be null")
  @Tag("standard-processing")
  void toStringShouldIncludeKeyFields() {
    RecipeRevisionDto dto = RecipeRevisionDto.builder()
        .revisionId(1L)
        .recipeId(1L)
        .userId(UUID.randomUUID())
        .revisionCategory(RevisionCategory.INGREDIENT)
        .revisionType(RevisionType.ADD)
        .previousData("{}")
        .newData("{\"ingredient\":\"salt\"}")
        .changeComment("Added salt to recipe")
        .build();
    String str = dto.toString();
    assertThat(str).isNotNull();
    assertThat(str).contains("RecipeRevisionDto");
    assertThat(str).contains("Added salt to recipe");
  }

  @Test
  @DisplayName("Should handle nulls for all fields")
  @Tag("error-processing")
  void shouldHandleNullsForAllFields() {
    RecipeRevisionDto dto = new RecipeRevisionDto(null, null, null, null, null, null, null, null, null, null, null);
    assertThat(dto.getRevisionId()).isNull();
    assertThat(dto.getRecipeId()).isNull();
    assertThat(dto.getUserId()).isNull();
    assertThat(dto.getRevisionCategory()).isNull();
    assertThat(dto.getRevisionType()).isNull();
    assertThat(dto.getPreviousData()).isNull();
    assertThat(dto.getNewData()).isNull();
    assertThat(dto.getChangeComment()).isNull();
    assertThat(dto.getCreatedAt()).isNull();
    assertThat(dto.getUpdatedAt()).isNull();
    assertThat(dto.getMedia()).isEmpty();
  }
}
