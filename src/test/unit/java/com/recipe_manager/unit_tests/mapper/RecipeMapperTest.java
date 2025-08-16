package com.recipe_manager.unit_tests.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.recipe_manager.model.dto.recipe.RecipeDto;
import com.recipe_manager.model.dto.recipe.RecipeIngredientDto;
import com.recipe_manager.model.dto.recipe.RecipeStepDto;
import com.recipe_manager.model.dto.request.UpdateRecipeRequest;
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.entity.recipe.RecipeIngredient;
import com.recipe_manager.model.entity.recipe.RecipeStep;
import com.recipe_manager.model.enums.DifficultyLevel;
import com.recipe_manager.model.enums.IngredientUnit;
import com.recipe_manager.model.mapper.RecipeMapper;

@SpringBootTest(classes = {
    com.recipe_manager.model.mapper.RecipeMapperImpl.class,
    com.recipe_manager.model.mapper.RecipeIngredientMapperImpl.class,
    com.recipe_manager.model.mapper.RecipeStepMapperImpl.class,
    com.recipe_manager.model.mapper.RecipeFavoriteMapperImpl.class,
    com.recipe_manager.model.mapper.RecipeRevisionMapperImpl.class,
    com.recipe_manager.model.mapper.RecipeTagMapperImpl.class
})
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=none",
    "spring.flyway.enabled=false"
})
@org.junit.jupiter.api.Tag("unit")
class RecipeMapperTest {

  @Autowired
  private RecipeMapper recipeMapper;

  private Recipe recipe;
  private UpdateRecipeRequest updateRequest;
  private UUID userId;
  private LocalDateTime now;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    now = LocalDateTime.now();

    // Create a recipe with ingredients and steps
    recipe = Recipe.builder()
        .recipeId(1L)
        .userId(userId)
        .title("Original Title")
        .description("Original Description")
        .originUrl("https://original.example.com")
        .servings(BigDecimal.valueOf(4.0))
        .preparationTime(30)
        .cookingTime(45)
        .difficulty(DifficultyLevel.MEDIUM)
        .createdAt(now)
        .updatedAt(now)
        .recipeIngredients(new ArrayList<>())
        .recipeSteps(new ArrayList<>())
        .recipeFavorites(new ArrayList<>())
        .recipeRevisions(new ArrayList<>())
        .recipeTags(new ArrayList<>())
        .build();

    // Add some ingredients
    RecipeIngredient ingredient1 = RecipeIngredient.builder()
        .recipe(recipe)
        .quantity(BigDecimal.valueOf(2.0))
        .unit(IngredientUnit.CUP)
        .isOptional(false)
        .build();
    recipe.getRecipeIngredients().add(ingredient1);

    // Add some steps
    RecipeStep step1 = RecipeStep.builder()
        .recipe(recipe)
        .stepNumber(1)
        .instruction("Mix ingredients")
        .build();
    recipe.getRecipeSteps().add(step1);

