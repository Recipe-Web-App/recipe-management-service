package com.recipe_manager.component_tests.favorite_service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.recipe_manager.client.usermanagement.UserManagementClient;
import com.recipe_manager.component_tests.AbstractComponentTest;
import com.recipe_manager.controller.FavoriteController;
import com.recipe_manager.exception.GlobalExceptionHandler;
import com.recipe_manager.model.entity.collection.CollectionFavorite;
import com.recipe_manager.model.entity.collection.CollectionFavoriteId;
import com.recipe_manager.model.entity.collection.RecipeCollection;
import com.recipe_manager.model.enums.CollaborationMode;
import com.recipe_manager.model.mapper.CollectionFavoriteMapper;
import com.recipe_manager.model.mapper.CollectionMapper;
import com.recipe_manager.model.mapper.RecipeFavoriteMapper;
import com.recipe_manager.model.mapper.RecipeMapper;
import com.recipe_manager.repository.collection.CollectionFavoriteRepository;
import com.recipe_manager.repository.collection.RecipeCollectionRepository;
import com.recipe_manager.repository.recipe.RecipeFavoriteRepository;
import com.recipe_manager.service.FavoriteService;
import com.recipe_manager.util.SecurityUtils;

/**
 * Component tests for adding collection favorites.
 *
 * <p>Tests the POST /favorites/collections/{collectionId} endpoint with real service and mapper
 * logic, mocking only repositories and external dependencies.
 */
@SpringBootTest(
    classes = {
      com.recipe_manager.model.mapper.RecipeFavoriteMapperImpl.class,
      com.recipe_manager.model.mapper.CollectionFavoriteMapperImpl.class,
      com.recipe_manager.model.mapper.CollectionMapperImpl.class
    })
@TestPropertySource(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:testdb",
      "spring.jpa.hibernate.ddl-auto=none",
      "spring.flyway.enabled=false"
    })
@Tag("component")
class FavoriteCollectionComponentTest extends AbstractComponentTest {

  private FavoriteService favoriteService;
  private FavoriteController favoriteController;

  // Mocked dependencies
  private RecipeFavoriteRepository recipeFavoriteRepository;
  private RecipeMapper testRecipeMapper;
  private UserManagementClient userManagementClient;
  private CollectionFavoriteRepository collectionFavoriteRepository;
  private RecipeCollectionRepository recipeCollectionRepository;

  @Autowired(required = false)
  private RecipeFavoriteMapper recipeFavoriteMapper;

  @Autowired(required = false)
  private CollectionFavoriteMapper collectionFavoriteMapper;

  @Autowired(required = false)
  private CollectionMapper collectionMapper;

  private UUID testUserId;
  private Long testCollectionId;

  @Override
  @BeforeEach
  protected void setUp() {
    super.setUp();
    useRealFavoriteService();
  }

