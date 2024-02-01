package io.github.xezzon.tao.trait;

import java.util.Objects;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

/**
 * 可恢复异常
 * 一定要由调用方处理，禁止向上传递
 * @author xezzon
 */
public class Result<T, E extends Throwable> {

  private T value;
  private E exception;

  private Result() {
    super();
  }

  private Result(T value, E exception) {
    this.value = value;
    this.exception = exception;
  }

  public static <T, E extends Throwable> Result<T, E> ok(T value) {
    return new Result<>(value, null);
  }

  public static <T, E extends Throwable> Result<T, E> err(@NotNull E error) {
    Objects.requireNonNull(error);
    return new Result<>(null, error);
  }

  public boolean isOk() {
    return this.exception == null;
  }

  public boolean isErr() {
    return !isOk();
  }

  public T unwrap() throws E {
    if (isErr()) {
      throw exception;
    }
    return value;
  }

  public Optional<E> leak() {
    return Optional.ofNullable(exception);
  }
}
