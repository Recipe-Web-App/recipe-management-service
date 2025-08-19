package com.recipe_manager.unit_tests.model.dto.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.recipe_manager.model.dto.request.AddTagRequest;

/**
 * Unit tests for AddTagRequest DTO.
 */
@Tag("unit")
class AddTagRequestTest {

  @Test
  @DisplayName("Should create AddTagRequest with all fields")
  void shouldCreateAddTagRequestWithAllFields() {
    // Given
    String name = "Italian";

    // When
    AddTagRequest request = AddTagRequest.builder()
        .name(name)
        .build();

    // Then
    assertNotNull(request);
    assertEquals(name, request.getName());
  }

  @Test
  @DisplayName("Should create AddTagRequest using constructor")
  void shouldCreateAddTagRequestUsingConstructor() {
    // Given
    String name = "Vegetarian";

    // When
    AddTagRequest request = new AddTagRequest(name);

    // Then
    assertNotNull(request);
    assertEquals(name, request.getName());
  }

  @Test
  @DisplayName("Should create AddTagRequest using no-args constructor")
  void shouldCreateAddTagRequestUsingNoArgsConstructor() {
    // When
    AddTagRequest request = new AddTagRequest();

    // Then
    assertNotNull(request);
  }

  @Test
  @DisplayName("Should support setter methods")
  void shouldSupportSetterMethods() {
    // Given
    AddTagRequest request = new AddTagRequest();
    String name = "Spicy";

    // When
    request.setName(name);

    // Then
    assertEquals(name, request.getName());
  }

  @Test
  @DisplayName("Should implement equals and hashCode correctly")
  void shouldImplementEqualsAndHashCodeCorrectly() {
    // Given
    AddTagRequest request1 = AddTagRequest.builder()
        .name("Mediterranean")
        .build();

    AddTagRequest request2 = AddTagRequest.builder()
        .name("Mediterranean")
        .build();

    AddTagRequest request3 = AddTagRequest.builder()
        .name("Asian")
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
    AddTagRequest request = AddTagRequest.builder()
        .name("Quick & Easy")
        .build();

    // When
    String toString = request.toString();

    // Then
    assertNotNull(toString);
    assertEquals("AddTagRequest(name=Quick & Easy)", toString);
  }

  @Test
  @DisplayName("Should handle null values gracefully")
  void shouldHandleNullValuesGracefully() {
    // When
    AddTagRequest request = AddTagRequest.builder()
        .name(null)
        .build();

    // Then
    assertNotNull(request);
    assertEquals(null, request.getName());
  }

  @Test
  @DisplayName("Should handle empty string values")
  void shouldHandleEmptyStringValues() {
    // When
    AddTagRequest request = AddTagRequest.builder()
        .name("")
        .build();

    // Then
    assertNotNull(request);
    assertEquals("", request.getName());
  }
}
