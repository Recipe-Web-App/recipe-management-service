package com.recipe_manager.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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

import com.recipe_manager.model.dto.collection.CollectionRecipeDto;
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
    assertThat(response.getStatusCodeValue()).isEqualTo(200);
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
}
