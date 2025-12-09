package com.recipe_manager.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.recipe_manager.model.dto.collection.CollectionCollaboratorDto;
import com.recipe_manager.model.dto.collection.CollectionRecipeDto;
import com.recipe_manager.model.dto.collection.RecipeCollectionItemDto;
import com.recipe_manager.model.dto.request.SearchCollectionsRequest;
import com.recipe_manager.model.dto.request.UpdateCollectionRequest;
import com.recipe_manager.model.dto.response.CollectionDetailsDto;
import com.recipe_manager.model.dto.response.CollectionDto;
import com.recipe_manager.model.enums.CollaborationMode;
import com.recipe_manager.model.enums.CollectionVisibility;
import com.recipe_manager.service.CollectionService;

/** Unit tests for CollectionController. */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
class CollectionControllerTest {

  @Mock private CollectionService collectionService;

  private CollectionController collectionController;

  @BeforeEach
  void setUp() {
    collectionController = new CollectionController(collectionService);
  }

  @Test
  @DisplayName("Should get collections successfully with default pagination")
  @Tag("standard-processing")
  void shouldGetCollectionsSuccessfullyWithDefaultPagination() {
    // Given
    Pageable pageable = PageRequest.of(0, 20);
    List<CollectionDto> collections = Arrays.asList(createTestDto(1L), createTestDto(2L));
    Page<CollectionDto> page = new PageImpl<>(collections, pageable, 2);
    ResponseEntity<Page<CollectionDto>> expectedResponse = ResponseEntity.ok(page);

    when(collectionService.getAccessibleCollections(any(Pageable.class)))
        .thenReturn(expectedResponse);

    // When
    ResponseEntity<Page<CollectionDto>> response = collectionController.getCollections(pageable);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(2);
    assertThat(response.getBody().getTotalElements()).isEqualTo(2);

    verify(collectionService).getAccessibleCollections(pageable);
  }

  @Test
  @DisplayName("Should get empty collections page")
  @Tag("standard-processing")
  void shouldGetEmptyCollectionsPage() {
    // Given
    Pageable pageable = PageRequest.of(0, 20);
    Page<CollectionDto> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
    ResponseEntity<Page<CollectionDto>> expectedResponse = ResponseEntity.ok(emptyPage);

    when(collectionService.getAccessibleCollections(any(Pageable.class)))
        .thenReturn(expectedResponse);

    // When
    ResponseEntity<Page<CollectionDto>> response = collectionController.getCollections(pageable);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getContent()).isEmpty();
    assertThat(response.getBody().getTotalElements()).isEqualTo(0);

