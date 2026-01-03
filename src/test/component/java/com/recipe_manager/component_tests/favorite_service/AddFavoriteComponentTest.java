package com.recipe_manager.component_tests.favorite_service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.entity.recipe.RecipeFavorite;
import com.recipe_manager.model.entity.recipe.RecipeFavoriteId;
import com.recipe_manager.model.mapper.RecipeFavoriteMapper;
import com.recipe_manager.model.mapper.RecipeMapper;
import com.recipe_manager.repository.recipe.RecipeFavoriteRepository;
import com.recipe_manager.service.FavoriteService;
import com.recipe_manager.util.SecurityUtils;

/**
 * Component tests for adding recipe favorites.
 *
 * <p>Tests the POST /favorites/recipes/{recipeId} endpoint with real service and mapper logic,
 * mocking only repositories and external dependencies.
 */
@SpringBootTest(classes = {
    com.recipe_manager.model.mapper.RecipeFavoriteMapperImpl.class
})
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=none",
    "spring.flyway.enabled=false"
})
@Tag("component")
class AddFavoriteComponentTest extends AbstractComponentTest {

  private FavoriteService favoriteService;
  private FavoriteController favoriteController;

  // Fields for dependencies NOT in AbstractComponentTest
  private RecipeFavoriteRepository recipeFavoriteRepository;
  private RecipeMapper testRecipeMapper;
  private UserManagementClient userManagementClient;

  @Autowired(required = false)
  private RecipeFavoriteMapper recipeFavoriteMapper;

  private UUID testUserId;
  private Long testRecipeId;

  @Override
  @BeforeEach
  protected void setUp() {
    super.setUp(); // MUST call parent setup first
    useRealFavoriteService();
  }

  private void useRealFavoriteService() {
    testUserId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    testRecipeId = 100L;

    // Mock dependencies NOT in base class
    this.recipeFavoriteRepository = Mockito.mock(RecipeFavoriteRepository.class);
    this.testRecipeMapper = Mockito.mock(RecipeMapper.class);
    this.userManagementClient = Mockito.mock(UserManagementClient.class);

    if (recipeFavoriteMapper == null) {
      throw new RuntimeException("RecipeFavoriteMapper not available in test context");
    }

    // Create real service with all dependencies (including collection favorites - mocked)
    this.favoriteService =
        new FavoriteService(
            recipeFavoriteRepository,
            recipeRepository, // From AbstractComponentTest
            Mockito.mock(
                com.recipe_manager.repository.collection.CollectionFavoriteRepository.class),
            Mockito.mock(
                com.recipe_manager.repository.collection.RecipeCollectionRepository.class),
            recipeFavoriteMapper,
            testRecipeMapper, // Mocked for this test
            Mockito.mock(com.recipe_manager.model.mapper.CollectionFavoriteMapper.class),
            Mockito.mock(com.recipe_manager.model.mapper.CollectionMapper.class),
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
  @DisplayName("Should add favorite successfully and return 201 Created")
  void shouldAddFavoriteSuccessfully() throws Exception {
    // Given
    Recipe recipe = createTestRecipe(testRecipeId);
    RecipeFavorite savedFavorite = createTestFavorite(testUserId, testRecipeId);

    when(recipeFavoriteRepository.existsByIdUserIdAndIdRecipeId(testUserId, testRecipeId))
        .thenReturn(false);
    when(recipeRepository.findById(testRecipeId)).thenReturn(Optional.of(recipe));
    when(recipeFavoriteRepository.save(any(RecipeFavorite.class))).thenReturn(savedFavorite);

    // When/Then
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      mockMvc
          .perform(post("/favorites/recipes/{recipeId}", testRecipeId))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.userId").value(testUserId.toString()))
          .andExpect(jsonPath("$.recipeId").value(testRecipeId))
          .andExpect(jsonPath("$.favoritedAt").exists());
    }

    verify(recipeFavoriteRepository).existsByIdUserIdAndIdRecipeId(testUserId, testRecipeId);
    verify(recipeRepository).findById(testRecipeId);
    verify(recipeFavoriteRepository).save(any(RecipeFavorite.class));
  }

  @Test
  @Tag("error-handling")
  @DisplayName("Should return 400 Bad Request when recipe already favorited")
  void shouldReturn400WhenRecipeAlreadyFavorited() throws Exception {
    // Given
    when(recipeFavoriteRepository.existsByIdUserIdAndIdRecipeId(testUserId, testRecipeId))
        .thenReturn(true);

    // When/Then
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      mockMvc
          .perform(post("/favorites/recipes/{recipeId}", testRecipeId))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.error").value("Business error"))
          .andExpect(jsonPath("$.message").value("User has already favorited this recipe"));
    }

    verify(recipeFavoriteRepository).existsByIdUserIdAndIdRecipeId(testUserId, testRecipeId);
  }

