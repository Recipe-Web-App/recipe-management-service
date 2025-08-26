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
class RecipeTagMediaDtoTest {

  @Test
  @DisplayName("Should create RecipeTagMediaDto with builder pattern")
  @Tag("standard-processing")
  void shouldCreateRecipeTagMediaDtoWithBuilder() {
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
    RecipeTagMediaDto dto = RecipeTagMediaDto.builder()
        .mediaId(1L)
        .recipeId(2L)
        .tagId(3L)
        .media(mediaDto)
        .build();

    // Then
    assertThat(dto.getMediaId()).isEqualTo(1L);
    assertThat(dto.getRecipeId()).isEqualTo(2L);
    assertThat(dto.getTagId()).isEqualTo(3L);
    assertThat(dto.getMedia()).isEqualTo(mediaDto);
  }

  @Test
  @DisplayName("Should create RecipeTagMediaDto with minimal fields")
  @Tag("standard-processing")
  void shouldCreateRecipeTagMediaDtoWithMinimalFields() {
    // When
    RecipeTagMediaDto dto = RecipeTagMediaDto.builder()
        .mediaId(1L)
        .recipeId(2L)
        .tagId(3L)
        .build();

    // Then
    assertThat(dto.getMediaId()).isEqualTo(1L);
    assertThat(dto.getRecipeId()).isEqualTo(2L);
    assertThat(dto.getTagId()).isEqualTo(3L);
    assertThat(dto.getMedia()).isNull();
  }

  @Test
  @DisplayName("Should set and get all fields")
  @Tag("standard-processing")
  void shouldSetAndGetAllFields() {
    // Given
    RecipeTagMediaDto dto = new RecipeTagMediaDto();
    MediaDto mediaDto = MediaDto.builder().mediaId(5L).build();

    // When
    dto.setTagId(5L);
    dto.setRecipeId(6L);
    dto.setMediaId(7L);
    dto.setMedia(mediaDto);

    // Then
    assertThat(dto.getTagId()).isEqualTo(5L);
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
    RecipeTagMediaDto dto1 = RecipeTagMediaDto.builder()
        .mediaId(1L)
        .tagId(2L)
        .recipeId(3L)
        .media(mediaDto)
        .build();
    RecipeTagMediaDto dto2 = RecipeTagMediaDto.builder()
        .mediaId(1L)
        .tagId(2L)
        .recipeId(3L)
        .media(mediaDto)
        .build();

    // Then
    assertThat(dto1)
        .isEqualTo(dto2)
        .hasSameHashCodeAs(dto2);
    assertThat(dto1.toString()).contains("tagId=2");
  }

  @Test
  @DisplayName("Should handle null values correctly")
  @Tag("error-processing")
  void shouldHandleNullValuesCorrectly() {
    // When
    RecipeTagMediaDto dto = RecipeTagMediaDto.builder().build();

    // Then
    assertThat(dto.getMediaId()).isNull();
    assertThat(dto.getRecipeId()).isNull();
    assertThat(dto.getTagId()).isNull();
    assertThat(dto.getMedia()).isNull();
  }
}
