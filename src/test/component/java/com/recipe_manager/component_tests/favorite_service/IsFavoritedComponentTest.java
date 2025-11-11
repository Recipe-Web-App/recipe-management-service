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
import com.recipe_manager.model.mapper.RecipeFavoriteMapper;
import com.recipe_manager.model.mapper.RecipeMapper;
import com.recipe_manager.repository.recipe.RecipeFavoriteRepository;
import com.recipe_manager.service.FavoriteService;
import com.recipe_manager.util.SecurityUtils;

/**
 * Component tests for checking if a recipe is favorited.
 *
 * <p>Tests the GET /favorites/recipes/{recipeId}/is-favorited endpoint with real service and
 * mapper logic, mocking only repositories and external dependencies.
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
class IsFavoritedComponentTest extends AbstractComponentTest {

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

    // Create real service with all dependencies
    this.favoriteService =
        new FavoriteService(
            recipeFavoriteRepository,
            recipeRepository, // From AbstractComponentTest
            recipeFavoriteMapper,
            testRecipeMapper, // Mocked for this test
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
  @DisplayName("Should return true when recipe is favorited")
  void shouldReturnTrueWhenRecipeIsFavorited() throws Exception {
    // Given
    when(recipeFavoriteRepository.existsByIdUserIdAndIdRecipeId(testUserId, testRecipeId))
        .thenReturn(true);

    // When/Then
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      mockMvc
          .perform(get("/favorites/recipes/{recipeId}/is-favorited", testRecipeId))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(content().string("true"));
    }

    verify(recipeFavoriteRepository).existsByIdUserIdAndIdRecipeId(testUserId, testRecipeId);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should return false when recipe is not favorited")
  void shouldReturnFalseWhenRecipeIsNotFavorited() throws Exception {
    // Given
    when(recipeFavoriteRepository.existsByIdUserIdAndIdRecipeId(testUserId, testRecipeId))
        .thenReturn(false);

    // When/Then
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      mockMvc
          .perform(get("/favorites/recipes/{recipeId}/is-favorited", testRecipeId))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(content().string("false"));
    }

    verify(recipeFavoriteRepository).existsByIdUserIdAndIdRecipeId(testUserId, testRecipeId);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should return boolean type in response")
  void shouldReturnBooleanTypeInResponse() throws Exception {
    // Given
    when(recipeFavoriteRepository.existsByIdUserIdAndIdRecipeId(testUserId, testRecipeId))
        .thenReturn(true);

    // When/Then
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      mockMvc
          .perform(get("/favorites/recipes/{recipeId}/is-favorited", testRecipeId))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(content().string(org.hamcrest.Matchers.either(
              org.hamcrest.Matchers.is("true")).or(org.hamcrest.Matchers.is("false"))));
    }
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should verify repository exists method called")
  void shouldVerifyRepositoryExistsMethodCalled() throws Exception {
    // Given
    when(recipeFavoriteRepository.existsByIdUserIdAndIdRecipeId(testUserId, testRecipeId))
        .thenReturn(false);

    // When
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      mockMvc
          .perform(get("/favorites/recipes/{recipeId}/is-favorited", testRecipeId))
          .andExpect(status().isOk());
    }

    // Then - Verify exists method was called
    verify(recipeFavoriteRepository).existsByIdUserIdAndIdRecipeId(testUserId, testRecipeId);
  }
}
