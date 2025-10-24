package com.recipe_manager.model.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.UUID;

import com.recipe_manager.model.dto.collection.CollectionRecipeDto;
import com.recipe_manager.model.entity.collection.RecipeCollectionItem;
import com.recipe_manager.model.entity.collection.RecipeCollectionItemId;
import com.recipe_manager.model.entity.recipe.Recipe;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/** Unit tests for CollectionRecipeMapper. */
@Tag("unit")
@SpringBootTest(classes = {CollectionRecipeMapperImpl.class})
@TestPropertySource(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:testdb",
      "spring.jpa.hibernate.ddl-auto=none",
      "spring.flyway.enabled=false"
    })
class CollectionRecipeMapperTest {

  @Autowired private CollectionRecipeMapper collectionRecipeMapper;

  @Test
  @DisplayName("Should map RecipeCollectionItem and Recipe to CollectionRecipeDto")
  void shouldMapToCollectionRecipeDto() {
    UUID userId = UUID.randomUUID();
    UUID addedBy = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    Recipe recipe =
        Recipe.builder()
            .recipeId(2L)
            .userId(userId)
            .title("Chocolate Chip Cookies")
            .description("Delicious homemade cookies")
            .build();

    RecipeCollectionItemId id =
        RecipeCollectionItemId.builder().collectionId(1L).recipeId(2L).build();

    RecipeCollectionItem item =
        RecipeCollectionItem.builder()
            .id(id)
            .displayOrder(10)
            .addedBy(addedBy)
            .addedAt(now)
            .build();

    CollectionRecipeDto result = collectionRecipeMapper.toDto(item, recipe);

    assertThat(result).isNotNull();
    assertThat(result.getRecipeId()).isEqualTo(2L);
    assertThat(result.getRecipeTitle()).isEqualTo("Chocolate Chip Cookies");
    assertThat(result.getRecipeDescription()).isEqualTo("Delicious homemade cookies");
    assertThat(result.getDisplayOrder()).isEqualTo(10);
    assertThat(result.getAddedBy()).isEqualTo(addedBy);
    assertThat(result.getAddedAt()).isEqualTo(now);
  }

  @Test
  @DisplayName("Should handle null description in Recipe")
  void shouldHandleNullDescription() {
    UUID userId = UUID.randomUUID();
    UUID addedBy = UUID.randomUUID();

    Recipe recipe = Recipe.builder().recipeId(3L).userId(userId).title("Simple Recipe").build();

    RecipeCollectionItemId id =
        RecipeCollectionItemId.builder().collectionId(1L).recipeId(3L).build();

    RecipeCollectionItem item =
        RecipeCollectionItem.builder()
            .id(id)
            .displayOrder(15)
            .addedBy(addedBy)
            .addedAt(LocalDateTime.now())
            .build();

    CollectionRecipeDto result = collectionRecipeMapper.toDto(item, recipe);

    assertThat(result).isNotNull();
    assertThat(result.getRecipeTitle()).isEqualTo("Simple Recipe");
    assertThat(result.getRecipeDescription()).isNull();
    assertThat(result.getDisplayOrder()).isEqualTo(15);
  }

  @Test
  @DisplayName("Should handle null RecipeCollectionItem")
  void shouldHandleNullItem() {
    UUID userId = UUID.randomUUID();
    Recipe recipe = Recipe.builder().recipeId(1L).userId(userId).title("Test").build();

    CollectionRecipeDto result = collectionRecipeMapper.toDto(null, recipe);

    assertThat(result).isNull();
  }

  @Test
  @DisplayName("Should handle null Recipe")
  void shouldHandleNullRecipe() {
    RecipeCollectionItemId id =
        RecipeCollectionItemId.builder().collectionId(1L).recipeId(2L).build();

    RecipeCollectionItem item =
        RecipeCollectionItem.builder()
            .id(id)
            .displayOrder(10)
            .addedBy(UUID.randomUUID())
            .addedAt(LocalDateTime.now())
            .build();

    CollectionRecipeDto result = collectionRecipeMapper.toDto(item, null);

    assertThat(result).isNull();
  }

  @Test
  @DisplayName("Should handle both null parameters")
  void shouldHandleBothNull() {
    CollectionRecipeDto result = collectionRecipeMapper.toDto(null, null);
    assertThat(result).isNull();
  }
}
