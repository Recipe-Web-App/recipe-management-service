package com.recipe_manager.component_tests.collection_service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipe_manager.config.RequestIdFilter;
import com.recipe_manager.controller.CollectionController;
import com.recipe_manager.exception.GlobalExceptionHandler;
import com.recipe_manager.model.dto.request.ReorderRecipesRequest;
import com.recipe_manager.model.entity.collection.RecipeCollection;
import com.recipe_manager.model.entity.collection.RecipeCollectionItem;
import com.recipe_manager.model.entity.collection.RecipeCollectionItemId;
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.enums.CollaborationMode;
import com.recipe_manager.model.mapper.CollectionMapper;
import com.recipe_manager.model.mapper.RecipeCollectionItemMapper;
import com.recipe_manager.model.mapper.RecipeCollectionMapper;
import com.recipe_manager.repository.collection.CollectionCollaboratorRepository;
import com.recipe_manager.repository.collection.RecipeCollectionItemRepository;
import com.recipe_manager.repository.collection.RecipeCollectionRepository;
import com.recipe_manager.repository.recipe.RecipeRepository;
import com.recipe_manager.service.CollectionService;
import com.recipe_manager.service.external.notificationservice.NotificationService;

import jakarta.persistence.EntityManager;

/**
 * Component test for PUT /collections/{collectionId}/recipes/reorder endpoint.
 *
 * <p>Tests the integration between CollectionController, CollectionService, and mappers with mocked
 * repository layer.
 */
@Tag("component")
@SpringBootTest(
    classes = {
      com.recipe_manager.model.mapper.CollectionMapperImpl.class,
      com.recipe_manager.model.mapper.RecipeCollectionMapperImpl.class,
      com.recipe_manager.model.mapper.RecipeCollectionItemMapperImpl.class,
      com.recipe_manager.model.mapper.CollectionCollaboratorMapperImpl.class
    })
@TestPropertySource(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:testdb",
      "spring.jpa.hibernate.ddl-auto=none",
      "spring.flyway.enabled=false"
    })
class ReorderRecipesTest {

  private MockMvc mockMvc;

  @Mock private RecipeCollectionRepository recipeCollectionRepository;

  @Mock private RecipeCollectionItemRepository recipeCollectionItemRepository;

  @Mock private CollectionCollaboratorRepository collectionCollaboratorRepository;

  @Mock private RecipeRepository recipeRepository;

  @Mock private NotificationService notificationService;

  @Mock private EntityManager entityManager;

  @Autowired private CollectionMapper collectionMapper;

  @Autowired private RecipeCollectionMapper recipeCollectionMapper;

  @Autowired private RecipeCollectionItemMapper recipeCollectionItemMapper;

  private CollectionService collectionService;
  private CollectionController collectionController;

  private UUID testUserId;
  private UUID otherUserId;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    testUserId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    otherUserId = UUID.fromString("987e6543-e21b-12d3-a456-426614174999");
    objectMapper = new ObjectMapper();

    // Set up security context with test user ID
    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(
            testUserId.toString(), null, Collections.emptyList());
    securityContext.setAuthentication(authentication);
    SecurityContextHolder.setContext(securityContext);

    // Create real service with mocked repositories
    collectionService =
        new CollectionService(
            recipeCollectionRepository,
            recipeCollectionItemRepository,
            collectionCollaboratorRepository,
            collectionMapper,
            recipeCollectionMapper,
            recipeCollectionItemMapper,
            recipeRepository,
            notificationService,
            entityManager);
    collectionController = new CollectionController(collectionService);

