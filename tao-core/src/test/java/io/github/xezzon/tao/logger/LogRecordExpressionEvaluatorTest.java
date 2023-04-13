package io.github.xezzon.tao.logger;

import cn.hutool.core.util.RandomUtil;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * @author xezzon
 */
class LogRecordExpressionEvaluatorTest {

  private static final LogRecordExpressionEvaluator EVALUATOR = new LogRecordExpressionEvaluator();

  @Test
  void evaluate() {
    LocalDateTime time = LocalDateTime.of(2023, 4, 13, 15, 18, 33);
    String expression = "用户 #{#user.username} 于 #{T(java.time.LocalDateTime).of(2023, 4, 13, 15, 18, 33).toString()} 登录";
    EvaluationContext context = new StandardEvaluationContext();
    String username = RandomUtil.randomString(RandomUtil.randomNumber());
    User user = new User(username);
    context.setVariable("user", user);
    String logValue = EVALUATOR.evaluate(expression, context);
    Assertions.assertEquals(
        String.format("用户 %s 于 %s 登录", username, time),
        logValue
    );
  }
}

record User(String username) {

}