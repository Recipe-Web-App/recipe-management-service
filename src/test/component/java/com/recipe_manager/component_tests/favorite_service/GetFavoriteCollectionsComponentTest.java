package com.recipe_manager.component_tests.favorite_service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.recipe_manager.client.usermanagement.UserManagementClient;
import com.recipe_manager.component_tests.AbstractComponentTest;
import com.recipe_manager.controller.FavoriteController;
import com.recipe_manager.exception.GlobalExceptionHandler;
import com.recipe_manager.model.dto.external.usermanagement.GetFollowersResponseDto;
import com.recipe_manager.model.dto.external.usermanagement.PrivacyPreferencesDto;
import com.recipe_manager.model.dto.external.usermanagement.UserDto;
import com.recipe_manager.model.dto.external.usermanagement.UserPreferencesDto;
import com.recipe_manager.model.dto.response.CollectionDto;
import com.recipe_manager.model.entity.collection.CollectionFavorite;
import com.recipe_manager.model.entity.collection.CollectionFavoriteId;
import com.recipe_manager.model.entity.collection.RecipeCollection;
import com.recipe_manager.model.enums.CollaborationMode;
import com.recipe_manager.model.enums.ProfileVisibilityEnum;
import com.recipe_manager.model.mapper.CollectionFavoriteMapper;
import com.recipe_manager.model.mapper.CollectionMapper;
import com.recipe_manager.model.mapper.RecipeFavoriteMapper;
import com.recipe_manager.model.mapper.RecipeMapper;
import com.recipe_manager.repository.collection.CollectionFavoriteRepository;
import com.recipe_manager.repository.collection.RecipeCollectionRepository;
import com.recipe_manager.repository.recipe.RecipeFavoriteRepository;
import com.recipe_manager.service.FavoriteService;
import com.recipe_manager.util.SecurityUtils;

/**
 * Component tests for getting user's favorite collections with privacy controls.
 *
 * <p>Tests the GET /favorites/collections endpoint with real service and mapper logic, mocking only
 * repositories and external dependencies. Includes comprehensive privacy testing scenarios.
 */
@SpringBootTest(
    classes = {
      com.recipe_manager.model.mapper.RecipeFavoriteMapperImpl.class,
      com.recipe_manager.model.mapper.CollectionFavoriteMapperImpl.class,
      com.recipe_manager.model.mapper.CollectionMapperImpl.class
    })
@TestPropertySource(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:testdb",
      "spring.jpa.hibernate.ddl-auto=none",
      "spring.flyway.enabled=false"
    })
@Tag("component")
class GetFavoriteCollectionsComponentTest extends AbstractComponentTest {

  private FavoriteService favoriteService;
  private FavoriteController favoriteController;

  // Mocked dependencies
  private RecipeFavoriteRepository recipeFavoriteRepository;
  private RecipeMapper testRecipeMapper;
  private UserManagementClient userManagementClient;
  private CollectionFavoriteRepository collectionFavoriteRepository;
  private RecipeCollectionRepository recipeCollectionRepository;

  @Autowired(required = false)
  private RecipeFavoriteMapper recipeFavoriteMapper;

  @Autowired(required = false)
  private CollectionFavoriteMapper collectionFavoriteMapper;

  @Autowired(required = false)
  private CollectionMapper collectionMapper;

  private UUID testUserId;
  private UUID otherUserId;

  @Override
  @BeforeEach
  protected void setUp() {
    super.setUp();
    useRealFavoriteService();
  }

