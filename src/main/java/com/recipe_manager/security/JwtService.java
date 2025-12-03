package com.recipe_manager.security;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.recipe_manager.config.ExternalServicesConfig;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Service for JWT token validation and processing.
 *
 * <p>This service:
 *
 * <ul>
 *   <li>Validates JWT tokens from OAuth2 authentication service
 *   <li>Supports both legacy user-management-service tokens and OAuth2 tokens
 *   <li>Extracts user information from tokens
 *   <li>Verifies token signatures and expiration
 *   <li>Handles OAuth2-specific claims like client_id, scopes, type
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

  /** OAuth2 client for token introspection. */
  private final OAuth2Client oauth2Client;

  /** OAuth2 service configuration. */
  private final ExternalServicesConfig.OAuth2ServiceConfig oauth2Config;

  /**
   * Constructs a new JwtService with OAuth2 support.
   *
   * @param oauth2Client OAuth2 client for token operations
   * @param externalServicesConfig external services configuration
   */
  public JwtService(
      final OAuth2Client oauth2Client, final ExternalServicesConfig externalServicesConfig) {
    this.oauth2Client = oauth2Client;
    this.oauth2Config = externalServicesConfig.getOauth2Service();
  }

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
   * Validates the JWT token using local validation or OAuth2 introspection.
   *
   * @param token the JWT token
   * @return true if valid, false otherwise
   */
  public boolean isTokenValid(final String token) {
    try {
      // Try local JWT validation first
      if (isLocalTokenValid(token)) {
        return true;
      }

      // If OAuth2 introspection is enabled and local validation fails,
      // try introspection as fallback
      if (oauth2Config.getEnabled() && oauth2Config.getIntrospectionEnabled()) {
        return isTokenValidViaIntrospection(token);
      }

      return false;
    } catch (Exception e) {
      LOGGER.warn("Token validation failed: {}", e.getMessage());
      return false;
    }
  }

  /**
   * Validates token and returns claims if valid.
   *
   * <p>This method first attempts local JWT validation. If that fails and OAuth2 introspection is
   * enabled, it falls back to token introspection. The returned TokenInfo contains claims extracted
   * from either the local JWT or the introspection response.
   *
   * @param token the JWT token
   * @return Optional containing TokenInfo if valid, empty if invalid
   */
  public Optional<TokenInfo> validateToken(final String token) {
    // Try local validation first
    try {
      if (isLocalTokenValid(token)) {
        return getTokenInfo(token);
      }
    } catch (Exception e) {
      LOGGER.debug("Local validation failed: {}", e.getMessage());
    }

    // Try introspection if enabled
    if (oauth2Config.getEnabled() && oauth2Config.getIntrospectionEnabled()) {
      return validateTokenViaIntrospection(token);
    }

    return Optional.empty();
  }

  /**
   * Validates token via OAuth2 introspection and maps response to TokenInfo.
   *
   * @param token the JWT token
   * @return Optional containing TokenInfo if active, empty otherwise
   */
  private Optional<TokenInfo> validateTokenViaIntrospection(final String token) {
    try {
      OAuth2Client.TokenIntrospectionResponse response = oauth2Client.introspectToken(token).join();

      if (response == null || !Boolean.TRUE.equals(response.getActive())) {
        LOGGER.debug("Token introspection returned inactive or null response");
        return Optional.empty();
      }

      LOGGER.debug("Token validated via introspection for subject: {}", response.getSub());

      // Map introspection response to TokenInfo
      return Optional.of(
          TokenInfo.builder()
              .subject(response.getSub())
              .userId(response.getUserId())
              .clientId(response.getClientId())
              .scopes(response.getScopes())
              .tokenType("access_token")
              .build());
    } catch (Exception e) {
      LOGGER.warn("Token introspection failed: {}", e.getMessage());
      return Optional.empty();
    }
  }

  /**
   * Validates token locally using JWT signature and expiration.
   *
   * @param token the JWT token
   * @return true if valid locally, false otherwise
   */
  public boolean isLocalTokenValid(final String token) {
    try {
      return !isTokenExpired(token) && isOAuth2TokenType(token);
    } catch (Exception e) {
      LOGGER.debug("Local token validation failed: {}", e.getMessage());
      return false;
    }
  }

  /**
   * Validates token via OAuth2 introspection endpoint.
   *
   * @param token the JWT token
   * @return true if valid according to introspection, false otherwise
   */
  public boolean isTokenValidViaIntrospection(final String token) {
    try {
      OAuth2Client.TokenIntrospectionResponse response = oauth2Client.introspectToken(token).join();
      return response != null && Boolean.TRUE.equals(response.getActive());
    } catch (Exception e) {
      LOGGER.warn("Token introspection validation failed: {}", e.getMessage());
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

      if (rolesObj instanceof String string) {
        return new String[] {string};
      } else if (rolesObj instanceof List<?> list) {
        @SuppressWarnings("unchecked")
        List<String> rolesList = (List<String>) list;
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

  /**
   * Extracts client ID from OAuth2 JWT token claims.
   *
   * @param token The JWT token
   * @return The client ID or null if not found
   */
  public String extractClientId(final String token) {
    try {
      Claims claims = extractAllClaims(token);
      return claims.get("client_id", String.class);
    } catch (Exception e) {
      LOGGER.warn("Failed to extract client ID from token: {}", e.getMessage());
      return null;
    }
  }

  /**
   * Extracts scopes from OAuth2 JWT token claims.
   *
   * @param token The JWT token
   * @return Array of scopes or empty array if not found
   */
  public String[] extractScopes(final String token) {
    try {
      Claims claims = extractAllClaims(token);
      Object scopesObj = claims.get("scopes");

      if (scopesObj instanceof String scopeString) {
        // Handle space-delimited scope string
        return scopeString.trim().isEmpty() ? new String[0] : scopeString.split("\\s+");
      } else if (scopesObj instanceof List) {
        @SuppressWarnings("unchecked")
        List<String> scopesList = (List<String>) scopesObj;
        return scopesList.toArray(new String[0]);
      }

      return new String[0];
    } catch (Exception e) {
      LOGGER.warn("Failed to extract scopes from token: {}", e.getMessage());
      return new String[0];
    }
  }

  /**
   * Extracts the token type from OAuth2 JWT token claims.
   *
   * @param token The JWT token
   * @return The token type or null if not found
   */
  public String extractTokenType(final String token) {
    try {
      Claims claims = extractAllClaims(token);
      return claims.get("type", String.class);
    } catch (Exception e) {
      LOGGER.warn("Failed to extract token type from token: {}", e.getMessage());
      return null;
    }
  }

  /**
   * Checks if the token is a valid OAuth2 access token.
   *
   * @param token The JWT token
   * @return true if it's an OAuth2 access token, false otherwise
   */
  public boolean isOAuth2TokenType(final String token) {
    try {
      String tokenType = extractTokenType(token);
      return "access_token".equals(tokenType);
    } catch (Exception e) {
      LOGGER.debug("Token type check failed: {}", e.getMessage());
      // For backward compatibility, assume valid if type claim is missing
      return true;
    }
  }

  /**
   * Extracts the issuer from JWT token claims.
   *
   * @param token The JWT token
   * @return The issuer or null if not found
   */
  public String extractIssuer(final String token) {
    try {
      Claims claims = extractAllClaims(token);
      return claims.getIssuer();
    } catch (Exception e) {
      LOGGER.warn("Failed to extract issuer from token: {}", e.getMessage());
      return null;
    }
  }

  /**
   * Checks if the token has a specific scope.
   *
   * @param token The JWT token
   * @param scope The scope to check for
   * @return true if the token has the scope, false otherwise
   */
  public boolean hasScope(final String token, final String scope) {
    try {
      String[] scopes = extractScopes(token);
      for (String tokenScope : scopes) {
        if (scope.equals(tokenScope)) {
          return true;
        }
      }
      return false;
    } catch (Exception e) {
      LOGGER.warn("Failed to check scope '{}' in token: {}", scope, e.getMessage());
      return false;
    }
  }

  /**
   * Gets comprehensive token information including OAuth2-specific claims.
   *
   * @param token The JWT token
   * @return Optional containing token info, or empty if extraction fails
   */
  public Optional<TokenInfo> getTokenInfo(final String token) {
    try {
      Claims claims = extractAllClaims(token);
      TokenInfo tokenInfo =
          TokenInfo.builder()
              .subject(claims.getSubject())
              .userId(extractUserId(token))
              .clientId(extractClientId(token))
              .scopes(extractScopes(token))
              .roles(extractRoles(token))
              .tokenType(extractTokenType(token))
              .issuer(extractIssuer(token))
              .issuedAt(claims.getIssuedAt())
              .expiration(claims.getExpiration())
              .build();

      return Optional.of(tokenInfo);
    } catch (Exception e) {
      LOGGER.warn("Failed to extract comprehensive token info: {}", e.getMessage());
      return Optional.empty();
    }
  }

  /** Data class for comprehensive token information. */
  @Data
  @Builder
  @AllArgsConstructor
  public static class TokenInfo {
    /** The subject identifier from the token. */
    private final String subject;

    /** The user identifier from the token. */
    private final String userId;

    /** The client identifier from the token. */
    private final String clientId;

    /** Array of granted scopes. */
    private final String[] scopes;

    /** Array of user roles. */
    private final String[] roles;

    /** The type of token (e.g., "access_token"). */
    private final String tokenType;

    /** The token issuer. */
    private final String issuer;

    /** When the token was issued. */
    private final Date issuedAt;

    /** When the token expires. */
    private final Date expiration;
  }
}
