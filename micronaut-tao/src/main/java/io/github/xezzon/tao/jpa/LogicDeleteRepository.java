package io.github.xezzon.tao.jpa;

import io.micronaut.data.jpa.repository.JpaRepository;
import io.micronaut.transaction.annotation.Transactional;
import java.io.Serializable;
import java.time.Instant;
import java.util.Optional;

/**
 * @author xezzon
 */
public interface LogicDeleteRepository<T extends BaseEntity<ID>, ID extends Serializable>
    extends JpaRepository<T, ID> {

  /**
   * 逻辑删除
   * @param id 主键
   */
  @Transactional
  default void logicDelete(ID id) {
    Optional<T> entity = findById(id);
    if (entity.isEmpty()) {
      return;
    }
    entity.get().setDeleteTime(Instant.now());
    update(entity.get());
  };
}
