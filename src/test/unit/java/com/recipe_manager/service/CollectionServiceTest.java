package com.recipe_manager.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;

import com.recipe_manager.exception.ResourceNotFoundException;
import com.recipe_manager.model.dto.collection.CollectionRecipeDto;
import com.recipe_manager.model.dto.request.CreateCollectionRequest;
import com.recipe_manager.model.dto.request.SearchCollectionsRequest;
import com.recipe_manager.model.dto.request.UpdateCollectionRequest;
import com.recipe_manager.model.dto.response.CollectionDetailsDto;
import com.recipe_manager.model.dto.response.CollectionDto;
import com.recipe_manager.model.entity.collection.RecipeCollection;
import com.recipe_manager.model.entity.collection.RecipeCollectionItem;
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.entity.collection.RecipeCollectionItemId;
import com.recipe_manager.model.enums.CollaborationMode;
import com.recipe_manager.model.enums.CollectionVisibility;
import com.recipe_manager.model.mapper.CollectionMapper;
import com.recipe_manager.model.mapper.RecipeCollectionMapper;
import com.recipe_manager.repository.collection.CollectionSummaryProjection;
import com.recipe_manager.repository.collection.RecipeCollectionRepository;
import com.recipe_manager.util.SecurityUtils;

/** Unit tests for CollectionService. */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
class CollectionServiceTest {

  @Mock private RecipeCollectionRepository recipeCollectionRepository;

  @Mock private CollectionMapper collectionMapper;

  @Mock private RecipeCollectionMapper recipeCollectionMapper;

  private CollectionService collectionService;

  private UUID testUserId;

  /**
   * Matcher for null or empty arrays.
   *
   * @param <T> the array type
   * @return matcher that accepts null or empty arrays
   */
  private static <T> T[] nullOrEmpty() {
    return argThat(arr -> arr == null || arr.length == 0);
  }

  @BeforeEach
  void setUp() {
    collectionService =
        new CollectionService(
            recipeCollectionRepository, collectionMapper, recipeCollectionMapper);
    testUserId = UUID.randomUUID();
  }

  @Test
  @DisplayName("Should get accessible collections successfully")
  @Tag("standard-processing")
  void shouldGetAccessibleCollectionsSuccessfully() {
    // Given
    Pageable pageable = PageRequest.of(0, 20);
    List<CollectionSummaryProjection> projections =
        Arrays.asList(createTestProjection(1L), createTestProjection(2L));
    Page<CollectionSummaryProjection> projectionPage = new PageImpl<>(projections, pageable, 2);

    CollectionDto dto1 = createTestDto(1L);
    CollectionDto dto2 = createTestDto(2L);

    when(recipeCollectionRepository.findAccessibleCollections(any(UUID.class), any(Pageable.class)))
        .thenReturn(projectionPage);
    when(collectionMapper.fromProjection(projections.get(0))).thenReturn(dto1);
    when(collectionMapper.fromProjection(projections.get(1))).thenReturn(dto2);

    // When
    ResponseEntity<Page<CollectionDto>> response;
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
      response = collectionService.getAccessibleCollections(pageable);
    }

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    Page<CollectionDto> resultPage = response.getBody();
    assertThat(resultPage.getContent()).hasSize(2);
    assertThat(resultPage.getTotalElements()).isEqualTo(2);
    assertThat(resultPage.getNumber()).isEqualTo(0);
    assertThat(resultPage.getSize()).isEqualTo(20);

