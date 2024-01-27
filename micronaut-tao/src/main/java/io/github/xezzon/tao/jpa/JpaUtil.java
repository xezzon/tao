package io.github.xezzon.tao.jpa;

import cn.hutool.core.util.ReflectUtil;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.SimplePath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import io.github.xezzon.tao.retrieval.CommonQuery;
import io.micronaut.data.annotation.DateUpdated;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Sort;
import io.micronaut.data.model.Sort.Order;
import jakarta.persistence.Column;
import jakarta.persistence.Version;
import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.antlr.v4.runtime.tree.ParseTree;

/**
 * @author xezzon
 */
public class JpaUtil {

  public static final BooleanExpression TRUE_EXPRESSION = Expressions.ONE.eq(1);

  /**
   * 局部更新语句
   * @param obj 更新的信息
   * @param queryFactory update语句
   * @param dataObj DO对象
   * @return 拼接后的update语句
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  public static <T> JPAUpdateClause getUpdateClause(
      T obj,
      JPAQueryFactory queryFactory,
      EntityPathBase<T> dataObj
  ) {
    JPAUpdateClause clause = queryFactory.update(dataObj);
    Instant current = Instant.now();
    Set<Field> fields = Arrays.stream(obj.getClass().getDeclaredFields())
        .filter(field -> Objects.nonNull(field.getAnnotation(Column.class)))
        .filter(field -> field.getAnnotation(Column.class).updatable())
        .collect(Collectors.toSet());
    for (Field field : fields) {
      SimplePath path = Expressions.path(dataObj.getClass(), field.getName());
      Object value = ReflectUtil.getFieldValue(obj, field.getName());
      if (field.isAnnotationPresent(DateUpdated.class)) {
        clause.set(path, current);
      }
      if (value != null) {
        clause.set(path, value);
      }
      if (field.isAnnotationPresent(Version.class)) {
        clause.where(path.eq(value));
      }
    }
    return clause;
  }

  public static <T extends EntityPathBase<RT>, RT> BooleanExpression getQueryClause(
      CommonQuery commonQuery,
      T dataObj,
      Class<RT> clazz
  ) {
    ParseTree parseTree = commonQuery.parseFilter();
    if (parseTree == null) {
      return TRUE_EXPRESSION;
    }
    // 筛选
    CommonQueryFilterJpaVisitor<T, RT> visitor = new CommonQueryFilterJpaVisitor<>(dataObj, clazz);
    return Optional.ofNullable(visitor.visit(parseTree))
        .orElse(TRUE_EXPRESSION);
  }

  public static Pageable getPageable(CommonQuery commonQuery) {
    if (commonQuery.getPageSize() <= 0) {
      return Pageable.unpaged();
    }
    List<Order> orders = commonQuery.parseSort().parallelStream()
        .map(sorter -> switch (sorter.getDirection()) {
          case ASC -> Order.asc(sorter.getField());
          case DESC -> Order.desc(sorter.getField());
        })
        .toList();
    Sort sort = Sort.of(orders);
    return Pageable.from(commonQuery.getPageNum() - 1, commonQuery.getPageSize(), sort);
  }
}