  @Test
  @Tag("error-handling")
  @DisplayName("Should return 404 Not Found when recipe does not exist")
  void shouldReturn404WhenRecipeNotFound() throws Exception {
    // Given
    when(recipeFavoriteRepository.existsByIdUserIdAndIdRecipeId(testUserId, testRecipeId))
        .thenReturn(false);
    when(recipeRepository.findById(testRecipeId)).thenReturn(Optional.empty());

    // When/Then
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      mockMvc
          .perform(post("/favorites/recipes/{recipeId}", testRecipeId))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.error").value("Resource not found"))
          .andExpect(jsonPath("$.message").value("Recipe not found with ID: " + testRecipeId));
    }

    verify(recipeFavoriteRepository).existsByIdUserIdAndIdRecipeId(testUserId, testRecipeId);
    verify(recipeRepository).findById(testRecipeId);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should construct RecipeFavoriteId correctly with composite key")
  void shouldConstructRecipeFavoriteIdCorrectly() throws Exception {
    // Given
    Recipe recipe = createTestRecipe(testRecipeId);
    RecipeFavorite savedFavorite = createTestFavorite(testUserId, testRecipeId);

    when(recipeFavoriteRepository.existsByIdUserIdAndIdRecipeId(testUserId, testRecipeId))
        .thenReturn(false);
    when(recipeRepository.findById(testRecipeId)).thenReturn(Optional.of(recipe));
    when(recipeFavoriteRepository.save(any(RecipeFavorite.class))).thenReturn(savedFavorite);

    // When
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      mockMvc
          .perform(post("/favorites/recipes/{recipeId}", testRecipeId))
          .andExpect(status().isCreated());
    }

    // Then - Verify save called with entity containing correct composite key
    verify(recipeFavoriteRepository)
        .save(
            org.mockito.ArgumentMatchers.argThat(
                favorite ->
                    favorite.getId().getUserId().equals(testUserId)
                        && favorite.getId().getRecipeId().equals(testRecipeId)));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should include all required fields in response DTO")
  void shouldIncludeAllRequiredFieldsInResponse() throws Exception {
    // Given
    Recipe recipe = createTestRecipe(testRecipeId);
    RecipeFavorite savedFavorite = createTestFavorite(testUserId, testRecipeId);

    when(recipeFavoriteRepository.existsByIdUserIdAndIdRecipeId(testUserId, testRecipeId))
        .thenReturn(false);
    when(recipeRepository.findById(testRecipeId)).thenReturn(Optional.of(recipe));
    when(recipeFavoriteRepository.save(any(RecipeFavorite.class))).thenReturn(savedFavorite);

    // When/Then
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      mockMvc
          .perform(
              post("/favorites/recipes/{recipeId}", testRecipeId)
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.userId").exists())
          .andExpect(jsonPath("$.recipeId").exists())
          .andExpect(jsonPath("$.favoritedAt").exists())
          .andExpect(jsonPath("$.userId").value(testUserId.toString()))
          .andExpect(jsonPath("$.recipeId").value(testRecipeId.intValue()));
    }
  }

  // Helper methods

  private Recipe createTestRecipe(Long recipeId) {
    return Recipe.builder()
        .recipeId(recipeId)
        .title("Test Recipe " + recipeId)
        .description("Test Description")
        .build();
  }

  private RecipeFavorite createTestFavorite(UUID userId, Long recipeId) {
    RecipeFavoriteId id =
        RecipeFavoriteId.builder().userId(userId).recipeId(recipeId).build();

    Recipe recipe = createTestRecipe(recipeId);

    return RecipeFavorite.builder()
        .id(id)
        .recipe(recipe)
        .favoritedAt(LocalDateTime.now())
        .build();
  }
}
