package com.recipe_manager.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Test class for CustomUserDetailsService.
 * Verifies that custom user details service works correctly.
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class CustomUserDetailsServiceTest {

  @Mock
  private JwtService jwtService;

  private CustomUserDetailsService customUserDetailsService;

  @BeforeEach
  void setUp() {
    customUserDetailsService = new CustomUserDetailsService(jwtService);
  }

  /**
   * Test that custom user details service can be instantiated.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should instantiate custom user details service")
  void shouldInstantiateCustomUserDetailsService() {
    assertNotNull(customUserDetailsService);
  }

  /**
   * Test loading user details by username.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should load user details by username")
  void shouldLoadUserDetailsByUsername() {
    // Given
    String username = "testuser";

    // When
    UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

    // Then
    assertNotNull(userDetails);
    assertEquals(username, userDetails.getUsername());
  }

  /**
   * Test loading user details with null username.
   */
  @Test
  @Tag("error-processing")
  @DisplayName("Should handle null username")
  void shouldHandleNullUsername() {
    // Given
    String username = null;

    // When & Then
    assertThrows(IllegalArgumentException.class, () -> {
      customUserDetailsService.loadUserByUsername(username);
    });
  }

  /**
   * Test loading user details with empty username.
   */
  @Test
  @Tag("error-processing")
  @DisplayName("Should handle empty username")
  void shouldHandleEmptyUsername() {
    // Given
    String username = "";

    // When & Then
    assertThrows(IllegalArgumentException.class, () -> {
      customUserDetailsService.loadUserByUsername(username);
    });
  }

  /**
   * Test loading user details from token.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should load user details from token")
  void shouldLoadUserDetailsFromToken() {
    // Given
    String token = "test-token";
    String username = "testuser";
    String[] roles = { "USER", "ADMIN" };
    String[] scopes = { "read", "write" };

    when(jwtService.extractUsername(token)).thenReturn(username);
    when(jwtService.extractRoles(token)).thenReturn(roles);
    when(jwtService.extractScopes(token)).thenReturn(scopes);

    // When
    UserDetails userDetails = customUserDetailsService.loadUserFromToken(token);

    // Then
    assertNotNull(userDetails);
    assertEquals(username, userDetails.getUsername());
    assertFalse(userDetails.getAuthorities().isEmpty());
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should handle null token in loadUserFromToken")
  void shouldHandleNullTokenInLoadUserFromToken() {
    assertThrows(UsernameNotFoundException.class,
        () -> customUserDetailsService.loadUserFromToken(null));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should handle empty token in loadUserFromToken")
  void shouldHandleEmptyTokenInLoadUserFromToken() {
    assertThrows(UsernameNotFoundException.class,
        () -> customUserDetailsService.loadUserFromToken(""));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should handle token with missing username in loadUserFromToken")
  void shouldHandleTokenWithMissingUsernameInLoadUserFromToken() {
    String token = "test-token";
    when(jwtService.extractUsername(token)).thenReturn(null);
    assertThrows(UsernameNotFoundException.class,
        () -> customUserDetailsService.loadUserFromToken(token));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle token with missing roles in loadUserFromToken")
  void shouldHandleTokenWithMissingRolesInLoadUserFromToken() {
    String token = "test-token";
    String username = "testuser";
    when(jwtService.extractUsername(token)).thenReturn(username);
    when(jwtService.extractRoles(token)).thenReturn(null);
    when(jwtService.extractScopes(token)).thenReturn(null);

    // Should create user with default ROLE_USER authority
    UserDetails userDetails = customUserDetailsService.loadUserFromToken(token);

    assertNotNull(userDetails);
    assertEquals(username, userDetails.getUsername());
    assertEquals(1, userDetails.getAuthorities().size());
    assertEquals("ROLE_USER", userDetails.getAuthorities().iterator().next().getAuthority());
  }
}
