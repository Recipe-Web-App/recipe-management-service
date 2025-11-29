package com.recipe_manager.component_tests.user_service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import com.recipe_manager.config.RequestIdFilter;
import com.recipe_manager.controller.UserController;
import com.recipe_manager.exception.GlobalExceptionHandler;
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.enums.DifficultyLevel;
import com.recipe_manager.model.mapper.RecipeCommentMapper;
import com.recipe_manager.model.mapper.RecipeCommentMapperImpl;
import com.recipe_manager.model.mapper.RecipeFavoriteMapperImpl;
import com.recipe_manager.model.mapper.RecipeIngredientMapperImpl;
import com.recipe_manager.model.mapper.RecipeMapper;
import com.recipe_manager.model.mapper.RecipeMapperImpl;
import com.recipe_manager.model.mapper.RecipeRevisionMapper;
import com.recipe_manager.model.mapper.RecipeRevisionMapperImpl;
import com.recipe_manager.model.mapper.RecipeStepMapper;
import com.recipe_manager.model.mapper.RecipeStepMapperImpl;
import com.recipe_manager.model.mapper.RecipeTagMapperImpl;
import com.recipe_manager.repository.ingredient.IngredientRepository;
import com.recipe_manager.repository.recipe.RecipeCommentRepository;
import com.recipe_manager.repository.recipe.RecipeRepository;
import com.recipe_manager.repository.recipe.RecipeRevisionRepository;
import com.recipe_manager.repository.recipe.RecipeTagRepository;
import com.recipe_manager.service.RecipeService;
import com.recipe_manager.service.external.notificationservice.NotificationService;
import com.recipe_manager.util.SecurityUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Component tests for GET /users/me/recipes endpoint. Tests the actual RecipeService getMyRecipes
 * logic with mocked repository calls.
 */
@SpringBootTest(
    classes = {
      RecipeMapperImpl.class,
      RecipeIngredientMapperImpl.class,
      RecipeStepMapperImpl.class,
      RecipeFavoriteMapperImpl.class,
      RecipeRevisionMapperImpl.class,
      RecipeTagMapperImpl.class,
      RecipeCommentMapperImpl.class
    })
@TestPropertySource(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:testdb",
      "spring.jpa.hibernate.ddl-auto=none",
      "spring.flyway.enabled=false"
    })
@Tag("component")
class GetMyRecipesComponentTest {

  protected MockMvc mockMvc;

  @Mock protected RecipeRepository recipeRepository;

  @Mock protected IngredientRepository ingredientRepository;

  @Mock protected RecipeTagRepository recipeTagRepository;

  @Mock protected RecipeRevisionRepository recipeRevisionRepository;

  @Mock protected RecipeCommentRepository recipeCommentRepository;

  @Mock protected NotificationService notificationService;

  @Autowired protected RecipeMapper recipeMapper;

  @Autowired protected RecipeRevisionMapper recipeRevisionMapper;

  @Autowired protected RecipeStepMapper recipeStepMapper;

  @Autowired protected RecipeCommentMapper recipeCommentMapper;

  private RecipeService recipeService;
  private UserController userController;

  private Recipe recipe1;
  private Recipe recipe2;
  private UUID testUserId;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    testUserId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

    // Create real service with mocked repositories
    recipeService =
        new RecipeService(
            recipeRepository,
            ingredientRepository,
            recipeTagRepository,
            recipeRevisionRepository,
            recipeMapper,
            recipeRevisionMapper,
            recipeStepMapper,
            recipeCommentRepository,
            recipeCommentMapper,
            notificationService);

    userController = new UserController(recipeService);

    mockMvc =
        MockMvcBuilders.standaloneSetup(userController)
            .addFilters(new RequestIdFilter())
            .setControllerAdvice(new GlobalExceptionHandler())
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();

    // Setup test data
    recipe1 = new Recipe();
    recipe1.setRecipeId(1L);
    recipe1.setUserId(testUserId);
    recipe1.setTitle("My Spaghetti Carbonara");
    recipe1.setDescription("My classic Italian pasta dish");
    recipe1.setServings(BigDecimal.valueOf(4));
    recipe1.setPreparationTime(15);
    recipe1.setCookingTime(20);
    recipe1.setDifficulty(DifficultyLevel.MEDIUM);

