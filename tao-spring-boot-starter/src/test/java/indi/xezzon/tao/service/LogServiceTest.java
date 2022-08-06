package indi.xezzon.tao.service;

import indi.xezzon.tao.logger.LogRecord;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@SpringBootTest
@Component
class LogServiceTest {

  @Resource
  private LogService logService;

  @Test
  void log() {
    logService.log(new User("hello", "world"));
  }
}

@Component
class LogService {

  public static void main(String[] args) {
    ExpressionParser parser = new SpelExpressionParser();
    List<Boolean> list = new ArrayList<>();
    list.add(true);
    EvaluationContext ctx2 = new StandardEvaluationContext();
    // 将list设置成EvaluationContext的一个变量
    ctx2.setVariable("list", list);
    String value = parser.parseExpression("测试： #{#list[0]}", new TemplateParserContext())
        .getValue(ctx2, String.class);
    // list集合的第一个元素被改变
    System.out.println(value);
  }

  @LogRecord("登录: #{#user.username}; #{sayPassword(#user.password)}")
  public void log(User user) {
    System.out.println("日志测试");
  }

  public String sayPassword(String password) {
    return "say password: " + password;
  }
}

class User {

  private String username;
  private String password;

  public User(String username, String password) {
    this.username = username;
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
