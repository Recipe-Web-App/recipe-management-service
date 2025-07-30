package com.recipe_manager.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.recipe_manager.model.dto.response.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

/**
 * Global exception handler that provides centralized error handling for the application.
 *
 * <p>This handler:
 *
 * <ul>
 *   <li>Catches and handles all unhandled exceptions
 *   <li>Provides structured error responses
 *   <li>Logs errors with appropriate levels
 *   <li>Includes request ID in error responses
 *   <li>Handles validation errors
 *   <li>Provides security-aware error messages
 * </ul>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  /** Logger for this class. */
  private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  /**
   * Handles validation errors from @Valid annotations.
   *
   * @param ex The validation exception
   * @param request The HTTP request
   * @return Error response with validation details
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationExceptions(
      final MethodArgumentNotValidException ex, final HttpServletRequest request) {

    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult()
        .getAllErrors()
        .forEach(
            error -> {
              String fieldName = ((FieldError) error).getField();
              String errorMessage = error.getDefaultMessage();
              errors.put(fieldName, errorMessage);
            });

    ErrorResponse errorResponse =
        createErrorResponse(
            "Validation failed", "One or more fields failed validation", errors, request);

    LOGGER.warn("Validation error: {}", errorResponse);
    return ResponseEntity.badRequest().body(errorResponse);
  }

  /**
   * Handles constraint violation exceptions.
   *
   * @param ex The constraint violation exception
   * @param request The HTTP request
   * @return Error response
   */
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolation(
      final ConstraintViolationException ex, final HttpServletRequest request) {

    ErrorResponse errorResponse =
        createErrorResponse("Constraint violation", ex.getMessage(), null, request);

    LOGGER.warn("Constraint violation: {}", errorResponse);
    return ResponseEntity.badRequest().body(errorResponse);
  }

  /**
   * Handles authentication exceptions.
   *
   * @param ex The authentication exception
   * @param request The HTTP request
   * @return Error response
   */
  @ExceptionHandler({AuthenticationException.class, BadCredentialsException.class})
  public ResponseEntity<ErrorResponse> handleAuthenticationException(
      final Exception ex, final HttpServletRequest request) {

    ErrorResponse errorResponse =
        createErrorResponse("Authentication failed", "Invalid credentials", null, request);

    LOGGER.warn("Authentication error: {}", errorResponse);
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
  }

  /**
   * Handles access denied exceptions.
   *
   * @param ex The access denied exception
   * @param request The HTTP request
   * @return Error response
   */
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAccessDeniedException(
      final AccessDeniedException ex, final HttpServletRequest request) {

    ErrorResponse errorResponse =
        createErrorResponse(
            "Access denied", "You don't have permission to access this resource", null, request);

    LOGGER.warn("Access denied: {}", errorResponse);
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
  }

  /**
   * Handles resource not found exceptions.
   *
   * @param ex The resource not found exception
   * @param request The HTTP request
   * @return Error response
   */
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
      final ResourceNotFoundException ex, final HttpServletRequest request) {

    ErrorResponse errorResponse =
        createErrorResponse("Resource not found", ex.getMessage(), null, request);

    LOGGER.info("Resource not found: {}", errorResponse);
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  /**
   * Handles business logic exceptions.
   *
   * @param ex The business exception
   * @param request The HTTP request
   * @return Error response
   */
  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ErrorResponse> handleBusinessException(
      final BusinessException ex, final HttpServletRequest request) {

    ErrorResponse errorResponse =
        createErrorResponse("Business error", ex.getMessage(), null, request);

    LOGGER.warn("Business error: {}", errorResponse);
    return ResponseEntity.badRequest().body(errorResponse);
  }

  /**
   * Handles HTTP message not readable exceptions (malformed JSON).
   *
   * @param ex The HTTP message not readable exception
   * @param request The HTTP request
   * @return Error response
   */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
      final HttpMessageNotReadableException ex, final HttpServletRequest request) {

    ErrorResponse errorResponse =
        createErrorResponse(
            "Invalid request body", "The request body could not be parsed", null, request);

    LOGGER.warn("Invalid request body: {}", errorResponse);
    return ResponseEntity.badRequest().body(errorResponse);
  }

  /**
   * Handles method argument type mismatch exceptions.
   *
   * @param ex The method argument type mismatch exception
   * @param request The HTTP request
   * @return Error response
   */
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(
      final MethodArgumentTypeMismatchException ex, final HttpServletRequest request) {

    ErrorResponse errorResponse =
        createErrorResponse(
            "Invalid parameter",
            "Parameter '" + ex.getName() + "' has invalid value: " + ex.getValue(),
            null,
            request);

    LOGGER.warn("Parameter type mismatch: {}", errorResponse);
    return ResponseEntity.badRequest().body(errorResponse);
  }

  /**
   * Handles missing request parameter exceptions.
   *
   * @param ex The missing request parameter exception
   * @param request The HTTP request
   * @return Error response
   */
  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ErrorResponse> handleMissingServletRequestParameter(
      final MissingServletRequestParameterException ex, final HttpServletRequest request) {

    ErrorResponse errorResponse =
        createErrorResponse(
            "Missing parameter",
            "Required parameter '" + ex.getParameterName() + "' is missing",
            null,
            request);

    LOGGER.warn("Missing parameter: {}", errorResponse);
    return ResponseEntity.badRequest().body(errorResponse);
  }

  /**
   * Handles HTTP request method not supported exceptions.
   *
   * @param ex The HTTP request method not supported exception
   * @param request The HTTP request
   * @return Error response
   */
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupported(
      final HttpRequestMethodNotSupportedException ex, final HttpServletRequest request) {

    ErrorResponse errorResponse =
        createErrorResponse(
            "Method not allowed",
            "HTTP method '" + ex.getMethod() + "' is not supported for this endpoint",
            null,
            request);

    LOGGER.warn("Method not allowed: {}", errorResponse);
    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorResponse);
  }

  /**
   * Handles no handler found exceptions (404 errors).
   *
   * @param ex The no handler found exception
   * @param request The HTTP request
   * @return Error response
   */
  @ExceptionHandler(NoHandlerFoundException.class)
  public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(
      final NoHandlerFoundException ex, final HttpServletRequest request) {

    ErrorResponse errorResponse =
        createErrorResponse(
            "Endpoint not found",
            "The requested endpoint '" + ex.getRequestURL() + "' was not found",
            null,
            request);

    LOGGER.info("Endpoint not found: {}", errorResponse);
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  /**
   * Handles data integrity violation exceptions.
   *
   * @param ex The data integrity violation exception
   * @param request The HTTP request
   * @return Error response
   */
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
      final DataIntegrityViolationException ex, final HttpServletRequest request) {

    ErrorResponse errorResponse =
        createErrorResponse(
            "Data integrity error",
            "The operation could not be completed due to data constraints",
            null,
            request);

    LOGGER.error("Data integrity violation: {}", errorResponse, ex);
    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
  }

  /**
   * Handles database access exceptions.
   *
   * @param ex The database access exception
   * @param request The HTTP request
   * @return Error response
   */
  @ExceptionHandler(InvalidDataAccessResourceUsageException.class)
  public ResponseEntity<ErrorResponse> handleDatabaseException(
      final InvalidDataAccessResourceUsageException ex, final HttpServletRequest request) {

    ErrorResponse errorResponse =
        createErrorResponse(
            500,
            "Database error",
            "A database error occurred while processing your request",
            null,
            request);

    LOGGER.error("Database error: {}", errorResponse, ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }

  /**
   * Handles all other unhandled exceptions.
   *
   * @param ex The unhandled exception
   * @param request The HTTP request
   * @return Error response
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(
      final Exception ex, final HttpServletRequest request) {

    ErrorResponse errorResponse =
        createErrorResponse("Internal server error", "An unexpected error occurred", null, request);

    LOGGER.error("Unhandled exception: {}", errorResponse, ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }

  /**
   * Creates a standardized error response with request tracking information.
   *
   * @param status The HTTP status code
   * @param title The error title
   * @param message The error message
   * @param details Additional error details
   * @param request The HTTP request
   * @return ErrorResponse object
   */
  private ErrorResponse createErrorResponse(
      final int status,
      final String title,
      final String message,
      final Map<String, String> details,
      final HttpServletRequest request) {

    String requestId = request.getHeader("X-Request-ID");
    if (requestId == null) {
      requestId = UUID.randomUUID().toString();
    }

    return ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(status)
        .error(title)
        .message(message)
        .path(request.getRequestURI())
        .requestId(requestId)
        .details(details != null ? Map.copyOf(details) : null)
        .build();
  }

  /**
   * Creates a standardized error response with request tracking information (legacy method).
   *
   * @param title The error title
   * @param message The error message
   * @param details Additional error details
   * @param request The HTTP request
   * @return ErrorResponse object
   */
  private ErrorResponse createErrorResponse(
      final String title,
      final String message,
      final Map<String, String> details,
      final HttpServletRequest request) {
    return createErrorResponse(HttpStatus.BAD_REQUEST.value(), title, message, details, request);
  }
}
