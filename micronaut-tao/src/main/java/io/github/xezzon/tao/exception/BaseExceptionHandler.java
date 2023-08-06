package io.github.xezzon.tao.exception;

import io.github.xezzon.tao.web.Result;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.Status;

/**
 * @author xezzon
 */
@Controller
public class BaseExceptionHandler {

  protected final transient String errorMessage;

  public BaseExceptionHandler() {
    this("");
  }

  protected BaseExceptionHandler(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  /**
   * 客户端异常
   */
  @Error(value = ClientException.class, global = true)
  @Status(HttpStatus.BAD_REQUEST)
  public Result<Void> handleClientException(ClientException e) {
    return Result.fail(e.getCode(), e.getMessage());
  }

  /**
   * 预期的服务端异常
   */
  @Error(value = ServerException.class, global = true)
  @Status(HttpStatus.INTERNAL_SERVER_ERROR)
  public Result<Void> handleServerException(ServerException e) {
    return Result.fail(e.getCode(), errorMessage);
  }

  /**
   * 第三方服务异常
   */
  @Error(value = ThirdPartyException.class, global = true)
  @Status(HttpStatus.INTERNAL_SERVER_ERROR)
  public Result<Void> handleThirdPartyException(ThirdPartyException e) {
    return Result.fail(e.getCode(), errorMessage);
  }

  /**
   * 多消息异常
   */
  @Error(MultiException.class)
  @Status(HttpStatus.BAD_REQUEST)
  public Result<?> handleMultiException(MultiException e) {
    return Result.fail(e.getCode(), errorMessage, e.getMessages());
  }
}
