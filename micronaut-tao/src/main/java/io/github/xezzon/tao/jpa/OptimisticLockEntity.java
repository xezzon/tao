package io.github.xezzon.tao.jpa;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import java.time.Instant;

/**
 * @author xezzon
 */
@MappedSuperclass
public interface OptimisticLockEntity extends UpdateTimeEntity {

  @Version
  @Override
  Instant getUpdateTime();
}
