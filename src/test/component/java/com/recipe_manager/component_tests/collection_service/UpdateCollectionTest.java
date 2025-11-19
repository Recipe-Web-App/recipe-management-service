package com.recipe_manager.component_tests.collection_service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Collections;
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
import com.recipe_manager.repository.recipe.RecipeRepository;
import com.recipe_manager.service.CollectionService;
import com.recipe_manager.service.external.notificationservice.NotificationService;

/**
 * Component test for PUT /collections/{collectionId} endpoint.
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
class UpdateCollectionTest {

  private MockMvc mockMvc;

  @Mock private RecipeCollectionRepository recipeCollectionRepository;

  @Mock private RecipeCollectionItemRepository recipeCollectionItemRepository;

  @Mock private CollectionCollaboratorRepository collectionCollaboratorRepository;

  @Mock private RecipeRepository recipeRepository;

  @Mock private NotificationService notificationService;

  @Autowired private CollectionMapper collectionMapper;

  @Autowired private RecipeCollectionMapper recipeCollectionMapper;

  @Autowired private RecipeCollectionItemMapper recipeCollectionItemMapper;

  private CollectionService collectionService;
  private CollectionController collectionController;

  private ObjectMapper objectMapper;

  private UUID testUserId;
  private UUID otherUserId;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    testUserId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    otherUserId = UUID.fromString("987e6543-e21b-12d3-a456-426614174999");

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
            notificationService);
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
  @DisplayName("Should update collection successfully with all fields")
  @Tag("standard-processing")
  void shouldUpdateCollectionSuccessfullyWithAllFields() throws Exception {
    // Given
    Long collectionId = 1L;
    String requestBody =
        """
        {
          "name": "Updated Collection Name",
          "description": "Updated description",
          "visibility": "PRIVATE",
          "collaborationMode": "ALL_USERS"
        }
        """;

    RecipeCollection existingCollection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(testUserId)
            .name("Original Name")
            .description("Original description")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .createdAt(LocalDateTime.now().minusDays(1))
            .updatedAt(LocalDateTime.now().minusDays(1))
            .collectionItems(Collections.emptyList())
            .collaborators(Collections.emptyList())
            .build();

    RecipeCollection updatedCollection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(testUserId)
            .name("Updated Collection Name")
            .description("Updated description")
            .visibility(CollectionVisibility.PRIVATE)
            .collaborationMode(CollaborationMode.ALL_USERS)
            .createdAt(existingCollection.getCreatedAt())
            .updatedAt(LocalDateTime.now())
            .collectionItems(Collections.emptyList())
            .collaborators(Collections.emptyList())
            .build();

    when(recipeCollectionRepository.findById(collectionId))
        .thenReturn(Optional.of(existingCollection));
    when(recipeCollectionRepository.save(any(RecipeCollection.class))).thenReturn(updatedCollection);

    // When & Then
    mockMvc
        .perform(
            put("/collections/{collectionId}", collectionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.collectionId").value(collectionId))
        .andExpect(jsonPath("$.userId").value(testUserId.toString()))
        .andExpect(jsonPath("$.name").value("Updated Collection Name"))
        .andExpect(jsonPath("$.description").value("Updated description"))
        .andExpect(jsonPath("$.visibility").value("PRIVATE"))
        .andExpect(jsonPath("$.collaborationMode").value("ALL_USERS"));
  }

  @Test
  @DisplayName("Should update only name when only name is provided")
  @Tag("standard-processing")
  void shouldUpdateOnlyNameWhenOnlyNameProvided() throws Exception {
    // Given
    Long collectionId = 2L;
    String requestBody =
        """
        {
          "name": "New Name Only"
        }
        """;

    RecipeCollection existingCollection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(testUserId)
            .name("Old Name")
            .description("Keep this description")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .createdAt(LocalDateTime.now().minusDays(1))
            .updatedAt(LocalDateTime.now().minusDays(1))
            .collectionItems(Collections.emptyList())
            .collaborators(Collections.emptyList())
            .build();

    RecipeCollection updatedCollection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(testUserId)
            .name("New Name Only")
            .description("Keep this description")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .createdAt(existingCollection.getCreatedAt())
            .updatedAt(LocalDateTime.now())
            .collectionItems(Collections.emptyList())
            .collaborators(Collections.emptyList())
            .build();

    when(recipeCollectionRepository.findById(collectionId))
        .thenReturn(Optional.of(existingCollection));
    when(recipeCollectionRepository.save(any(RecipeCollection.class))).thenReturn(updatedCollection);

    // When & Then
    mockMvc
        .perform(
            put("/collections/{collectionId}", collectionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("New Name Only"))
        .andExpect(jsonPath("$.description").value("Keep this description"))
        .andExpect(jsonPath("$.visibility").value("PUBLIC"))
        .andExpect(jsonPath("$.collaborationMode").value("OWNER_ONLY"));
  }

  @Test
  @DisplayName("Should update only description when only description is provided")
  @Tag("standard-processing")
  void shouldUpdateOnlyDescriptionWhenOnlyDescriptionProvided() throws Exception {
    // Given
    Long collectionId = 3L;
    String requestBody =
        """
        {
          "description": "New description only"
        }
        """;

    RecipeCollection existingCollection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(testUserId)
            .name("Keep this name")
            .description("Old description")
            .visibility(CollectionVisibility.PRIVATE)
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .createdAt(LocalDateTime.now().minusDays(1))
            .updatedAt(LocalDateTime.now().minusDays(1))
            .collectionItems(Collections.emptyList())
            .collaborators(Collections.emptyList())
            .build();

    RecipeCollection updatedCollection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(testUserId)
            .name("Keep this name")
            .description("New description only")
            .visibility(CollectionVisibility.PRIVATE)
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .createdAt(existingCollection.getCreatedAt())
            .updatedAt(LocalDateTime.now())
            .collectionItems(Collections.emptyList())
            .collaborators(Collections.emptyList())
            .build();

    when(recipeCollectionRepository.findById(collectionId))
        .thenReturn(Optional.of(existingCollection));
    when(recipeCollectionRepository.save(any(RecipeCollection.class))).thenReturn(updatedCollection);

    // When & Then
    mockMvc
        .perform(
            put("/collections/{collectionId}", collectionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Keep this name"))
        .andExpect(jsonPath("$.description").value("New description only"))
        .andExpect(jsonPath("$.visibility").value("PRIVATE"))
        .andExpect(jsonPath("$.collaborationMode").value("SPECIFIC_USERS"));
  }

  @Test
  @DisplayName("Should update only visibility when only visibility is provided")
  @Tag("standard-processing")
  void shouldUpdateOnlyVisibilityWhenOnlyVisibilityProvided() throws Exception {
    // Given
    Long collectionId = 4L;
    String requestBody =
        """
        {
          "visibility": "FRIENDS_ONLY"
        }
        """;

    RecipeCollection existingCollection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(testUserId)
            .name("Keep name")
            .description("Keep description")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .createdAt(LocalDateTime.now().minusDays(1))
            .updatedAt(LocalDateTime.now().minusDays(1))
            .collectionItems(Collections.emptyList())
            .collaborators(Collections.emptyList())
            .build();

    RecipeCollection updatedCollection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(testUserId)
            .name("Keep name")
            .description("Keep description")
            .visibility(CollectionVisibility.FRIENDS_ONLY)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .createdAt(existingCollection.getCreatedAt())
            .updatedAt(LocalDateTime.now())
            .collectionItems(Collections.emptyList())
            .collaborators(Collections.emptyList())
            .build();

    when(recipeCollectionRepository.findById(collectionId))
        .thenReturn(Optional.of(existingCollection));
    when(recipeCollectionRepository.save(any(RecipeCollection.class))).thenReturn(updatedCollection);

    // When & Then
    mockMvc
        .perform(
            put("/collections/{collectionId}", collectionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.visibility").value("FRIENDS_ONLY"));
  }

  @Test
  @DisplayName("Should update only collaboration mode when only collaboration mode is provided")
  @Tag("standard-processing")
  void shouldUpdateOnlyCollaborationModeWhenOnlyCollaborationModeProvided() throws Exception {
    // Given
    Long collectionId = 5L;
    String requestBody =
        """
        {
          "collaborationMode": "ALL_USERS"
        }
        """;

    RecipeCollection existingCollection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(testUserId)
            .name("Keep name")
            .description("Keep description")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .createdAt(LocalDateTime.now().minusDays(1))
            .updatedAt(LocalDateTime.now().minusDays(1))
            .collectionItems(Collections.emptyList())
            .collaborators(Collections.emptyList())
            .build();

    RecipeCollection updatedCollection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(testUserId)
            .name("Keep name")
            .description("Keep description")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.ALL_USERS)
            .createdAt(existingCollection.getCreatedAt())
            .updatedAt(LocalDateTime.now())
            .collectionItems(Collections.emptyList())
            .collaborators(Collections.emptyList())
            .build();

    when(recipeCollectionRepository.findById(collectionId))
        .thenReturn(Optional.of(existingCollection));
    when(recipeCollectionRepository.save(any(RecipeCollection.class))).thenReturn(updatedCollection);

    // When & Then
    mockMvc
        .perform(
            put("/collections/{collectionId}", collectionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.collaborationMode").value("ALL_USERS"));
  }

  @Test
  @DisplayName("Should return 403 Forbidden when user is not the collection owner")
  @Tag("standard-processing")
  void shouldReturn403WhenUserIsNotOwner() throws Exception {
    // Given
    Long collectionId = 6L;
    String requestBody =
        """
        {
          "name": "Trying to update"
        }
        """;

    RecipeCollection collectionOwnedByOther =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(otherUserId) // Different user
            .name("Other user's collection")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .collectionItems(Collections.emptyList())
            .collaborators(Collections.emptyList())
            .build();

    when(recipeCollectionRepository.findById(collectionId))
        .thenReturn(Optional.of(collectionOwnedByOther));

    // When & Then
    mockMvc
        .perform(
            put("/collections/{collectionId}", collectionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("Should return 404 Not Found when collection does not exist")
  @Tag("standard-processing")
  void shouldReturn404WhenCollectionNotFound() throws Exception {
    // Given
    Long nonExistentId = 999L;
    String requestBody =
        """
        {
          "name": "Trying to update non-existent"
        }
        """;

    when(recipeCollectionRepository.findById(nonExistentId)).thenReturn(Optional.empty());

    // When & Then
    mockMvc
        .perform(
            put("/collections/{collectionId}", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should return 400 Bad Request when name is empty")
  @Tag("standard-processing")
  void shouldReturn400WhenNameIsEmpty() throws Exception {
    // Given
    Long collectionId = 7L;
    String requestBody =
        """
        {
          "name": ""
        }
        """;

    // When & Then
    mockMvc
        .perform(
            put("/collections/{collectionId}", collectionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return 400 Bad Request when name exceeds max length")
  @Tag("standard-processing")
  void shouldReturn400WhenNameExceedsMaxLength() throws Exception {
    // Given
    Long collectionId = 8L;
    String longName = "a".repeat(256); // Max is 255
    String requestBody = String.format("{\"name\": \"%s\"}", longName);

    // When & Then
    mockMvc
        .perform(
            put("/collections/{collectionId}", collectionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return 400 Bad Request when description exceeds max length")
  @Tag("standard-processing")
  void shouldReturn400WhenDescriptionExceedsMaxLength() throws Exception {
    // Given
    Long collectionId = 9L;
    String longDescription = "a".repeat(2001); // Max is 2000
    String requestBody = String.format("{\"description\": \"%s\"}", longDescription);

    // When & Then
    mockMvc
        .perform(
            put("/collections/{collectionId}", collectionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should allow updating with empty description to clear it")
  @Tag("standard-processing")
  void shouldAllowEmptyDescriptionToClearIt() throws Exception {
    // Given
    Long collectionId = 10L;
    String requestBody =
        """
        {
          "description": ""
        }
        """;

    RecipeCollection existingCollection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(testUserId)
            .name("Collection Name")
            .description("Old description")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .createdAt(LocalDateTime.now().minusDays(1))
            .updatedAt(LocalDateTime.now().minusDays(1))
            .collectionItems(Collections.emptyList())
            .collaborators(Collections.emptyList())
            .build();

    RecipeCollection updatedCollection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(testUserId)
            .name("Collection Name")
            .description("")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .createdAt(existingCollection.getCreatedAt())
            .updatedAt(LocalDateTime.now())
            .collectionItems(Collections.emptyList())
            .collaborators(Collections.emptyList())
            .build();

    when(recipeCollectionRepository.findById(collectionId))
        .thenReturn(Optional.of(existingCollection));
    when(recipeCollectionRepository.save(any(RecipeCollection.class))).thenReturn(updatedCollection);

    // When & Then
    mockMvc
        .perform(
            put("/collections/{collectionId}", collectionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.description").value(""));
  }
}
