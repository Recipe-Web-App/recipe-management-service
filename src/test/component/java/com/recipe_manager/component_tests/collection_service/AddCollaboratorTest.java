package com.recipe_manager.component_tests.collection_service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
import org.springframework.dao.DataIntegrityViolationException;
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
import com.recipe_manager.model.dto.request.AddCollaboratorRequest;
import com.recipe_manager.model.entity.collection.CollectionCollaborator;
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
 * Component test for POST /collections/{collectionId}/collaborators endpoint.
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
class AddCollaboratorTest {

  private MockMvc mockMvc;

  @Mock private RecipeCollectionRepository recipeCollectionRepository;

  @Mock private RecipeCollectionItemRepository recipeCollectionItemRepository;

  @Mock private CollectionCollaboratorRepository collectionCollaboratorRepository;

  @Mock private RecipeRepository recipeRepository;

  @Mock private NotificationService notificationService;

  @Mock private EntityManager entityManager;

  @Autowired private CollectionMapper collectionMapper;

  @Autowired private RecipeCollectionMapper recipeCollectionMapper;

  @Autowired private RecipeCollectionItemMapper recipeCollectionItemMapper;

  private CollectionService collectionService;
  private CollectionController collectionController;

  private ObjectMapper objectMapper;

  private UUID testUserId;
  private UUID otherUserId;
  private UUID targetUserId;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    testUserId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    otherUserId = UUID.fromString("987e6543-e21b-12d3-a456-426614174000");
    targetUserId = UUID.fromString("111e1111-e11b-11d3-a111-111111111111");

