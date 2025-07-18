package com.recipe_manager.config;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filter that adds a unique request ID to each HTTP request for tracking purposes.
 *
 * <p>This filter:
 *
 * <ul>
 *   <li>Generates a unique request ID for each incoming request
 *   <li>Adds the request ID to the response headers
 *   <li>Sets up MDC context for structured logging
 *   <li>Extracts user and session information when available
 * </ul>
 */
public final class RequestIdFilter extends OncePerRequestFilter {

  /** Logger for this class. */
  private static final Logger LOGGER = LoggerFactory.getLogger(RequestIdFilter.class);

  /** Request ID header name. */
  private static final String REQUEST_ID_HEADER = "X-Request-ID";

  /** User ID header name. */
  private static final String USER_ID_HEADER = "X-User-ID";

  /** Session ID header name. */
  private static final String SESSION_ID_HEADER = "X-Session-ID";

  @Override
  protected void doFilterInternal(
      final HttpServletRequest request,
      final HttpServletResponse response,
      final FilterChain filterChain)
      throws ServletException, IOException {

    // Generate or extract request ID
    final String requestId = extractOrGenerateRequestId(request);

    // Extract user context if available
    final String userId = request.getHeader(USER_ID_HEADER);
    final String sessionId = request.getHeader(SESSION_ID_HEADER);

    // Set up MDC context for logging
    LoggingConfig.setupMdcContext(requestId, userId, sessionId);

    // Add request ID to response headers
    response.addHeader(REQUEST_ID_HEADER, requestId);

    try {
      LOGGER.debug(
          "Processing request: {} {} with requestId: {}",
          request.getMethod(),
          request.getRequestURI(),
          requestId);

      filterChain.doFilter(request, response);

      LOGGER.debug(
          "Completed request: {} {} with status: {} and requestId: {}",
          request.getMethod(),
          request.getRequestURI(),
          response.getStatus(),
          requestId);

    } finally {
      // Clean up MDC context
      LoggingConfig.clearMdcContext();
    }
  }

  /**
   * Extracts request ID from header or generates a new one if not present.
   *
   * @param request The HTTP request
   * @return The request ID to use for this request
   */
  private String extractOrGenerateRequestId(final HttpServletRequest request) {
    final String requestId = request.getHeader(REQUEST_ID_HEADER);
    if (requestId == null || requestId.trim().isEmpty()) {
      return UUID.randomUUID().toString();
    }
    return requestId;
  }

  @Override
  protected boolean shouldNotFilter(final HttpServletRequest request) {
    // Skip filtering for actuator endpoints to avoid noise in logs
    final String path = request.getRequestURI();
    return path.startsWith("/actuator") || path.startsWith("/health");
  }
}
