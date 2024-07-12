package io.github.xezzon.tao.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * @author xezzon
 */
@MappedSuperclass
public interface AuditingTimeEntity extends UpdateTimeEntity {

  @Column(nullable = false, updatable = false)
  @CreatedDate
  Instant getCreateTime();
}
