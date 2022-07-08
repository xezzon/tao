package indi.xezzon.tao.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 数据库通用实体类生成基础方法
 * @author xezzon
 */
public abstract class BaseEntity implements Serializable {

  @java.io.Serial
  private static final long serialVersionUID = 4129917285621615159L;
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
   * 逻辑删除标记 删除时间大于当前时间则认为已删除<br/>
   * 数据库中该字段的默认值为最大时间 （MySQL 的最大时间为 '9999-12-31 23:59:59.999')<br/>
   * 注意 时间精度至少要到毫秒级
   */
  protected LocalDateTime deleteTime;

  public String getId() {
    return id;
  }

  public BaseEntity setId(String id) {
    this.id = id;
    return this;
  }

  public LocalDateTime getCreateTime() {
    return createTime;
  }

  public BaseEntity setCreateTime(LocalDateTime createTime) {
    this.createTime = createTime;
    return this;
  }

  public LocalDateTime getUpdateTime() {
    return updateTime;
  }

  public BaseEntity setUpdateTime(LocalDateTime updateTime) {
    this.updateTime = updateTime;
    return this;
  }

  public LocalDateTime getDeleteTime() {
    return deleteTime;
  }

  public BaseEntity setDeleteTime(LocalDateTime deleteTime) {
    this.deleteTime = deleteTime;
    return this;
  }

  public boolean isDeleted() {
    if (deleteTime == null) {
      return false;
    }
    return Objects.compare(LocalDateTime.now(), deleteTime, LocalDateTime::compareTo) >= 0;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BaseEntity that = (BaseEntity) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
