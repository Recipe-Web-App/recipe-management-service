package com.recipe_manager.component_tests.favorite_service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
 * Component tests for checking if a collection is favorited.
 *
 * <p>Tests the GET /favorites/collections/{collectionId}/is-favorited endpoint with real service
 * and mapper logic, mocking only repositories and external dependencies.
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
class IsCollectionFavoritedComponentTest extends AbstractComponentTest {

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
  @DisplayName("Should return true when collection is favorited")
  void shouldReturnTrueWhenCollectionIsFavorited() throws Exception {
    // Given
    when(collectionFavoriteRepository.existsByIdUserIdAndIdCollectionId(
            testUserId, testCollectionId))
        .thenReturn(true);

    // When/Then
    try (MockedStatic<SecurityUtils> securityUtilsMock =
        Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      mockMvc
          .perform(get("/favorites/collections/{collectionId}/is-favorited", testCollectionId))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(content().string("true"));
    }

    verify(collectionFavoriteRepository)
        .existsByIdUserIdAndIdCollectionId(testUserId, testCollectionId);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should return false when collection is not favorited")
  void shouldReturnFalseWhenCollectionIsNotFavorited() throws Exception {
    // Given
    when(collectionFavoriteRepository.existsByIdUserIdAndIdCollectionId(
            testUserId, testCollectionId))
        .thenReturn(false);

    // When/Then
    try (MockedStatic<SecurityUtils> securityUtilsMock =
        Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      mockMvc
          .perform(get("/favorites/collections/{collectionId}/is-favorited", testCollectionId))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(content().string("false"));
    }

    verify(collectionFavoriteRepository)
        .existsByIdUserIdAndIdCollectionId(testUserId, testCollectionId);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should return boolean type in response")
  void shouldReturnBooleanTypeInResponse() throws Exception {
    // Given
    when(collectionFavoriteRepository.existsByIdUserIdAndIdCollectionId(
            testUserId, testCollectionId))
        .thenReturn(true);

    // When/Then
    try (MockedStatic<SecurityUtils> securityUtilsMock =
        Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      mockMvc
          .perform(get("/favorites/collections/{collectionId}/is-favorited", testCollectionId))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(
              content()
                  .string(
                      org.hamcrest.Matchers.either(org.hamcrest.Matchers.is("true"))
                          .or(org.hamcrest.Matchers.is("false"))));
    }
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should verify repository exists method called")
  void shouldVerifyRepositoryExistsMethodCalled() throws Exception {
    // Given
    when(collectionFavoriteRepository.existsByIdUserIdAndIdCollectionId(
            testUserId, testCollectionId))
        .thenReturn(false);

    // When
    try (MockedStatic<SecurityUtils> securityUtilsMock =
        Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      mockMvc
          .perform(get("/favorites/collections/{collectionId}/is-favorited", testCollectionId))
          .andExpect(status().isOk());
    }

    // Then - Verify exists method was called
    verify(collectionFavoriteRepository)
        .existsByIdUserIdAndIdCollectionId(testUserId, testCollectionId);
  }
}
