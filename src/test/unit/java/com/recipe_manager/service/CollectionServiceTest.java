package com.recipe_manager.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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

import com.recipe_manager.exception.DuplicateResourceException;
import com.recipe_manager.exception.ResourceNotFoundException;
import com.recipe_manager.model.dto.collection.CollectionCollaboratorDto;
import com.recipe_manager.model.dto.collection.CollectionRecipeDto;
import com.recipe_manager.model.dto.collection.RecipeCollectionItemDto;
import com.recipe_manager.model.dto.request.CreateCollectionRequest;
import com.recipe_manager.model.dto.request.SearchCollectionsRequest;
import com.recipe_manager.model.dto.request.UpdateCollectionRequest;
import com.recipe_manager.model.dto.response.CollectionDetailsDto;
import com.recipe_manager.model.dto.response.CollectionDto;
import com.recipe_manager.model.entity.collection.CollectionCollaborator;
import com.recipe_manager.model.entity.collection.RecipeCollection;
import com.recipe_manager.model.entity.collection.RecipeCollectionItem;
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.entity.collection.RecipeCollectionItemId;
import com.recipe_manager.model.enums.CollaborationMode;
import com.recipe_manager.model.enums.CollectionVisibility;
import com.recipe_manager.model.mapper.CollectionMapper;
import com.recipe_manager.model.mapper.RecipeCollectionItemMapper;
import com.recipe_manager.model.mapper.RecipeCollectionMapper;
import com.recipe_manager.repository.collection.CollectionCollaboratorRepository;
import com.recipe_manager.repository.collection.CollectionSummaryProjection;
import com.recipe_manager.repository.collection.RecipeCollectionItemRepository;
import com.recipe_manager.repository.collection.RecipeCollectionRepository;
import com.recipe_manager.repository.recipe.RecipeRepository;
import com.recipe_manager.service.external.notificationservice.NotificationService;
import com.recipe_manager.util.SecurityUtils;

/** Unit tests for CollectionService. */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
class CollectionServiceTest {

  @Mock private RecipeCollectionRepository recipeCollectionRepository;

  @Mock private RecipeCollectionItemRepository recipeCollectionItemRepository;

  @Mock private CollectionCollaboratorRepository collectionCollaboratorRepository;

  @Mock private CollectionMapper collectionMapper;

  @Mock private RecipeCollectionMapper recipeCollectionMapper;

  @Mock private RecipeCollectionItemMapper recipeCollectionItemMapper;

  @Mock private RecipeRepository recipeRepository;

