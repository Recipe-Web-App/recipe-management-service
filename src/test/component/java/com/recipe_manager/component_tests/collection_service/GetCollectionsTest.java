package com.recipe_manager.component_tests.collection_service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.recipe_manager.config.RequestIdFilter;
import com.recipe_manager.controller.CollectionController;
import com.recipe_manager.exception.GlobalExceptionHandler;
import com.recipe_manager.model.enums.CollaborationMode;
import com.recipe_manager.model.enums.CollectionVisibility;
import com.recipe_manager.model.mapper.CollectionMapper;
import com.recipe_manager.model.mapper.RecipeCollectionItemMapper;
import com.recipe_manager.model.mapper.RecipeCollectionMapper;
import com.recipe_manager.repository.collection.CollectionCollaboratorRepository;
import com.recipe_manager.repository.collection.CollectionSummaryProjection;
import com.recipe_manager.repository.collection.RecipeCollectionItemRepository;
import com.recipe_manager.repository.collection.RecipeCollectionRepository;
import com.recipe_manager.service.CollectionService;

/**
 * Component test for GET /collections endpoint.
 *
 * <p>Tests the integration between CollectionController, CollectionService, and CollectionMapper
 * with mocked repository layer.
 */
@Tag("component")
@SpringBootTest(
    classes = {
      com.recipe_manager.model.mapper.CollectionMapperImpl.class,
      com.recipe_manager.model.mapper.RecipeCollectionMapperImpl.class,
      com.recipe_manager.model.mapper.RecipeCollectionItemMapperImpl.class,
      com.recipe_manager.model.mapper.CollectionCollaboratorMapperImpl.class
    })
@TestPropertySource(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:testdb",
      "spring.jpa.hibernate.ddl-auto=none",
      "spring.flyway.enabled=false"
    })
class GetCollectionsTest {

  private MockMvc mockMvc;

  @Mock private RecipeCollectionRepository recipeCollectionRepository;

  @Mock private RecipeCollectionItemRepository recipeCollectionItemRepository;

  @Mock private CollectionCollaboratorRepository collectionCollaboratorRepository;

  @Autowired private CollectionMapper collectionMapper;

  @Autowired private RecipeCollectionMapper recipeCollectionMapper;

  @Autowired private RecipeCollectionItemMapper recipeCollectionItemMapper;

  private CollectionService collectionService;
  private CollectionController collectionController;

  private UUID testUserId;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    testUserId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

    // Set up security context with test user ID
    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(testUserId.toString(), null, Collections.emptyList());
    securityContext.setAuthentication(authentication);
    SecurityContextHolder.setContext(securityContext);

    // Create real service with mocked repository
    collectionService =
        new CollectionService(
            recipeCollectionRepository,
            recipeCollectionItemRepository,
            collectionCollaboratorRepository,
            collectionMapper,
            recipeCollectionMapper,
            recipeCollectionItemMapper);
    collectionController = new CollectionController(collectionService);

