package com.recipe_manager.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;

import com.recipe_manager.model.dto.ErrorResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
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
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

/**
 * Test class for GlobalExceptionHandler.
 * Verifies that global exception handling works correctly.
 */
@Tag("unit")
class GlobalExceptionHandlerTest {

  private GlobalExceptionHandler globalExceptionHandler;
  private HttpServletRequest httpServletRequest;

  @BeforeEach
  void setUp() {
    globalExceptionHandler = new GlobalExceptionHandler();
    httpServletRequest = mock(HttpServletRequest.class);
  }

  /**
   * Test that global exception handler can be instantiated.
   */
  @Test
  @Tag("error-processing")
  @DisplayName("Should instantiate global exception handler")
  void shouldInstantiateGlobalExceptionHandler() {
    assertNotNull(globalExceptionHandler);
  }

  /**
   * Test handling of ResourceNotFoundException.
   */
  @Test
  @Tag("error-processing")
  @DisplayName("Should handle ResourceNotFoundException")
  void shouldHandleResourceNotFoundException() {
    // Given
    ResourceNotFoundException exception = new ResourceNotFoundException("Resource not found");

    // When
    ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleResourceNotFoundException(
        exception, httpServletRequest);

    // Then
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  /**
   * Test handling of BusinessException.
   */
  @Test
  @Tag("error-processing")
  @DisplayName("Should handle BusinessException")
  void shouldHandleBusinessException() {
    // Given
    BusinessException exception = new BusinessException("Business error occurred");

    // When
    ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleBusinessException(
        exception, httpServletRequest);

    // Then
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  /**
   * Test handling of generic Exception.
   */
  @Test
  @Tag("error-processing")
  @DisplayName("Should handle generic Exception")
  void shouldHandleGenericException() {
    // Given
    Exception exception = new Exception("Generic error occurred");

    // When
    ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGenericException(
        exception, httpServletRequest);

    // Then
    assertNotNull(response);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should handle MethodArgumentNotValidException")
  void shouldHandleMethodArgumentNotValidException() {
    MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
    FieldError fieldError = new FieldError("object", "field", "error");
    when(ex.getBindingResult())
        .thenReturn(new org.springframework.validation.BeanPropertyBindingResult(new Object(), "object") {
          {
            addError(fieldError);
          }
        });
    ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleValidationExceptions(ex, httpServletRequest);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should handle ConstraintViolationException")
  void shouldHandleConstraintViolationException() {
    ConstraintViolationException ex = new ConstraintViolationException("violation", Collections.emptySet());
    ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleConstraintViolation(ex, httpServletRequest);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should handle AuthenticationException")
  void shouldHandleAuthenticationException() {
    AuthenticationException ex = mock(AuthenticationException.class);
    ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleAuthenticationException(ex,
        httpServletRequest);
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should handle BadCredentialsException")
  void shouldHandleBadCredentialsException() {
    BadCredentialsException ex = new BadCredentialsException("bad creds");
    ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleAuthenticationException(ex,
        httpServletRequest);
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should handle AccessDeniedException")
  void shouldHandleAccessDeniedException() {
    AccessDeniedException ex = new AccessDeniedException("denied");
    ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleAccessDeniedException(ex, httpServletRequest);
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should handle HttpMessageNotReadableException")
  void shouldHandleHttpMessageNotReadableException() {
    HttpMessageNotReadableException ex = new HttpMessageNotReadableException("bad body", (Throwable) null, null);
    ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleHttpMessageNotReadable(ex,
        httpServletRequest);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should handle MethodArgumentTypeMismatchException")
  void shouldHandleMethodArgumentTypeMismatchException() {
    MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
    when(ex.getName()).thenReturn("param");
    when(ex.getValue()).thenReturn("bad");
    ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleMethodArgumentTypeMismatch(ex,
        httpServletRequest);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should handle MissingServletRequestParameterException")
  void shouldHandleMissingServletRequestParameterException() {
    MissingServletRequestParameterException ex = new MissingServletRequestParameterException("param", "String");
    ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleMissingServletRequestParameter(ex,
        httpServletRequest);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should handle HttpRequestMethodNotSupportedException")
  void shouldHandleHttpRequestMethodNotSupportedException() {
    HttpRequestMethodNotSupportedException ex = new HttpRequestMethodNotSupportedException("POST");
    ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleHttpRequestMethodNotSupported(ex,
        httpServletRequest);
    assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should handle NoHandlerFoundException")
  void shouldHandleNoHandlerFoundException() {
    NoHandlerFoundException ex = new NoHandlerFoundException("GET", "/path", null);
    ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleNoHandlerFoundException(ex,
        httpServletRequest);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should handle DataIntegrityViolationException")
  void shouldHandleDataIntegrityViolationException() {
    DataIntegrityViolationException ex = new DataIntegrityViolationException("violation");
    ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleDataIntegrityViolation(ex,
        httpServletRequest);
    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should cover private constructor")
  void shouldCoverPrivateConstructor() throws Exception {
    java.lang.reflect.Constructor<GlobalExceptionHandler> constructor = GlobalExceptionHandler.class
        .getDeclaredConstructor();
    constructor.setAccessible(true);
    constructor.newInstance();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should create error response with null details")
  void shouldCreateErrorResponseWithNullDetails() throws Exception {
    /*
     * Use reflection to access the private createErrorResponse method
     * and test the null details branch
     */
    java.lang.reflect.Method method = GlobalExceptionHandler.class.getDeclaredMethod(
        "createErrorResponse", String.class, String.class, java.util.Map.class,
        jakarta.servlet.http.HttpServletRequest.class);
    method.setAccessible(true);

    ErrorResponse response = (ErrorResponse) method.invoke(globalExceptionHandler,
        "Test Error", "Test Message", null, httpServletRequest);

    assertNotNull(response);
    assertEquals("Test Error", response.getError());
    assertEquals("Test Message", response.getMessage());
    assertNull(response.getDetails());
  }
}
