package io.github.xezzon.tao.exception;

/**
 * 业务异常基础类
 * @author xezzon
 */
public abstract class BaseException extends RuntimeException {

  @java.io.Serial
  private static final long serialVersionUID = 8565056568458668598L;
  /**
   * 错误码
   */
  private final String code;

  protected BaseException(String code, String message) {
    super(message);
    this.code = code;
  }

  protected BaseException(String code, String message, Throwable cause) {
    super(message, cause);
    this.code = code;
  }

  public String getCode() {
    return this.code;
  }
}
