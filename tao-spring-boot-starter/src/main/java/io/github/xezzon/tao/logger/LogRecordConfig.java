package io.github.xezzon.tao.logger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xezzon
 */
@Configuration
public class LogRecordConfig {

  @Bean
  public LogRecordAspect logRecordAspect() {
    return new LogRecordAspect();
  }
}
