package indi.xezzon.tao.constant;

import java.time.LocalDateTime;

/**
 * 业务无关的全局静态变量
 */
public final class GlobalConstants {

  private GlobalConstants() {
  }

  /**
   * 最大可存储时间
   * 此处取的是 MySQL 的最大存储时间
   */
  public static final LocalDateTime MAXIMUM_TIME = LocalDateTime.of(9999, 12, 31, 23, 59, 59, 999);
}
