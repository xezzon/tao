package io.github.xezzon.tao.exception;

import io.github.xezzon.tao.web.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author xezzon
 */
@RestControllerAdvice
public class BaseExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(BaseExceptionHandler.class);

  private static final String ERROR_MESSAGE = "无法响应您的操作，请稍后重试或联系客服。";

  /**
   * 客户端异常
   */
  @ExceptionHandler(ClientException.class)
  @ResponseStatus(code = HttpStatus.BAD_REQUEST)
  public Result<Void> handleClientException(ClientException e) {
    return Result.fail(e.getCode(), e.getMessage());
  }

  /**
   * 预期的服务端异常
   */
  @ExceptionHandler(ServerException.class)
  @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
  public Result<Void> handleServerException(ServerException e) {
    return Result.fail(e.getCode(), ERROR_MESSAGE);
  }

  /**
   * 第三方服务异常
   */
  @ExceptionHandler(ThirdPartyException.class)
  @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
  public Result<Void> handleThirdPartyException(ThirdPartyException e) {
    return Result.fail(e.getCode(), ERROR_MESSAGE);
  }
}
