package com.recipe_manager.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import com.recipe_manager.model.dto.ingredient.IngredientCommentDto;
import com.recipe_manager.model.dto.recipe.RecipeDto;
import com.recipe_manager.model.dto.request.AddIngredientCommentRequest;
import com.recipe_manager.model.dto.request.CreateRecipeRequest;
import com.recipe_manager.model.dto.request.DeleteIngredientCommentRequest;
import com.recipe_manager.model.dto.request.EditIngredientCommentRequest;
import com.recipe_manager.model.dto.request.SearchRecipesRequest;
import com.recipe_manager.model.dto.request.UpdateRecipeRequest;
import com.recipe_manager.model.dto.response.IngredientCommentResponse;
import com.recipe_manager.model.dto.response.SearchRecipesResponse;
import com.recipe_manager.service.IngredientService;
import com.recipe_manager.service.MediaService;
import com.recipe_manager.service.RecipeService;
import com.recipe_manager.service.ReviewService;
import com.recipe_manager.service.StepService;
import com.recipe_manager.service.TagService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Test class for RecipeManagementController.
 * Verifies that recipe management endpoints work correctly.
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
class RecipeManagementControllerTest {

  @Mock
  private RecipeService recipeService;

  @Mock
  private IngredientService ingredientService;

  @Mock
  private StepService stepService;

  @Mock
  private MediaService mediaService;

  @Mock
  private TagService tagService;

  @Mock
  private ReviewService reviewService;

