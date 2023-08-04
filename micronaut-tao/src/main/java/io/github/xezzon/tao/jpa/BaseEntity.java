package io.github.xezzon.tao.jpa;

import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.DateUpdated;
import io.micronaut.data.annotation.Version;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 数据库通用实体类生成基础方法
 * @author xezzon
 */
@MappedSuperclass
public abstract class BaseEntity<ID> implements Serializable {

  public static final int ID_LENGTH = 64;
  @java.io.Serial
  private static final long serialVersionUID = 4129917285621615159L;

  /**
   * 记录创建时间
   */
  @Column(nullable = false, updatable = false)
  @DateCreated
  protected LocalDateTime createTime;
  /**
   * 记录最后更新时间
   */
  @Column(nullable = false)
  @DateUpdated
  @Version
  protected LocalDateTime updateTime;
  /**
   * 逻辑删除标记 删除时间不为空且大于当前时间则认为已删除<br/>
   * 注意 时间精度至少要到毫秒级
   */
  @Column()
  protected LocalDateTime deleteTime;

  public abstract ID getId();

  public abstract BaseEntity<ID> setId(ID id);

  public LocalDateTime getCreateTime() {
    return this.createTime;
  }

  public BaseEntity<ID> setCreateTime(LocalDateTime createTime) {
    this.createTime = createTime;
    return this;
  }

  public LocalDateTime getUpdateTime() {
    return this.updateTime;
  }

  public BaseEntity<ID> setUpdateTime(LocalDateTime updateTime) {
    this.updateTime = updateTime;
    return this;
  }

  public LocalDateTime getDeleteTime() {
    return this.deleteTime;
  }

  public BaseEntity<ID> setDeleteTime(LocalDateTime deleteTime) {
    this.deleteTime = deleteTime;
    return this;
  }

  @Transient
  public boolean isDeleted() {
    if (this.deleteTime == null) {
      return false;
    }
    return !this.deleteTime.isAfter(LocalDateTime.now());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BaseEntity<?> that = (BaseEntity<?>) o;
    return Objects.equals(this.getId(), that.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.getId());
  }
}
