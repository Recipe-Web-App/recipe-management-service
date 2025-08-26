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
class RecipeStepMediaDtoTest {

  @Test
  @DisplayName("Should create RecipeStepMediaDto with builder pattern")
  @Tag("standard-processing")
  void shouldCreateRecipeStepMediaDtoWithBuilder() {
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
    RecipeStepMediaDto dto = RecipeStepMediaDto.builder()
        .mediaId(1L)
        .recipeId(2L)
        .stepId(3L)
        .media(mediaDto)
        .build();

    // Then
    assertThat(dto.getMediaId()).isEqualTo(1L);
    assertThat(dto.getRecipeId()).isEqualTo(2L);
    assertThat(dto.getStepId()).isEqualTo(3L);
    assertThat(dto.getMedia()).isEqualTo(mediaDto);
  }

  @Test
  @DisplayName("Should create RecipeStepMediaDto with minimal fields")
  @Tag("standard-processing")
  void shouldCreateRecipeStepMediaDtoWithMinimalFields() {
    // When
    RecipeStepMediaDto dto = RecipeStepMediaDto.builder()
        .mediaId(1L)
        .recipeId(2L)
        .stepId(3L)
        .build();

    // Then
    assertThat(dto.getMediaId()).isEqualTo(1L);
    assertThat(dto.getRecipeId()).isEqualTo(2L);
    assertThat(dto.getStepId()).isEqualTo(3L);
    assertThat(dto.getMedia()).isNull();
  }

  @Test
  @DisplayName("Should set and get all fields")
  @Tag("standard-processing")
  void shouldSetAndGetAllFields() {
    // Given
    RecipeStepMediaDto dto = new RecipeStepMediaDto();
    MediaDto mediaDto = MediaDto.builder().mediaId(5L).build();

    // When
    dto.setStepId(5L);
    dto.setRecipeId(6L);
    dto.setMediaId(7L);
    dto.setMedia(mediaDto);

    // Then
    assertThat(dto.getStepId()).isEqualTo(5L);
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
    RecipeStepMediaDto dto1 = RecipeStepMediaDto.builder()
        .mediaId(1L)
        .stepId(2L)
        .recipeId(3L)
        .media(mediaDto)
        .build();
    RecipeStepMediaDto dto2 = RecipeStepMediaDto.builder()
        .mediaId(1L)
        .stepId(2L)
        .recipeId(3L)
        .media(mediaDto)
        .build();

    // Then
    assertThat(dto1)
        .isEqualTo(dto2)
        .hasSameHashCodeAs(dto2);
    assertThat(dto1.toString()).contains("stepId=2");
  }

  @Test
  @DisplayName("Should handle null values correctly")
  @Tag("error-processing")
  void shouldHandleNullValuesCorrectly() {
    // When
    RecipeStepMediaDto dto = new RecipeStepMediaDto();

    // Then
    assertThat(dto.getMediaId()).isNull();
    assertThat(dto.getRecipeId()).isNull();
    assertThat(dto.getStepId()).isNull();
    assertThat(dto.getMedia()).isNull();
  }
}
