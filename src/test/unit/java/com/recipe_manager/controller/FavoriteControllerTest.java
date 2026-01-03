package com.recipe_manager.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.recipe_manager.model.dto.collection.CollectionFavoriteDto;
import com.recipe_manager.model.dto.recipe.RecipeDto;
import com.recipe_manager.model.dto.response.CollectionDto;
import com.recipe_manager.model.dto.recipe.RecipeFavoriteDto;
import com.recipe_manager.model.dto.response.SearchRecipesResponse;
import com.recipe_manager.service.FavoriteService;

/** Unit tests for FavoriteController. */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
class FavoriteControllerTest {

  @Mock private FavoriteService favoriteService;

  private FavoriteController favoriteController;

  private UUID testUserId;
  private Long testRecipeId;

  @BeforeEach
  void setUp() {
    favoriteController = new FavoriteController(favoriteService);
    testUserId = UUID.randomUUID();
    testRecipeId = 100L;
  }

  // ==================== getUserFavorites Tests ====================

  @Test
  @DisplayName("Should get user favorites successfully with default pagination")
  @Tag("standard-processing")
  void shouldGetUserFavoritesSuccessfullyWithDefaultPagination() throws AccessDeniedException {
    // Given
    Pageable pageable = PageRequest.of(0, 20);
    List<RecipeDto> recipes = Arrays.asList(createTestRecipeDto(101L), createTestRecipeDto(102L));
    SearchRecipesResponse response = createSearchResponse(recipes, pageable, 2);
    ResponseEntity<SearchRecipesResponse> expectedResponse = ResponseEntity.ok(response);

    when(favoriteService.getUserFavorites(eq(testUserId), any(Pageable.class)))
        .thenReturn(expectedResponse);

    // When
    ResponseEntity<SearchRecipesResponse> result =
        favoriteController.getUserFavorites(testUserId, pageable);

    // Then
    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(result.getBody()).isNotNull();
    assertThat(result.getBody().getRecipes()).hasSize(2);
    assertThat(result.getBody().getTotalElements()).isEqualTo(2);

    verify(favoriteService).getUserFavorites(testUserId, pageable);
  }

  @Test
  @DisplayName("Should get own favorites when userId is null")
  @Tag("standard-processing")
  void shouldGetOwnFavoritesWhenUserIdIsNull() throws AccessDeniedException {
    // Given
    Pageable pageable = PageRequest.of(0, 20);
    List<RecipeDto> recipes = Arrays.asList(createTestRecipeDto(101L));
    SearchRecipesResponse response = createSearchResponse(recipes, pageable, 1);
    ResponseEntity<SearchRecipesResponse> expectedResponse = ResponseEntity.ok(response);

    when(favoriteService.getUserFavorites(eq(null), any(Pageable.class)))
        .thenReturn(expectedResponse);

    // When
    ResponseEntity<SearchRecipesResponse> result =
        favoriteController.getUserFavorites(null, pageable);

    // Then
    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(result.getBody()).isNotNull();
    assertThat(result.getBody().getRecipes()).hasSize(1);

    verify(favoriteService).getUserFavorites(null, pageable);
  }

  @Test
  @DisplayName("Should get empty favorites list")
  @Tag("standard-processing")
  void shouldGetEmptyFavoritesList() throws AccessDeniedException {
    // Given
    Pageable pageable = PageRequest.of(0, 20);
    SearchRecipesResponse response = createSearchResponse(Collections.emptyList(), pageable, 0);
    ResponseEntity<SearchRecipesResponse> expectedResponse = ResponseEntity.ok(response);

    when(favoriteService.getUserFavorites(eq(testUserId), any(Pageable.class)))
        .thenReturn(expectedResponse);

    // When
    ResponseEntity<SearchRecipesResponse> result =
        favoriteController.getUserFavorites(testUserId, pageable);

    // Then
    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(result.getBody()).isNotNull();
    assertThat(result.getBody().getRecipes()).isEmpty();
    assertThat(result.getBody().getTotalElements()).isZero();
  }

  @Test
  @DisplayName("Should pass custom pagination parameters to service")
  @Tag("standard-processing")
  void shouldPassCustomPaginationParametersToService() throws AccessDeniedException {
    // Given
    Pageable pageable = PageRequest.of(2, 5); // Third page, size 5
    List<RecipeDto> recipes = Arrays.asList(createTestRecipeDto(101L));
    SearchRecipesResponse response = createSearchResponse(recipes, pageable, 25);
    ResponseEntity<SearchRecipesResponse> expectedResponse = ResponseEntity.ok(response);

    when(favoriteService.getUserFavorites(eq(testUserId), any(Pageable.class)))
        .thenReturn(expectedResponse);

    // When
    ResponseEntity<SearchRecipesResponse> result =
        favoriteController.getUserFavorites(testUserId, pageable);

    // Then
    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(result.getBody()).isNotNull();
    assertThat(result.getBody().getPage()).isEqualTo(2);
    assertThat(result.getBody().getSize()).isEqualTo(5);
    assertThat(result.getBody().getTotalElements()).isEqualTo(25);

    verify(favoriteService).getUserFavorites(testUserId, pageable);
  }