  @Mock private NotificationService notificationService;

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
            recipeCollectionRepository,
            recipeCollectionItemRepository,
            collectionCollaboratorRepository,
            collectionMapper,
            recipeCollectionMapper,
            recipeCollectionItemMapper,
            recipeRepository,
            notificationService);
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

    CollectionDetailsDto expectedDto = createTestDetailsDto(1L);

    when(recipeCollectionMapper.fromRequest(request)).thenReturn(entityToSave);
    when(recipeCollectionRepository.save(any(RecipeCollection.class))).thenReturn(savedEntity);
    when(recipeCollectionRepository.findByIdWithItems(1L)).thenReturn(Optional.of(savedEntity));
    when(collectionMapper.toDetailsDto(savedEntity)).thenReturn(expectedDto);

    // When
    ResponseEntity<CollectionDetailsDto> response;
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
    verify(recipeCollectionRepository).findByIdWithItems(1L);
    verify(collectionMapper).toDetailsDto(savedEntity);
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
    when(recipeCollectionRepository.findByIdWithItems(1L)).thenReturn(Optional.of(savedEntity));
    when(collectionMapper.toDetailsDto(any(RecipeCollection.class)))
        .thenReturn(createTestDetailsDto(1L));

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
    when(recipeCollectionRepository.findByIdWithItems(1L)).thenReturn(Optional.of(savedEntity));
    when(collectionMapper.toDetailsDto(any(RecipeCollection.class)))
        .thenReturn(createTestDetailsDto(1L));

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
    when(recipeCollectionRepository.findByIdWithItems(1L)).thenReturn(Optional.of(savedEntity));
    when(collectionMapper.toDetailsDto(any(RecipeCollection.class)))
        .thenReturn(createTestDetailsDto(1L));

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
  void shouldMapSavedEntityToDetailsDto() {
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
    when(recipeCollectionRepository.findByIdWithItems(1L)).thenReturn(Optional.of(savedEntity));
    when(collectionMapper.toDetailsDto(savedEntity)).thenReturn(createTestDetailsDto(1L));

    // When
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
      collectionService.createCollection(request);
    }

    // Then
    verify(collectionMapper).toDetailsDto(savedEntity);
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
    when(recipeCollectionRepository.findByIdWithItems(1L)).thenReturn(Optional.of(savedEntity));
    when(collectionMapper.toDetailsDto(any(RecipeCollection.class)))
        .thenReturn(createTestDetailsDto(1L));

    // When
    ResponseEntity<CollectionDetailsDto> response;
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
      response = collectionService.createCollection(request);
    }

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getStatusCode().value()).isEqualTo(201);
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

    CollectionDetailsDto expectedDto =
        CollectionDetailsDto.builder()
            .collectionId(1L)
            .visibility(CollectionVisibility.PRIVATE)
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .recipes(Collections.emptyList())
            .collaborators(Collections.emptyList())
            .build();

    when(recipeCollectionMapper.fromRequest(any())).thenReturn(entityToSave);
    when(recipeCollectionRepository.save(any())).thenReturn(savedEntity);
    when(recipeCollectionRepository.findByIdWithItems(1L)).thenReturn(Optional.of(savedEntity));
    when(collectionMapper.toDetailsDto(savedEntity)).thenReturn(expectedDto);

    // When
    ResponseEntity<CollectionDetailsDto> response;
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

    CollectionDetailsDto expectedDto = createTestDetailsDto(1L);

    when(recipeCollectionMapper.fromRequest(any())).thenReturn(entityToSave);
    when(recipeCollectionRepository.save(any())).thenReturn(savedEntity);
    when(recipeCollectionRepository.findByIdWithItems(1L)).thenReturn(Optional.of(savedEntity));
    when(collectionMapper.toDetailsDto(any(RecipeCollection.class))).thenReturn(expectedDto);

    // When
    ResponseEntity<CollectionDetailsDto> response;
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
            .recipes(Collections.emptyList())
            .collaborators(Collections.emptyList())
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
            .recipes(Arrays.asList(recipe1, recipe2))
            .collaborators(Collections.emptyList())
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
        .recipes(Arrays.asList(recipeDto))
        .collaborators(Collections.emptyList())
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

  @Test
  @DisplayName("Should add recipe successfully to empty collection with displayOrder 10")
  @Tag("standard-processing")
  void shouldAddRecipeToEmptyCollectionSuccessfully() {
    // Given
    Long collectionId = 1L;
    Long recipeId = 100L;

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(testUserId)
            .name("My Collection")
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    RecipeCollectionItemId itemId =
        RecipeCollectionItemId.builder().collectionId(collectionId).recipeId(recipeId).build();

    RecipeCollectionItem savedItem =
        RecipeCollectionItem.builder()
            .id(itemId)
            .displayOrder(10)
            .addedBy(testUserId)
            .addedAt(LocalDateTime.now())
            .build();

    RecipeCollectionItemDto expectedDto =
        RecipeCollectionItemDto.builder()
            .collectionId(collectionId)
            .recipeId(recipeId)
            .displayOrder(10)
            .addedBy(testUserId)
            .build();

    Recipe recipe = Recipe.builder()
        .recipeId(recipeId)
        .userId(UUID.randomUUID())
        .build();

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));
    when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
    when(recipeCollectionItemRepository.existsByIdCollectionIdAndIdRecipeId(collectionId, recipeId))
        .thenReturn(false);
    when(recipeCollectionItemRepository.findMaxDisplayOrderByCollectionId(collectionId))
        .thenReturn(null); // Empty collection
    when(recipeCollectionItemRepository.save(any(RecipeCollectionItem.class)))
        .thenReturn(savedItem);
    when(recipeCollectionItemMapper.toDto(savedItem)).thenReturn(expectedDto);

    // When
    ResponseEntity<RecipeCollectionItemDto> response;
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
      response = collectionService.addRecipeToCollection(collectionId, recipeId);
    }

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getDisplayOrder()).isEqualTo(10);
    verify(recipeCollectionRepository).findById(collectionId);
    verify(recipeCollectionItemRepository).save(any(RecipeCollectionItem.class));
  }

  @Test
  @DisplayName("Should add recipe with correct displayOrder when collection has recipes")
  @Tag("standard-processing")
  void shouldAddRecipeWithCorrectDisplayOrder() {
    // Given
    Long collectionId = 2L;
    Long recipeId = 200L;

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(testUserId)
            .name("My Collection")
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    RecipeCollectionItemId itemId =
        RecipeCollectionItemId.builder().collectionId(collectionId).recipeId(recipeId).build();

    RecipeCollectionItem savedItem =
        RecipeCollectionItem.builder()
            .id(itemId)
            .displayOrder(40) // max (30) + 10
            .addedBy(testUserId)
            .addedAt(LocalDateTime.now())
            .build();

    RecipeCollectionItemDto expectedDto =
        RecipeCollectionItemDto.builder()
            .collectionId(collectionId)
            .recipeId(recipeId)
            .displayOrder(40)
            .addedBy(testUserId)
            .build();

    Recipe recipe = Recipe.builder()
        .recipeId(recipeId)
        .userId(UUID.randomUUID())
        .build();

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));
    when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
    when(recipeCollectionItemRepository.existsByIdCollectionIdAndIdRecipeId(collectionId, recipeId))
        .thenReturn(false);
    when(recipeCollectionItemRepository.findMaxDisplayOrderByCollectionId(collectionId))
        .thenReturn(30); // Collection has items with max displayOrder = 30
    when(recipeCollectionItemRepository.save(any(RecipeCollectionItem.class)))
        .thenReturn(savedItem);
    when(recipeCollectionItemMapper.toDto(savedItem)).thenReturn(expectedDto);

    // When
    ResponseEntity<RecipeCollectionItemDto> response;
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
      response = collectionService.addRecipeToCollection(collectionId, recipeId);
    }

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getDisplayOrder()).isEqualTo(40);
    verify(recipeCollectionItemRepository).findMaxDisplayOrderByCollectionId(collectionId);
  }

  @Test
  @DisplayName("Should throw ResourceNotFoundException when collection not found")
  @Tag("error-handling")
  void shouldThrowResourceNotFoundExceptionWhenAddingRecipeToNonExistentCollection() {
    // Given
    Long nonExistentCollectionId = 999L;
    Long recipeId = 100L;

    when(recipeCollectionRepository.findById(nonExistentCollectionId))
        .thenReturn(Optional.empty());

    // When/Then
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      assertThatThrownBy(
              () -> collectionService.addRecipeToCollection(nonExistentCollectionId, recipeId))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessage("Collection not found");
    }

    verify(recipeCollectionRepository).findById(nonExistentCollectionId);
    verify(recipeCollectionItemRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should throw AccessDeniedException when user lacks edit permission")
  @Tag("error-handling")
  void shouldThrowAccessDeniedExceptionWhenUserLacksEditPermission() {
    // Given
    Long collectionId = 3L;
    Long recipeId = 300L;
    UUID otherUserId = UUID.randomUUID();

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(otherUserId) // Different owner
            .name("Someone else's collection")
            .collaborationMode(CollaborationMode.OWNER_ONLY) // Only owner can edit
            .build();

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));

    // When/Then
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      assertThatThrownBy(() -> collectionService.addRecipeToCollection(collectionId, recipeId))
          .isInstanceOf(AccessDeniedException.class)
          .hasMessage("User doesn't have edit permission for this collection");
    }

    verify(recipeCollectionRepository).findById(collectionId);
    verify(recipeCollectionItemRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should throw DuplicateResourceException when recipe already in collection")
  @Tag("error-handling")
  void shouldThrowDuplicateResourceExceptionWhenRecipeAlreadyInCollection() {
    // Given
    Long collectionId = 4L;
    Long recipeId = 400L;

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(testUserId)
            .name("My Collection")
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));
    when(recipeCollectionItemRepository.existsByIdCollectionIdAndIdRecipeId(collectionId, recipeId))
        .thenReturn(true); // Recipe already exists

    // When/Then
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      assertThatThrownBy(() -> collectionService.addRecipeToCollection(collectionId, recipeId))
          .isInstanceOf(DuplicateResourceException.class)
          .hasMessage("Recipe is already in this collection");
    }

    verify(recipeCollectionRepository).findById(collectionId);
    verify(recipeCollectionItemRepository)
        .existsByIdCollectionIdAndIdRecipeId(collectionId, recipeId);
    verify(recipeCollectionItemRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should allow owner to add recipes in OWNER_ONLY mode")
  @Tag("standard-processing")
  void shouldAllowOwnerToAddRecipesInOwnerOnlyMode() {
    // Given
    Long collectionId = 5L;
    Long recipeId = 500L;

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(testUserId) // User is the owner
            .name("Owner Only Collection")
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    RecipeCollectionItemId itemId =
        RecipeCollectionItemId.builder().collectionId(collectionId).recipeId(recipeId).build();

    RecipeCollectionItem savedItem =
        RecipeCollectionItem.builder()
            .id(itemId)
            .displayOrder(10)
            .addedBy(testUserId)
            .build();

    RecipeCollectionItemDto expectedDto =
        RecipeCollectionItemDto.builder()
            .collectionId(collectionId)
            .recipeId(recipeId)
            .displayOrder(10)
            .build();

    Recipe recipe = Recipe.builder()
        .recipeId(recipeId)
        .userId(UUID.randomUUID())
        .build();

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));
    when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
    when(recipeCollectionItemRepository.existsByIdCollectionIdAndIdRecipeId(collectionId, recipeId))
        .thenReturn(false);
    when(recipeCollectionItemRepository.findMaxDisplayOrderByCollectionId(collectionId))
        .thenReturn(null);
    when(recipeCollectionItemRepository.save(any(RecipeCollectionItem.class)))
        .thenReturn(savedItem);
    when(recipeCollectionItemMapper.toDto(savedItem)).thenReturn(expectedDto);

    // When
    ResponseEntity<RecipeCollectionItemDto> response;
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
      response = collectionService.addRecipeToCollection(collectionId, recipeId);
    }

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  @DisplayName("Should allow any user to add recipes in ALL_USERS mode")
  @Tag("standard-processing")
  void shouldAllowAnyUserToAddRecipesInAllUsersMode() {
    // Given
    Long collectionId = 6L;
    Long recipeId = 600L;
    UUID otherUserId = UUID.randomUUID();

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(otherUserId) // Different owner
            .name("Public Collaboration Collection")
            .collaborationMode(CollaborationMode.ALL_USERS) // Any user can edit
            .build();

    RecipeCollectionItemId itemId =
        RecipeCollectionItemId.builder().collectionId(collectionId).recipeId(recipeId).build();

    RecipeCollectionItem savedItem =
        RecipeCollectionItem.builder()
            .id(itemId)
            .displayOrder(10)
            .addedBy(testUserId)
            .build();

    RecipeCollectionItemDto expectedDto =
        RecipeCollectionItemDto.builder()
            .collectionId(collectionId)
            .recipeId(recipeId)
            .displayOrder(10)
            .build();

    Recipe recipe = Recipe.builder()
        .recipeId(recipeId)
        .userId(UUID.randomUUID())
        .build();

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));
    when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
    when(recipeCollectionItemRepository.existsByIdCollectionIdAndIdRecipeId(collectionId, recipeId))
        .thenReturn(false);
    when(recipeCollectionItemRepository.findMaxDisplayOrderByCollectionId(collectionId))
        .thenReturn(null);
    when(recipeCollectionItemRepository.save(any(RecipeCollectionItem.class)))
        .thenReturn(savedItem);
    when(recipeCollectionItemMapper.toDto(savedItem)).thenReturn(expectedDto);

    // When
    ResponseEntity<RecipeCollectionItemDto> response;
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
      response = collectionService.addRecipeToCollection(collectionId, recipeId);
    }

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  @DisplayName("Should allow collaborators to add recipes in SPECIFIC_USERS mode")
  @Tag("standard-processing")
  void shouldAllowCollaboratorsToAddRecipesInSpecificUsersMode() {
    // Given
    Long collectionId = 7L;
    Long recipeId = 700L;
    UUID otherUserId = UUID.randomUUID();

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(otherUserId) // Different owner
            .name("Collaborator Collection")
            .collaborationMode(CollaborationMode.SPECIFIC_USERS) // Only specific users can edit
            .build();

    RecipeCollectionItemId itemId =
        RecipeCollectionItemId.builder().collectionId(collectionId).recipeId(recipeId).build();

    RecipeCollectionItem savedItem =
        RecipeCollectionItem.builder()
            .id(itemId)
            .displayOrder(10)
            .addedBy(testUserId)
            .build();

    RecipeCollectionItemDto expectedDto =
        RecipeCollectionItemDto.builder()
            .collectionId(collectionId)
            .recipeId(recipeId)
            .displayOrder(10)
            .build();

    Recipe recipe = Recipe.builder()
        .recipeId(recipeId)
        .userId(UUID.randomUUID())
        .build();

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));
    when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
    when(collectionCollaboratorRepository.existsByIdCollectionIdAndIdUserId(collectionId, testUserId))
        .thenReturn(true); // User is a collaborator
    when(recipeCollectionItemRepository.existsByIdCollectionIdAndIdRecipeId(collectionId, recipeId))
        .thenReturn(false);
    when(recipeCollectionItemRepository.findMaxDisplayOrderByCollectionId(collectionId))
        .thenReturn(null);
    when(recipeCollectionItemRepository.save(any(RecipeCollectionItem.class)))
        .thenReturn(savedItem);
    when(recipeCollectionItemMapper.toDto(savedItem)).thenReturn(expectedDto);

    // When
    ResponseEntity<RecipeCollectionItemDto> response;
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
      response = collectionService.addRecipeToCollection(collectionId, recipeId);
    }

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    verify(collectionCollaboratorRepository)
        .existsByIdCollectionIdAndIdUserId(collectionId, testUserId);
  }

  @Test
  @DisplayName("Should deny non-collaborators in SPECIFIC_USERS mode")
  @Tag("error-handling")
  void shouldDenyNonCollaboratorsInSpecificUsersMode() {
    // Given
    Long collectionId = 8L;
    Long recipeId = 800L;
    UUID otherUserId = UUID.randomUUID();

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(otherUserId) // Different owner
            .name("Collaborator Collection")
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .build();

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));
    when(collectionCollaboratorRepository.existsByIdCollectionIdAndIdUserId(collectionId, testUserId))
        .thenReturn(false); // User is NOT a collaborator

    // When/Then
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      assertThatThrownBy(() -> collectionService.addRecipeToCollection(collectionId, recipeId))
          .isInstanceOf(AccessDeniedException.class)
          .hasMessage("User doesn't have edit permission for this collection");
    }

    verify(recipeCollectionRepository).findById(collectionId);
    verify(collectionCollaboratorRepository)
        .existsByIdCollectionIdAndIdUserId(collectionId, testUserId);
    verify(recipeCollectionItemRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should remove recipe when user is owner")
  @Tag("standard-processing")
  void shouldRemoveRecipeWhenUserIsOwner() {
    // Given
    Long collectionId = 10L;
    Long recipeId = 1000L;

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(testUserId) // User is the owner
            .name("My Collection")
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));
    when(recipeCollectionItemRepository.existsByIdCollectionIdAndIdRecipeId(collectionId, recipeId))
        .thenReturn(true);

    // When
    ResponseEntity<Void> response;
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
      response = collectionService.removeRecipeFromCollection(collectionId, recipeId);
    }

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(response.getBody()).isNull();
    verify(recipeCollectionRepository).findById(collectionId);
    verify(recipeCollectionItemRepository)
        .existsByIdCollectionIdAndIdRecipeId(collectionId, recipeId);
    verify(recipeCollectionItemRepository)
        .deleteByIdCollectionIdAndIdRecipeId(collectionId, recipeId);
  }

  @Test
  @DisplayName("Should remove recipe when user has edit permission in ALL_USERS mode")
  @Tag("standard-processing")
  void shouldRemoveRecipeWhenUserHasEditPermissionInAllUsersMode() {
    // Given
    Long collectionId = 11L;
    Long recipeId = 1100L;
    UUID otherUserId = UUID.randomUUID();

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(otherUserId) // Different owner
            .name("Public Collection")
            .collaborationMode(CollaborationMode.ALL_USERS) // Any user can edit
            .build();

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));
    when(recipeCollectionItemRepository.existsByIdCollectionIdAndIdRecipeId(collectionId, recipeId))
        .thenReturn(true);

    // When
    ResponseEntity<Void> response;
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
      response = collectionService.removeRecipeFromCollection(collectionId, recipeId);
    }

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    verify(recipeCollectionItemRepository)
        .deleteByIdCollectionIdAndIdRecipeId(collectionId, recipeId);
  }

  @Test
  @DisplayName("Should throw ResourceNotFoundException when collection not found for remove")
  @Tag("error-handling")
  void shouldThrowResourceNotFoundExceptionWhenRemovingFromNonExistentCollection() {
    // Given
    Long nonExistentCollectionId = 999L;
    Long recipeId = 100L;

    when(recipeCollectionRepository.findById(nonExistentCollectionId))
        .thenReturn(Optional.empty());

    // When/Then
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      assertThatThrownBy(
              () ->
                  collectionService.removeRecipeFromCollection(nonExistentCollectionId, recipeId))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessage("Collection not found");
    }

    verify(recipeCollectionRepository).findById(nonExistentCollectionId);
    verify(recipeCollectionItemRepository, never())
        .deleteByIdCollectionIdAndIdRecipeId(any(), any());
  }

  @Test
  @DisplayName("Should throw AccessDeniedException when user lacks edit permission for remove")
  @Tag("error-handling")
  void shouldThrowAccessDeniedExceptionWhenUserLacksEditPermissionForRemove() {
    // Given
    Long collectionId = 12L;
    Long recipeId = 1200L;
    UUID otherUserId = UUID.randomUUID();

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(otherUserId) // Different owner
            .name("Someone else's collection")
            .collaborationMode(CollaborationMode.OWNER_ONLY) // Only owner can edit
            .build();

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));

    // When/Then
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      assertThatThrownBy(() -> collectionService.removeRecipeFromCollection(collectionId, recipeId))
          .isInstanceOf(AccessDeniedException.class)
          .hasMessage("User doesn't have edit permission for this collection");
    }

    verify(recipeCollectionRepository).findById(collectionId);
    verify(recipeCollectionItemRepository, never())
        .deleteByIdCollectionIdAndIdRecipeId(any(), any());
  }

  @Test
  @DisplayName("Should throw ResourceNotFoundException when recipe not in collection")
  @Tag("error-handling")
  void shouldThrowResourceNotFoundExceptionWhenRecipeNotInCollection() {
    // Given
    Long collectionId = 13L;
    Long recipeId = 1300L;

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(testUserId)
            .name("My Collection")
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));
    when(recipeCollectionItemRepository.existsByIdCollectionIdAndIdRecipeId(collectionId, recipeId))
        .thenReturn(false); // Recipe is not in collection

    // When/Then
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      assertThatThrownBy(() -> collectionService.removeRecipeFromCollection(collectionId, recipeId))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessage("Recipe not found in this collection");
    }

    verify(recipeCollectionRepository).findById(collectionId);
    verify(recipeCollectionItemRepository)
        .existsByIdCollectionIdAndIdRecipeId(collectionId, recipeId);
    verify(recipeCollectionItemRepository, never())
        .deleteByIdCollectionIdAndIdRecipeId(any(), any());
  }

  @Test
  @DisplayName("Should allow collaborator to remove recipe in SPECIFIC_USERS mode")
  @Tag("standard-processing")
  void shouldAllowCollaboratorToRemoveRecipeInSpecificUsersMode() {
    // Given
    Long collectionId = 14L;
    Long recipeId = 1400L;
    UUID otherUserId = UUID.randomUUID();

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(otherUserId) // Different owner
            .name("Collaborator Collection")
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .build();

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));
    when(collectionCollaboratorRepository.existsByIdCollectionIdAndIdUserId(collectionId, testUserId))
        .thenReturn(true); // User is a collaborator
    when(recipeCollectionItemRepository.existsByIdCollectionIdAndIdRecipeId(collectionId, recipeId))
        .thenReturn(true);

    // When
    ResponseEntity<Void> response;
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
      response = collectionService.removeRecipeFromCollection(collectionId, recipeId);
    }

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    verify(collectionCollaboratorRepository)
        .existsByIdCollectionIdAndIdUserId(collectionId, testUserId);
    verify(recipeCollectionItemRepository)
        .deleteByIdCollectionIdAndIdRecipeId(collectionId, recipeId);
  }

  @Test
  @DisplayName("Should check edit permission before removing recipe")
  @Tag("standard-processing")
  void shouldCheckEditPermissionBeforeRemovingRecipe() {
    // Given
    Long collectionId = 15L;
    Long recipeId = 1500L;

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(testUserId) // User is the owner
            .name("My Collection")
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));
    when(recipeCollectionItemRepository.existsByIdCollectionIdAndIdRecipeId(collectionId, recipeId))
        .thenReturn(true);

    // When
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
      collectionService.removeRecipeFromCollection(collectionId, recipeId);
    }

    // Then - Verify permission check was performed (via findById)
    verify(recipeCollectionRepository).findById(collectionId);
    // Verify recipe existence check was performed
    verify(recipeCollectionItemRepository)
        .existsByIdCollectionIdAndIdRecipeId(collectionId, recipeId);
    // Verify deletion was executed
    verify(recipeCollectionItemRepository)
        .deleteByIdCollectionIdAndIdRecipeId(collectionId, recipeId);
  }

  @Test
  @DisplayName("Should update recipe order when user is owner")
  @Tag("standard-processing")
  void shouldUpdateRecipeOrderWhenUserIsOwner() {
    // Given
    Long collectionId = 50L;
    Long recipeId = 500L;
    Integer newDisplayOrder = 25;

    com.recipe_manager.model.dto.request.UpdateRecipeOrderRequest request =
        com.recipe_manager.model.dto.request.UpdateRecipeOrderRequest.builder()
            .displayOrder(newDisplayOrder)
            .build();

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(testUserId)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    RecipeCollectionItemId itemId =
        RecipeCollectionItemId.builder().collectionId(collectionId).recipeId(recipeId).build();

    RecipeCollectionItem item =
        RecipeCollectionItem.builder()
            .id(itemId)
            .displayOrder(10) // Old display order
            .addedBy(testUserId)
            .addedAt(LocalDateTime.now())
            .build();

    com.recipe_manager.model.entity.recipe.Recipe recipe =
        com.recipe_manager.model.entity.recipe.Recipe.builder()
            .recipeId(recipeId)
            .title("Test Recipe")
            .description("Test Description")
            .userId(testUserId)
            .build();

    RecipeCollectionItem updatedItem =
        RecipeCollectionItem.builder()
            .id(itemId)
            .recipe(recipe)
            .displayOrder(newDisplayOrder)
            .addedBy(testUserId)
            .addedAt(LocalDateTime.now())
            .build();

    CollectionRecipeDto expectedDto =
        CollectionRecipeDto.builder()
            .recipeId(recipeId)
            .recipeTitle("Test Recipe")
            .recipeDescription("Test Description")
            .displayOrder(newDisplayOrder)
            .addedBy(testUserId)
            .addedAt(LocalDateTime.now())
            .build();

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));
    when(recipeCollectionItemRepository.findByIdCollectionIdAndIdRecipeId(collectionId, recipeId))
        .thenReturn(Optional.of(item));
    when(recipeCollectionItemRepository.save(any(RecipeCollectionItem.class))).thenReturn(item);
    when(recipeCollectionItemRepository.findByIdCollectionIdWithRecipe(collectionId))
        .thenReturn(List.of(updatedItem));
    when(collectionMapper.toRecipeDto(updatedItem)).thenReturn(expectedDto);

    // When
    ResponseEntity<CollectionRecipeDto> response;
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
      response = collectionService.updateRecipeOrder(collectionId, recipeId, request);
    }

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getRecipeId()).isEqualTo(recipeId);
    assertThat(response.getBody().getDisplayOrder()).isEqualTo(newDisplayOrder);

    verify(recipeCollectionRepository).findById(collectionId);
    verify(recipeCollectionItemRepository)
        .findByIdCollectionIdAndIdRecipeId(collectionId, recipeId);
    verify(recipeCollectionItemRepository).save(any(RecipeCollectionItem.class));
    verify(recipeCollectionItemRepository).findByIdCollectionIdWithRecipe(collectionId);
    verify(collectionMapper).toRecipeDto(updatedItem);
  }

  @Test
  @DisplayName("Should throw exception when collection not found during order update")
  @Tag("error-handling")
  void shouldThrowExceptionWhenCollectionNotFoundDuringOrderUpdate() {
    // Given
    Long collectionId = 999L;
    Long recipeId = 500L;

    com.recipe_manager.model.dto.request.UpdateRecipeOrderRequest request =
        com.recipe_manager.model.dto.request.UpdateRecipeOrderRequest.builder()
            .displayOrder(15)
            .build();

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.empty());

    // When & Then
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      assertThatThrownBy(
              () -> collectionService.updateRecipeOrder(collectionId, recipeId, request))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessage("Collection not found");
    }

    verify(recipeCollectionRepository).findById(collectionId);
    verify(recipeCollectionItemRepository, never())
        .findByIdCollectionIdAndIdRecipeId(any(), any());
  }

  @Test
  @DisplayName("Should throw exception when recipe not in collection during order update")
  @Tag("error-handling")
  void shouldThrowExceptionWhenRecipeNotInCollectionDuringOrderUpdate() {
    // Given
    Long collectionId = 50L;
    Long recipeId = 999L;

    com.recipe_manager.model.dto.request.UpdateRecipeOrderRequest request =
        com.recipe_manager.model.dto.request.UpdateRecipeOrderRequest.builder()
            .displayOrder(15)
            .build();

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(testUserId)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));
    when(recipeCollectionItemRepository.findByIdCollectionIdAndIdRecipeId(collectionId, recipeId))
        .thenReturn(Optional.empty());

    // When & Then
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      assertThatThrownBy(
              () -> collectionService.updateRecipeOrder(collectionId, recipeId, request))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessage("Recipe not found in this collection");
    }

    verify(recipeCollectionRepository).findById(collectionId);
    verify(recipeCollectionItemRepository)
        .findByIdCollectionIdAndIdRecipeId(collectionId, recipeId);
    verify(recipeCollectionItemRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should throw exception when user lacks edit permission for order update")
  @Tag("error-handling")
  void shouldThrowExceptionWhenUserLacksEditPermissionForOrderUpdate() {
    // Given
    Long collectionId = 50L;
    Long recipeId = 500L;
    UUID otherUserId = UUID.randomUUID();

    com.recipe_manager.model.dto.request.UpdateRecipeOrderRequest request =
        com.recipe_manager.model.dto.request.UpdateRecipeOrderRequest.builder()
            .displayOrder(15)
            .build();

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(otherUserId) // Different owner
            .collaborationMode(CollaborationMode.OWNER_ONLY) // Only owner can edit
            .build();

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));

    // When & Then
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      assertThatThrownBy(
              () -> collectionService.updateRecipeOrder(collectionId, recipeId, request))
          .isInstanceOf(AccessDeniedException.class)
          .hasMessage("User doesn't have edit permission for this collection");
    }

    verify(recipeCollectionRepository).findById(collectionId);
    verify(recipeCollectionItemRepository, never())
        .findByIdCollectionIdAndIdRecipeId(any(), any());
  }

  @Test
  @DisplayName("Should reorder recipes successfully when user is owner")
  @Tag("standard-processing")
  void shouldReorderRecipesWhenUserIsOwner() {
    // Given
    Long collectionId = 100L;
    Long recipeId1 = 1L;
    Long recipeId2 = 2L;

    com.recipe_manager.model.dto.request.ReorderRecipesRequest.RecipeOrder order1 =
        com.recipe_manager.model.dto.request.ReorderRecipesRequest.RecipeOrder.builder()
            .recipeId(recipeId1)
            .displayOrder(20)
            .build();

    com.recipe_manager.model.dto.request.ReorderRecipesRequest.RecipeOrder order2 =
        com.recipe_manager.model.dto.request.ReorderRecipesRequest.RecipeOrder.builder()
            .recipeId(recipeId2)
            .displayOrder(10)
            .build();

    com.recipe_manager.model.dto.request.ReorderRecipesRequest request =
        com.recipe_manager.model.dto.request.ReorderRecipesRequest.builder()
            .recipes(java.util.Arrays.asList(order1, order2))
            .build();

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(testUserId)
            .name("My Collection")
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    RecipeCollectionItemId itemId1 =
        RecipeCollectionItemId.builder().collectionId(collectionId).recipeId(recipeId1).build();
    RecipeCollectionItemId itemId2 =
        RecipeCollectionItemId.builder().collectionId(collectionId).recipeId(recipeId2).build();

    com.recipe_manager.model.entity.recipe.Recipe recipe1 =
        com.recipe_manager.model.entity.recipe.Recipe.builder()
            .recipeId(recipeId1)
            .title("Recipe 1")
            .description("Description 1")
            .build();

    com.recipe_manager.model.entity.recipe.Recipe recipe2 =
        com.recipe_manager.model.entity.recipe.Recipe.builder()
            .recipeId(recipeId2)
            .title("Recipe 2")
            .description("Description 2")
            .build();

    RecipeCollectionItem item1 =
        RecipeCollectionItem.builder()
            .id(itemId1)
            .recipe(recipe1)
            .displayOrder(30)
            .addedBy(testUserId)
            .build();

    RecipeCollectionItem item2 =
        RecipeCollectionItem.builder()
            .id(itemId2)
            .recipe(recipe2)
            .displayOrder(40)
            .addedBy(testUserId)
            .build();

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));
    when(recipeCollectionItemRepository.findByIdCollectionId(collectionId))
        .thenReturn(java.util.Arrays.asList(item1, item2));
    when(recipeCollectionItemRepository.findByIdCollectionIdAndIdRecipeId(collectionId, recipeId1))
        .thenReturn(Optional.of(item1));
    when(recipeCollectionItemRepository.findByIdCollectionIdAndIdRecipeId(collectionId, recipeId2))
        .thenReturn(Optional.of(item2));
    when(recipeCollectionItemRepository.save(any(RecipeCollectionItem.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // Update display orders after save
    item1.setDisplayOrder(20);
    item2.setDisplayOrder(10);

    when(recipeCollectionItemRepository.findByIdCollectionIdWithRecipe(collectionId))
        .thenReturn(java.util.Arrays.asList(item2, item1)); // Sorted by display order

    com.recipe_manager.model.dto.collection.CollectionRecipeDto dto1 =
        com.recipe_manager.model.dto.collection.CollectionRecipeDto.builder()
            .recipeId(recipeId2)
            .recipeTitle("Recipe 2")
            .recipeDescription("Description 2")
            .displayOrder(10)
            .build();

    com.recipe_manager.model.dto.collection.CollectionRecipeDto dto2 =
        com.recipe_manager.model.dto.collection.CollectionRecipeDto.builder()
            .recipeId(recipeId1)
            .recipeTitle("Recipe 1")
            .recipeDescription("Description 1")
            .displayOrder(20)
            .build();

    when(collectionMapper.toRecipeDto(item2)).thenReturn(dto1);
    when(collectionMapper.toRecipeDto(item1)).thenReturn(dto2);

    // When
    ResponseEntity<java.util.List<com.recipe_manager.model.dto.collection.CollectionRecipeDto>>
        response;
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
      response = collectionService.reorderRecipes(collectionId, request);
    }

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).hasSize(2);
    assertThat(response.getBody().get(0).getRecipeId()).isEqualTo(recipeId2);
    assertThat(response.getBody().get(0).getDisplayOrder()).isEqualTo(10);
    assertThat(response.getBody().get(1).getRecipeId()).isEqualTo(recipeId1);
    assertThat(response.getBody().get(1).getDisplayOrder()).isEqualTo(20);

    verify(recipeCollectionRepository).findById(collectionId);
    verify(recipeCollectionItemRepository).findByIdCollectionId(collectionId);
    verify(recipeCollectionItemRepository, times(2)).save(any(RecipeCollectionItem.class));
    verify(recipeCollectionItemRepository).findByIdCollectionIdWithRecipe(collectionId);
  }

  @Test
  @DisplayName("Should throw exception when collection not found during reorder")
  @Tag("error-handling")
  void shouldThrowExceptionWhenCollectionNotFoundDuringReorder() {
    // Given
    Long collectionId = 999L;

    com.recipe_manager.model.dto.request.ReorderRecipesRequest.RecipeOrder order1 =
        com.recipe_manager.model.dto.request.ReorderRecipesRequest.RecipeOrder.builder()
            .recipeId(1L)
            .displayOrder(10)
            .build();

    com.recipe_manager.model.dto.request.ReorderRecipesRequest request =
        com.recipe_manager.model.dto.request.ReorderRecipesRequest.builder()
            .recipes(java.util.Collections.singletonList(order1))
            .build();

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.empty());

    // When/Then
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      assertThatThrownBy(() -> collectionService.reorderRecipes(collectionId, request))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessage("Collection not found");
    }

    verify(recipeCollectionRepository).findById(collectionId);
    verify(recipeCollectionItemRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should throw exception when user lacks edit permission for reorder")
  @Tag("error-handling")
  void shouldThrowExceptionWhenUserLacksEditPermissionForReorder() {
    // Given
    Long collectionId = 200L;
    UUID otherUserId = UUID.randomUUID();

    com.recipe_manager.model.dto.request.ReorderRecipesRequest.RecipeOrder order1 =
        com.recipe_manager.model.dto.request.ReorderRecipesRequest.RecipeOrder.builder()
            .recipeId(1L)
            .displayOrder(10)
            .build();

    com.recipe_manager.model.dto.request.ReorderRecipesRequest request =
        com.recipe_manager.model.dto.request.ReorderRecipesRequest.builder()
            .recipes(java.util.Collections.singletonList(order1))
            .build();

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(otherUserId) // Different owner
            .name("Someone else's collection")
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));

    // When/Then
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      assertThatThrownBy(() -> collectionService.reorderRecipes(collectionId, request))
          .isInstanceOf(AccessDeniedException.class)
          .hasMessage("User doesn't have edit permission for this collection");
    }

    verify(recipeCollectionRepository).findById(collectionId);
    verify(recipeCollectionItemRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should throw exception when request contains duplicate display orders")
  @Tag("error-handling")
  void shouldThrowExceptionWhenRequestContainsDuplicateDisplayOrders() {
    // Given
    Long collectionId = 300L;

    com.recipe_manager.model.dto.request.ReorderRecipesRequest.RecipeOrder order1 =
        com.recipe_manager.model.dto.request.ReorderRecipesRequest.RecipeOrder.builder()
            .recipeId(1L)
            .displayOrder(10)
            .build();

    com.recipe_manager.model.dto.request.ReorderRecipesRequest.RecipeOrder order2 =
        com.recipe_manager.model.dto.request.ReorderRecipesRequest.RecipeOrder.builder()
            .recipeId(2L)
            .displayOrder(10) // Duplicate display order
            .build();

    com.recipe_manager.model.dto.request.ReorderRecipesRequest request =
        com.recipe_manager.model.dto.request.ReorderRecipesRequest.builder()
            .recipes(java.util.Arrays.asList(order1, order2))
            .build();

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(testUserId)
            .name("My Collection")
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));

    // When/Then
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      assertThatThrownBy(() -> collectionService.reorderRecipes(collectionId, request))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Request contains duplicate display orders");
    }

    verify(recipeCollectionRepository).findById(collectionId);
    verify(recipeCollectionItemRepository, never()).findByIdCollectionId(any());
  }

  @Test
  @DisplayName("Should throw exception when recipe not in collection during reorder")
  @Tag("error-handling")
  void shouldThrowExceptionWhenRecipeNotInCollectionDuringReorder() {
    // Given
    Long collectionId = 400L;
    Long existingRecipeId = 1L;
    Long nonExistentRecipeId = 999L;

    com.recipe_manager.model.dto.request.ReorderRecipesRequest.RecipeOrder order1 =
        com.recipe_manager.model.dto.request.ReorderRecipesRequest.RecipeOrder.builder()
            .recipeId(nonExistentRecipeId)
            .displayOrder(10)
            .build();

    com.recipe_manager.model.dto.request.ReorderRecipesRequest request =
        com.recipe_manager.model.dto.request.ReorderRecipesRequest.builder()
            .recipes(java.util.Collections.singletonList(order1))
            .build();

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(testUserId)
            .name("My Collection")
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    RecipeCollectionItemId itemId1 =
        RecipeCollectionItemId.builder()
            .collectionId(collectionId)
            .recipeId(existingRecipeId)
            .build();

    RecipeCollectionItem item1 =
        RecipeCollectionItem.builder().id(itemId1).displayOrder(10).addedBy(testUserId).build();

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));
    when(recipeCollectionItemRepository.findByIdCollectionId(collectionId))
        .thenReturn(java.util.Collections.singletonList(item1));

    // When/Then
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      assertThatThrownBy(() -> collectionService.reorderRecipes(collectionId, request))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessageContaining("not found in this collection");
    }

    verify(recipeCollectionRepository).findById(collectionId);
    verify(recipeCollectionItemRepository).findByIdCollectionId(collectionId);
    verify(recipeCollectionItemRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should get collaborators successfully for collection owner")
  @Tag("standard-processing")
  void shouldGetCollaboratorsSuccessfullyForOwner() {
    // Given
    Long collectionId = 1L;
    UUID collaborator1Id = UUID.randomUUID();
    UUID collaborator2Id = UUID.randomUUID();

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(testUserId)
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .visibility(CollectionVisibility.PRIVATE)
            .build();

    // Mock repository result rows: collection_id, user_id, username, granted_by,
    // granted_by_username, granted_at
    java.sql.Timestamp now = java.sql.Timestamp.valueOf(LocalDateTime.now());
    List<Object[]> collaboratorRows =
        Arrays.asList(
            new Object[] {collectionId, collaborator1Id, "user1", testUserId, "owner", now},
            new Object[] {
                collectionId,
                collaborator2Id,
                "user2",
                testUserId,
                "owner",
                java.sql.Timestamp.valueOf(LocalDateTime.now().minusDays(1))
            });

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));
    when(collectionCollaboratorRepository.findCollaboratorsWithUsernamesByCollectionId(
            collectionId))
        .thenReturn(collaboratorRows);

    // When
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      ResponseEntity<List<CollectionCollaboratorDto>> response =
          collectionService.getCollaborators(collectionId);

      // Then
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody()).hasSize(2);
      assertThat(response.getBody().get(0).getUserId()).isEqualTo(collaborator1Id);
      assertThat(response.getBody().get(0).getUsername()).isEqualTo("user1");
      assertThat(response.getBody().get(0).getGrantedBy()).isEqualTo(testUserId);
      assertThat(response.getBody().get(0).getGrantedByUsername()).isEqualTo("owner");
      assertThat(response.getBody().get(1).getUserId()).isEqualTo(collaborator2Id);
      assertThat(response.getBody().get(1).getUsername()).isEqualTo("user2");
    }

    verify(recipeCollectionRepository).findById(collectionId);
    verify(collectionCollaboratorRepository)
        .findCollaboratorsWithUsernamesByCollectionId(collectionId);
    verify(collectionCollaboratorRepository, never())
        .existsByIdCollectionIdAndIdUserId(any(), any());
  }

  @Test
  @DisplayName("Should throw ResourceNotFoundException when collection not found for getCollaborators")
  @Tag("error-handling")
  void shouldThrowNotFoundWhenCollectionNotFoundForGetCollaborators() {
    // Given
    Long collectionId = 999L;

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.empty());

    // When/Then
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      assertThatThrownBy(() -> collectionService.getCollaborators(collectionId))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessageContaining("Collection not found");
    }

    verify(recipeCollectionRepository).findById(collectionId);
    verify(collectionCollaboratorRepository, never())
        .findCollaboratorsWithUsernamesByCollectionId(any());
  }

  @Test
  @DisplayName(
      "Should throw AccessDeniedException when user lacks view permission for getCollaborators")
  @Tag("error-handling")
  void shouldThrowAccessDeniedWhenNoViewPermissionForGetCollaborators() {
    // Given
    Long collectionId = 1L;
    UUID otherUserId = UUID.randomUUID();

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(otherUserId)
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .visibility(CollectionVisibility.PRIVATE)
            .build();

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));
    when(collectionCollaboratorRepository.existsByIdCollectionIdAndIdUserId(collectionId, testUserId))
        .thenReturn(false);

    // When/Then
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      assertThatThrownBy(() -> collectionService.getCollaborators(collectionId))
          .isInstanceOf(AccessDeniedException.class)
          .hasMessageContaining("User doesn't have view permission");
    }

    verify(recipeCollectionRepository).findById(collectionId);
    verify(collectionCollaboratorRepository)
        .existsByIdCollectionIdAndIdUserId(collectionId, testUserId);
    verify(collectionCollaboratorRepository, never())
        .findCollaboratorsWithUsernamesByCollectionId(any());
  }

  @Test
  @DisplayName(
      "Should throw AccessDeniedException when collection doesn't use SPECIFIC_USERS mode")
  @Tag("error-handling")
  void shouldThrowAccessDeniedWhenNotSpecificUsersMode() {
    // Given
    Long collectionId = 1L;

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(testUserId)
            .collaborationMode(CollaborationMode.ALL_USERS)
            .visibility(CollectionVisibility.PUBLIC)
            .build();

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));

    // When/Then
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      assertThatThrownBy(() -> collectionService.getCollaborators(collectionId))
          .isInstanceOf(AccessDeniedException.class)
          .hasMessageContaining("doesn't use SPECIFIC_USERS collaboration mode");
    }

    verify(recipeCollectionRepository).findById(collectionId);
    verify(collectionCollaboratorRepository, never())
        .findCollaboratorsWithUsernamesByCollectionId(any());
  }

  @Test
  @DisplayName("Should return empty list when collection has no collaborators")
  @Tag("standard-processing")
  void shouldReturnEmptyListWhenNoCollaborators() {
    // Given
    Long collectionId = 1L;

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(testUserId)
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .visibility(CollectionVisibility.PRIVATE)
            .build();

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));
    when(collectionCollaboratorRepository.findCollaboratorsWithUsernamesByCollectionId(
            collectionId))
        .thenReturn(Collections.emptyList());

    // When
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      ResponseEntity<List<CollectionCollaboratorDto>> response =
          collectionService.getCollaborators(collectionId);

      // Then
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody()).isEmpty();
    }

    verify(recipeCollectionRepository).findById(collectionId);
    verify(collectionCollaboratorRepository)
        .findCollaboratorsWithUsernamesByCollectionId(collectionId);
  }

  @Test
  @DisplayName("Should allow collaborator to view collaborators list")
  @Tag("standard-processing")
  void shouldAllowCollaboratorToViewCollaboratorsList() {
    // Given
    Long collectionId = 1L;
    UUID ownerId = UUID.randomUUID();
    UUID collaboratorId = UUID.randomUUID();

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(ownerId)
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .visibility(CollectionVisibility.PRIVATE)
            .build();

    java.sql.Timestamp now = java.sql.Timestamp.valueOf(LocalDateTime.now());
    List<Object[]> collaboratorRows =
        List.<Object[]>of(
            new Object[] {collectionId, collaboratorId, "collaborator1", ownerId, "owner", now});

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));
    when(collectionCollaboratorRepository.existsByIdCollectionIdAndIdUserId(
            collectionId, testUserId))
        .thenReturn(true);
    when(collectionCollaboratorRepository.findCollaboratorsWithUsernamesByCollectionId(
            collectionId))
        .thenReturn(collaboratorRows);

    // When
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      ResponseEntity<List<CollectionCollaboratorDto>> response =
          collectionService.getCollaborators(collectionId);

      // Then
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody()).hasSize(1);
    }

    verify(recipeCollectionRepository).findById(collectionId);
    verify(collectionCollaboratorRepository)
        .existsByIdCollectionIdAndIdUserId(collectionId, testUserId);
    verify(collectionCollaboratorRepository)
        .findCollaboratorsWithUsernamesByCollectionId(collectionId);
  }

  @Test
  @DisplayName("addCollaborator - success")
  @Tag("standard-processing")
  void addCollaboratorSuccess() {
    // Given
    Long collectionId = 1L;
    UUID ownerId = UUID.randomUUID();
    UUID collaboratorId = UUID.randomUUID();

    com.recipe_manager.model.dto.request.AddCollaboratorRequest request =
        com.recipe_manager.model.dto.request.AddCollaboratorRequest.builder()
            .userId(collaboratorId)
            .build();

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(ownerId)
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .visibility(CollectionVisibility.PRIVATE)
            .build();

    java.sql.Timestamp now = java.sql.Timestamp.valueOf(LocalDateTime.now());
    List<Object[]> collaboratorRows =
        List.<Object[]>of(
            new Object[] {
              collectionId, collaboratorId, "collaborator1", ownerId, "owner", now
            });

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));
    when(collectionCollaboratorRepository.existsByIdCollectionIdAndIdUserId(
            collectionId, collaboratorId))
        .thenReturn(false);
    when(collectionCollaboratorRepository.findCollaboratorsWithUsernamesByCollectionId(
            collectionId))
        .thenReturn(collaboratorRows);

    // When
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(ownerId);

      ResponseEntity<CollectionCollaboratorDto> response =
          collectionService.addCollaborator(collectionId, request);

      // Then
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody().getUserId()).isEqualTo(collaboratorId);
      assertThat(response.getBody().getGrantedBy()).isEqualTo(ownerId);
    }

    verify(recipeCollectionRepository).findById(collectionId);
    verify(collectionCollaboratorRepository)
        .existsByIdCollectionIdAndIdUserId(collectionId, collaboratorId);
    verify(collectionCollaboratorRepository).save(any(CollectionCollaborator.class));
  }

  @Test
  @DisplayName("addCollaborator - collection not found")
  @Tag("error-handling")
  void addCollaboratorCollectionNotFound() {
    // Given
    Long collectionId = 999L;
    UUID collaboratorId = UUID.randomUUID();

    com.recipe_manager.model.dto.request.AddCollaboratorRequest request =
        com.recipe_manager.model.dto.request.AddCollaboratorRequest.builder()
            .userId(collaboratorId)
            .build();

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.empty());

    // When/Then
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      assertThatThrownBy(() -> collectionService.addCollaborator(collectionId, request))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessage("Collection not found");
    }

    verify(recipeCollectionRepository).findById(collectionId);
    verify(collectionCollaboratorRepository, never()).save(any());
  }

  @Test
  @DisplayName("addCollaborator - user is not owner")
  @Tag("error-handling")
  void addCollaboratorUserIsNotOwner() {
    // Given
    Long collectionId = 1L;
    UUID ownerId = UUID.randomUUID();
    UUID nonOwnerId = UUID.randomUUID();
    UUID collaboratorId = UUID.randomUUID();

    com.recipe_manager.model.dto.request.AddCollaboratorRequest request =
        com.recipe_manager.model.dto.request.AddCollaboratorRequest.builder()
            .userId(collaboratorId)
            .build();

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(ownerId)
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .build();

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));

    // When/Then
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(nonOwnerId);

      assertThatThrownBy(() -> collectionService.addCollaborator(collectionId, request))
          .isInstanceOf(AccessDeniedException.class)
          .hasMessage("Only the collection owner can add collaborators");
    }

    verify(recipeCollectionRepository).findById(collectionId);
    verify(collectionCollaboratorRepository, never()).save(any());
  }

  @Test
  @DisplayName("addCollaborator - wrong collaboration mode")
  @Tag("error-handling")
  void addCollaboratorWrongCollaborationMode() {
    // Given
    Long collectionId = 1L;
    UUID ownerId = UUID.randomUUID();
    UUID collaboratorId = UUID.randomUUID();

    com.recipe_manager.model.dto.request.AddCollaboratorRequest request =
        com.recipe_manager.model.dto.request.AddCollaboratorRequest.builder()
            .userId(collaboratorId)
            .build();

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(ownerId)
            .collaborationMode(CollaborationMode.OWNER_ONLY) // Wrong mode
            .build();

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));

    // When/Then
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(ownerId);

      assertThatThrownBy(() -> collectionService.addCollaborator(collectionId, request))
          .isInstanceOf(AccessDeniedException.class)
          .hasMessage("Can only add collaborators to collections with SPECIFIC_USERS mode");
    }

    verify(recipeCollectionRepository).findById(collectionId);
    verify(collectionCollaboratorRepository, never()).save(any());
  }

  @Test
  @DisplayName("addCollaborator - owner cannot be added as collaborator")
  @Tag("error-handling")
  void addCollaboratorOwnerCannotBeAdded() {
    // Given
    Long collectionId = 1L;
    UUID ownerId = UUID.randomUUID();

    com.recipe_manager.model.dto.request.AddCollaboratorRequest request =
        com.recipe_manager.model.dto.request.AddCollaboratorRequest.builder()
            .userId(ownerId) // Trying to add owner as collaborator
            .build();

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(ownerId)
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .build();

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));

    // When/Then
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(ownerId);

      assertThatThrownBy(() -> collectionService.addCollaborator(collectionId, request))
          .isInstanceOf(DuplicateResourceException.class)
          .hasMessage("Collection owner cannot be added as a collaborator");
    }

    verify(recipeCollectionRepository).findById(collectionId);
    verify(collectionCollaboratorRepository, never()).save(any());
  }

  @Test
  @DisplayName("addCollaborator - user already is collaborator")
  @Tag("error-handling")
  void addCollaboratorUserAlreadyCollaborator() {
    // Given
    Long collectionId = 1L;
    UUID ownerId = UUID.randomUUID();
    UUID collaboratorId = UUID.randomUUID();

    com.recipe_manager.model.dto.request.AddCollaboratorRequest request =
        com.recipe_manager.model.dto.request.AddCollaboratorRequest.builder()
            .userId(collaboratorId)
            .build();

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(ownerId)
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .build();

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));
    when(collectionCollaboratorRepository.existsByIdCollectionIdAndIdUserId(
            collectionId, collaboratorId))
        .thenReturn(true);

    // When/Then
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(ownerId);

      assertThatThrownBy(() -> collectionService.addCollaborator(collectionId, request))
          .isInstanceOf(DuplicateResourceException.class)
          .hasMessage("User is already a collaborator on this collection");
    }

    verify(recipeCollectionRepository).findById(collectionId);
    verify(collectionCollaboratorRepository)
        .existsByIdCollectionIdAndIdUserId(collectionId, collaboratorId);
    verify(collectionCollaboratorRepository, never()).save(any());
  }

  @Test
  @DisplayName("addCollaborator - target user does not exist")
  @Tag("error-handling")
  void addCollaboratorTargetUserDoesNotExist() {
    // Given
    Long collectionId = 1L;
    UUID ownerId = UUID.randomUUID();
    UUID nonExistentUserId = UUID.randomUUID();

    com.recipe_manager.model.dto.request.AddCollaboratorRequest request =
        com.recipe_manager.model.dto.request.AddCollaboratorRequest.builder()
            .userId(nonExistentUserId)
            .build();

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(ownerId)
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .build();

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));
    when(collectionCollaboratorRepository.existsByIdCollectionIdAndIdUserId(
            collectionId, nonExistentUserId))
        .thenReturn(false);
    when(collectionCollaboratorRepository.save(any(CollectionCollaborator.class)))
        .thenThrow(new org.springframework.dao.DataIntegrityViolationException("FK violation"));

    // When/Then
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(ownerId);

      assertThatThrownBy(() -> collectionService.addCollaborator(collectionId, request))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessageContaining("User with ID")
          .hasMessageContaining("not found");
    }

    verify(recipeCollectionRepository).findById(collectionId);
    verify(collectionCollaboratorRepository).save(any(CollectionCollaborator.class));
  }

  @Test
  @DisplayName("removeCollaborator - Success")
  @Tag("standard-processing")
  void removeCollaboratorSuccess() {
    // Given
    Long collectionId = 1L;
    UUID ownerId = UUID.randomUUID();
    UUID collaboratorId = UUID.randomUUID();

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(ownerId)
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .build();

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));
    when(collectionCollaboratorRepository.existsByIdCollectionIdAndIdUserId(
            collectionId, collaboratorId))
        .thenReturn(true);

    // When/Then
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(ownerId);

      ResponseEntity<Void> response =
          collectionService.removeCollaborator(collectionId, collaboratorId);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
      assertThat(response.getBody()).isNull();
    }

    verify(recipeCollectionRepository).findById(collectionId);
    verify(collectionCollaboratorRepository)
        .existsByIdCollectionIdAndIdUserId(collectionId, collaboratorId);
    verify(collectionCollaboratorRepository)
        .deleteByIdCollectionIdAndIdUserId(collectionId, collaboratorId);
  }

  @Test
  @DisplayName("removeCollaborator - Collection not found")
  @Tag("error-handling")
  void removeCollaboratorCollectionNotFound() {
    // Given
    Long collectionId = 999L;
    UUID ownerId = UUID.randomUUID();
    UUID collaboratorId = UUID.randomUUID();

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.empty());

    // When/Then
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(ownerId);

      assertThatThrownBy(() -> collectionService.removeCollaborator(collectionId, collaboratorId))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessage("Collection not found");
    }

    verify(recipeCollectionRepository).findById(collectionId);
    verify(collectionCollaboratorRepository, never())
        .existsByIdCollectionIdAndIdUserId(anyLong(), any(UUID.class));
    verify(collectionCollaboratorRepository, never())
        .deleteByIdCollectionIdAndIdUserId(anyLong(), any(UUID.class));
  }

  @Test
  @DisplayName("removeCollaborator - User is not owner")
  @Tag("error-handling")
  void removeCollaboratorUserIsNotOwner() {
    // Given
    Long collectionId = 1L;
    UUID ownerId = UUID.randomUUID();
    UUID otherUserId = UUID.randomUUID();
    UUID collaboratorId = UUID.randomUUID();

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(ownerId) // Different from current user
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .build();

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));

    // When/Then
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(otherUserId);

      assertThatThrownBy(() -> collectionService.removeCollaborator(collectionId, collaboratorId))
          .isInstanceOf(AccessDeniedException.class)
          .hasMessage("Only the collection owner can remove collaborators");
    }

    verify(recipeCollectionRepository).findById(collectionId);
    verify(collectionCollaboratorRepository, never())
        .existsByIdCollectionIdAndIdUserId(anyLong(), any(UUID.class));
    verify(collectionCollaboratorRepository, never())
        .deleteByIdCollectionIdAndIdUserId(anyLong(), any(UUID.class));
  }

  @Test
  @DisplayName("removeCollaborator - Collaborator not found")
  @Tag("error-handling")
  void removeCollaboratorCollaboratorNotFound() {
    // Given
    Long collectionId = 1L;
    UUID ownerId = UUID.randomUUID();
    UUID nonCollaboratorId = UUID.randomUUID();

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(ownerId)
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .build();

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));
    when(collectionCollaboratorRepository.existsByIdCollectionIdAndIdUserId(
            collectionId, nonCollaboratorId))
        .thenReturn(false);

    // When/Then
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(ownerId);

      assertThatThrownBy(
              () -> collectionService.removeCollaborator(collectionId, nonCollaboratorId))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessageContaining("User with ID")
          .hasMessageContaining("is not a collaborator on this collection");
    }

    verify(recipeCollectionRepository).findById(collectionId);
    verify(collectionCollaboratorRepository)
        .existsByIdCollectionIdAndIdUserId(collectionId, nonCollaboratorId);
    verify(collectionCollaboratorRepository, never())
        .deleteByIdCollectionIdAndIdUserId(anyLong(), any(UUID.class));
  }

  @Test
  @DisplayName("removeCollaborator - Works regardless of collaboration mode")
  @Tag("standard-processing")
  void removeCollaboratorWorksRegardlessOfCollaborationMode() {
    // Given
    Long collectionId = 1L;
    UUID ownerId = UUID.randomUUID();
    UUID collaboratorId = UUID.randomUUID();

    // Collection with OWNER_ONLY mode (not SPECIFIC_USERS)
    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(ownerId)
            .collaborationMode(CollaborationMode.OWNER_ONLY) // Different mode
            .build();

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));
    when(collectionCollaboratorRepository.existsByIdCollectionIdAndIdUserId(
            collectionId, collaboratorId))
        .thenReturn(true);

    // When/Then
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(ownerId);

      ResponseEntity<Void> response =
          collectionService.removeCollaborator(collectionId, collaboratorId);

      // Should succeed - owner can remove collaborators regardless of mode
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    verify(recipeCollectionRepository).findById(collectionId);
    verify(collectionCollaboratorRepository)
        .deleteByIdCollectionIdAndIdUserId(collectionId, collaboratorId);
  }

  @Test
  @DisplayName("removeCollaborator - Verifies deletion occurs")
  @Tag("standard-processing")
  void removeCollaboratorVerifiesDeletion() {
    // Given
    Long collectionId = 1L;
    UUID ownerId = UUID.randomUUID();
    UUID collaboratorId = UUID.randomUUID();

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(ownerId)
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .build();

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));
    when(collectionCollaboratorRepository.existsByIdCollectionIdAndIdUserId(
            collectionId, collaboratorId))
        .thenReturn(true);

    // When
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(ownerId);

      collectionService.removeCollaborator(collectionId, collaboratorId);
    }

    // Then - Verify the delete method was called with correct parameters
    verify(collectionCollaboratorRepository)
        .deleteByIdCollectionIdAndIdUserId(collectionId, collaboratorId);
  }

  // ==================== Batch Operations Tests ====================

  @Test
  @DisplayName("createCollection - Should add recipes during creation when recipeIds provided")
  @Tag("batch-operations")
  void shouldAddRecipesDuringCreation() {
    // Given
    Long recipeId1 = 100L;
    Long recipeId2 = 200L;
    UUID recipeAuthorId = UUID.randomUUID();

    CreateCollectionRequest request =
        CreateCollectionRequest.builder()
            .name("Collection with Recipes")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .recipeIds(Arrays.asList(recipeId1, recipeId2))
            .build();

    RecipeCollection entityToSave = RecipeCollection.builder().name("Collection with Recipes").build();
    RecipeCollection savedEntity =
        RecipeCollection.builder()
            .collectionId(1L)
            .userId(testUserId)
            .name("Collection with Recipes")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    Recipe recipe1 = Recipe.builder().recipeId(recipeId1).userId(recipeAuthorId).build();
    Recipe recipe2 = Recipe.builder().recipeId(recipeId2).userId(recipeAuthorId).build();

    CollectionDetailsDto expectedDto = createTestDetailsDto(1L);

    when(recipeCollectionMapper.fromRequest(request)).thenReturn(entityToSave);
    when(recipeCollectionRepository.save(any(RecipeCollection.class))).thenReturn(savedEntity);
    when(recipeRepository.findById(recipeId1)).thenReturn(Optional.of(recipe1));
    when(recipeRepository.findById(recipeId2)).thenReturn(Optional.of(recipe2));
    when(recipeCollectionItemRepository.save(any(RecipeCollectionItem.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(recipeCollectionRepository.findByIdWithItems(1L)).thenReturn(Optional.of(savedEntity));
    when(collectionMapper.toDetailsDto(savedEntity)).thenReturn(expectedDto);

    // When
    ResponseEntity<CollectionDetailsDto> response;
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
      response = collectionService.createCollection(request);
    }

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();

    // Verify recipes were saved with correct display order
    ArgumentCaptor<RecipeCollectionItem> itemCaptor =
        ArgumentCaptor.forClass(RecipeCollectionItem.class);
    verify(recipeCollectionItemRepository, times(2)).save(itemCaptor.capture());

    List<RecipeCollectionItem> savedItems = itemCaptor.getAllValues();
    assertThat(savedItems.get(0).getDisplayOrder()).isEqualTo(10);
    assertThat(savedItems.get(1).getDisplayOrder()).isEqualTo(20);

    // Verify collection and recipe relationships are set (required for @MapsId)
    assertThat(savedItems.get(0).getCollection()).isEqualTo(savedEntity);
    assertThat(savedItems.get(0).getRecipe()).isEqualTo(recipe1);
    assertThat(savedItems.get(1).getCollection()).isEqualTo(savedEntity);
    assertThat(savedItems.get(1).getRecipe()).isEqualTo(recipe2);

    // Verify notifications were sent
    verify(notificationService, times(2))
        .notifyRecipeCollectedAsync(eq(recipeAuthorId), anyLong(), eq(1L), eq(testUserId));
  }

  @Test
  @DisplayName("createCollection - Should add collaborators during creation when SPECIFIC_USERS mode")
  @Tag("batch-operations")
  void shouldAddCollaboratorsDuringCreation() {
    // Given
    UUID collaboratorId1 = UUID.randomUUID();
    UUID collaboratorId2 = UUID.randomUUID();

    CreateCollectionRequest request =
        CreateCollectionRequest.builder()
            .name("Collaborative Collection")
            .visibility(CollectionVisibility.PRIVATE)
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .collaboratorIds(Arrays.asList(collaboratorId1, collaboratorId2))
            .build();

    RecipeCollection entityToSave =
        RecipeCollection.builder()
            .name("Collaborative Collection")
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .build();
    RecipeCollection savedEntity =
        RecipeCollection.builder()
            .collectionId(1L)
            .userId(testUserId)
            .name("Collaborative Collection")
            .visibility(CollectionVisibility.PRIVATE)
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .build();

    CollectionDetailsDto expectedDto = createTestDetailsDto(1L);

    when(recipeCollectionMapper.fromRequest(request)).thenReturn(entityToSave);
    when(recipeCollectionRepository.save(any(RecipeCollection.class))).thenReturn(savedEntity);
    when(collectionCollaboratorRepository.save(any(CollectionCollaborator.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(recipeCollectionRepository.findByIdWithItems(1L)).thenReturn(Optional.of(savedEntity));
    when(collectionMapper.toDetailsDto(savedEntity)).thenReturn(expectedDto);

    // When
    ResponseEntity<CollectionDetailsDto> response;
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
      response = collectionService.createCollection(request);
    }

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();

    // Verify collaborators were saved
    verify(collectionCollaboratorRepository, times(2)).save(any(CollectionCollaborator.class));
  }

  @Test
  @DisplayName("createCollection - Should add both recipes and collaborators during creation")
  @Tag("batch-operations")
  void shouldAddBothRecipesAndCollaboratorsDuringCreation() {
    // Given
    Long recipeId = 100L;
    UUID recipeAuthorId = UUID.randomUUID();
    UUID collaboratorId = UUID.randomUUID();

    CreateCollectionRequest request =
        CreateCollectionRequest.builder()
            .name("Full Collection")
            .visibility(CollectionVisibility.PRIVATE)
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .recipeIds(Arrays.asList(recipeId))
            .collaboratorIds(Arrays.asList(collaboratorId))
            .build();

    RecipeCollection entityToSave =
        RecipeCollection.builder()
            .name("Full Collection")
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .build();
    RecipeCollection savedEntity =
        RecipeCollection.builder()
            .collectionId(1L)
            .userId(testUserId)
            .name("Full Collection")
            .visibility(CollectionVisibility.PRIVATE)
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .build();

    Recipe recipe = Recipe.builder().recipeId(recipeId).userId(recipeAuthorId).build();

    CollectionDetailsDto expectedDto = createTestDetailsDto(1L);

    when(recipeCollectionMapper.fromRequest(request)).thenReturn(entityToSave);
    when(recipeCollectionRepository.save(any(RecipeCollection.class))).thenReturn(savedEntity);
    when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
    when(recipeCollectionItemRepository.save(any(RecipeCollectionItem.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(collectionCollaboratorRepository.save(any(CollectionCollaborator.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(recipeCollectionRepository.findByIdWithItems(1L)).thenReturn(Optional.of(savedEntity));
    when(collectionMapper.toDetailsDto(savedEntity)).thenReturn(expectedDto);

    // When
    ResponseEntity<CollectionDetailsDto> response;
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
      response = collectionService.createCollection(request);
    }

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();

    verify(recipeCollectionItemRepository, times(1)).save(any(RecipeCollectionItem.class));
    verify(collectionCollaboratorRepository, times(1)).save(any(CollectionCollaborator.class));
  }

  @Test
  @DisplayName("createCollection - Should ignore collaborators when not SPECIFIC_USERS mode")
  @Tag("batch-operations")
  void shouldIgnoreCollaboratorsWhenNotSpecificUsersMode() {
    // Given
    UUID collaboratorId = UUID.randomUUID();

    CreateCollectionRequest request =
        CreateCollectionRequest.builder()
            .name("Owner Only Collection")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .collaboratorIds(Arrays.asList(collaboratorId))
            .build();

    RecipeCollection entityToSave =
        RecipeCollection.builder()
            .name("Owner Only Collection")
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();
    RecipeCollection savedEntity =
        RecipeCollection.builder()
            .collectionId(1L)
            .userId(testUserId)
            .name("Owner Only Collection")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    CollectionDetailsDto expectedDto = createTestDetailsDto(1L);

    when(recipeCollectionMapper.fromRequest(request)).thenReturn(entityToSave);
    when(recipeCollectionRepository.save(any(RecipeCollection.class))).thenReturn(savedEntity);
    when(recipeCollectionRepository.findByIdWithItems(1L)).thenReturn(Optional.of(savedEntity));
    when(collectionMapper.toDetailsDto(savedEntity)).thenReturn(expectedDto);

    // When
    ResponseEntity<CollectionDetailsDto> response;
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
      response = collectionService.createCollection(request);
    }

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();

    // Verify collaborators were NOT saved
    verify(collectionCollaboratorRepository, never()).save(any(CollectionCollaborator.class));
  }

  @Test
  @DisplayName("createCollection - Should skip owner in collaborator list")
  @Tag("batch-operations")
  void shouldSkipOwnerInCollaboratorList() {
    // Given
    UUID otherCollaboratorId = UUID.randomUUID();

    CreateCollectionRequest request =
        CreateCollectionRequest.builder()
            .name("Collection")
            .visibility(CollectionVisibility.PRIVATE)
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .collaboratorIds(Arrays.asList(testUserId, otherCollaboratorId)) // Include owner
            .build();

    RecipeCollection entityToSave =
        RecipeCollection.builder()
            .name("Collection")
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .build();
    RecipeCollection savedEntity =
        RecipeCollection.builder()
            .collectionId(1L)
            .userId(testUserId)
            .name("Collection")
            .visibility(CollectionVisibility.PRIVATE)
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .build();

    CollectionDetailsDto expectedDto = createTestDetailsDto(1L);

    when(recipeCollectionMapper.fromRequest(request)).thenReturn(entityToSave);
    when(recipeCollectionRepository.save(any(RecipeCollection.class))).thenReturn(savedEntity);
    when(collectionCollaboratorRepository.save(any(CollectionCollaborator.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(recipeCollectionRepository.findByIdWithItems(1L)).thenReturn(Optional.of(savedEntity));
    when(collectionMapper.toDetailsDto(savedEntity)).thenReturn(expectedDto);

    // When
    ResponseEntity<CollectionDetailsDto> response;
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
      response = collectionService.createCollection(request);
    }

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();

    // Verify only one collaborator was saved (not the owner)
    verify(collectionCollaboratorRepository, times(1)).save(any(CollectionCollaborator.class));
  }

  @Test
  @DisplayName("createCollection - Should throw exception for non-existent recipe")
  @Tag("batch-operations")
  void shouldThrowExceptionForNonExistentRecipe() {
    // Given
    Long nonExistentRecipeId = 999L;

    CreateCollectionRequest request =
        CreateCollectionRequest.builder()
            .name("Collection")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .recipeIds(Arrays.asList(nonExistentRecipeId))
            .build();

    RecipeCollection entityToSave = RecipeCollection.builder().name("Collection").build();
    RecipeCollection savedEntity =
        RecipeCollection.builder()
            .collectionId(1L)
            .userId(testUserId)
            .name("Collection")
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    when(recipeCollectionMapper.fromRequest(request)).thenReturn(entityToSave);
    when(recipeCollectionRepository.save(any(RecipeCollection.class))).thenReturn(savedEntity);
    when(recipeRepository.findById(nonExistentRecipeId)).thenReturn(Optional.empty());

    // When/Then
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      assertThatThrownBy(() -> collectionService.createCollection(request))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessageContaining("Recipe with ID " + nonExistentRecipeId + " not found");
    }
  }

  @Test
  @DisplayName("createCollection - Should throw exception for non-existent collaborator user")
  @Tag("batch-operations")
  void shouldThrowExceptionForNonExistentCollaboratorUser() {
    // Given
    UUID nonExistentUserId = UUID.randomUUID();

    CreateCollectionRequest request =
        CreateCollectionRequest.builder()
            .name("Collection")
            .visibility(CollectionVisibility.PRIVATE)
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .collaboratorIds(Arrays.asList(nonExistentUserId))
            .build();

    RecipeCollection entityToSave =
        RecipeCollection.builder()
            .name("Collection")
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .build();
    RecipeCollection savedEntity =
        RecipeCollection.builder()
            .collectionId(1L)
            .userId(testUserId)
            .name("Collection")
            .visibility(CollectionVisibility.PRIVATE)
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .build();

    when(recipeCollectionMapper.fromRequest(request)).thenReturn(entityToSave);
    when(recipeCollectionRepository.save(any(RecipeCollection.class))).thenReturn(savedEntity);
    when(collectionCollaboratorRepository.save(any(CollectionCollaborator.class)))
        .thenThrow(new org.springframework.dao.DataIntegrityViolationException("FK violation"));

    // When/Then
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      assertThatThrownBy(() -> collectionService.createCollection(request))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessageContaining("User with ID " + nonExistentUserId + " not found");
    }
  }

  @Test
  @DisplayName("createCollection - Should handle empty recipe and collaborator lists")
  @Tag("batch-operations")
  void shouldHandleEmptyRecipeAndCollaboratorLists() {
    // Given
    CreateCollectionRequest request =
        CreateCollectionRequest.builder()
            .name("Empty Collection")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .recipeIds(Collections.emptyList())
            .collaboratorIds(Collections.emptyList())
            .build();

    RecipeCollection entityToSave =
        RecipeCollection.builder()
            .name("Empty Collection")
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .build();
    RecipeCollection savedEntity =
        RecipeCollection.builder()
            .collectionId(1L)
            .userId(testUserId)
            .name("Empty Collection")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .build();

    CollectionDetailsDto expectedDto = createTestDetailsDto(1L);

    when(recipeCollectionMapper.fromRequest(request)).thenReturn(entityToSave);
    when(recipeCollectionRepository.save(any(RecipeCollection.class))).thenReturn(savedEntity);
    when(recipeCollectionRepository.findByIdWithItems(1L)).thenReturn(Optional.of(savedEntity));
    when(collectionMapper.toDetailsDto(savedEntity)).thenReturn(expectedDto);

    // When
    ResponseEntity<CollectionDetailsDto> response;
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
      response = collectionService.createCollection(request);
    }

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();

    // Verify no recipes or collaborators were saved
    verify(recipeCollectionItemRepository, never()).save(any(RecipeCollectionItem.class));
    verify(collectionCollaboratorRepository, never()).save(any(CollectionCollaborator.class));
  }

  @Test
  @DisplayName("createCollection - Should handle duplicate recipe IDs in request")
  @Tag("batch-operations")
  void shouldHandleDuplicateRecipeIdsInRequest() {
    // Given
    Long recipeId = 100L;
    UUID recipeAuthorId = UUID.randomUUID();

    CreateCollectionRequest request =
        CreateCollectionRequest.builder()
            .name("Collection")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .recipeIds(Arrays.asList(recipeId, recipeId, recipeId)) // Same ID 3 times
            .build();

    RecipeCollection entityToSave = RecipeCollection.builder().name("Collection").build();
    RecipeCollection savedEntity =
        RecipeCollection.builder()
            .collectionId(1L)
            .userId(testUserId)
            .name("Collection")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    Recipe recipe = Recipe.builder().recipeId(recipeId).userId(recipeAuthorId).build();

    CollectionDetailsDto expectedDto = createTestDetailsDto(1L);

    when(recipeCollectionMapper.fromRequest(request)).thenReturn(entityToSave);
    when(recipeCollectionRepository.save(any(RecipeCollection.class))).thenReturn(savedEntity);
    when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
    when(recipeCollectionItemRepository.save(any(RecipeCollectionItem.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(recipeCollectionRepository.findByIdWithItems(1L)).thenReturn(Optional.of(savedEntity));
    when(collectionMapper.toDetailsDto(savedEntity)).thenReturn(expectedDto);

    // When
    ResponseEntity<CollectionDetailsDto> response;
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
      response = collectionService.createCollection(request);
    }

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();

    // Verify only one recipe was saved (duplicates skipped)
    verify(recipeCollectionItemRepository, times(1)).save(any(RecipeCollectionItem.class));
    verify(recipeRepository, times(1)).findById(recipeId); // Only looked up once
  }

  @Test
  @DisplayName("createCollection - Should handle duplicate collaborator IDs in request")
  @Tag("batch-operations")
  void shouldHandleDuplicateCollaboratorIdsInRequest() {
    // Given
    UUID collaboratorId = UUID.randomUUID();

    CreateCollectionRequest request =
        CreateCollectionRequest.builder()
            .name("Collection")
            .visibility(CollectionVisibility.PRIVATE)
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .collaboratorIds(Arrays.asList(collaboratorId, collaboratorId, collaboratorId))
            .build();

    RecipeCollection entityToSave =
        RecipeCollection.builder()
            .name("Collection")
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .build();
    RecipeCollection savedEntity =
        RecipeCollection.builder()
            .collectionId(1L)
            .userId(testUserId)
            .name("Collection")
            .visibility(CollectionVisibility.PRIVATE)
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .build();

    CollectionDetailsDto expectedDto = createTestDetailsDto(1L);

    when(recipeCollectionMapper.fromRequest(request)).thenReturn(entityToSave);
    when(recipeCollectionRepository.save(any(RecipeCollection.class))).thenReturn(savedEntity);
    when(collectionCollaboratorRepository.save(any(CollectionCollaborator.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(recipeCollectionRepository.findByIdWithItems(1L)).thenReturn(Optional.of(savedEntity));
    when(collectionMapper.toDetailsDto(savedEntity)).thenReturn(expectedDto);

    // When
    ResponseEntity<CollectionDetailsDto> response;
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
      response = collectionService.createCollection(request);
    }

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();

    // Verify only one collaborator was saved (duplicates skipped)
    verify(collectionCollaboratorRepository, times(1)).save(any(CollectionCollaborator.class));
  }

  @Test
  @DisplayName("createCollection - Should ignore collaborators for ALL_USERS mode")
  @Tag("batch-operations")
  void shouldIgnoreCollaboratorsForAllUsersMode() {
    // Given
    UUID collaboratorId = UUID.randomUUID();

    CreateCollectionRequest request =
        CreateCollectionRequest.builder()
            .name("All Users Collection")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.ALL_USERS)
            .collaboratorIds(Arrays.asList(collaboratorId))
            .build();

    RecipeCollection entityToSave =
        RecipeCollection.builder()
            .name("All Users Collection")
            .collaborationMode(CollaborationMode.ALL_USERS)
            .build();
    RecipeCollection savedEntity =
        RecipeCollection.builder()
            .collectionId(1L)
            .userId(testUserId)
            .name("All Users Collection")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.ALL_USERS)
            .build();

    CollectionDetailsDto expectedDto = createTestDetailsDto(1L);

    when(recipeCollectionMapper.fromRequest(request)).thenReturn(entityToSave);
    when(recipeCollectionRepository.save(any(RecipeCollection.class))).thenReturn(savedEntity);
    when(recipeCollectionRepository.findByIdWithItems(1L)).thenReturn(Optional.of(savedEntity));
    when(collectionMapper.toDetailsDto(savedEntity)).thenReturn(expectedDto);

    // When
    ResponseEntity<CollectionDetailsDto> response;
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
      response = collectionService.createCollection(request);
    }

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();

    // Verify collaborators were NOT saved
    verify(collectionCollaboratorRepository, never()).save(any(CollectionCollaborator.class));
  }
}
