package com.recipe_manager.component_tests.collection_service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
import com.recipe_manager.model.entity.collection.RecipeCollection;
import com.recipe_manager.model.enums.CollaborationMode;
import com.recipe_manager.model.enums.CollectionVisibility;
import com.recipe_manager.model.mapper.CollectionMapper;
import com.recipe_manager.model.mapper.RecipeCollectionItemMapper;
import com.recipe_manager.model.mapper.RecipeCollectionMapper;
import com.recipe_manager.repository.collection.CollectionCollaboratorRepository;
import com.recipe_manager.repository.collection.RecipeCollectionItemRepository;
import com.recipe_manager.repository.collection.RecipeCollectionRepository;
import com.recipe_manager.service.CollectionService;

/**
 * Component test for POST /collections endpoint.
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
class CreateCollectionTest {

  private MockMvc mockMvc;

  @Mock private RecipeCollectionRepository recipeCollectionRepository;

  @Mock private RecipeCollectionItemRepository recipeCollectionItemRepository;

  @Mock private CollectionCollaboratorRepository collectionCollaboratorRepository;

  @Autowired private CollectionMapper collectionMapper;

  @Autowired private RecipeCollectionMapper recipeCollectionMapper;

  @Autowired private RecipeCollectionItemMapper recipeCollectionItemMapper;

  private CollectionService collectionService;
  private CollectionController collectionController;

  private ObjectMapper objectMapper;

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

    objectMapper = new ObjectMapper();
  }

  @Test
  @DisplayName("Should create collection successfully with 201 Created status")
  @Tag("standard-processing")
  void shouldCreateCollectionSuccessfully() throws Exception {
    // Given
    String requestBody =
        """
        {
          "name": "My New Collection",
          "description": "Test Description",
          "visibility": "PUBLIC",
          "collaborationMode": "OWNER_ONLY"
        }
        """;

    RecipeCollection savedEntity =
        RecipeCollection.builder()
            .collectionId(1L)
            .userId(testUserId)
            .name("My New Collection")
            .description("Test Description")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .collectionItems(Collections.emptyList())
            .collaborators(Collections.emptyList())
            .build();

    when(recipeCollectionRepository.save(any(RecipeCollection.class))).thenReturn(savedEntity);

    // When & Then
    mockMvc
        .perform(
            post("/collections")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.collectionId").value(1))
        .andExpect(jsonPath("$.userId").value(testUserId.toString()))
        .andExpect(jsonPath("$.name").value("My New Collection"))
        .andExpect(jsonPath("$.description").value("Test Description"))
        .andExpect(jsonPath("$.visibility").value("PUBLIC"))
        .andExpect(jsonPath("$.collaborationMode").value("OWNER_ONLY"))
        .andExpect(jsonPath("$.recipeCount").value(0))
        .andExpect(jsonPath("$.collaboratorCount").value(0))
        .andExpect(jsonPath("$.createdAt").exists())
        .andExpect(jsonPath("$.updatedAt").exists());
  }

  @Test
  @DisplayName("Should create collection with null description")
  @Tag("standard-processing")
  void shouldCreateCollectionWithNullDescription() throws Exception {
    // Given
    String requestBody =
        """
        {
          "name": "Collection Without Description",
          "visibility": "PRIVATE",
          "collaborationMode": "ALL_USERS"
        }
        """;

    RecipeCollection savedEntity =
        RecipeCollection.builder()
            .collectionId(2L)
            .userId(testUserId)
            .name("Collection Without Description")
            .description(null)
            .visibility(CollectionVisibility.PRIVATE)
            .collaborationMode(CollaborationMode.ALL_USERS)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .collectionItems(Collections.emptyList())
            .collaborators(Collections.emptyList())
            .build();

    when(recipeCollectionRepository.save(any(RecipeCollection.class))).thenReturn(savedEntity);

    // When & Then
    mockMvc
        .perform(
            post("/collections")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Collection Without Description"))
        .andExpect(jsonPath("$.description").doesNotExist())
        .andExpect(jsonPath("$.visibility").value("PRIVATE"))
        .andExpect(jsonPath("$.collaborationMode").value("ALL_USERS"));
  }

  @Test
  @DisplayName("Should return 400 when name is missing")
  @Tag("standard-processing")
  void shouldReturn400WhenNameIsMissing() throws Exception {
    // Given
    String requestBody =
        """
        {
          "description": "Test Description",
          "visibility": "PUBLIC",
          "collaborationMode": "OWNER_ONLY"
        }
        """;

    // When & Then
    mockMvc
        .perform(
            post("/collections")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return 400 when visibility is missing")
  @Tag("standard-processing")
  void shouldReturn400WhenVisibilityIsMissing() throws Exception {
    // Given
    String requestBody =
        """
        {
          "name": "My Collection",
          "collaborationMode": "OWNER_ONLY"
        }
        """;

    // When & Then
    mockMvc
        .perform(
            post("/collections")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return 400 when collaboration mode is missing")
  @Tag("standard-processing")
  void shouldReturn400WhenCollaborationModeIsMissing() throws Exception {
    // Given
    String requestBody =
        """
        {
          "name": "My Collection",
          "visibility": "PUBLIC"
        }
        """;

    // When & Then
    mockMvc
        .perform(
            post("/collections")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return 400 when name exceeds max length")
  @Tag("standard-processing")
  void shouldReturn400WhenNameExceedsMaxLength() throws Exception {
    // Given
    String longName = "a".repeat(256); // Max length is 255
    String requestBody =
        String.format(
            """
            {
              "name": "%s",
              "visibility": "PUBLIC",
              "collaborationMode": "OWNER_ONLY"
            }
            """,
            longName);

    // When & Then
    mockMvc
        .perform(
            post("/collections")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should create private collection")
  @Tag("standard-processing")
  void shouldCreatePrivateCollection() throws Exception {
    // Given
    String requestBody =
        """
        {
          "name": "Private Collection",
          "visibility": "PRIVATE",
          "collaborationMode": "SPECIFIC_USERS"
        }
        """;

    RecipeCollection savedEntity =
        RecipeCollection.builder()
            .collectionId(3L)
            .userId(testUserId)
            .name("Private Collection")
            .visibility(CollectionVisibility.PRIVATE)
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .collectionItems(Collections.emptyList())
            .collaborators(Collections.emptyList())
            .build();

    when(recipeCollectionRepository.save(any(RecipeCollection.class))).thenReturn(savedEntity);

    // When & Then
    mockMvc
        .perform(
            post("/collections")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.visibility").value("PRIVATE"))
        .andExpect(jsonPath("$.collaborationMode").value("SPECIFIC_USERS"));
  }

  @Test
  @DisplayName("Should create friends-only collection")
  @Tag("standard-processing")
  void shouldCreateFriendsOnlyCollection() throws Exception {
    // Given
    String requestBody =
        """
        {
          "name": "Friends Only Collection",
          "visibility": "FRIENDS_ONLY",
          "collaborationMode": "ALL_USERS"
        }
        """;

    RecipeCollection savedEntity =
        RecipeCollection.builder()
            .collectionId(4L)
            .userId(testUserId)
            .name("Friends Only Collection")
            .visibility(CollectionVisibility.FRIENDS_ONLY)
            .collaborationMode(CollaborationMode.ALL_USERS)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .collectionItems(Collections.emptyList())
            .collaborators(Collections.emptyList())
            .build();

    when(recipeCollectionRepository.save(any(RecipeCollection.class))).thenReturn(savedEntity);

    // When & Then
    mockMvc
        .perform(
            post("/collections")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.visibility").value("FRIENDS_ONLY"))
        .andExpect(jsonPath("$.collaborationMode").value("ALL_USERS"));
  }

  @Test
  @DisplayName("Should verify user ID matches authenticated user")
  @Tag("standard-processing")
  void shouldVerifyUserIdMatchesAuthenticatedUser() throws Exception {
    // Given
    String requestBody =
        """
        {
          "name": "Test Collection",
          "visibility": "PUBLIC",
          "collaborationMode": "OWNER_ONLY"
        }
        """;

    RecipeCollection savedEntity =
        RecipeCollection.builder()
            .collectionId(5L)
            .userId(testUserId)
            .name("Test Collection")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .collectionItems(Collections.emptyList())
            .collaborators(Collections.emptyList())
            .build();

    when(recipeCollectionRepository.save(any(RecipeCollection.class))).thenReturn(savedEntity);

    // When & Then
    mockMvc
        .perform(
            post("/collections")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.userId").value(testUserId.toString()));
  }

  @Test
  @DisplayName("Should verify recipe count and collaborator count are zero for new collection")
  @Tag("standard-processing")
  void shouldVerifyCountsAreZeroForNewCollection() throws Exception {
    // Given
    String requestBody =
        """
        {
          "name": "New Collection",
          "visibility": "PUBLIC",
          "collaborationMode": "OWNER_ONLY"
        }
        """;

    RecipeCollection savedEntity =
        RecipeCollection.builder()
            .collectionId(6L)
            .userId(testUserId)
            .name("New Collection")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .collectionItems(Collections.emptyList())
            .collaborators(Collections.emptyList())
            .build();

    when(recipeCollectionRepository.save(any(RecipeCollection.class))).thenReturn(savedEntity);

    // When & Then
    mockMvc
        .perform(
            post("/collections")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.recipeCount").value(0))
        .andExpect(jsonPath("$.collaboratorCount").value(0));
  }
}
