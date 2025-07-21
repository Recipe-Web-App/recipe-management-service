package com.recipe_manager.model.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.recipe_manager.model.enums.IngredientUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for CreateRecipeIngredientRequest.
 */
@Tag("unit")
class CreateRecipeIngredientRequestTest {

  private CreateRecipeIngredientRequest createRecipeIngredientRequest;

  @BeforeEach
  void setUp() {
    createRecipeIngredientRequest = CreateRecipeIngredientRequest.builder()
        .ingredientName("Test Ingredient")
        .ingredientId(1L)
        .quantity(BigDecimal.valueOf(2.5))
        .unit(IngredientUnit.CUP)
        .isOptional(false)
        .notes("Test notes")
        .build();
  }

  @Test
  @DisplayName("Should create recipe ingredient request with constructor")
  @Tag("standard-processing")
  void shouldCreateRecipeIngredientRequestWithConstructor() {
    // Given
    List<String> mediaUrls = Arrays.asList("https://example.com/image1.jpg", "https://example.com/image2.jpg");

    // When
    CreateRecipeIngredientRequest request = new CreateRecipeIngredientRequest("Test Ingredient", 1L,
        BigDecimal.valueOf(2.5), IngredientUnit.CUP, false, "Test notes", mediaUrls);

    // Then
    assertThat(request.getIngredientName()).isEqualTo("Test Ingredient");
    assertThat(request.getIngredientId()).isEqualTo(1L);
    assertThat(request.getQuantity()).isEqualTo(BigDecimal.valueOf(2.5));
    assertThat(request.getUnit()).isEqualTo(IngredientUnit.CUP);
    assertThat(request.getIsOptional()).isFalse();
    assertThat(request.getNotes()).isEqualTo("Test notes");
    assertThat(request.getMediaUrls()).isEqualTo(mediaUrls);
  }

  @Test
  @DisplayName("Should create recipe ingredient request with builder")
  @Tag("standard-processing")
  void shouldCreateRecipeIngredientRequestWithBuilder() {
    // Then
    assertThat(createRecipeIngredientRequest.getIngredientName()).isEqualTo("Test Ingredient");
    assertThat(createRecipeIngredientRequest.getIngredientId()).isEqualTo(1L);
    assertThat(createRecipeIngredientRequest.getQuantity()).isEqualTo(BigDecimal.valueOf(2.5));
    assertThat(createRecipeIngredientRequest.getUnit()).isEqualTo(IngredientUnit.CUP);
    assertThat(createRecipeIngredientRequest.getIsOptional()).isFalse();
    assertThat(createRecipeIngredientRequest.getNotes()).isEqualTo("Test notes");
  }

  @Test
  @DisplayName("Should set and get all properties")
  @Tag("standard-processing")
  void shouldSetAndGetAllProperties() {
    // Given
    String newIngredientName = "Updated Ingredient";
    Long newIngredientId = 2L;
    BigDecimal newQuantity = BigDecimal.valueOf(3.0);
    IngredientUnit newUnit = IngredientUnit.TBSP;
    Boolean newIsOptional = true;
    String newNotes = "Updated notes";

    // When
    createRecipeIngredientRequest.setIngredientName(newIngredientName);
    createRecipeIngredientRequest.setIngredientId(newIngredientId);
    createRecipeIngredientRequest.setQuantity(newQuantity);
    createRecipeIngredientRequest.setUnit(newUnit);
    createRecipeIngredientRequest.setIsOptional(newIsOptional);
    createRecipeIngredientRequest.setNotes(newNotes);

    // Then
    assertThat(createRecipeIngredientRequest.getIngredientName()).isEqualTo(newIngredientName);
    assertThat(createRecipeIngredientRequest.getIngredientId()).isEqualTo(newIngredientId);
    assertThat(createRecipeIngredientRequest.getQuantity()).isEqualTo(newQuantity);
    assertThat(createRecipeIngredientRequest.getUnit()).isEqualTo(newUnit);
    assertThat(createRecipeIngredientRequest.getIsOptional()).isEqualTo(newIsOptional);
    assertThat(createRecipeIngredientRequest.getNotes()).isEqualTo(newNotes);
  }

  @Test
  @DisplayName("Should return unmodifiable list for mediaUrls")
  @Tag("standard-processing")
  void shouldReturnUnmodifiableListForMediaUrls() {
    // Given
    List<String> mediaUrls = new ArrayList<>();
    createRecipeIngredientRequest.setMediaUrls(mediaUrls);

    // When & Then
    assertThat(createRecipeIngredientRequest.getMediaUrls()).isUnmodifiable();
  }

