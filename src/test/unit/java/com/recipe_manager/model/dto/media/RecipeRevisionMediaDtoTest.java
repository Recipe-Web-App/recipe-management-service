package com.recipe_manager.model.dto.media;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.UUID;

import com.recipe_manager.model.enums.MediaType;
import com.recipe_manager.model.enums.ProcessingStatus;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class RecipeRevisionMediaDtoTest {

  @Test
  @DisplayName("Should create RecipeRevisionMediaDto with builder pattern")
  @Tag("standard-processing")
  void shouldCreateRecipeRevisionMediaDtoWithBuilder() {
    // Given
    MediaDto mediaDto = MediaDto.builder()
        .mediaId(1L)
        .userId(UUID.randomUUID())
        .mediaType(MediaType.IMAGE_JPEG)
        .mediaPath("https://example.com/image.jpg")
        .processingStatus(ProcessingStatus.COMPLETE)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    // When
    RecipeRevisionMediaDto dto = RecipeRevisionMediaDto.builder()
        .mediaId(1L)
        .recipeId(2L)
        .revisionNumber(3)
        .media(mediaDto)
        .build();

    // Then
    assertThat(dto.getMediaId()).isEqualTo(1L);
    assertThat(dto.getRecipeId()).isEqualTo(2L);
    assertThat(dto.getRevisionNumber()).isEqualTo(3);
    assertThat(dto.getMedia()).isEqualTo(mediaDto);
  }

  @Test
  @DisplayName("Should create RecipeRevisionMediaDto with minimal fields")
  @Tag("standard-processing")
  void shouldCreateRecipeRevisionMediaDtoWithMinimalFields() {
    // When
    RecipeRevisionMediaDto dto = RecipeRevisionMediaDto.builder()
        .mediaId(1L)
        .recipeId(2L)
        .revisionNumber(3)
        .build();

    // Then
    assertThat(dto.getMediaId()).isEqualTo(1L);
    assertThat(dto.getRecipeId()).isEqualTo(2L);
    assertThat(dto.getRevisionNumber()).isEqualTo(3);
    assertThat(dto.getMedia()).isNull();
  }

  @Test
  @DisplayName("Should set and get all fields")
  @Tag("standard-processing")
  void shouldSetAndGetAllFields() {
    // Given
    RecipeRevisionMediaDto dto = new RecipeRevisionMediaDto();
    MediaDto mediaDto = MediaDto.builder().mediaId(5L).build();

    // When
    dto.setRevisionNumber(5);
    dto.setRecipeId(6L);
    dto.setMediaId(7L);
    dto.setMedia(mediaDto);

    // Then
    assertThat(dto.getRevisionNumber()).isEqualTo(5);
    assertThat(dto.getRecipeId()).isEqualTo(6L);
    assertThat(dto.getMediaId()).isEqualTo(7L);
    assertThat(dto.getMedia()).isEqualTo(mediaDto);
  }

  @Test
  @DisplayName("Should implement equals and hashCode correctly")
  @Tag("standard-processing")
  void shouldImplementEqualsAndHashCodeCorrectly() {
    // Given
    MediaDto mediaDto = MediaDto.builder().mediaId(1L).build();
    RecipeRevisionMediaDto dto1 = RecipeRevisionMediaDto.builder()
        .mediaId(1L)
        .revisionNumber(2)
        .recipeId(3L)
        .media(mediaDto)
        .build();
    RecipeRevisionMediaDto dto2 = RecipeRevisionMediaDto.builder()
        .mediaId(1L)
        .revisionNumber(2)
        .recipeId(3L)
        .media(mediaDto)
        .build();

    // Then
    assertThat(dto1)
        .isEqualTo(dto2)
        .hasSameHashCodeAs(dto2);
    assertThat(dto1.toString()).contains("revisionNumber=2");
  }

  @Test
  @DisplayName("Should handle null values correctly")
  @Tag("error-processing")
  void shouldHandleNullValuesCorrectly() {
    // When
    RecipeRevisionMediaDto dto = new RecipeRevisionMediaDto();

    // Then
    assertThat(dto.getMediaId()).isNull();
    assertThat(dto.getRecipeId()).isNull();
    assertThat(dto.getRevisionNumber()).isNull();
    assertThat(dto.getMedia()).isNull();
  }
}
