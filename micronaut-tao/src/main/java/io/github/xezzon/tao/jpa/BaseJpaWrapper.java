package io.github.xezzon.tao.jpa;

import cn.hutool.core.util.ReflectUtil;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import io.github.xezzon.tao.retrieval.CommonQuery;
import io.micronaut.data.jpa.repository.JpaRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Id;
import jakarta.transaction.Transactional;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * @author xezzon
 */
public abstract class BaseJpaWrapper<
    T,
    D extends EntityPathBase<T>,
    M extends JpaRepository<T, ?>
    >
    implements JpaWrapper<T, M> {

  private final transient M repository;
  private transient JPAQueryFactory queryFactory;

  protected BaseJpaWrapper(M repository) {
    this.repository = repository;
  }

  @Inject
  protected void setQueryFactory(EntityManager em) {
    this.queryFactory = new JPAQueryFactory(em);
  }

  protected JPAQueryFactory getQueryFactory() {
    return this.queryFactory;
  }

  @Override
  public M get() {
    return this.repository;
  }

  protected abstract D getQuery();

  protected abstract Class<T> getBeanClass();

  @Override
  public Page<T> query(CommonQuery params) {
    BooleanExpression queryClause =
        JpaUtil.getQueryClause(params, this.getQuery(), this.getBeanClass());
    Pageable pageable = JpaUtil.getPageable(params);
    return this.findAll(queryClause, pageable);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  @Transactional(rollbackOn = {Exception.class})
  public boolean update(T t) {
    JPAUpdateClause clause =
        JpaUtil.getUpdateClause(t, getQueryFactory(), this.getQuery());
    Field idField = Arrays.stream(this.getBeanClass().getDeclaredFields())
        .filter(field -> field.isAnnotationPresent(Id.class))
        .findAny()
        .orElseThrow(() -> new RuntimeException("No identifier specified for entity"));
    SimpleExpression column =
        (SimpleExpression) ReflectUtil.getFieldValue(this.getQuery(), idField.getName());
    Object value = ReflectUtil.getFieldValue(t, idField);
    clause.where(column.eq(value));
    long affected = clause.execute();
    return affected > 0;
  }

  protected List<T> findAll(Predicate predicate) {
    return this.createQuery(predicate, Pageable.unpaged()).fetch();
  }

  protected Page<T> findAll(Predicate predicate, Pageable pageable) {
    JPAQuery<T> query = createQuery(predicate, Pageable.unpaged());
    JPAQuery<T> pageQuery = createQuery(predicate, pageable);
    return Page.of(pageQuery.fetch(), pageable, query.fetchCount());
  }

  protected JPAQuery<T> createQuery(@NotNull Predicate predicate, @NotNull Pageable pageable) {
    JPAQuery<T> jpaQuery = queryFactory.selectFrom(this.getQuery());
    jpaQuery.where(predicate);
    if (pageable.isSorted()) {
      jpaQuery.orderBy(pageable.getOrderBy().stream()
          .map((orderBy) -> new OrderSpecifier(
              orderBy.isAscending() ? Order.ASC : Order.DESC,
              Expressions.path(this.getQuery().getType(), this.getQuery(), orderBy.getProperty())
          ))
          .toArray(OrderSpecifier[]::new)
      );
    }
    if (!pageable.isUnpaged()) {
      jpaQuery.offset(pageable.getOffset());
      jpaQuery.limit(pageable.getSize());
    }
    return jpaQuery;
  }
}
