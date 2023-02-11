package io.github.xezzon.tao.retrieval;

/**
 * @author xezzon
 */
public class CommonQuerySorter {

  private final String field;
  private final SorterDirectionEnum direction;

  public CommonQuerySorter(String expression) {
    final int length = 2;
    String[] ss = expression.split(":");
    if (ss.length != length) {
      throw CommonQuery.uoe(expression);
    }
    String field = ss[0];
    String direction = ss[1];

    this.field = field;
    try {
      this.direction = SorterDirectionEnum.valueOf(direction.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw CommonQuery.unsupportedOperator(direction);
    }
  }

  public String getField() {
    return field;
  }

  public SorterDirectionEnum getDirection() {
    return direction;
  }
}
