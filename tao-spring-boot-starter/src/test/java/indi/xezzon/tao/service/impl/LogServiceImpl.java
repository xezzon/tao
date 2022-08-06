package indi.xezzon.tao.service.impl;

import indi.xezzon.tao.logger.LogRecord;
import indi.xezzon.tao.service.LogService;
import org.springframework.stereotype.Service;

/**
 * @author xezzon
 */
@Service
public class LogServiceImpl implements LogService {

  @Override
  @LogRecord("切面日志")
  public void log() {
    System.out.println("日志测试");
  }
}
