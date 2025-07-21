package com.recipe_manager.model.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for CreateRecipeStepRequest.
 */
@Tag("unit")
class CreateRecipeStepRequestTest {

  private CreateRecipeStepRequest createRecipeStepRequest;

  @BeforeEach
  void setUp() {
    createRecipeStepRequest = CreateRecipeStepRequest.builder()
        .stepNumber(1)
        .instruction("Test instruction")
        .optional(false)
        .timerSeconds(300)
        .build();
  }

  @Test
  @DisplayName("Should create recipe step request with constructor")
  @Tag("standard-processing")
  void shouldCreateRecipeStepRequestWithConstructor() {
    // Given
    List<String> mediaUrls = Arrays.asList("https://example.com/image1.jpg", "https://example.com/image2.jpg");

    // When
    CreateRecipeStepRequest request = new CreateRecipeStepRequest(1, "Test instruction", false, 300, mediaUrls);

    // Then
    assertThat(request.getStepNumber()).isEqualTo(1);
    assertThat(request.getInstruction()).isEqualTo("Test instruction");
    assertThat(request.getOptional()).isFalse();
    assertThat(request.getTimerSeconds()).isEqualTo(300);
    assertThat(request.getMediaUrls()).isEqualTo(mediaUrls);
  }

  @Test
  @DisplayName("Should create recipe step request with builder")
  @Tag("standard-processing")
  void shouldCreateRecipeStepRequestWithBuilder() {
    // Then
    assertThat(createRecipeStepRequest.getStepNumber()).isEqualTo(1);
    assertThat(createRecipeStepRequest.getInstruction()).isEqualTo("Test instruction");
    assertThat(createRecipeStepRequest.getOptional()).isFalse();
    assertThat(createRecipeStepRequest.getTimerSeconds()).isEqualTo(300);
  }

  @Test
  @DisplayName("Should set and get all properties")
  @Tag("standard-processing")
  void shouldSetAndGetAllProperties() {
    // Given
    Integer newStepNumber = 2;
    String newInstruction = "Updated instruction";
    Boolean newOptional = true;
    Integer newTimerSeconds = 600;

    // When
    createRecipeStepRequest.setStepNumber(newStepNumber);
    createRecipeStepRequest.setInstruction(newInstruction);
    createRecipeStepRequest.setOptional(newOptional);
    createRecipeStepRequest.setTimerSeconds(newTimerSeconds);

    // Then
    assertThat(createRecipeStepRequest.getStepNumber()).isEqualTo(newStepNumber);
    assertThat(createRecipeStepRequest.getInstruction()).isEqualTo(newInstruction);
    assertThat(createRecipeStepRequest.getOptional()).isEqualTo(newOptional);
    assertThat(createRecipeStepRequest.getTimerSeconds()).isEqualTo(newTimerSeconds);
  }

  @Test
  @DisplayName("Should return unmodifiable list for mediaUrls")
  @Tag("standard-processing")
  void shouldReturnUnmodifiableListForMediaUrls() {
    // Given
    List<String> mediaUrls = new ArrayList<>();
    createRecipeStepRequest.setMediaUrls(mediaUrls);

    // When & Then
    assertThat(createRecipeStepRequest.getMediaUrls()).isUnmodifiable();
  }

  @Test
  @DisplayName("Should defensively copy mediaUrls list")
  @Tag("standard-processing")
  void shouldDefensivelyCopyMediaUrlsList() {
    // Given
    List<String> originalMediaUrls = new ArrayList<>();
    originalMediaUrls.add("https://example.com/image1.jpg");
    createRecipeStepRequest.setMediaUrls(originalMediaUrls);

    // When
    originalMediaUrls.add("https://example.com/image2.jpg");

    // Then
    assertThat(createRecipeStepRequest.getMediaUrls()).hasSize(1);
  }

  @Test
  @DisplayName("Should handle null mediaUrls in constructor")
  @Tag("error-processing")
  void shouldHandleNullMediaUrlsInConstructor() {
    // When
    CreateRecipeStepRequest request = new CreateRecipeStepRequest(1, "Test instruction", false, 300, null);

    // Then
    assertThat(request.getMediaUrls()).isEmpty();
  }

  @Test
  @DisplayName("Should handle null mediaUrls in setter")
  @Tag("error-processing")
  void shouldHandleNullMediaUrlsInSetter() {
    // Given
    createRecipeStepRequest.setMediaUrls(Arrays.asList("https://example.com/image1.jpg"));

    // When
    createRecipeStepRequest.setMediaUrls(null);

    // Then
    assertThat(createRecipeStepRequest.getMediaUrls()).isEmpty();
  }

  @Test
  @DisplayName("Should return true when comparing equal CreateRecipeStepRequest objects")
  @Tag("standard-processing")
  void shouldReturnTrueWhenComparingEqualCreateRecipeStepRequestObjects() {
    // Given
    CreateRecipeStepRequest request1 = new CreateRecipeStepRequest(1, "Test instruction", false, 300,
        new ArrayList<>());
    CreateRecipeStepRequest request2 = new CreateRecipeStepRequest(1, "Test instruction", false, 300,
        new ArrayList<>());

    // When & Then
    assertThat(request1).isEqualTo(request2);
    assertThat(request2).isEqualTo(request1);
  }

  @Test
  @DisplayName("Should return false when comparing different CreateRecipeStepRequest objects")
  @Tag("standard-processing")
  void shouldReturnFalseWhenComparingDifferentCreateRecipeStepRequestObjects() {
    // Given
    CreateRecipeStepRequest request1 = new CreateRecipeStepRequest(1, "Test instruction", false, 300,
        new ArrayList<>());
    CreateRecipeStepRequest request2 = new CreateRecipeStepRequest(2, "Test instruction", false, 300,
        new ArrayList<>());

    // When & Then
    assertThat(request1).isNotEqualTo(request2);
    assertThat(request2).isNotEqualTo(request1);
  }

