package com.recipe_manager.component_tests.collection_service;

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

import jakarta.persistence.EntityManager;

/**
 * Component test for GET /collections/{collectionId}/collaborators endpoint.
 *
 * <p>Tests the integration between CollectionController and CollectionService with mocked
 * repository layer.
 */
@Tag("component")
@SpringBootTest(
    classes = {
      com.recipe_manager.model.mapper.CollectionMapperImpl.class,
      com.recipe_manager.model.mapper.RecipeCollectionMapperImpl.class,
      com.recipe_manager.model.mapper.RecipeCollectionItemMapperImpl.class
    })
@TestPropertySource(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:testdb",
      "spring.jpa.hibernate.ddl-auto=none",
      "spring.flyway.enabled=false"
    })
class GetCollaboratorsTest {

  private MockMvc mockMvc;

  @Mock private RecipeCollectionRepository recipeCollectionRepository;

  @Mock private RecipeCollectionItemRepository recipeCollectionItemRepository;

  @Mock private CollectionCollaboratorRepository collectionCollaboratorRepository;

  @Autowired private CollectionMapper collectionMapper;

  @Autowired private RecipeCollectionMapper recipeCollectionMapper;

  @Autowired private RecipeCollectionItemMapper recipeCollectionItemMapper;

  @Mock private RecipeRepository recipeRepository;

  @Mock private NotificationService notificationService;

  @Mock private EntityManager entityManager;

  private CollectionService collectionService;
  private CollectionController collectionController;

