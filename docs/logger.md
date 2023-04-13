# 日志注解

灵感源自: [如何优雅地记录操作日志？](https://tech.meituan.com/2021/09/16/operational-logbook.html)

## Example

```java
package io.github.xezzon.geom;

import io.github.xezzon.tao.logger.EnableLogRecord;
import io.github.xezzon.tao.logger.LogRecord;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.github.xezzon.tao.logger.LogRecord;

class UserServiceImpl {

  @LogRecord(value = "用户 #{#user.username} 登录", catalog = "用户登录")
  void login(User user) {

  }
}
```

## @LogRecord 属性

### catalog

为同一目的记录的日志，catalog 应保持一致。

### value

这里记录着日志的详细内容。`#{}` 包裹的内容会被识别为一个 SpEL表达式的作用域。可以使用解析器 `LogRecordExpressionEvaluator` 对内容进行解析。SpEL 表达式用法非常丰富，可以参考资料 [Spring Expression Language](https://docs.spring.io/spring-framework/docs/4.2.x/spring-framework-reference/html/expressions.html)。

## spring-boot 扩展

使用 LogRecord 的 spring-boot 扩展需要做 2 件事。代码如下:

```java
package io.github.xezzon.geom;

import io.github.xezzon.tao.logger.EnableLogRecord;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author xezzon
 */
@SpringBootApplication
@EnableLogRecord  // 在启动类上增加 @EnableLogRecord 注解
public class AdminApplication {

  public static void main(String[] args) {
    SpringApplication.run(AdminApplication.class, args);
  }
}
```
```java
package io.github.xezzon.geom.core.config;

import io.github.xezzon.tao.logger.LogInterceptor;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addInterceptors(@NotNull InterceptorRegistry registry) {
    registry.addInterceptor(new LogInterceptor());  // 增加拦截器
    WebMvcConfigurer.super.addInterceptors(registry);
  }
}
```

### 拦截器

tao-spring-boot-starter 中定义了拦截器 `@LogInterceptor`。拦截器向 MDC 中新增了 `trace_id`、`span_id`、`parent_id`属性，这些概念源于 [opentelemetry](https://opentelemetry.io/docs/reference/specification/trace/api/)，`trace_id`和`parent_id`取值自 HttpHeader（这里需要一点运维手段，理论上不应该让前端来生成这个值）。另外还会将请求 URI 作为默认的 [`catalog` 属性值](#catalog)。

因为各位使用者会使用不同的手段进行认证授权，所以暂时没办法在 tao 中将 `operator_id` （操作人）写入 MDC，用户可以自行新增一个拦截器实现。

### 切面

使用`@EnableLogRecord`注解启用日志切面，函数执行完成后自动记录日志。
