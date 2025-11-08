package com.recipe_manager.client.notificationservice;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for NotificationServiceClient interface. These tests validate the client interface
 * definition and contracts. Integration testing with actual Feign client behavior is performed in
 * component tests.
 */
@Tag("unit")
class NotificationServiceClientTest {

  @Test
  @Tag("standard-processing")
  @DisplayName("Should have correct interface annotations")
  void shouldHaveCorrectInterfaceAnnotations() {
    assertThat(NotificationServiceClient.class.isInterface()).isTrue();
    assertThat(NotificationServiceClient.class.getAnnotations()).isNotEmpty();

    // Verify @FeignClient annotation exists
    boolean hasFeignClientAnnotation = java.util.Arrays.stream(NotificationServiceClient.class.getAnnotations())
        .anyMatch(annotation -> annotation.annotationType().getSimpleName().equals("FeignClient"));
    assertThat(hasFeignClientAnnotation).isTrue();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should have three notification methods")
  void shouldHaveThreeNotificationMethods() {
    java.lang.reflect.Method[] methods = NotificationServiceClient.class.getDeclaredMethods();
    assertThat(methods).hasSize(3);

    List<String> methodNames = java.util.Arrays.stream(methods)
        .map(java.lang.reflect.Method::getName)
        .toList();

    assertThat(methodNames).containsExactlyInAnyOrder(
        "notifyRecipePublished",
        "notifyRecipeLiked",
        "notifyRecipeCommented"
    );
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("notifyRecipePublished should have correct annotations")
  void notifyRecipePublishedShouldHaveCorrectAnnotations() throws NoSuchMethodException {
    java.lang.reflect.Method method = NotificationServiceClient.class.getDeclaredMethod(
        "notifyRecipePublished",
        com.recipe_manager.model.dto.external.notificationservice.request.RecipePublishedRequestDto.class
    );

    assertThat(method).isNotNull();

    // Should have @PostMapping annotation
    boolean hasPostMapping = java.util.Arrays.stream(method.getAnnotations())
        .anyMatch(annotation -> annotation.annotationType().getSimpleName().equals("PostMapping"));
    assertThat(hasPostMapping).isTrue();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("notifyRecipeLiked should have correct annotations")
  void notifyRecipeLikedShouldHaveCorrectAnnotations() throws NoSuchMethodException {
    java.lang.reflect.Method method = NotificationServiceClient.class.getDeclaredMethod(
        "notifyRecipeLiked",
        com.recipe_manager.model.dto.external.notificationservice.request.RecipeLikedRequestDto.class
    );

    assertThat(method).isNotNull();

    // Should have @PostMapping annotation
    boolean hasPostMapping = java.util.Arrays.stream(method.getAnnotations())
        .anyMatch(annotation -> annotation.annotationType().getSimpleName().equals("PostMapping"));
    assertThat(hasPostMapping).isTrue();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("notifyRecipeCommented should have correct annotations")
  void notifyRecipeCommentedShouldHaveCorrectAnnotations() throws NoSuchMethodException {
    java.lang.reflect.Method method = NotificationServiceClient.class.getDeclaredMethod(
        "notifyRecipeCommented",
        com.recipe_manager.model.dto.external.notificationservice.request.RecipeCommentedRequestDto.class
    );

    assertThat(method).isNotNull();

    // Should have @PostMapping annotation
    boolean hasPostMapping = java.util.Arrays.stream(method.getAnnotations())
        .anyMatch(annotation -> annotation.annotationType().getSimpleName().equals("PostMapping"));
    assertThat(hasPostMapping).isTrue();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("All methods should return BatchNotificationResponseDto")
  void allMethodsShouldReturnBatchNotificationResponseDto() {
    java.lang.reflect.Method[] methods = NotificationServiceClient.class.getDeclaredMethods();

    for (java.lang.reflect.Method method : methods) {
      assertThat(method.getReturnType().getSimpleName())
          .isEqualTo("BatchNotificationResponseDto");
    }
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("All methods should have @RequestBody parameter")
  void allMethodsShouldHaveRequestBodyParameter() throws NoSuchMethodException {
    java.lang.reflect.Method publishedMethod = NotificationServiceClient.class.getDeclaredMethod(
        "notifyRecipePublished",
        com.recipe_manager.model.dto.external.notificationservice.request.RecipePublishedRequestDto.class
    );

    java.lang.reflect.Method likedMethod = NotificationServiceClient.class.getDeclaredMethod(
        "notifyRecipeLiked",
        com.recipe_manager.model.dto.external.notificationservice.request.RecipeLikedRequestDto.class
    );

    java.lang.reflect.Method commentedMethod = NotificationServiceClient.class.getDeclaredMethod(
        "notifyRecipeCommented",
        com.recipe_manager.model.dto.external.notificationservice.request.RecipeCommentedRequestDto.class
    );

    // Verify each method has exactly one parameter
    assertThat(publishedMethod.getParameterCount()).isEqualTo(1);
    assertThat(likedMethod.getParameterCount()).isEqualTo(1);
    assertThat(commentedMethod.getParameterCount()).isEqualTo(1);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Interface should be public")
  void interfaceShouldBePublic() {
    assertThat(java.lang.reflect.Modifier.isPublic(NotificationServiceClient.class.getModifiers()))
        .isTrue();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("FeignClient annotation should have correct name")
  void feignClientAnnotationShouldHaveCorrectName() {
    org.springframework.cloud.openfeign.FeignClient feignClientAnnotation =
        NotificationServiceClient.class.getAnnotation(org.springframework.cloud.openfeign.FeignClient.class);

    assertThat(feignClientAnnotation).isNotNull();
    assertThat(feignClientAnnotation.name()).isEqualTo("notification-service");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("FeignClient annotation should reference correct configuration")
  void feignClientAnnotationShouldReferenceCorrectConfiguration() {
    org.springframework.cloud.openfeign.FeignClient feignClientAnnotation =
        NotificationServiceClient.class.getAnnotation(org.springframework.cloud.openfeign.FeignClient.class);

    assertThat(feignClientAnnotation).isNotNull();
    assertThat(feignClientAnnotation.configuration()).contains(
        com.recipe_manager.client.common.FeignClientConfig.class
    );
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("FeignClient annotation should reference correct fallback")
  void feignClientAnnotationShouldReferenceCorrectFallback() {
    org.springframework.cloud.openfeign.FeignClient feignClientAnnotation =
        NotificationServiceClient.class.getAnnotation(org.springframework.cloud.openfeign.FeignClient.class);

    assertThat(feignClientAnnotation).isNotNull();
    assertThat(feignClientAnnotation.fallback())
        .isEqualTo(com.recipe_manager.service.external.notificationservice.NotificationServiceFallback.class);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("notifyRecipePublished should accept RecipePublishedRequestDto")
  void notifyRecipePublishedShouldAcceptCorrectParameter() throws NoSuchMethodException {
    java.lang.reflect.Method method = NotificationServiceClient.class.getDeclaredMethod(
        "notifyRecipePublished",
        com.recipe_manager.model.dto.external.notificationservice.request.RecipePublishedRequestDto.class
    );

    assertThat(method.getParameterTypes()[0].getSimpleName())
        .isEqualTo("RecipePublishedRequestDto");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("notifyRecipeLiked should accept RecipeLikedRequestDto")
  void notifyRecipeLikedShouldAcceptCorrectParameter() throws NoSuchMethodException {
    java.lang.reflect.Method method = NotificationServiceClient.class.getDeclaredMethod(
        "notifyRecipeLiked",
        com.recipe_manager.model.dto.external.notificationservice.request.RecipeLikedRequestDto.class
    );

    assertThat(method.getParameterTypes()[0].getSimpleName())
        .isEqualTo("RecipeLikedRequestDto");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("notifyRecipeCommented should accept RecipeCommentedRequestDto")
  void notifyRecipeCommentedShouldAcceptCorrectParameter() throws NoSuchMethodException {
    java.lang.reflect.Method method = NotificationServiceClient.class.getDeclaredMethod(
        "notifyRecipeCommented",
        com.recipe_manager.model.dto.external.notificationservice.request.RecipeCommentedRequestDto.class
    );

    assertThat(method.getParameterTypes()[0].getSimpleName())
        .isEqualTo("RecipeCommentedRequestDto");
  }
}
