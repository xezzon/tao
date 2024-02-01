package io.github.xezzon.tao.jpa;

import io.github.xezzon.tao.retrieval.CommonQuery;
import io.github.xezzon.tao.trait.NewType;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

/**
 * 封装DAO为newtype
 * @param <T> 标准实体模型
 * @author xezzon
 */
public interface JpaWrapper<
    T,
    M extends JpaRepository<T, ?> & QuerydslPredicateExecutor<T>
    >
    extends NewType<M> {

  /**
   * 根据ID局部更新
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
