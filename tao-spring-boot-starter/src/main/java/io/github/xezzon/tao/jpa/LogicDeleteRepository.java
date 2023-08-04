package io.github.xezzon.tao.jpa;

import java.io.Serializable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author xezzon
 */
@NoRepositoryBean
public interface LogicDeleteRepository<T extends BaseEntity, ID extends Serializable>
    extends JpaRepository<T, ID> {

  /**
   * 逻辑删除
   * @param id 主键
   */
  @Query("update #{#entityName} e set e.deleteTime = current_timestamp where id = ?1")
  @Modifying
  @Transactional
  void logicDelete(ID id);
}
