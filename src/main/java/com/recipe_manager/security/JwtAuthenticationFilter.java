package com.recipe_manager.security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * JWT Authentication Filter that verifies tokens from the user-management-service.
 *
 * <p>This filter:
 *
 * <ul>
 *   <li>Extracts JWT tokens from Authorization header
 *   <li>Validates tokens using the configured JWT service
 *   <li>Sets up Spring Security context with user details
 *   <li>Handles token refresh and validation
 * </ul>
 */
@Component
public final class JwtAuthenticationFilter extends OncePerRequestFilter {

  /** Logger for this class. */
  private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

  /** Authorization header name. */
  private static final String AUTHORIZATION_HEADER = "Authorization";

  /** Bearer prefix. */
  private static final String BEARER_PREFIX = "Bearer ";

  /** JWT service for token operations. */
  private final JwtService jwtService;

  /** UserDetailsService for loading user details. */
  private final UserDetailsService userDetailsService;

  /**
   * Constructs a new JwtAuthenticationFilter.
   *
   * @param jwtService the JWT service
   * @param userDetailsService the user details service
   */
  public JwtAuthenticationFilter(
      final JwtService jwtService, final UserDetailsService userDetailsService) {
    this.jwtService = jwtService;
    this.userDetailsService = userDetailsService;
  }

  /**
   * Performs JWT authentication filtering.
   *
   * @param request the HTTP request
   * @param response the HTTP response
   * @param filterChain the filter chain
   * @throws ServletException if a servlet error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doFilterInternal(
      final HttpServletRequest request,
      final HttpServletResponse response,
      final FilterChain filterChain)
      throws ServletException, IOException {
    try {
      String token = extractTokenFromRequest(request);
      if (StringUtils.hasText(token) && jwtService.isTokenValid(token)) {
        String username = jwtService.extractUsername(token);
        if (StringUtils.hasText(username)
            && SecurityContextHolder.getContext().getAuthentication() == null) {
          UserDetails userDetails = userDetailsService.loadUserByUsername(username);
          if (userDetails != null) {
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // Log successful authentication
            LOGGER.debug(
                "User authenticated successfully: {} - RequestId: {}",
                username,
                extractRequestId(request));
          }
        }
      }
    } catch (final Exception e) {
      LOGGER.warn(
          "JWT authentication failed: {} - RequestId: {}",
          e.getMessage(),
          extractRequestId(request));
      // Clear any existing authentication context
      SecurityContextHolder.clearContext();
    }
    filterChain.doFilter(request, response);
  }

  /**
   * Extracts JWT token from the Authorization header.
   *
   * @param request the HTTP request
   * @return the JWT token or null if not found
   */
  private String extractTokenFromRequest(final HttpServletRequest request) {
    String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
      return bearerToken.substring(BEARER_PREFIX.length());
    }
    return null;
  }

  /**
   * Extracts request ID from headers for logging purposes.
   *
   * @param request the HTTP request
   * @return the request ID or "unknown" if not found
   */
  private String extractRequestId(final HttpServletRequest request) {
    String requestId = request.getHeader("X-Request-ID");
    return requestId != null ? requestId : "unknown";
  }

  /**
   * Determines if the filter should not be applied to the request.
   *
   * @param request the HTTP request
   * @return true if the filter should not be applied, false otherwise
   */
  @Override
  protected boolean shouldNotFilter(final HttpServletRequest request) {
    String path = request.getRequestURI();
    // Skip filtering for public endpoints
    return path.startsWith("/actuator")
        || path.startsWith("/health")
        || path.startsWith("/info")
        || path.startsWith("/swagger-ui")
        || path.startsWith("/v3/api-docs")
        || path.startsWith("/api/v1/auth");
  }
}
