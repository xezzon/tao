package io.github.xezzon.tao.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import java.io.Serializable;
import java.time.Instant;

/**
 * @author xezzon
 */
@MappedSuperclass
public interface LogicDeleteEntity extends Serializable {

  @Column()
  Instant getDeleteTime();

  void setDeleteTime(Instant deleteTime);

  @Transient
  default boolean isDeleted() {
    if (this.getDeleteTime() == null) {
      return false;
    }
    return !this.getDeleteTime().isAfter(Instant.now());
  }
}
