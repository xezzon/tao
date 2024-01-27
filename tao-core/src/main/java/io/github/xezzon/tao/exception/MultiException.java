package io.github.xezzon.tao.exception;

import java.util.Collection;

/**
 * 多消息异常
 * @author xezzon
 */
@Deprecated
public class MultiException extends RuntimeException {

  @java.io.Serial
  private static final long serialVersionUID = -2709278534007150462L;
  private final String code;
  private final Collection<String> messages;

  public MultiException(Collection<String> messages, BaseException cause) {
    super(cause);
    this.code = cause.getCode();
    this.messages = messages;
  }

  public String getCode() {
    return this.code;
  }

  public Collection<String> getMessages() {
    return this.messages;
  }
}