    verify(recipeCollectionRepository).findAccessibleCollections(testUserId, pageable);
  }

  @Test
  @DisplayName("Should return empty page when user has no accessible collections")
  @Tag("standard-processing")
  void shouldReturnEmptyPageWhenNoAccessibleCollections() {
    // Given
    Pageable pageable = PageRequest.of(0, 20);
    Page<CollectionSummaryProjection> emptyPage =
        new PageImpl<>(Collections.emptyList(), pageable, 0);

    when(recipeCollectionRepository.findAccessibleCollections(any(UUID.class), any(Pageable.class)))
        .thenReturn(emptyPage);

    // When
    ResponseEntity<Page<CollectionDto>> response;
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
      response = collectionService.getAccessibleCollections(pageable);
    }

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    Page<CollectionDto> resultPage = response.getBody();
    assertThat(resultPage.getContent()).isEmpty();
    assertThat(resultPage.getTotalElements()).isEqualTo(0);

    verify(recipeCollectionRepository).findAccessibleCollections(testUserId, pageable);
  }

  @Test
  @DisplayName("Should handle custom page size")
  @Tag("standard-processing")
  void shouldHandleCustomPageSize() {
    // Given
    Pageable pageable = PageRequest.of(0, 5);
    List<CollectionSummaryProjection> projections =
        Arrays.asList(
            createTestProjection(1L),
            createTestProjection(2L),
            createTestProjection(3L),
            createTestProjection(4L),
            createTestProjection(5L));
    Page<CollectionSummaryProjection> projectionPage = new PageImpl<>(projections, pageable, 10);

    for (int i = 0; i < 5; i++) {
      when(collectionMapper.fromProjection(projections.get(i)))
          .thenReturn(createTestDto((long) (i + 1)));
    }

    when(recipeCollectionRepository.findAccessibleCollections(any(UUID.class), any(Pageable.class)))
        .thenReturn(projectionPage);

    // When
    ResponseEntity<Page<CollectionDto>> response;
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
      response = collectionService.getAccessibleCollections(pageable);
    }

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    Page<CollectionDto> resultPage = response.getBody();
    assertThat(resultPage.getContent()).hasSize(5);
    assertThat(resultPage.getTotalElements()).isEqualTo(10);
    assertThat(resultPage.getTotalPages()).isEqualTo(2);
    assertThat(resultPage.getSize()).isEqualTo(5);
  }

  @Test
  @DisplayName("Should handle second page request")
  @Tag("standard-processing")
  void shouldHandleSecondPageRequest() {
    // Given
    Pageable pageable = PageRequest.of(1, 10); // Second page
    List<CollectionSummaryProjection> projections =
        Arrays.asList(createTestProjection(11L), createTestProjection(12L));
    Page<CollectionSummaryProjection> projectionPage = new PageImpl<>(projections, pageable, 25);

    when(collectionMapper.fromProjection(projections.get(0))).thenReturn(createTestDto(11L));
    when(collectionMapper.fromProjection(projections.get(1))).thenReturn(createTestDto(12L));

    when(recipeCollectionRepository.findAccessibleCollections(any(UUID.class), any(Pageable.class)))
        .thenReturn(projectionPage);

    // When
    ResponseEntity<Page<CollectionDto>> response;
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
      response = collectionService.getAccessibleCollections(pageable);
    }

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    Page<CollectionDto> resultPage = response.getBody();
    assertThat(resultPage.getContent()).hasSize(2);
    assertThat(resultPage.getNumber()).isEqualTo(1);
    assertThat(resultPage.getTotalPages()).isEqualTo(3);
  }

  @Test
  @DisplayName("Should extract correct user ID from security context")
  @Tag("standard-processing")
  void shouldExtractCorrectUserIdFromSecurityContext() {
    // Given
    UUID expectedUserId = UUID.randomUUID();
    Pageable pageable = PageRequest.of(0, 20);
    Page<CollectionSummaryProjection> emptyPage =
        new PageImpl<>(Collections.emptyList(), pageable, 0);

    when(recipeCollectionRepository.findAccessibleCollections(expectedUserId, pageable))
        .thenReturn(emptyPage);

    // When
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(expectedUserId);
      collectionService.getAccessibleCollections(pageable);
    }

    // Then
    verify(recipeCollectionRepository).findAccessibleCollections(expectedUserId, pageable);
  }

  @Test
  @DisplayName("Should map all projections to DTOs")
  @Tag("standard-processing")
  void shouldMapAllProjectionsToDtos() {
    // Given
    Pageable pageable = PageRequest.of(0, 3);
    List<CollectionSummaryProjection> projections =
        Arrays.asList(
            createTestProjection(1L), createTestProjection(2L), createTestProjection(3L));
    Page<CollectionSummaryProjection> projectionPage = new PageImpl<>(projections, pageable, 3);

    CollectionDto dto1 = createTestDto(1L);
    CollectionDto dto2 = createTestDto(2L);
    CollectionDto dto3 = createTestDto(3L);

    when(recipeCollectionRepository.findAccessibleCollections(any(UUID.class), any(Pageable.class)))
        .thenReturn(projectionPage);
    when(collectionMapper.fromProjection(projections.get(0))).thenReturn(dto1);
    when(collectionMapper.fromProjection(projections.get(1))).thenReturn(dto2);
    when(collectionMapper.fromProjection(projections.get(2))).thenReturn(dto3);

    // When
    ResponseEntity<Page<CollectionDto>> response;
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
      response = collectionService.getAccessibleCollections(pageable);
    }

    // Then
    assertThat(response.getBody()).isNotNull();
    List<CollectionDto> content = response.getBody().getContent();
    assertThat(content).hasSize(3);
    assertThat(content.get(0).getCollectionId()).isEqualTo(1L);
    assertThat(content.get(1).getCollectionId()).isEqualTo(2L);
    assertThat(content.get(2).getCollectionId()).isEqualTo(3L);

    verify(collectionMapper).fromProjection(projections.get(0));
    verify(collectionMapper).fromProjection(projections.get(1));
    verify(collectionMapper).fromProjection(projections.get(2));
  }

  @Test
  @DisplayName("Should preserve pagination metadata")
  @Tag("standard-processing")
  void shouldPreservePaginationMetadata() {
    // Given
    Pageable pageable = PageRequest.of(2, 10); // Third page, size 10
    List<CollectionSummaryProjection> projections =
        Arrays.asList(createTestProjection(21L));
    Page<CollectionSummaryProjection> projectionPage = new PageImpl<>(projections, pageable, 50);

    when(collectionMapper.fromProjection(any())).thenReturn(createTestDto(21L));

    when(recipeCollectionRepository.findAccessibleCollections(any(UUID.class), any(Pageable.class)))
        .thenReturn(projectionPage);

    // When
    ResponseEntity<Page<CollectionDto>> response;
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
      response = collectionService.getAccessibleCollections(pageable);
    }

    // Then
    assertThat(response.getBody()).isNotNull();
    Page<CollectionDto> resultPage = response.getBody();
    assertThat(resultPage.getNumber()).isEqualTo(2);
    assertThat(resultPage.getSize()).isEqualTo(10);
    assertThat(resultPage.getTotalElements()).isEqualTo(50);
    assertThat(resultPage.getTotalPages()).isEqualTo(5);
    assertThat(resultPage.isFirst()).isFalse();
    assertThat(resultPage.isLast()).isFalse();
  }

  private CollectionSummaryProjection createTestProjection(Long collectionId) {
    return new CollectionSummaryProjection() {
      @Override
      public Long getCollectionId() {
        return collectionId;
      }

      @Override
      public String getName() {
        return "Test Collection " + collectionId;
      }

      @Override
      public String getDescription() {
        return "Test Description";
      }

      @Override
      public CollectionVisibility getVisibility() {
        return CollectionVisibility.PUBLIC;
      }

      @Override
      public CollaborationMode getCollaborationMode() {
        return CollaborationMode.OWNER_ONLY;
      }

      @Override
      public UUID getOwnerId() {
        return testUserId;
      }

      @Override
      public Integer getRecipeCount() {
        return 5;
      }

      @Override
      public Integer getCollaboratorCount() {
        return 0;
      }

      @Override
      public LocalDateTime getCreatedAt() {
        return LocalDateTime.now().minusDays(1);
      }

      @Override
      public LocalDateTime getUpdatedAt() {
        return LocalDateTime.now();
      }
    };
  }

  private CollectionDto createTestDto(Long collectionId) {
    return CollectionDto.builder()
        .collectionId(collectionId)
        .userId(testUserId)
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
  @DisplayName("Should create collection successfully")
  @Tag("standard-processing")
  void shouldCreateCollectionSuccessfully() {
    // Given
    CreateCollectionRequest request =
        CreateCollectionRequest.builder()
            .name("New Collection")
            .description("Test Description")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    RecipeCollection entityToSave = RecipeCollection.builder().name("New Collection").build();

    RecipeCollection savedEntity =
        RecipeCollection.builder()
            .collectionId(1L)
            .userId(testUserId)
            .name("New Collection")
            .description("Test Description")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    CollectionDto expectedDto = createTestDto(1L);

    when(recipeCollectionMapper.fromRequest(request)).thenReturn(entityToSave);
    when(recipeCollectionRepository.save(any(RecipeCollection.class))).thenReturn(savedEntity);
    when(collectionMapper.toDto(savedEntity)).thenReturn(expectedDto);

    // When
    ResponseEntity<CollectionDto> response;
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
      response = collectionService.createCollection(request);
    }

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).isEqualTo(expectedDto);

    verify(recipeCollectionMapper).fromRequest(request);
    verify(recipeCollectionRepository).save(any(RecipeCollection.class));
    verify(collectionMapper).toDto(savedEntity);
  }

  @Test
  @DisplayName("Should extract user ID from security context when creating collection")
  @Tag("standard-processing")
  void shouldExtractUserIdFromSecurityContextWhenCreatingCollection() {
    // Given
    UUID expectedUserId = UUID.randomUUID();
    CreateCollectionRequest request =
        CreateCollectionRequest.builder()
            .name("New Collection")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    RecipeCollection entityToSave = RecipeCollection.builder().build();
    RecipeCollection savedEntity = RecipeCollection.builder().collectionId(1L).build();

    when(recipeCollectionMapper.fromRequest(any())).thenReturn(entityToSave);
    when(recipeCollectionRepository.save(any())).thenReturn(savedEntity);
    when(collectionMapper.toDto(any(RecipeCollection.class))).thenReturn(createTestDto(1L));

    // When
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(expectedUserId);
      collectionService.createCollection(request);

      // Then - Verify SecurityUtils.getCurrentUserId() was called
      securityUtilsMock.verify(SecurityUtils::getCurrentUserId);
    }
  }

  @Test
  @DisplayName("Should set user ID on entity before saving")
  @Tag("standard-processing")
  void shouldSetUserIdOnEntityBeforeSaving() {
    // Given
    CreateCollectionRequest request =
        CreateCollectionRequest.builder()
            .name("New Collection")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    RecipeCollection entityToSave = RecipeCollection.builder().name("New Collection").build();

    RecipeCollection savedEntity = RecipeCollection.builder().collectionId(1L).build();

    when(recipeCollectionMapper.fromRequest(request)).thenReturn(entityToSave);
    when(recipeCollectionRepository.save(any(RecipeCollection.class))).thenReturn(savedEntity);
    when(collectionMapper.toDto(any(RecipeCollection.class))).thenReturn(createTestDto(1L));

    // When
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
      collectionService.createCollection(request);
    }

    // Then - Capture the argument passed to save() and verify userId was set
    ArgumentCaptor<RecipeCollection> captor = ArgumentCaptor.forClass(RecipeCollection.class);
    verify(recipeCollectionRepository).save(captor.capture());

    RecipeCollection capturedEntity = captor.getValue();
    assertThat(capturedEntity.getUserId()).isEqualTo(testUserId);
  }

  @Test
  @DisplayName("Should save collection to repository")
  @Tag("standard-processing")
  void shouldSaveCollectionToRepository() {
    // Given
    CreateCollectionRequest request =
        CreateCollectionRequest.builder()
            .name("New Collection")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    RecipeCollection entityToSave = RecipeCollection.builder().build();
    RecipeCollection savedEntity = RecipeCollection.builder().collectionId(1L).build();

    when(recipeCollectionMapper.fromRequest(any())).thenReturn(entityToSave);
    when(recipeCollectionRepository.save(any())).thenReturn(savedEntity);
    when(collectionMapper.toDto(any(RecipeCollection.class))).thenReturn(createTestDto(1L));

    // When
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
      collectionService.createCollection(request);
    }

    // Then
    verify(recipeCollectionRepository).save(any(RecipeCollection.class));
  }

  @Test
  @DisplayName("Should map saved entity to DTO")
  @Tag("standard-processing")
  void shouldMapSavedEntityToDto() {
    // Given
    CreateCollectionRequest request =
        CreateCollectionRequest.builder()
            .name("New Collection")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    RecipeCollection entityToSave = RecipeCollection.builder().build();
    RecipeCollection savedEntity =
        RecipeCollection.builder()
            .collectionId(1L)
            .userId(testUserId)
            .name("New Collection")
            .build();

    when(recipeCollectionMapper.fromRequest(any())).thenReturn(entityToSave);
    when(recipeCollectionRepository.save(any())).thenReturn(savedEntity);
    when(collectionMapper.toDto(savedEntity)).thenReturn(createTestDto(1L));

    // When
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
      collectionService.createCollection(request);
    }

    // Then
    verify(collectionMapper).toDto(savedEntity);
  }

  @Test
  @DisplayName("Should return 201 Created status")
  @Tag("standard-processing")
  void shouldReturn201CreatedStatus() {
    // Given
    CreateCollectionRequest request =
        CreateCollectionRequest.builder()
            .name("New Collection")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    RecipeCollection entityToSave = RecipeCollection.builder().build();
    RecipeCollection savedEntity = RecipeCollection.builder().collectionId(1L).build();

    when(recipeCollectionMapper.fromRequest(any())).thenReturn(entityToSave);
    when(recipeCollectionRepository.save(any())).thenReturn(savedEntity);
    when(collectionMapper.toDto(any(RecipeCollection.class))).thenReturn(createTestDto(1L));

    // When
    ResponseEntity<CollectionDto> response;
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
      response = collectionService.createCollection(request);
    }

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getStatusCodeValue()).isEqualTo(201);
  }

  @Test
  @DisplayName("Should handle private collection creation")
  @Tag("standard-processing")
  void shouldHandlePrivateCollectionCreation() {
    // Given
    CreateCollectionRequest request =
        CreateCollectionRequest.builder()
            .name("Private Collection")
            .visibility(CollectionVisibility.PRIVATE)
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .build();

    RecipeCollection entityToSave = RecipeCollection.builder().build();
    RecipeCollection savedEntity =
        RecipeCollection.builder()
            .collectionId(1L)
            .visibility(CollectionVisibility.PRIVATE)
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .build();

    CollectionDto expectedDto =
        CollectionDto.builder()
            .collectionId(1L)
            .visibility(CollectionVisibility.PRIVATE)
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .recipeCount(0)
            .collaboratorCount(0)
            .build();

    when(recipeCollectionMapper.fromRequest(any())).thenReturn(entityToSave);
    when(recipeCollectionRepository.save(any())).thenReturn(savedEntity);
    when(collectionMapper.toDto(savedEntity)).thenReturn(expectedDto);

    // When
    ResponseEntity<CollectionDto> response;
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
      response = collectionService.createCollection(request);
    }

    // Then
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getVisibility()).isEqualTo(CollectionVisibility.PRIVATE);
    assertThat(response.getBody().getCollaborationMode())
        .isEqualTo(CollaborationMode.SPECIFIC_USERS);
  }

  @Test
  @DisplayName("Should create collection with null description")
  @Tag("standard-processing")
  void shouldCreateCollectionWithNullDescription() {
    // Given
    CreateCollectionRequest request =
        CreateCollectionRequest.builder()
            .name("Collection Without Description")
            .description(null)
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    RecipeCollection entityToSave = RecipeCollection.builder().build();
    RecipeCollection savedEntity =
        RecipeCollection.builder().collectionId(1L).description(null).build();

    CollectionDto expectedDto = createTestDto(1L);

    when(recipeCollectionMapper.fromRequest(any())).thenReturn(entityToSave);
    when(recipeCollectionRepository.save(any())).thenReturn(savedEntity);
    when(collectionMapper.toDto(any(RecipeCollection.class))).thenReturn(expectedDto);

    // When
    ResponseEntity<CollectionDto> response;
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
      response = collectionService.createCollection(request);
    }

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  @DisplayName("Should get collection by ID successfully when user is owner")
  @Tag("standard-processing")
  void shouldGetCollectionByIdSuccessfullyWhenUserIsOwner() {
    // Given
    Long collectionId = 1L;
    RecipeCollection collection = createTestCollectionWithRecipes(collectionId);
    CollectionDetailsDto expectedDto = createTestDetailsDto(collectionId);

    when(recipeCollectionRepository.hasViewAccess(collectionId, testUserId)).thenReturn(true);
    when(recipeCollectionRepository.findByIdWithItems(collectionId))
        .thenReturn(Optional.of(collection));
    when(collectionMapper.toDetailsDto(collection)).thenReturn(expectedDto);

    // When
    ResponseEntity<CollectionDetailsDto> response;
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
      response = collectionService.getCollectionById(collectionId);
    }

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).isEqualTo(expectedDto);

    verify(recipeCollectionRepository).hasViewAccess(collectionId, testUserId);
    verify(recipeCollectionRepository).findByIdWithItems(collectionId);
    verify(collectionMapper).toDetailsDto(collection);
  }

  @Test
  @DisplayName("Should get collection by ID successfully when user has view access")
  @Tag("standard-processing")
  void shouldGetCollectionByIdSuccessfullyWhenUserHasAccess() {
    // Given
    Long collectionId = 2L;
    UUID otherUserId = UUID.randomUUID();
    RecipeCollection collection = createTestCollectionWithRecipes(collectionId);
    collection.setUserId(otherUserId); // Collection owned by different user
    CollectionDetailsDto expectedDto = createTestDetailsDto(collectionId);

    when(recipeCollectionRepository.hasViewAccess(collectionId, testUserId)).thenReturn(true);
    when(recipeCollectionRepository.findByIdWithItems(collectionId))
        .thenReturn(Optional.of(collection));
    when(collectionMapper.toDetailsDto(collection)).thenReturn(expectedDto);

    // When
    ResponseEntity<CollectionDetailsDto> response;
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
      response = collectionService.getCollectionById(collectionId);
    }

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    verify(recipeCollectionRepository).hasViewAccess(collectionId, testUserId);
  }

  @Test
  @DisplayName("Should throw ResourceNotFoundException when user has no access")
  @Tag("standard-processing")
  void shouldThrowResourceNotFoundExceptionWhenUserHasNoAccess() {
    // Given
    Long collectionId = 3L;

    when(recipeCollectionRepository.hasViewAccess(collectionId, testUserId)).thenReturn(false);

    // When/Then
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      assertThatThrownBy(() -> collectionService.getCollectionById(collectionId))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessage("Collection not found or access denied");
    }

    verify(recipeCollectionRepository).hasViewAccess(collectionId, testUserId);
  }

  @Test
  @DisplayName("Should throw ResourceNotFoundException when collection does not exist")
  @Tag("standard-processing")
  void shouldThrowResourceNotFoundExceptionWhenCollectionDoesNotExist() {
    // Given
    Long collectionId = 999L;

    when(recipeCollectionRepository.hasViewAccess(collectionId, testUserId)).thenReturn(true);
    when(recipeCollectionRepository.findByIdWithItems(collectionId)).thenReturn(Optional.empty());

    // When/Then
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      assertThatThrownBy(() -> collectionService.getCollectionById(collectionId))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessage("Collection not found");
    }

    verify(recipeCollectionRepository).hasViewAccess(collectionId, testUserId);
    verify(recipeCollectionRepository).findByIdWithItems(collectionId);
  }

  @Test
  @DisplayName("Should handle collection with empty recipes list")
  @Tag("standard-processing")
  void shouldHandleCollectionWithEmptyRecipesList() {
    // Given
    Long collectionId = 4L;
    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(testUserId)
            .name("Empty Collection")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .collectionItems(Collections.emptyList())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    CollectionDetailsDto expectedDto =
        CollectionDetailsDto.builder()
            .collectionId(collectionId)
            .userId(testUserId)
            .name("Empty Collection")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .recipeCount(0)
            .collaboratorCount(0)
            .recipes(Collections.emptyList())
            .createdAt(collection.getCreatedAt())
            .updatedAt(collection.getUpdatedAt())
            .build();

    when(recipeCollectionRepository.hasViewAccess(collectionId, testUserId)).thenReturn(true);
    when(recipeCollectionRepository.findByIdWithItems(collectionId))
        .thenReturn(Optional.of(collection));
    when(collectionMapper.toDetailsDto(collection)).thenReturn(expectedDto);

    // When
    ResponseEntity<CollectionDetailsDto> response;
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
      response = collectionService.getCollectionById(collectionId);
    }

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getRecipes()).isEmpty();
    assertThat(response.getBody().getRecipeCount()).isEqualTo(0);
  }

  @Test
  @DisplayName("Should get collection with multiple recipes in order")
  @Tag("standard-processing")
  void shouldGetCollectionWithMultipleRecipesInOrder() {
    // Given
    Long collectionId = 5L;
    RecipeCollection collection = createTestCollectionWithMultipleRecipes(collectionId);

    CollectionRecipeDto recipe1 =
        CollectionRecipeDto.builder()
            .recipeId(1L)
            .recipeTitle("First Recipe")
            .displayOrder(10)
            .addedBy(testUserId)
            .addedAt(LocalDateTime.now())
            .build();

    CollectionRecipeDto recipe2 =
        CollectionRecipeDto.builder()
            .recipeId(2L)
            .recipeTitle("Second Recipe")
            .displayOrder(20)
            .addedBy(testUserId)
            .addedAt(LocalDateTime.now())
            .build();

    CollectionDetailsDto expectedDto =
        CollectionDetailsDto.builder()
            .collectionId(collectionId)
            .userId(testUserId)
            .name("Multi-Recipe Collection")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .recipeCount(2)
            .collaboratorCount(0)
            .recipes(Arrays.asList(recipe1, recipe2))
            .createdAt(collection.getCreatedAt())
            .updatedAt(collection.getUpdatedAt())
            .build();

    when(recipeCollectionRepository.hasViewAccess(collectionId, testUserId)).thenReturn(true);
    when(recipeCollectionRepository.findByIdWithItems(collectionId))
        .thenReturn(Optional.of(collection));
    when(collectionMapper.toDetailsDto(collection)).thenReturn(expectedDto);

    // When
    ResponseEntity<CollectionDetailsDto> response;
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
      response = collectionService.getCollectionById(collectionId);
    }

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getRecipes()).hasSize(2);
    assertThat(response.getBody().getRecipeCount()).isEqualTo(2);
  }

  @Test
  @DisplayName("Should extract correct user ID from security context for get by ID")
  @Tag("standard-processing")
  void shouldExtractCorrectUserIdFromSecurityContextForGetById() {
    // Given
    UUID expectedUserId = UUID.randomUUID();
    Long collectionId = 6L;
    RecipeCollection collection = createTestCollectionWithRecipes(collectionId);
    CollectionDetailsDto expectedDto = createTestDetailsDto(collectionId);

    when(recipeCollectionRepository.hasViewAccess(collectionId, expectedUserId)).thenReturn(true);
    when(recipeCollectionRepository.findByIdWithItems(collectionId))
        .thenReturn(Optional.of(collection));
    when(collectionMapper.toDetailsDto(collection)).thenReturn(expectedDto);

    // When
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(expectedUserId);
      collectionService.getCollectionById(collectionId);

      // Then - Verify SecurityUtils.getCurrentUserId() was called
      securityUtilsMock.verify(SecurityUtils::getCurrentUserId);
    }

    verify(recipeCollectionRepository).hasViewAccess(collectionId, expectedUserId);
  }

  private RecipeCollection createTestCollectionWithRecipes(Long collectionId) {
    Recipe recipe =
        Recipe.builder().recipeId(1L).title("Test Recipe").description("Test Description").build();

    RecipeCollectionItemId itemId = new RecipeCollectionItemId(collectionId, 1L);

    RecipeCollectionItem item =
        RecipeCollectionItem.builder()
            .id(itemId)
            .recipe(recipe)
            .displayOrder(10)
            .addedBy(testUserId)
            .addedAt(LocalDateTime.now())
            .build();

    return RecipeCollection.builder()
        .collectionId(collectionId)
        .userId(testUserId)
        .name("Test Collection")
        .description("Test Description")
        .visibility(CollectionVisibility.PUBLIC)
        .collaborationMode(CollaborationMode.OWNER_ONLY)
        .collectionItems(Arrays.asList(item))
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
  }

  private RecipeCollection createTestCollectionWithMultipleRecipes(Long collectionId) {
    Recipe recipe1 =
        Recipe.builder().recipeId(1L).title("First Recipe").description("First Description").build();

    Recipe recipe2 =
        Recipe.builder()
            .recipeId(2L)
            .title("Second Recipe")
            .description("Second Description")
            .build();

    RecipeCollectionItemId itemId1 = new RecipeCollectionItemId(collectionId, 1L);
    RecipeCollectionItemId itemId2 = new RecipeCollectionItemId(collectionId, 2L);

    RecipeCollectionItem item1 =
        RecipeCollectionItem.builder()
            .id(itemId1)
            .recipe(recipe1)
            .displayOrder(10)
            .addedBy(testUserId)
            .addedAt(LocalDateTime.now())
            .build();

    RecipeCollectionItem item2 =
        RecipeCollectionItem.builder()
            .id(itemId2)
            .recipe(recipe2)
            .displayOrder(20)
            .addedBy(testUserId)
            .addedAt(LocalDateTime.now())
            .build();

    return RecipeCollection.builder()
        .collectionId(collectionId)
        .userId(testUserId)
        .name("Multi-Recipe Collection")
        .visibility(CollectionVisibility.PUBLIC)
        .collaborationMode(CollaborationMode.OWNER_ONLY)
        .collectionItems(Arrays.asList(item1, item2))
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
  }

  private CollectionDetailsDto createTestDetailsDto(Long collectionId) {
    CollectionRecipeDto recipeDto =
        CollectionRecipeDto.builder()
            .recipeId(1L)
            .recipeTitle("Test Recipe")
            .recipeDescription("Test Description")
            .displayOrder(10)
            .addedBy(testUserId)
            .addedAt(LocalDateTime.now())
            .build();

    return CollectionDetailsDto.builder()
        .collectionId(collectionId)
        .userId(testUserId)
        .name("Test Collection")
        .description("Test Description")
        .visibility(CollectionVisibility.PUBLIC)
        .collaborationMode(CollaborationMode.OWNER_ONLY)
        .recipeCount(1)
        .collaboratorCount(0)
        .recipes(Arrays.asList(recipeDto))
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
  }

  @Test
  @DisplayName("Should update collection successfully with all fields")
  @Tag("standard-processing")
  void shouldUpdateCollectionWithAllFields() {
    // Given
    Long collectionId = 1L;
    UpdateCollectionRequest request =
        UpdateCollectionRequest.builder()
            .name("Updated Name")
            .description("Updated Description")
            .visibility(CollectionVisibility.PRIVATE)
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .build();

    RecipeCollection existingCollection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(testUserId)
            .name("Original Name")
            .description("Original Description")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    RecipeCollection updatedCollection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(testUserId)
            .name("Updated Name")
            .description("Updated Description")
            .visibility(CollectionVisibility.PRIVATE)
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .build();

    CollectionDto expectedDto =
        CollectionDto.builder()
            .collectionId(collectionId)
            .userId(testUserId)
            .name("Updated Name")
            .visibility(CollectionVisibility.PRIVATE)
            .build();

    when(recipeCollectionRepository.findById(collectionId))
        .thenReturn(Optional.of(existingCollection));
    when(recipeCollectionRepository.save(any(RecipeCollection.class)))
        .thenReturn(updatedCollection);
    when(collectionMapper.toDto(updatedCollection)).thenReturn(expectedDto);

    // When
    ResponseEntity<CollectionDto> response;
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
      response = collectionService.updateCollection(collectionId, request);
    }

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo(expectedDto);
    verify(recipeCollectionRepository).findById(collectionId);
    verify(recipeCollectionRepository).save(any(RecipeCollection.class));
  }

  @Test
  @DisplayName("Should update only name when only name provided")
  @Tag("standard-processing")
  void shouldUpdateOnlyNameWhenOnlyNameProvided() {
    // Given
    Long collectionId = 2L;
    UpdateCollectionRequest request = UpdateCollectionRequest.builder().name("New Name Only").build();

    RecipeCollection existingCollection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(testUserId)
            .name("Old Name")
            .description("Original Description")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    when(recipeCollectionRepository.findById(collectionId))
        .thenReturn(Optional.of(existingCollection));
    when(recipeCollectionRepository.save(any(RecipeCollection.class)))
        .thenReturn(existingCollection);
    when(collectionMapper.toDto(any(RecipeCollection.class)))
        .thenReturn(CollectionDto.builder().build());

    // When
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
      collectionService.updateCollection(collectionId, request);
    }

    // Then
    assertThat(existingCollection.getName()).isEqualTo("New Name Only");
    assertThat(existingCollection.getDescription()).isEqualTo("Original Description");
    assertThat(existingCollection.getVisibility()).isEqualTo(CollectionVisibility.PUBLIC);
  }

  @Test
  @DisplayName("Should throw AccessDeniedException when user is not owner")
  @Tag("error-handling")
  void shouldThrowAccessDeniedExceptionWhenUserIsNotOwner() {
    // Given
    Long collectionId = 3L;
    UUID otherUserId = UUID.randomUUID();
    UpdateCollectionRequest request = UpdateCollectionRequest.builder().name("Updated").build();

    RecipeCollection existingCollection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(otherUserId) // Different owner
            .name("Original")
            .build();

    when(recipeCollectionRepository.findById(collectionId))
        .thenReturn(Optional.of(existingCollection));

    // When/Then
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      assertThatThrownBy(() -> collectionService.updateCollection(collectionId, request))
          .isInstanceOf(AccessDeniedException.class)
          .hasMessage("Only the collection owner can update it");
    }

    verify(recipeCollectionRepository).findById(collectionId);
    verify(recipeCollectionRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should throw ResourceNotFoundException when collection not found")
  @Tag("error-handling")
  void shouldThrowResourceNotFoundExceptionOnUpdate() {
    // Given
    Long collectionId = 999L;
    UpdateCollectionRequest request = UpdateCollectionRequest.builder().name("Updated").build();

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.empty());

    // When/Then
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      assertThatThrownBy(() -> collectionService.updateCollection(collectionId, request))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessage("Collection not found");
    }

    verify(recipeCollectionRepository).findById(collectionId);
    verify(recipeCollectionRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should update only visibility when only visibility provided")
  @Tag("standard-processing")
  void shouldUpdateOnlyVisibility() {
    // Given
    Long collectionId = 4L;
    UpdateCollectionRequest request =
        UpdateCollectionRequest.builder().visibility(CollectionVisibility.FRIENDS_ONLY).build();

    RecipeCollection existingCollection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(testUserId)
            .name("Name")
            .visibility(CollectionVisibility.PUBLIC)
            .build();

    when(recipeCollectionRepository.findById(collectionId))
        .thenReturn(Optional.of(existingCollection));
    when(recipeCollectionRepository.save(any(RecipeCollection.class)))
        .thenReturn(existingCollection);
    when(collectionMapper.toDto(any(RecipeCollection.class)))
        .thenReturn(CollectionDto.builder().build());

    // When
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
      collectionService.updateCollection(collectionId, request);
    }

    // Then
    assertThat(existingCollection.getVisibility()).isEqualTo(CollectionVisibility.FRIENDS_ONLY);
    verify(recipeCollectionRepository).save(existingCollection);
  }

  @Test
  @DisplayName("Should update only collaboration mode when only mode provided")
  @Tag("standard-processing")
  void shouldUpdateOnlyCollaborationMode() {
    // Given
    Long collectionId = 5L;
    UpdateCollectionRequest request =
        UpdateCollectionRequest.builder().collaborationMode(CollaborationMode.ALL_USERS).build();

    RecipeCollection existingCollection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(testUserId)
            .name("Name")
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    when(recipeCollectionRepository.findById(collectionId))
        .thenReturn(Optional.of(existingCollection));
    when(recipeCollectionRepository.save(any(RecipeCollection.class)))
        .thenReturn(existingCollection);
    when(collectionMapper.toDto(any(RecipeCollection.class)))
        .thenReturn(CollectionDto.builder().build());

    // When
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
      collectionService.updateCollection(collectionId, request);
    }

    // Then
    assertThat(existingCollection.getCollaborationMode()).isEqualTo(CollaborationMode.ALL_USERS);
  }

  @Test
  @DisplayName("Should extract correct user ID from security context for update")
  @Tag("standard-processing")
  void shouldExtractCorrectUserIdForUpdate() {
    // Given
    UUID specificUserId = UUID.randomUUID();
    Long collectionId = 6L;
    UpdateCollectionRequest request = UpdateCollectionRequest.builder().name("Updated").build();

    RecipeCollection existingCollection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(specificUserId)
            .name("Original")
            .build();

    when(recipeCollectionRepository.findById(collectionId))
        .thenReturn(Optional.of(existingCollection));
    when(recipeCollectionRepository.save(any(RecipeCollection.class)))
        .thenReturn(existingCollection);
    when(collectionMapper.toDto(any(RecipeCollection.class)))
        .thenReturn(CollectionDto.builder().build());

    // When
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(specificUserId);
      collectionService.updateCollection(collectionId, request);

      // Then - Verify SecurityUtils.getCurrentUserId() was called
      securityUtilsMock.verify(SecurityUtils::getCurrentUserId);
    }
  }

  @Test
  @DisplayName("Should delete collection successfully when user is owner")
  @Tag("standard-processing")
  void shouldDeleteCollectionSuccessfully() {
    // Given
    Long collectionId = 7L;
    RecipeCollection existingCollection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(testUserId)
            .name("Collection to Delete")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    when(recipeCollectionRepository.findById(collectionId))
        .thenReturn(Optional.of(existingCollection));

    // When
    ResponseEntity<Void> response;
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
      response = collectionService.deleteCollection(collectionId);
    }

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(response.getBody()).isNull();
    verify(recipeCollectionRepository).findById(collectionId);
    verify(recipeCollectionRepository).delete(existingCollection);
  }

  @Test
  @DisplayName("Should throw ResourceNotFoundException when collection not found on delete")
  @Tag("error-handling")
  void shouldThrowResourceNotFoundExceptionWhenCollectionNotFoundOnDelete() {
    // Given
    Long nonExistentId = 999L;

    when(recipeCollectionRepository.findById(nonExistentId)).thenReturn(Optional.empty());

    // When/Then
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      assertThatThrownBy(() -> collectionService.deleteCollection(nonExistentId))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessage("Collection not found");
    }

    verify(recipeCollectionRepository).findById(nonExistentId);
    verify(recipeCollectionRepository, never()).delete(any());
  }

  @Test
  @DisplayName("Should throw AccessDeniedException when user is not owner on delete")
  @Tag("error-handling")
  void shouldThrowAccessDeniedExceptionWhenUserIsNotOwnerOnDelete() {
    // Given
    Long collectionId = 8L;
    UUID otherUserId = UUID.randomUUID();
    RecipeCollection existingCollection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(otherUserId) // Different owner
            .name("Someone else's collection")
            .build();

    when(recipeCollectionRepository.findById(collectionId))
        .thenReturn(Optional.of(existingCollection));

    // When/Then
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      assertThatThrownBy(() -> collectionService.deleteCollection(collectionId))
          .isInstanceOf(AccessDeniedException.class)
          .hasMessage("Only the collection owner can delete it");
    }

    verify(recipeCollectionRepository).findById(collectionId);
    verify(recipeCollectionRepository, never()).delete(any());
  }

  @Test
  @DisplayName("Should extract correct user ID from security context for delete")
  @Tag("standard-processing")
  void shouldExtractCorrectUserIdFromSecurityContextForDelete() {
    // Given
    UUID specificUserId = UUID.randomUUID();
    Long collectionId = 9L;
    RecipeCollection existingCollection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(specificUserId)
            .name("Collection to Delete")
            .build();

    when(recipeCollectionRepository.findById(collectionId))
        .thenReturn(Optional.of(existingCollection));

    // When
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(specificUserId);
      collectionService.deleteCollection(collectionId);

      // Then - Verify SecurityUtils.getCurrentUserId() was called
      securityUtilsMock.verify(SecurityUtils::getCurrentUserId);
    }

    verify(recipeCollectionRepository).findById(collectionId);
  }

  @Test
  @DisplayName("Should call repository delete method when deleting collection")
  @Tag("standard-processing")
  void shouldCallRepositoryDeleteMethod() {
    // Given
    Long collectionId = 10L;
    RecipeCollection existingCollection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(testUserId)
            .name("Collection to Delete")
            .visibility(CollectionVisibility.PRIVATE)
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .build();

    when(recipeCollectionRepository.findById(collectionId))
        .thenReturn(Optional.of(existingCollection));

    // When
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
      collectionService.deleteCollection(collectionId);
    }

    // Then - Verify delete was called with the correct entity
    ArgumentCaptor<RecipeCollection> captor = ArgumentCaptor.forClass(RecipeCollection.class);
    verify(recipeCollectionRepository).delete(captor.capture());

    RecipeCollection deletedCollection = captor.getValue();
    assertThat(deletedCollection.getCollectionId()).isEqualTo(collectionId);
    assertThat(deletedCollection.getUserId()).isEqualTo(testUserId);
    assertThat(deletedCollection.getName()).isEqualTo("Collection to Delete");
  }

  @Test
  @DisplayName("Should search collections with text query")
  @Tag("standard-processing")
  void shouldSearchCollectionsWithTextQuery() {
    // Given
    SearchCollectionsRequest request = SearchCollectionsRequest.builder().query("pasta").build();

    Pageable pageable = PageRequest.of(0, 20);

    RecipeCollection collection1 =
        RecipeCollection.builder()
            .collectionId(1L)
            .userId(testUserId)
            .name("Pasta Recipes")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    Page<RecipeCollection> collectionPage = new PageImpl<>(List.of(collection1));

    when(recipeCollectionRepository.searchCollections(
            eq("pasta"), nullOrEmpty(), nullOrEmpty(), isNull(), isNull(), isNull(), eq(pageable)))
        .thenReturn(collectionPage);

    when(collectionMapper.toDto(any(RecipeCollection.class)))
        .thenReturn(CollectionDto.builder().collectionId(1L).name("Pasta Recipes").build());

    // When
    ResponseEntity<Page<CollectionDto>> response =
        collectionService.searchCollections(request, pageable);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(1);
    assertThat(response.getBody().getContent().get(0).getName()).isEqualTo("Pasta Recipes");
  }

  @Test
  @DisplayName("Should search collections with visibility filter")
  @Tag("standard-processing")
  void shouldSearchCollectionsWithVisibilityFilter() {
    // Given
    SearchCollectionsRequest request =
        SearchCollectionsRequest.builder()
            .visibility(List.of(CollectionVisibility.PUBLIC, CollectionVisibility.FRIENDS_ONLY))
            .build();

    Pageable pageable = PageRequest.of(0, 20);
    Page<RecipeCollection> collectionPage = new PageImpl<>(List.of());

    when(recipeCollectionRepository.searchCollections(
            isNull(),
            eq(new String[] {"PUBLIC", "FRIENDS_ONLY"}),
            nullOrEmpty(),
            isNull(),
            isNull(),
            isNull(),
            eq(pageable)))
        .thenReturn(collectionPage);

    // When
    ResponseEntity<Page<CollectionDto>> response =
        collectionService.searchCollections(request, pageable);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    verify(recipeCollectionRepository)
        .searchCollections(
            isNull(),
            argThat(
                array ->
                    array != null
                        && array.length == 2
                        && array[0].equals("PUBLIC")
                        && array[1].equals("FRIENDS_ONLY")),
            nullOrEmpty(),
            isNull(),
            isNull(),
            isNull(),
            eq(pageable));
  }

  @Test
  @DisplayName("Should search collections with collaboration mode filter")
  @Tag("standard-processing")
  void shouldSearchCollectionsWithCollaborationModeFilter() {
    // Given
    SearchCollectionsRequest request =
        SearchCollectionsRequest.builder()
            .collaborationMode(List.of(CollaborationMode.ALL_USERS))
            .build();

    Pageable pageable = PageRequest.of(0, 20);
    Page<RecipeCollection> collectionPage = new PageImpl<>(List.of());

    when(recipeCollectionRepository.searchCollections(
            isNull(),
            nullOrEmpty(),
            eq(new String[] {"ALL_USERS"}),
            isNull(),
            isNull(),
            isNull(),
            eq(pageable)))
        .thenReturn(collectionPage);

    // When
    ResponseEntity<Page<CollectionDto>> response =
        collectionService.searchCollections(request, pageable);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    verify(recipeCollectionRepository)
        .searchCollections(
            isNull(),
            nullOrEmpty(),
            argThat(array -> array != null && array.length == 1 && array[0].equals("ALL_USERS")),
            isNull(),
            isNull(),
            isNull(),
            eq(pageable));
  }

  @Test
  @DisplayName("Should search collections with owner filter")
  @Tag("standard-processing")
  void shouldSearchCollectionsWithOwnerFilter() {
    // Given
    UUID ownerUuid = UUID.randomUUID();
    SearchCollectionsRequest request =
        SearchCollectionsRequest.builder().ownerUserId(ownerUuid).build();

    Pageable pageable = PageRequest.of(0, 20);
    Page<RecipeCollection> collectionPage = new PageImpl<>(List.of());

    when(recipeCollectionRepository.searchCollections(
            isNull(), nullOrEmpty(), nullOrEmpty(), eq(ownerUuid), isNull(), isNull(), eq(pageable)))
        .thenReturn(collectionPage);

    // When
    ResponseEntity<Page<CollectionDto>> response =
        collectionService.searchCollections(request, pageable);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    verify(recipeCollectionRepository)
        .searchCollections(
            isNull(), nullOrEmpty(), nullOrEmpty(), eq(ownerUuid), isNull(), isNull(), eq(pageable));
  }

  @Test
  @DisplayName("Should search collections with recipe count range")
  @Tag("standard-processing")
  void shouldSearchCollectionsWithRecipeCountRange() {
    // Given
    SearchCollectionsRequest request =
        SearchCollectionsRequest.builder().minRecipeCount(5).maxRecipeCount(20).build();

    Pageable pageable = PageRequest.of(0, 20);
    Page<RecipeCollection> collectionPage = new PageImpl<>(List.of());

    when(recipeCollectionRepository.searchCollections(
            isNull(), nullOrEmpty(), nullOrEmpty(), isNull(), eq(5), eq(20), eq(pageable)))
        .thenReturn(collectionPage);

    // When
    ResponseEntity<Page<CollectionDto>> response =
        collectionService.searchCollections(request, pageable);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    verify(recipeCollectionRepository)
        .searchCollections(isNull(), nullOrEmpty(), nullOrEmpty(), isNull(), eq(5), eq(20), eq(pageable));
  }

  @Test
  @DisplayName("Should search collections with multiple filters")
  @Tag("standard-processing")
  void shouldSearchCollectionsWithMultipleFilters() {
    // Given
    UUID ownerUuid = UUID.randomUUID();
    SearchCollectionsRequest request =
        SearchCollectionsRequest.builder()
            .query("italian")
            .visibility(List.of(CollectionVisibility.PUBLIC))
            .collaborationMode(List.of(CollaborationMode.ALL_USERS))
            .ownerUserId(ownerUuid)
            .minRecipeCount(3)
            .maxRecipeCount(15)
            .build();

    Pageable pageable = PageRequest.of(0, 10);

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(5L)
            .userId(ownerUuid)
            .name("Italian Recipes")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.ALL_USERS)
            .build();

    Page<RecipeCollection> collectionPage = new PageImpl<>(List.of(collection));

    when(recipeCollectionRepository.searchCollections(
            eq("italian"),
            eq(new String[] {"PUBLIC"}),
            eq(new String[] {"ALL_USERS"}),
            eq(ownerUuid),
            eq(3),
            eq(15),
            eq(pageable)))
        .thenReturn(collectionPage);

    when(collectionMapper.toDto(any(RecipeCollection.class)))
        .thenReturn(CollectionDto.builder().collectionId(5L).name("Italian Recipes").build());

    // When
    ResponseEntity<Page<CollectionDto>> response =
        collectionService.searchCollections(request, pageable);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(1);
    verify(recipeCollectionRepository)
        .searchCollections(
            eq("italian"),
            argThat(array -> array != null && array[0].equals("PUBLIC")),
            argThat(array -> array != null && array[0].equals("ALL_USERS")),
            eq(ownerUuid),
            eq(3),
            eq(15),
            eq(pageable));
  }

  @Test
  @DisplayName("Should search collections with null filters")
  @Tag("standard-processing")
  void shouldSearchCollectionsWithNullFilters() {
    // Given
    SearchCollectionsRequest request = SearchCollectionsRequest.builder().build();

    Pageable pageable = PageRequest.of(0, 20);
    Page<RecipeCollection> collectionPage = new PageImpl<>(List.of());

    when(recipeCollectionRepository.searchCollections(
            isNull(), nullOrEmpty(), nullOrEmpty(), isNull(), isNull(), isNull(), eq(pageable)))
        .thenReturn(collectionPage);

    // When
    ResponseEntity<Page<CollectionDto>> response =
        collectionService.searchCollections(request, pageable);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    verify(recipeCollectionRepository)
        .searchCollections(
            isNull(), nullOrEmpty(), nullOrEmpty(), isNull(), isNull(), isNull(), eq(pageable));
  }

  @Test
  @DisplayName("Should handle empty search results")
  @Tag("standard-processing")
  void shouldHandleEmptySearchResults() {
    // Given
    SearchCollectionsRequest request =
        SearchCollectionsRequest.builder().query("nonexistent").build();

    Pageable pageable = PageRequest.of(0, 20);
    Page<RecipeCollection> emptyPage = new PageImpl<>(List.of());

    when(recipeCollectionRepository.searchCollections(
            eq("nonexistent"), nullOrEmpty(), nullOrEmpty(), isNull(), isNull(), isNull(), eq(pageable)))
        .thenReturn(emptyPage);

    // When
    ResponseEntity<Page<CollectionDto>> response =
        collectionService.searchCollections(request, pageable);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getContent()).isEmpty();
    assertThat(response.getBody().getTotalElements()).isEqualTo(0);
  }

  @Test
  @DisplayName("Should apply pagination correctly in search")
  @Tag("standard-processing")
  void shouldApplyPaginationCorrectlyInSearch() {
    // Given
    SearchCollectionsRequest request = SearchCollectionsRequest.builder().build();

    Pageable pageable = PageRequest.of(1, 5, Sort.by("name").ascending());

    List<RecipeCollection> collections =
        List.of(
            RecipeCollection.builder()
                .collectionId(1L)
                .userId(testUserId)
                .name("Collection 1")
                .build(),
            RecipeCollection.builder()
                .collectionId(2L)
                .userId(testUserId)
                .name("Collection 2")
                .build());

    Page<RecipeCollection> collectionPage = new PageImpl<>(collections, pageable, 10);

    when(recipeCollectionRepository.searchCollections(
            isNull(), nullOrEmpty(), nullOrEmpty(), isNull(), isNull(), isNull(), eq(pageable)))
        .thenReturn(collectionPage);

    when(collectionMapper.toDto(any(RecipeCollection.class)))
        .thenAnswer(
            invocation -> {
              RecipeCollection entity = invocation.getArgument(0);
              return CollectionDto.builder()
                  .collectionId(entity.getCollectionId())
                  .name(entity.getName())
                  .build();
            });

    // When
    ResponseEntity<Page<CollectionDto>> response =
        collectionService.searchCollections(request, pageable);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getNumber()).isEqualTo(1);
    assertThat(response.getBody().getSize()).isEqualTo(5);
    assertThat(response.getBody().getTotalElements()).isEqualTo(10);
    assertThat(response.getBody().getTotalPages()).isEqualTo(2);
    assertThat(response.getBody().getContent()).hasSize(2);
  }
}