  @Test
  @DisplayName("Should defensively copy mediaUrls list")
  @Tag("standard-processing")
  void shouldDefensivelyCopyMediaUrlsList() {
    // Given
    List<String> originalMediaUrls = new ArrayList<>();
    originalMediaUrls.add("https://example.com/image1.jpg");
    createRecipeIngredientRequest.setMediaUrls(originalMediaUrls);

    // When
    originalMediaUrls.add("https://example.com/image2.jpg");

    // Then
    assertThat(createRecipeIngredientRequest.getMediaUrls()).hasSize(1);
  }

  @Test
  @DisplayName("Should handle null mediaUrls in constructor")
  @Tag("error-processing")
  void shouldHandleNullMediaUrlsInConstructor() {
    // When
    CreateRecipeIngredientRequest request = new CreateRecipeIngredientRequest("Test Ingredient", 1L,
        BigDecimal.valueOf(2.5), IngredientUnit.CUP, false, "Test notes", null);

    // Then
    assertThat(request.getMediaUrls()).isEmpty();
  }

  @Test
  @DisplayName("Should handle null mediaUrls in setter")
  @Tag("error-processing")
  void shouldHandleNullMediaUrlsInSetter() {
    // Given
    createRecipeIngredientRequest.setMediaUrls(Arrays.asList("https://example.com/image1.jpg"));

    // When
    createRecipeIngredientRequest.setMediaUrls(null);

    // Then
    assertThat(createRecipeIngredientRequest.getMediaUrls()).isEmpty();
  }

  @Test
  @DisplayName("Should return true when comparing equal CreateRecipeIngredientRequest objects")
  @Tag("standard-processing")
  void shouldReturnTrueWhenComparingEqualCreateRecipeIngredientRequestObjects() {
    // Given
    CreateRecipeIngredientRequest request1 = new CreateRecipeIngredientRequest("Test Ingredient", 1L,
        BigDecimal.valueOf(2.5), IngredientUnit.CUP, false, "Test notes", new ArrayList<>());
    CreateRecipeIngredientRequest request2 = new CreateRecipeIngredientRequest("Test Ingredient", 1L,
        BigDecimal.valueOf(2.5), IngredientUnit.CUP, false, "Test notes", new ArrayList<>());

    // When & Then
    assertThat(request1).isEqualTo(request2);
    assertThat(request2).isEqualTo(request1);
  }

  @Test
  @DisplayName("Should return false when comparing different CreateRecipeIngredientRequest objects")
  @Tag("standard-processing")
  void shouldReturnFalseWhenComparingDifferentCreateRecipeIngredientRequestObjects() {
    // Given
    CreateRecipeIngredientRequest request1 = new CreateRecipeIngredientRequest("Test Ingredient", 1L,
        BigDecimal.valueOf(2.5), IngredientUnit.CUP, false, "Test notes", new ArrayList<>());
    CreateRecipeIngredientRequest request2 = new CreateRecipeIngredientRequest("Different Ingredient", 1L,
        BigDecimal.valueOf(2.5), IngredientUnit.CUP, false, "Test notes", new ArrayList<>());

    // When & Then
    assertThat(request1).isNotEqualTo(request2);
    assertThat(request2).isNotEqualTo(request1);
  }

  @Test
  @DisplayName("Should return false when comparing with null")
  @Tag("error-processing")
  void shouldReturnFalseWhenComparingWithNull() {
    // Given
    CreateRecipeIngredientRequest request = new CreateRecipeIngredientRequest("Test Ingredient", 1L,
        BigDecimal.valueOf(2.5), IngredientUnit.CUP, false, "Test notes", new ArrayList<>());

    // When & Then
    assertThat(request).isNotEqualTo(null);
  }

  @Test
  @DisplayName("Should return false when comparing with different type")
  @Tag("error-processing")
  void shouldReturnFalseWhenComparingWithDifferentType() {
    // Given
    CreateRecipeIngredientRequest request = new CreateRecipeIngredientRequest("Test Ingredient", 1L,
        BigDecimal.valueOf(2.5), IngredientUnit.CUP, false, "Test notes", new ArrayList<>());
    Object other = new Object();

    // When & Then
    assertThat(request).isNotEqualTo(other);
  }

  @Test
  @DisplayName("Should return true when comparing with itself")
  @Tag("standard-processing")
  void shouldReturnTrueWhenComparingWithItself() {
    // Given
    CreateRecipeIngredientRequest request = new CreateRecipeIngredientRequest("Test Ingredient", 1L,
        BigDecimal.valueOf(2.5), IngredientUnit.CUP, false, "Test notes", new ArrayList<>());

    // When & Then
    assertThat(request).isEqualTo(request);
  }

  @Test
  @DisplayName("Should have same hashCode for equal objects")
  @Tag("standard-processing")
  void shouldHaveSameHashCodeForEqualObjects() {
    // Given
    CreateRecipeIngredientRequest request1 = new CreateRecipeIngredientRequest("Test Ingredient", 1L,
        BigDecimal.valueOf(2.5), IngredientUnit.CUP, false, "Test notes", new ArrayList<>());
    CreateRecipeIngredientRequest request2 = new CreateRecipeIngredientRequest("Test Ingredient", 1L,
        BigDecimal.valueOf(2.5), IngredientUnit.CUP, false, "Test notes", new ArrayList<>());

    // When & Then
    assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
  }

