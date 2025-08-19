package com.recipe_manager.unit_tests.model.dto.response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.recipe_manager.model.dto.recipe.RecipeTagDto;
import com.recipe_manager.model.dto.response.TagResponse;

/**
 * Unit tests for TagResponse DTO.
 */
@Tag("unit")
class TagResponseTest {

  @Test
  @DisplayName("Should create TagResponse with all fields")
  void shouldCreateTagResponseWithAllFields() {
    // Given
    Long recipeId = 123L;
    List<RecipeTagDto> tags = Arrays.asList(
        RecipeTagDto.builder().tagId(1L).name("Italian").build(),
        RecipeTagDto.builder().tagId(2L).name("Vegetarian").build()
    );

    // When
    TagResponse response = TagResponse.builder()
        .recipeId(recipeId)
        .tags(tags)
        .build();

    // Then
    assertNotNull(response);
    assertEquals(recipeId, response.getRecipeId());
    assertEquals(tags, response.getTags());
    assertEquals(2, response.getTags().size());
  }

  @Test
  @DisplayName("Should create TagResponse using constructor")
  void shouldCreateTagResponseUsingConstructor() {
    // Given
    Long recipeId = 456L;
    List<RecipeTagDto> tags = Arrays.asList(
        RecipeTagDto.builder().tagId(3L).name("Spicy").build()
    );

    // When
    TagResponse response = new TagResponse(recipeId, tags);

    // Then
    assertNotNull(response);
    assertEquals(recipeId, response.getRecipeId());
    assertEquals(tags, response.getTags());
    assertEquals(1, response.getTags().size());
  }

  @Test
  @DisplayName("Should create TagResponse using no-args constructor")
  void shouldCreateTagResponseUsingNoArgsConstructor() {
    // When
    TagResponse response = new TagResponse();

    // Then
    assertNotNull(response);
  }

  @Test
  @DisplayName("Should support setter methods")
  void shouldSupportSetterMethods() {
    // Given
    TagResponse response = new TagResponse();
    Long recipeId = 789L;
    List<RecipeTagDto> tags = Arrays.asList(
        RecipeTagDto.builder().tagId(4L).name("Mediterranean").build()
    );

    // When
    response.setRecipeId(recipeId);
    response.setTags(tags);

    // Then
    assertEquals(recipeId, response.getRecipeId());
    assertEquals(tags, response.getTags());
  }

  @Test
  @DisplayName("Should implement equals and hashCode correctly")
  void shouldImplementEqualsAndHashCodeCorrectly() {
    // Given
    List<RecipeTagDto> tags = Arrays.asList(
        RecipeTagDto.builder().tagId(1L).name("Asian").build()
    );

    TagResponse response1 = TagResponse.builder()
        .recipeId(100L)
        .tags(tags)
        .build();

    TagResponse response2 = TagResponse.builder()
        .recipeId(100L)
        .tags(tags)
        .build();

    TagResponse response3 = TagResponse.builder()
        .recipeId(200L)
        .tags(tags)
        .build();

    // Then
    assertEquals(response1, response2);
    assertEquals(response1.hashCode(), response2.hashCode());
    assertNotEquals(response1, response3);
    assertNotEquals(response1.hashCode(), response3.hashCode());
  }

  @Test
  @DisplayName("Should implement toString correctly")
  void shouldImplementToStringCorrectly() {
    // Given
    List<RecipeTagDto> tags = Arrays.asList(
        RecipeTagDto.builder().tagId(1L).name("Quick").build()
    );
    TagResponse response = TagResponse.builder()
        .recipeId(999L)
        .tags(tags)
        .build();

    // When
    String toString = response.toString();

    // Then
    assertNotNull(toString);
    assertEquals("TagResponse(recipeId=999, tags=[RecipeTagDto(tagId=1, name=Quick)])", toString);
  }

  @Test
  @DisplayName("Should handle empty tag list")
  void shouldHandleEmptyTagList() {
    // Given
    Long recipeId = 123L;
    List<RecipeTagDto> emptyTags = Collections.emptyList();

    // When
    TagResponse response = TagResponse.builder()
        .recipeId(recipeId)
        .tags(emptyTags)
        .build();

    // Then
    assertNotNull(response);
    assertEquals(recipeId, response.getRecipeId());
    assertEquals(emptyTags, response.getTags());
    assertEquals(0, response.getTags().size());
  }

  @Test
  @DisplayName("Should handle null values gracefully")
  void shouldHandleNullValuesGracefully() {
    // When
    TagResponse response = TagResponse.builder()
        .recipeId(null)
        .tags(null)
        .build();

    // Then
    assertNotNull(response);
    assertEquals(null, response.getRecipeId());
    assertEquals(null, response.getTags());
  }

  @Test
  @DisplayName("Should handle large tag lists")
  void shouldHandleLargeTagLists() {
    // Given
    Long recipeId = 555L;
    List<RecipeTagDto> largeTags = Arrays.asList(
        RecipeTagDto.builder().tagId(1L).name("Italian").build(),
        RecipeTagDto.builder().tagId(2L).name("Vegetarian").build(),
        RecipeTagDto.builder().tagId(3L).name("Spicy").build(),
        RecipeTagDto.builder().tagId(4L).name("Quick").build(),
        RecipeTagDto.builder().tagId(5L).name("Healthy").build()
    );

    // When
    TagResponse response = TagResponse.builder()
        .recipeId(recipeId)
        .tags(largeTags)
        .build();

    // Then
    assertNotNull(response);
    assertEquals(recipeId, response.getRecipeId());
    assertEquals(largeTags, response.getTags());
    assertEquals(5, response.getTags().size());
  }
}
