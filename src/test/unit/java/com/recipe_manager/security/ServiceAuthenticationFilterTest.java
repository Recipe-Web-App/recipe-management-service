package com.recipe_manager.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.recipe_manager.config.ExternalServicesConfig;

import jakarta.servlet.ServletException;

/**
 * Test class for ServiceAuthenticationFilter.
 * Verifies that OAuth2 service-to-service authentication filtering works correctly.
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class ServiceAuthenticationFilterTest {

  @Mock private JwtService jwtService;

  @Mock private ExternalServicesConfig externalServicesConfig;

  @Mock private ExternalServicesConfig.OAuth2ServiceConfig oauth2ServiceConfig;

  private ServiceAuthenticationFilter serviceAuthenticationFilter;

  @BeforeEach
  void setUp() {
    // Clear SecurityContext before each test
    SecurityContextHolder.clearContext();

    // Set up OAuth2 configuration mocks
    when(externalServicesConfig.getOauth2Service()).thenReturn(oauth2ServiceConfig);

    // Default enabled configuration
    lenient().when(oauth2ServiceConfig.getEnabled()).thenReturn(true);
    lenient().when(oauth2ServiceConfig.getServiceToServiceEnabled()).thenReturn(true);

    serviceAuthenticationFilter = new ServiceAuthenticationFilter(jwtService, externalServicesConfig);
  }

  @AfterEach
  void tearDown() {
    // Clear SecurityContext after each test
    SecurityContextHolder.clearContext();
  }

  @Test
  @DisplayName("Should instantiate service authentication filter")
  void shouldInstantiateServiceAuthenticationFilter() {
    assertNotNull(serviceAuthenticationFilter);
  }

  @Test
  @DisplayName("Should skip authentication when OAuth2 service is disabled")
  void shouldSkipAuthenticationWhenOAuth2ServiceDisabled() throws ServletException, IOException {
    // Given
    when(oauth2ServiceConfig.getEnabled()).thenReturn(false);

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain filterChain = new MockFilterChain();

    // When
    serviceAuthenticationFilter.doFilter(request, response, filterChain);

    // Then
    verify(jwtService, never()).validateToken(anyString());
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  @DisplayName("Should skip authentication when service-to-service is disabled")
  void shouldSkipAuthenticationWhenServiceToServiceDisabled() throws ServletException, IOException {
    // Given
    when(oauth2ServiceConfig.getServiceToServiceEnabled()).thenReturn(false);

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain filterChain = new MockFilterChain();

    // When
    serviceAuthenticationFilter.doFilter(request, response, filterChain);

    // Then
    verify(jwtService, never()).validateToken(anyString());
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  @DisplayName("Should authenticate service with valid Bearer token")
  void shouldAuthenticateServiceWithValidBearerToken() throws ServletException, IOException {
    // Given
    String token = "valid-jwt-token";
    String clientId = "service-client";

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer " + token);
    request.addHeader("X-Service-Name", "test-service");

    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain filterChain = new MockFilterChain();

    JwtService.TokenInfo tokenInfo = JwtService.TokenInfo.builder()
        .clientId(clientId)
        .tokenType("access_token")
        .build();
    when(jwtService.validateToken(token)).thenReturn(Optional.of(tokenInfo));

    // When
    serviceAuthenticationFilter.doFilter(request, response, filterChain);

    // Then
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    assertNotNull(auth);
    assertEquals("service-test-service", auth.getPrincipal());
    assertTrue(auth.getAuthorities().stream()
        .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_SERVICE")));
  }

  @Test
  @DisplayName("Should authenticate service with client ID when service name is missing")
  void shouldAuthenticateServiceWithClientIdWhenServiceNameMissing() throws ServletException, IOException {
    // Given
    String token = "valid-jwt-token";
    String clientId = "service-client";

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer " + token);

    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain filterChain = new MockFilterChain();

    JwtService.TokenInfo tokenInfo = JwtService.TokenInfo.builder()
        .clientId(clientId)
        .tokenType("access_token")
        .build();
    when(jwtService.validateToken(token)).thenReturn(Optional.of(tokenInfo));

    // When
    serviceAuthenticationFilter.doFilter(request, response, filterChain);

    // Then
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    assertNotNull(auth);
    assertEquals("service-service-client", auth.getPrincipal());
  }

  @Test
  @DisplayName("Should not authenticate with invalid token")
  void shouldNotAuthenticateWithInvalidToken() throws ServletException, IOException {
    // Given
    String token = "invalid-jwt-token";

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer " + token);

    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain filterChain = new MockFilterChain();

    when(jwtService.validateToken(token)).thenReturn(Optional.empty());

    // When
    serviceAuthenticationFilter.doFilter(request, response, filterChain);

    // Then - exception is caught, auth is null, filter chain continues
    assertNull(SecurityContextHolder.getContext().getAuthentication());
    verify(jwtService).validateToken(token);
  }

  @Test
  @DisplayName("Should not authenticate with non-access token type")
  void shouldNotAuthenticateWithNonAccessTokenType() throws ServletException, IOException {
    // Given
    String token = "valid-jwt-token";
    String clientId = "service-client";

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer " + token);

    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain filterChain = new MockFilterChain();

    JwtService.TokenInfo tokenInfo = JwtService.TokenInfo.builder()
        .clientId(clientId)
        .tokenType("refresh_token")
        .build();
    when(jwtService.validateToken(token)).thenReturn(Optional.of(tokenInfo));

    // When
    serviceAuthenticationFilter.doFilter(request, response, filterChain);

    // Then
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  @DisplayName("Should not authenticate without Bearer token")
  void shouldNotAuthenticateWithoutBearerToken() throws ServletException, IOException {
    // Given
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain filterChain = new MockFilterChain();

    // When
    serviceAuthenticationFilter.doFilter(request, response, filterChain);

    // Then
    verify(jwtService, never()).validateToken(anyString());
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  @DisplayName("Should handle authentication exception gracefully")
  void shouldHandleAuthenticationExceptionGracefully() throws ServletException, IOException {
    // Given
    String token = "problematic-token";

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer " + token);
    request.addHeader("X-Request-ID", "test-request-123");

    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain filterChain = new MockFilterChain();

    when(jwtService.validateToken(token)).thenThrow(new RuntimeException("Token parsing error"));

    // When
    serviceAuthenticationFilter.doFilter(request, response, filterChain);

    // Then
    assertNull(SecurityContextHolder.getContext().getAuthentication());
    // Should continue filter chain even after exception
  }

  @Test
  @DisplayName("Should not filter actuator endpoints")
  void shouldNotFilterActuatorEndpoints() throws ServletException, IOException {
    // Given
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setRequestURI("/actuator/health");
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain filterChain = new MockFilterChain();

    // When
    serviceAuthenticationFilter.doFilter(request, response, filterChain);

    // Then
    verify(jwtService, never()).validateToken(anyString());
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  @DisplayName("Should not filter health endpoints")
  void shouldNotFilterHealthEndpoints() throws ServletException, IOException {
    // Given
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setRequestURI("/health");
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain filterChain = new MockFilterChain();

    // When
    serviceAuthenticationFilter.doFilter(request, response, filterChain);

    // Then
    verify(jwtService, never()).validateToken(anyString());
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  @DisplayName("Should NOT authenticate as service when token has user subject (user token)")
  void shouldNotAuthenticateAsServiceWhenTokenHasUserSubject() throws ServletException, IOException {
    // Given - User token with both subject (user UUID) AND client_id
    String token = "user-jwt-token";
    String clientId = "user-client";
    String userSubject = "fe8305cd-2ee8-463a-a009-c624c2a3ca53"; // User UUID

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer " + token);
    request.addHeader("X-Service-Name", "test-service");

    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain filterChain = new MockFilterChain();

    JwtService.TokenInfo tokenInfo = JwtService.TokenInfo.builder()
        .subject(userSubject)
        .clientId(clientId)
        .tokenType("access_token")
        .build();
    when(jwtService.validateToken(token)).thenReturn(Optional.of(tokenInfo));

    // When
    serviceAuthenticationFilter.doFilter(request, response, filterChain);

    // Then - Should NOT set service authentication, allowing JwtAuthenticationFilter to handle it
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  @DisplayName("Should authenticate as service when subject equals client_id (client_credentials flow)")
  void shouldAuthenticateAsServiceWhenSubjectEqualsClientId() throws ServletException, IOException {
    // Given - Service token where subject == client_id (client_credentials flow)
    String token = "service-jwt-token";
    String clientId = "recipe-scraper-service";

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer " + token);
    request.addHeader("X-Service-Name", "recipe-scraper");

    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain filterChain = new MockFilterChain();

    JwtService.TokenInfo tokenInfo = JwtService.TokenInfo.builder()
        .subject(clientId) // Subject equals client_id in client_credentials flow
        .clientId(clientId)
        .tokenType("access_token")
        .build();
    when(jwtService.validateToken(token)).thenReturn(Optional.of(tokenInfo));

    // When
    serviceAuthenticationFilter.doFilter(request, response, filterChain);

    // Then - Should set service authentication
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    assertNotNull(auth);
    assertEquals("service-recipe-scraper", auth.getPrincipal());
    assertTrue(auth.getAuthorities().stream()
        .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_SERVICE")));
  }
}