    // Create update request
    updateRequest = UpdateRecipeRequest.builder()
        .title("Updated Title")
        .description("Updated Description")
        .originUrl("https://updated.example.com")
        .servings(BigDecimal.valueOf(6.0))
        .preparationTime(20)
        .cookingTime(30)
        .difficulty(DifficultyLevel.EASY)
        .ingredients(Arrays.asList(
            RecipeIngredientDto.builder()
                .ingredientName("Sugar")
                .quantity(BigDecimal.valueOf(1.0))
                .unit(IngredientUnit.CUP)
                .build()
        ))
        .steps(Arrays.asList(
            RecipeStepDto.builder()
                .stepNumber(1)
                .instruction("Updated instruction")
                .build()
        ))
        .build();
  }

  @Test
  @DisplayName("Should map Recipe entity to RecipeDto")
  void shouldMapRecipeEntityToDto() {
    // Act
    RecipeDto result = recipeMapper.toDto(recipe);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getRecipeId()).isEqualTo(1L);
    assertThat(result.getUserId()).isEqualTo(userId);
    assertThat(result.getTitle()).isEqualTo("Original Title");
    assertThat(result.getDescription()).isEqualTo("Original Description");
    assertThat(result.getOriginUrl()).isEqualTo("https://original.example.com");
    assertThat(result.getServings()).isEqualByComparingTo(BigDecimal.valueOf(4.0));
    assertThat(result.getPreparationTime()).isEqualTo(30);
    assertThat(result.getCookingTime()).isEqualTo(45);
    assertThat(result.getDifficulty()).isEqualTo(DifficultyLevel.MEDIUM);
    assertThat(result.getCreatedAt()).isEqualTo(now);
    assertThat(result.getUpdatedAt()).isEqualTo(now);

    // Check that collections are mapped
    assertThat(result.getIngredients()).isNotNull();
    assertThat(result.getSteps()).isNotNull();
    assertThat(result.getFavorites()).isNotNull();
    assertThat(result.getRevisions()).isNotNull();
    assertThat(result.getTags()).isNotNull();
  }

  @Test
  @DisplayName("Should handle null Recipe entity")
  void shouldHandleNullRecipeEntity() {
    // Act
    RecipeDto result = recipeMapper.toDto(null);

    // Assert
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("Should map Recipe with null collections")
  void shouldMapRecipeWithNullCollections() {
    // Arrange
    Recipe recipeWithNullCollections = Recipe.builder()
        .recipeId(2L)
        .userId(userId)
        .title("Test Recipe")
        .recipeIngredients(null)
        .recipeSteps(null)
        .recipeFavorites(null)
        .recipeRevisions(null)
        .recipeTags(null)
        .build();

    // Act
    RecipeDto result = recipeMapper.toDto(recipeWithNullCollections);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getRecipeId()).isEqualTo(2L);
    assertThat(result.getTitle()).isEqualTo("Test Recipe");
    assertThat(result.getIngredients()).isNull();
    assertThat(result.getSteps()).isNull();
    assertThat(result.getFavorites()).isNull();
    assertThat(result.getRevisions()).isNull();
    assertThat(result.getTags()).isNull();
  }

  @Test
  @DisplayName("Should update recipe from request")
  void shouldUpdateRecipeFromRequest() {
    // Arrange
    Recipe originalRecipe = Recipe.builder()
        .recipeId(1L)
        .userId(userId)
        .title("Original Title")
        .description("Original Description")
        .originUrl("https://original.example.com")
        .servings(BigDecimal.valueOf(4.0))
        .preparationTime(30)
        .cookingTime(45)
        .difficulty(DifficultyLevel.MEDIUM)
        .createdAt(now)
        .updatedAt(now)
        .recipeIngredients(new ArrayList<>())
        .recipeSteps(new ArrayList<>())
        .build();

    // Act
    recipeMapper.updateRecipeFromRequest(updateRequest, originalRecipe);

    // Assert
    assertThat(originalRecipe.getTitle()).isEqualTo("Updated Title");
    assertThat(originalRecipe.getDescription()).isEqualTo("Updated Description");
    assertThat(originalRecipe.getOriginUrl()).isEqualTo("https://updated.example.com");
    assertThat(originalRecipe.getServings()).isEqualByComparingTo(BigDecimal.valueOf(6.0));
    assertThat(originalRecipe.getPreparationTime()).isEqualTo(20);
    assertThat(originalRecipe.getCookingTime()).isEqualTo(30);
    assertThat(originalRecipe.getDifficulty()).isEqualTo(DifficultyLevel.EASY);

    // Verify ignored fields remain unchanged
    assertThat(originalRecipe.getRecipeId()).isEqualTo(1L);
    assertThat(originalRecipe.getUserId()).isEqualTo(userId);
    assertThat(originalRecipe.getCreatedAt()).isEqualTo(now);
    assertThat(originalRecipe.getUpdatedAt()).isEqualTo(now);

    // Verify ingredients and steps are updated
    assertThat(originalRecipe.getRecipeIngredients()).isNotNull();
    assertThat(originalRecipe.getRecipeSteps()).isNotNull();
  }

  @Test
  @DisplayName("Should handle partial update with null fields")
  void shouldHandlePartialUpdateWithNullFields() {
    // Arrange
    Recipe originalRecipe = Recipe.builder()
        .recipeId(1L)
        .userId(userId)
        .title("Original Title")
        .description("Original Description")
        .servings(BigDecimal.valueOf(4.0))
        .preparationTime(30)
        .build();

    UpdateRecipeRequest partialRequest = UpdateRecipeRequest.builder()
        .title("Updated Title")
        // Other fields are null
        .build();

    // Act
    recipeMapper.updateRecipeFromRequest(partialRequest, originalRecipe);

    // Assert
    assertThat(originalRecipe.getTitle()).isEqualTo("Updated Title");
    // MapStruct by default updates all fields, even null ones
    assertThat(originalRecipe.getDescription()).isNull();
    assertThat(originalRecipe.getServings()).isNull();
    assertThat(originalRecipe.getPreparationTime()).isNull();
  }

  @Test
  @DisplayName("Should handle null update request")
  void shouldHandleNullUpdateRequest() {
    // Arrange
    Recipe originalRecipe = Recipe.builder()
        .title("Original Title")
        .description("Original Description")
        .build();

    // Act
    recipeMapper.updateRecipeFromRequest(null, originalRecipe);

    // Assert - Recipe should remain unchanged
    assertThat(originalRecipe.getTitle()).isEqualTo("Original Title");
    assertThat(originalRecipe.getDescription()).isEqualTo("Original Description");
  }

  @Test
  @DisplayName("Should handle update request with null collections")
  void shouldHandleUpdateRequestWithNullCollections() {
    // Arrange
    Recipe originalRecipe = Recipe.builder()
        .title("Original Title")
        .recipeIngredients(new ArrayList<>())
        .recipeSteps(new ArrayList<>())
        .build();

    UpdateRecipeRequest requestWithNullCollections = UpdateRecipeRequest.builder()
        .title("Updated Title")
        .ingredients(null)
        .steps(null)
        .build();

    // Act
    recipeMapper.updateRecipeFromRequest(requestWithNullCollections, originalRecipe);

    // Assert
    assertThat(originalRecipe.getTitle()).isEqualTo("Updated Title");
    assertThat(originalRecipe.getRecipeIngredients()).isNull();
    assertThat(originalRecipe.getRecipeSteps()).isNull();
  }

  @Test
  @DisplayName("Should handle empty collections in update request")
  void shouldHandleEmptyCollectionsInUpdateRequest() {
    // Arrange
    Recipe originalRecipe = Recipe.builder()
        .title("Original Title")
        .recipeIngredients(new ArrayList<>())
        .recipeSteps(new ArrayList<>())
        .build();

    UpdateRecipeRequest requestWithEmptyCollections = UpdateRecipeRequest.builder()
        .title("Updated Title")
        .ingredients(new ArrayList<>())
        .steps(new ArrayList<>())
        .build();

    // Act
    recipeMapper.updateRecipeFromRequest(requestWithEmptyCollections, originalRecipe);

    // Assert
    assertThat(originalRecipe.getTitle()).isEqualTo("Updated Title");
    assertThat(originalRecipe.getRecipeIngredients()).isNotNull().isEmpty();
    assertThat(originalRecipe.getRecipeSteps()).isNotNull().isEmpty();
  }

  @Test
  @DisplayName("Should preserve Recipe ID fields during update")
  void shouldPreserveRecipeIdFieldsDuringUpdate() {
    // Arrange
    Recipe originalRecipe = Recipe.builder()
        .recipeId(999L)
        .userId(UUID.randomUUID())
        .title("Original Title")
        .createdAt(LocalDateTime.now().minusDays(1))
        .updatedAt(LocalDateTime.now().minusHours(1))
        .build();

    Long originalRecipeId = originalRecipe.getRecipeId();
    UUID originalUserId = originalRecipe.getUserId();
    LocalDateTime originalCreatedAt = originalRecipe.getCreatedAt();
    LocalDateTime originalUpdatedAt = originalRecipe.getUpdatedAt();

    // Act
    recipeMapper.updateRecipeFromRequest(updateRequest, originalRecipe);

    // Assert - ID fields should remain unchanged
    assertThat(originalRecipe.getRecipeId()).isEqualTo(originalRecipeId);
    assertThat(originalRecipe.getUserId()).isEqualTo(originalUserId);
    assertThat(originalRecipe.getCreatedAt()).isEqualTo(originalCreatedAt);
    assertThat(originalRecipe.getUpdatedAt()).isEqualTo(originalUpdatedAt);

    // But other fields should be updated
    assertThat(originalRecipe.getTitle()).isEqualTo("Updated Title");
    assertThat(originalRecipe.getDescription()).isEqualTo("Updated Description");
  }
}
