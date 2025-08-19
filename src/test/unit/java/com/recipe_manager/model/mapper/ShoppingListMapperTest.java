package com.recipe_manager.model.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mapstruct.factory.Mappers;

import com.recipe_manager.model.dto.external.recipescraper.IngredientShoppingInfoDto;
import com.recipe_manager.model.dto.external.recipescraper.RecipeScraperShoppingDto;
import com.recipe_manager.model.dto.ingredient.QuantityDto;
import com.recipe_manager.model.dto.shopping.ShoppingListItemDto;
import com.recipe_manager.model.entity.ingredient.Ingredient;
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.entity.recipe.RecipeIngredient;
import com.recipe_manager.model.enums.IngredientUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class ShoppingListMapperTest {

  private ShoppingListMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = Mappers.getMapper(ShoppingListMapper.class);
  }

  @Test
  @DisplayName("Should map recipe ingredients to shopping list items")
  void shouldMapRecipeIngredientsToShoppingItems() {
    // Arrange
    Recipe recipe = Recipe.builder().recipeId(123L).build();
    List<RecipeIngredient> ingredients = createTestIngredients(recipe);

    // Act
    List<ShoppingListItemDto> items = mapper.toAggregatedShoppingListItems(ingredients);

    // Assert
    assertThat(items).hasSize(2);
    ShoppingListItemDto saltItem = items.stream()
        .filter(i -> i.getIngredientName().equals("Salt"))
        .findFirst()
        .orElseThrow();

    assertThat(saltItem.getTotalQuantity())
        .isEqualByComparingTo(new BigDecimal("1.5"));
    assertThat(saltItem.getUnit())
        .isEqualTo(IngredientUnit.TSP);
  }

  @Test
  @DisplayName("Should map single recipe ingredient")
  void shouldMapSingleRecipeIngredient() {
    // Arrange
    Recipe recipe = Recipe.builder().recipeId(123L).build();
    Ingredient salt = Ingredient.builder()
        .ingredientId(1L)
        .name("Salt")
        .build();
    RecipeIngredient recipeIngredient = RecipeIngredient.builder()
        .recipe(recipe)
        .ingredient(salt)
        .quantity(new BigDecimal("1.0"))
        .unit(IngredientUnit.TSP)
        .isOptional(false)
        .build();

    // Act
    ShoppingListItemDto item = mapper.toShoppingListItem(recipeIngredient);

    // Assert
    assertThat(item.getIngredientId()).isEqualTo(1L);
    assertThat(item.getIngredientName()).isEqualTo("Salt");
    assertThat(item.getTotalQuantity()).isEqualByComparingTo(new BigDecimal("1.0"));
    assertThat(item.getUnit()).isEqualTo(IngredientUnit.TSP);
    assertThat(item.getIsOptional()).isFalse();
    assertThat(item.getEstimatedPrice()).isNull();
  }

  @Test
  @DisplayName("Should handle null recipe ingredient")
  void shouldHandleNullRecipeIngredient() {
    // Act
    ShoppingListItemDto item = mapper.toShoppingListItem(null);

    // Assert
    assertThat(item).isNull();
  }

  @Test
  @DisplayName("Should handle recipe ingredient with null ingredient")
  void shouldHandleRecipeIngredientWithNullIngredient() {
    // Arrange
    Recipe recipe = Recipe.builder().recipeId(123L).build();
    RecipeIngredient recipeIngredient = RecipeIngredient.builder()
        .recipe(recipe)
        .ingredient(null)
        .quantity(new BigDecimal("1.0"))
        .unit(IngredientUnit.TSP)
        .isOptional(false)
        .build();

    // Act
    ShoppingListItemDto item = mapper.toShoppingListItem(recipeIngredient);

    // Assert
    assertThat(item).isNotNull();
    assertThat(item.getIngredientId()).isNull();
    assertThat(item.getIngredientName()).isNull();
    assertThat(item.getTotalQuantity()).isEqualByComparingTo(new BigDecimal("1.0"));
    assertThat(item.getUnit()).isEqualTo(IngredientUnit.TSP);
    assertThat(item.getIsOptional()).isFalse();
  }

  @Test
  @DisplayName("Should map optional recipe ingredient")
  void shouldMapOptionalRecipeIngredient() {
    // Arrange
    Recipe recipe = Recipe.builder().recipeId(123L).build();
    Ingredient garnish = Ingredient.builder()
        .ingredientId(1L)
        .name("Parsley")
        .build();
    RecipeIngredient recipeIngredient = RecipeIngredient.builder()
        .recipe(recipe)
        .ingredient(garnish)
        .quantity(new BigDecimal("2.0"))
        .unit(IngredientUnit.TBSP)
        .isOptional(true)
        .build();

    // Act
    ShoppingListItemDto item = mapper.toShoppingListItem(recipeIngredient);

    // Assert
    assertThat(item.getIngredientId()).isEqualTo(1L);
    assertThat(item.getIngredientName()).isEqualTo("Parsley");
    assertThat(item.getTotalQuantity()).isEqualByComparingTo(new BigDecimal("2.0"));
    assertThat(item.getUnit()).isEqualTo(IngredientUnit.TBSP);
    assertThat(item.getIsOptional()).isTrue();
  }

  @Test
  @DisplayName("Should aggregate duplicate ingredients with same unit")
  void shouldAggregateDuplicateIngredientsWithSameUnit() {
    // Arrange
    Recipe recipe = Recipe.builder().recipeId(123L).build();
    Ingredient salt = Ingredient.builder()
        .ingredientId(1L)
        .name("Salt")
        .build();

    List<RecipeIngredient> ingredients = Arrays.asList(
        RecipeIngredient.builder()
            .recipe(recipe)
            .ingredient(salt)
            .quantity(new BigDecimal("1.0"))
            .unit(IngredientUnit.TSP)
            .isOptional(false)
            .build(),
        RecipeIngredient.builder()
            .recipe(recipe)
            .ingredient(salt)
            .quantity(new BigDecimal("0.5"))
            .unit(IngredientUnit.TSP)
            .isOptional(false)
            .build());

    // Act
    List<ShoppingListItemDto> items = mapper.toAggregatedShoppingListItems(ingredients);

    // Assert
    assertThat(items).hasSize(1);
    assertThat(items.get(0).getTotalQuantity())
        .isEqualByComparingTo(new BigDecimal("1.5"));
  }

  private List<RecipeIngredient> createTestIngredients(Recipe recipe) {
    Ingredient salt = Ingredient.builder()
        .ingredientId(1L)
        .name("Salt")
        .build();

    Ingredient pepper = Ingredient.builder()
        .ingredientId(2L)
        .name("Pepper")
        .build();

    return Arrays.asList(
        RecipeIngredient.builder()
            .recipe(recipe)
            .ingredient(salt)
            .quantity(new BigDecimal("1.5"))
            .unit(IngredientUnit.TSP)
            .isOptional(false)
            .build(),
        RecipeIngredient.builder()
            .recipe(recipe)
            .ingredient(pepper)
            .quantity(new BigDecimal("0.5"))
            .unit(IngredientUnit.TSP)
            .isOptional(false)
            .build());
  }

  @Test
  @DisplayName("Should merge pricing data with shopping list items")
  void shouldMergePricingDataWithShoppingListItems() {
    // Arrange
    List<ShoppingListItemDto> shoppingItems = Arrays.asList(
        ShoppingListItemDto.builder()
            .ingredientId(1L)
            .ingredientName("Salt")
            .totalQuantity(new BigDecimal("1.5"))
            .unit(IngredientUnit.TSP)
            .isOptional(false)
            .build(),
        ShoppingListItemDto.builder()
            .ingredientId(2L)
            .ingredientName("Pepper")
            .totalQuantity(new BigDecimal("0.5"))
            .unit(IngredientUnit.TSP)
            .isOptional(false)
            .build());

    Map<String, IngredientShoppingInfoDto> pricingData = new HashMap<>();
    pricingData.put("1", IngredientShoppingInfoDto.builder()
        .ingredientName("Salt")
        .quantity(QuantityDto.builder()
            .amount(1.0)
            .measurement(IngredientUnit.TSP)
            .build())
        .estimatedPrice(new BigDecimal("2.99"))
        .build());

    RecipeScraperShoppingDto pricingDto = RecipeScraperShoppingDto.builder()
        .recipeId(123L)
        .ingredients(pricingData)
        .totalEstimatedCost(new BigDecimal("2.99"))
        .build();

    // Act
    List<ShoppingListItemDto> enrichedItems = mapper.mergeWithPricingData(shoppingItems, pricingDto);

    // Assert
    assertThat(enrichedItems).hasSize(2);

    ShoppingListItemDto saltItem = enrichedItems.stream()
        .filter(item -> "Salt".equals(item.getIngredientName()))
        .findFirst()
        .orElseThrow();

    assertThat(saltItem.getEstimatedPrice()).isEqualByComparingTo(new BigDecimal("2.99"));

    ShoppingListItemDto pepperItem = enrichedItems.stream()
        .filter(item -> "Pepper".equals(item.getIngredientName()))
        .findFirst()
        .orElseThrow();

    assertThat(pepperItem.getEstimatedPrice()).isNull(); // No pricing data for pepper
  }

  @Test
  @DisplayName("Should handle null pricing data gracefully")
  void shouldHandleNullPricingData() {
    // Arrange
    List<ShoppingListItemDto> shoppingItems = Arrays.asList(
        ShoppingListItemDto.builder()
            .ingredientId(1L)
            .ingredientName("Salt")
            .totalQuantity(new BigDecimal("1.5"))
            .unit(IngredientUnit.TSP)
            .isOptional(false)
            .build());

    // Act
    List<ShoppingListItemDto> enrichedItems = mapper.mergeWithPricingData(shoppingItems, null);

    // Assert
    assertThat(enrichedItems).hasSize(1);
    assertThat(enrichedItems.get(0).getEstimatedPrice()).isNull();
  }

  @Test
  @DisplayName("Should handle empty pricing ingredients")
  void shouldHandleEmptyPricingIngredients() {
    // Arrange
    List<ShoppingListItemDto> shoppingItems = Arrays.asList(
        ShoppingListItemDto.builder()
            .ingredientId(1L)
            .ingredientName("Salt")
            .totalQuantity(new BigDecimal("1.5"))
            .unit(IngredientUnit.TSP)
            .isOptional(false)
            .build());

    RecipeScraperShoppingDto pricingDto = RecipeScraperShoppingDto.builder()
        .recipeId(123L)
        .ingredients(Collections.emptyMap())
        .totalEstimatedCost(BigDecimal.ZERO)
        .build();

    // Act
    List<ShoppingListItemDto> enrichedItems = mapper.mergeWithPricingData(shoppingItems, pricingDto);

    // Assert
    assertThat(enrichedItems).hasSize(1);
    assertThat(enrichedItems.get(0).getEstimatedPrice()).isNull();
  }

}
