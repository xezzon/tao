package io.github.xezzon.tao.logger;

import io.github.xezzon.tao.exception.BaseException;
import io.github.xezzon.tao.exception.MultiException;
import io.github.xezzon.tao.util.DesensitizedUtil;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * 处理日志
 * @author xezzon
 */
@Aspect
public class LogRecordAspect {

  private static final String LOG_MESSAGE_FORMAT =
      "catalog: {{}}; message:{{}}; startTime:{{}}; endTime:{{}};";
  private final transient LogRecordExpressionEvaluator evaluator =
      new LogRecordExpressionEvaluator();

  /**
   * 获取日志解析上下文
   * @return 日志解析上下文 包含方法参数及被切面的对象
   */
  public static EvaluationContext getEvaluationContext(ProceedingJoinPoint pjp) {
    /* 获取切面签名 */
    EvaluationContext evaluationContext = new StandardEvaluationContext(pjp.getTarget());
    MethodSignature signature = (MethodSignature) pjp.getSignature();
    String[] parameterNames = signature.getParameterNames();
    Object[] args = pjp.getArgs();
    /* 获取参数注解以判断是否存在脱敏字段 */
    Method method = signature.getMethod();
    Annotation[][] annotations = method.getParameterAnnotations();
    /* 遍历参数，将参数放入上下文 */
    for (int i = 0, cnt = parameterNames.length; i < cnt; i++) {
      String parameterName = parameterNames[i];
      Object arg = args[i];
      // 参数值脱敏
      Optional<Annotation> annotation = Arrays.stream(annotations[i]).parallel()
          .filter(o -> LogDesensitize.class.equals(o.annotationType()))
          .findFirst();
      if (arg instanceof CharSequence c && annotation.isPresent()) {
        arg = DesensitizedUtil.desensitized(c, (((LogDesensitize) annotation.get())).value());
      }
      // 将参数放入上下文
      evaluationContext.setVariable(parameterName, arg);
    }
    return evaluationContext;
  }

  /**
   * TODO: 全局日志
   * TODO: 异步日志
   * TODO: OTLP
   */
  @Around("@annotation(logRecord)")
  public Object around(ProceedingJoinPoint point, LogRecord logRecord) throws Throwable {
    // 日志实体
    Logger logger = LoggerFactory.getLogger(point.getTarget().getClass());
    // 开始时间
    Instant startTime = Instant.now();
    // 日志详情
    EvaluationContext context = getEvaluationContext(point);
    String logValue = evaluator.evaluate(logRecord.value(), context);
    try {
      // 执行业务
      Object ret = point.proceed();

      Instant endTime = Instant.now();
      logger.info(
          LOG_MESSAGE_FORMAT,
          logRecord.catalog(), logValue, startTime, endTime
      );

      return ret;
    } catch (BaseException | MultiException e) {
      Instant endTime = Instant.now();
      logger.warn(
          LOG_MESSAGE_FORMAT,
          logRecord.catalog(), logValue, startTime, endTime, e
      );

      throw e;
    } catch (Exception e) {
      Instant endTime = Instant.now();
      logger.error(
          LOG_MESSAGE_FORMAT,
          logRecord.catalog(), logValue, startTime, endTime, e
      );

      throw e;
    }
  }
}
