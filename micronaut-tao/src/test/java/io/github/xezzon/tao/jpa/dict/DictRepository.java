package io.github.xezzon.tao.jpa.dict;

import io.github.xezzon.tao.jpa.LogicDeleteRepository;
import io.micronaut.data.annotation.Repository;

/**
 * @author xezzon
 */
@Repository
public interface DictRepository extends LogicDeleteRepository<Dict, String> {

}
