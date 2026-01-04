package com.recipe_manager.component_tests.collection_service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipe_manager.config.RequestIdFilter;
import com.recipe_manager.controller.CollectionController;
import com.recipe_manager.exception.GlobalExceptionHandler;
import com.recipe_manager.model.dto.request.SearchCollectionsRequest;
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
 * Component test for POST /collections/search endpoint.
 *
 * <p>Tests the integration between CollectionController, CollectionService, and mappers with mocked
 * repository layer.
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
class SearchCollectionsTest {

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
  private ObjectMapper objectMapper;

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

    objectMapper = new ObjectMapper();

    mockMvc =
        MockMvcBuilders.standaloneSetup(collectionController)
            .addFilters(new RequestIdFilter())
            .setControllerAdvice(new GlobalExceptionHandler())
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();
  }

  @Test
  @DisplayName("Should search collections and return 200 OK with results")
  @Tag("standard-processing")
  void shouldSearchCollectionsAndReturn200() throws Exception {
    // Given
    SearchCollectionsRequest request =
        SearchCollectionsRequest.builder().query("pasta").build();

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(1L)
            .userId(testUserId)
            .name("Pasta Recipes")
            .description("Best pasta dishes")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .collectionItems(Collections.emptyList())
            .collaborators(Collections.emptyList())
            .build();

    Pageable pageable = PageRequest.of(0, 20);
    Page<RecipeCollection> collectionPage = new PageImpl<>(List.of(collection), pageable, 1);

    when(recipeCollectionRepository.searchCollections(
            eq("pasta"), nullOrEmpty(), nullOrEmpty(), isNull(), isNull(), isNull(), any(Pageable.class)))
        .thenReturn(collectionPage);

    // When & Then
    mockMvc
        .perform(
            post("/collections/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content[0].name").value("Pasta Recipes"))
        .andExpect(jsonPath("$.totalElements").value(1));
  }

  @Test
  @DisplayName("Should find collections by name search")
  @Tag("standard-processing")
  void shouldFindCollectionsByNameSearch() throws Exception {
    // Given
    SearchCollectionsRequest request =
        SearchCollectionsRequest.builder().query("italian").build();

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(2L)
            .userId(testUserId)
            .name("Italian Classics")
            .description("Traditional Italian food")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .collectionItems(Collections.emptyList())
            .collaborators(Collections.emptyList())
            .build();

    Pageable pageable = PageRequest.of(0, 20);
    Page<RecipeCollection> collectionPage = new PageImpl<>(List.of(collection), pageable, 1);

    when(recipeCollectionRepository.searchCollections(
            eq("italian"), nullOrEmpty(), nullOrEmpty(), isNull(), isNull(), isNull(), any(Pageable.class)))
        .thenReturn(collectionPage);

    // When & Then
    mockMvc
        .perform(
            post("/collections/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].name").value("Italian Classics"));
  }

  @Test
  @DisplayName("Should search with multiple filters")
  @Tag("standard-processing")
  void shouldSearchWithMultipleFilters() throws Exception {
    // Given
    SearchCollectionsRequest request =
        SearchCollectionsRequest.builder()
            .query("recipe")
            .visibility(List.of(CollectionVisibility.PUBLIC))
            .collaborationMode(List.of(CollaborationMode.ALL_USERS))
            .minRecipeCount(5)
            .maxRecipeCount(20)
            .build();

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(3L)
            .userId(testUserId)
            .name("My Recipe Book")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.ALL_USERS)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .collectionItems(Collections.emptyList())
            .collaborators(Collections.emptyList())
            .build();

    Pageable pageable = PageRequest.of(0, 20);
    Page<RecipeCollection> collectionPage = new PageImpl<>(List.of(collection), pageable, 1);

    when(recipeCollectionRepository.searchCollections(
            eq("recipe"),
            eq(new String[] {"PUBLIC"}),
            eq(new String[] {"ALL_USERS"}),
            isNull(),
            eq(5),
            eq(20),
            any(Pageable.class)))
        .thenReturn(collectionPage);

    // When & Then
    mockMvc
        .perform(
            post("/collections/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].name").value("My Recipe Book"))
        .andExpect(jsonPath("$.content[0].visibility").value("PUBLIC"))
        .andExpect(jsonPath("$.content[0].collaborationMode").value("ALL_USERS"));
  }

  @Test
  @DisplayName("Should return empty results when no collections match")
  @Tag("standard-processing")
  void shouldReturnEmptyResultsWhenNoMatch() throws Exception {
    // Given
    SearchCollectionsRequest request =
        SearchCollectionsRequest.builder().query("nonexistent").build();

    Pageable pageable = PageRequest.of(0, 20);
    Page<RecipeCollection> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

    when(recipeCollectionRepository.searchCollections(
            eq("nonexistent"),
            nullOrEmpty(),
            nullOrEmpty(),
            isNull(),
            isNull(),
            isNull(),
            any(Pageable.class)))
        .thenReturn(emptyPage);

    // When & Then
    mockMvc
        .perform(
            post("/collections/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isEmpty())
        .andExpect(jsonPath("$.totalElements").value(0));
  }

  @Test
  @DisplayName("Should search with empty criteria returns all collections")
  @Tag("standard-processing")
  void shouldSearchWithEmptyCriteriaReturnsAll() throws Exception {
    // Given
    SearchCollectionsRequest request = SearchCollectionsRequest.builder().build();

    List<RecipeCollection> collections =
        List.of(
            RecipeCollection.builder()
                .collectionId(1L)
                .userId(testUserId)
                .name("Collection 1")
                .visibility(CollectionVisibility.PUBLIC)
                .collaborationMode(CollaborationMode.OWNER_ONLY)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .collectionItems(Collections.emptyList())
                .collaborators(Collections.emptyList())
                .build(),
            RecipeCollection.builder()
                .collectionId(2L)
                .userId(testUserId)
                .name("Collection 2")
                .visibility(CollectionVisibility.PRIVATE)
                .collaborationMode(CollaborationMode.OWNER_ONLY)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .collectionItems(Collections.emptyList())
                .collaborators(Collections.emptyList())
                .build());

    Pageable pageable = PageRequest.of(0, 20);
    Page<RecipeCollection> collectionPage = new PageImpl<>(collections, pageable, 2);

    when(recipeCollectionRepository.searchCollections(
            isNull(), nullOrEmpty(), nullOrEmpty(), isNull(), isNull(), isNull(), any(Pageable.class)))
        .thenReturn(collectionPage);

    // When & Then
    mockMvc
        .perform(
            post("/collections/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content.length()").value(2))
        .andExpect(jsonPath("$.totalElements").value(2));
  }

  @Test
  @DisplayName("Should apply pagination and sorting correctly")
  @Tag("standard-processing")
  void shouldApplyPaginationAndSorting() throws Exception {
    // Given
    SearchCollectionsRequest request = SearchCollectionsRequest.builder().build();

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(1L)
            .userId(testUserId)
            .name("Collection 1")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .collectionItems(Collections.emptyList())
            .collaborators(Collections.emptyList())
            .build();

    Pageable pageable = PageRequest.of(0, 10);
    Page<RecipeCollection> collectionPage = new PageImpl<>(List.of(collection), pageable, 1);

    when(recipeCollectionRepository.searchCollections(
            isNull(), nullOrEmpty(), nullOrEmpty(), isNull(), isNull(), isNull(), any(Pageable.class)))
        .thenReturn(collectionPage);

    // When & Then
    mockMvc
        .perform(
            post("/collections/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .param("page", "0")
                .param("size", "10")
                .param("sort", "name,asc"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.number").value(0))
        .andExpect(jsonPath("$.size").value(10));
  }
}
