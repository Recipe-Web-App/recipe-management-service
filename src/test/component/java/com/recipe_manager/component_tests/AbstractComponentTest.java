package com.recipe_manager.component_tests;

import com.recipe_manager.config.RequestIdFilter;
import com.recipe_manager.controller.RecipeManagementController;
import com.recipe_manager.exception.GlobalExceptionHandler;
import com.recipe_manager.model.mapper.RecipeMapper;
import com.recipe_manager.repository.ingredient.IngredientRepository;
import com.recipe_manager.repository.recipe.RecipeRepository;
import com.recipe_manager.service.IngredientService;
import com.recipe_manager.service.MediaService;
import com.recipe_manager.service.RecipeService;
import com.recipe_manager.service.ReviewService;
import com.recipe_manager.service.StepService;
import com.recipe_manager.service.TagService;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
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
  protected MediaService mediaService;

  @Mock
  protected TagService tagService;

  @Mock
  protected ReviewService reviewService;

  // Repository mocks for proper component testing
  @Mock
  protected RecipeRepository recipeRepository;

  @Mock
  protected IngredientRepository ingredientRepository;

  // Real mapper for component testing
  @Autowired
  protected RecipeMapper recipeMapper;

  @InjectMocks
  protected RecipeManagementController controller;

  // Real service instances for component testing with repository mocking
  protected RecipeService realRecipeService;

  @BeforeEach
  protected void setUp() {
    MockitoAnnotations.openMocks(this);

    // Create real service instance for repository-level component testing
    realRecipeService = new RecipeService(recipeRepository, ingredientRepository, recipeMapper);

    mockMvc = MockMvcBuilders.standaloneSetup(controller)
        .addFilters(new RequestIdFilter())
        .setControllerAdvice(new GlobalExceptionHandler())
        .build();
  }

  /**
   * Switch to using real RecipeService with mocked repositories for proper
   * component testing.
   * Call this in tests that want to exercise real service logic.
   */
  protected void useRealRecipeService() {
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
}
