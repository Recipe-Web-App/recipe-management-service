package com.recipe_manager.security;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Development authentication filter that allows bypassing OAuth2 authentication.
 *
 * <p>This filter is ONLY active when OAuth2 is disabled (OAUTH2_SERVICE_ENABLED=false). It reads
 * the X-User-Id header and creates an authentication context for local development.
 *
 * <p><b>WARNING:</b> This filter should NEVER be enabled in production environments.
 */
@Component
@ConditionalOnProperty(name = "external.services.oauth2-service.enabled", havingValue = "false")
public final class DevAuthenticationFilter extends OncePerRequestFilter {

  /** Logger for this class. */
  private static final Logger LOGGER = LoggerFactory.getLogger(DevAuthenticationFilter.class);

  /** Header name for user ID. */
  private static final String USER_ID_HEADER = "X-User-Id";

  /** Logs a warning at startup indicating dev mode is active. */
  @PostConstruct
  public void logDevModeWarning() {
    LOGGER.warn("╔════════════════════════════════════════════════════════════════════╗");
    LOGGER.warn("║  DEV AUTHENTICATION FILTER IS ACTIVE                               ║");
    LOGGER.warn("║  OAuth2 is disabled - using X-User-Id header for authentication    ║");
    LOGGER.warn("║  DO NOT USE IN PRODUCTION                                          ║");
    LOGGER.warn("╚════════════════════════════════════════════════════════════════════╝");
  }

  /**
   * Performs development authentication filtering.
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

    // Only process if no authentication already exists
    if (SecurityContextHolder.getContext().getAuthentication() == null) {
      String userIdHeader = request.getHeader(USER_ID_HEADER);

      if (StringUtils.hasText(userIdHeader)) {
        try {
          // Validate UUID format to prevent injection
          UUID userId = UUID.fromString(userIdHeader);

          // Create authentication with ROLE_USER
          UsernamePasswordAuthenticationToken authentication =
              new UsernamePasswordAuthenticationToken(
                  userId.toString(),
                  null,
                  Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

          SecurityContextHolder.getContext().setAuthentication(authentication);

          LOGGER.debug(
              "Dev auth: User {} authenticated via X-User-Id header - RequestId: {}",
              userId,
              extractRequestId(request));

        } catch (IllegalArgumentException e) {
          LOGGER.warn(
              "Dev auth: Invalid UUID in X-User-Id header: {} - RequestId: {}",
              userIdHeader,
              extractRequestId(request));
        }
      }
    }

    filterChain.doFilter(request, response);
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
    // Skip filtering for public endpoints (same as JwtAuthenticationFilter)
    return path.startsWith("/actuator")
        || path.startsWith("/health")
        || path.startsWith("/info")
        || path.startsWith("/swagger-ui")
        || path.startsWith("/v3/api-docs");
  }
}
