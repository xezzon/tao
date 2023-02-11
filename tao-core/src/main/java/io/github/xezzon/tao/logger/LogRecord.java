package io.github.xezzon.tao.logger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 日志注解
 * @author xezzon
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogRecord {

  /**
   * 支持SpringEL表达式
   * 日志项之间用 ; 分隔
   * 日志项可以用 : 分隔键与值
   * @return 日志内容
   */
  String value() default "";

  /**
   * 若不填则默认从请求中读取
   * @return 目录
   */
  String catalog() default "";
}
