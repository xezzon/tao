package io.github.xezzon.tao.jpa.dict;

import cn.hutool.core.util.RandomUtil;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DictDataset implements CommandLineRunner {

  private static final List<Dict> DATASET = new ArrayList<>();
  private static int executeTimes = 1;

  static {
    for (int i = 0; i < 1000; i++) {
      Dict user = new Dict()
          .setName(RandomUtil.randomString(6))
          .setId(UUID.randomUUID().toString());
      DATASET.add(user);
    }
  }

  @Resource
  private transient DictRepository repository;

  public static List<Dict> getDataset() {
    return new ArrayList<>(DATASET);
  }

  @Override
  public void run(String... args) {
    if (executeTimes-- <= 0) {
      return;
    }
    repository.saveAllAndFlush(DATASET);
  }
}
