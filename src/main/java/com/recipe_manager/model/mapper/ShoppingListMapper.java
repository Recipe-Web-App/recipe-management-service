package com.recipe_manager.model.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.recipe_manager.model.dto.external.recipescraper.IngredientShoppingInfoDto;
import com.recipe_manager.model.dto.external.recipescraper.RecipeScraperShoppingDto;
import com.recipe_manager.model.dto.shopping.ShoppingListItemDto;
import com.recipe_manager.model.entity.recipe.RecipeIngredient;
import com.recipe_manager.model.enums.IngredientUnit;

/** MapStruct mapper for converting RecipeIngredient entities to shopping list items. */
@Mapper(componentModel = "spring")
public interface ShoppingListMapper {

  /**
   * Maps a RecipeIngredient entity to a ShoppingListItemDto.
   *
   * @param entity the RecipeIngredient entity
   * @return the mapped ShoppingListItemDto
   */
  @Mapping(target = "ingredientId", source = "ingredient.ingredientId")
  @Mapping(target = "ingredientName", source = "ingredient.name")
  @Mapping(target = "totalQuantity", source = "quantity")
  @Mapping(target = "unit", source = "unit")
  @Mapping(target = "isOptional", source = "isOptional")
  @Mapping(target = "estimatedPrice", ignore = true)
  ShoppingListItemDto toShoppingListItem(RecipeIngredient entity);

  /**
   * Converts a list of RecipeIngredient entities to a list of aggregated ShoppingListItemDto.
   * Ingredients with the same name and unit are combined, with quantities summed.
   *
   * @param entities the list of RecipeIngredient entities
   * @return the aggregated list of ShoppingListItemDto
   */
  default List<ShoppingListItemDto> toAggregatedShoppingListItems(List<RecipeIngredient> entities) {
    // Group by ingredient name and unit
    Map<String, List<RecipeIngredient>> groupedIngredients =
        entities.stream()
            .collect(
                Collectors.groupingBy(
                    entity -> entity.getIngredient().getName() + "_" + entity.getUnit()));

    return groupedIngredients.values().stream().map(this::aggregateIngredients).toList();
  }

  /**
   * Aggregates a list of RecipeIngredient entities with the same name and unit into a single
   * ShoppingListItemDto.
   *
   * @param ingredients the list of ingredients to aggregate
   * @return the aggregated ShoppingListItemDto
   */
  default ShoppingListItemDto aggregateIngredients(List<RecipeIngredient> ingredients) {
    if (ingredients.isEmpty()) {
      return null;
    }

    RecipeIngredient first = ingredients.get(0);
    Long ingredientId = first.getIngredient().getIngredientId();
    String ingredientName = first.getIngredient().getName();
    IngredientUnit unit = first.getUnit();

    // Sum quantities
    BigDecimal totalQuantity =
        ingredients.stream()
            .map(RecipeIngredient::getQuantity)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    // If any ingredient is required (not optional), the shopping list item is required
    boolean isOptional = ingredients.stream().allMatch(RecipeIngredient::getIsOptional);

    return ShoppingListItemDto.builder()
        .ingredientId(ingredientId)
        .ingredientName(ingredientName)
        .totalQuantity(totalQuantity)
        .unit(unit)
        .isOptional(isOptional)
        .estimatedPrice(null) // Will be set by mergeWithPricingData if available
        .build();
  }

  /**
   * Merges shopping list items with external pricing data from recipe scraper service.
   *
   * @param shoppingListItems the list of shopping list items
   * @param pricingData the external pricing data, may be null
   * @return the shopping list items with pricing information merged
   */
  default List<ShoppingListItemDto> mergeWithPricingData(
      List<ShoppingListItemDto> shoppingListItems, RecipeScraperShoppingDto pricingData) {
    if (pricingData == null || pricingData.getIngredients() == null) {
      // Return original items with null pricing if no external data available
      return shoppingListItems;
    }

    return shoppingListItems.stream()
        .map(item -> mergeItemWithPricing(item, pricingData.getIngredients()))
        .toList();
  }

  /**
   * Merges a single shopping list item with its corresponding pricing data.
   *
   * @param item the shopping list item
   * @param pricingMap the map of ingredient IDs to pricing information
   * @return the shopping list item with pricing information merged
   */
  default ShoppingListItemDto mergeItemWithPricing(
      ShoppingListItemDto item, Map<String, IngredientShoppingInfoDto> pricingMap) {
    // The external service now uses ingredient ID as the map key
    String ingredientId = item.getIngredientId() != null ? item.getIngredientId().toString() : null;
    IngredientShoppingInfoDto pricingInfo =
        ingredientId != null ? pricingMap.get(ingredientId) : null;

    if (pricingInfo != null) {
      return ShoppingListItemDto.builder()
          .ingredientId(item.getIngredientId())
          .ingredientName(item.getIngredientName())
          .totalQuantity(item.getTotalQuantity())
          .unit(item.getUnit())
          .isOptional(item.getIsOptional())
          .estimatedPrice(pricingInfo.getEstimatedPrice())
          .build();
    }

    // Return original item if no pricing data available
    return item;
  }
}
