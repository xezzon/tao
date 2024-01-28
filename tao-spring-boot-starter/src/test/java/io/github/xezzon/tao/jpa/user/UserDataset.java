package io.github.xezzon.tao.jpa.user;

import cn.hutool.core.util.RandomUtil;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class UserDataset implements CommandLineRunner {

  private static final List<User> DATASET = new ArrayList<>();
  private static int executeTimes = 1;

  static {
    for (int i = 0; i < 1000; i++) {
      User user = new User();
      user.setId(UUID.randomUUID().toString());
      user.setName(RandomUtil.randomString(6));
      user.setAge(RandomUtil.randomInt(6, 60));
      user.setCredit(RandomUtil.randomBigDecimal(new BigDecimal("100.000")));
      user.setGender(RandomUtil.randomEle(GenderEnum.values()));
      user.setCreateTime(Instant.EPOCH.plus(
          RandomUtil.randomLong(365 * 30, 365 * 300),
          ChronoUnit.DAYS
      ));
      user.setDeleteDateTime(RandomUtil.randomBoolean() ?
          LocalDateTime.of(
              RandomUtil.randomInt(2000, 2999),
              RandomUtil.randomInt(1, 11),
              RandomUtil.randomInt(1, 27),
              RandomUtil.randomInt(1, 11),
              RandomUtil.randomInt(1, 59),
              RandomUtil.randomInt(1, 59)
          ) : null
      );
      user.setDeleted(user.getDeleteDateTime() != null);
      user.setDeleteDate(
          user.getDeleteDateTime() == null
              ? null
              : user.getDeleteDateTime().toLocalDate()
      );
      user.setDeleteTime(
          user.getDeleteDateTime() == null
              ? null
              : user.getDeleteDateTime().toLocalTime()
      );
      DATASET.add(user);
    }
  }

  public static List<User> getDataset() {
    return new ArrayList<>(DATASET);
  }

  @Resource
  private transient UserRepository repository;

  @Override
  public void run(String... args) {
    if (executeTimes-- <= 0) {
      return;
    }
    repository.saveAllAndFlush(DATASET);
  }
}
