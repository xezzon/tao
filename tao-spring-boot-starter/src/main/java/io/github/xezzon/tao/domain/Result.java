package io.github.xezzon.tao.domain;

import static io.github.xezzon.tao.constant.KeyConstants.TRACE_ID;

import org.slf4j.MDC;

public class Result<T> {

  public static final String SUCCESS_CODE = "00000";
  public static final String SUCCESS_MESSAGE = "操作成功";
  /**
   * 状态码
   */
  private String code;
  /**
   * 提示消息
   */
  private String message;
  /**
   * 负载数据
   */
  private T data;
  /**
   * 请求追踪号
   */
  private String requestId;

  public Result() {
    super();
  }

  private Result(String code, String message, T data, String requestId) {
    this.code = code;
    this.message = message;
    this.data = data;
    this.requestId = requestId;
  }

  public static <T> Result<T> succeed() {
    return new Result<>(SUCCESS_CODE, SUCCESS_MESSAGE, null, null);
  }

  public static <T> Result<T> succeed(T data) {
    return new Result<>(SUCCESS_CODE, null, data, null);
  }

  public static <T> Result<T> fail(String code, String message) {
    return new Result<>(code, message, null, MDC.get(TRACE_ID));
  }

  public static <T> Result<T> fail(String code, String message, T data) {
    return new Result<>(code, message, data, MDC.get(TRACE_ID));
  }

  public String getCode() {
    return code;
  }

  public String getMessage() {
    return message;
  }

  public T getData() {
    return data;
  }

  public String getRequestId() {
    return requestId;
  }
}
