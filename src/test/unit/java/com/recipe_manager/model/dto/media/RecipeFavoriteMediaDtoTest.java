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
class RecipeFavoriteMediaDtoTest {

  @Test
  @DisplayName("Should create RecipeFavoriteMediaDto with builder pattern")
  @Tag("standard-processing")
  void shouldCreateRecipeFavoriteMediaDtoWithBuilder() {
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
    RecipeFavoriteMediaDto dto = RecipeFavoriteMediaDto.builder()
        .mediaId(1L)
        .recipeId(2L)
        .userId("user123")
        .media(mediaDto)
        .build();

    // Then
    assertThat(dto.getMediaId()).isEqualTo(1L);
    assertThat(dto.getRecipeId()).isEqualTo(2L);
    assertThat(dto.getUserId()).isEqualTo("user123");
    assertThat(dto.getMedia()).isEqualTo(mediaDto);
  }

  @Test
  @DisplayName("Should create RecipeFavoriteMediaDto with minimal fields")
  @Tag("standard-processing")
  void shouldCreateRecipeFavoriteMediaDtoWithMinimalFields() {
    // When
    RecipeFavoriteMediaDto dto = RecipeFavoriteMediaDto.builder()
        .mediaId(1L)
        .recipeId(2L)
        .userId("user123")
        .build();

    // Then
    assertThat(dto.getMediaId()).isEqualTo(1L);
    assertThat(dto.getRecipeId()).isEqualTo(2L);
    assertThat(dto.getUserId()).isEqualTo("user123");
    assertThat(dto.getMedia()).isNull();
  }

  @Test
  @DisplayName("Should set and get all fields")
  @Tag("standard-processing")
  void shouldSetAndGetAllFields() {
    // Given
    RecipeFavoriteMediaDto dto = new RecipeFavoriteMediaDto();
    MediaDto mediaDto = MediaDto.builder().mediaId(5L).build();

    // When
    dto.setUserId("user456");
    dto.setRecipeId(6L);
    dto.setMediaId(7L);
    dto.setMedia(mediaDto);

    // Then
    assertThat(dto.getUserId()).isEqualTo("user456");
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
    RecipeFavoriteMediaDto dto1 = RecipeFavoriteMediaDto.builder()
        .mediaId(1L)
        .userId("user123")
        .recipeId(2L)
        .media(mediaDto)
        .build();
    RecipeFavoriteMediaDto dto2 = RecipeFavoriteMediaDto.builder()
        .mediaId(1L)
        .userId("user123")
        .recipeId(2L)
        .media(mediaDto)
        .build();

    // Then
    assertThat(dto1)
        .isEqualTo(dto2)
        .hasSameHashCodeAs(dto2);
    assertThat(dto1.toString()).contains("userId=user123");
  }

  @Test
  @DisplayName("Should handle null values correctly")
  @Tag("error-processing")
  void shouldHandleNullValuesCorrectly() {
    // When
    RecipeFavoriteMediaDto dto = new RecipeFavoriteMediaDto();

    // Then
    assertThat(dto.getMediaId()).isNull();
    assertThat(dto.getRecipeId()).isNull();
    assertThat(dto.getUserId()).isNull();
    assertThat(dto.getMedia()).isNull();
  }
}
