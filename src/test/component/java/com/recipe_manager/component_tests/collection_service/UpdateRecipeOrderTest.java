package com.recipe_manager.component_tests.collection_service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
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
import com.recipe_manager.model.dto.request.UpdateRecipeOrderRequest;
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
import com.recipe_manager.service.CollectionTagService;
import com.recipe_manager.service.external.notificationservice.NotificationService;

import jakarta.persistence.EntityManager;

/**
 * Component test for PATCH /collections/{collectionId}/recipes/{recipeId} endpoint.
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
class UpdateRecipeOrderTest {

  private MockMvc mockMvc;

  @Mock private RecipeCollectionRepository recipeCollectionRepository;

  @Mock private RecipeCollectionItemRepository recipeCollectionItemRepository;

  @Mock private CollectionCollaboratorRepository collectionCollaboratorRepository;

  @Mock private RecipeRepository recipeRepository;

  @Mock private NotificationService notificationService;

  @Mock private EntityManager entityManager;

  @Mock private CollectionTagService collectionTagService;

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
    collectionController = new CollectionController(collectionService, collectionTagService);

    mockMvc =
        MockMvcBuilders.standaloneSetup(collectionController)
            .addFilters(new RequestIdFilter())
            .setControllerAdvice(new GlobalExceptionHandler())
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();
  }

  @Test
  @DisplayName("Should update recipe order when user is owner and return 200")
  @Tag("standard-processing")
  void shouldUpdateRecipeOrderWhenUserIsOwner() throws Exception {
    // Given
    Long collectionId = 1L;
    Long recipeId = 100L;
    Integer newDisplayOrder = 15;

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(testUserId)
            .name("My Collection")
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    RecipeCollectionItemId itemId =
        RecipeCollectionItemId.builder().collectionId(collectionId).recipeId(recipeId).build();

    Recipe recipe =
        Recipe.builder()
            .recipeId(recipeId)
            .title("Test Recipe")
            .description("Test Description")
            .build();

    RecipeCollectionItem item =
        RecipeCollectionItem.builder()
            .id(itemId)
            .displayOrder(10) // Old order
            .addedBy(testUserId)
            .addedAt(LocalDateTime.now())
            .build();

    RecipeCollectionItem updatedItem =
        RecipeCollectionItem.builder()
            .id(itemId)
            .recipe(recipe)
            .displayOrder(newDisplayOrder)
            .addedBy(testUserId)
            .addedAt(LocalDateTime.now())
            .build();

    UpdateRecipeOrderRequest request =
        UpdateRecipeOrderRequest.builder().displayOrder(newDisplayOrder).build();

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));
    when(recipeCollectionItemRepository.findByIdCollectionIdAndIdRecipeId(collectionId, recipeId))
        .thenReturn(Optional.of(item));
    when(recipeCollectionItemRepository.save(any(RecipeCollectionItem.class))).thenReturn(item);
    when(recipeCollectionItemRepository.findByIdCollectionIdWithRecipe(collectionId))
        .thenReturn(List.of(updatedItem));

    // When & Then
    mockMvc
        .perform(
            patch("/collections/{collectionId}/recipes/{recipeId}", collectionId, recipeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.recipeId").value(recipeId))
        .andExpect(jsonPath("$.recipeTitle").value("Test Recipe"))
        .andExpect(jsonPath("$.recipeDescription").value("Test Description"))
        .andExpect(jsonPath("$.displayOrder").value(newDisplayOrder));

    verify(recipeCollectionRepository).findById(collectionId);
    verify(recipeCollectionItemRepository)
        .findByIdCollectionIdAndIdRecipeId(collectionId, recipeId);
    verify(recipeCollectionItemRepository).save(any(RecipeCollectionItem.class));
  }

  @Test
  @DisplayName("Should update recipe order in ALL_USERS collaboration mode and return 200")
  @Tag("standard-processing")
  void shouldUpdateRecipeOrderInAllUsersMode() throws Exception {
    // Given
    Long collectionId = 2L;
    Long recipeId = 200L;
    Integer newDisplayOrder = 25;

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(otherUserId) // Different owner
            .name("Shared Collection")
            .collaborationMode(CollaborationMode.ALL_USERS) // Any user can edit
            .build();

    RecipeCollectionItemId itemId =
        RecipeCollectionItemId.builder().collectionId(collectionId).recipeId(recipeId).build();

    Recipe recipe =
        Recipe.builder()
            .recipeId(recipeId)
            .title("Shared Recipe")
            .description("Shared Description")
            .build();

    RecipeCollectionItem item =
        RecipeCollectionItem.builder()
            .id(itemId)
            .displayOrder(10)
            .addedBy(otherUserId)
            .addedAt(LocalDateTime.now())
            .build();

    RecipeCollectionItem updatedItem =
        RecipeCollectionItem.builder()
            .id(itemId)
            .recipe(recipe)
            .displayOrder(newDisplayOrder)
            .addedBy(otherUserId)
            .addedAt(LocalDateTime.now())
            .build();

    UpdateRecipeOrderRequest request =
        UpdateRecipeOrderRequest.builder().displayOrder(newDisplayOrder).build();

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));
    when(recipeCollectionItemRepository.findByIdCollectionIdAndIdRecipeId(collectionId, recipeId))
        .thenReturn(Optional.of(item));
    when(recipeCollectionItemRepository.save(any(RecipeCollectionItem.class))).thenReturn(item);
    when(recipeCollectionItemRepository.findByIdCollectionIdWithRecipe(collectionId))
        .thenReturn(List.of(updatedItem));

    // When & Then
    mockMvc
        .perform(
            patch("/collections/{collectionId}/recipes/{recipeId}", collectionId, recipeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.recipeId").value(recipeId))
        .andExpect(jsonPath("$.displayOrder").value(newDisplayOrder));
  }

  @Test
  @DisplayName("Should return 404 when collection not found")
  @Tag("error-handling")
  void shouldReturn404WhenCollectionNotFound() throws Exception {
    // Given
    Long collectionId = 999L;
    Long recipeId = 100L;

    UpdateRecipeOrderRequest request =
        UpdateRecipeOrderRequest.builder().displayOrder(15).build();

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.empty());

    // When & Then
    mockMvc
        .perform(
            patch("/collections/{collectionId}/recipes/{recipeId}", collectionId, recipeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("Resource not found"))
        .andExpect(jsonPath("$.message").value("Collection not found"));
  }

  @Test
  @DisplayName("Should return 404 when recipe not in collection")
  @Tag("error-handling")
  void shouldReturn404WhenRecipeNotInCollection() throws Exception {
    // Given
    Long collectionId = 1L;
    Long recipeId = 999L;

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(testUserId)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .build();

    UpdateRecipeOrderRequest request =
        UpdateRecipeOrderRequest.builder().displayOrder(15).build();

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));
    when(recipeCollectionItemRepository.findByIdCollectionIdAndIdRecipeId(collectionId, recipeId))
        .thenReturn(Optional.empty());

    // When & Then
    mockMvc
        .perform(
            patch("/collections/{collectionId}/recipes/{recipeId}", collectionId, recipeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("Resource not found"))
        .andExpect(jsonPath("$.message").value("Recipe not found in this collection"));
  }

  @Test
  @DisplayName("Should return 403 when user lacks edit permission")
  @Tag("error-handling")
  void shouldReturn403WhenUserLacksEditPermission() throws Exception {
    // Given
    Long collectionId = 1L;
    Long recipeId = 100L;

    RecipeCollection collection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(otherUserId) // Different owner
            .collaborationMode(CollaborationMode.OWNER_ONLY) // Only owner can edit
            .build();

    UpdateRecipeOrderRequest request =
        UpdateRecipeOrderRequest.builder().displayOrder(15).build();

    when(recipeCollectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));

    // When & Then
    mockMvc
        .perform(
            patch("/collections/{collectionId}/recipes/{recipeId}", collectionId, recipeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error").value("Access denied"))
        .andExpect(jsonPath("$.message").value("You don't have permission to access this resource"));
  }

  @Test
  @DisplayName("Should return 400 when display order is invalid (null)")
  @Tag("error-handling")
  void shouldReturn400WhenDisplayOrderIsNull() throws Exception {
    // Given
    Long collectionId = 1L;
    Long recipeId = 100L;

    String invalidRequest = "{\"displayOrder\": null}";

    // When & Then
    mockMvc
        .perform(
            patch("/collections/{collectionId}/recipes/{recipeId}", collectionId, recipeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequest))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return 400 when display order is less than 1")
  @Tag("error-handling")
  void shouldReturn400WhenDisplayOrderIsLessThanOne() throws Exception {
    // Given
    Long collectionId = 1L;
    Long recipeId = 100L;

    UpdateRecipeOrderRequest request = UpdateRecipeOrderRequest.builder().displayOrder(0).build();

    // When & Then
    mockMvc
        .perform(
            patch("/collections/{collectionId}/recipes/{recipeId}", collectionId, recipeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }
}
