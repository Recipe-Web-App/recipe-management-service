package com.recipe_manager.component_tests.collection_service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import com.recipe_manager.model.entity.collection.RecipeCollection;
import com.recipe_manager.model.enums.CollaborationMode;
import com.recipe_manager.model.enums.CollectionVisibility;
import com.recipe_manager.model.mapper.CollectionMapper;
import com.recipe_manager.model.mapper.RecipeCollectionItemMapper;
import com.recipe_manager.model.mapper.RecipeCollectionMapper;
import com.recipe_manager.repository.collection.CollectionCollaboratorRepository;
import com.recipe_manager.repository.collection.CollectionTagRepository;
import com.recipe_manager.repository.collection.RecipeCollectionItemRepository;
import com.recipe_manager.repository.collection.RecipeCollectionRepository;
import com.recipe_manager.repository.recipe.RecipeRepository;
import com.recipe_manager.service.CollectionService;
import com.recipe_manager.service.CollectionTagService;
import com.recipe_manager.service.external.notificationservice.NotificationService;

import jakarta.persistence.EntityManager;

/**
 * Component test for GET /collections/trending endpoint.
 *
 * <p>Tests the integration between CollectionController, CollectionService, and CollectionMapper
 * for the trending collections feature with mocked repository layer.
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
class GetTrendingCollectionsTest {

  private MockMvc mockMvc;

  @Mock private RecipeCollectionRepository recipeCollectionRepository;

  @Mock private RecipeCollectionItemRepository recipeCollectionItemRepository;

  @Mock private CollectionCollaboratorRepository collectionCollaboratorRepository;

  @Mock private CollectionTagRepository collectionTagRepository;

  @Mock private RecipeRepository recipeRepository;

  @Mock private NotificationService notificationService;

  @Mock private EntityManager entityManager;

  @Mock private CollectionTagService collectionTagService;

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
        new UsernamePasswordAuthenticationToken(
            testUserId.toString(), null, Collections.emptyList());
    securityContext.setAuthentication(authentication);
    SecurityContextHolder.setContext(securityContext);

    // Create real service with mocked repository
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
    collectionController = new CollectionController(collectionService, collectionTagService);

    mockMvc =
        MockMvcBuilders.standaloneSetup(collectionController)
            .addFilters(new RequestIdFilter())
            .setControllerAdvice(new GlobalExceptionHandler())
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();
  }

  @Test
  @DisplayName("Should get trending collections successfully with real mapper")
  @Tag("standard-processing")
  void shouldGetTrendingCollectionsSuccessfully() throws Exception {
    // Given
    Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 20);
    List<RecipeCollection> collections =
        Arrays.asList(createTestCollection(1L), createTestCollection(2L));
    Page<RecipeCollection> collectionPage = new PageImpl<>(collections, pageable, 2);

    when(recipeCollectionRepository.findTrendingCollections(any(UUID.class), any(Pageable.class)))
        .thenReturn(collectionPage);

    // When & Then
    mockMvc
        .perform(get("/collections/trending").param("page", "0").param("size", "20"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content.length()").value(2))
        .andExpect(jsonPath("$.content[0].collectionId").value(1))
        .andExpect(jsonPath("$.content[0].userId").value(testUserId.toString()))
        .andExpect(jsonPath("$.content[0].name").value("Trending Collection 1"))
        .andExpect(jsonPath("$.content[0].visibility").value("PUBLIC"))
        .andExpect(jsonPath("$.content[0].collaborationMode").value("OWNER_ONLY"))
        .andExpect(jsonPath("$.content[1].collectionId").value(2))
        .andExpect(jsonPath("$.totalElements").value(2))
        .andExpect(jsonPath("$.totalPages").value(1))
        .andExpect(jsonPath("$.number").value(0))
        .andExpect(jsonPath("$.size").value(20));
  }

  @Test
  @DisplayName("Should return empty page when no trending collections")
  @Tag("standard-processing")
  void shouldReturnEmptyPageWhenNoTrendingCollections() throws Exception {
    // Given
    Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 20);
    Page<RecipeCollection> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

    when(recipeCollectionRepository.findTrendingCollections(any(UUID.class), any(Pageable.class)))
        .thenReturn(emptyPage);

    // When & Then
    mockMvc
        .perform(get("/collections/trending"))
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
    List<RecipeCollection> collections =
        Arrays.asList(
            createTestCollection(1L),
            createTestCollection(2L),
            createTestCollection(3L),
            createTestCollection(4L),
            createTestCollection(5L));
    Page<RecipeCollection> collectionPage = new PageImpl<>(collections, Pageable.ofSize(5), 10);

    when(recipeCollectionRepository.findTrendingCollections(any(UUID.class), any(Pageable.class)))
        .thenReturn(collectionPage);

    // When & Then
    mockMvc
        .perform(get("/collections/trending").param("page", "0").param("size", "5"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(5))
        .andExpect(jsonPath("$.size").value(5))
        .andExpect(jsonPath("$.totalElements").value(10))
        .andExpect(jsonPath("$.totalPages").value(2));
  }

  @Test
  @DisplayName("Should handle second page request for trending collections")
  @Tag("standard-processing")
  void shouldHandleSecondPageRequest() throws Exception {
    // Given
    List<RecipeCollection> collections =
        Arrays.asList(createTestCollection(11L), createTestCollection(12L));
    Page<RecipeCollection> collectionPage =
        new PageImpl<>(collections, Pageable.ofSize(10).withPage(1), 25);

    when(recipeCollectionRepository.findTrendingCollections(any(UUID.class), any(Pageable.class)))
        .thenReturn(collectionPage);

    // When & Then
    mockMvc
        .perform(get("/collections/trending").param("page", "1").param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(2))
        .andExpect(jsonPath("$.number").value(1))
        .andExpect(jsonPath("$.first").value(false))
        .andExpect(jsonPath("$.totalPages").value(3));
  }

  @Test
  @DisplayName("Should map all collection fields correctly through real mapper")
  @Tag("standard-processing")
  void shouldMapAllCollectionFieldsCorrectly() throws Exception {
    // Given
    Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 20);
    RecipeCollection collection = createTestCollection(123L);
    Page<RecipeCollection> collectionPage = new PageImpl<>(List.of(collection), pageable, 1);

    when(recipeCollectionRepository.findTrendingCollections(any(UUID.class), any(Pageable.class)))
        .thenReturn(collectionPage);

    // When & Then
    mockMvc
        .perform(get("/collections/trending"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].collectionId").value(123))
        .andExpect(jsonPath("$.content[0].userId").exists())
        .andExpect(jsonPath("$.content[0].name").exists())
        .andExpect(jsonPath("$.content[0].description").exists())
        .andExpect(jsonPath("$.content[0].visibility").exists())
        .andExpect(jsonPath("$.content[0].collaborationMode").exists())
        .andExpect(jsonPath("$.content[0].recipeCount").value(0))
        .andExpect(jsonPath("$.content[0].collaboratorCount").value(0))
        .andExpect(jsonPath("$.content[0].createdAt").exists())
        .andExpect(jsonPath("$.content[0].updatedAt").exists());
  }

  @Test
  @DisplayName("Should use default page size when not specified")
  @Tag("standard-processing")
  void shouldUseDefaultPageSizeWhenNotSpecified() throws Exception {
    // Given
    Page<RecipeCollection> emptyPage =
        new PageImpl<>(Collections.emptyList(), Pageable.ofSize(20), 0);

    when(recipeCollectionRepository.findTrendingCollections(any(UUID.class), any(Pageable.class)))
        .thenReturn(emptyPage);

    // When & Then
    mockMvc
        .perform(get("/collections/trending"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size").value(20));
  }

  private RecipeCollection createTestCollection(Long collectionId) {
    return RecipeCollection.builder()
        .collectionId(collectionId)
        .userId(testUserId)
        .name("Trending Collection " + collectionId)
        .description("Description for trending collection " + collectionId)
        .visibility(CollectionVisibility.PUBLIC)
        .collaborationMode(CollaborationMode.OWNER_ONLY)
        .createdAt(LocalDateTime.now().minusDays(1))
        .updatedAt(LocalDateTime.now())
        .build();
  }
}
