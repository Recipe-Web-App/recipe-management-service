package com.recipe_manager.component_tests;

import com.recipe_manager.config.RequestIdFilter;
import com.recipe_manager.controller.RecipeManagementController;
import com.recipe_manager.exception.GlobalExceptionHandler;
import com.recipe_manager.model.mapper.IngredientCommentMapper;
import com.recipe_manager.model.mapper.RecipeCommentMapper;
import com.recipe_manager.model.mapper.RecipeIngredientMapper;
import com.recipe_manager.model.mapper.RecipeMapper;
import com.recipe_manager.model.mapper.RecipeRevisionMapper;
import com.recipe_manager.model.mapper.RecipeStepMapper;
import com.recipe_manager.model.mapper.RecipeTagMapper;
import com.recipe_manager.model.mapper.ShoppingListMapper;
import com.recipe_manager.model.mapper.StepCommentMapper;
import com.recipe_manager.repository.ingredient.IngredientCommentRepository;
import com.recipe_manager.repository.ingredient.IngredientRepository;
import com.recipe_manager.repository.recipe.RecipeCommentRepository;
import com.recipe_manager.repository.recipe.RecipeIngredientRepository;
import com.recipe_manager.repository.recipe.RecipeRepository;
import com.recipe_manager.repository.recipe.RecipeRevisionRepository;
import com.recipe_manager.repository.recipe.RecipeStepRepository;
import com.recipe_manager.repository.recipe.RecipeTagRepository;
import com.recipe_manager.repository.recipe.StepCommentRepository;
import com.recipe_manager.service.IngredientService;
import com.recipe_manager.service.RecipeService;
import com.recipe_manager.service.ReviewService;
import com.recipe_manager.service.StepService;
import com.recipe_manager.service.TagService;
import com.recipe_manager.service.external.RecipeScraperService;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public abstract class AbstractComponentTest {
  protected MockMvc mockMvc;

  // Service mocks for existing tests that mock at service layer
  @Mock
  protected RecipeService recipeService;

  @Mock
  protected IngredientService ingredientService;

  @Mock
  protected StepService stepService;

  @Mock
  protected TagService tagService;

  @Mock
  protected ReviewService reviewService;

  @Mock
  protected RecipeScraperService recipeScraperService;

  // Repository mocks for proper component testing
  @Mock
  protected RecipeRepository recipeRepository;

  @Mock
  protected IngredientRepository ingredientRepository;

  @Mock
  protected RecipeTagRepository recipeTagRepository;

  @Mock
  protected RecipeRevisionRepository recipeRevisionRepository;

  @Mock
  protected RecipeIngredientRepository recipeIngredientRepository;

  @Mock
  protected IngredientCommentRepository ingredientCommentRepository;

  @Mock
  protected RecipeStepRepository recipeStepRepository;

  @Mock
  protected StepCommentRepository stepCommentRepository;

  @Mock
  protected RecipeCommentRepository recipeCommentRepository;

  // Real mappers for component testing
  @Autowired(required = false)
  protected RecipeMapper recipeMapper;

  @Autowired(required = false)
  protected RecipeIngredientMapper recipeIngredientMapper;

  @Autowired(required = false)
  protected IngredientCommentMapper ingredientCommentMapper;

  @Autowired(required = false)
  protected ShoppingListMapper shoppingListMapper;

  @Autowired(required = false)
  protected RecipeStepMapper recipeStepMapper;

  @Autowired(required = false)
  protected StepCommentMapper stepCommentMapper;

  @Autowired(required = false)
  protected RecipeTagMapper recipeTagMapper;

  @Autowired(required = false)
  protected RecipeRevisionMapper recipeRevisionMapper;

  @Autowired(required = false)
  protected RecipeCommentMapper recipeCommentMapper;

  @InjectMocks
  protected RecipeManagementController controller;

  // Real service instances for component testing with repository mocking
  protected RecipeService realRecipeService;
  protected IngredientService realIngredientService;
  protected StepService realStepService;
  protected TagService realTagService;

  @BeforeEach
  protected void setUp() {
    MockitoAnnotations.openMocks(this);

    // Create real service instances for repository-level component testing
    if (recipeMapper != null && recipeRevisionMapper != null) {
      realRecipeService = new RecipeService(
          recipeRepository, ingredientRepository, recipeTagRepository, recipeRevisionRepository, recipeMapper,
          recipeRevisionMapper, recipeStepMapper, recipeCommentRepository, recipeCommentMapper);
    }
    if (recipeIngredientMapper != null && recipeRevisionMapper != null) {
      realIngredientService = new IngredientService(recipeIngredientRepository, ingredientRepository,
          ingredientCommentRepository, recipeRepository, recipeRevisionRepository, recipeIngredientMapper,
          ingredientCommentMapper, recipeRevisionMapper, shoppingListMapper, recipeScraperService);
    }
    if (stepCommentMapper != null && recipeStepMapper != null && recipeRevisionMapper != null) {
      realStepService = new StepService(recipeRepository, recipeStepRepository, stepCommentRepository,
          recipeRevisionRepository, recipeStepMapper, stepCommentMapper, recipeRevisionMapper);
    }
    if (recipeTagMapper != null) {
      realTagService = new TagService(recipeRepository, recipeTagRepository, recipeTagMapper);
    }

    mockMvc = MockMvcBuilders.standaloneSetup(controller)
        .addFilters(new RequestIdFilter())
        .setControllerAdvice(new GlobalExceptionHandler())
        .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
        .build();
  }

  /**
   * Switch to using real RecipeService with mocked repositories for proper
   * component testing.
   * Call this in tests that want to exercise real service logic.
   */
  protected void useRealRecipeService() {
    if (realRecipeService == null) {
      throw new RuntimeException("RecipeMapper not available in this test context");
    }
    // Manually set the real service in the controller
    // Note: This is a bit hacky but necessary for proper component testing
    try {
      var field = RecipeManagementController.class.getDeclaredField("recipeService");
      field.setAccessible(true);
      field.set(controller, realRecipeService);
    } catch (Exception e) {
      throw new RuntimeException("Failed to inject real RecipeService", e);
    }
  }

  /**
   * Switch to using real IngredientService with mocked repositories for proper
   * component testing.
   * Call this in tests that want to exercise real service logic.
   */
  protected void useRealIngredientService() {
    if (realIngredientService == null) {
      throw new RuntimeException("RecipeIngredientMapper not available in this test context");
    }
    // Manually set the real service in the controller
    // Note: This is a bit hacky but necessary for proper component testing
    try {
      var field = RecipeManagementController.class.getDeclaredField("ingredientService");
      field.setAccessible(true);
      field.set(controller, realIngredientService);
    } catch (Exception e) {
      throw new RuntimeException("Failed to inject real IngredientService", e);
    }
  }

  /**
   * Switch to using real StepService with mocked repositories for proper
   * component testing.
   * Call this in tests that want to exercise real service logic.
   */
  protected void useRealStepService() {
    if (realStepService == null) {
      throw new RuntimeException("StepCommentMapper not available in this test context");
    }
    // Manually set the real service in the controller
    // Note: This is a bit hacky but necessary for proper component testing
    try {
      var field = RecipeManagementController.class.getDeclaredField("stepService");
      field.setAccessible(true);
      field.set(controller, realStepService);
    } catch (Exception e) {
      throw new RuntimeException("Failed to inject real StepService", e);
    }
  }

  /**
   * Switch to using real TagService with mocked repositories for proper
   * component testing.
   * Call this in tests that want to exercise real service logic.
   */
  protected void useRealTagService() {
    if (realTagService == null) {
      throw new RuntimeException("RecipeTagMapper not available in this test context");
    }
    // Manually set the real service in the controller
    // Note: This is a bit hacky but necessary for proper component testing
    try {
      var field = RecipeManagementController.class.getDeclaredField("tagService");
      field.setAccessible(true);
      field.set(controller, realTagService);
    } catch (Exception e) {
      throw new RuntimeException("Failed to inject real TagService", e);
    }
  }
}