  @Test
  @DisplayName("Should return false when comparing with null")
  @Tag("error-processing")
  void shouldReturnFalseWhenComparingWithNull() {
    // Given
    CreateRecipeStepRequest request = new CreateRecipeStepRequest(1, "Test instruction", false, 300, new ArrayList<>());

    // When & Then
    assertThat(request).isNotEqualTo(null);
  }

  @Test
  @DisplayName("Should return false when comparing with different type")
  @Tag("error-processing")
  void shouldReturnFalseWhenComparingWithDifferentType() {
    // Given
    CreateRecipeStepRequest request = new CreateRecipeStepRequest(1, "Test instruction", false, 300, new ArrayList<>());
    Object other = new Object();

    // When & Then
    assertThat(request).isNotEqualTo(other);
  }

  @Test
  @DisplayName("Should return true when comparing with itself")
  @Tag("standard-processing")
  void shouldReturnTrueWhenComparingWithItself() {
    // Given
    CreateRecipeStepRequest request = new CreateRecipeStepRequest(1, "Test instruction", false, 300, new ArrayList<>());

    // When & Then
    assertThat(request).isEqualTo(request);
  }

  @Test
  @DisplayName("Should have same hashCode for equal objects")
  @Tag("standard-processing")
  void shouldHaveSameHashCodeForEqualObjects() {
    // Given
    CreateRecipeStepRequest request1 = new CreateRecipeStepRequest(1, "Test instruction", false, 300,
        new ArrayList<>());
    CreateRecipeStepRequest request2 = new CreateRecipeStepRequest(1, "Test instruction", false, 300,
        new ArrayList<>());

    // When & Then
    assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
  }

  @Test
  @DisplayName("Should have different hashCode for different objects")
  @Tag("standard-processing")
  void shouldHaveDifferentHashCodeForDifferentObjects() {
    // Given
    CreateRecipeStepRequest request1 = new CreateRecipeStepRequest(1, "Test instruction", false, 300,
        new ArrayList<>());
    CreateRecipeStepRequest request2 = new CreateRecipeStepRequest(2, "Test instruction", false, 300,
        new ArrayList<>());

    // When & Then
    assertThat(request1.hashCode()).isNotEqualTo(request2.hashCode());
  }

  @Test
  @DisplayName("Should handle null values in equals")
  @Tag("error-processing")
  void shouldHandleNullValuesInEquals() {
    // Given
    CreateRecipeStepRequest request1 = new CreateRecipeStepRequest(1, null, false, 300, new ArrayList<>());
    CreateRecipeStepRequest request2 = new CreateRecipeStepRequest(1, null, false, 300, new ArrayList<>());
    CreateRecipeStepRequest request3 = new CreateRecipeStepRequest(1, "Test instruction", false, 300,
        new ArrayList<>());

    // When & Then
    assertThat(request1).isEqualTo(request2);
    assertThat(request1).isNotEqualTo(request3);
  }

  @Test
  @DisplayName("Should handle null values in hashCode")
  @Tag("error-processing")
  void shouldHandleNullValuesInHashCode() {
    // Given
    CreateRecipeStepRequest request = new CreateRecipeStepRequest(1, null, false, 300, new ArrayList<>());

    // When & Then
    assertThat(request.hashCode()).isNotNull();
  }

  @Test
  @DisplayName("Builder should defensively copy mediaUrls list")
  @Tag("standard-processing")
  void builderShouldDefensivelyCopyMediaUrlsList() {
    List<String> mediaUrls = new ArrayList<>();
    mediaUrls.add("https://example.com/image1.jpg");
    CreateRecipeStepRequest dto = CreateRecipeStepRequest.builder()
        .mediaUrls(mediaUrls)
        .build();
    mediaUrls.add("https://example.com/image2.jpg");
    assertThat(dto.getMediaUrls()).hasSize(1);
  }

  @Test
  @DisplayName("Builder should handle null mediaUrls as empty")
  @Tag("error-processing")
  void builderShouldHandleNullMediaUrlsAsEmpty() {
    CreateRecipeStepRequest dto = CreateRecipeStepRequest.builder()
        .mediaUrls(null)
        .build();
    assertThat(dto.getMediaUrls()).isEmpty();
  }

  @Test
  @DisplayName("toString should include key fields and not be null")
  @Tag("standard-processing")
  void toStringShouldIncludeKeyFields() {
    CreateRecipeStepRequest dto = CreateRecipeStepRequest.builder()
        .stepNumber(1)
        .instruction("Test instruction")
        .optional(false)
        .timerSeconds(300)
        .build();
    String str = dto.toString();
    assertThat(str).isNotNull();
    assertThat(str).contains("CreateRecipeStepRequest");
    assertThat(str).contains("Test instruction");
  }

  @Test
  @DisplayName("Should handle nulls for all fields")
  @Tag("error-processing")
  void shouldHandleNullsForAllFields() {
    CreateRecipeStepRequest dto = new CreateRecipeStepRequest(null, null, null, null, null);
    assertThat(dto.getStepNumber()).isNull();
    assertThat(dto.getInstruction()).isNull();
    assertThat(dto.getOptional()).isNull();
    assertThat(dto.getTimerSeconds()).isNull();
    assertThat(dto.getMediaUrls()).isEmpty();
  }
}
