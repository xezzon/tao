package indi.xezzon.tao.config;

import indi.xezzon.tao.logger.LogRecordAspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xezzon
 */
@Configuration
@ConditionalOnProperty(
    prefix = "tao.logger",
    name = "enable",
    havingValue = "true",
    matchIfMissing = true
)
public class LogAutoConfig {

  @Bean
  public LogRecordAspect logRecordAspect() {
    return new LogRecordAspect();
  }
}
