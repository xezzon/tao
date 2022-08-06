package indi.xezzon.tao.domain;

import java.time.LocalDateTime;

/**
 * 数据库通用实体类
 */
public class BaseEntity {

  /**
   * 主键
   */
  protected String id;
  /**
   * 记录创建时间
   */
  protected LocalDateTime createTime;
  /**
   * 记录最后更新时间
   */
  protected LocalDateTime updateTime;
  /**
   * 逻辑删除标记 删除时间大于当前时间则认为已删除
   * 数据库中该字段的默认值为最大时间 （MySQL 的最大时间为 '9999-12-31 23:59:59.999')
   * 注意 时间精度至少要到毫秒级
   */
  protected LocalDateTime deleteTime;
}
