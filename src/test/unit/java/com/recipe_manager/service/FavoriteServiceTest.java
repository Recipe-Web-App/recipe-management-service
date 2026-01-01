package com.recipe_manager.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.recipe_manager.client.usermanagement.UserManagementClient;
import com.recipe_manager.exception.BusinessException;
import com.recipe_manager.exception.ResourceNotFoundException;
import com.recipe_manager.model.dto.external.usermanagement.GetFollowersResponseDto;
import com.recipe_manager.model.dto.external.usermanagement.PrivacyPreferencesDto;
import com.recipe_manager.model.dto.external.usermanagement.UserDto;
import com.recipe_manager.model.dto.external.usermanagement.UserPreferencesDto;
import com.recipe_manager.model.dto.recipe.RecipeDto;
import com.recipe_manager.model.dto.recipe.RecipeFavoriteDto;
import com.recipe_manager.model.dto.response.SearchRecipesResponse;
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.entity.recipe.RecipeFavorite;
import com.recipe_manager.model.entity.recipe.RecipeFavoriteId;
import com.recipe_manager.model.enums.ProfileVisibilityEnum;
import com.recipe_manager.model.mapper.RecipeFavoriteMapper;
import com.recipe_manager.model.mapper.RecipeMapper;
import com.recipe_manager.repository.recipe.RecipeFavoriteRepository;
import com.recipe_manager.repository.recipe.RecipeRepository;
import com.recipe_manager.util.SecurityUtils;