  @Test
  @DisplayName("Should handle first page request")
  @Tag("standard-processing")
  void shouldHandleFirstPageRequest() throws AccessDeniedException {
    // Given
    Pageable pageable = PageRequest.of(0, 10);
    List<RecipeDto> recipes =
        Arrays.asList(createTestRecipeDto(101L), createTestRecipeDto(102L));
    SearchRecipesResponse response = createSearchResponse(recipes, pageable, 30);
    ResponseEntity<SearchRecipesResponse> expectedResponse = ResponseEntity.ok(response);

    when(favoriteService.getUserFavorites(eq(testUserId), any(Pageable.class)))
        .thenReturn(expectedResponse);

    // When
    ResponseEntity<SearchRecipesResponse> result =
        favoriteController.getUserFavorites(testUserId, pageable);

    // Then
    assertThat(result.getBody()).isNotNull();
    assertThat(result.getBody().isFirst()).isTrue();
    assertThat(result.getBody().isLast()).isFalse();
    assertThat(result.getBody().getTotalPages()).isEqualTo(3);
  }

  @Test
  @DisplayName("Should handle last page request")
  @Tag("standard-processing")
  void shouldHandleLastPageRequest() throws AccessDeniedException {
    // Given
    Pageable pageable = PageRequest.of(2, 10); // Last page
    List<RecipeDto> recipes = Arrays.asList(createTestRecipeDto(101L));
    SearchRecipesResponse response = createSearchResponse(recipes, pageable, 21);
    ResponseEntity<SearchRecipesResponse> expectedResponse = ResponseEntity.ok(response);

    when(favoriteService.getUserFavorites(eq(testUserId), any(Pageable.class)))
        .thenReturn(expectedResponse);

    // When
    ResponseEntity<SearchRecipesResponse> result =
        favoriteController.getUserFavorites(testUserId, pageable);

    // Then
    assertThat(result.getBody()).isNotNull();
    assertThat(result.getBody().isFirst()).isFalse();
    assertThat(result.getBody().isLast()).isTrue();
    assertThat(result.getBody().getNumberOfElements()).isEqualTo(1);
  }

  @Test
  @DisplayName("Should delegate to service layer for getUserFavorites")
  @Tag("standard-processing")
  void shouldDelegateToServiceLayerForGetUserFavorites() throws AccessDeniedException {
    // Given
    Pageable pageable = PageRequest.of(0, 20);
    SearchRecipesResponse response = createSearchResponse(Collections.emptyList(), pageable, 0);
    ResponseEntity<SearchRecipesResponse> expectedResponse = ResponseEntity.ok(response);

    when(favoriteService.getUserFavorites(eq(testUserId), any(Pageable.class)))
        .thenReturn(expectedResponse);

    // When
    favoriteController.getUserFavorites(testUserId, pageable);

    // Then
    verify(favoriteService).getUserFavorites(testUserId, pageable);
  }

  // ==================== addFavorite Tests ====================

  @Test
  @DisplayName("Should add favorite successfully")
  @Tag("standard-processing")
  void shouldAddFavoriteSuccessfully() {
    // Given
    RecipeFavoriteDto favoriteDto = createTestFavoriteDto(testUserId, testRecipeId);
    ResponseEntity<RecipeFavoriteDto> expectedResponse =
        ResponseEntity.status(HttpStatus.CREATED).body(favoriteDto);

    when(favoriteService.addFavorite(testRecipeId)).thenReturn(expectedResponse);

    // When
    ResponseEntity<RecipeFavoriteDto> result = favoriteController.addFavorite(testRecipeId);

    // Then
    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(result.getBody()).isNotNull();
    assertThat(result.getBody().getRecipeId()).isEqualTo(testRecipeId);
    assertThat(result.getBody().getUserId()).isEqualTo(testUserId);

    verify(favoriteService).addFavorite(testRecipeId);
  }

  @Test
  @DisplayName("Should delegate to service layer for addFavorite")
  @Tag("standard-processing")
  void shouldDelegateToServiceLayerForAddFavorite() {
    // Given
    RecipeFavoriteDto favoriteDto = createTestFavoriteDto(testUserId, testRecipeId);
    ResponseEntity<RecipeFavoriteDto> expectedResponse =
        ResponseEntity.status(HttpStatus.CREATED).body(favoriteDto);

    when(favoriteService.addFavorite(testRecipeId)).thenReturn(expectedResponse);

    // When
    favoriteController.addFavorite(testRecipeId);

    // Then
    verify(favoriteService).addFavorite(testRecipeId);
  }

