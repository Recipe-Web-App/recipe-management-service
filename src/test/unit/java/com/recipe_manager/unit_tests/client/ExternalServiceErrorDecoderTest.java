package com.recipe_manager.unit_tests.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import com.recipe_manager.client.common.ExternalServiceErrorDecoder;
import com.recipe_manager.exception.ExternalServiceException;
import com.recipe_manager.exception.ExternalServiceTimeoutException;
import com.recipe_manager.model.enums.ExternalServiceName;

import feign.Request;
import feign.RequestTemplate;
import feign.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ExternalServiceErrorDecoderTest {

  private ExternalServiceErrorDecoder errorDecoder;

  @BeforeEach
  void setUp() {
    errorDecoder = new ExternalServiceErrorDecoder();
  }

  @Test
  @DisplayName("Should decode 408 timeout error")
  void shouldDecode408TimeoutError() {
    Response response = createMockResponse(408, "Request timeout");

    Exception exception = errorDecoder.decode("RecipeScraperClient#getShoppingInfo", response);

    assertThat(exception).isInstanceOf(ExternalServiceTimeoutException.class);
    ExternalServiceTimeoutException timeoutException = (ExternalServiceTimeoutException) exception;
    assertThat(timeoutException.getServiceName()).isEqualTo(ExternalServiceName.RECIPE_SCRAPER);
    assertThat(timeoutException.getTimeoutDuration()).isEqualTo(5000L);
  }

  @Test
  @DisplayName("Should decode 504 gateway timeout error")
  void shouldDecode504GatewayTimeoutError() {
    Response response = createMockResponse(504, "Gateway timeout");

    Exception exception = errorDecoder.decode("RecipeScraperClient#getShoppingInfo", response);

    assertThat(exception).isInstanceOf(ExternalServiceTimeoutException.class);
    ExternalServiceTimeoutException timeoutException = (ExternalServiceTimeoutException) exception;
    assertThat(timeoutException.getServiceName()).isEqualTo(ExternalServiceName.RECIPE_SCRAPER);
  }

  @Test
  @DisplayName("Should decode 429 rate limit error")
  void shouldDecode429RateLimitError() {
    Response response = createMockResponse(429, "Too many requests");

    Exception exception = errorDecoder.decode("RecipeScraperClient#getShoppingInfo", response);

    assertThat(exception).isInstanceOf(ExternalServiceException.class);
    ExternalServiceException serviceException = (ExternalServiceException) exception;
    assertThat(serviceException.getServiceName()).isEqualTo(ExternalServiceName.RECIPE_SCRAPER);
    assertThat(serviceException.getStatusCode()).isEqualTo(429);
    assertThat(serviceException.getMessage()).contains("Rate limit exceeded");
  }

  @Test
  @DisplayName("Should decode 500 server error")
  void shouldDecode500ServerError() {
    Response response = createMockResponse(500, "Internal server error");

    Exception exception = errorDecoder.decode("RecipeScraperClient#getShoppingInfo", response);

    assertThat(exception).isInstanceOf(ExternalServiceException.class);
    ExternalServiceException serviceException = (ExternalServiceException) exception;
    assertThat(serviceException.getServiceName()).isEqualTo(ExternalServiceName.RECIPE_SCRAPER);
    assertThat(serviceException.getStatusCode()).isEqualTo(500);
    assertThat(serviceException.getMessage()).contains("Server error");
    assertThat(serviceException.getMessage()).contains("Internal server error");
  }

  @Test
  @DisplayName("Should decode 502 bad gateway error")
  void shouldDecode502BadGatewayError() {
    Response response = createMockResponse(502, "Bad gateway");

    Exception exception = errorDecoder.decode("RecipeScraperClient#getShoppingInfo", response);

    assertThat(exception).isInstanceOf(ExternalServiceException.class);
    ExternalServiceException serviceException = (ExternalServiceException) exception;
    assertThat(serviceException.getStatusCode()).isEqualTo(502);
  }

  @Test
  @DisplayName("Should decode 503 service unavailable error")
  void shouldDecode503ServiceUnavailableError() {
    Response response = createMockResponse(503, "Service unavailable");

    Exception exception = errorDecoder.decode("RecipeScraperClient#getShoppingInfo", response);

    assertThat(exception).isInstanceOf(ExternalServiceException.class);
    ExternalServiceException serviceException = (ExternalServiceException) exception;
    assertThat(serviceException.getStatusCode()).isEqualTo(503);
  }

  @Test
  @DisplayName("Should decode 400 bad request error")
  void shouldDecode400BadRequestError() {
    Response response = createMockResponse(400, "Bad request");

    Exception exception = errorDecoder.decode("RecipeScraperClient#getShoppingInfo", response);

    assertThat(exception).isInstanceOf(ExternalServiceException.class);
    ExternalServiceException serviceException = (ExternalServiceException) exception;
    assertThat(serviceException.getStatusCode()).isEqualTo(400);
    assertThat(serviceException.getMessage()).contains("Client error");
    assertThat(serviceException.getMessage()).contains("Bad request");
  }

  @Test
  @DisplayName("Should decode 404 not found error")
  void shouldDecode404NotFoundError() {
    Response response = createMockResponse(404, "Not found");

    Exception exception = errorDecoder.decode("RecipeScraperClient#getShoppingInfo", response);

    assertThat(exception).isInstanceOf(ExternalServiceException.class);
    ExternalServiceException serviceException = (ExternalServiceException) exception;
    assertThat(serviceException.getStatusCode()).isEqualTo(404);
  }

  @Test
  @DisplayName("Should decode 422 unprocessable entity error")
  void shouldDecode422UnprocessableEntityError() {
    Response response = createMockResponse(422, "Unprocessable entity");

    Exception exception = errorDecoder.decode("RecipeScraperClient#getShoppingInfo", response);

    assertThat(exception).isInstanceOf(ExternalServiceException.class);
    ExternalServiceException serviceException = (ExternalServiceException) exception;
    assertThat(serviceException.getStatusCode()).isEqualTo(422);
  }

  @Test
  @DisplayName("Should decode unexpected status code")
  void shouldDecodeUnexpectedStatusCode() {
    Response response = createMockResponse(418, "I'm a teapot");

    Exception exception = errorDecoder.decode("RecipeScraperClient#getShoppingInfo", response);

    assertThat(exception).isInstanceOf(ExternalServiceException.class);
    ExternalServiceException serviceException = (ExternalServiceException) exception;
    assertThat(serviceException.getStatusCode()).isEqualTo(418);
    assertThat(serviceException.getMessage()).contains("Unexpected error");
    assertThat(serviceException.getMessage()).contains("I'm a teapot");
  }

  @Test
  @DisplayName("Should handle null response body")
  void shouldHandleNullResponseBody() {
    Response response = createMockResponseWithNullBody(500);

    Exception exception = errorDecoder.decode("RecipeScraperClient#getShoppingInfo", response);

    assertThat(exception).isInstanceOf(ExternalServiceException.class);
    ExternalServiceException serviceException = (ExternalServiceException) exception;
    assertThat(serviceException.getMessage()).contains("Server error");
  }

  @Test
  @DisplayName("Should handle IOException when reading response body")
  void shouldHandleIOExceptionWhenReadingResponseBody() throws IOException {
    Response response = mock(Response.class);
    Response.Body mockBody = mock(Response.Body.class);

    when(response.status()).thenReturn(500);
    when(response.body()).thenReturn(mockBody);
    when(mockBody.asInputStream()).thenThrow(new IOException("Stream error"));

    Exception exception = errorDecoder.decode("RecipeScraperClient#getShoppingInfo", response);

    assertThat(exception).isInstanceOf(ExternalServiceException.class);
    ExternalServiceException serviceException = (ExternalServiceException) exception;
    assertThat(serviceException.getMessage()).contains("Server error");
  }

  @Test
  @DisplayName("Should extract service name from method key")
  void shouldExtractServiceNameFromMethodKey() {
    Response response = createMockResponse(400, "Error");

    Exception exception1 = errorDecoder.decode("RecipeScraperClient#getShoppingInfo", response);
    Exception exception2 = errorDecoder.decode("OtherServiceClient#someMethod", response);

    assertThat(((ExternalServiceException) exception1).getServiceName())
        .isEqualTo(ExternalServiceName.RECIPE_SCRAPER);
    assertThat(((ExternalServiceException) exception2).getServiceName())
        .isEqualTo(ExternalServiceName.RECIPE_SCRAPER); // Falls back to default
  }

  private Response createMockResponse(int status, String body) {
    Request request = Request.create(Request.HttpMethod.GET, "/test", Collections.emptyMap(),
        null, new RequestTemplate());

    return Response.builder()
        .status(status)
        .reason("HTTP " + status)
        .headers(Collections.emptyMap())
        .body(body, StandardCharsets.UTF_8)
        .request(request)
        .build();
  }

  private Response createMockResponseWithNullBody(int status) {
    Request request = Request.create(Request.HttpMethod.GET, "/test", Collections.emptyMap(),
        null, new RequestTemplate());

    return Response.builder()
        .status(status)
        .reason("HTTP " + status)
        .headers(Collections.emptyMap())
        .request(request)
        .build();
  }
}
