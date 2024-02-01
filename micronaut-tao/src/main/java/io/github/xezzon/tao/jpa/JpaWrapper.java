package io.github.xezzon.tao.jpa;

import io.github.xezzon.tao.retrieval.CommonQuery;
import io.github.xezzon.tao.trait.NewType;
import io.micronaut.data.jpa.repository.JpaRepository;
import io.micronaut.data.model.Page;

/**
 * 封装 Repository
 * @author xezzon
 */
public interface JpaWrapper<T, M extends JpaRepository<T, ?>>
  extends NewType<M> {

  /**
   * 根据 ID 局部更新
   * @param t 字段为null不更新
   * @return 是否更新成功
   */
  boolean update(T t);

  /**
   * 通用查询组件
   * @param params 查询参数
   * @return 分页后的查询内容
   */
  Page<T> query(CommonQuery params);
}