  // ==================== removeFavorite Tests ====================

  @Test
  @DisplayName("Should remove favorite successfully")
  @Tag("standard-processing")
  void shouldRemoveFavoriteSuccessfully() {
    // Given
    ResponseEntity<Void> expectedResponse = ResponseEntity.noContent().build();

    when(favoriteService.removeFavorite(testRecipeId)).thenReturn(expectedResponse);

    // When
    ResponseEntity<Void> result = favoriteController.removeFavorite(testRecipeId);

    // Then
    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(result.getBody()).isNull();

    verify(favoriteService).removeFavorite(testRecipeId);
  }

  @Test
  @DisplayName("Should delegate to service layer for removeFavorite")
  @Tag("standard-processing")
  void shouldDelegateToServiceLayerForRemoveFavorite() {
    // Given
    ResponseEntity<Void> expectedResponse = ResponseEntity.noContent().build();

    when(favoriteService.removeFavorite(testRecipeId)).thenReturn(expectedResponse);

    // When
    favoriteController.removeFavorite(testRecipeId);

    // Then
    verify(favoriteService).removeFavorite(testRecipeId);
  }

  // ==================== isFavorited Tests ====================

  @Test
  @DisplayName("Should return true when recipe is favorited")
  @Tag("standard-processing")
  void shouldReturnTrueWhenRecipeIsFavorited() {
    // Given
    ResponseEntity<Boolean> expectedResponse = ResponseEntity.ok(true);

    when(favoriteService.isFavorited(testRecipeId)).thenReturn(expectedResponse);

    // When
    ResponseEntity<Boolean> result = favoriteController.isFavorited(testRecipeId);

    // Then
    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(result.getBody()).isTrue();

    verify(favoriteService).isFavorited(testRecipeId);
  }

  @Test
  @DisplayName("Should return false when recipe is not favorited")
  @Tag("standard-processing")
  void shouldReturnFalseWhenRecipeIsNotFavorited() {
    // Given
    ResponseEntity<Boolean> expectedResponse = ResponseEntity.ok(false);

    when(favoriteService.isFavorited(testRecipeId)).thenReturn(expectedResponse);

    // When
    ResponseEntity<Boolean> result = favoriteController.isFavorited(testRecipeId);

    // Then
    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(result.getBody()).isFalse();

    verify(favoriteService).isFavorited(testRecipeId);
  }

  @Test
  @DisplayName("Should delegate to service layer for isFavorited")
  @Tag("standard-processing")
  void shouldDelegateToServiceLayerForIsFavorited() {
    // Given
    ResponseEntity<Boolean> expectedResponse = ResponseEntity.ok(false);

    when(favoriteService.isFavorited(testRecipeId)).thenReturn(expectedResponse);

    // When
    favoriteController.isFavorited(testRecipeId);

    // Then
    verify(favoriteService).isFavorited(testRecipeId);
  }

  // ==================== Collection Favorites Tests ====================

  @Test
  @DisplayName("Should delegate getFavoriteCollections to service with userId")
  @Tag("standard-processing")
  void shouldDelegateGetFavoriteCollectionsToServiceWithUserId() throws AccessDeniedException {
    // Given
    Pageable pageable = PageRequest.of(0, 20);
    Page<CollectionDto> emptyPage = Page.empty(pageable);
    ResponseEntity<Page<CollectionDto>> expectedResponse = ResponseEntity.ok(emptyPage);

    when(favoriteService.getFavoriteCollections(eq(testUserId), any(Pageable.class)))
        .thenReturn(expectedResponse);

    // When
    ResponseEntity<Page<CollectionDto>> result =
        favoriteController.getFavoriteCollections(testUserId, pageable);

    // Then
    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(result.getBody()).isNotNull();
    verify(favoriteService).getFavoriteCollections(testUserId, pageable);
  }

  @Test
  @DisplayName("Should delegate getFavoriteCollections to service with null userId")
  @Tag("standard-processing")
  void shouldDelegateGetFavoriteCollectionsToServiceWithNullUserId() throws AccessDeniedException {
    // Given
    Pageable pageable = PageRequest.of(0, 20);
    Page<CollectionDto> emptyPage = Page.empty(pageable);
    ResponseEntity<Page<CollectionDto>> expectedResponse = ResponseEntity.ok(emptyPage);

    when(favoriteService.getFavoriteCollections(eq(null), any(Pageable.class)))
        .thenReturn(expectedResponse);

    // When
    ResponseEntity<Page<CollectionDto>> result =
        favoriteController.getFavoriteCollections(null, pageable);

    // Then
    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    verify(favoriteService).getFavoriteCollections(null, pageable);
  }

