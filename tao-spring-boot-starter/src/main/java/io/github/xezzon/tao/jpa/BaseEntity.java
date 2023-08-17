package io.github.xezzon.tao.jpa;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 数据库通用实体类生成基础方法
 * @author xezzon
 */
@Getter
@MappedSuperclass
@EntityListeners({AuditingEntityListener.class})
public abstract class BaseEntity<ID> implements Serializable {

  public static final int ID_LENGTH = 64;
  @java.io.Serial
  private static final long serialVersionUID = 4129917285621615159L;

  /**
   * 记录创建时间
   */
  @Column(nullable = false, updatable = false)
  @CreatedDate
  protected LocalDateTime createTime;
  /**
   * 记录最后更新时间
   */
  @Column(nullable = false)
  @LastModifiedDate
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

  public BaseEntity<ID> setCreateTime(LocalDateTime createTime) {
    this.createTime = createTime;
    return this;
  }

  public BaseEntity<ID> setUpdateTime(LocalDateTime updateTime) {
    this.updateTime = updateTime;
    return this;
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
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BaseEntity<?> that = (BaseEntity<?>) o;
    return Objects.equals(this.getId(), that.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.getId());
  }
}
