package com.recipe_manager.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Unit tests for DevAuthenticationFilter.
 */
@Tag("unit")
class DevAuthenticationFilterTest {

  private DevAuthenticationFilter filter;
  private HttpServletRequest request;
  private HttpServletResponse response;
  private FilterChain filterChain;

  @BeforeEach
  void setUp() {
    filter = new DevAuthenticationFilter();
    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);
    filterChain = mock(FilterChain.class);
    SecurityContextHolder.clearContext();
  }

  @Test
  void testDoFilterInternal_ValidUuidHeader_SetsAuthentication()
      throws ServletException, IOException {
    UUID userId = UUID.randomUUID();
    when(request.getHeader("X-User-Id")).thenReturn(userId.toString());
    when(request.getHeader("X-Request-ID")).thenReturn("test-request-id");
    when(request.getRequestURI()).thenReturn("/api/v1/recipes");

    filter.doFilterInternal(request, response, filterChain);

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    assertNotNull(auth, "Authentication should be set");
    assertEquals(userId.toString(), auth.getName());
    assertTrue(
        auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")),
        "Should have ROLE_USER authority");
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void testDoFilterInternal_NoHeader_NoAuthentication() throws ServletException, IOException {
    when(request.getHeader("X-User-Id")).thenReturn(null);
    when(request.getRequestURI()).thenReturn("/api/v1/recipes");

    filter.doFilterInternal(request, response, filterChain);

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    assertNull(auth, "Authentication should not be set when header is missing");
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void testDoFilterInternal_EmptyHeader_NoAuthentication() throws ServletException, IOException {
    when(request.getHeader("X-User-Id")).thenReturn("");
    when(request.getRequestURI()).thenReturn("/api/v1/recipes");

    filter.doFilterInternal(request, response, filterChain);

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    assertNull(auth, "Authentication should not be set when header is empty");
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void testDoFilterInternal_InvalidUuid_NoAuthentication() throws ServletException, IOException {
    when(request.getHeader("X-User-Id")).thenReturn("not-a-valid-uuid");
    when(request.getHeader("X-Request-ID")).thenReturn("test-request-id");
    when(request.getRequestURI()).thenReturn("/api/v1/recipes");

    filter.doFilterInternal(request, response, filterChain);

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    assertNull(auth, "Authentication should not be set when UUID is invalid");
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void testDoFilterInternal_ExistingAuthentication_DoesNotOverwrite()
      throws ServletException, IOException {
    // Set up existing authentication
    Authentication existingAuth = mock(Authentication.class);
    when(existingAuth.getName()).thenReturn("existing-user");
    SecurityContextHolder.getContext().setAuthentication(existingAuth);

    UUID newUserId = UUID.randomUUID();
    when(request.getHeader("X-User-Id")).thenReturn(newUserId.toString());
    when(request.getRequestURI()).thenReturn("/api/v1/recipes");

    filter.doFilterInternal(request, response, filterChain);

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    assertEquals(
        "existing-user", auth.getName(), "Should not overwrite existing authentication");
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void testShouldNotFilter_ActuatorPath_ReturnsTrue() {
    when(request.getRequestURI()).thenReturn("/actuator/health");
    assertTrue(filter.shouldNotFilter(request));
  }

  @Test
  void testShouldNotFilter_HealthPath_ReturnsTrue() {
    when(request.getRequestURI()).thenReturn("/health");
    assertTrue(filter.shouldNotFilter(request));
  }

  @Test
  void testShouldNotFilter_InfoPath_ReturnsTrue() {
    when(request.getRequestURI()).thenReturn("/info");
    assertTrue(filter.shouldNotFilter(request));
  }

  @Test
  void testShouldNotFilter_SwaggerPath_ReturnsTrue() {
    when(request.getRequestURI()).thenReturn("/swagger-ui/index.html");
    assertTrue(filter.shouldNotFilter(request));
  }

  @Test
  void testShouldNotFilter_ApiDocsPath_ReturnsTrue() {
    when(request.getRequestURI()).thenReturn("/v3/api-docs");
    assertTrue(filter.shouldNotFilter(request));
  }

  @Test
  void testShouldNotFilter_ApiPath_ReturnsFalse() {
    when(request.getRequestURI()).thenReturn("/api/v1/recipes");
    assertTrue(!filter.shouldNotFilter(request));
  }
}