  private UUID testUserId;
  private UUID otherUserId;
  private UUID collaboratorUserId;
  private UUID grantedByUserId;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    testUserId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    otherUserId = UUID.fromString("987e6543-e21b-12d3-a456-426614174000");
    collaboratorUserId = UUID.fromString("111e1111-e11b-11d3-a111-111111111111");
    grantedByUserId = UUID.fromString("222e2222-e22b-22d3-a222-222222222222");

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
            collectionMapper,
            recipeCollectionMapper,
            recipeCollectionItemMapper,
            recipeRepository,
            notificationService,
            entityManager);
    collectionController = new CollectionController(collectionService);

    mockMvc =
        MockMvcBuilders.standaloneSetup(collectionController)
            .addFilters(new RequestIdFilter())
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
  }

  @Test
  @DisplayName("Should get collaborators successfully when user is owner")
  @Tag("standard-processing")
  void shouldGetCollaboratorsWhenUserIsOwner() throws Exception {
    // Given
    Long collectionId = 1L;
    RecipeCollection collection = createTestCollection(collectionId, testUserId);

    LocalDateTime grantedAt = LocalDateTime.now().minusDays(1);
    Object[] collaborator1 =
        new Object[] {
          collectionId,
          collaboratorUserId,
          "john_doe",
          grantedByUserId,
          "admin_user",
          java.sql.Timestamp.valueOf(grantedAt)
        };

    when(recipeCollectionRepository.findById(collectionId))
        .thenReturn(Optional.of(collection));
    when(collectionCollaboratorRepository.findCollaboratorsWithUsernamesByCollectionId(
            collectionId))
        .thenReturn(List.<Object[]>of(collaborator1));

    // When & Then
    mockMvc
        .perform(get("/collections/{collectionId}/collaborators", collectionId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].collectionId").value(collectionId))
        .andExpect(jsonPath("$[0].userId").value(collaboratorUserId.toString()))
        .andExpect(jsonPath("$[0].username").value("john_doe"))
        .andExpect(jsonPath("$[0].grantedBy").value(grantedByUserId.toString()))
        .andExpect(jsonPath("$[0].grantedByUsername").value("admin_user"))
        .andExpect(jsonPath("$[0].grantedAt").exists());
  }

  @Test
  @DisplayName("Should get collaborators when user has view access")
  @Tag("standard-processing")
  void shouldGetCollaboratorsWhenUserHasViewAccess() throws Exception {
    // Given
    Long collectionId = 2L;
    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(otherUserId) // Different owner
            .name("Shared Collection")
            .visibility(CollectionVisibility.PUBLIC) // Public so testUserId has view access
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .collectionItems(new ArrayList<>())
            .collaborators(new ArrayList<>())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    LocalDateTime grantedAt = LocalDateTime.now().minusHours(2);
    Object[] collaborator1 =
        new Object[] {
          collectionId,
          collaboratorUserId,
          "jane_smith",
          otherUserId,
          "collection_owner",
          java.sql.Timestamp.valueOf(grantedAt)
        };

    when(recipeCollectionRepository.findById(collectionId))
        .thenReturn(Optional.of(collection));
    when(collectionCollaboratorRepository.findCollaboratorsWithUsernamesByCollectionId(
            collectionId))
        .thenReturn(List.<Object[]>of(collaborator1));

    // When & Then
    mockMvc
        .perform(get("/collections/{collectionId}/collaborators", collectionId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].username").value("jane_smith"))
        .andExpect(jsonPath("$[0].grantedByUsername").value("collection_owner"));
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
        .perform(get("/collections/{collectionId}/collaborators", collectionId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("Resource not found"))
        .andExpect(jsonPath("$.message").value("Collection not found"));
  }

  @Test
  @DisplayName("Should return 403 when user has no view permission")
  @Tag("error-handling")
  void shouldReturn403WhenUserHasNoViewPermission() throws Exception {
    // Given
    Long collectionId = 3L;
    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(otherUserId) // Different owner
            .name("Private Collection")
            .visibility(CollectionVisibility.PRIVATE) // Private - no view access for testUserId
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .collectionItems(new ArrayList<>())
            .collaborators(new ArrayList<>())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    when(recipeCollectionRepository.findById(collectionId))
        .thenReturn(Optional.of(collection));

    // When & Then
    mockMvc
        .perform(get("/collections/{collectionId}/collaborators", collectionId))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error").value("Access denied"))
        .andExpect(jsonPath("$.message").value("You don't have permission to access this resource"));
  }

  @Test
  @DisplayName("Should return 403 when collection is not SPECIFIC_USERS mode")
  @Tag("error-handling")
  void shouldReturn403WhenCollectionIsNotSpecificUsersMode() throws Exception {
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

    // When & Then
    mockMvc
        .perform(get("/collections/{collectionId}/collaborators", collectionId))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error").value("Access denied"))
        .andExpect(jsonPath("$.message").value("You don't have permission to access this resource"));
  }

  @Test
  @DisplayName("Should handle empty collaborators list")
  @Tag("standard-processing")
  void shouldHandleEmptyCollaboratorsList() throws Exception {
    // Given
    Long collectionId = 5L;
    RecipeCollection collection = createTestCollection(collectionId, testUserId);

    when(recipeCollectionRepository.findById(collectionId))
        .thenReturn(Optional.of(collection));
    when(collectionCollaboratorRepository.findCollaboratorsWithUsernamesByCollectionId(
            collectionId))
        .thenReturn(new ArrayList<>());

    // When & Then
    mockMvc
        .perform(get("/collections/{collectionId}/collaborators", collectionId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  @DisplayName("Should handle multiple collaborators")
  @Tag("standard-processing")
  void shouldHandleMultipleCollaborators() throws Exception {
    // Given
    Long collectionId = 6L;
    RecipeCollection collection = createTestCollection(collectionId, testUserId);

    LocalDateTime grantedAt1 = LocalDateTime.now().minusDays(2);
    LocalDateTime grantedAt2 = LocalDateTime.now().minusDays(1);

    UUID collaborator2Id = UUID.fromString("333e3333-e33b-33d3-a333-333333333333");

    Object[] collaborator1 =
        new Object[] {
          collectionId,
          collaboratorUserId,
          "john_doe",
          testUserId,
          "owner_user",
          java.sql.Timestamp.valueOf(grantedAt1)
        };

    Object[] collaborator2 =
        new Object[] {
          collectionId,
          collaborator2Id,
          "jane_smith",
          testUserId,
          "owner_user",
          java.sql.Timestamp.valueOf(grantedAt2)
        };

    when(recipeCollectionRepository.findById(collectionId))
        .thenReturn(Optional.of(collection));
    when(collectionCollaboratorRepository.findCollaboratorsWithUsernamesByCollectionId(
            collectionId))
        .thenReturn(List.<Object[]>of(collaborator1, collaborator2));

    // When & Then
    mockMvc
        .perform(get("/collections/{collectionId}/collaborators", collectionId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].username").value("john_doe"))
        .andExpect(jsonPath("$[0].grantedByUsername").value("owner_user"))
        .andExpect(jsonPath("$[1].username").value("jane_smith"))
        .andExpect(jsonPath("$[1].grantedByUsername").value("owner_user"));
  }

  @Test
  @DisplayName("Should preserve all DTO fields through mapper")
  @Tag("standard-processing")
  void shouldPreserveAllDtoFields() throws Exception {
    // Given
    Long collectionId = 7L;
    RecipeCollection collection = createTestCollection(collectionId, testUserId);

    LocalDateTime grantedAt = LocalDateTime.now().minusDays(1);
    Object[] collaborator =
        new Object[] {
          collectionId,
          collaboratorUserId,
          "test_user",
          grantedByUserId,
          "granting_user",
          java.sql.Timestamp.valueOf(grantedAt)
        };

    when(recipeCollectionRepository.findById(collectionId))
        .thenReturn(Optional.of(collection));
    when(collectionCollaboratorRepository.findCollaboratorsWithUsernamesByCollectionId(
            collectionId))
        .thenReturn(List.<Object[]>of(collaborator));

    // When & Then
    mockMvc
        .perform(get("/collections/{collectionId}/collaborators", collectionId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].collectionId").exists())
        .andExpect(jsonPath("$[0].userId").exists())
        .andExpect(jsonPath("$[0].username").exists())
        .andExpect(jsonPath("$[0].grantedBy").exists())
        .andExpect(jsonPath("$[0].grantedByUsername").exists())
        .andExpect(jsonPath("$[0].grantedAt").exists());
  }

  @Test
  @DisplayName("Should return collaborators ordered by grantedAt DESC")
  @Tag("standard-processing")
  void shouldReturnCollaboratorsOrderedByGrantedAtDesc() throws Exception {
    // Given
    Long collectionId = 8L;
    RecipeCollection collection = createTestCollection(collectionId, testUserId);

    // Newest first (DESC order)
    LocalDateTime newerGrantedAt = LocalDateTime.now().minusHours(1);
    LocalDateTime olderGrantedAt = LocalDateTime.now().minusDays(5);

    UUID newerUserId = UUID.fromString("444e4444-e44b-44d3-a444-444444444444");
    UUID olderUserId = UUID.fromString("555e5555-e55b-55d3-a555-555555555555");

    Object[] newerCollaborator =
        new Object[] {
          collectionId,
          newerUserId,
          "newer_user",
          testUserId,
          "owner",
          java.sql.Timestamp.valueOf(newerGrantedAt)
        };

    Object[] olderCollaborator =
        new Object[] {
          collectionId,
          olderUserId,
          "older_user",
          testUserId,
          "owner",
          java.sql.Timestamp.valueOf(olderGrantedAt)
        };

    when(recipeCollectionRepository.findById(collectionId))
        .thenReturn(Optional.of(collection));
    // Repository returns in DESC order (newest first)
    when(collectionCollaboratorRepository.findCollaboratorsWithUsernamesByCollectionId(
            collectionId))
        .thenReturn(Arrays.asList(newerCollaborator, olderCollaborator));

    // When & Then
    mockMvc
        .perform(get("/collections/{collectionId}/collaborators", collectionId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].username").value("newer_user")) // Newer first
        .andExpect(jsonPath("$[1].username").value("older_user")); // Older second
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
