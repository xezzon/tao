package io.github.xezzon.tao.jpa;

import cn.hutool.core.util.ReflectUtil;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import io.github.xezzon.tao.retrieval.CommonQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Id;
import java.lang.reflect.Field;
import java.util.Arrays;
import org.hibernate.AnnotationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author xezzon
 */
public abstract class BaseJpaWrapper<
    T,
    D extends EntityPathBase<T>,
    M extends JpaRepository<T, ?> & QuerydslPredicateExecutor<T>
    >
    implements JpaWrapper<T, M> {

  private final transient M repository;
  private transient JPAQueryFactory queryFactory;

  protected BaseJpaWrapper(M repository) {
    this.repository = repository;
  }

  protected JPAQueryFactory getQueryFactory() {
    return this.queryFactory;
  }

  @Autowired
  private void setQueryFactory(EntityManager em) {
    this.queryFactory = new JPAQueryFactory(em);
  }

  @Override
  public M get() {
    return this.repository;
  }

  /**
   * 获取DO对象
   * @return DO对象
   */
  protected abstract D getQuery();

  /**
   * 获取标准实体模型的类
   * @return 类
   */
  protected abstract Class<T> getBeanClass();

  @Override
  public Page<T> query(CommonQuery params) {
    BooleanExpression queryClause =
        JpaUtil.getQueryClause(params, this.getQuery(), this.getBeanClass());
    Pageable pageable = JpaUtil.getPageable(params);
    return this.get().findAll(queryClause, pageable);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  @Transactional(rollbackFor = {Exception.class})
  public boolean update(T t) {
    JPAUpdateClause clause =
        JpaUtil.getUpdateClause(t, getQueryFactory(), this.getQuery());
    Field idField = Arrays.stream(this.getBeanClass().getDeclaredFields())
        .filter(field -> field.isAnnotationPresent(Id.class))
        .findAny()
        .orElseThrow(() -> new AnnotationException("No identifier specified for entity"));
    SimpleExpression column =
        (SimpleExpression) ReflectUtil.getFieldValue(this.getQuery(), idField.getName());
    Object value = ReflectUtil.getFieldValue(t, idField);
    clause.where(column.eq(value));
    long affected = clause.execute();
    return affected > 0;
  }
}
