package com.recipe_manager.model.entity.media;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import com.recipe_manager.model.entity.ingredient.Ingredient;
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.enums.MediaType;
import com.recipe_manager.model.enums.ProcessingStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class IngredientMediaTest {

  private Recipe recipe;
  private Ingredient ingredient;
  private Media media;
  private IngredientMediaId ingredientMediaId;

  @BeforeEach
  void setUp() {
    recipe = Recipe.builder()
        .recipeId(1L)
        .userId(UUID.randomUUID())
        .title("Test Recipe")
        .build();

    ingredient = Ingredient.builder()
        .ingredientId(2L)
        .name("Test Ingredient")
        .build();

    media = Media.builder()
        .mediaId(3L)
        .userId(UUID.randomUUID())
        .mediaType(MediaType.IMAGE_JPEG)
        .mediaPath("https://example.com/image.jpg")
        .processingStatus(ProcessingStatus.COMPLETE)
        .build();

    ingredientMediaId = IngredientMediaId.builder()
        .recipeId(1L)
        .ingredientId(2L)
        .mediaId(3L)
        .build();
  }

  @Test
  @DisplayName("Should create IngredientMedia with builder pattern")
  void shouldCreateIngredientMediaWithBuilder() {
    // When
    IngredientMedia ingredientMedia = IngredientMedia.builder()
        .id(ingredientMediaId)
        .recipe(recipe)
        .ingredient(ingredient)
        .media(media)
        .build();

    // Then
    assertThat(ingredientMedia.getId()).isEqualTo(ingredientMediaId);
    assertThat(ingredientMedia.getRecipe()).isEqualTo(recipe);
    assertThat(ingredientMedia.getIngredient()).isEqualTo(ingredient);
    assertThat(ingredientMedia.getMedia()).isEqualTo(media);
  }

  @Test
  @DisplayName("Should create IngredientMedia with no-args constructor")
  void shouldCreateIngredientMediaWithNoArgsConstructor() {
    // When
    IngredientMedia ingredientMedia = new IngredientMedia();

    // Then
    assertThat(ingredientMedia.getId()).isNull();
    assertThat(ingredientMedia.getRecipe()).isNull();
    assertThat(ingredientMedia.getIngredient()).isNull();
    assertThat(ingredientMedia.getMedia()).isNull();
  }

  @Test
  @DisplayName("Should create IngredientMedia with all-args constructor")
  void shouldCreateIngredientMediaWithAllArgsConstructor() {
    // When
    IngredientMedia ingredientMedia = new IngredientMedia(ingredientMediaId, recipe, ingredient, media);

    // Then
    assertThat(ingredientMedia.getId()).isEqualTo(ingredientMediaId);
    assertThat(ingredientMedia.getRecipe()).isEqualTo(recipe);
    assertThat(ingredientMedia.getIngredient()).isEqualTo(ingredient);
    assertThat(ingredientMedia.getMedia()).isEqualTo(media);
  }

  @Test
  @DisplayName("Should set and get all properties")
  void shouldSetAndGetAllProperties() {
    // Given
    IngredientMedia ingredientMedia = new IngredientMedia();

    // When
    ingredientMedia.setId(ingredientMediaId);
    ingredientMedia.setRecipe(recipe);
    ingredientMedia.setIngredient(ingredient);
    ingredientMedia.setMedia(media);

    // Then
    assertThat(ingredientMedia.getId()).isEqualTo(ingredientMediaId);
    assertThat(ingredientMedia.getRecipe()).isEqualTo(recipe);
    assertThat(ingredientMedia.getIngredient()).isEqualTo(ingredient);
    assertThat(ingredientMedia.getMedia()).isEqualTo(media);
  }

  @Test
  @DisplayName("Should implement equals and hashCode correctly")
  void shouldImplementEqualsAndHashCodeCorrectly() {
    // Given
    IngredientMedia ingredientMedia1 = IngredientMedia.builder()
        .id(ingredientMediaId)
        .recipe(recipe)
        .ingredient(ingredient)
        .media(media)
        .build();

    IngredientMedia ingredientMedia2 = IngredientMedia.builder()
        .id(ingredientMediaId)
        .recipe(recipe)
        .ingredient(ingredient)
        .media(media)
        .build();

    IngredientMediaId differentId = IngredientMediaId.builder()
        .recipeId(2L)
        .ingredientId(3L)
        .mediaId(4L)
        .build();

    IngredientMedia ingredientMedia3 = IngredientMedia.builder()
        .id(differentId)
        .recipe(recipe)
        .ingredient(ingredient)
        .media(media)
        .build();

    // Then
    assertThat(ingredientMedia1)
        .isEqualTo(ingredientMedia2)
        .hasSameHashCodeAs(ingredientMedia2)
        .isNotEqualTo(ingredientMedia3);
  }

  @Test
  @DisplayName("Should implement toString correctly")
  void shouldImplementToStringCorrectly() {
    // Given
    IngredientMedia ingredientMedia = IngredientMedia.builder()
        .id(ingredientMediaId)
        .recipe(recipe)
        .ingredient(ingredient)
        .media(media)
        .build();

    // When
    String toString = ingredientMedia.toString();

    // Then
    assertThat(toString)
        .contains("IngredientMedia")
        .contains("id=")
        .contains("recipe=")
        .contains("ingredient=")
        .contains("media=");
  }
}
