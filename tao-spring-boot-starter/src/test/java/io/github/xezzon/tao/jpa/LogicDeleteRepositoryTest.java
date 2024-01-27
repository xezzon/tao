package io.github.xezzon.tao.jpa;

import io.github.xezzon.tao.jpa.dict.Dict;
import io.github.xezzon.tao.jpa.dict.DictDAO;
import io.github.xezzon.tao.jpa.dict.DictDataset;
import io.github.xezzon.tao.jpa.dict.QDict;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * @author xezzon
 */
@SpringBootTest
@ActiveProfiles("test")
class LogicDeleteRepositoryTest {

  @Autowired
  private DictDAO dictDAO;

  @Test
  void logicDelete() {
    Dict dict = DictDataset.getDataset().parallelStream()
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
  }
}

