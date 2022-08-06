package indi.xezzon.tao.service;

import javax.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LogServiceTest {
  @Resource
  private LogService logService;

  @Test
  void log() {
    logService.log();
  }
}