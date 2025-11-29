package com.recipe_manager.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import com.recipe_manager.model.dto.recipe.RecipeDto;
import com.recipe_manager.model.dto.response.SearchRecipesResponse;
import com.recipe_manager.service.RecipeService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Test class for UserController. Verifies that user-scoped endpoints work correctly.
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

  @Mock
  private RecipeService recipeService;

  @InjectMocks
  private UserController userController;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(userController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();
  }

  /** Test that controller can be instantiated. */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should instantiate controller")
  void shouldInstantiateController() {
    assertNotNull(userController);
  }

  /** Test GET /users/me/recipes endpoint returns paginated recipes. */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle GET /users/me/recipes with default pagination")
  void shouldHandleGetMyRecipesWithDefaultPagination() throws Exception {
    SearchRecipesResponse mockResponse =
        SearchRecipesResponse.builder()
            .recipes(
                List.of(
                    RecipeDto.builder().recipeId(1L).title("Test Recipe 1").build(),
                    RecipeDto.builder().recipeId(2L).title("Test Recipe 2").build()))
            .page(0)
            .size(20)
            .totalElements(2)
            .totalPages(1)
            .first(true)
            .last(true)
            .numberOfElements(2)
            .empty(false)
            .build();

    when(recipeService.getMyRecipes(any(Pageable.class)))
        .thenReturn(ResponseEntity.ok(mockResponse));

    mockMvc
        .perform(get("/users/me/recipes").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.recipes").isArray())
        .andExpect(jsonPath("$.recipes.length()").value(2))
        .andExpect(jsonPath("$.recipes[0].recipeId").value(1))
        .andExpect(jsonPath("$.recipes[0].title").value("Test Recipe 1"))
        .andExpect(jsonPath("$.page").value(0))
        .andExpect(jsonPath("$.size").value(20))
        .andExpect(jsonPath("$.totalElements").value(2))
        .andExpect(jsonPath("$.totalPages").value(1))
        .andExpect(jsonPath("$.first").value(true))
        .andExpect(jsonPath("$.last").value(true));
  }

  /** Test GET /users/me/recipes endpoint with custom pagination parameters. */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle GET /users/me/recipes with custom pagination")
  void shouldHandleGetMyRecipesWithCustomPagination() throws Exception {
    SearchRecipesResponse mockResponse =
        SearchRecipesResponse.builder()
            .recipes(List.of(RecipeDto.builder().recipeId(3L).title("Test Recipe 3").build()))
            .page(1)
            .size(1)
            .totalElements(3)
            .totalPages(3)
            .first(false)
            .last(false)
            .numberOfElements(1)
            .empty(false)
            .build();

    when(recipeService.getMyRecipes(any(Pageable.class)))
        .thenReturn(ResponseEntity.ok(mockResponse));

    mockMvc
        .perform(
            get("/users/me/recipes")
                .param("page", "1")
                .param("size", "1")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.page").value(1))
        .andExpect(jsonPath("$.size").value(1))
        .andExpect(jsonPath("$.totalElements").value(3))
        .andExpect(jsonPath("$.totalPages").value(3))
        .andExpect(jsonPath("$.first").value(false))
        .andExpect(jsonPath("$.last").value(false));
  }

  /** Test GET /users/me/recipes endpoint returns empty list when user has no recipes. */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle GET /users/me/recipes with no recipes")
  void shouldHandleGetMyRecipesWithNoRecipes() throws Exception {
    SearchRecipesResponse mockResponse =
        SearchRecipesResponse.builder()
            .recipes(List.of())
            .page(0)
            .size(20)
            .totalElements(0)
            .totalPages(0)
            .first(true)
            .last(true)
            .numberOfElements(0)
            .empty(true)
            .build();

    when(recipeService.getMyRecipes(any(Pageable.class)))
        .thenReturn(ResponseEntity.ok(mockResponse));

    mockMvc
        .perform(get("/users/me/recipes").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.recipes").isArray())
        .andExpect(jsonPath("$.recipes.length()").value(0))
        .andExpect(jsonPath("$.totalElements").value(0))
        .andExpect(jsonPath("$.empty").value(true));
  }

  /** Test GET /users/me/recipes endpoint with sorting parameter. */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle GET /users/me/recipes with sorting")
  void shouldHandleGetMyRecipesWithSorting() throws Exception {
    SearchRecipesResponse mockResponse =
        SearchRecipesResponse.builder()
            .recipes(List.of(RecipeDto.builder().recipeId(1L).title("Test Recipe").build()))
            .page(0)
            .size(20)
            .totalElements(1)
            .totalPages(1)
            .first(true)
            .last(true)
            .numberOfElements(1)
            .empty(false)
            .build();

    when(recipeService.getMyRecipes(any(Pageable.class)))
        .thenReturn(ResponseEntity.ok(mockResponse));

    mockMvc
        .perform(
            get("/users/me/recipes")
                .param("sort", "createdAt,desc")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.recipes").isArray());
  }
}