  private void useRealFavoriteService() {
    testUserId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    otherUserId = UUID.fromString("987e6543-e21b-12d3-a456-426614174999");

    // Mock dependencies
    this.recipeFavoriteRepository = Mockito.mock(RecipeFavoriteRepository.class);
    this.testRecipeMapper = Mockito.mock(RecipeMapper.class);
    this.userManagementClient = Mockito.mock(UserManagementClient.class);
    this.collectionFavoriteRepository = Mockito.mock(CollectionFavoriteRepository.class);
    this.recipeCollectionRepository = Mockito.mock(RecipeCollectionRepository.class);

    if (collectionFavoriteMapper == null) {
      throw new RuntimeException("CollectionFavoriteMapper not available in test context");
    }

    // Create real service with all dependencies
    this.favoriteService =
        new FavoriteService(
            recipeFavoriteRepository,
            recipeRepository,
            collectionFavoriteRepository,
            recipeCollectionRepository,
            recipeFavoriteMapper,
            testRecipeMapper,
            collectionFavoriteMapper,
            collectionMapper,
            userManagementClient);

    // Create controller
    this.favoriteController = new FavoriteController(favoriteService);

    // Rebuild MockMvc with FavoriteController and PageableArgumentResolver
    mockMvc =
        MockMvcBuilders.standaloneSetup(favoriteController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .setCustomArgumentResolvers(
                new org.springframework.data.web.PageableHandlerMethodArgumentResolver())
            .build();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should get own favorite collections without userId param and no privacy check")
  void shouldGetOwnFavoriteCollectionsWithoutUserIdParam() throws Exception {
    // Given
    Pageable pageable = PageRequest.of(0, 20);
    List<CollectionFavorite> favorites =
        Arrays.asList(
            createTestFavorite(testUserId, 201L), createTestFavorite(testUserId, 202L));
    Page<CollectionFavorite> favoritesPage = new PageImpl<>(favorites, pageable, 2);

    when(collectionFavoriteRepository.findByUserIdWithCollection(testUserId, pageable))
        .thenReturn(favoritesPage);

    try (MockedStatic<SecurityUtils> mock = Mockito.mockStatic(SecurityUtils.class)) {
      mock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      mockMvc
          .perform(get("/favorites/collections").param("page", "0").param("size", "20"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.content").isArray())
          .andExpect(jsonPath("$.content.length()").value(2))
          .andExpect(jsonPath("$.totalElements").value(2));
    }

    verify(userManagementClient, never()).getUserPreferences(any());
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should get own favorite collections with explicit userId")
  void shouldGetOwnFavoriteCollectionsWithExplicitUserId() throws Exception {
    // Given
    Pageable pageable = PageRequest.of(0, 20);
    List<CollectionFavorite> favorites = Arrays.asList(createTestFavorite(testUserId, 201L));
    Page<CollectionFavorite> favoritesPage = new PageImpl<>(favorites, pageable, 1);

    when(collectionFavoriteRepository.findByUserIdWithCollection(testUserId, pageable))
        .thenReturn(favoritesPage);

    try (MockedStatic<SecurityUtils> mock = Mockito.mockStatic(SecurityUtils.class)) {
      mock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      mockMvc
          .perform(
              get("/favorites/collections")
                  .param("userId", testUserId.toString())
                  .param("page", "0")
                  .param("size", "20"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.content.length()").value(1));
    }

    verify(userManagementClient, never()).getUserPreferences(any());
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should return empty list when user has no favorite collections")
  void shouldReturnEmptyListWhenNoFavorites() throws Exception {
    // Given
    Pageable pageable = PageRequest.of(0, 20);
    Page<CollectionFavorite> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

    when(collectionFavoriteRepository.findByUserIdWithCollection(testUserId, pageable))
        .thenReturn(emptyPage);

    try (MockedStatic<SecurityUtils> mock = Mockito.mockStatic(SecurityUtils.class)) {
      mock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      mockMvc
          .perform(get("/favorites/collections").param("page", "0").param("size", "20"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.content").isEmpty())
          .andExpect(jsonPath("$.totalElements").value(0));
    }
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should get other user PUBLIC favorite collections")
  void shouldGetOtherUserPublicFavoriteCollections() throws Exception {
    // Given
    Pageable pageable = PageRequest.of(0, 20);
    List<CollectionFavorite> favorites = Arrays.asList(createTestFavorite(otherUserId, 201L));
    Page<CollectionFavorite> favoritesPage = new PageImpl<>(favorites, pageable, 1);

    when(collectionFavoriteRepository.findByUserIdWithCollection(otherUserId, pageable))
        .thenReturn(favoritesPage);
    when(userManagementClient.getUserPreferences(otherUserId))
        .thenReturn(createUserPreferences(ProfileVisibilityEnum.PUBLIC));

    try (MockedStatic<SecurityUtils> mock = Mockito.mockStatic(SecurityUtils.class)) {
      mock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      mockMvc
          .perform(
              get("/favorites/collections")
                  .param("userId", otherUserId.toString())
                  .param("page", "0")
                  .param("size", "20"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.content.length()").value(1));
    }

    verify(userManagementClient).getUserPreferences(otherUserId);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should allow follower to view FRIENDS_ONLY favorite collections")
  void shouldAllowFollowerToViewFriendsOnlyFavoriteCollections() throws Exception {
    // Given
    Pageable pageable = PageRequest.of(0, 20);
    List<CollectionFavorite> favorites = Arrays.asList(createTestFavorite(otherUserId, 201L));
    Page<CollectionFavorite> favoritesPage = new PageImpl<>(favorites, pageable, 1);

    when(collectionFavoriteRepository.findByUserIdWithCollection(otherUserId, pageable))
        .thenReturn(favoritesPage);
    when(userManagementClient.getUserPreferences(otherUserId))
        .thenReturn(createUserPreferences(ProfileVisibilityEnum.FRIENDS_ONLY));
    when(userManagementClient.getFollowers(eq(otherUserId), isNull(), isNull(), eq(false)))
        .thenReturn(createFollowersResponse(testUserId));

    try (MockedStatic<SecurityUtils> mock = Mockito.mockStatic(SecurityUtils.class)) {
      mock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      mockMvc
          .perform(
              get("/favorites/collections")
                  .param("userId", otherUserId.toString())
                  .param("page", "0")
                  .param("size", "20"))
          .andExpect(status().isOk());
    }

    verify(userManagementClient).getFollowers(otherUserId, null, null, false);
  }

  @Test
  @Tag("error-handling")
  @DisplayName("Should deny non-follower access to FRIENDS_ONLY favorite collections")
  void shouldDenyNonFollowerAccessToFriendsOnlyFavoriteCollections() throws Exception {
    // Given
    UUID differentUser = UUID.randomUUID();

    when(userManagementClient.getUserPreferences(otherUserId))
        .thenReturn(createUserPreferences(ProfileVisibilityEnum.FRIENDS_ONLY));
    when(userManagementClient.getFollowers(eq(otherUserId), any(), any(), anyBoolean()))
        .thenReturn(createFollowersResponse(differentUser));

    try (MockedStatic<SecurityUtils> mock = Mockito.mockStatic(SecurityUtils.class)) {
      mock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      mockMvc
          .perform(
              get("/favorites/collections")
                  .param("userId", otherUserId.toString())
                  .param("page", "0")
                  .param("size", "20"))
          .andExpect(status().isForbidden());
    }

    verify(collectionFavoriteRepository, never()).findByUserIdWithCollection(any(), any());
  }

  @Test
  @Tag("error-handling")
  @DisplayName("Should deny access to PRIVATE user favorite collections")
  void shouldDenyAccessToPrivateUserFavoriteCollections() throws Exception {
    // Given
    when(userManagementClient.getUserPreferences(otherUserId))
        .thenReturn(createUserPreferences(ProfileVisibilityEnum.PRIVATE));

    try (MockedStatic<SecurityUtils> mock = Mockito.mockStatic(SecurityUtils.class)) {
      mock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      mockMvc
          .perform(
              get("/favorites/collections")
                  .param("userId", otherUserId.toString())
                  .param("page", "0")
                  .param("size", "20"))
          .andExpect(status().isForbidden());
    }

    verify(collectionFavoriteRepository, never()).findByUserIdWithCollection(any(), any());
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle first page pagination")
  void shouldHandleFirstPagePagination() throws Exception {
    // Given
    Pageable pageable = PageRequest.of(0, 10);
    List<CollectionFavorite> favorites = Arrays.asList(createTestFavorite(testUserId, 201L));
    Page<CollectionFavorite> favoritesPage = new PageImpl<>(favorites, pageable, 30);

    when(collectionFavoriteRepository.findByUserIdWithCollection(testUserId, pageable))
        .thenReturn(favoritesPage);

    try (MockedStatic<SecurityUtils> mock = Mockito.mockStatic(SecurityUtils.class)) {
      mock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      mockMvc
          .perform(get("/favorites/collections").param("page", "0").param("size", "10"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.number").value(0))
          .andExpect(jsonPath("$.size").value(10))
          .andExpect(jsonPath("$.first").value(true))
          .andExpect(jsonPath("$.last").value(false));
    }
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle custom page size")
  void shouldHandleCustomPageSize() throws Exception {
    // Given
    Pageable pageable = PageRequest.of(0, 5);
    List<CollectionFavorite> favorites = Arrays.asList(createTestFavorite(testUserId, 201L));
    Page<CollectionFavorite> favoritesPage = new PageImpl<>(favorites, pageable, 25);

    when(collectionFavoriteRepository.findByUserIdWithCollection(testUserId, pageable))
        .thenReturn(favoritesPage);

    try (MockedStatic<SecurityUtils> mock = Mockito.mockStatic(SecurityUtils.class)) {
      mock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      mockMvc
          .perform(get("/favorites/collections").param("page", "0").param("size", "5"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.size").value(5));
    }
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should validate response structure")
  void shouldValidateResponseStructure() throws Exception {
    // Given
    Pageable pageable = PageRequest.of(0, 20);
    List<CollectionFavorite> favorites = Arrays.asList(createTestFavorite(testUserId, 201L));
    Page<CollectionFavorite> favoritesPage = new PageImpl<>(favorites, pageable, 1);

    when(collectionFavoriteRepository.findByUserIdWithCollection(testUserId, pageable))
        .thenReturn(favoritesPage);

    try (MockedStatic<SecurityUtils> mock = Mockito.mockStatic(SecurityUtils.class)) {
      mock.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      mockMvc
          .perform(get("/favorites/collections").param("page", "0").param("size", "20"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.content").exists())
          .andExpect(jsonPath("$.number").exists())
          .andExpect(jsonPath("$.size").exists())
          .andExpect(jsonPath("$.totalElements").exists())
          .andExpect(jsonPath("$.first").exists())
          .andExpect(jsonPath("$.last").exists());
    }
  }

  // Helper methods

  private RecipeCollection createTestCollection(Long collectionId) {
    return RecipeCollection.builder()
        .collectionId(collectionId)
        .name("Test Collection " + collectionId)
        .description("Test Description")
        .userId(UUID.randomUUID())
        .collaborationMode(CollaborationMode.OWNER_ONLY)
        .build();
  }

  private CollectionDto createTestCollectionDto(Long collectionId) {
    return CollectionDto.builder()
        .collectionId(collectionId)
        .name("Test Collection " + collectionId)
        .description("Test Description")
        .build();
  }

  private CollectionFavorite createTestFavorite(UUID userId, Long collectionId) {
    CollectionFavoriteId id =
        CollectionFavoriteId.builder().userId(userId).collectionId(collectionId).build();
    RecipeCollection collection = createTestCollection(collectionId);
    return CollectionFavorite.builder()
        .id(id)
        .collection(collection)
        .favoritedAt(LocalDateTime.now())
        .build();
  }

  private UserPreferencesDto createUserPreferences(ProfileVisibilityEnum visibility) {
    PrivacyPreferencesDto privacy =
        PrivacyPreferencesDto.builder().profileVisibility(visibility).build();
    return UserPreferencesDto.builder().userId(otherUserId).privacy(privacy).build();
  }

  private GetFollowersResponseDto createFollowersResponse(UUID followerId) {
    UserDto follower = UserDto.builder().userId(followerId).username("user").build();
    return GetFollowersResponseDto.builder()
        .followedUsers(Arrays.asList(follower))
        .totalCount(1)
        .build();
  }
}
