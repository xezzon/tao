package io.github.xezzon.tao.jpa;

import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.github.xezzon.tao.retrieval.CommonQuery;
import io.micronaut.data.jpa.repository.JpaRepository;
import io.micronaut.data.model.Page;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

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
    return null;
  }

  @Override
  public boolean update(T t) {
    return false;
  }
}
