package io.github.xezzon.tao.logger;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;

/**
 * 解析日志中 SpEL 表达式的解析器
 * @author xezzon
 */
public class LogRecordExpressionEvaluator {

  private static final ExpressionParser PARSER = new SpelExpressionParser();
  private static final ParserContext PARSER_CONTEXT = new TemplateParserContext();

  public String evaluate(String expression, EvaluationContext evaluationContext) {
    return PARSER.parseExpression(expression, PARSER_CONTEXT)
        .getValue(evaluationContext, String.class);
  }
}
