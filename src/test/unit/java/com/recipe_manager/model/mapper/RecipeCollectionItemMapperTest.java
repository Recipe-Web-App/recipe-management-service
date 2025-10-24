package com.recipe_manager.model.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.UUID;

import com.recipe_manager.model.dto.collection.RecipeCollectionItemDto;
import com.recipe_manager.model.entity.collection.RecipeCollectionItem;
import com.recipe_manager.model.entity.collection.RecipeCollectionItemId;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/** Unit tests for RecipeCollectionItemMapper. */
@Tag("unit")
@SpringBootTest(classes = {RecipeCollectionItemMapperImpl.class})
@TestPropertySource(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:testdb",
      "spring.jpa.hibernate.ddl-auto=none",
      "spring.flyway.enabled=false"
    })
class RecipeCollectionItemMapperTest {

  @Autowired private RecipeCollectionItemMapper recipeCollectionItemMapper;

  @Test
  @DisplayName("Should map RecipeCollectionItem entity to RecipeCollectionItemDto")
  void shouldMapRecipeCollectionItemEntityToDto() {
    RecipeCollectionItemId id =
        RecipeCollectionItemId.builder().collectionId(1L).recipeId(2L).build();
    UUID addedBy = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    RecipeCollectionItem item =
        RecipeCollectionItem.builder()
            .id(id)
            .displayOrder(10)
            .addedBy(addedBy)
            .addedAt(now)
            .build();

    RecipeCollectionItemDto result = recipeCollectionItemMapper.toDto(item);

    assertThat(result).isNotNull();
    assertThat(result.getCollectionId()).isEqualTo(1L);
    assertThat(result.getRecipeId()).isEqualTo(2L);
    assertThat(result.getDisplayOrder()).isEqualTo(10);
    assertThat(result.getAddedBy()).isEqualTo(addedBy);
    assertThat(result.getAddedAt()).isEqualTo(now);
  }

  @Test
  @DisplayName("Should handle null RecipeCollectionItem entity")
  void shouldHandleNullRecipeCollectionItemEntity() {
    RecipeCollectionItemDto result = recipeCollectionItemMapper.toDto(null);
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("Should map composite ID correctly")
  void shouldMapCompositeIdCorrectly() {
    RecipeCollectionItemId id =
        RecipeCollectionItemId.builder().collectionId(5L).recipeId(10L).build();

    RecipeCollectionItem item =
        RecipeCollectionItem.builder()
            .id(id)
            .displayOrder(20)
            .addedBy(UUID.randomUUID())
            .build();

    RecipeCollectionItemDto result = recipeCollectionItemMapper.toDto(item);

    assertThat(result.getCollectionId()).isEqualTo(5L);
    assertThat(result.getRecipeId()).isEqualTo(10L);
  }
}
