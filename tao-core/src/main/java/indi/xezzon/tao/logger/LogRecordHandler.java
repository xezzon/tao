package indi.xezzon.tao.logger;

import indi.xezzon.tao.exception.BaseException;
import indi.xezzon.tao.user.UserContext;
import java.time.Duration;
import java.time.Instant;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

/**
 * 处理日志
 * @author xezzon
 */
@Aspect
@Component
public class LogRecordHandler {

  @Around("@annotation(LogRecord)")
  public void around(ProceedingJoinPoint point, LogRecord logRecord) throws Throwable {
    // 日志实体
    Logger logger = LoggerFactory.getLogger(point.getTarget().getClass());
    // 开始时间
    Instant startTime = Instant.now();
    // 操作人
    MDC.put("operator", UserContext.getId());
    // 日志描述
    if (!logRecord.catalog().isEmpty()) {
      MDC.put("catalog", logRecord.catalog());
    }
    try {
      // 执行业务
      point.proceed();
      // 花费时间
      Instant endTime = Instant.now();
      MDC.put("spend (ms)", String.valueOf(Duration.between(startTime, endTime).toMillis()));
      logger.info(logRecord.value());
    } catch (BaseException e) {
      logger.warn(logRecord.value(), e);
      throw e;
    } catch (Throwable e) {
      logger.error(logRecord.value(), e);
      throw e;
    }
  }
}
