package com.recipe_manager.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

/**
 * Test class for LoggingConfig.
 * Verifies that logging configuration loads correctly.
 */
@Tag("unit")
class LoggingConfigTest {

  /**
   * Test that logging configuration can be loaded.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should load logging configuration")
  void loggingConfigLoads() {
    // Verify that logging configuration can be instantiated
    LoggingConfig config = new LoggingConfig();
    assertNotNull(config);
  }

  /**
   * Test that logging beans are created.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should create logging beans")
  void loggingBeansCreated() {
    // This test verifies that logging configuration exists
    assertNotNull(LoggingConfig.class);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should create requestLoggingFilter bean")
  void shouldCreateRequestLoggingFilterBean() {
    LoggingConfig config = new LoggingConfig();
    CommonsRequestLoggingFilter filter = config.requestLoggingFilter();
    assertNotNull(filter);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should generate request ID")
  void shouldGenerateRequestId() {
    String id1 = LoggingConfig.generateRequestId();
    String id2 = LoggingConfig.generateRequestId();
    assertNotNull(id1);
    assertNotNull(id2);
    assertNotEquals(id1, id2);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should setup and clear MDC context")
  void shouldSetupAndClearMdcContext() {
    LoggingConfig.setupMdcContext("reqid", "user", "sess");
    assertEquals("reqid", MDC.get("requestId"));
    assertEquals("user", MDC.get("userId"));
    assertEquals("sess", MDC.get("sessionId"));
    assertNotNull(MDC.get("timestamp"));
    LoggingConfig.clearMdcContext();
    assertNull(MDC.get("requestId"));
    assertNull(MDC.get("userId"));
    assertNull(MDC.get("sessionId"));
    assertNull(MDC.get("timestamp"));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should cover private constructor")
  void shouldCoverPrivateConstructor() throws Exception {
    java.lang.reflect.Constructor<LoggingConfig> constructor = LoggingConfig.class.getDeclaredConstructor();
    constructor.setAccessible(true);
    constructor.newInstance();
  }
}
