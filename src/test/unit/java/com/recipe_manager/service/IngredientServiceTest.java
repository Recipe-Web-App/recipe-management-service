package com.recipe_manager.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.recipe_manager.exception.BusinessException;
import com.recipe_manager.model.dto.external.recipescraper.RecipeScraperShoppingDto;
import com.recipe_manager.model.dto.recipe.RecipeIngredientDto;
import com.recipe_manager.model.dto.request.AddIngredientCommentRequest;
import com.recipe_manager.model.dto.request.DeleteIngredientCommentRequest;
import com.recipe_manager.model.dto.request.EditIngredientCommentRequest;
import com.recipe_manager.model.dto.response.IngredientCommentResponse;
import com.recipe_manager.model.dto.response.RecipeIngredientsResponse;
import com.recipe_manager.model.dto.response.ShoppingListResponse;
import com.recipe_manager.model.dto.shopping.ShoppingListItemDto;
import com.recipe_manager.model.entity.ingredient.Ingredient;
import com.recipe_manager.model.entity.ingredient.IngredientComment;
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.entity.recipe.RecipeIngredient;
import com.recipe_manager.model.enums.IngredientUnit;
import com.recipe_manager.model.mapper.IngredientCommentMapper;
import com.recipe_manager.model.mapper.RecipeIngredientMapper;
import com.recipe_manager.model.mapper.ShoppingListMapper;
import com.recipe_manager.repository.ingredient.IngredientCommentRepository;
import com.recipe_manager.repository.ingredient.IngredientRepository;
import com.recipe_manager.repository.recipe.RecipeIngredientRepository;
import com.recipe_manager.service.external.RecipeScraperService;
import com.recipe_manager.util.SecurityUtils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Unit tests for IngredientService.
 *
 * <p>
 * Tests cover all placeholder methods:
 * <ul>
 * <li>getIngredients</li>
 * <li>scaleIngredients</li>
 * <li>generateShoppingList</li>
 * <li>addComment</li>
 * <li>editComment</li>
 * <li>deleteComment</li>
 * <li>addMedia</li>
 * <li>updateMedia</li>
 * <li>deleteMedia</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class IngredientServiceTest {

  @Mock
  private RecipeIngredientRepository recipeIngredientRepository;

  @Mock
  private IngredientRepository ingredientRepository;

  @Mock
  private IngredientCommentRepository ingredientCommentRepository;

  @Mock
  private RecipeIngredientMapper recipeIngredientMapper;

  @Mock
  private IngredientCommentMapper ingredientCommentMapper;

  @Mock
  private ShoppingListMapper shoppingListMapper;

  @Mock
  private RecipeScraperService recipeScraperService;

  @InjectMocks
  private IngredientService ingredientService;

  @Test
  @Tag("standard-processing")
  @DisplayName("Should get ingredients for recipe successfully")
  void shouldGetIngredientsForRecipeSuccessfully() {
    // Given
    String recipeId = "123";
    Long id = 123L;

    RecipeIngredient ingredient1 = createMockRecipeIngredient(id, 1L, "Salt", new BigDecimal("1.5"),
        IngredientUnit.TSP);
    RecipeIngredient ingredient2 = createMockRecipeIngredient(id, 2L, "Pepper", new BigDecimal("0.5"),
        IngredientUnit.TSP);
    List<RecipeIngredient> ingredients = Arrays.asList(ingredient1, ingredient2);

    RecipeIngredientDto dto1 = createMockRecipeIngredientDto(id, 1L, "Salt", new BigDecimal("1.5"), IngredientUnit.TSP);
    RecipeIngredientDto dto2 = createMockRecipeIngredientDto(id, 2L, "Pepper", new BigDecimal("0.5"),
        IngredientUnit.TSP);
    List<RecipeIngredientDto> dtos = Arrays.asList(dto1, dto2);

    when(recipeIngredientRepository.findByRecipeRecipeId(id)).thenReturn(ingredients);
    when(recipeIngredientMapper.toDtoList(ingredients)).thenReturn(dtos);

    // Mock comment repository calls for each ingredient
    when(ingredientCommentRepository.findByIngredientIngredientIdOrderByCreatedAtAsc(1L))
        .thenReturn(new ArrayList<>());
    when(ingredientCommentRepository.findByIngredientIngredientIdOrderByCreatedAtAsc(2L))
        .thenReturn(new ArrayList<>());
    when(ingredientCommentMapper.toDtoList(anyList())).thenReturn(new ArrayList<>());

    // When
    ResponseEntity<RecipeIngredientsResponse> response = ingredientService.getIngredients(recipeId);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getRecipeId()).isEqualTo(id);
    assertThat(response.getBody().getIngredients()).hasSize(2);
    assertThat(response.getBody().getTotalCount()).isEqualTo(2);

    // Verify comment repository was called for each ingredient
    verify(ingredientCommentRepository).findByIngredientIngredientIdOrderByCreatedAtAsc(1L);
    verify(ingredientCommentRepository).findByIngredientIngredientIdOrderByCreatedAtAsc(2L);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should scale ingredients successfully")
  void shouldScaleIngredientsSuccessfully() {
    // Given
    String recipeId = "123";
    Long id = 123L;
    float scaleFactor = 2.5f;

    RecipeIngredient ingredient1 = createMockRecipeIngredient(id, 1L, "Salt", new BigDecimal("1.0"),
        IngredientUnit.TSP);
    RecipeIngredient ingredient2 = createMockRecipeIngredient(id, 2L, "Pepper", new BigDecimal("0.5"),
        IngredientUnit.TSP);
    List<RecipeIngredient> ingredients = Arrays.asList(ingredient1, ingredient2);

    RecipeIngredientDto scaledDto1 = createMockRecipeIngredientDto(id, 1L, "Salt", new BigDecimal("2.5"),
        IngredientUnit.TSP);
    RecipeIngredientDto scaledDto2 = createMockRecipeIngredientDto(id, 2L, "Pepper", new BigDecimal("1.25"),
        IngredientUnit.TSP);
    List<RecipeIngredientDto> scaledDtos = Arrays.asList(scaledDto1, scaledDto2);

    when(recipeIngredientRepository.findByRecipeRecipeId(id)).thenReturn(ingredients);
    when(recipeIngredientMapper.toDtoListWithScale(ingredients, scaleFactor)).thenReturn(scaledDtos);

    // Mock comment repository calls for each ingredient
    when(ingredientCommentRepository.findByIngredientIngredientIdOrderByCreatedAtAsc(1L))
        .thenReturn(new ArrayList<>());
    when(ingredientCommentRepository.findByIngredientIngredientIdOrderByCreatedAtAsc(2L))
        .thenReturn(new ArrayList<>());
    when(ingredientCommentMapper.toDtoList(anyList())).thenReturn(new ArrayList<>());

    // When
    ResponseEntity<RecipeIngredientsResponse> response = ingredientService.scaleIngredients(recipeId, scaleFactor);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getRecipeId()).isEqualTo(id);
    assertThat(response.getBody().getIngredients()).hasSize(2);
    assertThat(response.getBody().getTotalCount()).isEqualTo(2);
    assertThat(response.getBody().getIngredients().get(0).getQuantity()).isEqualTo(new BigDecimal("2.5"));
    assertThat(response.getBody().getIngredients().get(1).getQuantity()).isEqualTo(new BigDecimal("1.25"));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should generate shopping list successfully")
  void shouldGenerateShoppingListSuccessfully() {
    // Given
    String recipeId = "123";
    Long id = 123L;

    RecipeIngredient ingredient1 = createMockRecipeIngredient(id, 1L, "Salt", new BigDecimal("1.5"),
        IngredientUnit.TSP);
    RecipeIngredient ingredient2 = createMockRecipeIngredient(id, 2L, "Pepper", new BigDecimal("0.5"),
        IngredientUnit.TSP);
    List<RecipeIngredient> ingredients = Arrays.asList(ingredient1, ingredient2);

    ShoppingListItemDto item1 = createMockShoppingListItem("Salt", new BigDecimal("1.5"), IngredientUnit.TSP, false);
    ShoppingListItemDto item2 = createMockShoppingListItem("Pepper", new BigDecimal("0.5"), IngredientUnit.TSP, false);
    List<ShoppingListItemDto> shoppingListItems = Arrays.asList(item1, item2);

    // Mock external service to return empty pricing data for unit tests
    RecipeScraperShoppingDto emptyPricingData = RecipeScraperShoppingDto.builder()
        .recipeId(id)
        .ingredients(java.util.Collections.emptyMap())
        .totalEstimatedCost(BigDecimal.ZERO)
        .build();

    when(recipeIngredientRepository.findByRecipeRecipeId(id)).thenReturn(ingredients);
    when(shoppingListMapper.toAggregatedShoppingListItems(ingredients)).thenReturn(shoppingListItems);
    when(recipeScraperService.getShoppingInfo(id)).thenReturn(CompletableFuture.completedFuture(emptyPricingData));
    when(shoppingListMapper.mergeWithPricingData(shoppingListItems, emptyPricingData)).thenReturn(shoppingListItems);

    // When
    ResponseEntity<ShoppingListResponse> response = ingredientService.generateShoppingList(recipeId);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getRecipeId()).isEqualTo(id);
    assertThat(response.getBody().getItems()).hasSize(2);
    assertThat(response.getBody().getTotalCount()).isEqualTo(2);
    assertThat(response.getBody().getItems().get(0).getIngredientName()).isEqualTo("Salt");
    assertThat(response.getBody().getItems().get(1).getIngredientName()).isEqualTo("Pepper");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should add comment to ingredient successfully")
  void shouldAddCommentToIngredientSuccessfully() {
    // Given
    String recipeId = "123";
    String ingredientId = "456";
    Long recipeIdLong = 123L;
    Long ingredientIdLong = 456L;
    UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

    AddIngredientCommentRequest request = AddIngredientCommentRequest.builder()
        .comment("Test comment")
        .build();

    // Create mock entities
    Recipe recipe = new Recipe();
    recipe.setRecipeId(recipeIdLong);

    Ingredient ingredient = new Ingredient();
    ingredient.setIngredientId(ingredientIdLong);

    RecipeIngredient recipeIngredient = new RecipeIngredient();
    recipeIngredient.setRecipe(recipe);
    recipeIngredient.setIngredient(ingredient);

    // Mock repository calls
    when(recipeIngredientRepository.findByRecipeRecipeIdAndIngredientIngredientId(recipeIdLong, ingredientIdLong))
        .thenReturn(Optional.of(recipeIngredient));
    when(ingredientCommentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    when(ingredientCommentRepository.findByIngredientIngredientIdOrderByCreatedAtAsc(ingredientIdLong))
        .thenReturn(java.util.Collections.emptyList());
    when(ingredientCommentMapper.toDtoList(anyList())).thenReturn(java.util.Collections.emptyList());

    // Mock SecurityUtils
    try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
      securityMock.when(SecurityUtils::getCurrentUserId).thenReturn(userId);

      // When
      ResponseEntity<IngredientCommentResponse> response = ingredientService.addComment(recipeId, ingredientId,
          request);

      // Then
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody().getRecipeId()).isEqualTo(recipeIdLong);
      assertThat(response.getBody().getIngredientId()).isEqualTo(ingredientIdLong);
      assertThat(response.getBody().getComments()).hasSize(0); // Empty list for this test
    }
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should edit comment on ingredient successfully")
  void shouldEditCommentOnIngredientSuccessfully() {
    // Given
    String recipeId = "123";
    String ingredientId = "456";
    Long recipeIdLong = 123L;
    Long ingredientIdLong = 456L;

    EditIngredientCommentRequest request = EditIngredientCommentRequest.builder()
        .commentId(1L)
        .comment("Updated comment")
        .build();

    // Create mock entities
    Recipe recipe = new Recipe();
    recipe.setRecipeId(recipeIdLong);

    Ingredient ingredient = new Ingredient();
    ingredient.setIngredientId(ingredientIdLong);

    RecipeIngredient recipeIngredient = new RecipeIngredient();
    recipeIngredient.setRecipe(recipe);
    recipeIngredient.setIngredient(ingredient);

    // Mock repository calls
    when(recipeIngredientRepository.findByRecipeRecipeIdAndIngredientIngredientId(recipeIdLong, ingredientIdLong))
        .thenReturn(Optional.of(recipeIngredient));
    when(ingredientCommentRepository.findByCommentIdAndIngredientIngredientId(1L, ingredientIdLong))
        .thenReturn(Optional.of(new IngredientComment()));
    when(ingredientCommentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    when(ingredientCommentRepository.findByIngredientIngredientIdOrderByCreatedAtAsc(ingredientIdLong))
        .thenReturn(java.util.Collections.emptyList());
    when(ingredientCommentMapper.toDtoList(anyList())).thenReturn(java.util.Collections.emptyList());

    // When
    ResponseEntity<IngredientCommentResponse> response = ingredientService.editComment(recipeId, ingredientId, request);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getComments()).hasSize(0); // Empty list for this test
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should delete comment from ingredient successfully")
  void shouldDeleteCommentFromIngredientSuccessfully() {
    // Given
    String recipeId = "123";
    String ingredientId = "456";
    Long recipeIdLong = 123L;
    Long ingredientIdLong = 456L;

    DeleteIngredientCommentRequest request = DeleteIngredientCommentRequest.builder()
        .commentId(1L)
        .build();

    // Create mock entities
    Recipe recipe = new Recipe();
    recipe.setRecipeId(recipeIdLong);

    Ingredient ingredient = new Ingredient();
    ingredient.setIngredientId(ingredientIdLong);

    RecipeIngredient recipeIngredient = new RecipeIngredient();
    recipeIngredient.setRecipe(recipe);
    recipeIngredient.setIngredient(ingredient);

    // Mock repository calls
    when(recipeIngredientRepository.findByRecipeRecipeIdAndIngredientIngredientId(recipeIdLong, ingredientIdLong))
        .thenReturn(Optional.of(recipeIngredient));
    when(ingredientCommentRepository.findByCommentIdAndIngredientIngredientId(1L, ingredientIdLong))
        .thenReturn(Optional.of(new IngredientComment()));
    when(ingredientCommentRepository.findByIngredientIngredientIdOrderByCreatedAtAsc(ingredientIdLong))
        .thenReturn(java.util.Collections.emptyList());
    when(ingredientCommentMapper.toDtoList(anyList())).thenReturn(java.util.Collections.emptyList());

    // When
    ResponseEntity<IngredientCommentResponse> response = ingredientService.deleteComment(recipeId, ingredientId,
        request);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getComments()).isEmpty();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should add media to ingredient successfully")
  void shouldAddMediaToIngredientSuccessfully() {
    // Given
    String recipeId = "recipe-123";
    String ingredientId = "ingredient-456";

    // When
    ResponseEntity<String> response = ingredientService.addMedia(recipeId, ingredientId);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("Add Media Ref to Ingredient - placeholder");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should update media on ingredient successfully")
  void shouldUpdateMediaOnIngredientSuccessfully() {
    // Given
    String recipeId = "recipe-123";
    String ingredientId = "ingredient-456";

    // When
    ResponseEntity<String> response = ingredientService.updateMedia(recipeId, ingredientId);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("Update Media Ref on Ingredient - placeholder");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should delete media from ingredient successfully")
  void shouldDeleteMediaFromIngredientSuccessfully() {
    // Given
    String recipeId = "recipe-123";
    String ingredientId = "ingredient-456";

    // When
    ResponseEntity<String> response = ingredientService.deleteMedia(recipeId, ingredientId);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("Delete Media Ref from Ingredient - placeholder");
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should handle invalid recipe ID gracefully")
  void shouldHandleInvalidRecipeIdGracefully() {
    // Given
    String invalidRecipeId = "invalid";

    // When/Then
    BusinessException exception = assertThrows(BusinessException.class, () -> {
      ingredientService.getIngredients(invalidRecipeId);
    });

    assertThat(exception.getMessage()).isEqualTo("Invalid recipe ID: invalid");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should get ingredients with comments successfully")
  void shouldGetIngredientsWithCommentsSuccessfully() {
    // Given
    String recipeId = "123";
    Long id = 123L;

    RecipeIngredient ingredient1 = createMockRecipeIngredient(id, 1L, "Salt", new BigDecimal("1.5"),
        IngredientUnit.TSP);
    List<RecipeIngredient> ingredients = Arrays.asList(ingredient1);

    RecipeIngredientDto dto1 = createMockRecipeIngredientDto(id, 1L, "Salt", new BigDecimal("1.5"), IngredientUnit.TSP);
    List<RecipeIngredientDto> dtos = Arrays.asList(dto1);

    // Create mock comments
    IngredientComment comment1 = IngredientComment.builder()
        .commentId(1L)
        .recipeId(id)
        .userId(UUID.randomUUID())
        .commentText("Great ingredient!")
        .isPublic(true)
        .build();

    IngredientComment comment2 = IngredientComment.builder()
        .commentId(2L)
        .recipeId(id)
        .userId(UUID.randomUUID())
        .commentText("Very fresh")
        .isPublic(false)
        .build();

    List<IngredientComment> comments = Arrays.asList(comment1, comment2);

    when(recipeIngredientRepository.findByRecipeRecipeId(id)).thenReturn(ingredients);
    when(recipeIngredientMapper.toDtoList(ingredients)).thenReturn(dtos);
    when(ingredientCommentRepository.findByIngredientIngredientIdOrderByCreatedAtAsc(1L))
        .thenReturn(comments);
    when(ingredientCommentMapper.toDtoList(comments)).thenReturn(Arrays.asList(
        com.recipe_manager.model.dto.ingredient.IngredientCommentDto.builder()
            .commentId(1L).commentText("Great ingredient!").build(),
        com.recipe_manager.model.dto.ingredient.IngredientCommentDto.builder()
            .commentId(2L).commentText("Very fresh").build()));

    // When
    ResponseEntity<RecipeIngredientsResponse> response = ingredientService.getIngredients(recipeId);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getRecipeId()).isEqualTo(id);
    assertThat(response.getBody().getIngredients()).hasSize(1);
    assertThat(response.getBody().getIngredients().get(0).getComments()).hasSize(2);

    // Verify comment repository was called
    verify(ingredientCommentRepository).findByIngredientIngredientIdOrderByCreatedAtAsc(1L);
    verify(ingredientCommentMapper).toDtoList(comments);
  }

  private RecipeIngredient createMockRecipeIngredient(Long recipeId, Long ingredientId, String ingredientName,
      BigDecimal quantity, IngredientUnit unit) {
    Recipe recipe = new Recipe();
    recipe.setRecipeId(recipeId);

    Ingredient ingredient = new Ingredient();
    ingredient.setIngredientId(ingredientId);
    ingredient.setName(ingredientName);

    RecipeIngredient recipeIngredient = new RecipeIngredient();
    recipeIngredient.setRecipe(recipe);
    recipeIngredient.setIngredient(ingredient);
    recipeIngredient.setQuantity(quantity);
    recipeIngredient.setUnit(unit);
    recipeIngredient.setIsOptional(false);

    return recipeIngredient;
  }

  private RecipeIngredientDto createMockRecipeIngredientDto(Long recipeId, Long ingredientId, String ingredientName,
      BigDecimal quantity, IngredientUnit unit) {
    return RecipeIngredientDto.builder()
        .recipeId(recipeId)
        .ingredientId(ingredientId)
        .ingredientName(ingredientName)
        .quantity(quantity)
        .unit(unit)
        .isOptional(false)
        .comments(new ArrayList<>())
        .build();
  }

  private ShoppingListItemDto createMockShoppingListItem(String ingredientName, BigDecimal totalQuantity,
      IngredientUnit unit, Boolean isOptional) {
    return ShoppingListItemDto.builder()
        .ingredientName(ingredientName)
        .totalQuantity(totalQuantity)
        .unit(unit)
        .isOptional(isOptional)
        .build();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle null ingredient ID gracefully")
  void shouldHandleNullIngredientIdGracefully() {
    AddIngredientCommentRequest request = AddIngredientCommentRequest.builder()
        .comment("Test comment")
        .build();

    // When/Then
    BusinessException exception = assertThrows(BusinessException.class, () -> {
      ingredientService.addComment("recipe-123", null, request);
    });

    assertThat(exception.getMessage()).contains("Invalid recipe ID: recipe-123");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle zero quantity scaling gracefully")
  void shouldHandleZeroQuantityScalingGracefully() {
    // Given
    String recipeId = "123";
    Long id = 123L;
    float scaleFactor = 0.0f;

    RecipeIngredient ingredient1 = createMockRecipeIngredient(id, 1L, "Salt", new BigDecimal("1.0"),
        IngredientUnit.TSP);
    List<RecipeIngredient> ingredients = Arrays.asList(ingredient1);

    RecipeIngredientDto scaledDto1 = createMockRecipeIngredientDto(id, 1L, "Salt", new BigDecimal("0.0"),
        IngredientUnit.TSP);
    List<RecipeIngredientDto> scaledDtos = Arrays.asList(scaledDto1);

    when(recipeIngredientRepository.findByRecipeRecipeId(id)).thenReturn(ingredients);
    when(recipeIngredientMapper.toDtoListWithScale(ingredients, scaleFactor)).thenReturn(scaledDtos);

    // When
    ResponseEntity<RecipeIngredientsResponse> response = ingredientService.scaleIngredients(recipeId, scaleFactor);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getIngredients().get(0).getQuantity()).isEqualTo(new BigDecimal("0.0"));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should generate shopping list with duplicate ingredient aggregation")
  void shouldGenerateShoppingListWithDuplicateIngredientAggregation() {
    // Given
    String recipeId = "123";
    Long id = 123L;

    RecipeIngredient ingredient1 = createMockRecipeIngredient(id, 1L, "Salt", new BigDecimal("1.0"),
        IngredientUnit.TSP);
    RecipeIngredient ingredient2 = createMockRecipeIngredient(id, 2L, "Salt", new BigDecimal("0.5"),
        IngredientUnit.TSP);
    List<RecipeIngredient> ingredients = Arrays.asList(ingredient1, ingredient2);

    ShoppingListItemDto aggregatedItem = createMockShoppingListItem("Salt", new BigDecimal("1.5"), IngredientUnit.TSP,
        false);
    List<ShoppingListItemDto> shoppingListItems = Arrays.asList(aggregatedItem);

    // Mock external service to return empty pricing data for unit tests
    RecipeScraperShoppingDto emptyPricingData = RecipeScraperShoppingDto.builder()
        .recipeId(id)
        .ingredients(java.util.Collections.emptyMap())
        .totalEstimatedCost(BigDecimal.ZERO)
        .build();

    when(recipeIngredientRepository.findByRecipeRecipeId(id)).thenReturn(ingredients);
    when(shoppingListMapper.toAggregatedShoppingListItems(ingredients)).thenReturn(shoppingListItems);
    when(recipeScraperService.getShoppingInfo(id)).thenReturn(CompletableFuture.completedFuture(emptyPricingData));
    when(shoppingListMapper.mergeWithPricingData(shoppingListItems, emptyPricingData)).thenReturn(shoppingListItems);

    // When
    ResponseEntity<ShoppingListResponse> response = ingredientService.generateShoppingList(recipeId);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getItems()).hasSize(1);
    assertThat(response.getBody().getItems().get(0).getIngredientName()).isEqualTo("Salt");
    assertThat(response.getBody().getItems().get(0).getTotalQuantity()).isEqualTo(new BigDecimal("1.5"));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should handle invalid recipe ID for shopping list generation")
  void shouldHandleInvalidRecipeIdForShoppingListGeneration() {
    // Given
    String invalidRecipeId = "invalid";

    // When/Then
    BusinessException exception = assertThrows(BusinessException.class, () -> {
      ingredientService.generateShoppingList(invalidRecipeId);
    });

    assertThat(exception.getMessage()).isEqualTo("Invalid recipe ID: invalid");
  }
}
