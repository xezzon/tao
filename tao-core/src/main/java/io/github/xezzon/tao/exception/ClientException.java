package io.github.xezzon.tao.exception;

import java.io.Serial;

/**
 * 客户端异常
 * @author xezzon
 */
public class ClientException extends BaseException {

  public static final String CLIENT_ERROR_CODE = "A0001";
  @Serial
  private static final long serialVersionUID = 1881092138741933130L;

  public ClientException(String message) {
    super(CLIENT_ERROR_CODE, message);
  }

  public ClientException(String message, Throwable cause) {
    super(CLIENT_ERROR_CODE, message, cause);
  }

  protected ClientException(String code, String message) {
    super(code, message);
  }

  protected ClientException(String code, String message, Throwable cause) {
    super(code, message, cause);
  }
}
