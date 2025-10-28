package com.recipe_manager.component_tests.collection_service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.recipe_manager.service.CollectionService;

/**
 * Component test for DELETE /collections/{collectionId}/recipes/{recipeId} endpoint.
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
class RemoveRecipeFromCollectionTest {

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

    // Create real service with mocked repositories
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
  @DisplayName("Should remove recipe from collection when user is owner and return 204")
  @Tag("standard-processing")
  void shouldRemoveRecipeWhenUserIsOwner() throws Exception {
    // Given
    Long collectionId = 1L;
    Long recipeId = 100L;

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(testUserId) // User is the owner
            .name("My Collection")
            .visibility(CollectionVisibility.PRIVATE)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));
    when(recipeCollectionItemRepository.existsByIdCollectionIdAndIdRecipeId(collectionId, recipeId))
        .thenReturn(true);

    // When/Then
    mockMvc
        .perform(delete("/collections/{collectionId}/recipes/{recipeId}", collectionId, recipeId))
        .andExpect(status().isNoContent());

    verify(recipeCollectionRepository).findById(collectionId);
    verify(recipeCollectionItemRepository)
        .existsByIdCollectionIdAndIdRecipeId(collectionId, recipeId);
    verify(recipeCollectionItemRepository)
        .deleteByIdCollectionIdAndIdRecipeId(collectionId, recipeId);
  }

  @Test
  @DisplayName("Should remove recipe in ALL_USERS mode when user has edit permission")
  @Tag("standard-processing")
  void shouldRemoveRecipeInAllUsersMode() throws Exception {
    // Given
    Long collectionId = 2L;
    Long recipeId = 200L;

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(otherUserId) // Different owner
            .name("Public Collaboration Collection")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.ALL_USERS) // Any user can edit
            .build();

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));
    when(recipeCollectionItemRepository.existsByIdCollectionIdAndIdRecipeId(collectionId, recipeId))
        .thenReturn(true);

    // When/Then
    mockMvc
        .perform(delete("/collections/{collectionId}/recipes/{recipeId}", collectionId, recipeId))
        .andExpect(status().isNoContent());

    verify(recipeCollectionItemRepository)
        .deleteByIdCollectionIdAndIdRecipeId(collectionId, recipeId);
  }

  @Test
  @DisplayName("Should return 404 when collection does not exist")
  @Tag("error-handling")
  void shouldReturn404WhenCollectionNotFound() throws Exception {
    // Given
    Long nonExistentCollectionId = 999L;
    Long recipeId = 100L;

    when(recipeCollectionRepository.findById(nonExistentCollectionId))
        .thenReturn(Optional.empty());

    // When/Then
    mockMvc
        .perform(
            delete(
                "/collections/{collectionId}/recipes/{recipeId}",
                nonExistentCollectionId,
                recipeId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("Resource not found"))
        .andExpect(jsonPath("$.message").value("Collection not found"));

    verify(recipeCollectionRepository).findById(nonExistentCollectionId);
  }

  @Test
  @DisplayName("Should return 403 when user lacks edit permission (OWNER_ONLY mode)")
  @Tag("error-handling")
  void shouldReturn403WhenUserLacksEditPermission() throws Exception {
    // Given
    Long collectionId = 3L;
    Long recipeId = 300L;

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(otherUserId) // Different owner
            .name("Someone else's collection")
            .visibility(CollectionVisibility.PRIVATE)
            .collaborationMode(CollaborationMode.OWNER_ONLY) // Only owner can edit
            .build();

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));

    // When/Then
    mockMvc
        .perform(delete("/collections/{collectionId}/recipes/{recipeId}", collectionId, recipeId))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error").value("Access denied"))
        .andExpect(jsonPath("$.message").value("You don't have permission to access this resource"));

    verify(recipeCollectionRepository).findById(collectionId);
  }

  @Test
  @DisplayName("Should return 404 when recipe is not in collection")
  @Tag("error-handling")
  void shouldReturn404WhenRecipeNotInCollection() throws Exception {
    // Given
    Long collectionId = 4L;
    Long recipeId = 400L;

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(testUserId)
            .name("My Collection")
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));
    when(recipeCollectionItemRepository.existsByIdCollectionIdAndIdRecipeId(collectionId, recipeId))
        .thenReturn(false); // Recipe is not in collection

    // When/Then
    mockMvc
        .perform(delete("/collections/{collectionId}/recipes/{recipeId}", collectionId, recipeId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("Resource not found"))
        .andExpect(jsonPath("$.message").value("Recipe not found in this collection"));

    verify(recipeCollectionItemRepository)
        .existsByIdCollectionIdAndIdRecipeId(collectionId, recipeId);
  }

  @Test
  @DisplayName("Should allow owner to remove recipe in OWNER_ONLY mode")
  @Tag("standard-processing")
  void shouldAllowOwnerToRemoveRecipeInOwnerOnlyMode() throws Exception {
    // Given
    Long collectionId = 5L;
    Long recipeId = 500L;

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(testUserId) // User is the owner
            .name("Owner Only Collection")
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));
    when(recipeCollectionItemRepository.existsByIdCollectionIdAndIdRecipeId(collectionId, recipeId))
        .thenReturn(true);

    // When/Then
    mockMvc
        .perform(delete("/collections/{collectionId}/recipes/{recipeId}", collectionId, recipeId))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("Should allow collaborators to remove recipe in SPECIFIC_USERS mode")
  @Tag("standard-processing")
  void shouldAllowCollaboratorsToRemoveRecipeInSpecificUsersMode() throws Exception {
    // Given
    Long collectionId = 6L;
    Long recipeId = 600L;

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(otherUserId) // Different owner
            .name("Collaborator Collection")
            .collaborationMode(CollaborationMode.SPECIFIC_USERS) // Only specific users can edit
            .build();

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));
    when(collectionCollaboratorRepository.existsByIdCollectionIdAndIdUserId(
            collectionId, testUserId))
        .thenReturn(true); // User is a collaborator
    when(recipeCollectionItemRepository.existsByIdCollectionIdAndIdRecipeId(collectionId, recipeId))
        .thenReturn(true);

    // When/Then
    mockMvc
        .perform(delete("/collections/{collectionId}/recipes/{recipeId}", collectionId, recipeId))
        .andExpect(status().isNoContent());

    verify(collectionCollaboratorRepository)
        .existsByIdCollectionIdAndIdUserId(collectionId, testUserId);
    verify(recipeCollectionItemRepository)
        .deleteByIdCollectionIdAndIdRecipeId(collectionId, recipeId);
  }

  @Test
  @DisplayName("Should deny non-collaborators in SPECIFIC_USERS mode")
  @Tag("error-handling")
  void shouldDenyNonCollaboratorsInSpecificUsersMode() throws Exception {
    // Given
    Long collectionId = 7L;
    Long recipeId = 700L;

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(otherUserId) // Different owner
            .name("Collaborator Collection")
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .build();

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));
    when(collectionCollaboratorRepository.existsByIdCollectionIdAndIdUserId(
            collectionId, testUserId))
        .thenReturn(false); // User is NOT a collaborator

    // When/Then
    mockMvc
        .perform(delete("/collections/{collectionId}/recipes/{recipeId}", collectionId, recipeId))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error").value("Access denied"));

    verify(collectionCollaboratorRepository)
        .existsByIdCollectionIdAndIdUserId(collectionId, testUserId);
  }
}
