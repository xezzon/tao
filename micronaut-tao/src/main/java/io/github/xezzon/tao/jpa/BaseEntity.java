package io.github.xezzon.tao.jpa;

import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.DateUpdated;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 数据库通用实体类生成基础方法
 * @author xezzon
 */
@Getter
@Setter
@Accessors(chain = true)
@MappedSuperclass
@Deprecated
public abstract class BaseEntity<ID> implements Serializable {

  public static final int ID_LENGTH = 64;
  @java.io.Serial
  private static final long serialVersionUID = 4129917285621615159L;

  /**
   * 记录创建时间
   */
  @Column(nullable = false, updatable = false)
  @DateCreated
  protected Instant createTime;
  /**
   * 记录最后更新时间
   */
  @Column(nullable = false)
  @DateUpdated
  @Version
  protected Instant updateTime;

  public abstract ID getId();

  public abstract void setId(ID id);

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