    mockMvc =
        MockMvcBuilders.standaloneSetup(collectionController)
            .addFilters(new RequestIdFilter())
            .setControllerAdvice(new GlobalExceptionHandler())
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();
  }

  @Test
  @DisplayName("Should get accessible collections successfully with real mapper")
  @Tag("standard-processing")
  void shouldGetAccessibleCollectionsSuccessfully() throws Exception {
    // Given
    Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 20);
    List<CollectionSummaryProjection> projections =
        Arrays.asList(createTestProjection(1L), createTestProjection(2L));
    Page<CollectionSummaryProjection> projectionPage =
        new PageImpl<>(projections, pageable, 2);

    when(recipeCollectionRepository.findAccessibleCollections(any(UUID.class), any(Pageable.class)))
        .thenReturn(projectionPage);

    // When & Then
    mockMvc
        .perform(get("/collections").param("page", "0").param("size", "20"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content.length()").value(2))
        .andExpect(jsonPath("$.content[0].collectionId").value(1))
        .andExpect(jsonPath("$.content[0].userId").value(testUserId.toString()))
        .andExpect(jsonPath("$.content[0].name").value("Test Collection 1"))
        .andExpect(jsonPath("$.content[0].visibility").value("PUBLIC"))
        .andExpect(jsonPath("$.content[0].collaborationMode").value("OWNER_ONLY"))
        .andExpect(jsonPath("$.content[0].recipeCount").value(5))
        .andExpect(jsonPath("$.content[0].collaboratorCount").value(2))
        .andExpect(jsonPath("$.content[1].collectionId").value(2))
        .andExpect(jsonPath("$.totalElements").value(2))
        .andExpect(jsonPath("$.totalPages").value(1))
        .andExpect(jsonPath("$.number").value(0))
        .andExpect(jsonPath("$.size").value(20));
  }

  @Test
  @DisplayName("Should return empty page when no collections accessible")
  @Tag("standard-processing")
  void shouldReturnEmptyPageWhenNoCollections() throws Exception {
    // Given
    Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 20);
    Page<CollectionSummaryProjection> emptyPage =
        new PageImpl<>(Collections.emptyList(), pageable, 0);

    when(recipeCollectionRepository.findAccessibleCollections(any(UUID.class), any(Pageable.class)))
        .thenReturn(emptyPage);

    // When & Then
    mockMvc
        .perform(get("/collections"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content.length()").value(0))
        .andExpect(jsonPath("$.totalElements").value(0))
        .andExpect(jsonPath("$.empty").value(true));
  }

  @Test
  @DisplayName("Should handle custom page size parameter")
  @Tag("standard-processing")
  void shouldHandleCustomPageSize() throws Exception {
    // Given
    List<CollectionSummaryProjection> projections =
        Arrays.asList(
            createTestProjection(1L),
            createTestProjection(2L),
            createTestProjection(3L),
            createTestProjection(4L),
            createTestProjection(5L));
    Page<CollectionSummaryProjection> projectionPage =
        new PageImpl<>(projections, Pageable.ofSize(5), 10);

    when(recipeCollectionRepository.findAccessibleCollections(any(UUID.class), any(Pageable.class)))
        .thenReturn(projectionPage);

    // When & Then
    mockMvc
        .perform(get("/collections").param("page", "0").param("size", "5"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(5))
        .andExpect(jsonPath("$.size").value(5))
        .andExpect(jsonPath("$.totalElements").value(10))
        .andExpect(jsonPath("$.totalPages").value(2));
  }

  @Test
  @DisplayName("Should handle second page request")
  @Tag("standard-processing")
  void shouldHandleSecondPageRequest() throws Exception {
    // Given
    List<CollectionSummaryProjection> projections =
        Arrays.asList(createTestProjection(11L), createTestProjection(12L));
    Page<CollectionSummaryProjection> projectionPage =
        new PageImpl<>(projections, Pageable.ofSize(10).withPage(1), 25);

    when(recipeCollectionRepository.findAccessibleCollections(any(UUID.class), any(Pageable.class)))
        .thenReturn(projectionPage);

    // When & Then
    mockMvc
        .perform(get("/collections").param("page", "1").param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(2))
        .andExpect(jsonPath("$.number").value(1))
        .andExpect(jsonPath("$.first").value(false))
        .andExpect(jsonPath("$.totalPages").value(3));
  }

  @Test
  @DisplayName("Should map all enum values correctly")
  @Tag("standard-processing")
  void shouldMapAllEnumValuesCorrectly() throws Exception {
    // Given
    Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 20);
    CollectionSummaryProjection projection = createCustomProjection(
        1L, CollectionVisibility.PRIVATE, CollaborationMode.ALL_USERS);
    Page<CollectionSummaryProjection> projectionPage =
        new PageImpl<>(Arrays.asList(projection), pageable, 1);

    when(recipeCollectionRepository.findAccessibleCollections(any(UUID.class), any(Pageable.class)))
        .thenReturn(projectionPage);

    // When & Then
    mockMvc
        .perform(get("/collections"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].visibility").value("PRIVATE"))
        .andExpect(jsonPath("$.content[0].collaborationMode").value("ALL_USERS"));
  }

  @Test
  @DisplayName("Should handle null description in projection")
  @Tag("standard-processing")
  void shouldHandleNullDescription() throws Exception {
    // Given
    Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 20);
    CollectionSummaryProjection projection = createProjectionWithNullDescription(1L);
    Page<CollectionSummaryProjection> projectionPage =
        new PageImpl<>(Arrays.asList(projection), pageable, 1);

    when(recipeCollectionRepository.findAccessibleCollections(any(UUID.class), any(Pageable.class)))
        .thenReturn(projectionPage);

    // When & Then
    mockMvc
        .perform(get("/collections"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].collectionId").value(1))
        .andExpect(jsonPath("$.content[0].name").exists())
        .andExpect(jsonPath("$.content[0].description").isEmpty());
  }

  @Test
  @DisplayName("Should preserve all DTO fields through mapper")
  @Tag("standard-processing")
  void shouldPreserveAllDtoFields() throws Exception {
    // Given
    Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 20);
    CollectionSummaryProjection projection = createTestProjection(123L);
    Page<CollectionSummaryProjection> projectionPage =
        new PageImpl<>(Arrays.asList(projection), pageable, 1);

    when(recipeCollectionRepository.findAccessibleCollections(any(UUID.class), any(Pageable.class)))
        .thenReturn(projectionPage);

    // When & Then
    mockMvc
        .perform(get("/collections"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].collectionId").value(123))
        .andExpect(jsonPath("$.content[0].userId").exists())
        .andExpect(jsonPath("$.content[0].name").exists())
        .andExpect(jsonPath("$.content[0].description").exists())
        .andExpect(jsonPath("$.content[0].visibility").exists())
        .andExpect(jsonPath("$.content[0].collaborationMode").exists())
        .andExpect(jsonPath("$.content[0].recipeCount").exists())
        .andExpect(jsonPath("$.content[0].collaboratorCount").exists())
        .andExpect(jsonPath("$.content[0].createdAt").exists())
        .andExpect(jsonPath("$.content[0].updatedAt").exists());
  }

  private CollectionSummaryProjection createTestProjection(Long collectionId) {
    return createCustomProjection(
        collectionId, CollectionVisibility.PUBLIC, CollaborationMode.OWNER_ONLY);
  }

  private CollectionSummaryProjection createCustomProjection(
      Long collectionId,
      CollectionVisibility visibility,
      CollaborationMode collaborationMode) {
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
        return visibility;
      }

      @Override
      public CollaborationMode getCollaborationMode() {
        return collaborationMode;
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
        return 2;
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

  private CollectionSummaryProjection createProjectionWithNullDescription(Long collectionId) {
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
        return null;
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
        return 0;
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
}