    verify(collectionService).getAccessibleCollections(pageable);
  }

  @Test
  @DisplayName("Should pass custom pagination parameters to service")
  @Tag("standard-processing")
  void shouldPassCustomPaginationParametersToService() {
    // Given
    Pageable pageable = PageRequest.of(2, 5); // Third page, size 5
    List<CollectionDto> collections = Arrays.asList(createTestDto(11L));
    Page<CollectionDto> page = new PageImpl<>(collections, pageable, 50);
    ResponseEntity<Page<CollectionDto>> expectedResponse = ResponseEntity.ok(page);

    when(collectionService.getAccessibleCollections(pageable)).thenReturn(expectedResponse);

    // When
    ResponseEntity<Page<CollectionDto>> response = collectionController.getCollections(pageable);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getNumber()).isEqualTo(2);
    assertThat(response.getBody().getSize()).isEqualTo(5);
    assertThat(response.getBody().getTotalElements()).isEqualTo(50);

    verify(collectionService).getAccessibleCollections(pageable);
  }

  @Test
  @DisplayName("Should handle first page request")
  @Tag("standard-processing")
  void shouldHandleFirstPageRequest() {
    // Given
    Pageable pageable = PageRequest.of(0, 10);
    List<CollectionDto> collections =
        Arrays.asList(createTestDto(1L), createTestDto(2L), createTestDto(3L));
    Page<CollectionDto> page = new PageImpl<>(collections, pageable, 30);
    ResponseEntity<Page<CollectionDto>> expectedResponse = ResponseEntity.ok(page);

    when(collectionService.getAccessibleCollections(pageable)).thenReturn(expectedResponse);

    // When
    ResponseEntity<Page<CollectionDto>> response = collectionController.getCollections(pageable);

    // Then
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().isFirst()).isTrue();
    assertThat(response.getBody().isLast()).isFalse();
    assertThat(response.getBody().getTotalPages()).isEqualTo(3);
  }

  @Test
  @DisplayName("Should handle last page request")
  @Tag("standard-processing")
  void shouldHandleLastPageRequest() {
    // Given
    Pageable pageable = PageRequest.of(2, 10); // Last page
    List<CollectionDto> collections = Arrays.asList(createTestDto(21L));
    Page<CollectionDto> page = new PageImpl<>(collections, pageable, 21);
    ResponseEntity<Page<CollectionDto>> expectedResponse = ResponseEntity.ok(page);

    when(collectionService.getAccessibleCollections(pageable)).thenReturn(expectedResponse);

    // When
    ResponseEntity<Page<CollectionDto>> response = collectionController.getCollections(pageable);

    // Then
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().isFirst()).isFalse();
    assertThat(response.getBody().isLast()).isTrue();
    assertThat(response.getBody().getNumberOfElements()).isEqualTo(1);
  }

  @Test
  @DisplayName("Should delegate to service layer")
  @Tag("standard-processing")
  void shouldDelegateToServiceLayer() {
    // Given
    Pageable pageable = PageRequest.of(0, 20);
    Page<CollectionDto> page = new PageImpl<>(Collections.emptyList(), pageable, 0);
    ResponseEntity<Page<CollectionDto>> expectedResponse = ResponseEntity.ok(page);

    when(collectionService.getAccessibleCollections(pageable)).thenReturn(expectedResponse);

    // When
    collectionController.getCollections(pageable);

    // Then
    verify(collectionService).getAccessibleCollections(pageable);
  }

  @Test
  @DisplayName("Should return page with correct content")
  @Tag("standard-processing")
  void shouldReturnPageWithCorrectContent() {
    // Given
    Pageable pageable = PageRequest.of(0, 20);
    CollectionDto dto1 = createTestDto(1L);
    CollectionDto dto2 = createTestDto(2L);
    CollectionDto dto3 = createTestDto(3L);
    List<CollectionDto> collections = Arrays.asList(dto1, dto2, dto3);
    Page<CollectionDto> page = new PageImpl<>(collections, pageable, 3);
    ResponseEntity<Page<CollectionDto>> expectedResponse = ResponseEntity.ok(page);

    when(collectionService.getAccessibleCollections(any(Pageable.class)))
        .thenReturn(expectedResponse);

    // When
    ResponseEntity<Page<CollectionDto>> response = collectionController.getCollections(pageable);

    // Then
    assertThat(response.getBody()).isNotNull();
    List<CollectionDto> content = response.getBody().getContent();
    assertThat(content).hasSize(3);
    assertThat(content.get(0).getCollectionId()).isEqualTo(1L);
    assertThat(content.get(1).getCollectionId()).isEqualTo(2L);
    assertThat(content.get(2).getCollectionId()).isEqualTo(3L);
  }

  @Test
  @DisplayName("Should preserve DTO fields in response")
  @Tag("standard-processing")
  void shouldPreserveDtoFieldsInResponse() {
    // Given
    UUID userId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();
    CollectionDto dto =
        CollectionDto.builder()
            .collectionId(123L)
            .userId(userId)
            .name("My Collection")
            .description("Test Description")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .recipeCount(10)
            .collaboratorCount(3)
            .createdAt(now)
            .updatedAt(now)
            .build();

    Pageable pageable = PageRequest.of(0, 20);
    Page<CollectionDto> page = new PageImpl<>(Arrays.asList(dto), pageable, 1);
    ResponseEntity<Page<CollectionDto>> expectedResponse = ResponseEntity.ok(page);

    when(collectionService.getAccessibleCollections(any(Pageable.class)))
        .thenReturn(expectedResponse);

    // When
    ResponseEntity<Page<CollectionDto>> response = collectionController.getCollections(pageable);

    // Then
    assertThat(response.getBody()).isNotNull();
    CollectionDto resultDto = response.getBody().getContent().get(0);
    assertThat(resultDto.getCollectionId()).isEqualTo(123L);
    assertThat(resultDto.getUserId()).isEqualTo(userId);
    assertThat(resultDto.getName()).isEqualTo("My Collection");
    assertThat(resultDto.getDescription()).isEqualTo("Test Description");
    assertThat(resultDto.getVisibility()).isEqualTo(CollectionVisibility.PUBLIC);
    assertThat(resultDto.getCollaborationMode()).isEqualTo(CollaborationMode.OWNER_ONLY);
    assertThat(resultDto.getRecipeCount()).isEqualTo(10);
    assertThat(resultDto.getCollaboratorCount()).isEqualTo(3);
    assertThat(resultDto.getCreatedAt()).isEqualTo(now);
    assertThat(resultDto.getUpdatedAt()).isEqualTo(now);
  }

  private CollectionDto createTestDto(Long collectionId) {
    return CollectionDto.builder()
        .collectionId(collectionId)
        .userId(UUID.randomUUID())
        .name("Test Collection " + collectionId)
        .description("Test Description")
        .visibility(CollectionVisibility.PUBLIC)
        .collaborationMode(CollaborationMode.OWNER_ONLY)
        .recipeCount(5)
        .collaboratorCount(0)
        .createdAt(LocalDateTime.now().minusDays(1))
        .updatedAt(LocalDateTime.now())
        .build();
  }

  @Test
  @DisplayName("Should get collection by ID successfully")
  @Tag("standard-processing")
  void shouldGetCollectionByIdSuccessfully() {
    // Given
    Long collectionId = 1L;
    CollectionDetailsDto detailsDto = createTestDetailsDto(collectionId);
    ResponseEntity<CollectionDetailsDto> expectedResponse = ResponseEntity.ok(detailsDto);

    when(collectionService.getCollectionById(collectionId)).thenReturn(expectedResponse);

    // When
    ResponseEntity<CollectionDetailsDto> response =
        collectionController.getCollectionById(collectionId);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).isEqualTo(detailsDto);

    verify(collectionService).getCollectionById(collectionId);
  }

  @Test
  @DisplayName("Should pass correct collection ID to service")
  @Tag("standard-processing")
  void shouldPassCorrectCollectionIdToService() {
    // Given
    Long collectionId = 123L;
    CollectionDetailsDto detailsDto = createTestDetailsDto(collectionId);
    ResponseEntity<CollectionDetailsDto> expectedResponse = ResponseEntity.ok(detailsDto);

    when(collectionService.getCollectionById(collectionId)).thenReturn(expectedResponse);

    // When
    collectionController.getCollectionById(collectionId);

    // Then
    verify(collectionService).getCollectionById(collectionId);
  }

  @Test
  @DisplayName("Should delegate get by ID to service layer")
  @Tag("standard-processing")
  void shouldDelegateGetByIdToServiceLayer() {
    // Given
    Long collectionId = 456L;
    CollectionDetailsDto detailsDto = createTestDetailsDto(collectionId);
    ResponseEntity<CollectionDetailsDto> expectedResponse = ResponseEntity.ok(detailsDto);

    when(collectionService.getCollectionById(collectionId)).thenReturn(expectedResponse);

    // When
    collectionController.getCollectionById(collectionId);

    // Then
    verify(collectionService).getCollectionById(collectionId);
  }

  @Test
  @DisplayName("Should return collection details with all fields")
  @Tag("standard-processing")
  void shouldReturnCollectionDetailsWithAllFields() {
    // Given
    Long collectionId = 789L;
    UUID userId = UUID.randomUUID();
    UUID addedBy = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    CollectionRecipeDto recipeDto =
        CollectionRecipeDto.builder()
            .recipeId(1L)
            .recipeTitle("Test Recipe")
            .recipeDescription("Test Description")
            .displayOrder(10)
            .addedBy(addedBy)
            .addedAt(now)
            .build();

    CollectionDetailsDto detailsDto =
        CollectionDetailsDto.builder()
            .collectionId(collectionId)
            .userId(userId)
            .name("Detailed Collection")
            .description("Detailed Description")
            .visibility(CollectionVisibility.FRIENDS_ONLY)
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .recipeCount(1)
            .collaboratorCount(2)
            .recipes(Arrays.asList(recipeDto))
            .createdAt(now)
            .updatedAt(now)
            .build();

    ResponseEntity<CollectionDetailsDto> expectedResponse = ResponseEntity.ok(detailsDto);

    when(collectionService.getCollectionById(collectionId)).thenReturn(expectedResponse);

    // When
    ResponseEntity<CollectionDetailsDto> response =
        collectionController.getCollectionById(collectionId);

    // Then
    assertThat(response.getBody()).isNotNull();
    CollectionDetailsDto result = response.getBody();
    assertThat(result.getCollectionId()).isEqualTo(collectionId);
    assertThat(result.getUserId()).isEqualTo(userId);
    assertThat(result.getName()).isEqualTo("Detailed Collection");
    assertThat(result.getDescription()).isEqualTo("Detailed Description");
    assertThat(result.getVisibility()).isEqualTo(CollectionVisibility.FRIENDS_ONLY);
    assertThat(result.getCollaborationMode()).isEqualTo(CollaborationMode.SPECIFIC_USERS);
    assertThat(result.getRecipeCount()).isEqualTo(1);
    assertThat(result.getCollaboratorCount()).isEqualTo(2);
    assertThat(result.getRecipes()).hasSize(1);
    assertThat(result.getCreatedAt()).isEqualTo(now);
    assertThat(result.getUpdatedAt()).isEqualTo(now);
  }

  @Test
  @DisplayName("Should return collection with multiple recipes")
  @Tag("standard-processing")
  void shouldReturnCollectionWithMultipleRecipes() {
    // Given
    Long collectionId = 999L;
    UUID userId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    CollectionRecipeDto recipe1 =
        CollectionRecipeDto.builder()
            .recipeId(1L)
            .recipeTitle("First Recipe")
            .displayOrder(10)
            .addedBy(userId)
            .addedAt(now.minusDays(2))
            .build();

    CollectionRecipeDto recipe2 =
        CollectionRecipeDto.builder()
            .recipeId(2L)
            .recipeTitle("Second Recipe")
            .displayOrder(20)
            .addedBy(userId)
            .addedAt(now.minusDays(1))
            .build();

    CollectionDetailsDto detailsDto =
        CollectionDetailsDto.builder()
            .collectionId(collectionId)
            .userId(userId)
            .name("Multi-Recipe Collection")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .recipeCount(2)
            .collaboratorCount(0)
            .recipes(Arrays.asList(recipe1, recipe2))
            .createdAt(now)
            .updatedAt(now)
            .build();

    ResponseEntity<CollectionDetailsDto> expectedResponse = ResponseEntity.ok(detailsDto);

    when(collectionService.getCollectionById(collectionId)).thenReturn(expectedResponse);

    // When
    ResponseEntity<CollectionDetailsDto> response =
        collectionController.getCollectionById(collectionId);

    // Then
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getRecipes()).hasSize(2);
    assertThat(response.getBody().getRecipeCount()).isEqualTo(2);
    assertThat(response.getBody().getRecipes().get(0).getRecipeId()).isEqualTo(1L);
    assertThat(response.getBody().getRecipes().get(1).getRecipeId()).isEqualTo(2L);
  }

  @Test
  @DisplayName("Should return 200 OK status for get by ID")
  @Tag("standard-processing")
  void shouldReturn200OkStatusForGetById() {
    // Given
    Long collectionId = 100L;
    CollectionDetailsDto detailsDto = createTestDetailsDto(collectionId);
    ResponseEntity<CollectionDetailsDto> expectedResponse = ResponseEntity.ok(detailsDto);

    when(collectionService.getCollectionById(collectionId)).thenReturn(expectedResponse);

    // When
    ResponseEntity<CollectionDetailsDto> response =
        collectionController.getCollectionById(collectionId);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getStatusCode().value()).isEqualTo(200);
  }

  private CollectionDetailsDto createTestDetailsDto(Long collectionId) {
    UUID userId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    CollectionRecipeDto recipeDto =
        CollectionRecipeDto.builder()
            .recipeId(1L)
            .recipeTitle("Test Recipe")
            .recipeDescription("Test Description")
            .displayOrder(10)
            .addedBy(userId)
            .addedAt(now)
            .build();

    return CollectionDetailsDto.builder()
        .collectionId(collectionId)
        .userId(userId)
        .name("Test Collection " + collectionId)
        .description("Test Description")
        .visibility(CollectionVisibility.PUBLIC)
        .collaborationMode(CollaborationMode.OWNER_ONLY)
        .recipeCount(1)
        .collaboratorCount(0)
        .recipes(Arrays.asList(recipeDto))
        .createdAt(now)
        .updatedAt(now)
        .build();
  }

  @Test
  @DisplayName("Should update collection successfully")
  @Tag("standard-processing")
  void shouldUpdateCollectionSuccessfully() {
    // Given
    Long collectionId = 1L;
    UpdateCollectionRequest request =
        UpdateCollectionRequest.builder()
            .name("Updated Name")
            .visibility(CollectionVisibility.PRIVATE)
            .build();

    CollectionDto updatedDto =
        CollectionDto.builder()
            .collectionId(collectionId)
            .name("Updated Name")
            .visibility(CollectionVisibility.PRIVATE)
            .build();

    ResponseEntity<CollectionDto> expectedResponse = ResponseEntity.ok(updatedDto);

    when(collectionService.updateCollection(collectionId, request)).thenReturn(expectedResponse);

    // When
    ResponseEntity<CollectionDto> response =
        collectionController.updateCollection(collectionId, request);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo(updatedDto);
    verify(collectionService).updateCollection(collectionId, request);
  }

  @Test
  @DisplayName("Should delegate update to service layer")
  @Tag("standard-processing")
  void shouldDelegateUpdateToServiceLayer() {
    // Given
    Long collectionId = 2L;
    UpdateCollectionRequest request =
        UpdateCollectionRequest.builder().name("Updated").build();

    when(collectionService.updateCollection(collectionId, request))
        .thenReturn(ResponseEntity.ok(CollectionDto.builder().build()));

    // When
    collectionController.updateCollection(collectionId, request);

    // Then
    verify(collectionService).updateCollection(collectionId, request);
  }

  @Test
  @DisplayName("Should return 200 OK status for update")
  @Tag("standard-processing")
  void shouldReturn200OkStatusForUpdate() {
    // Given
    Long collectionId = 3L;
    UpdateCollectionRequest request =
        UpdateCollectionRequest.builder().description("Updated Description").build();

    ResponseEntity<CollectionDto> expectedResponse =
        ResponseEntity.ok(CollectionDto.builder().build());

    when(collectionService.updateCollection(collectionId, request)).thenReturn(expectedResponse);

    // When
    ResponseEntity<CollectionDto> response =
        collectionController.updateCollection(collectionId, request);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getStatusCode().value()).isEqualTo(200);
  }

  @Test
  @DisplayName("Should delegate delete to service layer")
  @Tag("standard-processing")
  void shouldDelegateDeleteToServiceLayer() {
    // Given
    Long collectionId = 4L;
    ResponseEntity<Void> expectedResponse = ResponseEntity.noContent().build();

    when(collectionService.deleteCollection(collectionId)).thenReturn(expectedResponse);

    // When
    ResponseEntity<Void> response = collectionController.deleteCollection(collectionId);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(response.getStatusCode().value()).isEqualTo(204);
    assertThat(response.getBody()).isNull();
    verify(collectionService).deleteCollection(collectionId);
  }

  @Test
  @DisplayName("Should delegate search to service layer")
  @Tag("standard-processing")
  void shouldDelegateSearchToServiceLayer() {
    // Given
    SearchCollectionsRequest request =
        SearchCollectionsRequest.builder().query("italian").build();

    Pageable pageable = PageRequest.of(0, 20);

    Page<CollectionDto> expectedPage =
        new PageImpl<>(
            List.of(CollectionDto.builder().collectionId(1L).name("Italian Recipes").build()));

    ResponseEntity<Page<CollectionDto>> expectedResponse = ResponseEntity.ok(expectedPage);

    when(collectionService.searchCollections(request, pageable)).thenReturn(expectedResponse);

    // When
    ResponseEntity<Page<CollectionDto>> response =
        collectionController.searchCollections(request, pageable);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(1);
    assertThat(response.getBody().getContent().get(0).getName()).isEqualTo("Italian Recipes");
    verify(collectionService).searchCollections(request, pageable);
  }

  @Test
  @DisplayName("Should add recipe to collection successfully")
  @Tag("standard-processing")
  void shouldAddRecipeToCollectionSuccessfully() {
    // Given
    Long collectionId = 1L;
    Long recipeId = 100L;
    UUID addedBy = UUID.randomUUID();
    LocalDateTime addedAt = LocalDateTime.now();

    RecipeCollectionItemDto itemDto =
        RecipeCollectionItemDto.builder()
            .collectionId(collectionId)
            .recipeId(recipeId)
            .displayOrder(10)
            .addedBy(addedBy)
            .addedAt(addedAt)
            .build();

    ResponseEntity<RecipeCollectionItemDto> expectedResponse =
        ResponseEntity.status(HttpStatus.CREATED).body(itemDto);

    when(collectionService.addRecipeToCollection(collectionId, recipeId))
        .thenReturn(expectedResponse);

    // When
    ResponseEntity<RecipeCollectionItemDto> response =
        collectionController.addRecipeToCollection(collectionId, recipeId);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getStatusCode().value()).isEqualTo(201);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).isEqualTo(itemDto);
    assertThat(response.getBody().getCollectionId()).isEqualTo(collectionId);
    assertThat(response.getBody().getRecipeId()).isEqualTo(recipeId);
    assertThat(response.getBody().getDisplayOrder()).isEqualTo(10);
    assertThat(response.getBody().getAddedBy()).isEqualTo(addedBy);
    assertThat(response.getBody().getAddedAt()).isEqualTo(addedAt);

    verify(collectionService).addRecipeToCollection(collectionId, recipeId);
  }

  @Test
  @DisplayName("Should delegate add recipe to service layer")
  @Tag("standard-processing")
  void shouldDelegateAddRecipeToServiceLayer() {
    // Given
    Long collectionId = 5L;
    Long recipeId = 500L;

    RecipeCollectionItemDto itemDto =
        RecipeCollectionItemDto.builder()
            .collectionId(collectionId)
            .recipeId(recipeId)
            .displayOrder(20)
            .build();

    ResponseEntity<RecipeCollectionItemDto> expectedResponse =
        ResponseEntity.status(HttpStatus.CREATED).body(itemDto);

    when(collectionService.addRecipeToCollection(collectionId, recipeId))
        .thenReturn(expectedResponse);

    // When
    collectionController.addRecipeToCollection(collectionId, recipeId);

    // Then
    verify(collectionService).addRecipeToCollection(collectionId, recipeId);
  }

  @Test
  @DisplayName("Should pass correct path variables to service for add recipe")
  @Tag("standard-processing")
  void shouldPassCorrectPathVariablesToServiceForAddRecipe() {
    // Given
    Long collectionId = 123L;
    Long recipeId = 456L;

    RecipeCollectionItemDto itemDto =
        RecipeCollectionItemDto.builder()
            .collectionId(collectionId)
            .recipeId(recipeId)
            .displayOrder(30)
            .build();

    ResponseEntity<RecipeCollectionItemDto> expectedResponse =
        ResponseEntity.status(HttpStatus.CREATED).body(itemDto);

    when(collectionService.addRecipeToCollection(collectionId, recipeId))
        .thenReturn(expectedResponse);

    // When
    collectionController.addRecipeToCollection(collectionId, recipeId);

    // Then
    verify(collectionService).addRecipeToCollection(collectionId, recipeId);
  }

  @Test
  @DisplayName("Should return no content when removing recipe from collection")
  @Tag("standard-processing")
  void shouldReturnNoContentWhenRemovingRecipe() {
    // Given
    Long collectionId = 1L;
    Long recipeId = 100L;

    ResponseEntity<Void> expectedResponse = ResponseEntity.noContent().build();

    when(collectionService.removeRecipeFromCollection(collectionId, recipeId))
        .thenReturn(expectedResponse);

    // When
    ResponseEntity<Void> response =
        collectionController.removeRecipeFromCollection(collectionId, recipeId);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(response.getStatusCode().value()).isEqualTo(204);
    assertThat(response.getBody()).isNull();

    verify(collectionService).removeRecipeFromCollection(collectionId, recipeId);
  }

  @Test
  @DisplayName("Should delegate remove recipe to service layer")
  @Tag("standard-processing")
  void shouldDelegateRemoveRecipeToServiceLayer() {
    // Given
    Long collectionId = 5L;
    Long recipeId = 500L;

    ResponseEntity<Void> expectedResponse = ResponseEntity.noContent().build();

    when(collectionService.removeRecipeFromCollection(collectionId, recipeId))
        .thenReturn(expectedResponse);

    // When
    collectionController.removeRecipeFromCollection(collectionId, recipeId);

    // Then
    verify(collectionService).removeRecipeFromCollection(collectionId, recipeId);
  }

  @Test
  @DisplayName("Should pass correct path variables to service for remove recipe")
  @Tag("standard-processing")
  void shouldPassCorrectPathVariablesToServiceForRemoveRecipe() {
    // Given
    Long collectionId = 123L;
    Long recipeId = 456L;

    ResponseEntity<Void> expectedResponse = ResponseEntity.noContent().build();

    when(collectionService.removeRecipeFromCollection(collectionId, recipeId))
        .thenReturn(expectedResponse);

    // When
    collectionController.removeRecipeFromCollection(collectionId, recipeId);

    // Then
    verify(collectionService).removeRecipeFromCollection(collectionId, recipeId);
  }

  @Test
  @DisplayName("Should update recipe order successfully and return 200 OK")
  @Tag("standard-processing")
  void shouldUpdateRecipeOrderSuccessfully() {
    // Given
    Long collectionId = 1L;
    Long recipeId = 100L;

    com.recipe_manager.model.dto.request.UpdateRecipeOrderRequest request =
        com.recipe_manager.model.dto.request.UpdateRecipeOrderRequest.builder()
            .displayOrder(15)
            .build();

    CollectionRecipeDto responseDto =
        CollectionRecipeDto.builder()
            .recipeId(recipeId)
            .recipeTitle("Test Recipe")
            .recipeDescription("Test Description")
            .displayOrder(15)
            .addedBy(UUID.randomUUID())
            .addedAt(LocalDateTime.now())
            .build();

    ResponseEntity<CollectionRecipeDto> expectedResponse = ResponseEntity.ok(responseDto);

    when(collectionService.updateRecipeOrder(collectionId, recipeId, request))
        .thenReturn(expectedResponse);

    // When
    ResponseEntity<CollectionRecipeDto> response =
        collectionController.updateRecipeOrder(collectionId, recipeId, request);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getStatusCode().value()).isEqualTo(200);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getRecipeId()).isEqualTo(recipeId);
    assertThat(response.getBody().getDisplayOrder()).isEqualTo(15);
  }

  @Test
  @DisplayName("Should delegate update recipe order to service layer")
  @Tag("standard-processing")
  void shouldDelegateUpdateRecipeOrderToServiceLayer() {
    // Given
    Long collectionId = 5L;
    Long recipeId = 500L;

    com.recipe_manager.model.dto.request.UpdateRecipeOrderRequest request =
        com.recipe_manager.model.dto.request.UpdateRecipeOrderRequest.builder()
            .displayOrder(20)
            .build();

    CollectionRecipeDto responseDto =
        CollectionRecipeDto.builder()
            .recipeId(recipeId)
            .recipeTitle("Test Recipe")
            .displayOrder(20)
            .build();

    ResponseEntity<CollectionRecipeDto> expectedResponse = ResponseEntity.ok(responseDto);

    when(collectionService.updateRecipeOrder(collectionId, recipeId, request))
        .thenReturn(expectedResponse);

    // When
    collectionController.updateRecipeOrder(collectionId, recipeId, request);

    // Then
    verify(collectionService).updateRecipeOrder(collectionId, recipeId, request);
  }

  @Test
  @DisplayName("Should pass correct parameters to service for update recipe order")
  @Tag("standard-processing")
  void shouldPassCorrectParametersToServiceForUpdateRecipeOrder() {
    // Given
    Long collectionId = 123L;
    Long recipeId = 456L;

    com.recipe_manager.model.dto.request.UpdateRecipeOrderRequest request =
        com.recipe_manager.model.dto.request.UpdateRecipeOrderRequest.builder()
            .displayOrder(30)
            .build();

    CollectionRecipeDto responseDto =
        CollectionRecipeDto.builder()
            .recipeId(recipeId)
            .recipeTitle("Test Recipe")
            .displayOrder(30)
            .build();

    ResponseEntity<CollectionRecipeDto> expectedResponse = ResponseEntity.ok(responseDto);

    when(collectionService.updateRecipeOrder(collectionId, recipeId, request))
        .thenReturn(expectedResponse);

    // When
    collectionController.updateRecipeOrder(collectionId, recipeId, request);

    // Then
    verify(collectionService).updateRecipeOrder(eq(collectionId), eq(recipeId), eq(request));
  }

  @Test
  @DisplayName("Should reorder recipes successfully and return 200 OK")
  @Tag("standard-processing")
  void shouldReorderRecipesSuccessfully() {
    // Given
    Long collectionId = 1L;

    com.recipe_manager.model.dto.request.ReorderRecipesRequest.RecipeOrder order1 =
        com.recipe_manager.model.dto.request.ReorderRecipesRequest.RecipeOrder.builder()
            .recipeId(10L)
            .displayOrder(5)
            .build();

    com.recipe_manager.model.dto.request.ReorderRecipesRequest.RecipeOrder order2 =
        com.recipe_manager.model.dto.request.ReorderRecipesRequest.RecipeOrder.builder()
            .recipeId(20L)
            .displayOrder(10)
            .build();

    com.recipe_manager.model.dto.request.ReorderRecipesRequest request =
        com.recipe_manager.model.dto.request.ReorderRecipesRequest.builder()
            .recipes(Arrays.asList(order1, order2))
            .build();

    CollectionRecipeDto dto1 =
        CollectionRecipeDto.builder()
            .recipeId(10L)
            .recipeTitle("Recipe One")
            .recipeDescription("First recipe")
            .displayOrder(5)
            .addedBy(UUID.randomUUID())
            .addedAt(LocalDateTime.now())
            .build();

    CollectionRecipeDto dto2 =
        CollectionRecipeDto.builder()
            .recipeId(20L)
            .recipeTitle("Recipe Two")
            .recipeDescription("Second recipe")
            .displayOrder(10)
            .addedBy(UUID.randomUUID())
            .addedAt(LocalDateTime.now())
            .build();

    List<CollectionRecipeDto> responseDtos = Arrays.asList(dto1, dto2);
    ResponseEntity<List<CollectionRecipeDto>> expectedResponse = ResponseEntity.ok(responseDtos);

    when(collectionService.reorderRecipes(collectionId, request)).thenReturn(expectedResponse);

    // When
    ResponseEntity<List<CollectionRecipeDto>> response =
        collectionController.reorderRecipes(collectionId, request);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).hasSize(2);
    assertThat(response.getBody().get(0).getRecipeId()).isEqualTo(10L);
    assertThat(response.getBody().get(0).getDisplayOrder()).isEqualTo(5);
    assertThat(response.getBody().get(1).getRecipeId()).isEqualTo(20L);
    assertThat(response.getBody().get(1).getDisplayOrder()).isEqualTo(10);

    verify(collectionService).reorderRecipes(collectionId, request);
  }

  @Test
  @DisplayName("Should delegate reorder recipes to service layer")
  @Tag("standard-processing")
  void shouldDelegateReorderRecipesToServiceLayer() {
    // Given
    Long collectionId = 5L;

    com.recipe_manager.model.dto.request.ReorderRecipesRequest.RecipeOrder order1 =
        com.recipe_manager.model.dto.request.ReorderRecipesRequest.RecipeOrder.builder()
            .recipeId(100L)
            .displayOrder(10)
            .build();

    com.recipe_manager.model.dto.request.ReorderRecipesRequest request =
        com.recipe_manager.model.dto.request.ReorderRecipesRequest.builder()
            .recipes(Collections.singletonList(order1))
            .build();

    ResponseEntity<List<CollectionRecipeDto>> expectedResponse =
        ResponseEntity.ok(Collections.emptyList());

    when(collectionService.reorderRecipes(collectionId, request)).thenReturn(expectedResponse);

    // When
    collectionController.reorderRecipes(collectionId, request);

    // Then
    verify(collectionService).reorderRecipes(collectionId, request);
  }

  @Test
  @DisplayName("Should pass correct path variable and request body to service for reorder")
  @Tag("standard-processing")
  void shouldPassCorrectParametersToServiceForReorder() {
    // Given
    Long collectionId = 123L;

    com.recipe_manager.model.dto.request.ReorderRecipesRequest.RecipeOrder order1 =
        com.recipe_manager.model.dto.request.ReorderRecipesRequest.RecipeOrder.builder()
            .recipeId(456L)
            .displayOrder(20)
            .build();

    com.recipe_manager.model.dto.request.ReorderRecipesRequest.RecipeOrder order2 =
        com.recipe_manager.model.dto.request.ReorderRecipesRequest.RecipeOrder.builder()
            .recipeId(789L)
            .displayOrder(30)
            .build();

    com.recipe_manager.model.dto.request.ReorderRecipesRequest request =
        com.recipe_manager.model.dto.request.ReorderRecipesRequest.builder()
            .recipes(Arrays.asList(order1, order2))
            .build();

    ResponseEntity<List<CollectionRecipeDto>> expectedResponse =
        ResponseEntity.ok(Collections.emptyList());

    when(collectionService.reorderRecipes(collectionId, request)).thenReturn(expectedResponse);

    // When
    collectionController.reorderRecipes(collectionId, request);

    // Then
    verify(collectionService).reorderRecipes(collectionId, request);
  }

  @Test
  @DisplayName("Should get collaborators successfully")
  @Tag("standard-processing")
  void shouldGetCollaboratorsSuccessfully() {
    // Given
    Long collectionId = 1L;
    UUID collaboratorId1 = UUID.randomUUID();
    UUID collaboratorId2 = UUID.randomUUID();
    UUID grantedById = UUID.randomUUID();

    List<CollectionCollaboratorDto> collaborators =
        Arrays.asList(
            CollectionCollaboratorDto.builder()
                .collectionId(collectionId)
                .userId(collaboratorId1)
                .username("user1")
                .grantedBy(grantedById)
                .grantedByUsername("admin")
                .grantedAt(LocalDateTime.now())
                .build(),
            CollectionCollaboratorDto.builder()
                .collectionId(collectionId)
                .userId(collaboratorId2)
                .username("user2")
                .grantedBy(grantedById)
                .grantedByUsername("admin")
                .grantedAt(LocalDateTime.now().minusDays(1))
                .build());

    ResponseEntity<List<CollectionCollaboratorDto>> expectedResponse =
        ResponseEntity.ok(collaborators);

    when(collectionService.getCollaborators(collectionId)).thenReturn(expectedResponse);

    // When
    ResponseEntity<List<CollectionCollaboratorDto>> response =
        collectionController.getCollaborators(collectionId);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).hasSize(2);
    assertThat(response.getBody().get(0).getUserId()).isEqualTo(collaboratorId1);
    assertThat(response.getBody().get(0).getUsername()).isEqualTo("user1");
    assertThat(response.getBody().get(0).getGrantedByUsername()).isEqualTo("admin");

    verify(collectionService).getCollaborators(collectionId);
  }

  @Test
  @DisplayName("Should get empty collaborators list")
  @Tag("standard-processing")
  void shouldGetEmptyCollaboratorsList() {
    // Given
    Long collectionId = 1L;
    ResponseEntity<List<CollectionCollaboratorDto>> expectedResponse =
        ResponseEntity.ok(Collections.emptyList());

    when(collectionService.getCollaborators(collectionId)).thenReturn(expectedResponse);

    // When
    ResponseEntity<List<CollectionCollaboratorDto>> response =
        collectionController.getCollaborators(collectionId);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).isEmpty();

    verify(collectionService).getCollaborators(collectionId);
  }

  @Test
  @DisplayName("Should call service with correct collection ID for getCollaborators")
  @Tag("standard-processing")
  void shouldCallServiceWithCorrectCollectionId() {
    // Given
    Long collectionId = 999L;
    ResponseEntity<List<CollectionCollaboratorDto>> expectedResponse =
        ResponseEntity.ok(Collections.emptyList());

    when(collectionService.getCollaborators(collectionId)).thenReturn(expectedResponse);

    // When
    collectionController.getCollaborators(collectionId);

    // Then
    verify(collectionService).getCollaborators(eq(999L));
  }

  @Test
  @DisplayName("Should delegate to service for addCollaborator")
  @Tag("standard-processing")
  void shouldDelegateToServiceForAddCollaborator() {
    // Given
    Long collectionId = 1L;
    UUID collaboratorId = UUID.randomUUID();

    com.recipe_manager.model.dto.request.AddCollaboratorRequest request =
        com.recipe_manager.model.dto.request.AddCollaboratorRequest.builder()
            .userId(collaboratorId)
            .build();

    CollectionCollaboratorDto responseDto =
        CollectionCollaboratorDto.builder()
            .collectionId(collectionId)
            .userId(collaboratorId)
            .username("testuser")
            .grantedBy(UUID.randomUUID())
            .grantedByUsername("owner")
            .grantedAt(java.time.LocalDateTime.now())
            .build();

    ResponseEntity<CollectionCollaboratorDto> expectedResponse =
        ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(responseDto);

    when(collectionService.addCollaborator(collectionId, request)).thenReturn(expectedResponse);

    // When
    ResponseEntity<CollectionCollaboratorDto> response =
        collectionController.addCollaborator(collectionId, request);

    // Then
    assertThat(response).isEqualTo(expectedResponse);
    assertThat(response.getStatusCode()).isEqualTo(org.springframework.http.HttpStatus.CREATED);
    assertThat(response.getBody()).isEqualTo(responseDto);
    verify(collectionService).addCollaborator(collectionId, request);
  }

  @Test
  @DisplayName("Should call service with correct parameters for addCollaborator")
  @Tag("standard-processing")
  void shouldCallServiceWithCorrectParametersForAddCollaborator() {
    // Given
    Long collectionId = 123L;
    UUID userId = UUID.randomUUID();

    com.recipe_manager.model.dto.request.AddCollaboratorRequest request =
        com.recipe_manager.model.dto.request.AddCollaboratorRequest.builder()
            .userId(userId)
            .build();

    CollectionCollaboratorDto responseDto =
        CollectionCollaboratorDto.builder()
            .collectionId(collectionId)
            .userId(userId)
            .username("collaborator")
            .grantedBy(UUID.randomUUID())
            .grantedByUsername("owner")
            .grantedAt(java.time.LocalDateTime.now())
            .build();

    ResponseEntity<CollectionCollaboratorDto> expectedResponse =
        ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(responseDto);

    when(collectionService.addCollaborator(collectionId, request)).thenReturn(expectedResponse);

    // When
    collectionController.addCollaborator(collectionId, request);

    // Then
    verify(collectionService).addCollaborator(eq(123L), argThat(r -> r.getUserId().equals(userId)));
  }

  @Test
  @DisplayName("Should return 201 Created for successful addCollaborator")
  @Tag("standard-processing")
  void shouldReturn201CreatedForSuccessfulAddCollaborator() {
    // Given
    Long collectionId = 1L;
    UUID collaboratorId = UUID.randomUUID();

    com.recipe_manager.model.dto.request.AddCollaboratorRequest request =
        com.recipe_manager.model.dto.request.AddCollaboratorRequest.builder()
            .userId(collaboratorId)
            .build();

    CollectionCollaboratorDto responseDto =
        CollectionCollaboratorDto.builder()
            .collectionId(collectionId)
            .userId(collaboratorId)
            .username("newuser")
            .grantedBy(UUID.randomUUID())
            .grantedByUsername("owner")
            .grantedAt(java.time.LocalDateTime.now())
            .build();

    ResponseEntity<CollectionCollaboratorDto> expectedResponse =
        ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(responseDto);

    when(collectionService.addCollaborator(collectionId, request)).thenReturn(expectedResponse);

    // When
    ResponseEntity<CollectionCollaboratorDto> response =
        collectionController.addCollaborator(collectionId, request);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(org.springframework.http.HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getUserId()).isEqualTo(collaboratorId);
  }

  @Test
  @DisplayName("Should pass validation on valid request for addCollaborator")
  @Tag("standard-processing")
  void shouldPassValidationOnValidRequestForAddCollaborator() {
    // Given
    Long collectionId = 1L;
    UUID userId = UUID.randomUUID();

    com.recipe_manager.model.dto.request.AddCollaboratorRequest validRequest =
        com.recipe_manager.model.dto.request.AddCollaboratorRequest.builder()
            .userId(userId)
            .build();

    CollectionCollaboratorDto responseDto =
        CollectionCollaboratorDto.builder()
            .collectionId(collectionId)
            .userId(userId)
            .username("valid")
            .grantedBy(UUID.randomUUID())
            .grantedByUsername("owner")
            .grantedAt(java.time.LocalDateTime.now())
            .build();

    ResponseEntity<CollectionCollaboratorDto> expectedResponse =
        ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(responseDto);

    when(collectionService.addCollaborator(collectionId, validRequest))
        .thenReturn(expectedResponse);

    // When
    ResponseEntity<CollectionCollaboratorDto> response =
        collectionController.addCollaborator(collectionId, validRequest);

    // Then
    assertThat(response).isNotNull();
    verify(collectionService).addCollaborator(collectionId, validRequest);
  }

  @Test
  @DisplayName("Should delegate to service for removeCollaborator")
  void shouldDelegateToServiceForRemoveCollaborator() {
    // Given
    Long collectionId = 1L;
    UUID userId = UUID.randomUUID();
    ResponseEntity<Void> mockResponse = ResponseEntity.noContent().build();
    when(collectionService.removeCollaborator(collectionId, userId)).thenReturn(mockResponse);

    // When
    ResponseEntity<Void> response = collectionController.removeCollaborator(collectionId, userId);

    // Then
    assertThat(response).isNotNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    verify(collectionService).removeCollaborator(collectionId, userId);
  }

  @Test
  @DisplayName("Should call service with correct parameters for removeCollaborator")
  void shouldCallServiceWithCorrectParametersForRemoveCollaborator() {
    // Given
    Long collectionId = 123L;
    UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    ResponseEntity<Void> mockResponse = ResponseEntity.noContent().build();
    when(collectionService.removeCollaborator(collectionId, userId)).thenReturn(mockResponse);

    // When
    collectionController.removeCollaborator(collectionId, userId);

    // Then
    verify(collectionService)
        .removeCollaborator(
            argThat(id -> id.equals(123L)),
            argThat(uid -> uid.equals(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))));
  }

  @Test
  @DisplayName("Should return 204 No Content for successful removeCollaborator")
  void shouldReturn204NoContentForSuccessfulRemoveCollaborator() {
    // Given
    Long collectionId = 1L;
    UUID userId = UUID.randomUUID();
    ResponseEntity<Void> mockResponse = ResponseEntity.noContent().build();
    when(collectionService.removeCollaborator(collectionId, userId)).thenReturn(mockResponse);

    // When
    ResponseEntity<Void> response = collectionController.removeCollaborator(collectionId, userId);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(response.getBody()).isNull();
  }

  @Test
  @DisplayName("Should handle UUID parameter correctly for removeCollaborator")
  void shouldHandleUuidParameterCorrectlyForRemoveCollaborator() {
    // Given
    Long collectionId = 1L;
    UUID validUserId = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
    ResponseEntity<Void> mockResponse = ResponseEntity.noContent().build();
    when(collectionService.removeCollaborator(collectionId, validUserId)).thenReturn(mockResponse);

    // When
    ResponseEntity<Void> response =
        collectionController.removeCollaborator(collectionId, validUserId);

    // Then
    assertThat(response).isNotNull();
    verify(collectionService).removeCollaborator(collectionId, validUserId);
  }
}
