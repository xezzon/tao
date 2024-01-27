package io.github.xezzon.tao.jpa;

import static io.github.xezzon.tao.retrieval.CommonQuery.nonexistentField;
import static io.github.xezzon.tao.retrieval.CommonQuery.unsupportedOperator;
import static io.github.xezzon.tao.retrieval.CommonQuery.uoe;

import cn.hutool.core.util.ReflectUtil;
import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.BooleanPath;
import com.querydsl.core.types.dsl.DatePath;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.core.types.dsl.TimePath;
import io.github.xezzon.tao.retrieval.CommonQueryFilterBaseVisitor;
import io.github.xezzon.tao.retrieval.CommonQueryFilterParser.AndLogicContext;
import io.github.xezzon.tao.retrieval.CommonQueryFilterParser.OrLogicContext;
import io.github.xezzon.tao.retrieval.CommonQueryFilterParser.ParenthesisContext;
import io.github.xezzon.tao.retrieval.CommonQueryFilterParser.PredicateContext;
import io.github.xezzon.tao.retrieval.FilterOperatorEnum;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 处理 CommonQueryFilter 语法树
 * @param <T> DO类
 * @param <RT> 实体类
 */
class CommonQueryFilterJpaVisitor<T extends EntityPathBase<RT>, RT>
    extends CommonQueryFilterBaseVisitor<BooleanExpression> {

  private static final Logger log = LoggerFactory.getLogger(CommonQueryFilterJpaVisitor.class);
  private final T dataObj;
  private final Class<RT> clazz;

  public CommonQueryFilterJpaVisitor(T dataObj, Class<RT> clazz) {
    this.dataObj = dataObj;
    this.clazz = clazz;
  }

  @Override
  public BooleanExpression visitAndLogic(AndLogicContext ctx) {
    BooleanExpression clause0 = this.visit(ctx.clause(0));
    BooleanExpression clause1 = this.visit(ctx.clause(1));
    return clause0.and(clause1);
  }

  @Override
  public BooleanExpression visitOrLogic(OrLogicContext ctx) {
    BooleanExpression clause0 = this.visit(ctx.clause(0));
    BooleanExpression clause1 = this.visit(ctx.clause(1));
    return clause0.or(clause1);
  }

  @Override
  public BooleanExpression visitParenthesis(ParenthesisContext ctx) {
    return this.visit(ctx.clause());
  }

  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public BooleanExpression visitPredicate(PredicateContext ctx) {
    try {
      String rawField = ctx.FIELD().getText();
      String rawOperator = ctx.OP().getText();
      String rawValue = ctx.VALUE().getText();
      /* 解析字段 */
      SimpleExpression<?> column =
          (SimpleExpression<?>) ReflectUtil.getFieldValue(dataObj, rawField);
      if (column == null) {
        throw nonexistentField(ctx.getText());
      }
      /* 解析操作符 */
      FilterOperatorEnum op = FilterOperatorEnum.valueOf(rawOperator);
      // 空操作符单独处理
      if (op == FilterOperatorEnum.NULL) {
        if (Boolean.parseBoolean(rawValue)) {
          return column.isNull();
        } else {
          return column.isNotNull();
        }
      }
      /* 解析值 */
      rawValue = CharMatcher.anyOf("'").trimFrom(rawValue);
      /* 组装查询语句 */
      if (column instanceof StringPath f) {
        return switch (op) {
          case EQ -> f.eq(rawValue);
          case NE -> f.ne(rawValue);
          case LLIKE -> f.startsWith(rawValue);
          case IN -> f.in(Splitter.on(",").splitToList(rawValue));
          case OUT -> f.notIn(Splitter.on(",").splitToList(rawValue));
          default -> throw unsupportedOperator(ctx.getText());
        };
      } else if (column instanceof EnumPath f) {
        Class<Enum> enumClazz = (Class<Enum>) ReflectUtil.getField(this.clazz, rawField).getType();
        Set<Enum> values = Arrays.stream(rawValue.split(",")).parallel()
            .map(o -> Enum.valueOf(enumClazz, o))
            .collect(Collectors.toSet());
        return switch (op) {
          case EQ, IN -> f.in(values);
          case NE, OUT -> f.notIn(values);
          default -> throw unsupportedOperator(ctx.getText());
        };
      } else if (column instanceof NumberPath f) {
        BigDecimal value = new BigDecimal(rawValue);
        return switch (op) {
          case EQ -> f.eq(value);
          case NE -> f.ne(value);
          case GT -> f.gt(value);
          case LT -> f.lt(value);
          case GE -> f.goe(value);
          case LE -> f.loe(value);
          default -> throw unsupportedOperator(ctx.getText());
        };
      } else if (column instanceof DateTimePath f) {
        Field field = clazz.getDeclaredField(rawField);
        if (Objects.equals(field.getType(), LocalDateTime.class)) {
          LocalDateTime value = LocalDateTime.parse(rawValue);
          return switch (op) {
            case EQ -> f.eq(value);
            case NE -> f.ne(value);
            case GT -> f.gt(value);
            case LT -> f.lt(value);
            case GE -> f.goe(value);
            case LE -> f.loe(value);
            default -> throw unsupportedOperator(ctx.getText());
          };
        } else if (Objects.equals(field.getType(), Instant.class)) {
          Instant value = Instant.parse(rawValue);
          return switch (op) {
            case EQ -> f.eq(value);
            case NE -> f.ne(value);
            case GT -> f.gt(value);
            case LT -> f.lt(value);
            case GE -> f.goe(value);
            case LE -> f.loe(value);
            default -> throw unsupportedOperator(ctx.getText());
          };
        }
        throw uoe(ctx.getText());
      } else if (column instanceof DatePath f) {
        LocalDate value = LocalDate.parse(rawValue);
        return switch (op) {
          case EQ -> f.eq(value);
          case NE -> f.ne(value);
          case GT -> f.gt(value);
          case LT -> f.lt(value);
          case GE -> f.goe(value);
          case LE -> f.loe(value);
          default -> throw unsupportedOperator(ctx.getText());
        };
      } else if (column instanceof TimePath f) {
        LocalTime value = LocalTime.parse(rawValue);
        return switch (op) {
          case EQ -> f.eq(value);
          case NE -> f.ne(value);
          case GT -> f.gt(value);
          case LT -> f.lt(value);
          case GE -> f.goe(value);
          case LE -> f.loe(value);
          default -> throw unsupportedOperator(ctx.getText());
        };
      } else if (column instanceof BooleanPath f) {
        boolean value = Boolean.parseBoolean(rawValue);
        return switch (op) {
          case EQ -> f.eq(value);
          case NE -> f.ne(value);
          default -> throw unsupportedOperator(ctx.getText());
        };
      } else {
        throw nonexistentField(ctx.getText());
      }
    } catch (UnsupportedOperationException e) {
      throw e;
    } catch (Exception e) {
      String expression = ctx.getText();
      log.error("解析失败: {}", expression, e);
      throw uoe(expression);
    }
  }
}