  @Test
  @DisplayName("Should delegate favoriteCollection to service and return 201 Created")
  @Tag("standard-processing")
  void shouldDelegateFavoriteCollectionToService() throws AccessDeniedException {
    // Given
    Long testCollectionId = 301L;
    CollectionFavoriteDto favoriteDto = createTestCollectionFavoriteDto(testCollectionId);
    ResponseEntity<CollectionFavoriteDto> expectedResponse =
        ResponseEntity.status(HttpStatus.CREATED).body(favoriteDto);

    when(favoriteService.favoriteCollection(testCollectionId)).thenReturn(expectedResponse);

    // When
    ResponseEntity<CollectionFavoriteDto> result =
        favoriteController.favoriteCollection(testCollectionId);

    // Then
    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(result.getBody()).isNotNull();
    assertThat(result.getBody().getCollectionId()).isEqualTo(testCollectionId);
    verify(favoriteService).favoriteCollection(testCollectionId);
  }

  @Test
  @DisplayName("Should delegate unfavoriteCollection to service and return 204 No Content")
  @Tag("standard-processing")
  void shouldDelegateUnfavoriteCollectionToService() {
    // Given
    Long testCollectionId = 301L;
    ResponseEntity<Void> expectedResponse = ResponseEntity.noContent().build();

    when(favoriteService.unfavoriteCollection(testCollectionId)).thenReturn(expectedResponse);

    // When
    ResponseEntity<Void> result = favoriteController.unfavoriteCollection(testCollectionId);

    // Then
    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(result.getBody()).isNull();
    verify(favoriteService).unfavoriteCollection(testCollectionId);
  }

  @Test
  @DisplayName("Should delegate isCollectionFavorited to service and return result")
  @Tag("standard-processing")
  void shouldDelegateIsCollectionFavoritedToService() {
    // Given
    Long testCollectionId = 301L;
    ResponseEntity<Boolean> expectedResponse = ResponseEntity.ok(true);

    when(favoriteService.isCollectionFavorited(testCollectionId)).thenReturn(expectedResponse);

    // When
    ResponseEntity<Boolean> result = favoriteController.isCollectionFavorited(testCollectionId);

    // Then
    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(result.getBody()).isTrue();
    verify(favoriteService).isCollectionFavorited(testCollectionId);
  }

  @Test
  @DisplayName("Should return false when collection is not favorited")
  @Tag("standard-processing")
  void shouldReturnFalseWhenCollectionNotFavorited() {
    // Given
    Long testCollectionId = 301L;
    ResponseEntity<Boolean> expectedResponse = ResponseEntity.ok(false);

    when(favoriteService.isCollectionFavorited(testCollectionId)).thenReturn(expectedResponse);

    // When
    ResponseEntity<Boolean> result = favoriteController.isCollectionFavorited(testCollectionId);

    // Then
    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(result.getBody()).isFalse();
    verify(favoriteService).isCollectionFavorited(testCollectionId);
  }

  // ==================== Helper Methods ====================

  private RecipeDto createTestRecipeDto(Long recipeId) {
    return RecipeDto.builder()
        .recipeId(recipeId)
        .title("Test Recipe " + recipeId)
        .description("Test Description")
        .build();
  }

  private RecipeFavoriteDto createTestFavoriteDto(UUID userId, Long recipeId) {
    return RecipeFavoriteDto.builder()
        .userId(userId)
        .recipeId(recipeId)
        .favoritedAt(LocalDateTime.now())
        .build();
  }

  private SearchRecipesResponse createSearchResponse(
      List<RecipeDto> recipes, Pageable pageable, long totalElements) {
    return SearchRecipesResponse.builder()
        .recipes(recipes)
        .page(pageable.getPageNumber())
        .size(pageable.getPageSize())
        .totalElements(totalElements)
        .totalPages((int) Math.ceil((double) totalElements / pageable.getPageSize()))
        .first(pageable.getPageNumber() == 0)
        .last(
            pageable.getPageNumber()
                >= Math.ceil((double) totalElements / pageable.getPageSize()) - 1)
        .numberOfElements(recipes.size())
        .empty(recipes.isEmpty())
        .build();
  }

  private CollectionFavoriteDto createTestCollectionFavoriteDto(Long collectionId) {
    return CollectionFavoriteDto.builder()
        .userId(testUserId)
        .collectionId(collectionId)
        .favoritedAt(LocalDateTime.now())
        .build();
  }
}
