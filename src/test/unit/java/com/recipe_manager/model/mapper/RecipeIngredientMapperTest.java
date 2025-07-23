package com.recipe_manager.model.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;

import com.recipe_manager.model.dto.recipe.RecipeIngredientDto;
import com.recipe_manager.model.entity.ingredient.Ingredient;
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.entity.recipe.RecipeIngredient;
import com.recipe_manager.model.enums.IngredientUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

/**
 * Unit tests for RecipeIngredientMapper.
 */
@Tag("unit")
class RecipeIngredientMapperTest {

  private final RecipeIngredientMapper mapper = Mappers.getMapper(RecipeIngredientMapper.class);

  @Test
  @Tag("standard-processing")
  @DisplayName("Should map all simple fields from CreateRecipeIngredientRequest to RecipeIngredient")
  void shouldMapAllSimpleFields() {
    RecipeIngredientDto dto = RecipeIngredientDto.builder()
        .ingredientName("Sugar")
        .ingredientId(42L)
        .quantity(BigDecimal.valueOf(2.5))
        .unit(null)
        .isOptional(true)
        .notes("Fine")
        .build();

    RecipeIngredient entity = mapper.toEntity(dto);

    assertThat(entity).isNotNull();
    assertThat(entity.getQuantity()).isEqualTo(dto.getQuantity());
    assertThat(entity.getUnit()).isEqualTo(dto.getUnit());
    assertThat(entity.getIsOptional()).isEqualTo(Boolean.TRUE);
    // Ignored fields
    assertThat(entity.getId()).isNull();
    assertThat(entity.getIngredient()).isNull();
    assertThat(entity.getRecipe()).isNull();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should map a list of CreateRecipeIngredientRequest to RecipeIngredient list")
  void shouldMapList() {
    RecipeIngredientDto dto1 = RecipeIngredientDto.builder().ingredientName("A")
        .quantity(BigDecimal.ONE).build();
    RecipeIngredientDto dto2 = RecipeIngredientDto.builder().ingredientName("B")
        .quantity(BigDecimal.TEN).build();
    List<RecipeIngredient> entities = mapper.toEntityList(List.of(dto1, dto2));
    assertThat(entities).hasSize(2);
    assertThat(entities.get(0).getQuantity()).isEqualTo(BigDecimal.ONE);
    assertThat(entities.get(1).getQuantity()).isEqualTo(BigDecimal.TEN);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should map RecipeIngredient entity to RecipeIngredientDto")
  void shouldMapEntityToDto() {
    Ingredient ingredient = Ingredient.builder()
        .ingredientId(5L)
        .name("Sugar")
        .build();

    Recipe recipe = Recipe.builder()
        .recipeId(10L)
        .build();

    RecipeIngredient entity = RecipeIngredient.builder()
        .ingredient(ingredient)
        .recipe(recipe)
        .quantity(BigDecimal.valueOf(2.5))
        .unit(IngredientUnit.CUP)
        .isOptional(true)
        .build();

    RecipeIngredientDto result = mapper.toDto(entity);

    assertThat(result).isNotNull();
    assertThat(result.getIngredientId()).isEqualTo(5L);
    assertThat(result.getIngredientName()).isEqualTo("Sugar");
    assertThat(result.getRecipeId()).isEqualTo(10L);
    assertThat(result.getQuantity()).isEqualTo(BigDecimal.valueOf(2.5));
    assertThat(result.getUnit()).isEqualTo(IngredientUnit.CUP);
    assertThat(result.getIsOptional()).isTrue();
    // Ignored fields should be null or default values
    assertThat(result.getMedia()).isNotNull().isEmpty(); // MapStruct returns empty list for @Default fields
    assertThat(result.getNotes()).isNull();
    assertThat(result.getCreatedAt()).isNull();
    assertThat(result.getUpdatedAt()).isNull();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should map a list of RecipeIngredient entities to RecipeIngredientDto list")
  void shouldMapEntityListToDto() {
    Ingredient ingredient1 = Ingredient.builder()
        .ingredientId(1L)
        .name("Flour")
        .build();

    Ingredient ingredient2 = Ingredient.builder()
        .ingredientId(2L)
        .name("Sugar")
        .build();

    Recipe recipe = Recipe.builder()
        .recipeId(10L)
        .build();

    RecipeIngredient entity1 = RecipeIngredient.builder()
        .ingredient(ingredient1)
        .recipe(recipe)
        .quantity(BigDecimal.valueOf(1.0))
        .unit(IngredientUnit.CUP)
        .isOptional(false)
        .build();

    RecipeIngredient entity2 = RecipeIngredient.builder()
        .ingredient(ingredient2)
        .recipe(recipe)
        .quantity(BigDecimal.valueOf(0.5))
        .unit(IngredientUnit.CUP)
        .isOptional(true)
        .build();

    List<RecipeIngredientDto> results = mapper.toDtoList(List.of(entity1, entity2));

    assertThat(results).hasSize(2);
    assertThat(results.get(0).getIngredientName()).isEqualTo("Flour");
    assertThat(results.get(0).getQuantity()).isEqualTo(BigDecimal.valueOf(1.0));
    assertThat(results.get(0).getIsOptional()).isFalse();
    assertThat(results.get(1).getIngredientName()).isEqualTo("Sugar");
    assertThat(results.get(1).getQuantity()).isEqualTo(BigDecimal.valueOf(0.5));
    assertThat(results.get(1).getIsOptional()).isTrue();
  }
}
