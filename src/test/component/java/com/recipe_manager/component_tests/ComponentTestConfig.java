package com.recipe_manager.component_tests;

/**
 * Base configuration for component tests.
 *
 * <p>
 * This class provides common constants/utilities for component tests.
 * </p>
 */
public abstract class ComponentTestConfig {

  /**
   * Common test constants and utilities for component tests.
   */
  protected static final class TestConstants {
    public static final String API_BASE_PATH = "/api/v1";
    public static final String HEALTH_ENDPOINT = "/actuator/health";
    public static final String TEST_USER_ID = "component-test-user";
    public static final String TEST_RECIPE_ID = "component-test-recipe";

    private TestConstants() {
      // Utility class
    }
  }
}
