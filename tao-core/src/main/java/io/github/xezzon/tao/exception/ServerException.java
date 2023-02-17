package io.github.xezzon.tao.exception;

import java.io.Serial;

/**
 * 服务端异常
 * @author xezzon
 */
public class ServerException extends BaseException {

  public static final String SERVER_ERROR_CODE = "B0001";
  @Serial
  private static final long serialVersionUID = -1456247394077069945L;

  public ServerException(String message) {
    super(SERVER_ERROR_CODE, message);
  }

  public ServerException(String message, Throwable cause) {
    super(SERVER_ERROR_CODE, message, cause);
  }

  protected ServerException(String code, String message) {
    super(code, message);
  }

  protected ServerException(String code, String message, Throwable cause) {
    super(code, message, cause);
  }
}
