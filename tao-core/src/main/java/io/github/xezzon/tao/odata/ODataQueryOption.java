package io.github.xezzon.tao.odata;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.jetbrains.annotations.Nullable;

/**
 * @author xezzon
 * @see <a
 * href="https://docs.oasis-open.org/odata/odata/v4.01/cs01/abnf/odata-abnf-construction-rules.txt">OData
 * ABNF Construction Rules Version 4.01</a>
 */
public class ODataQueryOption {

  /**
   * @see <a
   * href="https://docs.oasis-open.org/odata/odata/v4.01/odata-v4.01-part2-url-conventions.html#_Toc31360956">Common
   * Expression Syntax</a>
   */
  private String filter;
  /**
   *
   */
  private String orderby;
  /**
   * <a
   * href="https://docs.oasis-open.org/odata/odata/v4.01/odata-v4.01-part2-url-conventions.html#sec_SystemQueryOptionselect">System
   * Query Option $select</a>
   */
  private String select;
  /**
   * <a
   * href="https://docs.oasis-open.org/odata/odata/v4.01/odata-v4.01-part2-url-conventions.html#sec_SystemQueryOptionstopandskip">System
   * Query Options $top and $skip</a>
   */
  private Integer top;
  /**
   * <a
   * href="https://docs.oasis-open.org/odata/odata/v4.01/odata-v4.01-part2-url-conventions.html#sec_SystemQueryOptionstopandskip">System
   * Query Options $top and $skip</a>
   */
  private Integer skip;
  /**
   * <a
   * href="https://docs.oasis-open.org/odata/odata/v4.01/odata-v4.01-part2-url-conventions.html#_Toc31361044">System
   * Query Option $search</a>
   */
  private String search;

  @Nullable
  public ParseTree parseFilter() {
    if (this.filter == null) {
      return null;
    }
    try {
      ODataLexer lexer = new ODataLexer(
          CharStreams.fromString(this.filter)
      );
      CommonTokenStream tokens = new CommonTokenStream(lexer);
      return new ODataFilterParser(tokens).clause();
    } catch (Exception e) {
      throw new UnsupportedOperationException("$filter 表达式异常: " + this.filter);
    }
  }

  @Nullable
  public ParseTree parseOrderby() {
    if (this.orderby == null) {
      return null;
    }
    try {
      ODataOrderbyLexer lexer = new ODataOrderbyLexer(
          CharStreams.fromString(this.orderby)
      );
      CommonTokenStream tokens = new CommonTokenStream(lexer);
      return new ODataOrderbyParser(tokens).clause();
    } catch (Exception e) {
      throw new UnsupportedOperationException("$orderby 表达式异常: " + this.orderby);
    }
  }
}
