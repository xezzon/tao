package io.github.xezzon.tao.exception;

import java.io.Serial;

/**
 * 第三方服务异常
 * @author xezzon 
 */
public class ThirdPartyException extends BaseException {

  public static final String THIRD_PARTY_ERROR_CODE = "C0001";
  @Serial
  private static final long serialVersionUID = -6690044716770522289L;

  public ThirdPartyException(String message) {
    super(THIRD_PARTY_ERROR_CODE, message);
  }

  public ThirdPartyException(String message, Throwable cause) {
    super(THIRD_PARTY_ERROR_CODE, message, cause);
  }

  protected ThirdPartyException(String code, String message) {
    super(code, message);
  }

  protected ThirdPartyException(String code, String message, Throwable cause) {
    super(code, message, cause);
  }
}