    mockMvc =
        MockMvcBuilders.standaloneSetup(collectionController)
            .addFilters(new RequestIdFilter())
            .setControllerAdvice(new GlobalExceptionHandler())
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();
  }

  @Test
  @DisplayName("Should reorder recipes when user is owner and return 200")
  @Tag("standard-processing")
  void shouldReorderRecipesWhenUserIsOwner() throws Exception {
    // Given
    Long collectionId = 1L;
    Long recipeId1 = 10L;
    Long recipeId2 = 20L;

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(testUserId)
            .name("My Collection")
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    RecipeCollectionItemId itemId1 =
        RecipeCollectionItemId.builder().collectionId(collectionId).recipeId(recipeId1).build();
    RecipeCollectionItemId itemId2 =
        RecipeCollectionItemId.builder().collectionId(collectionId).recipeId(recipeId2).build();

    Recipe recipe1 =
        Recipe.builder().recipeId(recipeId1).title("Recipe 1").description("Desc 1").build();
    Recipe recipe2 =
        Recipe.builder().recipeId(recipeId2).title("Recipe 2").description("Desc 2").build();

    RecipeCollectionItem item1 =
        RecipeCollectionItem.builder()
            .id(itemId1)
            .recipe(recipe1)
            .displayOrder(30)
            .addedBy(testUserId)
            .addedAt(LocalDateTime.now())
            .build();

    RecipeCollectionItem item2 =
        RecipeCollectionItem.builder()
            .id(itemId2)
            .recipe(recipe2)
            .displayOrder(40)
            .addedBy(testUserId)
            .addedAt(LocalDateTime.now())
            .build();

    ReorderRecipesRequest.RecipeOrder order1 =
        ReorderRecipesRequest.RecipeOrder.builder().recipeId(recipeId1).displayOrder(5).build();

    ReorderRecipesRequest.RecipeOrder order2 =
        ReorderRecipesRequest.RecipeOrder.builder().recipeId(recipeId2).displayOrder(10).build();

    ReorderRecipesRequest request =
        ReorderRecipesRequest.builder().recipes(Arrays.asList(order1, order2)).build();

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));
    when(recipeCollectionItemRepository.findByIdCollectionId(collectionId))
        .thenReturn(Arrays.asList(item1, item2));
    when(recipeCollectionItemRepository.findByIdCollectionIdAndIdRecipeId(collectionId, recipeId1))
        .thenReturn(Optional.of(item1));
    when(recipeCollectionItemRepository.findByIdCollectionIdAndIdRecipeId(collectionId, recipeId2))
        .thenReturn(Optional.of(item2));
    when(recipeCollectionItemRepository.save(any(RecipeCollectionItem.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // Update display orders
    item1.setDisplayOrder(5);
    item2.setDisplayOrder(10);

    when(recipeCollectionItemRepository.findByIdCollectionIdWithRecipe(collectionId))
        .thenReturn(Arrays.asList(item1, item2));

    // When/Then
    mockMvc
        .perform(
            put("/collections/{collectionId}/recipes/reorder", collectionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].recipeId").value(recipeId1))
        .andExpect(jsonPath("$[0].displayOrder").value(5))
        .andExpect(jsonPath("$[0].recipeTitle").value("Recipe 1"))
        .andExpect(jsonPath("$[1].recipeId").value(recipeId2))
        .andExpect(jsonPath("$[1].displayOrder").value(10))
        .andExpect(jsonPath("$[1].recipeTitle").value("Recipe 2"));

    verify(recipeCollectionRepository).findById(collectionId);
    verify(recipeCollectionItemRepository).findByIdCollectionIdWithRecipe(collectionId);
  }

  @Test
  @DisplayName("Should return 404 when collection does not exist")
  @Tag("error-handling")
  void shouldReturn404WhenCollectionNotFound() throws Exception {
    // Given
    Long nonExistentCollectionId = 999L;

    ReorderRecipesRequest.RecipeOrder order1 =
        ReorderRecipesRequest.RecipeOrder.builder().recipeId(1L).displayOrder(10).build();

    ReorderRecipesRequest request =
        ReorderRecipesRequest.builder().recipes(Collections.singletonList(order1)).build();

    when(recipeCollectionRepository.findById(nonExistentCollectionId))
        .thenReturn(Optional.empty());

    // When/Then
    mockMvc
        .perform(
            put("/collections/{collectionId}/recipes/reorder", nonExistentCollectionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("Resource not found"))
        .andExpect(jsonPath("$.message").value("Collection not found"));

    verify(recipeCollectionRepository).findById(nonExistentCollectionId);
  }

  @Test
  @DisplayName("Should return 403 when user lacks edit permission")
  @Tag("error-handling")
  void shouldReturn403WhenUserLacksEditPermission() throws Exception {
    // Given
    Long collectionId = 2L;

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(otherUserId) // Different owner
            .name("Someone else's collection")
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    ReorderRecipesRequest.RecipeOrder order1 =
        ReorderRecipesRequest.RecipeOrder.builder().recipeId(1L).displayOrder(10).build();

    ReorderRecipesRequest request =
        ReorderRecipesRequest.builder().recipes(Collections.singletonList(order1)).build();

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));

    // When/Then
    mockMvc
        .perform(
            put("/collections/{collectionId}/recipes/reorder", collectionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error").value("Access denied"));

    verify(recipeCollectionRepository).findById(collectionId);
  }

  @Test
  @DisplayName("Should return 400 when request contains duplicate display orders")
  @Tag("error-handling")
  void shouldReturn400WhenDuplicateDisplayOrders() throws Exception {
    // Given
    Long collectionId = 3L;

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(testUserId)
            .name("My Collection")
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    ReorderRecipesRequest.RecipeOrder order1 =
        ReorderRecipesRequest.RecipeOrder.builder().recipeId(1L).displayOrder(10).build();

    ReorderRecipesRequest.RecipeOrder order2 =
        ReorderRecipesRequest.RecipeOrder.builder().recipeId(2L).displayOrder(10).build(); // Duplicate

    ReorderRecipesRequest request =
        ReorderRecipesRequest.builder().recipes(Arrays.asList(order1, order2)).build();

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));

    // When/Then
    mockMvc
        .perform(
            put("/collections/{collectionId}/recipes/reorder", collectionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Request contains duplicate display orders"));

    verify(recipeCollectionRepository).findById(collectionId);
  }

  @Test
  @DisplayName("Should return 404 when recipe not in collection")
  @Tag("error-handling")
  void shouldReturn404WhenRecipeNotInCollection() throws Exception {
    // Given
    Long collectionId = 4L;
    Long existingRecipeId = 1L;
    Long nonExistentRecipeId = 999L;

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(testUserId)
            .name("My Collection")
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    RecipeCollectionItemId itemId1 =
        RecipeCollectionItemId.builder()
            .collectionId(collectionId)
            .recipeId(existingRecipeId)
            .build();

    RecipeCollectionItem item1 =
        RecipeCollectionItem.builder().id(itemId1).displayOrder(10).addedBy(testUserId).build();

    ReorderRecipesRequest.RecipeOrder order1 =
        ReorderRecipesRequest.RecipeOrder.builder()
            .recipeId(nonExistentRecipeId)
            .displayOrder(10)
            .build();

    ReorderRecipesRequest request =
        ReorderRecipesRequest.builder().recipes(Collections.singletonList(order1)).build();

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));
    when(recipeCollectionItemRepository.findByIdCollectionId(collectionId))
        .thenReturn(Collections.singletonList(item1));

    // When/Then
    mockMvc
        .perform(
            put("/collections/{collectionId}/recipes/reorder", collectionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("Resource not found"))
        .andExpect(jsonPath("$.message").value("Recipe with ID 999 not found in this collection"));

    verify(recipeCollectionRepository).findById(collectionId);
    verify(recipeCollectionItemRepository).findByIdCollectionId(collectionId);
  }

  @Test
  @DisplayName("Should allow reorder in ALL_USERS collaboration mode")
  @Tag("standard-processing")
  void shouldAllowReorderInAllUsersMode() throws Exception {
    // Given
    Long collectionId = 5L;
    Long recipeId = 10L;

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(otherUserId) // Different owner
            .name("Public Collection")
            .collaborationMode(CollaborationMode.ALL_USERS)
            .build();

    RecipeCollectionItemId itemId =
        RecipeCollectionItemId.builder().collectionId(collectionId).recipeId(recipeId).build();

    Recipe recipe = Recipe.builder().recipeId(recipeId).title("Recipe").build();

    RecipeCollectionItem item =
        RecipeCollectionItem.builder()
            .id(itemId)
            .recipe(recipe)
            .displayOrder(10)
            .addedBy(testUserId)
            .build();

    ReorderRecipesRequest.RecipeOrder order1 =
        ReorderRecipesRequest.RecipeOrder.builder().recipeId(recipeId).displayOrder(15).build();

    ReorderRecipesRequest request =
        ReorderRecipesRequest.builder().recipes(Collections.singletonList(order1)).build();

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));
    when(recipeCollectionItemRepository.findByIdCollectionId(collectionId))
        .thenReturn(Collections.singletonList(item));
    when(recipeCollectionItemRepository.findByIdCollectionIdAndIdRecipeId(collectionId, recipeId))
        .thenReturn(Optional.of(item));
    when(recipeCollectionItemRepository.save(any(RecipeCollectionItem.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    item.setDisplayOrder(15);

    when(recipeCollectionItemRepository.findByIdCollectionIdWithRecipe(collectionId))
        .thenReturn(Collections.singletonList(item));

    // When/Then
    mockMvc
        .perform(
            put("/collections/{collectionId}/recipes/reorder", collectionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].recipeId").value(recipeId))
        .andExpect(jsonPath("$[0].displayOrder").value(15));

    verify(recipeCollectionRepository).findById(collectionId);
  }
}
