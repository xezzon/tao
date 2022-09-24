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
    prefix = LogAutoConfig.SPI_PREFIX,
    name = LogAutoConfig.SPI_NAME,
    havingValue = "true",
    matchIfMissing = true
)
public class LogAutoConfig {

  static final String SPI_PREFIX = "tao.logger";
  static final String SPI_NAME = "enable";

  @Bean
  public LogRecordAspect logRecordAspect() {
    return new LogRecordAspect();
  }
}
