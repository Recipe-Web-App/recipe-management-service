package com.recipe_manager.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Service for JWT token validation and processing.
 *
 * <p>This service:
 *
 * <ul>
 *   <li>Validates JWT tokens from the user-management-service
 *   <li>Extracts user information from tokens
 *   <li>Verifies token signatures and expiration
 *   <li>Handles token refresh logic
 * </ul>
 */
@Service
public final class JwtService {

  /** Logger for this class. */
  private static final Logger LOGGER = LoggerFactory.getLogger(JwtService.class);

  /** Secret key for signing JWT tokens. */
  @Value("${app.security.jwt.secret}")
  private String secretKey;

  /** JWT token expiration time in milliseconds. */
  @Value("${app.security.jwt.expiration}")
  private long jwtExpiration;

  /**
   * Extracts the username from the JWT token.
   *
   * @param token the JWT token
   * @return the username
   */
  public String extractUsername(final String token) {
    return extractClaim(token, Claims::getSubject);
  }

  /**
   * Extracts a claim from the JWT token.
   *
   * @param <T> the type of the claim
   * @param token the JWT token
   * @param claimsResolver the function to extract the claim
   * @return the claim value
   */
  public <T> T extractClaim(final String token, final Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  /**
   * Generates a JWT token for a user.
   *
   * @param username The username
   * @return The generated JWT token
   */
  public String generateToken(final String username) {
    return generateToken(new HashMap<>(), username);
  }

  /**
   * Generates a JWT token with extra claims.
   *
   * @param extraClaims Additional claims to include
   * @param username The username
   * @return The generated JWT token
   */
  public String generateToken(final Map<String, Object> extraClaims, final String username) {
    return Jwts.builder()
        .claims(extraClaims)
        .subject(username)
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
        .signWith(getSignInKey())
        .compact();
  }

  /**
   * Validates the JWT token.
   *
   * @param token the JWT token
   * @return true if valid, false otherwise
   */
  public boolean isTokenValid(final String token) {
    try {
      return !isTokenExpired(token);
    } catch (Exception e) {
      LOGGER.warn("Token validation failed: {}", e.getMessage());
      return false;
    }
  }

  /**
   * Checks if a JWT token is expired.
   *
   * @param token The JWT token
   * @return true if the token is expired, false otherwise
   */
  private boolean isTokenExpired(final String token) {
    return extractExpiration(token).before(new Date());
  }

  /**
   * Extracts the expiration date from a JWT token.
   *
   * @param token The JWT token
   * @return The expiration date
   */
  private Date extractExpiration(final String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  /**
   * Extracts all claims from a JWT token.
   *
   * @param token The JWT token
   * @return All claims from the token
   */
  private Claims extractAllClaims(final String token) {
    return Jwts.parser().verifyWith(getSignInKey()).build().parseSignedClaims(token).getPayload();
  }

  /**
   * Gets the signing key for JWT operations.
   *
   * @return The signing key
   */
  private SecretKey getSignInKey() {
    return Keys.hmacShaKeyFor(secretKey.getBytes(java.nio.charset.StandardCharsets.UTF_8));
  }

  /**
   * Extracts user ID from JWT token claims.
   *
   * @param token The JWT token
   * @return The user ID or null if not found
   */
  public String extractUserId(final String token) {
    try {
      Claims claims = extractAllClaims(token);
      return claims.get("userId", String.class);
    } catch (Exception e) {
      LOGGER.warn("Failed to extract user ID from token: {}", e.getMessage());
      return null;
    }
  }

  /**
   * Extracts user roles from JWT token claims.
   *
   * @param token The JWT token
   * @return Array of user roles or empty array if not found
   */
  public String[] extractRoles(final String token) {
    try {
      Claims claims = extractAllClaims(token);
      Object rolesObj = claims.get("roles");

      if (rolesObj instanceof String) {
        return new String[] {(String) rolesObj};
      } else if (rolesObj instanceof java.util.List) {
        @SuppressWarnings("unchecked")
        java.util.List<String> rolesList = (java.util.List<String>) rolesObj;
        return rolesList.toArray(new String[0]);
      }

      return new String[0];
    } catch (Exception e) {
      LOGGER.warn("Failed to extract roles from token: {}", e.getMessage());
      return new String[0];
    }
  }

  /**
   * Gets the time until token expiration in milliseconds.
   *
   * @param token The JWT token
   * @return Time until expiration in milliseconds, or 0 if already expired
   */
  public long getTimeUntilExpiration(final String token) {
    try {
      Date expiration = extractExpiration(token);
      long timeUntilExpiration = expiration.getTime() - System.currentTimeMillis();
      return Math.max(0, timeUntilExpiration);
    } catch (Exception e) {
      LOGGER.warn("Failed to calculate time until expiration: {}", e.getMessage());
      return 0;
    }
  }
}
