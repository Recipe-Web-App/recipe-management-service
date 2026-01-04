package com.recipe_manager.component_tests.user_service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import com.recipe_manager.config.RequestIdFilter;
import com.recipe_manager.controller.UserController;
import com.recipe_manager.exception.GlobalExceptionHandler;
import com.recipe_manager.model.enums.CollaborationMode;
import com.recipe_manager.model.enums.CollectionVisibility;
import com.recipe_manager.model.mapper.CollectionMapper;
import com.recipe_manager.model.mapper.CollectionMapperImpl;
import com.recipe_manager.model.mapper.RecipeCollectionItemMapper;
import com.recipe_manager.model.mapper.RecipeCollectionItemMapperImpl;
import com.recipe_manager.model.mapper.RecipeCollectionMapper;
import com.recipe_manager.model.mapper.CollectionCollaboratorMapperImpl;
import com.recipe_manager.model.mapper.RecipeCollectionMapperImpl;
import com.recipe_manager.repository.collection.CollectionCollaboratorRepository;
import com.recipe_manager.repository.collection.CollectionSummaryProjection;
import com.recipe_manager.repository.collection.CollectionTagRepository;
import com.recipe_manager.repository.collection.RecipeCollectionItemRepository;
import com.recipe_manager.repository.collection.RecipeCollectionRepository;
import com.recipe_manager.repository.recipe.RecipeRepository;
import com.recipe_manager.service.CollectionService;
import com.recipe_manager.service.RecipeService;
import com.recipe_manager.service.external.notificationservice.NotificationService;
import com.recipe_manager.util.SecurityUtils;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Component tests for GET /users/me/collections endpoint. Tests the actual CollectionService
 * getMyCollections logic with mocked repository calls.
 */
@SpringBootTest(
    classes = {
      CollectionMapperImpl.class,
      RecipeCollectionMapperImpl.class,
      RecipeCollectionItemMapperImpl.class,
      CollectionCollaboratorMapperImpl.class
    })
@TestPropertySource(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:testdb",
      "spring.jpa.hibernate.ddl-auto=none",
      "spring.flyway.enabled=false"
    })
@Tag("component")
class GetMyCollectionsComponentTest {

  protected MockMvc mockMvc;

  @Mock protected RecipeCollectionRepository recipeCollectionRepository;

  @Mock protected RecipeCollectionItemRepository recipeCollectionItemRepository;

  @Mock protected CollectionCollaboratorRepository collectionCollaboratorRepository;

  @Mock protected CollectionTagRepository collectionTagRepository;

  @Mock protected RecipeRepository recipeRepository;

  @Mock protected NotificationService notificationService;

  @Mock protected EntityManager entityManager;

  @Mock protected RecipeService recipeService;

  @Autowired protected CollectionMapper collectionMapper;

  @Autowired protected RecipeCollectionMapper recipeCollectionMapper;

  @Autowired protected RecipeCollectionItemMapper recipeCollectionItemMapper;

  private CollectionService collectionService;
  private UserController userController;

  private UUID testUserId;
  private UUID otherUserId;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    testUserId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    otherUserId = UUID.fromString("99999999-9999-9999-9999-999999999999");

    // Create real service with mocked repositories
    collectionService =
        new CollectionService(
            recipeCollectionRepository,
            recipeCollectionItemRepository,
            collectionCollaboratorRepository,
            collectionTagRepository,
            collectionMapper,
            recipeCollectionMapper,
            recipeCollectionItemMapper,
            recipeRepository,
            notificationService,
            entityManager);

    userController = new UserController(recipeService, collectionService);

