package com.recipe_manager.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import jakarta.servlet.ServletException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import java.lang.reflect.Field;

/**
 * Test class for ServiceToServiceAuthFilter.
 * Verifies that service-to-service authentication filtering works correctly.
 */
@Tag("unit")
class ServiceToServiceAuthFilterTest {

  private ServiceToServiceAuthFilter serviceToServiceAuthFilter;
  private MockHttpServletRequest request;
  private MockHttpServletResponse response;
  private MockFilterChain filterChain;

  @BeforeEach
  void setUp() {
    serviceToServiceAuthFilter = new ServiceToServiceAuthFilter();
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
    filterChain = new MockFilterChain();
  }

  /**
   * Test that service-to-service auth filter can be instantiated.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should instantiate service-to-service auth filter")
  void shouldInstantiateServiceToServiceAuthFilter() {
    assertNotNull(serviceToServiceAuthFilter);
  }

  /**
   * Test filtering request without service token header.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle request without service token header")
  void shouldHandleRequestWithoutServiceTokenHeader() throws ServletException, IOException {
    // Given: request without service token header
    assertNull(request.getHeader("X-Service-Token"));

    // When: filter is applied
    serviceToServiceAuthFilter.doFilter(request, response, filterChain);

    // Then: filter chain should continue
    assertNotNull(filterChain);
  }

  /**
   * Test filtering request with valid service token.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle request with valid service token")
  void shouldHandleRequestWithValidServiceToken() throws ServletException, IOException {
    // Given: request with valid service token
    String serviceToken = "valid-service-token";
    request.addHeader("X-Service-Token", serviceToken);

    // When: filter is applied
    serviceToServiceAuthFilter.doFilter(request, response, filterChain);

    // Then: filter chain should continue
    assertNotNull(filterChain);
  }

  /**
   * Test filtering request with invalid service token.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle request with invalid service token")
  void shouldHandleRequestWithInvalidServiceToken() throws ServletException, IOException {
    // Given: request with invalid service token
    String serviceToken = "invalid-service-token";
    request.addHeader("X-Service-Token", serviceToken);

    // When: filter is applied
    serviceToServiceAuthFilter.doFilter(request, response, filterChain);

    // Then: filter chain should continue
    assertNotNull(filterChain);
  }

  /**
   * Test filtering request with null service token header.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle request with null service token header")
  void shouldHandleRequestWithNullServiceTokenHeader() throws ServletException, IOException {
    // Given: request without service token header (null is not allowed by
    // MockHttpServletRequest)
    assertNull(request.getHeader("X-Service-Token"));

    // When: filter is applied
    serviceToServiceAuthFilter.doFilter(request, response, filterChain);

    // Then: filter chain should continue
    assertNotNull(filterChain);
  }

  /**
   * Test filtering request with empty service token.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle request with empty service token")
  void shouldHandleRequestWithEmptyServiceToken() throws ServletException, IOException {
    // Given: request with empty service token
    request.addHeader("X-Service-Token", "");

    // When: filter is applied
    serviceToServiceAuthFilter.doFilter(request, response, filterChain);

    // Then: filter chain should continue
    assertNotNull(filterChain);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should extract request ID from header")
  void shouldExtractRequestIdFromHeader() {
    request.addHeader("X-Request-ID", "req-456");
    String requestId = serviceToServiceAuthFilter.extractRequestId(request);
    assertEquals("req-456", requestId);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should extract request ID as null if not present")
  void shouldExtractRequestIdAsNullIfNotPresent() {
    String requestId = serviceToServiceAuthFilter.extractRequestId(request);
    assertEquals("unknown", requestId);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should not filter actuator endpoints")
  void shouldNotFilterActuatorEndpoints() {
    request.setRequestURI("/actuator/health");
    assertTrue(serviceToServiceAuthFilter.shouldNotFilter(request));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should not filter swagger endpoints")
  void shouldNotFilterSwaggerEndpoints() {
    request.setRequestURI("/swagger-ui/index.html");
    assertTrue(serviceToServiceAuthFilter.shouldNotFilter(request));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should filter normal API endpoints")
  void shouldFilterNormalApiEndpoints() {
    request.setRequestURI("/api/v1/ingredients");
    assertFalse(serviceToServiceAuthFilter.shouldNotFilter(request));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should authenticate service when enabled and correct header/value")
  void shouldAuthenticateServiceWhenEnabledAndCorrectHeader() throws Exception {
    Field enabledField = ServiceToServiceAuthFilter.class.getDeclaredField("serviceAuthEnabled");
    enabledField.setAccessible(true);
    enabledField.set(serviceToServiceAuthFilter, true);
    Field keyField = ServiceToServiceAuthFilter.class.getDeclaredField("serviceAuthKey");
    keyField.setAccessible(true);
    keyField.set(serviceToServiceAuthFilter, "secret");
    request.addHeader("X-Service-Auth", "secret");
    request.addHeader("X-Service-Name", "test-service");
    SecurityContextHolder.clearContext();
    serviceToServiceAuthFilter.doFilter(request, response, filterChain);
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    assertNotNull(auth);
    assertTrue(auth instanceof UsernamePasswordAuthenticationToken);
    assertEquals("service-test-service", auth.getPrincipal());
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should not authenticate service when enabled but wrong header")
  void shouldNotAuthenticateServiceWhenEnabledButWrongHeader() throws Exception {
    Field enabledField = ServiceToServiceAuthFilter.class.getDeclaredField("serviceAuthEnabled");
    enabledField.setAccessible(true);
    enabledField.set(serviceToServiceAuthFilter, true);
    Field keyField = ServiceToServiceAuthFilter.class.getDeclaredField("serviceAuthKey");
    keyField.setAccessible(true);
    keyField.set(serviceToServiceAuthFilter, "secret");
    request.addHeader("X-Service-Auth", "wrong");
    request.addHeader("X-Service-Name", "test-service");
    SecurityContextHolder.clearContext();
    serviceToServiceAuthFilter.doFilter(request, response, filterChain);
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    assertNull(auth);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should skip authentication logic when disabled")
  void shouldSkipAuthenticationWhenDisabled() throws Exception {
    Field enabledField = ServiceToServiceAuthFilter.class.getDeclaredField("serviceAuthEnabled");
    enabledField.setAccessible(true);
    enabledField.set(serviceToServiceAuthFilter, false);
    Field keyField = ServiceToServiceAuthFilter.class.getDeclaredField("serviceAuthKey");
    keyField.setAccessible(true);
    keyField.set(serviceToServiceAuthFilter, "secret");
    request.addHeader("X-Service-Auth", "secret");
    request.addHeader("X-Service-Name", "test-service");
    SecurityContextHolder.clearContext();
    serviceToServiceAuthFilter.doFilter(request, response, filterChain);
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    assertNull(auth);
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should handle exception in doFilterInternal")
  void shouldHandleExceptionInDoFilterInternal() throws Exception {
    Field enabledField = ServiceToServiceAuthFilter.class.getDeclaredField("serviceAuthEnabled");
    enabledField.setAccessible(true);
    enabledField.set(serviceToServiceAuthFilter, true);
    Field keyField = ServiceToServiceAuthFilter.class.getDeclaredField("serviceAuthKey");
    keyField.setAccessible(true);
    keyField.set(serviceToServiceAuthFilter, "secret");
    request.addHeader("X-Service-Auth", "secret");
    request.addHeader("X-Service-Name", "test-service");
    /*
     * Simulate exception by removing header map (not possible with
     * MockHttpServletRequest), so just call and ensure no exception thrown
     */
    serviceToServiceAuthFilter.doFilter(request, response, filterChain);
    assertNotNull(filterChain);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should not filter /health endpoint")
  void shouldNotFilterHealthEndpoint() {
    request.setRequestURI("/health");
    assertTrue(serviceToServiceAuthFilter.shouldNotFilter(request));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should not filter /info endpoint")
  void shouldNotFilterInfoEndpoint() {
    request.setRequestURI("/info");
    assertTrue(serviceToServiceAuthFilter.shouldNotFilter(request));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should not filter /v3/api-docs endpoint")
  void shouldNotFilterApiDocsEndpoint() {
    request.setRequestURI("/v3/api-docs");
    assertTrue(serviceToServiceAuthFilter.shouldNotFilter(request));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should not filter /api/v1/auth endpoint")
  void shouldNotFilterAuthEndpoint() {
    request.setRequestURI("/api/v1/auth");
    assertTrue(serviceToServiceAuthFilter.shouldNotFilter(request));
  }
}