  @Test
  @DisplayName("Should have different hashCode for different objects")
  @Tag("standard-processing")
  void shouldHaveDifferentHashCodeForDifferentObjects() {
    // Given
    CreateRecipeIngredientRequest request1 = new CreateRecipeIngredientRequest("Test Ingredient", 1L,
        BigDecimal.valueOf(2.5), IngredientUnit.CUP, false, "Test notes", new ArrayList<>());
    CreateRecipeIngredientRequest request2 = new CreateRecipeIngredientRequest("Different Ingredient", 1L,
        BigDecimal.valueOf(2.5), IngredientUnit.CUP, false, "Test notes", new ArrayList<>());

    // When & Then
    assertThat(request1.hashCode()).isNotEqualTo(request2.hashCode());
  }

  @Test
  @DisplayName("Should handle null values in equals")
  @Tag("error-processing")
  void shouldHandleNullValuesInEquals() {
    // Given
    CreateRecipeIngredientRequest request1 = new CreateRecipeIngredientRequest(null, 1L,
        BigDecimal.valueOf(2.5), IngredientUnit.CUP, false, "Test notes", new ArrayList<>());
    CreateRecipeIngredientRequest request2 = new CreateRecipeIngredientRequest(null, 1L,
        BigDecimal.valueOf(2.5), IngredientUnit.CUP, false, "Test notes", new ArrayList<>());
    CreateRecipeIngredientRequest request3 = new CreateRecipeIngredientRequest("Test Ingredient", 1L,
        BigDecimal.valueOf(2.5), IngredientUnit.CUP, false, "Test notes", new ArrayList<>());

    // When & Then
    assertThat(request1).isEqualTo(request2);
    assertThat(request1).isNotEqualTo(request3);
  }

  @Test
  @DisplayName("Should handle null values in hashCode")
  @Tag("error-processing")
  void shouldHandleNullValuesInHashCode() {
    // Given
    CreateRecipeIngredientRequest request = new CreateRecipeIngredientRequest(null, 1L,
        BigDecimal.valueOf(2.5), IngredientUnit.CUP, false, "Test notes", new ArrayList<>());

    // When & Then
    assertThat(request.hashCode()).isNotNull();
  }

  @Test
  @DisplayName("Builder should defensively copy mediaUrls list")
  @Tag("standard-processing")
  void builderShouldDefensivelyCopyMediaUrlsList() {
    List<String> mediaUrls = new ArrayList<>();
    mediaUrls.add("https://example.com/image1.jpg");
    CreateRecipeIngredientRequest dto = CreateRecipeIngredientRequest.builder()
        .mediaUrls(mediaUrls)
        .build();
    mediaUrls.add("https://example.com/image2.jpg");
    assertThat(dto.getMediaUrls()).hasSize(1);
  }

  @Test
  @DisplayName("Builder should handle null mediaUrls as empty")
  @Tag("error-processing")
  void builderShouldHandleNullMediaUrlsAsEmpty() {
    CreateRecipeIngredientRequest dto = CreateRecipeIngredientRequest.builder()
        .mediaUrls(null)
        .build();
    assertThat(dto.getMediaUrls()).isEmpty();
  }

  @Test
  @DisplayName("toString should include key fields and not be null")
  @Tag("standard-processing")
  void toStringShouldIncludeKeyFields() {
    CreateRecipeIngredientRequest dto = CreateRecipeIngredientRequest.builder()
        .ingredientName("Test Ingredient")
        .ingredientId(1L)
        .quantity(BigDecimal.valueOf(2.5))
        .unit(IngredientUnit.CUP)
        .isOptional(false)
        .notes("Test notes")
        .build();
    String str = dto.toString();
    assertThat(str).isNotNull();
    assertThat(str).contains("CreateRecipeIngredientRequest");
    assertThat(str).contains("Test Ingredient");
  }

  @Test
  @DisplayName("Should handle nulls for all fields")
  @Tag("error-processing")
  void shouldHandleNullsForAllFields() {
    CreateRecipeIngredientRequest dto = new CreateRecipeIngredientRequest(null, null, null, null, null, null, null);
    assertThat(dto.getIngredientName()).isNull();
    assertThat(dto.getIngredientId()).isNull();
    assertThat(dto.getQuantity()).isNull();
    assertThat(dto.getUnit()).isNull();
    assertThat(dto.getIsOptional()).isNull();
    assertThat(dto.getNotes()).isNull();
    assertThat(dto.getMediaUrls()).isEmpty();
  }
}
