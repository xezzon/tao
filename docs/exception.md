# 基础异常类

起源于阿里巴巴出品的 [《Java 开发手册》](https://github.com/alibaba/p3c) 异常日志章节。

如果抛出基础异常的子类，那么 [@LogRecord 切面日志](./logger.md#spring-boot-扩展) 将输出一条 `warn` 级别的日志。

## 三类异常

分别指 `ClientException`（客户端异常）、`ServerException`（服务端异常）、`ThirdPartyException`（第三方服务异常）。

## 自定义异常类

使用者可以继承三类异常自行定义异常类。demo 如下:

```java
/**
 * 尚未登录
 * @author xezzon
 */
class UnauthorizedException extends ClientException {

  @Serial
  private static final long serialVersionUID = 1587877116361597984L;
  private static final String ERROR_CODE = "A0230";
  private static final String ERROR_MESSAGE = "尚未登录";

  protected UnauthorizedException() {
    super(ERROR_CODE, ERROR_MESSAGE);
  }

  protected UnauthorizedException(String message) {
    super(ERROR_CODE, message);
  }
}
```

## 全局异常捕获

使用者可以通过 spring-boot 的 [全局异常捕获机制](https://www.baeldung.com/exception-handling-for-rest-with-spring) 处理基础异常类。如果想要偷一下懒，tao 提供了一个基础异常类的全局捕获器，可以通过如下方式使用：

```java
package io.github.xezzon.geom;

import io.github.xezzon.geom.core.exception.BaseExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(BaseExceptionHandler.class)  // 引入基础异常类的全局捕获器
public class AdminApplication {

  public static void main(String[] args) {
    SpringApplication.run(AdminApplication.class, args);
  }
}
```

`ClientException` 意味着错误源于用户的异常输入，所以会将错误消息返回给前端并提示给用户。而 `ServerException` 和 `ThirdPartyException` 用户显然处理不了，所以除了记录日志之外，异常消息并不会返回给前端。

## Result

区别于可恢复异常 [Result](./Result.md)，这里的 Result 是响应结果（并不特指 HTTP 响应）。

Result 提供了静态方法 `success` 和 `fail` 分别用于正确的返回与异常的返回。
Result 对象会提供给调用者 `code`（异常码）、`message`（异常消息）、`data`（负载数据）、`requestId`（即 [trace_id](./logger.md#拦截器)）。通常而言，正确的返回值包括 `code`（默认为`00000`） 和 `data`，而错误的返回值包括 `code`、`message`、`requestId`，但这并非绝对。

对于 Restful 接口，强烈不建议对正确的返回值包裹 Result。而对于 RPC 接口却恰恰相反 —— 即使是一个正确的返回，仍然建议返回一个 Result 包裹的对象。

## 多消息异常 MultiException

有时难免会需要返回一个包含多条错误消息的异常，最常见的就是字段校验，比起一段一段校验，一次性校验完所有字段或许是一种更好的体验。因受限于 `BaseException` 的设计，提供了 `MultiException` 对 `BaseException` 进行封装。抛出 `MultiException` 时，[全局异常捕获](#全局异常捕获)返回的 [Result](#result) 会将错误消息置于 `data` 而非 `message` 字段。
