package io.github.xezzon.tao.web;

/**
 * @author xezzon
 */
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

  public Result() {
    super();
  }

  private Result(String code, String message, T data) {
    this.code = code;
    this.message = message;
    this.data = data;
  }

  public static <T> Result<T> succeed() {
    return new Result<>(SUCCESS_CODE, SUCCESS_MESSAGE, null);
  }

  public static <T> Result<T> succeed(T data) {
    return new Result<>(SUCCESS_CODE, null, data);
  }

  public static <T> Result<T> fail(String code, String message) {
    return new Result<>(code, message, null);
  }

  public static <T> Result<T> fail(String code, String message, T data) {
    return new Result<>(code, message, data);
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
}
