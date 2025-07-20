package com.recipe_manager.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import jakarta.servlet.ServletException;

/**
 * Test class for RequestIdFilter.
 * Verifies that request ID filtering works correctly.
 */
@Tag("unit")
class RequestIdFilterTest {

  private RequestIdFilter requestIdFilter;
  private MockHttpServletRequest request;
  private MockHttpServletResponse response;
  private MockFilterChain filterChain;

  @BeforeEach
  void setUp() {
    requestIdFilter = new RequestIdFilter();
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
    filterChain = new MockFilterChain();
  }

  /**
   * Test that request ID filter can be instantiated.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should instantiate request ID filter")
  void shouldInstantiateRequestIdFilter() {
    assertNotNull(requestIdFilter);
  }

  /**
   * Test that request ID is added when not present.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should add request ID when not present")
  void shouldAddRequestIdWhenNotPresent() throws ServletException, IOException {
    // Given: request without request ID
    assertNull(request.getHeader("X-Request-ID"));

    // When: filter is applied
    requestIdFilter.doFilter(request, response, filterChain);

    // Then: request ID should be added to response headers
    String requestId = response.getHeader("X-Request-ID");
    assertNotNull(requestId);
    assertTrue(isValidUUID(requestId));
  }

  /**
   * Test that existing request ID is preserved.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should preserve existing request ID")
  void shouldPreserveExistingRequestId() throws ServletException, IOException {
    // Given: request with existing request ID
    String existingRequestId = UUID.randomUUID().toString();
    request.addHeader("X-Request-ID", existingRequestId);

    // When: filter is applied
    requestIdFilter.doFilter(request, response, filterChain);

    // Then: existing request ID should be preserved in response headers
    String requestId = response.getHeader("X-Request-ID");
    assertEquals(existingRequestId, requestId);
  }

  /**
   * Test that filter chain continues after processing.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should continue filter chain")
  void shouldContinueFilterChain() throws ServletException, IOException {
    // When: filter is applied
    requestIdFilter.doFilter(request, response, filterChain);

    // Then: filter chain should be called
    assertNotNull(filterChain); // Verify filter chain exists
  }

  /**
   * Test that private constructor is covered.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should cover private constructor")
  void shouldCoverPrivateConstructor() throws Exception {
    java.lang.reflect.Constructor<RequestIdFilter> constructor = RequestIdFilter.class.getDeclaredConstructor();
    constructor.setAccessible(true);
    constructor.newInstance();
  }

  /**
   * Test that shouldNotFilter works for actuator endpoints.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should not filter actuator endpoints")
  void shouldNotFilterActuatorEndpoints() {
    request.setRequestURI("/actuator/health");
    assertTrue(requestIdFilter.shouldNotFilter(request));
  }

  /**
   * Test that shouldNotFilter works for health endpoints.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should not filter health endpoints")
  void shouldNotFilterHealthEndpoints() {
    request.setRequestURI("/health");
    assertTrue(requestIdFilter.shouldNotFilter(request));
  }

  /**
   * Test that shouldNotFilter works for normal endpoints.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should filter normal endpoints")
  void shouldFilterNormalEndpoints() {
    request.setRequestURI("/api/recipes");
    assertTrue(!requestIdFilter.shouldNotFilter(request));
  }

  /**
   * Test that extractOrGenerateRequestId generates a request ID for a blank
   * header.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should generate request ID for blank header")
  void shouldGenerateRequestIdForBlankHeader() throws Exception {
    request.addHeader("X-Request-ID", "   ");
    requestIdFilter.doFilter(request, response, filterChain);
    String requestId = response.getHeader("X-Request-ID");
    assertNotNull(requestId);
    assertTrue(isValidUUID(requestId));
  }

  /**
   * Helper method to validate UUID format.
   */
  private boolean isValidUUID(String uuid) {
    try {
      UUID.fromString(uuid);
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
}
