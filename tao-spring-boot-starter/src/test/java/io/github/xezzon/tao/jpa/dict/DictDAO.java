package io.github.xezzon.tao.jpa.dict;

import io.github.xezzon.tao.jpa.BaseJpaWrapper;
import org.springframework.stereotype.Repository;

@Repository
public class DictDAO extends BaseJpaWrapper<Dict, QDict, DictRepository> {

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
