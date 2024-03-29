package io.github.xezzon.tao.constant;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * 业务无关的全局静态变量
 * @author xezzon
 */
public final class GlobalConstants {

  /**
   * 最大可存储时间 此处取的是 MySQL 的最大存储时间
   */
  public static final Instant MAXIMUM_TIME = LocalDateTime.of(9999, 12, 31, 23, 59, 59, 999)
      .toInstant(ZoneOffset.UTC);

  private GlobalConstants() {
  }
}
