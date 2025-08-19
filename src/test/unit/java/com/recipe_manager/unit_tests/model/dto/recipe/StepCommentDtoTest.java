package com.recipe_manager.unit_tests.model.dto.recipe;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.recipe_manager.model.dto.recipe.StepCommentDto;

@Tag("unit")
class StepCommentDtoTest {

  private StepCommentDto stepCommentDto;

  @BeforeEach
  void setUp() {
    stepCommentDto = StepCommentDto.builder()
        .commentId(1L)
        .recipeId(1L)
        .stepId(1L)
        .userId(UUID.randomUUID())
        .commentText("Test comment")
        .isPublic(true)
        .build();
  }

  @Test
  void testBuilder() {
    UUID userId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    StepCommentDto dto = StepCommentDto.builder()
        .commentId(2L)
        .recipeId(2L)
        .stepId(2L)
        .userId(userId)
        .commentText("Another test comment")
        .isPublic(false)
        .createdAt(now)
        .updatedAt(now)
        .build();

    assertThat(dto.getCommentId()).isEqualTo(2L);
    assertThat(dto.getRecipeId()).isEqualTo(2L);
    assertThat(dto.getStepId()).isEqualTo(2L);
    assertThat(dto.getUserId()).isEqualTo(userId);
    assertThat(dto.getCommentText()).isEqualTo("Another test comment");
    assertThat(dto.getIsPublic()).isFalse();
    assertThat(dto.getCreatedAt()).isEqualTo(now);
    assertThat(dto.getUpdatedAt()).isEqualTo(now);
  }

  @Test
  void testGettersAndSetters() {
    UUID newUserId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    stepCommentDto.setCommentId(5L);
    stepCommentDto.setRecipeId(5L);
    stepCommentDto.setStepId(5L);
    stepCommentDto.setUserId(newUserId);
    stepCommentDto.setCommentText("Updated comment");
    stepCommentDto.setIsPublic(false);
    stepCommentDto.setCreatedAt(now);
    stepCommentDto.setUpdatedAt(now);

    assertThat(stepCommentDto.getCommentId()).isEqualTo(5L);
    assertThat(stepCommentDto.getRecipeId()).isEqualTo(5L);
    assertThat(stepCommentDto.getStepId()).isEqualTo(5L);
    assertThat(stepCommentDto.getUserId()).isEqualTo(newUserId);
    assertThat(stepCommentDto.getCommentText()).isEqualTo("Updated comment");
    assertThat(stepCommentDto.getIsPublic()).isFalse();
    assertThat(stepCommentDto.getCreatedAt()).isEqualTo(now);
    assertThat(stepCommentDto.getUpdatedAt()).isEqualTo(now);
  }

  @Test
  void testEqualsAndHashCode() {
    UUID userId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    StepCommentDto dto1 = StepCommentDto.builder()
        .commentId(1L)
        .recipeId(1L)
        .stepId(1L)
        .userId(userId)
        .commentText("Test comment")
        .isPublic(true)
        .createdAt(now)
        .updatedAt(now)
        .build();

    StepCommentDto dto2 = StepCommentDto.builder()
        .commentId(1L)
        .recipeId(1L)
        .stepId(1L)
        .userId(userId)
        .commentText("Test comment")
        .isPublic(true)
        .createdAt(now)
        .updatedAt(now)
        .build();

    assertThat(dto1).isEqualTo(dto2);
    assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
  }

  @Test
  void testToString() {
    String toStringResult = stepCommentDto.toString();

    assertThat(toStringResult).contains("StepCommentDto");
    assertThat(toStringResult).contains("commentId=" + stepCommentDto.getCommentId());
    assertThat(toStringResult).contains("recipeId=" + stepCommentDto.getRecipeId());
    assertThat(toStringResult).contains("stepId=" + stepCommentDto.getStepId());
    assertThat(toStringResult).contains("commentText=" + stepCommentDto.getCommentText());
  }

  @Test
  void testNoArgsConstructor() {
    StepCommentDto dto = new StepCommentDto();

    assertThat(dto.getCommentId()).isNull();
    assertThat(dto.getRecipeId()).isNull();
    assertThat(dto.getStepId()).isNull();
    assertThat(dto.getUserId()).isNull();
    assertThat(dto.getCommentText()).isNull();
    assertThat(dto.getIsPublic()).isNull();
    assertThat(dto.getCreatedAt()).isNull();
    assertThat(dto.getUpdatedAt()).isNull();
  }

  @Test
  void testAllArgsConstructor() {
    UUID userId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    StepCommentDto dto = new StepCommentDto(
        1L, 1L, 1L, userId, "Test comment", true, now, now
    );

    assertThat(dto.getCommentId()).isEqualTo(1L);
    assertThat(dto.getRecipeId()).isEqualTo(1L);
    assertThat(dto.getStepId()).isEqualTo(1L);
    assertThat(dto.getUserId()).isEqualTo(userId);
    assertThat(dto.getCommentText()).isEqualTo("Test comment");
    assertThat(dto.getIsPublic()).isTrue();
    assertThat(dto.getCreatedAt()).isEqualTo(now);
    assertThat(dto.getUpdatedAt()).isEqualTo(now);
  }

  @Test
  void testWithNullValues() {
    StepCommentDto dto = StepCommentDto.builder()
        .commentId(null)
        .recipeId(null)
        .stepId(null)
        .userId(null)
        .commentText(null)
        .isPublic(null)
        .createdAt(null)
        .updatedAt(null)
        .build();

    assertThat(dto.getCommentId()).isNull();
    assertThat(dto.getRecipeId()).isNull();
    assertThat(dto.getStepId()).isNull();
    assertThat(dto.getUserId()).isNull();
    assertThat(dto.getCommentText()).isNull();
    assertThat(dto.getIsPublic()).isNull();
    assertThat(dto.getCreatedAt()).isNull();
    assertThat(dto.getUpdatedAt()).isNull();
  }
}
