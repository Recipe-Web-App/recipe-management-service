package com.recipe_manager.component_tests.collection_service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
import com.recipe_manager.model.entity.collection.RecipeCollectionItem;
import com.recipe_manager.model.entity.collection.RecipeCollectionItemId;
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.enums.CollaborationMode;
import com.recipe_manager.model.enums.CollectionVisibility;
import com.recipe_manager.model.mapper.CollectionMapper;
import com.recipe_manager.model.mapper.RecipeCollectionItemMapper;
import com.recipe_manager.model.mapper.RecipeCollectionMapper;
import com.recipe_manager.repository.collection.CollectionCollaboratorRepository;
import com.recipe_manager.repository.collection.RecipeCollectionItemRepository;
import com.recipe_manager.repository.collection.RecipeCollectionRepository;
import com.recipe_manager.repository.recipe.RecipeRepository;
import com.recipe_manager.service.CollectionService;
import com.recipe_manager.service.CollectionTagService;
import com.recipe_manager.service.external.notificationservice.NotificationService;

import jakarta.persistence.EntityManager;

/**
 * Component test for GET /collections/{collectionId} endpoint.
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
class GetCollectionByIdTest {

  private MockMvc mockMvc;

  @Mock private RecipeCollectionRepository recipeCollectionRepository;

  @Mock private RecipeCollectionItemRepository recipeCollectionItemRepository;

  @Mock private CollectionCollaboratorRepository collectionCollaboratorRepository;

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
  private UUID otherUserId;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    testUserId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    otherUserId = UUID.fromString("987e6543-e21b-12d3-a456-426614174000");

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
            .build();
  }

  @Test
  @DisplayName("Should get collection by ID successfully when user is owner")
  @Tag("standard-processing")
  void shouldGetCollectionByIdWhenUserIsOwner() throws Exception {
    // Given
    Long collectionId = 1L;
    RecipeCollection collection = createTestCollectionWithRecipes(collectionId, testUserId);

    when(recipeCollectionRepository.hasViewAccess(collectionId, testUserId)).thenReturn(true);
    when(recipeCollectionRepository.findByIdWithItems(collectionId))
        .thenReturn(Optional.of(collection));

    // When & Then
    mockMvc
        .perform(get("/collections/{collectionId}", collectionId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.collectionId").value(collectionId))
        .andExpect(jsonPath("$.userId").value(testUserId.toString()))
        .andExpect(jsonPath("$.name").value("Test Collection"))
        .andExpect(jsonPath("$.description").value("Test Description"))
        .andExpect(jsonPath("$.visibility").value("PUBLIC"))
        .andExpect(jsonPath("$.collaborationMode").value("OWNER_ONLY"))
        .andExpect(jsonPath("$.recipes").isArray())
        .andExpect(jsonPath("$.collaborators").isArray())
        .andExpect(jsonPath("$.recipes.length()").value(1))
        .andExpect(jsonPath("$.recipes[0].recipeId").value(1))
        .andExpect(jsonPath("$.recipes[0].recipeTitle").value("Test Recipe"))
        .andExpect(jsonPath("$.recipes[0].recipeDescription").value("Test Description"))
        .andExpect(jsonPath("$.recipes[0].displayOrder").value(10))
        .andExpect(jsonPath("$.createdAt").exists())
        .andExpect(jsonPath("$.updatedAt").exists());
  }

  @Test
  @DisplayName("Should get collection by ID when user has view access")
  @Tag("standard-processing")
  void shouldGetCollectionByIdWhenUserHasViewAccess() throws Exception {
    // Given
    Long collectionId = 2L;
    RecipeCollection collection =
        createTestCollectionWithRecipes(collectionId, otherUserId); // Owned by different user

    when(recipeCollectionRepository.hasViewAccess(collectionId, testUserId)).thenReturn(true);
    when(recipeCollectionRepository.findByIdWithItems(collectionId))
        .thenReturn(Optional.of(collection));

    // When & Then
    mockMvc
        .perform(get("/collections/{collectionId}", collectionId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.collectionId").value(collectionId))
        .andExpect(jsonPath("$.userId").value(otherUserId.toString()))
        .andExpect(jsonPath("$.name").value("Test Collection"));
  }

  @Test
  @DisplayName("Should return 404 when user has no view access")
  @Tag("error-handling")
  void shouldReturn404WhenUserHasNoViewAccess() throws Exception {
    // Given
    Long collectionId = 3L;

    when(recipeCollectionRepository.hasViewAccess(collectionId, testUserId)).thenReturn(false);

    // When & Then
    mockMvc
        .perform(get("/collections/{collectionId}", collectionId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("Resource not found"))
        .andExpect(jsonPath("$.message").value("Collection not found or access denied"));
  }

  @Test
  @DisplayName("Should return 404 when collection does not exist")
  @Tag("error-handling")
  void shouldReturn404WhenCollectionDoesNotExist() throws Exception {
    // Given
    Long collectionId = 999L;

    when(recipeCollectionRepository.hasViewAccess(collectionId, testUserId)).thenReturn(true);
    when(recipeCollectionRepository.findByIdWithItems(collectionId))
        .thenReturn(Optional.empty());

    // When & Then
    mockMvc
        .perform(get("/collections/{collectionId}", collectionId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("Resource not found"))
        .andExpect(jsonPath("$.message").value("Collection not found"));
  }

  @Test
  @DisplayName("Should handle collection with empty recipes list")
  @Tag("standard-processing")
  void shouldHandleCollectionWithEmptyRecipesList() throws Exception {
    // Given
    Long collectionId = 4L;
    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(testUserId)
            .name("Empty Collection")
            .description("No recipes")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .collectionItems(new ArrayList<>())
            .collaborators(new ArrayList<>())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    when(recipeCollectionRepository.hasViewAccess(collectionId, testUserId)).thenReturn(true);
    when(recipeCollectionRepository.findByIdWithItems(collectionId))
        .thenReturn(Optional.of(collection));

    // When & Then
    mockMvc
        .perform(get("/collections/{collectionId}", collectionId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.collectionId").value(collectionId))
        .andExpect(jsonPath("$.name").value("Empty Collection"))
        .andExpect(jsonPath("$.recipes").isArray())
        .andExpect(jsonPath("$.recipes.length()").value(0))
        .andExpect(jsonPath("$.collaborators").isArray())
        .andExpect(jsonPath("$.collaborators.length()").value(0));
  }

  @Test
  @DisplayName("Should handle collection with multiple recipes")
  @Tag("standard-processing")
  void shouldHandleCollectionWithMultipleRecipes() throws Exception {
    // Given
    Long collectionId = 5L;
    RecipeCollection collection = createTestCollectionWithMultipleRecipes(collectionId, testUserId);

    when(recipeCollectionRepository.hasViewAccess(collectionId, testUserId)).thenReturn(true);
    when(recipeCollectionRepository.findByIdWithItems(collectionId))
        .thenReturn(Optional.of(collection));

    // When & Then
    mockMvc
        .perform(get("/collections/{collectionId}", collectionId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.collectionId").value(collectionId))
        .andExpect(jsonPath("$.recipes").isArray())
        .andExpect(jsonPath("$.recipes.length()").value(2))
        .andExpect(jsonPath("$.recipes[0].recipeId").value(1))
        .andExpect(jsonPath("$.recipes[0].recipeTitle").value("First Recipe"))
        .andExpect(jsonPath("$.recipes[0].displayOrder").value(10))
        .andExpect(jsonPath("$.recipes[1].recipeId").value(2))
        .andExpect(jsonPath("$.recipes[1].recipeTitle").value("Second Recipe"))
        .andExpect(jsonPath("$.recipes[1].displayOrder").value(20));
  }

  @Test
  @DisplayName("Should map all enum values correctly")
  @Tag("standard-processing")
  void shouldMapAllEnumValuesCorrectly() throws Exception {
    // Given
    Long collectionId = 6L;
    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(testUserId)
            .name("Private Collection")
            .visibility(CollectionVisibility.PRIVATE)
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .collectionItems(new ArrayList<>())
            .collaborators(new ArrayList<>())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    when(recipeCollectionRepository.hasViewAccess(collectionId, testUserId)).thenReturn(true);
    when(recipeCollectionRepository.findByIdWithItems(collectionId))
        .thenReturn(Optional.of(collection));

    // When & Then
    mockMvc
        .perform(get("/collections/{collectionId}", collectionId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.visibility").value("PRIVATE"))
        .andExpect(jsonPath("$.collaborationMode").value("SPECIFIC_USERS"));
  }

  @Test
  @DisplayName("Should handle null description")
  @Tag("standard-processing")
  void shouldHandleNullDescription() throws Exception {
    // Given
    Long collectionId = 7L;
    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(testUserId)
            .name("Collection Without Description")
            .description(null)
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .collectionItems(new ArrayList<>())
            .collaborators(new ArrayList<>())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    when(recipeCollectionRepository.hasViewAccess(collectionId, testUserId)).thenReturn(true);
    when(recipeCollectionRepository.findByIdWithItems(collectionId))
        .thenReturn(Optional.of(collection));

    // When & Then
    mockMvc
        .perform(get("/collections/{collectionId}", collectionId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.collectionId").value(collectionId))
        .andExpect(jsonPath("$.name").value("Collection Without Description"))
        .andExpect(jsonPath("$.description").isEmpty());
  }

  @Test
  @DisplayName("Should preserve all DTO fields through mapper")
  @Tag("standard-processing")
  void shouldPreserveAllDtoFields() throws Exception {
    // Given
    Long collectionId = 8L;
    RecipeCollection collection = createTestCollectionWithRecipes(collectionId, testUserId);

    when(recipeCollectionRepository.hasViewAccess(collectionId, testUserId)).thenReturn(true);
    when(recipeCollectionRepository.findByIdWithItems(collectionId))
        .thenReturn(Optional.of(collection));

    // When & Then
    mockMvc
        .perform(get("/collections/{collectionId}", collectionId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.collectionId").exists())
        .andExpect(jsonPath("$.userId").exists())
        .andExpect(jsonPath("$.name").exists())
        .andExpect(jsonPath("$.description").exists())
        .andExpect(jsonPath("$.visibility").exists())
        .andExpect(jsonPath("$.collaborationMode").exists())
        .andExpect(jsonPath("$.recipes").exists())
        .andExpect(jsonPath("$.collaborators").exists())
        .andExpect(jsonPath("$.createdAt").exists())
        .andExpect(jsonPath("$.updatedAt").exists());
  }

  private RecipeCollection createTestCollectionWithRecipes(Long collectionId, UUID ownerId) {
    Recipe recipe =
        Recipe.builder().recipeId(1L).title("Test Recipe").description("Test Description").build();

    RecipeCollectionItemId itemId = new RecipeCollectionItemId(collectionId, 1L);

    RecipeCollectionItem item =
        RecipeCollectionItem.builder()
            .id(itemId)
            .recipe(recipe)
            .displayOrder(10)
            .addedBy(ownerId)
            .addedAt(LocalDateTime.now())
            .build();

    return RecipeCollection.builder()
        .collectionId(collectionId)
        .userId(ownerId)
        .name("Test Collection")
        .description("Test Description")
        .visibility(CollectionVisibility.PUBLIC)
        .collaborationMode(CollaborationMode.OWNER_ONLY)
        .collectionItems(Arrays.asList(item))
        .collaborators(new ArrayList<>())
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
  }

  private RecipeCollection createTestCollectionWithMultipleRecipes(
      Long collectionId, UUID ownerId) {
    Recipe recipe1 =
        Recipe.builder()
            .recipeId(1L)
            .title("First Recipe")
            .description("First Description")
            .build();

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
            .addedBy(ownerId)
            .addedAt(LocalDateTime.now())
            .build();

    RecipeCollectionItem item2 =
        RecipeCollectionItem.builder()
            .id(itemId2)
            .recipe(recipe2)
            .displayOrder(20)
            .addedBy(ownerId)
            .addedAt(LocalDateTime.now())
            .build();

    return RecipeCollection.builder()
        .collectionId(collectionId)
        .userId(ownerId)
        .name("Multi-Recipe Collection")
        .visibility(CollectionVisibility.PUBLIC)
        .collaborationMode(CollaborationMode.OWNER_ONLY)
        .collectionItems(Arrays.asList(item1, item2))
        .collaborators(new ArrayList<>())
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
  }
}
