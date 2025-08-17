package com.recipe_manager.unit_tests.model.dto.ingredient;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import com.recipe_manager.model.dto.ingredient.IngredientCommentDto;

/** Unit tests for IngredientCommentDto. */
@Tag("unit")
class IngredientCommentDtoTest {

  private UUID testUserId;
  private Long testRecipeId;

  @BeforeEach
  void setUp() {
    testUserId = UUID.randomUUID();
    testRecipeId = 123L;
  }

  @Test
  void shouldCreateIngredientCommentDtoWithAllFields() {
    final LocalDateTime now = LocalDateTime.now();
    final IngredientCommentDto dto = IngredientCommentDto.builder()
        .commentId(1L)
        .recipeId(testRecipeId)
        .userId(testUserId)
        .commentText("Test comment")
        .isPublic(true)
        .createdAt(now)
        .updatedAt(now)
        .build();

    assertThat(dto.getCommentId()).isEqualTo(1L);
    assertThat(dto.getRecipeId()).isEqualTo(testRecipeId);
    assertThat(dto.getUserId()).isEqualTo(testUserId);
    assertThat(dto.getCommentText()).isEqualTo("Test comment");
    assertThat(dto.getIsPublic()).isTrue();
    assertThat(dto.getCreatedAt()).isEqualTo(now);
    assertThat(dto.getUpdatedAt()).isEqualTo(now);
  }

  @Test
  void shouldCreateIngredientCommentDtoWithNoArgsConstructor() {
    final IngredientCommentDto dto = new IngredientCommentDto();

    assertThat(dto).isNotNull();
    assertThat(dto.getCommentId()).isNull();
    assertThat(dto.getRecipeId()).isNull();
    assertThat(dto.getUserId()).isNull();
    assertThat(dto.getCommentText()).isNull();
    assertThat(dto.getIsPublic()).isNull();
    assertThat(dto.getCreatedAt()).isNull();
    assertThat(dto.getUpdatedAt()).isNull();
  }

  @Test
  void shouldCreateIngredientCommentDtoWithAllArgsConstructor() {
    final LocalDateTime now = LocalDateTime.now();
    final IngredientCommentDto dto = new IngredientCommentDto(
        1L, testRecipeId, testUserId, "Test comment", true, now, now);

    assertThat(dto.getCommentId()).isEqualTo(1L);
    assertThat(dto.getRecipeId()).isEqualTo(testRecipeId);
    assertThat(dto.getUserId()).isEqualTo(testUserId);
    assertThat(dto.getCommentText()).isEqualTo("Test comment");
    assertThat(dto.getIsPublic()).isTrue();
    assertThat(dto.getCreatedAt()).isEqualTo(now);
    assertThat(dto.getUpdatedAt()).isEqualTo(now);
  }

  @Test
  void shouldSetAndGetAllFields() {
    final IngredientCommentDto dto = new IngredientCommentDto();
    final LocalDateTime now = LocalDateTime.now();

    dto.setCommentId(1L);
    dto.setRecipeId(testRecipeId);
    dto.setUserId(testUserId);
    dto.setCommentText("Test comment");
    dto.setIsPublic(false);
    dto.setCreatedAt(now);
    dto.setUpdatedAt(now);

    assertThat(dto.getCommentId()).isEqualTo(1L);
    assertThat(dto.getRecipeId()).isEqualTo(testRecipeId);
    assertThat(dto.getUserId()).isEqualTo(testUserId);
    assertThat(dto.getCommentText()).isEqualTo("Test comment");
    assertThat(dto.getIsPublic()).isFalse();
    assertThat(dto.getCreatedAt()).isEqualTo(now);
    assertThat(dto.getUpdatedAt()).isEqualTo(now);
  }

  @Test
  void shouldImplementEqualsAndHashCode() {
    final LocalDateTime now = LocalDateTime.now();
    final IngredientCommentDto dto1 = IngredientCommentDto.builder()
        .commentId(1L)
        .recipeId(testRecipeId)
        .userId(testUserId)
        .commentText("Test comment")
        .isPublic(true)
        .createdAt(now)
        .updatedAt(now)
        .build();

    final IngredientCommentDto dto2 = IngredientCommentDto.builder()
        .commentId(1L)
        .recipeId(testRecipeId)
        .userId(testUserId)
        .commentText("Test comment")
        .isPublic(true)
        .createdAt(now)
        .updatedAt(now)
        .build();

    assertThat(dto1).isEqualTo(dto2);
    assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
  }

  @Test
  void shouldImplementToString() {
    final IngredientCommentDto dto = IngredientCommentDto.builder()
        .commentId(1L)
        .recipeId(testRecipeId)
        .userId(testUserId)
        .commentText("Test comment")
        .isPublic(true)
        .build();

    final String toString = dto.toString();

    assertThat(toString).contains("commentId=1");
    assertThat(toString).contains("recipeId=" + testRecipeId);
    assertThat(toString).contains("userId=" + testUserId);
    assertThat(toString).contains("commentText=Test comment");
    assertThat(toString).contains("isPublic=true");
  }
}
