package io.github.xezzon.tao.jpa;

import static io.github.xezzon.tao.retrieval.CommonQuery.nonexistentField;
import static io.github.xezzon.tao.retrieval.CommonQuery.unsupportedOperator;
import static io.github.xezzon.tao.retrieval.CommonQuery.uoe;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
      SimpleExpression<?> field =
          (SimpleExpression<?>) ReflectUtil.getFieldValue(dataObj, rawField);
      if (field == null) {
        throw nonexistentField(ctx.getText());
      }
      /* 解析操作符 */
      FilterOperatorEnum op = FilterOperatorEnum.valueOf(rawOperator);
      // 空操作符单独处理
      if (op == FilterOperatorEnum.NULL) {
        if (Boolean.parseBoolean(rawValue)) {
          return field.isNull();
        } else {
          return field.isNotNull();
        }
      }
      /* 解析值 */
      rawValue = StrUtil.strip(rawValue, "'");
      /* 组装查询语句 */
      if (field instanceof StringPath f) {
        return switch (op) {
          case EQ -> f.eq(rawValue);
          case NE -> f.ne(rawValue);
          case LLIKE -> f.startsWith(rawValue);
          case IN -> f.in(StrUtil.split(rawValue, ","));
          case OUT -> f.notIn(StrUtil.split(rawValue, ","));
          default -> throw unsupportedOperator(ctx.getText());
        };
      } else if (field instanceof EnumPath<?> f) {
        Class<Enum> enumClazz = (Class<Enum>) ReflectUtil.getField(this.clazz, rawField).getType();
        Set<Enum> values = StrUtil.split(rawValue, ",").parallelStream()
            .map(o -> Enum.valueOf(enumClazz, o))
            .collect(Collectors.toSet());
        return switch (op) {
          case EQ, IN -> ReflectUtil.invoke(f, "in", values);
          case NE, OUT -> ReflectUtil.invoke(f, "notIn", values);
          default -> throw unsupportedOperator(ctx.getText());
        };
      } else if (field instanceof NumberPath<?> f) {
        BigDecimal value = new BigDecimal(rawValue);
        return switch (op) {
          case EQ -> ReflectUtil.invoke(f, "eq", value);
          case NE -> ReflectUtil.invoke(f, "ne", value);
          case GT -> f.gt(value);
          case LT -> f.lt(value);
          case GE -> f.goe(value);
          case LE -> f.loe(value);
          default -> throw unsupportedOperator(ctx.getText());
        };
      } else if (field instanceof DateTimePath<?> f) {
        LocalDateTime value = LocalDateTime.parse(rawValue);
        return switch (op) {
          case EQ -> ReflectUtil.invoke(f, "eq", value);
          case NE -> ReflectUtil.invoke(f, "ne", value);
          case GT -> ReflectUtil.invoke(f, "gt", value);
          case LT -> ReflectUtil.invoke(f, "lt", value);
          case GE -> ReflectUtil.invoke(f, "goe", value);
          case LE -> ReflectUtil.invoke(f, "loe", value);
          default -> throw unsupportedOperator(ctx.getText());
        };
      } else if (field instanceof DatePath<?> f) {
        LocalDate value = LocalDate.parse(rawValue);
        return switch (op) {
          case EQ -> ReflectUtil.invoke(f, "eq", value);
          case NE -> ReflectUtil.invoke(f, "ne", value);
          case GT -> ReflectUtil.invoke(f, "gt", value);
          case LT -> ReflectUtil.invoke(f, "lt", value);
          case GE -> ReflectUtil.invoke(f, "goe", value);
          case LE -> ReflectUtil.invoke(f, "loe", value);
          default -> throw unsupportedOperator(ctx.getText());
        };
      } else if (field instanceof TimePath<?> f) {
        LocalTime value = LocalTime.parse(rawValue);
        return switch (op) {
          case EQ -> ReflectUtil.invoke(f, "eq", value);
          case NE -> ReflectUtil.invoke(f, "ne", value);
          case GT -> ReflectUtil.invoke(f, "gt", value);
          case LT -> ReflectUtil.invoke(f, "lt", value);
          case GE -> ReflectUtil.invoke(f, "goe", value);
          case LE -> ReflectUtil.invoke(f, "loe", value);
          default -> throw unsupportedOperator(ctx.getText());
        };
      } else if (field instanceof BooleanPath f) {
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