    recipe2 = new Recipe();
    recipe2.setRecipeId(2L);
    recipe2.setUserId(testUserId);
    recipe2.setTitle("My Chicken Stir Fry");
    recipe2.setDescription("My quick and healthy stir fry");
    recipe2.setServings(BigDecimal.valueOf(2));
    recipe2.setPreparationTime(10);
    recipe2.setCookingTime(15);
    recipe2.setDifficulty(DifficultyLevel.EASY);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("GET /users/me/recipes should return paginated user recipes successfully")
  void shouldReturnPaginatedUserRecipesSuccessfully() throws Exception {
    // Given
    Pageable pageable = PageRequest.of(0, 20);
    Page<Recipe> recipePage = new PageImpl<>(Arrays.asList(recipe1, recipe2), pageable, 2);

    try (MockedStatic<SecurityUtils> securityMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
      when(recipeRepository.findByUserId(eq(testUserId), any(Pageable.class)))
          .thenReturn(recipePage);

      // When & Then
      mockMvc
          .perform(get("/users/me/recipes").accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.recipes").isArray())
          .andExpect(jsonPath("$.recipes.length()").value(2))
          .andExpect(jsonPath("$.recipes[0].recipeId").value(1))
          .andExpect(jsonPath("$.recipes[0].title").value("My Spaghetti Carbonara"))
          .andExpect(jsonPath("$.recipes[0].description").value("My classic Italian pasta dish"))
          .andExpect(jsonPath("$.recipes[0].userId").value(testUserId.toString()))
          .andExpect(jsonPath("$.recipes[1].recipeId").value(2))
          .andExpect(jsonPath("$.recipes[1].title").value("My Chicken Stir Fry"))
          .andExpect(jsonPath("$.page").value(0))
          .andExpect(jsonPath("$.size").value(20))
          .andExpect(jsonPath("$.totalElements").value(2))
          .andExpect(jsonPath("$.totalPages").value(1))
          .andExpect(jsonPath("$.first").value(true))
          .andExpect(jsonPath("$.last").value(true))
          .andExpect(jsonPath("$.numberOfElements").value(2))
          .andExpect(jsonPath("$.empty").value(false));
    }
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("GET /users/me/recipes should return empty page when user has no recipes")
  void shouldReturnEmptyPageWhenUserHasNoRecipes() throws Exception {
    // Given
    Pageable pageable = PageRequest.of(0, 20);
    Page<Recipe> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

    try (MockedStatic<SecurityUtils> securityMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
      when(recipeRepository.findByUserId(eq(testUserId), any(Pageable.class))).thenReturn(emptyPage);

      // When & Then
      mockMvc
          .perform(get("/users/me/recipes").accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.recipes").isArray())
          .andExpect(jsonPath("$.recipes.length()").value(0))
          .andExpect(jsonPath("$.page").value(0))
          .andExpect(jsonPath("$.size").value(20))
          .andExpect(jsonPath("$.totalElements").value(0))
          .andExpect(jsonPath("$.totalPages").value(0))
          .andExpect(jsonPath("$.first").value(true))
          .andExpect(jsonPath("$.last").value(true))
          .andExpect(jsonPath("$.numberOfElements").value(0))
          .andExpect(jsonPath("$.empty").value(true));
    }
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("GET /users/me/recipes should handle pagination parameters correctly")
  void shouldHandlePaginationParametersCorrectly() throws Exception {
    // Given
    Pageable pageable = PageRequest.of(1, 1);
    Page<Recipe> pagedRecipes = new PageImpl<>(Arrays.asList(recipe2), pageable, 2);

    try (MockedStatic<SecurityUtils> securityMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
      when(recipeRepository.findByUserId(eq(testUserId), any(Pageable.class)))
          .thenReturn(pagedRecipes);

      // When & Then
      mockMvc
          .perform(
              get("/users/me/recipes")
                  .param("page", "1")
                  .param("size", "1")
                  .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.recipes").isArray())
          .andExpect(jsonPath("$.recipes.length()").value(1))
          .andExpect(jsonPath("$.recipes[0].recipeId").value(2))
          .andExpect(jsonPath("$.recipes[0].title").value("My Chicken Stir Fry"))
          .andExpect(jsonPath("$.page").value(1))
          .andExpect(jsonPath("$.size").value(1))
          .andExpect(jsonPath("$.totalElements").value(2))
          .andExpect(jsonPath("$.totalPages").value(2))
          .andExpect(jsonPath("$.first").value(false))
          .andExpect(jsonPath("$.last").value(true))
          .andExpect(jsonPath("$.numberOfElements").value(1))
          .andExpect(jsonPath("$.empty").value(false));
    }
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("GET /users/me/recipes should only return recipes for current user")
  void shouldOnlyReturnRecipesForCurrentUser() throws Exception {
    // Given - a different user ID to verify isolation
    UUID differentUserId = UUID.fromString("99999999-9999-9999-9999-999999999999");
    Pageable pageable = PageRequest.of(0, 20);

    // Recipe belongs to a different user
    Recipe otherUserRecipe = new Recipe();
    otherUserRecipe.setRecipeId(100L);
    otherUserRecipe.setUserId(differentUserId);
    otherUserRecipe.setTitle("Other User Recipe");

    // Current user should get empty result as their recipes
    Page<Recipe> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

    try (MockedStatic<SecurityUtils> securityMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
      // Repository returns empty for the current user (recipes belong to different user)
      when(recipeRepository.findByUserId(eq(testUserId), any(Pageable.class))).thenReturn(emptyPage);

      // When & Then
      mockMvc
          .perform(get("/users/me/recipes").accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.recipes").isArray())
          .andExpect(jsonPath("$.recipes.length()").value(0))
          .andExpect(jsonPath("$.empty").value(true));
    }
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("GET /users/me/recipes should use default pagination when no parameters provided")
  void shouldUseDefaultPaginationWhenNoParametersProvided() throws Exception {
    // Given
    Pageable pageable = PageRequest.of(0, 20);
    Page<Recipe> recipePage = new PageImpl<>(Arrays.asList(recipe1), pageable, 1);

    try (MockedStatic<SecurityUtils> securityMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);
      when(recipeRepository.findByUserId(eq(testUserId), any(Pageable.class)))
          .thenReturn(recipePage);

      // When & Then
      mockMvc
          .perform(get("/users/me/recipes").accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.recipes").isArray())
          .andExpect(jsonPath("$.recipes.length()").value(1))
          .andExpect(jsonPath("$.page").value(0))
          .andExpect(jsonPath("$.size").value(20))
          .andExpect(jsonPath("$.totalElements").value(1))
          .andExpect(jsonPath("$.totalPages").value(1))
          .andExpect(jsonPath("$.first").value(true))
          .andExpect(jsonPath("$.last").value(true))
          .andExpect(jsonPath("$.numberOfElements").value(1))
          .andExpect(jsonPath("$.empty").value(false));
    }
  }
}
