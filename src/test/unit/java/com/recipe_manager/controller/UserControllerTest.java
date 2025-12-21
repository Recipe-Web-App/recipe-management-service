package com.recipe_manager.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.recipe_manager.model.dto.recipe.RecipeDto;
import com.recipe_manager.model.dto.response.CollectionDto;
import com.recipe_manager.model.dto.response.SearchRecipesResponse;
import com.recipe_manager.model.enums.CollaborationMode;
import com.recipe_manager.model.enums.CollectionVisibility;
import com.recipe_manager.service.CollectionService;
import com.recipe_manager.service.RecipeService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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

  @Mock
  private CollectionService collectionService;

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

  /** Test GET /users/me/collections endpoint returns paginated collections with default params. */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle GET /users/me/collections with default parameters")
  void shouldHandleGetMyCollectionsWithDefaultParameters() throws Exception {
    UUID userId = UUID.randomUUID();
    List<CollectionDto> collections =
        List.of(
            createTestCollectionDto(1L, userId, "My Recipes"),
            createTestCollectionDto(2L, userId, "Favorites"));
    Page<CollectionDto> mockPage = new PageImpl<>(collections, PageRequest.of(0, 20), 2);

    when(collectionService.getMyCollections(eq(false), any(Pageable.class)))
        .thenReturn(ResponseEntity.ok(mockPage));

    mockMvc
        .perform(get("/users/me/collections").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content.length()").value(2))
        .andExpect(jsonPath("$.content[0].collectionId").value(1))
        .andExpect(jsonPath("$.content[0].name").value("My Recipes"))
        .andExpect(jsonPath("$.totalElements").value(2))
        .andExpect(jsonPath("$.totalPages").value(1));

    verify(collectionService).getMyCollections(eq(false), any(Pageable.class));
  }

  /** Test GET /users/me/collections with includeCollaborations=true. */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle GET /users/me/collections with includeCollaborations=true")
  void shouldHandleGetMyCollectionsWithIncludeCollaborationsTrue() throws Exception {
    UUID userId = UUID.randomUUID();
    UUID otherUserId = UUID.randomUUID();
    List<CollectionDto> collections =
        List.of(
            createTestCollectionDto(1L, userId, "My Recipes"),
            createTestCollectionDto(2L, otherUserId, "Shared Collection"));
    Page<CollectionDto> mockPage = new PageImpl<>(collections, PageRequest.of(0, 20), 2);

    when(collectionService.getMyCollections(eq(true), any(Pageable.class)))
        .thenReturn(ResponseEntity.ok(mockPage));

    mockMvc
        .perform(
            get("/users/me/collections")
                .param("includeCollaborations", "true")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content.length()").value(2));

    verify(collectionService).getMyCollections(eq(true), any(Pageable.class));
  }

  /** Test GET /users/me/collections with custom pagination parameters. */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle GET /users/me/collections with custom pagination")
  void shouldHandleGetMyCollectionsWithCustomPagination() throws Exception {
    UUID userId = UUID.randomUUID();
    List<CollectionDto> collections =
        List.of(createTestCollectionDto(3L, userId, "Page 2 Collection"));
    Page<CollectionDto> mockPage = new PageImpl<>(collections, PageRequest.of(1, 1), 3);

    when(collectionService.getMyCollections(anyBoolean(), any(Pageable.class)))
        .thenReturn(ResponseEntity.ok(mockPage));

    mockMvc
        .perform(
            get("/users/me/collections")
                .param("page", "1")
                .param("size", "1")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(1))
        .andExpect(jsonPath("$.totalElements").value(3))
        .andExpect(jsonPath("$.totalPages").value(3));
  }

  /** Test GET /users/me/collections returns empty list when user has no collections. */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle GET /users/me/collections with no collections")
  void shouldHandleGetMyCollectionsWithNoCollections() throws Exception {
    Page<CollectionDto> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);

    when(collectionService.getMyCollections(anyBoolean(), any(Pageable.class)))
        .thenReturn(ResponseEntity.ok(emptyPage));

    mockMvc
        .perform(get("/users/me/collections").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content.length()").value(0))
        .andExpect(jsonPath("$.totalElements").value(0))
        .andExpect(jsonPath("$.empty").value(true));
  }

  /**
   * Creates a test CollectionDto with the given parameters.
   *
   * @param collectionId the collection ID
   * @param userId the owner user ID
   * @param name the collection name
   * @return a CollectionDto for testing
   */
  private CollectionDto createTestCollectionDto(Long collectionId, UUID userId, String name) {
    return CollectionDto.builder()
        .collectionId(collectionId)
        .userId(userId)
        .name(name)
        .description("Test description for " + name)
        .visibility(CollectionVisibility.PRIVATE)
        .collaborationMode(CollaborationMode.OWNER_ONLY)
        .recipeCount(5)
        .collaboratorCount(0)
        .createdAt(LocalDateTime.now().minusDays(1))
        .updatedAt(LocalDateTime.now())
        .build();
  }
}
