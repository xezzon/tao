package io.github.xezzon.tao.jpa;

import io.micronaut.data.annotation.DateUpdated;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.Instant;

/**
 * @author xezzon
 */
@MappedSuperclass
interface UpdateTimeEntity extends Serializable {

  @Column(nullable = false)
  @DateUpdated
  Instant getUpdateTime();
}
