package com.recipe_manager.security;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Custom UserDetailsService that creates user details from JWT token information.
 *
 * <p>This service:
 *
 * <ul>
 *   <li>Creates UserDetails from JWT token claims
 *   <li>Extracts roles and permissions from tokens
 *   <li>Does not require a local user database
 *   <li>Works with the user-management-service
 * </ul>
 */
@Service
public final class CustomUserDetailsService implements UserDetailsService {

  /** Logger for this class. */
  private static final Logger LOGGER = LoggerFactory.getLogger(CustomUserDetailsService.class);

  /** JWT service for extracting user information from tokens. */
  private final JwtService jwtService;

  /**
   * Constructs a new CustomUserDetailsService.
   *
   * @param jwtService the JWT service
   */
  public CustomUserDetailsService(final JwtService jwtService) {
    this.jwtService = jwtService;
  }

  /**
   * Loads the user details by username.
   *
   * @param username the username
   * @return the user details
   * @throws UsernameNotFoundException if the user is not found
   */
  @Override
  public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
    // This method is called by the JWT filter
    // We create a basic user with default authorities
    // The actual user details will be extracted from the JWT token

    LOGGER.debug("Loading user details for username: {}", username);

    // Create a basic user with default authorities
    // The actual roles will be extracted from the JWT token in the filter
    return User.builder()
        .username(username)
        .password("") // No password needed for JWT authentication
        .authorities(Arrays.asList(new SimpleGrantedAuthority("USER")))
        .accountExpired(false)
        .accountLocked(false)
        .credentialsExpired(false)
        .disabled(false)
        .build();
  }

  /**
   * Loads the user details from a JWT token.
   *
   * @param token the JWT token
   * @return the user details
   */
  public UserDetails loadUserFromToken(final String token) {
    try {
      final String username = jwtService.extractUsername(token);
      final String[] roles = jwtService.extractRoles(token);

      if (username == null) {
        LOGGER.warn("No username found in JWT token");
        throw new UsernameNotFoundException("No username found in token");
      }

      // Convert roles to Spring Security authorities
      List<SimpleGrantedAuthority> authorities =
          Arrays.stream(roles)
              .map(
                  role ->
                      new SimpleGrantedAuthority("ROLE_" + role.toUpperCase(java.util.Locale.ROOT)))
              .collect(Collectors.toList());

      // Add default USER role if no roles found
      if (authorities.isEmpty()) {
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
      }

      LOGGER.debug("Created user details for {} with roles: {}", username, authorities);

      return User.builder()
          .username(username)
          .password("") // No password needed for JWT authentication
          .authorities(authorities)
          .accountExpired(false)
          .accountLocked(false)
          .credentialsExpired(false)
          .disabled(false)
          .build();

    } catch (Exception e) {
      LOGGER.warn("Failed to create user details from token: {}", e.getMessage());
      throw new UsernameNotFoundException("Failed to create user details from token", e);
    }
  }
}
