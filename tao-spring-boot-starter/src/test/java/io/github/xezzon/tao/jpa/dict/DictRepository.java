package io.github.xezzon.tao.jpa.dict;

import io.github.xezzon.tao.jpa.LogicDeleteRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DictRepository
    extends LogicDeleteRepository<Dict, String>, QuerydslPredicateExecutor<Dict> {

}