  @InjectMocks
  private RecipeManagementController recipeManagementController;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(recipeManagementController)
        .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
        .build();
  }

  /**
   * Test that controller can be instantiated.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should instantiate controller")
  void shouldInstantiateController() {
    assertNotNull(recipeManagementController);
  }

  /**
   * Test POST /recipe-management/recipes/search endpoint.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle POST /recipe-management/recipes/search")
  void shouldHandleSearchRecipes() throws Exception {
    SearchRecipesResponse mockResponse = SearchRecipesResponse.builder()
        .build();

    when(recipeService.searchRecipes(any(SearchRecipesRequest.class), any()))
        .thenReturn(ResponseEntity.ok(mockResponse));

    String validSearchRequestJson = "{" +
        "\"title\":\"Test Recipe\"," +
        "\"difficulty\":\"BEGINNER\"," +
        "\"maxPreparationTimeMinutes\":30," +
        "\"maxCookingTimeMinutes\":60," +
        "\"servings\":4," +
        "\"ingredientNames\":[\"flour\",\"sugar\"]," +
        "\"ingredientMatchMode\":\"AND\"" +
        "}";

    mockMvc.perform(post("/recipe-management/recipes/search")
        .contentType(MediaType.APPLICATION_JSON)
        .content(validSearchRequestJson))
        .andExpect(status().isOk());
  }

  /**
   * Test GET /recipe-management/recipes/{recipeId}/ingredients endpoint.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle GET /recipe-management/recipes/{recipeId}/ingredients")
  void shouldHandleGetIngredients() throws Exception {
    com.recipe_manager.model.dto.response.RecipeIngredientsResponse mockResponse = com.recipe_manager.model.dto.response.RecipeIngredientsResponse
        .builder()
        .recipeId(1L)
        .ingredients(java.util.Arrays.asList())
        .totalCount(0)
        .build();
    when(ingredientService.getIngredients("1")).thenReturn(ResponseEntity.ok(mockResponse));

    mockMvc.perform(get("/recipe-management/recipes/1/ingredients")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  /**
   * Test GET /recipe-management/recipes/{recipeId}/steps endpoint.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle GET /recipe-management/recipes/{recipeId}/steps")
  void shouldHandleGetSteps() throws Exception {
    when(stepService.getSteps("1")).thenReturn(ResponseEntity.ok("Get Steps - placeholder"));

    mockMvc.perform(get("/recipe-management/recipes/1/steps")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  /**
   * Test POST /recipe-management/recipes/{recipeId}/media endpoint.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle POST /recipe-management/recipes/{recipeId}/media")
  void shouldHandlePostMedia() throws Exception {
    when(mediaService.addMediaToRecipe("1")).thenReturn(ResponseEntity.ok("Add Media - placeholder"));

    mockMvc.perform(post("/recipe-management/recipes/1/media")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{}"))
        .andExpect(status().isOk());
  }

  /**
   * Test POST /recipe-management/recipes endpoint.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle POST /recipe-management/recipes")
  void shouldHandlePostRecipes() throws Exception {
    String validRequestJson = "{" +
        "\"title\":\"Test Recipe\"," +
        "\"description\":\"A test recipe\"," +
        "\"originUrl\":\"http://example.com\"," +
        "\"servings\":2," +
        "\"preparationTime\":10," +
        "\"cookingTime\":20," +
        "\"difficulty\":\"BEGINNER\"," +
        "\"ingredients\":[{" +
        "  \"ingredientName\":\"Flour\"," +
        "  \"quantity\":1.0," +
        "  \"isOptional\":false" +
        "}]," +
        "\"steps\":[{" +
        "  \"stepNumber\":1," +
        "  \"instruction\":\"Mix ingredients\"" +
        "}]" +
        "}";
    when(recipeService.createRecipe(ArgumentMatchers.any(CreateRecipeRequest.class)))
        .thenReturn(ResponseEntity.ok(RecipeDto.builder().build()));

    mockMvc.perform(post("/recipe-management/recipes")
        .contentType(MediaType.APPLICATION_JSON)
        .content(validRequestJson))
        .andExpect(status().isOk());
  }

  /**
   * Test PUT /recipe-management/recipes/{recipeId} endpoint.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle PUT /recipe-management/recipes/{recipeId}")
  void shouldHandlePutRecipes() throws Exception {
    when(recipeService.updateRecipe(eq("1"), any(UpdateRecipeRequest.class)))
        .thenReturn(ResponseEntity.ok(RecipeDto.builder().build()));

    mockMvc.perform(put("/recipe-management/recipes/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{}"))
        .andExpect(status().isOk());
  }

  /**
   * Test DELETE /recipe-management/recipes/{recipeId} endpoint.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle DELETE /recipe-management/recipes/{recipeId}")
  void shouldHandleDeleteRecipes() throws Exception {
    when(recipeService.deleteRecipe("1")).thenReturn(ResponseEntity.noContent().build());

    mockMvc.perform(delete("/recipe-management/recipes/1")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());
  }

  /**
   * Test POST
   * /recipe-management/recipes/{recipeId}/ingredients/{ingredientId}/comment
   * endpoint.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle POST /recipe-management/recipes/{recipeId}/ingredients/{ingredientId}/comment")
  void shouldHandleAddCommentToIngredient() throws Exception {
    IngredientCommentDto commentDto = IngredientCommentDto.builder()
        .commentId(1L)
        .userId(UUID.randomUUID())
        .commentText("Test comment")
        .isPublic(true)
        .build();

    IngredientCommentResponse response = IngredientCommentResponse.builder()
        .recipeId(1L)
        .ingredientId(2L)
        .comments(List.of(commentDto))
        .build();
    when(ingredientService.addComment(eq("1"), eq("2"), any(AddIngredientCommentRequest.class)))
        .thenReturn(ResponseEntity.ok(response));

    mockMvc.perform(post("/recipe-management/recipes/1/ingredients/2/comment")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"comment\":\"Test comment\"}"))
        .andExpect(status().isOk());
  }

  /**
   * Test PUT
   * /recipe-management/recipes/{recipeId}/ingredients/{ingredientId}/comment
   * endpoint.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle PUT /recipe-management/recipes/{recipeId}/ingredients/{ingredientId}/comment")
  void shouldHandleEditCommentOnIngredient() throws Exception {
    IngredientCommentDto commentDto = IngredientCommentDto.builder()
        .commentId(1L)
        .userId(UUID.randomUUID())
        .commentText("Updated comment")
        .isPublic(true)
        .build();

    IngredientCommentResponse response = IngredientCommentResponse.builder()
        .recipeId(1L)
        .ingredientId(2L)
        .comments(List.of(commentDto))
        .build();
    when(ingredientService.editComment(eq("1"), eq("2"), any(EditIngredientCommentRequest.class)))
        .thenReturn(ResponseEntity.ok(response));

    mockMvc.perform(put("/recipe-management/recipes/1/ingredients/2/comment")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"commentId\":1,\"comment\":\"Updated comment\"}"))
        .andExpect(status().isOk());
  }

  /**
   * Test DELETE
   * /recipe-management/recipes/{recipeId}/ingredients/{ingredientId}/comment
   * endpoint.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle DELETE /recipe-management/recipes/{recipeId}/ingredients/{ingredientId}/comment")
  void shouldHandleDeleteCommentFromIngredient() throws Exception {
    IngredientCommentResponse response = IngredientCommentResponse.builder()
        .recipeId(1L)
        .ingredientId(2L)
        .comments(List.of())
        .build();
    when(ingredientService.deleteComment(eq("1"), eq("2"), any(DeleteIngredientCommentRequest.class)))
        .thenReturn(ResponseEntity.ok(response));

    mockMvc.perform(delete("/recipe-management/recipes/1/ingredients/2/comment")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"commentId\":1}"))
        .andExpect(status().isOk());
  }

  /**
   * Test POST /recipe-management/recipes/{recipeId}/steps/{stepId}/comment
   * endpoint.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle POST /recipe-management/recipes/{recipeId}/steps/{stepId}/comment")
  void shouldHandleAddCommentToStep() throws Exception {
    when(stepService.addComment("1", "2")).thenReturn(ResponseEntity.ok("Add Comment - placeholder"));
    mockMvc.perform(post("/recipe-management/recipes/1/steps/2/comment")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  /**
   * Test PUT /recipe-management/recipes/{recipeId}/steps/{stepId}/comment
   * endpoint.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle PUT /recipe-management/recipes/{recipeId}/steps/{stepId}/comment")
  void shouldHandleEditCommentOnStep() throws Exception {
    when(stepService.editComment("1", "2")).thenReturn(ResponseEntity.ok("Edit Comment - placeholder"));
    mockMvc.perform(put("/recipe-management/recipes/1/steps/2/comment")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  /**
   * Test DELETE /recipe-management/recipes/{recipeId}/steps/{stepId}/comment
   * endpoint.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle DELETE /recipe-management/recipes/{recipeId}/steps/{stepId}/comment")
  void shouldHandleDeleteCommentFromStep() throws Exception {
    when(stepService.deleteComment("1", "2")).thenReturn(ResponseEntity.ok("Delete Comment - placeholder"));
    mockMvc.perform(delete("/recipe-management/recipes/1/steps/2/comment")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }
}
