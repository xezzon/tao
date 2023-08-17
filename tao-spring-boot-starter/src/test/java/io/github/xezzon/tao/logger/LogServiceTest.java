package io.github.xezzon.tao.logger;

import io.github.xezzon.tao.util.DesensitizedUtil.MobilePhoneDesensitizer;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@SpringBootTest
class LogServiceTest {

  @Autowired
  private transient LogService logService;

  @Test
  void log() {
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outputStream));
    logService.log(new User("hello", "world"), "12345678910");
    Assertions.assertTrue(outputStream.toString()
        .contains("message:{登录: hello; say password: world; mobile: 123****8910;};")
    );
  }
}

@Component
class LogService {

  private static final Logger log = LoggerFactory.getLogger(LogService.class);

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
    log.debug(value);
  }

  @LogRecord("登录: #{#user.username}; #{sayPassword(#user.password)}; mobile: #{#mobilePhone};")
  public void log(User user, @LogDesensitize(MobilePhoneDesensitizer.class) String mobilePhone) {
    log.debug("日志测试");
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
