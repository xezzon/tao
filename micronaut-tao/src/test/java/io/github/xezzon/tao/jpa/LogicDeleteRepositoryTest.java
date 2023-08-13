package io.github.xezzon.tao.jpa;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.runtime.event.ApplicationStartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author xezzon
 */
@MicronautTest
class LogicDeleteRepositoryTest {

  @Inject
  private DictDAO dictDAO;

  @Test
  void logicDelete() {
    Dict dict = DictDataset.getDataset().parallelStream()
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

@Getter
@Setter
@Accessors(chain = true)
@ToString
@Entity
@MappedEntity
@Table
class Dict extends BaseEntity<String> {

  @Serial
  private static final long serialVersionUID = 5254551771226841499L;

  @Id
  @Column
  private String id;
  @Column
  private String name;
}

@Singleton
class DictDataset {

  private static final List<Dict> DATASET = new ArrayList<>();

  static {
    for (int i = 0; i < 1000; i++) {
      Dict user = new Dict();
      user.setId(IdUtil.getSnowflakeNextIdStr());
      user.setName(RandomUtil.randomString(6));
      DATASET.add(user);
    }
  }

  @Inject
  private transient DictRepository repository;

  public static List<Dict> getDataset() {
    return new ArrayList<>(DATASET);
  }

  @EventListener
  public void init(ApplicationStartupEvent event) {
    repository.saveAll(DATASET);
  }
}

@Repository
interface DictRepository
    extends LogicDeleteRepository<Dict, String> {

}

@Repository
class DictDAO extends BaseJpaWrapper<Dict, QDict, DictRepository> {

  protected DictDAO(DictRepository dao) {
    super(dao);
  }

  @Override
  protected QDict getQuery() {
    return QDict.dict;
  }

  @Override
  protected Class<Dict> getBeanClass() {
    return Dict.class;
  }
}
