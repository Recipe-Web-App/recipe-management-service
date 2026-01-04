package com.recipe_manager.component_tests.collection_service;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
 * Component test for DELETE /collections/{collectionId}/collaborators/{userId} endpoint.
 *
 * <p>Tests the integration between CollectionController and CollectionService with mocked
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
class RemoveCollaboratorTest {

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
  private UUID otherUserId;
  private UUID collaboratorUserId;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    testUserId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    otherUserId = UUID.fromString("987e6543-e21b-12d3-a456-426614174000");
    collaboratorUserId = UUID.fromString("111e1111-e11b-11d3-a111-111111111111");

    // Set up security context with test user ID
    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(
            testUserId.toString(), null, Collections.emptyList());
    securityContext.setAuthentication(authentication);
    SecurityContextHolder.setContext(securityContext);

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
    collectionController = new CollectionController(collectionService, collectionTagService);

    mockMvc =
        MockMvcBuilders.standaloneSetup(collectionController)
            .addFilters(new RequestIdFilter())
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
  }

  @Test
  @DisplayName("Should remove collaborator successfully when user is owner")
  @Tag("standard-processing")
  void shouldRemoveCollaboratorWhenUserIsOwner() throws Exception {
    // Given
    Long collectionId = 1L;
    RecipeCollection collection = createTestCollection(collectionId, testUserId);

    when(recipeCollectionRepository.findById(collectionId))
        .thenReturn(Optional.of(collection));
    when(collectionCollaboratorRepository.existsByIdCollectionIdAndIdUserId(
            collectionId, collaboratorUserId))
        .thenReturn(true);

    // When & Then
    mockMvc
        .perform(
            delete("/collections/{collectionId}/collaborators/{userId}", collectionId, collaboratorUserId))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("Should return 404 when collection does not exist")
  @Tag("error-handling")
  void shouldReturn404WhenCollectionDoesNotExist() throws Exception {
    // Given
    Long collectionId = 999L;

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.empty());

    // When & Then
    mockMvc
        .perform(
            delete("/collections/{collectionId}/collaborators/{userId}", collectionId, collaboratorUserId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("Resource not found"))
        .andExpect(jsonPath("$.message").value("Collection not found"));
  }

  @Test
  @DisplayName("Should return 403 when user is not the owner")
  @Tag("error-handling")
  void shouldReturn403WhenUserIsNotOwner() throws Exception {
    // Given
    Long collectionId = 2L;
    RecipeCollection collection =
        createTestCollection(collectionId, otherUserId); // Different owner

    when(recipeCollectionRepository.findById(collectionId))
        .thenReturn(Optional.of(collection));

    // When & Then
    mockMvc
        .perform(
            delete("/collections/{collectionId}/collaborators/{userId}", collectionId, collaboratorUserId))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error").value("Access denied"))
        .andExpect(
            jsonPath("$.message")
                .value("You don't have permission to access this resource"));
  }

  @Test
  @DisplayName("Should return 404 when collaborator does not exist")
  @Tag("error-handling")
  void shouldReturn404WhenCollaboratorDoesNotExist() throws Exception {
    // Given
    Long collectionId = 3L;
    UUID nonCollaboratorId = UUID.fromString("222e2222-e22b-22d3-a222-222222222222");
    RecipeCollection collection = createTestCollection(collectionId, testUserId);

    when(recipeCollectionRepository.findById(collectionId))
        .thenReturn(Optional.of(collection));
    when(collectionCollaboratorRepository.existsByIdCollectionIdAndIdUserId(
            collectionId, nonCollaboratorId))
        .thenReturn(false);

    // When & Then
    mockMvc
        .perform(
            delete("/collections/{collectionId}/collaborators/{userId}", collectionId, nonCollaboratorId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("Resource not found"))
        .andExpect(jsonPath("$.message").value("User with ID " + nonCollaboratorId + " is not a collaborator on this collection"));
  }

  @Test
  @DisplayName("Should work regardless of collaboration mode")
  @Tag("standard-processing")
  void shouldWorkRegardlessOfCollaborationMode() throws Exception {
    // Given
    Long collectionId = 4L;
    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(testUserId)
            .name("Owner Only Collection")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY) // Not SPECIFIC_USERS
            .collectionItems(new ArrayList<>())
            .collaborators(new ArrayList<>())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    when(recipeCollectionRepository.findById(collectionId))
        .thenReturn(Optional.of(collection));
    when(collectionCollaboratorRepository.existsByIdCollectionIdAndIdUserId(
            collectionId, collaboratorUserId))
        .thenReturn(true);

    // When & Then
    mockMvc
        .perform(
            delete("/collections/{collectionId}/collaborators/{userId}", collectionId, collaboratorUserId))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("Should handle removal from PRIVATE collection")
  @Tag("standard-processing")
  void shouldHandleRemovalFromPrivateCollection() throws Exception {
    // Given
    Long collectionId = 5L;
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

    when(recipeCollectionRepository.findById(collectionId))
        .thenReturn(Optional.of(collection));
    when(collectionCollaboratorRepository.existsByIdCollectionIdAndIdUserId(
            collectionId, collaboratorUserId))
        .thenReturn(true);

    // When & Then
    mockMvc
        .perform(
            delete("/collections/{collectionId}/collaborators/{userId}", collectionId, collaboratorUserId))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("Should handle invalid UUID format")
  @Tag("error-handling")
  void shouldHandleInvalidUuidFormat() throws Exception {
    // Given
    Long collectionId = 6L;
    String invalidUserId = "invalid-uuid";

    // When & Then
    mockMvc
        .perform(
            delete("/collections/{collectionId}/collaborators/{userId}", collectionId, invalidUserId))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return no content with empty body")
  @Tag("standard-processing")
  void shouldReturnNoContentWithEmptyBody() throws Exception {
    // Given
    Long collectionId = 7L;
    RecipeCollection collection = createTestCollection(collectionId, testUserId);

    when(recipeCollectionRepository.findById(collectionId))
        .thenReturn(Optional.of(collection));
    when(collectionCollaboratorRepository.existsByIdCollectionIdAndIdUserId(
            collectionId, collaboratorUserId))
        .thenReturn(true);

    // When & Then
    mockMvc
        .perform(
            delete("/collections/{collectionId}/collaborators/{userId}", collectionId, collaboratorUserId))
        .andExpect(status().isNoContent())
        .andExpect(jsonPath("$").doesNotExist());
  }

  private RecipeCollection createTestCollection(Long collectionId, UUID ownerId) {
    return RecipeCollection.builder()
        .collectionId(collectionId)
        .userId(ownerId)
        .name("Test Collection")
        .description("Test Description")
        .visibility(CollectionVisibility.PUBLIC)
        .collaborationMode(CollaborationMode.SPECIFIC_USERS)
        .collectionItems(new ArrayList<>())
        .collaborators(new ArrayList<>())
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
  }
}