    objectMapper = new ObjectMapper();

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
  @DisplayName("Should add collaborator successfully when user is owner")
  @Tag("standard-processing")
  void shouldAddCollaboratorWhenUserIsOwner() throws Exception {
    // Given
    Long collectionId = 1L;
    RecipeCollection collection = createTestCollection(collectionId, testUserId);

    AddCollaboratorRequest request =
        AddCollaboratorRequest.builder().userId(targetUserId).build();

    LocalDateTime grantedAt = LocalDateTime.now();
    Object[] collaboratorRow =
        new Object[] {
          collectionId,
          targetUserId,
          "new_collaborator",
          testUserId,
          "owner_user",
          java.sql.Timestamp.valueOf(grantedAt)
        };

    when(recipeCollectionRepository.findById(collectionId))
        .thenReturn(Optional.of(collection));
    when(collectionCollaboratorRepository.existsByIdCollectionIdAndIdUserId(
            collectionId, targetUserId))
        .thenReturn(false);
    when(collectionCollaboratorRepository.save(any(CollectionCollaborator.class)))
        .thenReturn(null);
    when(collectionCollaboratorRepository.findCollaboratorsWithUsernamesByCollectionId(
            collectionId))
        .thenReturn(List.<Object[]>of(collaboratorRow));

    // When & Then
    mockMvc
        .perform(
            post("/collections/{collectionId}/collaborators", collectionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.collectionId").value(collectionId))
        .andExpect(jsonPath("$.userId").value(targetUserId.toString()))
        .andExpect(jsonPath("$.username").value("new_collaborator"))
        .andExpect(jsonPath("$.grantedBy").value(testUserId.toString()))
        .andExpect(jsonPath("$.grantedByUsername").value("owner_user"))
        .andExpect(jsonPath("$.grantedAt").exists());
  }

  @Test
  @DisplayName("Should return 404 when collection does not exist")
  @Tag("error-handling")
  void shouldReturn404WhenCollectionDoesNotExist() throws Exception {
    // Given
    Long collectionId = 999L;
    AddCollaboratorRequest request =
        AddCollaboratorRequest.builder().userId(targetUserId).build();

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.empty());

    // When & Then
    mockMvc
        .perform(
            post("/collections/{collectionId}/collaborators", collectionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
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

    AddCollaboratorRequest request =
        AddCollaboratorRequest.builder().userId(targetUserId).build();

    when(recipeCollectionRepository.findById(collectionId))
        .thenReturn(Optional.of(collection));

    // When & Then
    mockMvc
        .perform(
            post("/collections/{collectionId}/collaborators", collectionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error").value("Access denied"))
        .andExpect(
            jsonPath("$.message")
                .value("You don't have permission to access this resource"));
  }

  @Test
  @DisplayName("Should return 403 when collaboration mode is OWNER_ONLY")
  @Tag("error-handling")
  void shouldReturn403WhenCollaborationModeIsOwnerOnly() throws Exception {
    // Given
    Long collectionId = 3L;
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

    AddCollaboratorRequest request =
        AddCollaboratorRequest.builder().userId(targetUserId).build();

    when(recipeCollectionRepository.findById(collectionId))
        .thenReturn(Optional.of(collection));

    // When & Then
    mockMvc
        .perform(
            post("/collections/{collectionId}/collaborators", collectionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error").value("Access denied"))
        .andExpect(
            jsonPath("$.message")
                .value("You don't have permission to access this resource"));
  }

  @Test
  @DisplayName("Should return 403 when collaboration mode is ALL_USERS")
  @Tag("error-handling")
  void shouldReturn403WhenCollaborationModeIsAllUsers() throws Exception {
    // Given
    Long collectionId = 4L;
    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(testUserId)
            .name("All Users Collection")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.ALL_USERS) // Not SPECIFIC_USERS
            .collectionItems(new ArrayList<>())
            .collaborators(new ArrayList<>())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    AddCollaboratorRequest request =
        AddCollaboratorRequest.builder().userId(targetUserId).build();

    when(recipeCollectionRepository.findById(collectionId))
        .thenReturn(Optional.of(collection));

    // When & Then
    mockMvc
        .perform(
            post("/collections/{collectionId}/collaborators", collectionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error").value("Access denied"))
        .andExpect(
            jsonPath("$.message")
                .value("You don't have permission to access this resource"));
  }

  @Test
  @DisplayName("Should return 409 when user is already a collaborator")
  @Tag("error-handling")
  void shouldReturn409WhenUserIsAlreadyCollaborator() throws Exception {
    // Given
    Long collectionId = 5L;
    RecipeCollection collection = createTestCollection(collectionId, testUserId);

    AddCollaboratorRequest request =
        AddCollaboratorRequest.builder().userId(targetUserId).build();

    when(recipeCollectionRepository.findById(collectionId))
        .thenReturn(Optional.of(collection));
    when(collectionCollaboratorRepository.existsByIdCollectionIdAndIdUserId(
            collectionId, targetUserId))
        .thenReturn(true); // Already exists

    // When & Then
    mockMvc
        .perform(
            post("/collections/{collectionId}/collaborators", collectionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.error").value("Resource conflict"))
        .andExpect(
            jsonPath("$.message").value("User is already a collaborator on this collection"));
  }

  @Test
  @DisplayName("Should return 409 when trying to add owner as collaborator")
  @Tag("error-handling")
  void shouldReturn409WhenTryingToAddOwnerAsCollaborator() throws Exception {
    // Given
    Long collectionId = 6L;
    RecipeCollection collection = createTestCollection(collectionId, testUserId);

    AddCollaboratorRequest request =
        AddCollaboratorRequest.builder().userId(testUserId).build(); // Same as owner

    when(recipeCollectionRepository.findById(collectionId))
        .thenReturn(Optional.of(collection));

    // When & Then
    mockMvc
        .perform(
            post("/collections/{collectionId}/collaborators", collectionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.error").value("Resource conflict"))
        .andExpect(
            jsonPath("$.message").value("Collection owner cannot be added as a collaborator"));
  }

  @Test
  @DisplayName("Should return 404 when target user does not exist")
  @Tag("error-handling")
  void shouldReturn404WhenTargetUserDoesNotExist() throws Exception {
    // Given
    Long collectionId = 7L;
    RecipeCollection collection = createTestCollection(collectionId, testUserId);

    UUID nonExistentUserId = UUID.fromString("999e9999-e99b-99d3-a999-999999999999");
    AddCollaboratorRequest request =
        AddCollaboratorRequest.builder().userId(nonExistentUserId).build();

    when(recipeCollectionRepository.findById(collectionId))
        .thenReturn(Optional.of(collection));
    when(collectionCollaboratorRepository.existsByIdCollectionIdAndIdUserId(
            collectionId, nonExistentUserId))
        .thenReturn(false);
    when(collectionCollaboratorRepository.save(any(CollectionCollaborator.class)))
        .thenThrow(new DataIntegrityViolationException("FK constraint violation"));

    // When & Then
    mockMvc
        .perform(
            post("/collections/{collectionId}/collaborators", collectionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("Resource not found"))
        .andExpect(
            jsonPath("$.message").value("User with ID " + nonExistentUserId + " not found"));
  }

  @Test
  @DisplayName("Should work with different collection visibilities")
  @Tag("standard-processing")
  void shouldWorkWithDifferentCollectionVisibilities() throws Exception {
    // Given
    Long collectionId = 8L;
    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(testUserId)
            .name("Private Collection")
            .visibility(CollectionVisibility.PRIVATE) // Private visibility
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .collectionItems(new ArrayList<>())
            .collaborators(new ArrayList<>())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    AddCollaboratorRequest request =
        AddCollaboratorRequest.builder().userId(targetUserId).build();

    LocalDateTime grantedAt = LocalDateTime.now();
    Object[] collaboratorRow =
        new Object[] {
          collectionId,
          targetUserId,
          "private_collaborator",
          testUserId,
          "owner_user",
          java.sql.Timestamp.valueOf(grantedAt)
        };

    when(recipeCollectionRepository.findById(collectionId))
        .thenReturn(Optional.of(collection));
    when(collectionCollaboratorRepository.existsByIdCollectionIdAndIdUserId(
            collectionId, targetUserId))
        .thenReturn(false);
    when(collectionCollaboratorRepository.save(any(CollectionCollaborator.class)))
        .thenReturn(null);
    when(collectionCollaboratorRepository.findCollaboratorsWithUsernamesByCollectionId(
            collectionId))
        .thenReturn(List.<Object[]>of(collaboratorRow));

    // When & Then
    mockMvc
        .perform(
            post("/collections/{collectionId}/collaborators", collectionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("private_collaborator"));
  }

  @Test
  @DisplayName("Should preserve all DTO fields in response")
  @Tag("standard-processing")
  void shouldPreserveAllDtoFieldsInResponse() throws Exception {
    // Given
    Long collectionId = 9L;
    RecipeCollection collection = createTestCollection(collectionId, testUserId);

    AddCollaboratorRequest request =
        AddCollaboratorRequest.builder().userId(targetUserId).build();

    LocalDateTime grantedAt = LocalDateTime.now();
    Object[] collaboratorRow =
        new Object[] {
          collectionId,
          targetUserId,
          "complete_collaborator",
          testUserId,
          "complete_owner",
          java.sql.Timestamp.valueOf(grantedAt)
        };

    when(recipeCollectionRepository.findById(collectionId))
        .thenReturn(Optional.of(collection));
    when(collectionCollaboratorRepository.existsByIdCollectionIdAndIdUserId(
            collectionId, targetUserId))
        .thenReturn(false);
    when(collectionCollaboratorRepository.save(any(CollectionCollaborator.class)))
        .thenReturn(null);
    when(collectionCollaboratorRepository.findCollaboratorsWithUsernamesByCollectionId(
            collectionId))
        .thenReturn(List.<Object[]>of(collaboratorRow));

    // When & Then
    mockMvc
        .perform(
            post("/collections/{collectionId}/collaborators", collectionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.collectionId").exists())
        .andExpect(jsonPath("$.userId").exists())
        .andExpect(jsonPath("$.username").exists())
        .andExpect(jsonPath("$.grantedBy").exists())
        .andExpect(jsonPath("$.grantedByUsername").exists())
        .andExpect(jsonPath("$.grantedAt").exists());
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
