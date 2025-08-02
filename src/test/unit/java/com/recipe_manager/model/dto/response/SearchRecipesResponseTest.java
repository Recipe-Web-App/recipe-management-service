package com.recipe_manager.model.dto.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import com.recipe_manager.model.dto.recipe.RecipeDto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class SearchRecipesResponseTest {

  @Test
  @DisplayName("Builder sets all fields correctly")
  @Tag("standard-processing")
  void builderSetsAllFields() {
    // Given
    RecipeDto recipe1 = RecipeDto.builder()
        .recipeId(1L)
        .title("Test Recipe 1")
        .build();

    RecipeDto recipe2 = RecipeDto.builder()
        .recipeId(2L)
        .title("Test Recipe 2")
        .build();

    List<RecipeDto> recipes = Arrays.asList(recipe1, recipe2);

    // When
    SearchRecipesResponse response = SearchRecipesResponse.builder()
        .recipes(recipes)
        .page(1)
        .size(10)
        .totalElements(25L)
        .totalPages(3)
        .first(false)
        .last(false)
        .numberOfElements(2)
        .empty(false)
        .build();

    // Then
    assertThat(response.getRecipes()).isSameAs(recipes);
    assertThat(response.getPage()).isEqualTo(1);
    assertThat(response.getSize()).isEqualTo(10);
    assertThat(response.getTotalElements()).isEqualTo(25L);
    assertThat(response.getTotalPages()).isEqualTo(3);
    assertThat(response.isFirst()).isFalse();
    assertThat(response.isLast()).isFalse();
    assertThat(response.getNumberOfElements()).isEqualTo(2);
    assertThat(response.isEmpty()).isFalse();
  }

  @Test
  @DisplayName("Setters and getters work for all fields")
  @Tag("standard-processing")
  void settersAndGettersWork() {
    // Given
    SearchRecipesResponse response = new SearchRecipesResponse();
    List<RecipeDto> recipes = Arrays.asList(
        RecipeDto.builder().recipeId(1L).title("Recipe").build());

    // When
    response.setRecipes(recipes);
    response.setPage(2);
    response.setSize(5);
    response.setTotalElements(15L);
    response.setTotalPages(3);
    response.setFirst(false);
    response.setLast(true);
    response.setNumberOfElements(5);
    response.setEmpty(false);

    // Then
    assertThat(response.getRecipes()).isSameAs(recipes);
    assertThat(response.getPage()).isEqualTo(2);
    assertThat(response.getSize()).isEqualTo(5);
    assertThat(response.getTotalElements()).isEqualTo(15L);
    assertThat(response.getTotalPages()).isEqualTo(3);
    assertThat(response.isFirst()).isFalse();
    assertThat(response.isLast()).isTrue();
    assertThat(response.getNumberOfElements()).isEqualTo(5);
    assertThat(response.isEmpty()).isFalse();
  }

  @Test
  @DisplayName("NoArgsConstructor creates instance with default values")
  @Tag("standard-processing")
  void noArgsConstructorCreatesInstanceWithDefaultValues() {
    // When
    SearchRecipesResponse response = new SearchRecipesResponse();

    // Then
    assertThat(response.getRecipes()).isNull();
    assertThat(response.getPage()).isEqualTo(0);
    assertThat(response.getSize()).isEqualTo(0);
    assertThat(response.getTotalElements()).isEqualTo(0L);
    assertThat(response.getTotalPages()).isEqualTo(0);
    assertThat(response.isFirst()).isFalse();
    assertThat(response.isLast()).isFalse();
    assertThat(response.getNumberOfElements()).isEqualTo(0);
    assertThat(response.isEmpty()).isFalse();
  }

  @Test
  @DisplayName("AllArgsConstructor sets all fields correctly")
  @Tag("standard-processing")
  void allArgsConstructorSetsAllFields() {
    // Given
    List<RecipeDto> recipes = Arrays.asList(
        RecipeDto.builder().recipeId(1L).title("Recipe 1").build(),
        RecipeDto.builder().recipeId(2L).title("Recipe 2").build());

    // When
    SearchRecipesResponse response = new SearchRecipesResponse(
        recipes, 0, 20, 2L, 1, true, true, 2, false);

    // Then
    assertThat(response.getRecipes()).isSameAs(recipes);
    assertThat(response.getPage()).isEqualTo(0);
    assertThat(response.getSize()).isEqualTo(20);
    assertThat(response.getTotalElements()).isEqualTo(2L);
    assertThat(response.getTotalPages()).isEqualTo(1);
    assertThat(response.isFirst()).isTrue();
    assertThat(response.isLast()).isTrue();
    assertThat(response.getNumberOfElements()).isEqualTo(2);
    assertThat(response.isEmpty()).isFalse();
  }

  @Test
  @DisplayName("Equals and hashCode work correctly for same content")
  @Tag("standard-processing")
  void equalsAndHashCodeWorkForSameContent() {
    // Given
    List<RecipeDto> recipes = Arrays.asList(
        RecipeDto.builder().recipeId(1L).title("Recipe").build());

    SearchRecipesResponse response1 = SearchRecipesResponse.builder()
        .recipes(recipes)
        .page(0)
        .size(10)
        .totalElements(1L)
        .totalPages(1)
        .first(true)
        .last(true)
        .numberOfElements(1)
        .empty(false)
        .build();

    SearchRecipesResponse response2 = SearchRecipesResponse.builder()
        .recipes(recipes)
        .page(0)
        .size(10)
        .totalElements(1L)
        .totalPages(1)
        .first(true)
        .last(true)
        .numberOfElements(1)
        .empty(false)
        .build();

    // Then
    assertThat(response1).isEqualTo(response2);
    assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
  }

  @Test
  @DisplayName("Equals returns false for different content")
  @Tag("standard-processing")
  void equalsReturnsFalseForDifferentContent() {
    // Given
    SearchRecipesResponse response1 = SearchRecipesResponse.builder()
        .page(0)
        .size(10)
        .build();

    SearchRecipesResponse response2 = SearchRecipesResponse.builder()
        .page(1)
        .size(10)
        .build();

    // Then
    assertThat(response1).isNotEqualTo(response2);
  }

  @Test
  @DisplayName("ToString includes all field information")
  @Tag("standard-processing")
  void toStringIncludesAllFieldInformation() {
    // Given
    List<RecipeDto> recipes = Arrays.asList(
        RecipeDto.builder().recipeId(1L).title("Test Recipe").build());

    SearchRecipesResponse response = SearchRecipesResponse.builder()
        .recipes(recipes)
        .page(1)
        .size(10)
        .totalElements(25L)
        .totalPages(3)
        .first(false)
        .last(false)
        .numberOfElements(10)
        .empty(false)
        .build();

    // When
    String toString = response.toString();

    // Then
    assertThat(toString).contains("SearchRecipesResponse");
    assertThat(toString).contains("page=1");
    assertThat(toString).contains("size=10");
    assertThat(toString).contains("totalElements=25");
    assertThat(toString).contains("totalPages=3");
    assertThat(toString).contains("first=false");
    assertThat(toString).contains("last=false");
    assertThat(toString).contains("numberOfElements=10");
    assertThat(toString).contains("empty=false");
  }

  @Test
  @DisplayName("Builder allows partial field setting")
  @Tag("standard-processing")
  void builderAllowsPartialFieldSetting() {
    // When
    SearchRecipesResponse response = SearchRecipesResponse.builder()
        .page(2)
        .totalElements(50L)
        .build();

    // Then
    assertThat(response.getPage()).isEqualTo(2);
    assertThat(response.getTotalElements()).isEqualTo(50L);
    assertThat(response.getRecipes()).isNull();
    assertThat(response.getSize()).isEqualTo(0);
    assertThat(response.getTotalPages()).isEqualTo(0);
  }

  @Test
  @DisplayName("Can represent empty search results")
  @Tag("standard-processing")
  void canRepresentEmptySearchResults() {
    // When
    SearchRecipesResponse response = SearchRecipesResponse.builder()
        .recipes(Arrays.asList())
        .page(0)
        .size(10)
        .totalElements(0L)
        .totalPages(0)
        .first(true)
        .last(true)
        .numberOfElements(0)
        .empty(true)
        .build();

    // Then
    assertThat(response.getRecipes()).isEmpty();
    assertThat(response.getTotalElements()).isEqualTo(0L);
    assertThat(response.getTotalPages()).isEqualTo(0);
    assertThat(response.isFirst()).isTrue();
    assertThat(response.isLast()).isTrue();
    assertThat(response.getNumberOfElements()).isEqualTo(0);
    assertThat(response.isEmpty()).isTrue();
  }

  @Test
  @DisplayName("Can represent first page scenario")
  @Tag("standard-processing")
  void canRepresentFirstPageScenario() {
    // When
    SearchRecipesResponse response = SearchRecipesResponse.builder()
        .page(0)
        .size(10)
        .totalElements(25L)
        .totalPages(3)
        .first(true)
        .last(false)
        .numberOfElements(10)
        .empty(false)
        .build();

    // Then
    assertThat(response.getPage()).isEqualTo(0);
    assertThat(response.isFirst()).isTrue();
    assertThat(response.isLast()).isFalse();
    assertThat(response.getTotalPages()).isEqualTo(3);
  }

  @Test
  @DisplayName("Can represent last page scenario")
  @Tag("standard-processing")
  void canRepresentLastPageScenario() {
    // When
    SearchRecipesResponse response = SearchRecipesResponse.builder()
        .page(2)
        .size(10)
        .totalElements(25L)
        .totalPages(3)
        .first(false)
        .last(true)
        .numberOfElements(5)
        .empty(false)
        .build();

    // Then
    assertThat(response.getPage()).isEqualTo(2);
    assertThat(response.isFirst()).isFalse();
    assertThat(response.isLast()).isTrue();
    assertThat(response.getNumberOfElements()).isEqualTo(5); // Less than size on last page
  }
}
