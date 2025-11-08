package com.recipe_manager.client.usermanagement;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for UserManagementClient interface. These tests validate the client interface
 * definition and contracts. Integration testing with actual Feign client behavior is performed in
 * component tests.
 */
@Tag("unit")
class UserManagementClientTest {

  @Test
  @Tag("standard-processing")
  @DisplayName("Should have correct interface annotations")
  void shouldHaveCorrectInterfaceAnnotations() {
    assertThat(UserManagementClient.class.isInterface()).isTrue();
    assertThat(UserManagementClient.class.getAnnotations()).isNotEmpty();

    // Verify @FeignClient annotation exists
    boolean hasFeignClientAnnotation = java.util.Arrays.stream(UserManagementClient.class.getAnnotations())
        .anyMatch(annotation -> annotation.annotationType().getSimpleName().equals("FeignClient"));
    assertThat(hasFeignClientAnnotation).isTrue();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should have one getFollowers method")
  void shouldHaveOneGetFollowersMethod() {
    java.lang.reflect.Method[] methods = UserManagementClient.class.getDeclaredMethods();
    assertThat(methods).hasSize(1);
    assertThat(methods[0].getName()).isEqualTo("getFollowers");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("getFollowers should have correct annotations")
  void getFollowersShouldHaveCorrectAnnotations() throws NoSuchMethodException {
    java.lang.reflect.Method method = UserManagementClient.class.getDeclaredMethod(
        "getFollowers",
        UUID.class,
        Integer.class,
        Integer.class,
        Boolean.class
    );

    assertThat(method).isNotNull();

    // Should have @GetMapping annotation
    boolean hasGetMapping = java.util.Arrays.stream(method.getAnnotations())
        .anyMatch(annotation -> annotation.annotationType().getSimpleName().equals("GetMapping"));
    assertThat(hasGetMapping).isTrue();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("getFollowers should return GetFollowersResponseDto")
  void getFollowersShouldReturnCorrectType() throws NoSuchMethodException {
    java.lang.reflect.Method method = UserManagementClient.class.getDeclaredMethod(
        "getFollowers",
        UUID.class,
        Integer.class,
        Integer.class,
        Boolean.class
    );

    assertThat(method.getReturnType().getSimpleName())
        .isEqualTo("GetFollowersResponseDto");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("getFollowers should have four parameters")
  void getFollowersShouldHaveFourParameters() throws NoSuchMethodException {
    java.lang.reflect.Method method = UserManagementClient.class.getDeclaredMethod(
        "getFollowers",
        UUID.class,
        Integer.class,
        Integer.class,
        Boolean.class
    );

    assertThat(method.getParameterCount()).isEqualTo(4);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("getFollowers should accept correct parameter types")
  void getFollowersShouldAcceptCorrectParameterTypes() throws NoSuchMethodException {
    java.lang.reflect.Method method = UserManagementClient.class.getDeclaredMethod(
        "getFollowers",
        UUID.class,
        Integer.class,
        Integer.class,
        Boolean.class
    );

    Class<?>[] paramTypes = method.getParameterTypes();
    assertThat(paramTypes[0]).isEqualTo(UUID.class);
    assertThat(paramTypes[1]).isEqualTo(Integer.class);
    assertThat(paramTypes[2]).isEqualTo(Integer.class);
    assertThat(paramTypes[3]).isEqualTo(Boolean.class);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Interface should be public")
  void interfaceShouldBePublic() {
    assertThat(java.lang.reflect.Modifier.isPublic(UserManagementClient.class.getModifiers()))
        .isTrue();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("FeignClient annotation should have correct name")
  void feignClientAnnotationShouldHaveCorrectName() {
    org.springframework.cloud.openfeign.FeignClient feignClientAnnotation =
        UserManagementClient.class.getAnnotation(org.springframework.cloud.openfeign.FeignClient.class);

    assertThat(feignClientAnnotation).isNotNull();
    assertThat(feignClientAnnotation.name()).isEqualTo("user-management-service");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("FeignClient annotation should reference correct configuration")
  void feignClientAnnotationShouldReferenceCorrectConfiguration() {
    org.springframework.cloud.openfeign.FeignClient feignClientAnnotation =
        UserManagementClient.class.getAnnotation(org.springframework.cloud.openfeign.FeignClient.class);

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
        UserManagementClient.class.getAnnotation(org.springframework.cloud.openfeign.FeignClient.class);

    assertThat(feignClientAnnotation).isNotNull();
    assertThat(feignClientAnnotation.fallback())
        .isEqualTo(UserManagementFallback.class);
  }
}
