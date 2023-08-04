package io.github.xezzon.tao.jpa;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.jpa.repository.JpaRepository;
import io.micronaut.transaction.annotation.Transactional;
import java.io.Serializable;

/**
 * @author xezzon
 */
public interface LogicDeleteRepository<T extends BaseEntity<ID>, ID extends Serializable>
    extends JpaRepository<T, ID> {

  /**
   * 逻辑删除
   * @param id 主键
   */
  @Query("update #{#entityName} e set e.deleteTime = current_timestamp where id = ?1")
  @Transactional
  void logicDelete(ID id);
}