  private void useRealFavoriteService() {
    testUserId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    testCollectionId = 200L;

    // Mock dependencies
    this.recipeFavoriteRepository = Mockito.mock(RecipeFavoriteRepository.class);
    this.testRecipeMapper = Mockito.mock(RecipeMapper.class);
    this.userManagementClient = Mockito.mock(UserManagementClient.class);
    this.collectionFavoriteRepository = Mockito.mock(CollectionFavoriteRepository.class);
    this.recipeCollectionRepository = Mockito.mock(RecipeCollectionRepository.class);

    if (collectionFavoriteMapper == null) {
      throw new RuntimeException("CollectionFavoriteMapper not available in test context");
    }

    // Create real service with all dependencies
    this.favoriteService =
        new FavoriteService(
            recipeFavoriteRepository,
            recipeRepository,
            collectionFavoriteRepository,
            recipeCollectionRepository,
            recipeFavoriteMapper,
            testRecipeMapper,
            collectionFavoriteMapper,
            collectionMapper,
            userManagementClient);

    // Create controller
    this.favoriteController = new FavoriteController(favoriteService);

    // Rebuild MockMvc with FavoriteController
    mockMvc =
        MockMvcBuilders.standaloneSetup(favoriteController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should add collection favorite successfully and return 201 Created")
  void shouldAddCollectionFavoriteSuccessfully() throws Exception {
    // Given
    RecipeCollection collection = createTestCollection(testCollectionId);
    CollectionFavorite savedFavorite = createTestFavorite(testUserId, testCollectionId, collection);

    when(collectionFavoriteRepository.existsByIdUserIdAndIdCollectionId(
            testUserId, testCollectionId))
        .thenReturn(false);
    when(recipeCollectionRepository.findById(testCollectionId))
        .thenReturn(Optional.of(collection));
    when(recipeCollectionRepository.hasViewAccess(testCollectionId, testUserId)).thenReturn(true);
    when(collectionFavoriteRepository.save(any(CollectionFavorite.class)))
        .thenReturn(savedFavorite);

    // When/Then
    try (MockedStatic<SecurityUtils> securityUtilsMock =
        Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      mockMvc
          .perform(post("/favorites/collections/{collectionId}", testCollectionId))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.userId").value(testUserId.toString()))
          .andExpect(jsonPath("$.collectionId").value(testCollectionId))
          .andExpect(jsonPath("$.favoritedAt").exists());
    }

    verify(collectionFavoriteRepository)
        .existsByIdUserIdAndIdCollectionId(testUserId, testCollectionId);
    verify(recipeCollectionRepository).findById(testCollectionId);
    verify(collectionFavoriteRepository).save(any(CollectionFavorite.class));
  }

  @Test
  @Tag("error-handling")
  @DisplayName("Should return 400 Bad Request when collection already favorited")
  void shouldReturn400WhenCollectionAlreadyFavorited() throws Exception {
    // Given
    when(collectionFavoriteRepository.existsByIdUserIdAndIdCollectionId(
            testUserId, testCollectionId))
        .thenReturn(true);

    // When/Then
    try (MockedStatic<SecurityUtils> securityUtilsMock =
        Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      mockMvc
          .perform(post("/favorites/collections/{collectionId}", testCollectionId))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.error").value("Business error"))
          .andExpect(jsonPath("$.message").value("User has already favorited this collection"));
    }

    verify(collectionFavoriteRepository)
        .existsByIdUserIdAndIdCollectionId(testUserId, testCollectionId);
  }

  @Test
  @Tag("error-handling")
  @DisplayName("Should return 404 Not Found when collection does not exist")
  void shouldReturn404WhenCollectionNotFound() throws Exception {
    // Given
    when(collectionFavoriteRepository.existsByIdUserIdAndIdCollectionId(
            testUserId, testCollectionId))
        .thenReturn(false);
    when(recipeCollectionRepository.findById(testCollectionId)).thenReturn(Optional.empty());

    // When/Then
    try (MockedStatic<SecurityUtils> securityUtilsMock =
        Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      mockMvc
          .perform(post("/favorites/collections/{collectionId}", testCollectionId))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.error").value("Resource not found"))
          .andExpect(
              jsonPath("$.message").value("Collection not found with ID: " + testCollectionId));
    }

    verify(collectionFavoriteRepository)
        .existsByIdUserIdAndIdCollectionId(testUserId, testCollectionId);
    verify(recipeCollectionRepository).findById(testCollectionId);
  }

  @Test
  @Tag("error-handling")
  @DisplayName("Should return 403 Forbidden when user cannot view collection")
  void shouldReturn403WhenUserCannotViewCollection() throws Exception {
    // Given
    RecipeCollection collection = createTestCollection(testCollectionId);

    when(collectionFavoriteRepository.existsByIdUserIdAndIdCollectionId(
            testUserId, testCollectionId))
        .thenReturn(false);
    when(recipeCollectionRepository.findById(testCollectionId))
        .thenReturn(Optional.of(collection));
    when(recipeCollectionRepository.hasViewAccess(testCollectionId, testUserId)).thenReturn(false);

    // When/Then
    try (MockedStatic<SecurityUtils> securityUtilsMock =
        Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      mockMvc
          .perform(post("/favorites/collections/{collectionId}", testCollectionId))
          .andExpect(status().isForbidden());
    }

    verify(recipeCollectionRepository).hasViewAccess(testCollectionId, testUserId);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should construct CollectionFavoriteId correctly with composite key")
  void shouldConstructCollectionFavoriteIdCorrectly() throws Exception {
    // Given
    RecipeCollection collection = createTestCollection(testCollectionId);
    CollectionFavorite savedFavorite = createTestFavorite(testUserId, testCollectionId, collection);

    when(collectionFavoriteRepository.existsByIdUserIdAndIdCollectionId(
            testUserId, testCollectionId))
        .thenReturn(false);
    when(recipeCollectionRepository.findById(testCollectionId))
        .thenReturn(Optional.of(collection));
    when(recipeCollectionRepository.hasViewAccess(testCollectionId, testUserId)).thenReturn(true);
    when(collectionFavoriteRepository.save(any(CollectionFavorite.class)))
        .thenReturn(savedFavorite);

    // When
    try (MockedStatic<SecurityUtils> securityUtilsMock =
        Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      mockMvc
          .perform(post("/favorites/collections/{collectionId}", testCollectionId))
          .andExpect(status().isCreated());
    }

    // Then - Verify save called with entity containing correct composite key
    verify(collectionFavoriteRepository)
        .save(
            org.mockito.ArgumentMatchers.argThat(
                favorite ->
                    favorite.getId().getUserId().equals(testUserId)
                        && favorite.getId().getCollectionId().equals(testCollectionId)));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should include all required fields in response DTO")
  void shouldIncludeAllRequiredFieldsInResponse() throws Exception {
    // Given
    RecipeCollection collection = createTestCollection(testCollectionId);
    CollectionFavorite savedFavorite = createTestFavorite(testUserId, testCollectionId, collection);

    when(collectionFavoriteRepository.existsByIdUserIdAndIdCollectionId(
            testUserId, testCollectionId))
        .thenReturn(false);
    when(recipeCollectionRepository.findById(testCollectionId))
        .thenReturn(Optional.of(collection));
    when(recipeCollectionRepository.hasViewAccess(testCollectionId, testUserId)).thenReturn(true);
    when(collectionFavoriteRepository.save(any(CollectionFavorite.class)))
        .thenReturn(savedFavorite);

    // When/Then
    try (MockedStatic<SecurityUtils> securityUtilsMock =
        Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      mockMvc
          .perform(
              post("/favorites/collections/{collectionId}", testCollectionId)
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.userId").exists())
          .andExpect(jsonPath("$.collectionId").exists())
          .andExpect(jsonPath("$.favoritedAt").exists())
          .andExpect(jsonPath("$.userId").value(testUserId.toString()))
          .andExpect(jsonPath("$.collectionId").value(testCollectionId.intValue()));
    }
  }

  // Helper methods

  private RecipeCollection createTestCollection(Long collectionId) {
    return RecipeCollection.builder()
        .collectionId(collectionId)
        .name("Test Collection " + collectionId)
        .description("Test Description")
        .userId(UUID.randomUUID())
        .collaborationMode(CollaborationMode.OWNER_ONLY)
        .build();
  }

  private CollectionFavorite createTestFavorite(
      UUID userId, Long collectionId, RecipeCollection collection) {
    CollectionFavoriteId id =
        CollectionFavoriteId.builder().userId(userId).collectionId(collectionId).build();

    return CollectionFavorite.builder()
        .id(id)
        .collection(collection)
        .favoritedAt(LocalDateTime.now())
        .build();
  }
}
