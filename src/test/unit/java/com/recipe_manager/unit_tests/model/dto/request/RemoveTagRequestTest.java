package com.recipe_manager.unit_tests.model.dto.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.recipe_manager.model.dto.request.RemoveTagRequest;

/**
 * Unit tests for RemoveTagRequest DTO.
 */
@Tag("unit")
class RemoveTagRequestTest {

  @Test
  @DisplayName("Should create RemoveTagRequest with all fields")
  void shouldCreateRemoveTagRequestWithAllFields() {
    // Given
    String tagName = "Italian";

    // When
    RemoveTagRequest request = RemoveTagRequest.builder()
        .tagName(tagName)
        .build();

    // Then
    assertNotNull(request);
    assertEquals(tagName, request.getTagName());
  }

  @Test
  @DisplayName("Should create RemoveTagRequest using constructor")
  void shouldCreateRemoveTagRequestUsingConstructor() {
    // Given
    String tagName = "Vegetarian";

    // When
    RemoveTagRequest request = new RemoveTagRequest(tagName);

    // Then
    assertNotNull(request);
    assertEquals(tagName, request.getTagName());
  }

  @Test
  @DisplayName("Should create RemoveTagRequest using no-args constructor")
  void shouldCreateRemoveTagRequestUsingNoArgsConstructor() {
    // When
    RemoveTagRequest request = new RemoveTagRequest();

    // Then
    assertNotNull(request);
  }

  @Test
  @DisplayName("Should support setter methods")
  void shouldSupportSetterMethods() {
    // Given
    RemoveTagRequest request = new RemoveTagRequest();
    String tagName = "Spicy";

    // When
    request.setTagName(tagName);

    // Then
    assertEquals(tagName, request.getTagName());
  }

  @Test
  @DisplayName("Should implement equals and hashCode correctly")
  void shouldImplementEqualsAndHashCodeCorrectly() {
    // Given
    RemoveTagRequest request1 = RemoveTagRequest.builder()
        .tagName("Mediterranean")
        .build();

    RemoveTagRequest request2 = RemoveTagRequest.builder()
        .tagName("Mediterranean")
        .build();

    RemoveTagRequest request3 = RemoveTagRequest.builder()
        .tagName("Asian")
        .build();

    // Then
    assertEquals(request1, request2);
    assertEquals(request1.hashCode(), request2.hashCode());
    assertNotEquals(request1, request3);
    assertNotEquals(request1.hashCode(), request3.hashCode());
  }

  @Test
  @DisplayName("Should implement toString correctly")
  void shouldImplementToStringCorrectly() {
    // Given
    RemoveTagRequest request = RemoveTagRequest.builder()
        .tagName("Quick & Easy")
        .build();

    // When
    String toString = request.toString();

    // Then
    assertNotNull(toString);
    assertEquals("RemoveTagRequest(tagName=Quick & Easy)", toString);
  }

  @Test
  @DisplayName("Should handle null values gracefully")
  void shouldHandleNullValuesGracefully() {
    // When
    RemoveTagRequest request = RemoveTagRequest.builder()
        .tagName(null)
        .build();

    // Then
    assertNotNull(request);
    assertEquals(null, request.getTagName());
  }

  @Test
  @DisplayName("Should handle empty string values")
  void shouldHandleEmptyStringValues() {
    // When
    RemoveTagRequest request = RemoveTagRequest.builder()
        .tagName("")
        .build();

    // Then
    assertNotNull(request);
    assertEquals("", request.getTagName());
  }
}
