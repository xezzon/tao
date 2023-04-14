package io.github.xezzon.tao.retrieval;

import io.github.xezzon.tao.retrieval.CommonQueryFilterParser.PredicateContext;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author xezzon
 */
class CommonQueryTest {

  static final String filter =
      "field1 OUT 'value1,value2' AND (field2 EQ 2e-1 OR field3 NULL true)";
  private final CommonQuery commonQuery = new CommonQuery();

  CommonQueryTest() {
    commonQuery.setSort(List.of(
        "field1:ASC",
        "field2:DESC"
    ));
    commonQuery.setFilter(filter);
  }

  @Test
  void parseSort() {
    List<CommonQuerySorter> sorters = commonQuery.parseSort();
    Assertions.assertEquals("field1", sorters.get(0).getField());
    Assertions.assertEquals(SorterDirectionEnum.ASC, sorters.get(0).getDirection());
    Assertions.assertEquals("field2", sorters.get(1).getField());
    Assertions.assertEquals(SorterDirectionEnum.DESC, sorters.get(1).getDirection());
  }

  @Test
  void parseFilter() {
    ParseTree parseTree = commonQuery.parseFilter();
    Assertions.assertNotNull(parseTree);
    Assertions.assertEquals(3, parseTree.getChildCount());
    ParseTreeWalker walker = new ParseTreeWalker();
    CommonQueryTestListener listener = new CommonQueryTestListener();
    walker.walk(listener, parseTree);
  }
}

class CommonQueryTestListener
    extends io.github.xezzon.tao.retrieval.CommonQueryFilterBaseListener {

  public void enterPredicate(PredicateContext ctx) {
    Assertions.assertTrue(
        Arrays.stream(FilterOperatorEnum.values())
            .anyMatch((op) -> Objects.equals(op.toString(), ctx.OP().getText()))
    );
  }
}