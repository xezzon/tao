package indi.xezzon.tao.exception;

import java.io.Serial;

/**
 * 业务异常基础类
 * @author xezzon
 */
public class BaseException extends RuntimeException {
  @Serial
  private static final long serialVersionUID = -1L;

  /**
   * 错误码
   */
  private final String code;
  /**
   * 提示信息
   */
  private final String tip;

  public BaseException() {
    super();
    this.code = null;
    this.tip = null;
  }

  public BaseException(String message) {
    super(message);
    this.code = null;
    this.tip = null;
  }

  public BaseException(Throwable cause) {
    super(cause);
    this.code = null;
    this.tip = null;
  }

  public BaseException(String message, Throwable cause) {
    super(message, cause);
    this.code = null;
    this.tip = null;
  }

  public BaseException(String code, String message, String tip, Throwable cause) {
    super(message, cause);
    this.code = code;
    this.tip = tip;
  }

  public String getCode() {
    return this.code;
  }

  public String getTip() {
    return this.tip;
  }
}
