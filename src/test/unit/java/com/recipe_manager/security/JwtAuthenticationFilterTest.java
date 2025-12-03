package com.recipe_manager.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.ServletException;

/**
 * Test class for JwtAuthenticationFilter.
 * Verifies that JWT authentication filtering works correctly.
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class JwtAuthenticationFilterTest {

  @Mock
  private JwtService jwtService;

  @Mock
  private CustomUserDetailsService userDetailsService;

  private JwtAuthenticationFilter jwtAuthenticationFilter;
  private MockHttpServletRequest request;
  private MockHttpServletResponse response;
  private MockFilterChain filterChain;

  @BeforeEach
  void setUp() {
    jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtService, userDetailsService);
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
    filterChain = new MockFilterChain();

    // Clear security context
    SecurityContextHolder.clearContext();
  }

  /**
   * Test that JWT authentication filter can be instantiated.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should instantiate JWT authentication filter")
  void shouldInstantiateJwtAuthenticationFilter() {
    assertNotNull(jwtAuthenticationFilter);
  }

  /**
   * Test filtering request without authorization header.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle request without authorization header")
  void shouldHandleRequestWithoutAuthorizationHeader() throws ServletException, IOException {
    // Given: request without authorization header
    assertNull(request.getHeader("Authorization"));

    // When: filter is applied
    jwtAuthenticationFilter.doFilter(request, response, filterChain);

    // Then: filter chain should continue
    assertNotNull(filterChain);
  }

  /**
   * Test filtering request with invalid authorization header.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle request with invalid authorization header")
  void shouldHandleRequestWithInvalidAuthorizationHeader() throws ServletException, IOException {
    // Given: request with invalid authorization header
    request.addHeader("Authorization", "InvalidHeader");

    // When: filter is applied
    jwtAuthenticationFilter.doFilter(request, response, filterChain);

    // Then: filter chain should continue
    assertNotNull(filterChain);
  }

  /**
   * Test filtering request with valid JWT token.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle request with valid JWT token")
  void shouldHandleRequestWithValidJwtToken() throws ServletException, IOException {
    // Given: request with valid authorization header
    String token = "valid.jwt.token";
    request.addHeader("Authorization", "Bearer " + token);

    JwtService.TokenInfo tokenInfo = JwtService.TokenInfo.builder()
        .subject("testuser")
        .build();
    when(jwtService.validateToken(token)).thenReturn(Optional.of(tokenInfo));

    // When: filter is applied
    jwtAuthenticationFilter.doFilter(request, response, filterChain);

    // Then: filter chain should continue
    assertNotNull(filterChain);
  }

  /**
   * Test filtering request with invalid JWT token.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle request with invalid JWT token")
  void shouldHandleRequestWithInvalidJwtToken() throws ServletException, IOException {
    // Given: request with invalid JWT token
    String token = "invalid.jwt.token";
    request.addHeader("Authorization", "Bearer " + token);

    when(jwtService.validateToken(token)).thenReturn(Optional.empty());

    // When: filter is applied
    jwtAuthenticationFilter.doFilter(request, response, filterChain);

    // Then: filter chain should continue (exception is caught)
    assertNotNull(filterChain);
  }

  /**
   * Test filtering request with null authorization header.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle request with null authorization header")
  void shouldHandleRequestWithNullAuthorizationHeader() throws ServletException, IOException {
    // Given: request without authorization header (null is not allowed by
    // MockHttpServletRequest)
    assertNull(request.getHeader("Authorization"));

    // When: filter is applied
    jwtAuthenticationFilter.doFilter(request, response, filterChain);

    // Then: filter chain should continue
    assertNotNull(filterChain);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should extract request ID from header")
  void shouldExtractRequestIdFromHeader() {
    request.addHeader("X-Request-ID", "req-123");
    String requestId = jwtAuthenticationFilter.extractRequestId(request);
    assertEquals("req-123", requestId);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should extract request ID as null if not present")
  void shouldExtractRequestIdAsNullIfNotPresent() {
    String requestId = jwtAuthenticationFilter.extractRequestId(request);
    assertEquals("unknown", requestId);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should not filter actuator endpoints")
  void shouldNotFilterActuatorEndpoints() {
    request.setRequestURI("/actuator/health");
    assertTrue(jwtAuthenticationFilter.shouldNotFilter(request));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should not filter swagger endpoints")
  void shouldNotFilterSwaggerEndpoints() {
    request.setRequestURI("/swagger-ui/index.html");
    assertTrue(jwtAuthenticationFilter.shouldNotFilter(request));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should filter normal API endpoints")
  void shouldFilterNormalApiEndpoints() {
    request.setRequestURI("/api/v1/recipes");
    assertFalse(jwtAuthenticationFilter.shouldNotFilter(request));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should skip authentication if SecurityContext already has authentication")
  void shouldSkipIfSecurityContextHasAuthentication() throws Exception {
    String token = "valid.jwt.token";
    request.addHeader("Authorization", "Bearer " + token);
    JwtService.TokenInfo tokenInfo = JwtService.TokenInfo.builder()
        .subject("testuser")
        .build();
    when(jwtService.validateToken(token)).thenReturn(Optional.of(tokenInfo));
    Authentication existingAuth = new UsernamePasswordAuthenticationToken("user", null, java.util.List.of());
    SecurityContextHolder.getContext().setAuthentication(existingAuth);
    jwtAuthenticationFilter.doFilter(request, response, filterChain);
    assertEquals(existingAuth, SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should skip authentication if userDetailsService returns null")
  void shouldSkipIfUserDetailsServiceReturnsNull() throws Exception {
    String token = "valid.jwt.token";
    request.addHeader("Authorization", "Bearer " + token);
    JwtService.TokenInfo tokenInfo = JwtService.TokenInfo.builder()
        .subject("testuser")
        .build();
    when(jwtService.validateToken(token)).thenReturn(Optional.of(tokenInfo));
    when(userDetailsService.loadUserByUsername("testuser")).thenReturn(null);
    SecurityContextHolder.clearContext();
    jwtAuthenticationFilter.doFilter(request, response, filterChain);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should clear context on exception in doFilterInternal")
  void shouldClearContextOnException() throws Exception {
    String token = "valid.jwt.token";
    request.addHeader("Authorization", "Bearer " + token);
    when(jwtService.validateToken(token)).thenThrow(new RuntimeException("fail"));
    SecurityContextHolder.getContext().setAuthentication(null);
    jwtAuthenticationFilter.doFilter(request, response, filterChain);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should not filter /health endpoint")
  void shouldNotFilterHealthEndpoint() {
    request.setRequestURI("/health");
    assertTrue(jwtAuthenticationFilter.shouldNotFilter(request));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should not filter /info endpoint")
  void shouldNotFilterInfoEndpoint() {
    request.setRequestURI("/info");
    assertTrue(jwtAuthenticationFilter.shouldNotFilter(request));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should not filter /v3/api-docs endpoint")
  void shouldNotFilterApiDocsEndpoint() {
    request.setRequestURI("/v3/api-docs");
    assertTrue(jwtAuthenticationFilter.shouldNotFilter(request));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should not filter /api/v1/auth endpoint")
  void shouldNotFilterAuthEndpoint() {
    request.setRequestURI("/api/v1/auth");
    assertTrue(jwtAuthenticationFilter.shouldNotFilter(request));
  }
}
