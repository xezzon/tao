package io.github.xezzon.tao.jpa;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import io.github.xezzon.tao.jpa.dict.Dict;
import io.github.xezzon.tao.jpa.dict.DictDAO;
import io.github.xezzon.tao.jpa.dict.DictRepository;
import io.github.xezzon.tao.jpa.dict.QDict;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

/**
 * @author xezzon
 */
@MicronautTest
@TestInstance(Lifecycle.PER_CLASS)
class LogicDeleteRepositoryTest {

  private static final List<Dict> DATASET = new ArrayList<>();

  @Inject
  private DictDAO dictDAO;
  @Inject
  private DictRepository repository;

  @BeforeAll
  void beforeAll() {
    for (int i = 0; i < Byte.MAX_VALUE; i++) {
      Dict dict = new Dict()
          .setName(RandomUtil.randomString(6))
          .setId(IdUtil.getSnowflakeNextIdStr());
      DATASET.add(dict);
    }
    repository.saveAll(DATASET);
  }

  @Test
  void logicDelete() {
    Dict dict = DATASET.parallelStream()
        .findAny().get();
    Assertions.assertDoesNotThrow(() -> dictDAO.get().logicDelete(dict.getId()));
    List<Dict> result = dictDAO.findAll(QDict.dict.deleteTime.isNotNull());
    Assertions.assertEquals(
        1,
        result.size()
    );
    Assertions.assertEquals(
        dict,
        result.get(0)
    );
  }
}

