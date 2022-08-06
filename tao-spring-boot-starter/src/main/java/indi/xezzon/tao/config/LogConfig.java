package indi.xezzon.tao.config;

import indi.xezzon.tao.logger.LogRecordAspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xezzon
 */
@Configuration
@ConditionalOnMissingBean(LogRecordAspect.class)
public class LogConfig {

  private static final Logger log = LoggerFactory.getLogger(LogConfig.class);

  @Bean
  public LogRecordAspect logRecordAspect() {
    return new LogRecordAspect();
  }
}
