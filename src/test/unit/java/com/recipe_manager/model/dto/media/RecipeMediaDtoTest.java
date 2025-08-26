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
class RecipeMediaDtoTest {

  @Test
  @DisplayName("Should create RecipeMediaDto with builder pattern")
  @Tag("standard-processing")
  void shouldCreateRecipeMediaDtoWithBuilder() {
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
    RecipeMediaDto dto = RecipeMediaDto.builder()
        .mediaId(1L)
        .recipeId(2L)
        .media(mediaDto)
        .build();

    // Then
    assertThat(dto.getMediaId()).isEqualTo(1L);
    assertThat(dto.getRecipeId()).isEqualTo(2L);
    assertThat(dto.getMedia()).isEqualTo(mediaDto);
  }

  @Test
  @DisplayName("Should create RecipeMediaDto with minimal fields")
  @Tag("standard-processing")
  void shouldCreateRecipeMediaDtoWithMinimalFields() {
    // When
    RecipeMediaDto dto = RecipeMediaDto.builder()
        .mediaId(1L)
        .recipeId(2L)
        .build();

    // Then
    assertThat(dto.getMediaId()).isEqualTo(1L);
    assertThat(dto.getRecipeId()).isEqualTo(2L);
    assertThat(dto.getMedia()).isNull();
  }

  @Test
  @DisplayName("Should set and get all fields")
  @Tag("standard-processing")
  void shouldSetAndGetAllFields() {
    // Given
    RecipeMediaDto dto = new RecipeMediaDto();
    MediaDto mediaDto = MediaDto.builder().mediaId(5L).build();

    // When
    dto.setRecipeId(5L);
    dto.setMediaId(6L);
    dto.setMedia(mediaDto);

    // Then
    assertThat(dto.getRecipeId()).isEqualTo(5L);
    assertThat(dto.getMediaId()).isEqualTo(6L);
    assertThat(dto.getMedia()).isEqualTo(mediaDto);
  }

  @Test
  @DisplayName("Should implement equals and hashCode correctly")
  @Tag("standard-processing")
  void shouldImplementEqualsAndHashCodeCorrectly() {
    // Given
    MediaDto mediaDto = MediaDto.builder().mediaId(1L).build();
    RecipeMediaDto dto1 = RecipeMediaDto.builder()
        .mediaId(1L)
        .recipeId(2L)
        .media(mediaDto)
        .build();
    RecipeMediaDto dto2 = RecipeMediaDto.builder()
        .mediaId(1L)
        .recipeId(2L)
        .media(mediaDto)
        .build();

    // Then
    assertThat(dto1)
        .isEqualTo(dto2)
        .hasSameHashCodeAs(dto2);
    assertThat(dto1.toString()).contains("recipeId=2");
  }

  @Test
  @DisplayName("Should handle null values correctly")
  @Tag("error-processing")
  void shouldHandleNullValuesCorrectly() {
    // When
    RecipeMediaDto dto = new RecipeMediaDto();

    // Then
    assertThat(dto.getMediaId()).isNull();
    assertThat(dto.getRecipeId()).isNull();
    assertThat(dto.getMedia()).isNull();
  }
}
