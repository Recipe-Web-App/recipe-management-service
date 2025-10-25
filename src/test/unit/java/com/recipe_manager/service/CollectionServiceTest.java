package com.recipe_manager.service;

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
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.recipe_manager.model.dto.response.CollectionDto;
import com.recipe_manager.model.enums.CollaborationMode;
import com.recipe_manager.model.enums.CollectionVisibility;
import com.recipe_manager.model.mapper.CollectionMapper;
import com.recipe_manager.repository.collection.CollectionSummaryProjection;
import com.recipe_manager.repository.collection.RecipeCollectionRepository;
import com.recipe_manager.util.SecurityUtils;

/** Unit tests for CollectionService. */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
class CollectionServiceTest {

  @Mock private RecipeCollectionRepository recipeCollectionRepository;

  @Mock private CollectionMapper collectionMapper;

  private CollectionService collectionService;

  private UUID testUserId;

  @BeforeEach
  void setUp() {
    collectionService = new CollectionService(recipeCollectionRepository, collectionMapper);
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
}
