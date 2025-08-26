package com.recipe_manager.model.entity.media;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.enums.MediaType;
import com.recipe_manager.model.enums.ProcessingStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class RecipeMediaTest {

  private Recipe recipe;
  private Media media;
  private RecipeMediaId recipeMediaId;

  @BeforeEach
  void setUp() {
    recipe = Recipe.builder()
        .recipeId(1L)
        .userId(UUID.randomUUID())
        .title("Test Recipe")
        .build();

    media = Media.builder()
        .mediaId(2L)
        .userId(UUID.randomUUID())
        .mediaType(MediaType.IMAGE_JPEG)
        .mediaPath("https://example.com/image.jpg")
        .processingStatus(ProcessingStatus.COMPLETE)
        .build();

    recipeMediaId = RecipeMediaId.builder()
        .mediaId(2L)
        .recipeId(1L)
        .build();
  }

  @Test
  @DisplayName("Should create RecipeMedia with builder pattern")
  void shouldCreateRecipeMediaWithBuilder() {
    // When
    RecipeMedia recipeMedia = RecipeMedia.builder()
        .id(recipeMediaId)
        .media(media)
        .recipe(recipe)
        .build();

    // Then
    assertThat(recipeMedia.getId()).isEqualTo(recipeMediaId);
    assertThat(recipeMedia.getMedia()).isEqualTo(media);
    assertThat(recipeMedia.getRecipe()).isEqualTo(recipe);
  }

  @Test
  @DisplayName("Should create RecipeMedia with no-args constructor")
  void shouldCreateRecipeMediaWithNoArgsConstructor() {
    // When
    RecipeMedia recipeMedia = new RecipeMedia();

    // Then
    assertThat(recipeMedia.getId()).isNull();
    assertThat(recipeMedia.getMedia()).isNull();
    assertThat(recipeMedia.getRecipe()).isNull();
  }

  @Test
  @DisplayName("Should create RecipeMedia with all-args constructor")
  void shouldCreateRecipeMediaWithAllArgsConstructor() {
    // When
    RecipeMedia recipeMedia = new RecipeMedia(recipeMediaId, media, recipe);

    // Then
    assertThat(recipeMedia.getId()).isEqualTo(recipeMediaId);
    assertThat(recipeMedia.getMedia()).isEqualTo(media);
    assertThat(recipeMedia.getRecipe()).isEqualTo(recipe);
  }

  @Test
  @DisplayName("Should set and get all properties")
  void shouldSetAndGetAllProperties() {
    // Given
    RecipeMedia recipeMedia = new RecipeMedia();

    // When
    recipeMedia.setId(recipeMediaId);
    recipeMedia.setMedia(media);
    recipeMedia.setRecipe(recipe);

    // Then
    assertThat(recipeMedia.getId()).isEqualTo(recipeMediaId);
    assertThat(recipeMedia.getMedia()).isEqualTo(media);
    assertThat(recipeMedia.getRecipe()).isEqualTo(recipe);
  }

  @Test
  @DisplayName("Should implement equals and hashCode correctly")
  void shouldImplementEqualsAndHashCodeCorrectly() {
    // Given
    RecipeMedia recipeMedia1 = RecipeMedia.builder()
        .id(recipeMediaId)
        .media(media)
        .recipe(recipe)
        .build();

    RecipeMedia recipeMedia2 = RecipeMedia.builder()
        .id(recipeMediaId)
        .media(media)
        .recipe(recipe)
        .build();

    RecipeMediaId differentId = RecipeMediaId.builder()
        .mediaId(3L)
        .recipeId(2L)
        .build();

    RecipeMedia recipeMedia3 = RecipeMedia.builder()
        .id(differentId)
        .media(media)
        .recipe(recipe)
        .build();

    // Then
    assertThat(recipeMedia1)
        .isEqualTo(recipeMedia2)
        .hasSameHashCodeAs(recipeMedia2)
        .isNotEqualTo(recipeMedia3);
  }

  @Test
  @DisplayName("Should implement toString correctly")
  void shouldImplementToStringCorrectly() {
    // Given
    RecipeMedia recipeMedia = RecipeMedia.builder()
        .id(recipeMediaId)
        .media(media)
        .recipe(recipe)
        .build();

    // When
    String toString = recipeMedia.toString();

    // Then
    assertThat(toString)
        .contains("RecipeMedia")
        .contains("id=")
        .contains("media=")
        .contains("recipe=");
  }
}