    mockMvc =
        MockMvcBuilders.standaloneSetup(userController)
            .addFilters(new RequestIdFilter())
            .setControllerAdvice(new GlobalExceptionHandler())
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("GET /users/me/collections should return paginated owned collections successfully")
  void shouldReturnPaginatedOwnedCollectionsSuccessfully() throws Exception {
    // Given
    Pageable pageable = PageRequest.of(0, 20);
    Page<CollectionSummaryProjection> projectionPage =
        new PageImpl<>(
            Arrays.asList(
                createTestProjection(1L, testUserId, "My Favorites"),
                createTestProjection(2L, testUserId, "Quick Meals")),
            pageable,
            2);

    try (MockedStatic<SecurityUtils> securityMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
      when(recipeCollectionRepository.findOwnedCollections(eq(testUserId), any(Pageable.class)))
          .thenReturn(projectionPage);

      // When & Then
      mockMvc
          .perform(get("/users/me/collections").accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.content").isArray())
          .andExpect(jsonPath("$.content.length()").value(2))
          .andExpect(jsonPath("$.content[0].collectionId").value(1))
          .andExpect(jsonPath("$.content[0].name").value("My Favorites"))
          .andExpect(jsonPath("$.content[0].userId").value(testUserId.toString()))
          .andExpect(jsonPath("$.content[1].collectionId").value(2))
          .andExpect(jsonPath("$.content[1].name").value("Quick Meals"))
          .andExpect(jsonPath("$.totalElements").value(2))
          .andExpect(jsonPath("$.totalPages").value(1))
          .andExpect(jsonPath("$.first").value(true))
          .andExpect(jsonPath("$.last").value(true))
          .andExpect(jsonPath("$.empty").value(false));
    }
  }

  @Test
  @Tag("standard-processing")
  @DisplayName(
      "GET /users/me/collections with includeCollaborations=true should include collaborating"
          + " collections")
  void shouldIncludeCollaboratingCollectionsWhenFlagIsTrue() throws Exception {
    // Given
    Pageable pageable = PageRequest.of(0, 20);
    Page<CollectionSummaryProjection> projectionPage =
        new PageImpl<>(
            Arrays.asList(
                createTestProjection(1L, testUserId, "My Collection"),
                createTestProjection(2L, otherUserId, "Shared Collection")),
            pageable,
            2);

    try (MockedStatic<SecurityUtils> securityMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
      when(recipeCollectionRepository.findOwnedAndCollaboratingCollections(
              eq(testUserId), any(Pageable.class)))
          .thenReturn(projectionPage);

      // When & Then
      mockMvc
          .perform(
              get("/users/me/collections")
                  .param("includeCollaborations", "true")
                  .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.content").isArray())
          .andExpect(jsonPath("$.content.length()").value(2))
          .andExpect(jsonPath("$.content[0].name").value("My Collection"))
          .andExpect(jsonPath("$.content[0].userId").value(testUserId.toString()))
          .andExpect(jsonPath("$.content[1].name").value("Shared Collection"))
          .andExpect(jsonPath("$.content[1].userId").value(otherUserId.toString()))
          .andExpect(jsonPath("$.totalElements").value(2));
    }
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("GET /users/me/collections should return empty page when user has no collections")
  void shouldReturnEmptyPageWhenUserHasNoCollections() throws Exception {
    // Given
    Pageable pageable = PageRequest.of(0, 20);
    Page<CollectionSummaryProjection> emptyPage =
        new PageImpl<>(Collections.emptyList(), pageable, 0);

    try (MockedStatic<SecurityUtils> securityMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
      when(recipeCollectionRepository.findOwnedCollections(eq(testUserId), any(Pageable.class)))
          .thenReturn(emptyPage);

      // When & Then
      mockMvc
          .perform(get("/users/me/collections").accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.content").isArray())
          .andExpect(jsonPath("$.content.length()").value(0))
          .andExpect(jsonPath("$.totalElements").value(0))
          .andExpect(jsonPath("$.totalPages").value(0))
          .andExpect(jsonPath("$.first").value(true))
          .andExpect(jsonPath("$.last").value(true))
          .andExpect(jsonPath("$.empty").value(true));
    }
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("GET /users/me/collections should handle pagination parameters correctly")
  void shouldHandlePaginationParametersCorrectly() throws Exception {
    // Given
    Pageable pageable = PageRequest.of(1, 1);
    Page<CollectionSummaryProjection> pagedCollections =
        new PageImpl<>(
            Arrays.asList(createTestProjection(2L, testUserId, "Second Collection")), pageable, 3);

    try (MockedStatic<SecurityUtils> securityMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
      when(recipeCollectionRepository.findOwnedCollections(eq(testUserId), any(Pageable.class)))
          .thenReturn(pagedCollections);

      // When & Then
      mockMvc
          .perform(
              get("/users/me/collections")
                  .param("page", "1")
                  .param("size", "1")
                  .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.content").isArray())
          .andExpect(jsonPath("$.content.length()").value(1))
          .andExpect(jsonPath("$.content[0].name").value("Second Collection"))
          .andExpect(jsonPath("$.number").value(1))
          .andExpect(jsonPath("$.size").value(1))
          .andExpect(jsonPath("$.totalElements").value(3))
          .andExpect(jsonPath("$.totalPages").value(3))
          .andExpect(jsonPath("$.first").value(false))
          .andExpect(jsonPath("$.last").value(false));
    }
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("GET /users/me/collections should map all collection fields correctly")
  void shouldMapAllCollectionFieldsCorrectly() throws Exception {
    // Given
    Pageable pageable = PageRequest.of(0, 20);
    Page<CollectionSummaryProjection> projectionPage =
        new PageImpl<>(
            Arrays.asList(createTestProjection(1L, testUserId, "Complete Collection")),
            pageable,
            1);

    try (MockedStatic<SecurityUtils> securityMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
      when(recipeCollectionRepository.findOwnedCollections(eq(testUserId), any(Pageable.class)))
          .thenReturn(projectionPage);

      // When & Then
      mockMvc
          .perform(get("/users/me/collections").accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.content[0].collectionId").value(1))
          .andExpect(jsonPath("$.content[0].name").value("Complete Collection"))
          .andExpect(jsonPath("$.content[0].description").value("Test Description"))
          .andExpect(jsonPath("$.content[0].visibility").value("PUBLIC"))
          .andExpect(jsonPath("$.content[0].collaborationMode").value("OWNER_ONLY"))
          .andExpect(jsonPath("$.content[0].userId").value(testUserId.toString()))
          .andExpect(jsonPath("$.content[0].recipeCount").value(5))
          .andExpect(jsonPath("$.content[0].collaboratorCount").value(2))
          .andExpect(jsonPath("$.content[0].createdAt").exists())
          .andExpect(jsonPath("$.content[0].updatedAt").exists());
    }
  }

  /**
   * Creates a test CollectionSummaryProjection with the given parameters.
   *
   * @param collectionId the collection ID
   * @param ownerId the owner user ID
   * @param name the collection name
   * @return a CollectionSummaryProjection for testing
   */
  private CollectionSummaryProjection createTestProjection(
      Long collectionId, UUID ownerId, String name) {
    return new CollectionSummaryProjection() {
      @Override
      public Long getCollectionId() {
        return collectionId;
      }

      @Override
      public String getName() {
        return name;
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
        return ownerId;
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
}
