package com.recipe_manager.component_tests.collection_service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.recipe_manager.config.RequestIdFilter;
import com.recipe_manager.controller.CollectionController;
import com.recipe_manager.exception.GlobalExceptionHandler;
import com.recipe_manager.model.entity.collection.RecipeCollection;
import com.recipe_manager.model.enums.CollaborationMode;
import com.recipe_manager.model.enums.CollectionVisibility;
import com.recipe_manager.model.mapper.CollectionMapper;
import com.recipe_manager.model.mapper.RecipeCollectionItemMapper;
import com.recipe_manager.model.mapper.RecipeCollectionMapper;
import com.recipe_manager.repository.collection.CollectionCollaboratorRepository;
import com.recipe_manager.repository.collection.CollectionTagRepository;
import com.recipe_manager.repository.collection.RecipeCollectionItemRepository;
import com.recipe_manager.repository.collection.RecipeCollectionRepository;
import com.recipe_manager.repository.recipe.RecipeRepository;
import com.recipe_manager.service.CollectionService;
import com.recipe_manager.service.CollectionTagService;
import com.recipe_manager.service.external.notificationservice.NotificationService;

import jakarta.persistence.EntityManager;

/**
 * Component test for DELETE /collections/{collectionId} endpoint.
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
class DeleteCollectionTest {

  private MockMvc mockMvc;

  @Mock private RecipeCollectionRepository recipeCollectionRepository;

  @Mock private RecipeCollectionItemRepository recipeCollectionItemRepository;

  @Mock private CollectionCollaboratorRepository collectionCollaboratorRepository;

  @Mock private CollectionTagRepository collectionTagRepository;

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

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    testUserId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    otherUserId = UUID.fromString("987e6543-e21b-12d3-a456-426614174999");

    // Set up security context with test user ID
    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(
            testUserId.toString(), null, Collections.emptyList());
    securityContext.setAuthentication(authentication);
    SecurityContextHolder.setContext(securityContext);

    // Create real service with mocked repository
    collectionService =
        new CollectionService(
            recipeCollectionRepository,
            recipeCollectionItemRepository,
            collectionCollaboratorRepository,
            collectionTagRepository,
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
  @DisplayName("Should delete collection successfully and return 204 No Content")
  @Tag("standard-processing")
  void shouldDeleteCollectionSuccessfullyAndReturn204() throws Exception {
    // Given
    Long collectionId = 1L;

    RecipeCollection existingCollection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(testUserId)
            .name("Collection to Delete")
            .description("This will be deleted")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .createdAt(LocalDateTime.now().minusDays(1))
            .updatedAt(LocalDateTime.now().minusDays(1))
            .collectionItems(Collections.emptyList())
            .collaborators(Collections.emptyList())
            .build();

    when(recipeCollectionRepository.findById(collectionId))
        .thenReturn(Optional.of(existingCollection));

    // When & Then
    mockMvc
        .perform(delete("/collections/{collectionId}", collectionId))
        .andDo(print())
        .andExpect(status().isNoContent());

    verify(recipeCollectionRepository).findById(collectionId);
    verify(recipeCollectionRepository).delete(existingCollection);
  }

  @Test
  @DisplayName("Should return 404 Not Found when collection does not exist")
  @Tag("standard-processing")
  void shouldReturn404WhenCollectionNotFound() throws Exception {
    // Given
    Long nonExistentId = 999L;

    when(recipeCollectionRepository.findById(nonExistentId)).thenReturn(Optional.empty());

    // When & Then
    mockMvc
        .perform(delete("/collections/{collectionId}", nonExistentId))
        .andExpect(status().isNotFound());

    verify(recipeCollectionRepository).findById(nonExistentId);
  }

  @Test
  @DisplayName("Should return 403 Forbidden when user is not the collection owner")
  @Tag("standard-processing")
  void shouldReturn403WhenUserIsNotOwner() throws Exception {
    // Given
    Long collectionId = 2L;

    RecipeCollection collectionOwnedByOther =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(otherUserId) // Different user
            .name("Other user's collection")
            .visibility(CollectionVisibility.PUBLIC)
            .collaborationMode(CollaborationMode.OWNER_ONLY)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .collectionItems(Collections.emptyList())
            .collaborators(Collections.emptyList())
            .build();

    when(recipeCollectionRepository.findById(collectionId))
        .thenReturn(Optional.of(collectionOwnedByOther));

    // When & Then
    mockMvc
        .perform(delete("/collections/{collectionId}", collectionId))
        .andExpect(status().isForbidden());

    verify(recipeCollectionRepository).findById(collectionId);
  }

  @Test
  @DisplayName("Should verify repository delete is called with correct collection")
  @Tag("standard-processing")
  void shouldVerifyRepositoryDeleteCalledWithCorrectCollection() throws Exception {
    // Given
    Long collectionId = 3L;

    RecipeCollection existingCollection =
        RecipeCollection.builder()
            .collectionId(collectionId)
            .userId(testUserId)
            .name("Test Collection")
            .description("Test Description")
            .visibility(CollectionVisibility.PRIVATE)
            .collaborationMode(CollaborationMode.SPECIFIC_USERS)
            .createdAt(LocalDateTime.now().minusDays(5))
            .updatedAt(LocalDateTime.now().minusDays(2))
            .collectionItems(Collections.emptyList())
            .collaborators(Collections.emptyList())
            .build();

    when(recipeCollectionRepository.findById(collectionId))
        .thenReturn(Optional.of(existingCollection));

    // When & Then
    mockMvc
        .perform(delete("/collections/{collectionId}", collectionId))
        .andExpect(status().isNoContent());

    // Verify delete was called with the exact collection entity
    verify(recipeCollectionRepository).delete(existingCollection);
  }
}
