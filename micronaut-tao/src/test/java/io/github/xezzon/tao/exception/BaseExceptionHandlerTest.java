package io.github.xezzon.tao.exception;

import static io.github.xezzon.tao.exception.BaseExceptionHandlerController.RANDOM_MESSAGE;

import cn.hutool.core.util.RandomUtil;
import io.github.xezzon.tao.web.Result;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@MicronautTest()
public class BaseExceptionHandlerTest {

  @Inject
  EmbeddedServer server;
  @Inject
  @Client("/exception")
  HttpClient client;

  @Test
  void handleClientException() {
    HttpClientResponseException e = Assertions.assertThrows(
        HttpClientResponseException.class,
        () -> client.toBlocking().exchange(
            HttpRequest.GET("/client"),
            Argument.of(Void.class),
            Argument.of(Result.class)
        )
    );
    HttpResponse<?> response = e.getResponse();
    Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
    Optional<Result> result = response.getBody(Result.class);
    Assertions.assertTrue(result.isPresent());
    Assertions.assertEquals(ClientException.CLIENT_ERROR_CODE, result.get().getCode());
    Assertions.assertEquals(RANDOM_MESSAGE, result.get().getMessage());
  }

  @Test
  void handleServerException() {
    HttpClientResponseException e = Assertions.assertThrows(
        HttpClientResponseException.class,
        () -> client.toBlocking().exchange(
            HttpRequest.GET("/server"),
            Argument.of(Void.class),
            Argument.of(Result.class)
        )
    );
    HttpResponse<?> response = e.getResponse();
    Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatus());
    Optional<Result> result = response.getBody(Result.class);
    Assertions.assertTrue(result.isPresent());
    Assertions.assertEquals(ServerException.SERVER_ERROR_CODE, result.get().getCode());
    Assertions.assertEquals("", result.get().getMessage());
  }

  @Test
  void handleThirdPartyException() {
    HttpClientResponseException e = Assertions.assertThrows(
        HttpClientResponseException.class,
        () -> client.toBlocking().exchange(
            HttpRequest.GET("/third-party"),
            Argument.of(Void.class),
            Argument.of(Result.class)
        )
    );
    HttpResponse<?> response = e.getResponse();
    Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatus());
    Optional<Result> result = response.getBody(Result.class);
    Assertions.assertTrue(result.isPresent());
    Assertions.assertEquals(ThirdPartyException.THIRD_PARTY_ERROR_CODE, result.get().getCode());
    Assertions.assertEquals("", result.get().getMessage());
  }

  @Test
  void handleMultiException() {
  }
}

@Controller("/exception")
class BaseExceptionHandlerController {

  static final String RANDOM_MESSAGE = RandomUtil.randomString(6);

  @Get("/client")
  public void clientException() {
    throw new ClientException(RANDOM_MESSAGE);
  }

  @Get("/server")
  public void serverException() {
    throw new ServerException(RANDOM_MESSAGE);
  }

  @Get("/third-party")
  public void thirdPartyException() {
    throw new ThirdPartyException(RANDOM_MESSAGE);
  }
}
