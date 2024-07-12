package io.github.xezzon.tao.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * @author xezzon
 */
@MappedSuperclass
interface UpdateTimeEntity extends Serializable {

  @Column(nullable = false)
  @LastModifiedDate
  Instant getUpdateTime();
}
