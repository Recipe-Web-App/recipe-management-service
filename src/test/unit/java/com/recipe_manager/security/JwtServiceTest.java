package com.recipe_manager.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.lenient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.recipe_manager.config.ExternalServicesConfig;

/**
 * Test class for JwtService.
 * Verifies that JWT service works correctly with OAuth2 integration.
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class JwtServiceTest {

  @Mock private OAuth2Client oauth2Client;

  @Mock private ExternalServicesConfig externalServicesConfig;

  @Mock private ExternalServicesConfig.OAuth2ServiceConfig oauth2ServiceConfig;

  private JwtService jwtService;

  @BeforeEach
  void setUp() {
    // Setup OAuth2 service configuration mock
    when(externalServicesConfig.getOauth2Service()).thenReturn(oauth2ServiceConfig);
    // Only stub what we actually use
    lenient().when(oauth2ServiceConfig.getEnabled()).thenReturn(false);
    lenient().when(oauth2ServiceConfig.getIntrospectionEnabled()).thenReturn(false);

    jwtService = new JwtService(oauth2Client, externalServicesConfig);

    // Set test values for JWT configuration
    ReflectionTestUtils.setField(jwtService, "secretKey", "test-secret-key-for-jwt-service-testing-very-long-key");
    ReflectionTestUtils.setField(jwtService, "jwtExpiration", 3600000L); // 1 hour in milliseconds
  }

  /**
   * Test that JWT service can be instantiated.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should instantiate JWT service")
  void shouldInstantiateJwtService() {
    assertNotNull(jwtService);
  }

  /**
   * Test generating JWT token.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should generate JWT token")
  void shouldGenerateJwtToken() {
    // Given
    String username = "testuser";

    // When
    String token = jwtService.generateToken(username);

    // Then
    assertNotNull(token);
    assertFalse(token.isEmpty());
  }

  /**
   * Test extracting username from JWT token.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should extract username from JWT token")
  void shouldExtractUsernameFromJwtToken() {
    // Given
    String username = "testuser";
    String token = jwtService.generateToken(username);

    // When
    String extractedUsername = jwtService.extractUsername(token);

    // Then
    assertNotNull(extractedUsername);
    assertEquals(username, extractedUsername);
  }

  /**
   * Test extracting roles from JWT token.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should extract roles from JWT token")
  void shouldExtractRolesFromJwtToken() {
    // Given
    String username = "testuser";
    String token = jwtService.generateToken(username);

    // When
    String[] roles = jwtService.extractRoles(token);

    // Then
    assertNotNull(roles);
    assertTrue(roles.length >= 0);
  }

  /**
   * Test extracting roles from token with no roles.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should extract roles from token with no roles")
  void shouldExtractRolesFromTokenWithNoRoles() {
    String username = "testuser";
    String token = jwtService.generateToken(username);
    String[] roles = jwtService.extractRoles(token);
    assertNotNull(roles);
  }

  /**
   * Test getting time until expiration for valid token.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should get time until expiration for valid token")
  void shouldGetTimeUntilExpirationForValidToken() {
    String username = "testuser";
    String token = jwtService.generateToken(username);
    long time = jwtService.getTimeUntilExpiration(token);
    assertTrue(time > 0);
  }

  /**
   * Test extracting user ID from token.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should extract user ID from token")
  void shouldExtractUserIdFromToken() {
    java.util.Map<String, Object> claims = new java.util.HashMap<>();
    claims.put("userId", "test-user-id");
    String token = jwtService.generateToken(claims, "testuser");
    String userId = jwtService.extractUserId(token);
    assertEquals("test-user-id", userId);
  }

  /**
   * Test validating JWT token.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should validate JWT token")
  void shouldValidateJwtToken() {
    // Given
    String username = "testuser";
    java.util.Map<String, Object> claims = new java.util.HashMap<>();
    claims.put("type", "access_token"); // Add OAuth2 token type claim
    String token = jwtService.generateToken(claims, username);

    // When
    boolean isValid = jwtService.isTokenValid(token);

    // Then
    assertTrue(isValid);
  }

  /**
   * Test validating JWT token with wrong username.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should reject invalid JWT token")
  void shouldRejectInvalidJwtToken() {
    // Given
    String invalidToken = "invalid.token.here";

    // When
    boolean isValid = jwtService.isTokenValid(invalidToken);

    // Then
    assertFalse(isValid);
  }

  /**
   * Test handling null token.
   */
  @Test
  @Tag("error-processing")
  @DisplayName("Should handle null token")
  void shouldHandleNullToken() {
    // Given
    String token = null;

    // When & Then
    assertThrows(IllegalArgumentException.class, () -> {
      jwtService.extractUsername(token);
    });
  }

  /**
   * Test handling empty token.
   */
  @Test
  @Tag("error-processing")
  @DisplayName("Should handle empty token")
  void shouldHandleEmptyToken() {
    // Given
    String token = "";

    // When & Then
    assertThrows(IllegalArgumentException.class, () -> {
      jwtService.extractUsername(token);
    });
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should extract roles as string from JWT token")
  void shouldExtractRolesAsString() {
    java.util.Map<String, Object> claims = new java.util.HashMap<>();
    claims.put("roles", "ADMIN");
    String token = jwtService.generateToken(claims, "testuser");
    String[] roles = jwtService.extractRoles(token);
    assertNotNull(roles);
    assertEquals(1, roles.length);
    assertEquals("ADMIN", roles[0]);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should extract roles as list from JWT token")
  void shouldExtractRolesAsList() {
    java.util.Map<String, Object> claims = new java.util.HashMap<>();
    java.util.List<String> rolesList = java.util.Arrays.asList("USER", "ADMIN");
    claims.put("roles", rolesList);
    String token = jwtService.generateToken(claims, "testuser");
    String[] roles = jwtService.extractRoles(token);
    assertNotNull(roles);
    assertEquals(2, roles.length);
    assertEquals("USER", roles[0]);
    assertEquals("ADMIN", roles[1]);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should return null for extractUserId if claim missing")
  void shouldReturnNullForExtractUserIdIfClaimMissing() {
    String token = jwtService.generateToken("testuser");
    String userId = jwtService.extractUserId(token);
    assertEquals(null, userId);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should return false for isTokenValid if token expired")
  void shouldReturnFalseForIsTokenValidIfTokenExpired() {
    ReflectionTestUtils.setField(jwtService, "jwtExpiration", -1000L); // already expired
    String token = jwtService.generateToken("testuser");
    boolean isValid = jwtService.isTokenValid(token);
    assertFalse(isValid);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should return 0 for getTimeUntilExpiration if token expired")
  void shouldReturnZeroForGetTimeUntilExpirationIfTokenExpired() {
    ReflectionTestUtils.setField(jwtService, "jwtExpiration", -1000L); // already expired
    String token = jwtService.generateToken("testuser");
    long time = jwtService.getTimeUntilExpiration(token);
    assertEquals(0, time);
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should handle exception in extractUserId")
  void shouldHandleExceptionInExtractUserId() {
    String invalidToken = "invalid.token.here";
    String userId = jwtService.extractUserId(invalidToken);
    assertNull(userId);
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should handle exception in extractRoles")
  void shouldHandleExceptionInExtractRoles() {
    String invalidToken = "invalid.token.here";
    String[] roles = jwtService.extractRoles(invalidToken);
    assertNotNull(roles);
    assertEquals(0, roles.length);
  }
}
