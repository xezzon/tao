package io.github.xezzon.tao.exception;

import io.github.xezzon.tao.web.Result;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author xezzon
 */
@RestControllerAdvice
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
  @ExceptionHandler(ClientException.class)
  @ResponseStatus(code = HttpStatus.BAD_REQUEST)
  public Result<Void> handleClientException(ClientException e) {
    return Result.fail(e);
  }

  /**
   * 预期的服务端异常
   */
  @ExceptionHandler(ServerException.class)
  @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
  public Result<Void> handleServerException(ServerException e) {
    return Result.fail(e, errorMessage);
  }

  /**
   * 第三方服务异常
   */
  @ExceptionHandler(ThirdPartyException.class)
  @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
  public Result<Void> handleThirdPartyException(ThirdPartyException e) {
    return Result.fail(e, errorMessage);
  }

  /**
   * 多消息异常
   */
  @ExceptionHandler(MultiException.class)
  @ResponseStatus(code = HttpStatus.BAD_REQUEST)
  @Deprecated
  public Result<?> handleMultiException(MultiException e) {
    return Result.fail(e.getCode(), errorMessage, e.getMessages());
  }
}
