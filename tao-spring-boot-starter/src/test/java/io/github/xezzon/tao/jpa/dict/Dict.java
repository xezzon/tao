package io.github.xezzon.tao.jpa.dict;

import io.github.xezzon.tao.jpa.AuditingTimeEntity;
import io.github.xezzon.tao.jpa.LogicDeleteEntity;
import io.github.xezzon.tao.jpa.OptimisticLockEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serial;
import java.time.Instant;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@ToString
@Entity
@Table
@EntityListeners(AuditingEntityListener.class)
public class Dict implements AuditingTimeEntity, OptimisticLockEntity, LogicDeleteEntity {

  @Serial
  private static final long serialVersionUID = 5254551771226841499L;

  @Id
  @Column
  private String id;
  @Column
  private String name;
  private Instant createTime;
  private Instant updateTime;
  private Instant deleteTime;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Dict dict = (Dict) o;
    return Objects.equals(id, dict.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
