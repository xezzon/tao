package io.github.xezzon.tao.jpa;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import io.github.xezzon.tao.jpa.dict.Dict;
import io.github.xezzon.tao.jpa.dict.DictDAO;
import io.github.xezzon.tao.jpa.dict.DictRepository;
import io.github.xezzon.tao.jpa.dict.QDict;
import jakarta.persistence.OptimisticLockException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * @author xezzon
 */
@SpringBootTest
@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
class LogicDeleteRepositoryTest {

  private static final List<Dict> DATASET = new ArrayList<>();

  @Autowired
  private DictDAO dictDAO;
  @Autowired
  private DictRepository repository;

  @BeforeAll
  void beforeAll() {
    for (int i = 0; i < Byte.MAX_VALUE; i++) {
      Dict dict = new Dict();
      dict.setName(RandomUtil.randomString(6));
      dict.setId(IdUtil.getSnowflakeNextIdStr());
      DATASET.add(dict);
    }
    repository.saveAll(DATASET);
  }

  @Test
  void logicDelete() {
    Dict dict = DATASET.parallelStream()
        .findAny().get();
    Assertions.assertDoesNotThrow(() -> dictDAO.get().logicDelete(dict.getId()));
    List<Dict> result = new ArrayList<>();
    dictDAO.get()
        .findAll(QDict.dict.deleteTime.isNotNull())
        .forEach(result::add);
    Assertions.assertEquals(
        1,
        result.size()
    );
    Assertions.assertEquals(
        dict,
        result.get(0)
    );
    Assertions.assertNotNull(result.get(0).getCreateTime());
    Assertions.assertNotNull(result.get(0).getUpdateTime());
  }

  @Test
  void optimisticLock() {
    Dict dict = DATASET.parallelStream()
        .findAny().get();
    dict.setUpdateTime(Instant.now());
    Assertions.assertThrows(OptimisticLockException.class, () -> dictDAO.get().save(dict));
  }
}

