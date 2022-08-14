package indi.xezzon.tao.config;

import indi.xezzon.tao.logger.LogRecordAspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xezzon
 */
@Configuration
@ConditionalOnMissingBean(LogRecordAspect.class)
public class LogConfig {

  @Bean
  public LogRecordAspect logRecordAspect() {
    return new LogRecordAspect();
  }
}
