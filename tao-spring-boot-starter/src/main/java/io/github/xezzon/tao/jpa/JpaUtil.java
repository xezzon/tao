package io.github.xezzon.tao.jpa;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import io.github.xezzon.tao.retrieval.CommonQuery;
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
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

/**
 * @author xezzon
 */
public class JpaUtil {

  private JpaUtil() {
    super();
  }

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
    try {
      for (Field field : fields) {
        Object column = field.get(dataObj);
        Path path = (Path) column;
        Object value = field.get(obj);
        if (field.isAnnotationPresent(LastModifiedDate.class)) {
          clause.set(path, current);
        }
        if (value != null) {
          clause.set(path, value);
        }
        SimpleExpression expression = (SimpleExpression) column;
        if (field.isAnnotationPresent(Version.class)) {
          clause.where(expression.eq(value));
        }
      }
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
    return clause;
  }

  public static <T extends EntityPathBase<R>, R> BooleanExpression getQueryClause(
      CommonQuery commonQuery,
      T dataObj,
      Class<R> clazz
  ) {
    ParseTree parseTree = commonQuery.parseFilter();
    if (parseTree == null) {
      return Expressions.TRUE;
    }
    // 筛选
    CommonQueryFilterJpaVisitor<T, R> visitor = new CommonQueryFilterJpaVisitor<>(dataObj, clazz);
    return Optional.ofNullable(visitor.visit(parseTree))
        .orElse(Expressions.TRUE);
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
    Sort sort = Sort.by(orders);
    return PageRequest.of(commonQuery.getPageNum() - 1, commonQuery.getPageSize(), sort);
  }
}

