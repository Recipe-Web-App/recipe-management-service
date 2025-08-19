package com.recipe_manager.unit_tests.model.dto.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.recipe_manager.model.dto.recipe.StepCommentDto;
import com.recipe_manager.model.dto.response.StepCommentResponse;

@Tag("unit")
class StepCommentResponseTest {

  private StepCommentResponse response;
  private List<StepCommentDto> commentDtos;

  @BeforeEach
  void setUp() {
    LocalDateTime now = LocalDateTime.now();

    StepCommentDto comment1 = StepCommentDto.builder()
        .commentId(1L)
        .recipeId(1L)
        .stepId(1L)
        .userId(UUID.randomUUID())
        .commentText("First comment")
        .isPublic(true)
        .createdAt(now)
        .updatedAt(now)
        .build();

    StepCommentDto comment2 = StepCommentDto.builder()
        .commentId(2L)
        .recipeId(1L)
        .stepId(1L)
        .userId(UUID.randomUUID())
        .commentText("Second comment")
        .isPublic(false)
        .createdAt(now)
        .updatedAt(now)
        .build();

    commentDtos = Arrays.asList(comment1, comment2);

    response = StepCommentResponse.builder()
        .recipeId(1L)
        .stepId(1L)
        .comments(commentDtos)
        .build();
  }

  @Test
  void testBuilder() {
    StepCommentResponse newResponse = StepCommentResponse.builder()
        .recipeId(2L)
        .stepId(2L)
        .comments(commentDtos)
        .build();

    assertThat(newResponse.getRecipeId()).isEqualTo(2L);
    assertThat(newResponse.getStepId()).isEqualTo(2L);
    assertThat(newResponse.getComments()).isEqualTo(commentDtos);
  }

  @Test
  void testGettersAndSetters() {
    List<StepCommentDto> newComments = Collections.emptyList();

    response.setRecipeId(5L);
    response.setStepId(5L);
    response.setComments(newComments);

    assertThat(response.getRecipeId()).isEqualTo(5L);
    assertThat(response.getStepId()).isEqualTo(5L);
    assertThat(response.getComments()).isEqualTo(newComments);
  }

  @Test
  void testEqualsAndHashCode() {
    StepCommentResponse response1 = StepCommentResponse.builder()
        .recipeId(1L)
        .stepId(1L)
        .comments(commentDtos)
        .build();

    StepCommentResponse response2 = StepCommentResponse.builder()
        .recipeId(1L)
        .stepId(1L)
        .comments(commentDtos)
        .build();

    assertThat(response1).isEqualTo(response2);
    assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
  }

  @Test
  void testToString() {
    String toStringResult = response.toString();

    assertThat(toStringResult).contains("StepCommentResponse");
    assertThat(toStringResult).contains("recipeId=" + response.getRecipeId());
    assertThat(toStringResult).contains("stepId=" + response.getStepId());
    assertThat(toStringResult).contains("comments=" + response.getComments());
  }

  @Test
  void testNoArgsConstructor() {
    StepCommentResponse newResponse = new StepCommentResponse();

    assertThat(newResponse.getRecipeId()).isNull();
    assertThat(newResponse.getStepId()).isNull();
    assertThat(newResponse.getComments()).isNull();
  }

  @Test
  void testAllArgsConstructor() {
    StepCommentResponse newResponse = new StepCommentResponse(1L, 1L, commentDtos);

    assertThat(newResponse.getRecipeId()).isEqualTo(1L);
    assertThat(newResponse.getStepId()).isEqualTo(1L);
    assertThat(newResponse.getComments()).isEqualTo(commentDtos);
  }

  @Test
  void testWithEmptyComments() {
    List<StepCommentDto> emptyComments = Collections.emptyList();

    StepCommentResponse responseWithEmptyComments = StepCommentResponse.builder()
        .recipeId(1L)
        .stepId(1L)
        .comments(emptyComments)
        .build();

    assertThat(responseWithEmptyComments.getComments()).isEmpty();
  }

  @Test
  void testWithNullValues() {
    StepCommentResponse responseWithNulls = StepCommentResponse.builder()
        .recipeId(null)
        .stepId(null)
        .comments(null)
        .build();

    assertThat(responseWithNulls.getRecipeId()).isNull();
    assertThat(responseWithNulls.getStepId()).isNull();
    assertThat(responseWithNulls.getComments()).isNull();
  }

  @Test
  void testNotEqualsWithDifferentRecipeId() {
    StepCommentResponse response1 = StepCommentResponse.builder()
        .recipeId(1L)
        .stepId(1L)
        .comments(commentDtos)
        .build();

    StepCommentResponse response2 = StepCommentResponse.builder()
        .recipeId(2L)
        .stepId(1L)
        .comments(commentDtos)
        .build();

    assertThat(response1).isNotEqualTo(response2);
  }

  @Test
  void testNotEqualsWithDifferentStepId() {
    StepCommentResponse response1 = StepCommentResponse.builder()
        .recipeId(1L)
        .stepId(1L)
        .comments(commentDtos)
        .build();

    StepCommentResponse response2 = StepCommentResponse.builder()
        .recipeId(1L)
        .stepId(2L)
        .comments(commentDtos)
        .build();

    assertThat(response1).isNotEqualTo(response2);
  }

  @Test
  void testNotEqualsWithDifferentComments() {
    StepCommentResponse response1 = StepCommentResponse.builder()
        .recipeId(1L)
        .stepId(1L)
        .comments(commentDtos)
        .build();

    StepCommentResponse response2 = StepCommentResponse.builder()
        .recipeId(1L)
        .stepId(1L)
        .comments(Collections.emptyList())
        .build();

    assertThat(response1).isNotEqualTo(response2);
  }

  @Test
  void testCommentsListMutability() {
    assertThat(response.getComments()).hasSize(2);
    assertThat(response.getComments().get(0).getCommentText()).isEqualTo("First comment");
    assertThat(response.getComments().get(1).getCommentText()).isEqualTo("Second comment");
  }
}