/** Unit tests for FavoriteService including privacy controls. */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {

  @Mock private RecipeFavoriteRepository recipeFavoriteRepository;

  @Mock private RecipeRepository recipeRepository;

  @Mock private RecipeFavoriteMapper recipeFavoriteMapper;

  @Mock private RecipeMapper recipeMapper;

  @Mock private UserManagementClient userManagementClient;

  private FavoriteService favoriteService;

  private UUID authenticatedUserId;
  private UUID targetUserId;
  private Long testRecipeId;

  @BeforeEach
  void setUp() {
    favoriteService =
        new FavoriteService(
            recipeFavoriteRepository,
            recipeRepository,
            recipeFavoriteMapper,
            recipeMapper,
            userManagementClient);
    authenticatedUserId = UUID.randomUUID();
    targetUserId = UUID.randomUUID();
    testRecipeId = 100L;
  }

  // ==================== getUserFavorites Tests ====================

  @Test
  @DisplayName("Should get own favorites without privacy check")
  @Tag("standard-processing")
  void shouldGetOwnFavoritesWithoutPrivacyCheck() throws AccessDeniedException {
    // Given
    Pageable pageable = PageRequest.of(0, 20);
    List<RecipeFavorite> favorites =
        Arrays.asList(createTestFavorite(authenticatedUserId, 101L));
    Page<RecipeFavorite> favoritesPage = new PageImpl<>(favorites, pageable, 1);

    RecipeDto recipeDto = createTestRecipeDto(101L);

    when(recipeFavoriteRepository.findByUserIdWithRecipe(authenticatedUserId, pageable))
        .thenReturn(favoritesPage);
    when(recipeMapper.toDto(any(Recipe.class))).thenReturn(recipeDto);

    // When
    ResponseEntity<SearchRecipesResponse> response;
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(authenticatedUserId);
      response = favoriteService.getUserFavorites(null, pageable);
    }

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getRecipes()).hasSize(1);
    verify(recipeFavoriteRepository).findByUserIdWithRecipe(authenticatedUserId, pageable);
    verify(userManagementClient, never()).getUserPreferences(any());
  }

  @Test
  @DisplayName("Should get other user favorites with PUBLIC privacy")
  @Tag("standard-processing")
  void shouldGetOtherUserFavoritesWithPublicPrivacy() throws AccessDeniedException {
    // Given
    Pageable pageable = PageRequest.of(0, 20);
    List<RecipeFavorite> favorites = Arrays.asList(createTestFavorite(targetUserId, 101L));
    Page<RecipeFavorite> favoritesPage = new PageImpl<>(favorites, pageable, 1);

    RecipeDto recipeDto = createTestRecipeDto(101L);
    UserPreferencesDto preferences = createUserPreferences(ProfileVisibilityEnum.PUBLIC);

    when(recipeFavoriteRepository.findByUserIdWithRecipe(targetUserId, pageable))
        .thenReturn(favoritesPage);
    when(recipeMapper.toDto(any(Recipe.class))).thenReturn(recipeDto);
    when(userManagementClient.getUserPreferences(targetUserId)).thenReturn(preferences);

    // When
    ResponseEntity<SearchRecipesResponse> response;
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(authenticatedUserId);
      response = favoriteService.getUserFavorites(targetUserId, pageable);
    }

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getRecipes()).hasSize(1);
    verify(userManagementClient).getUserPreferences(targetUserId);
    verify(recipeFavoriteRepository).findByUserIdWithRecipe(targetUserId, pageable);
  }

  @Test
  @DisplayName("Should deny access to PRIVATE user favorites")
  @Tag("error-handling")
  void shouldDenyAccessToPrivateUserFavorites() {
    // Given
    Pageable pageable = PageRequest.of(0, 20);
    UserPreferencesDto preferences = createUserPreferences(ProfileVisibilityEnum.PRIVATE);

    when(userManagementClient.getUserPreferences(targetUserId)).thenReturn(preferences);

    // When / Then
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(authenticatedUserId);

      assertThatThrownBy(() -> favoriteService.getUserFavorites(targetUserId, pageable))
          .isInstanceOf(AccessDeniedException.class)
          .hasMessageContaining("User's favorites are private");
    }

    verify(userManagementClient).getUserPreferences(targetUserId);
    verify(recipeFavoriteRepository, never()).findByUserIdWithRecipe(any(), any());
  }

  @Test
  @DisplayName("Should allow follower to view FRIENDS_ONLY favorites")
  @Tag("standard-processing")
  void shouldAllowFollowerToViewFriendsOnlyFavorites() throws AccessDeniedException {
    // Given
    Pageable pageable = PageRequest.of(0, 20);
    List<RecipeFavorite> favorites = Arrays.asList(createTestFavorite(targetUserId, 101L));
    Page<RecipeFavorite> favoritesPage = new PageImpl<>(favorites, pageable, 1);

    RecipeDto recipeDto = createTestRecipeDto(101L);
    UserPreferencesDto preferences = createUserPreferences(ProfileVisibilityEnum.FRIENDS_ONLY);
    GetFollowersResponseDto followers = createFollowersResponse(authenticatedUserId);

    when(recipeFavoriteRepository.findByUserIdWithRecipe(targetUserId, pageable))
        .thenReturn(favoritesPage);
    when(recipeMapper.toDto(any(Recipe.class))).thenReturn(recipeDto);
    when(userManagementClient.getUserPreferences(targetUserId)).thenReturn(preferences);
    when(userManagementClient.getFollowers(eq(targetUserId), any(), any(), anyBoolean()))
        .thenReturn(followers);

    // When
    ResponseEntity<SearchRecipesResponse> response;
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(authenticatedUserId);
      response = favoriteService.getUserFavorites(targetUserId, pageable);
    }

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getRecipes()).hasSize(1);
    verify(userManagementClient).getUserPreferences(targetUserId);
    verify(userManagementClient).getFollowers(targetUserId, null, null, false);
  }

  @Test
  @DisplayName("Should deny non-follower access to FRIENDS_ONLY favorites")
  @Tag("error-handling")
  void shouldDenyNonFollowerAccessToFriendsOnlyFavorites() {
    // Given
    Pageable pageable = PageRequest.of(0, 20);
    UserPreferencesDto preferences = createUserPreferences(ProfileVisibilityEnum.FRIENDS_ONLY);
    GetFollowersResponseDto followers =
        createFollowersResponse(UUID.randomUUID()); // Different user

    when(userManagementClient.getUserPreferences(targetUserId)).thenReturn(preferences);
    when(userManagementClient.getFollowers(eq(targetUserId), any(), any(), anyBoolean()))
        .thenReturn(followers);

    // When / Then
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(authenticatedUserId);

      assertThatThrownBy(() -> favoriteService.getUserFavorites(targetUserId, pageable))
          .isInstanceOf(AccessDeniedException.class)
          .hasMessageContaining("Only followers can view them");
    }

    verify(userManagementClient).getUserPreferences(targetUserId);
    verify(userManagementClient).getFollowers(targetUserId, null, null, false);
    verify(recipeFavoriteRepository, never()).findByUserIdWithRecipe(any(), any());
  }

  @Test
  @DisplayName("Should return empty page when user has no favorites")
  @Tag("standard-processing")
  void shouldReturnEmptyPageWhenUserHasNoFavorites() throws AccessDeniedException {
    // Given
    Pageable pageable = PageRequest.of(0, 20);
    Page<RecipeFavorite> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

    when(recipeFavoriteRepository.findByUserIdWithRecipe(authenticatedUserId, pageable))
        .thenReturn(emptyPage);

    // When
    ResponseEntity<SearchRecipesResponse> response;
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(authenticatedUserId);
      response = favoriteService.getUserFavorites(null, pageable);
    }

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getRecipes()).isEmpty();
    assertThat(response.getBody().getTotalElements()).isZero();
  }

  // ==================== addFavorite Tests ====================

  @Test
  @DisplayName("Should add favorite successfully")
  @Tag("standard-processing")
  void shouldAddFavoriteSuccessfully() {
    // Given
    Recipe recipe = createTestRecipe(testRecipeId);
    RecipeFavorite favorite = createTestFavorite(authenticatedUserId, testRecipeId);
    RecipeFavoriteDto dto = createTestFavoriteDto(authenticatedUserId, testRecipeId);

    when(recipeFavoriteRepository.existsByIdUserIdAndIdRecipeId(authenticatedUserId, testRecipeId))
        .thenReturn(false);
    when(recipeRepository.findById(testRecipeId)).thenReturn(Optional.of(recipe));
    when(recipeFavoriteRepository.save(any(RecipeFavorite.class))).thenReturn(favorite);
    when(recipeFavoriteMapper.toDto(favorite)).thenReturn(dto);

    // When
    ResponseEntity<RecipeFavoriteDto> response;
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(authenticatedUserId);
      response = favoriteService.addFavorite(testRecipeId);
    }

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).isEqualTo(dto);
    verify(recipeFavoriteRepository).save(any(RecipeFavorite.class));
  }

  @Test
  @DisplayName("Should throw exception when recipe already favorited")
  @Tag("error-handling")
  void shouldThrowExceptionWhenRecipeAlreadyFavorited() {
    // Given
    when(recipeFavoriteRepository.existsByIdUserIdAndIdRecipeId(authenticatedUserId, testRecipeId))
        .thenReturn(true);

    // When / Then
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(authenticatedUserId);

      assertThatThrownBy(() -> favoriteService.addFavorite(testRecipeId))
          .isInstanceOf(BusinessException.class)
          .hasMessageContaining("User has already favorited this recipe");
    }

    verify(recipeFavoriteRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should throw exception when recipe not found")
  @Tag("error-handling")
  void shouldThrowExceptionWhenRecipeNotFound() {
    // Given
    when(recipeFavoriteRepository.existsByIdUserIdAndIdRecipeId(authenticatedUserId, testRecipeId))
        .thenReturn(false);
    when(recipeRepository.findById(testRecipeId)).thenReturn(Optional.empty());

    // When / Then
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(authenticatedUserId);

      assertThatThrownBy(() -> favoriteService.addFavorite(testRecipeId))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessageContaining("Recipe not found with ID: " + testRecipeId);
    }

    verify(recipeFavoriteRepository, never()).save(any());
  }

  // ==================== removeFavorite Tests ====================

  @Test
  @DisplayName("Should remove favorite successfully")
  @Tag("standard-processing")
  void shouldRemoveFavoriteSuccessfully() {
    // Given
    when(recipeFavoriteRepository.existsByIdUserIdAndIdRecipeId(authenticatedUserId, testRecipeId))
        .thenReturn(true);

    // When
    ResponseEntity<Void> response;
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(authenticatedUserId);
      response = favoriteService.removeFavorite(testRecipeId);
    }

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    verify(recipeFavoriteRepository).deleteByIdUserIdAndIdRecipeId(authenticatedUserId, testRecipeId);
  }

  @Test
  @DisplayName("Should throw exception when favorite not found for removal")
  @Tag("error-handling")
  void shouldThrowExceptionWhenFavoriteNotFoundForRemoval() {
    // Given
    when(recipeFavoriteRepository.existsByIdUserIdAndIdRecipeId(authenticatedUserId, testRecipeId))
        .thenReturn(false);

    // When / Then
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(authenticatedUserId);

      assertThatThrownBy(() -> favoriteService.removeFavorite(testRecipeId))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessageContaining("Favorite not found for this user and recipe");
    }

    verify(recipeFavoriteRepository, never()).deleteByIdUserIdAndIdRecipeId(any(), any());
  }

  // ==================== isFavorited Tests ====================

  @Test
  @DisplayName("Should return true when recipe is favorited")
  @Tag("standard-processing")
  void shouldReturnTrueWhenRecipeIsFavorited() {
    // Given
    when(recipeFavoriteRepository.existsByIdUserIdAndIdRecipeId(authenticatedUserId, testRecipeId))
        .thenReturn(true);

    // When
    ResponseEntity<Boolean> response;
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(authenticatedUserId);
      response = favoriteService.isFavorited(testRecipeId);
    }

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isTrue();
  }

  @Test
  @DisplayName("Should return false when recipe is not favorited")
  @Tag("standard-processing")
  void shouldReturnFalseWhenRecipeIsNotFavorited() {
    // Given
    when(recipeFavoriteRepository.existsByIdUserIdAndIdRecipeId(authenticatedUserId, testRecipeId))
        .thenReturn(false);

    // When
    ResponseEntity<Boolean> response;
    try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
      securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(authenticatedUserId);
      response = favoriteService.isFavorited(testRecipeId);
    }

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isFalse();
  }

  // ==================== Helper Methods ====================

  private RecipeFavorite createTestFavorite(UUID userId, Long recipeId) {
    RecipeFavoriteId id = RecipeFavoriteId.builder().userId(userId).recipeId(recipeId).build();

    Recipe recipe = createTestRecipe(recipeId);

    return RecipeFavorite.builder()
        .id(id)
        .recipe(recipe)
        .favoritedAt(LocalDateTime.now())
        .build();
  }

  private Recipe createTestRecipe(Long recipeId) {
    return Recipe.builder()
        .recipeId(recipeId)
        .title("Test Recipe " + recipeId)
        .description("Test Description")
        .build();
  }

  private RecipeDto createTestRecipeDto(Long recipeId) {
    return RecipeDto.builder()
        .recipeId(recipeId)
        .title("Test Recipe " + recipeId)
        .description("Test Description")
        .build();
  }

  private RecipeFavoriteDto createTestFavoriteDto(UUID userId, Long recipeId) {
    return RecipeFavoriteDto.builder()
        .userId(userId)
        .recipeId(recipeId)
        .favoritedAt(LocalDateTime.now())
        .build();
  }

  private UserPreferencesDto createUserPreferences(ProfileVisibilityEnum visibility) {
    PrivacyPreferencesDto privacyPrefs =
        PrivacyPreferencesDto.builder().profileVisibility(visibility).build();

    return UserPreferencesDto.builder()
        .userId(targetUserId)
        .privacy(privacyPrefs)
        .build();
  }

  private GetFollowersResponseDto createFollowersResponse(UUID followerId) {
    UserDto follower =
        UserDto.builder().userId(followerId).username("follower" + followerId).build();

    return GetFollowersResponseDto.builder()
        .followedUsers(Arrays.asList(follower))
        .totalCount(1)
        .build();
  }
}
