package io.github.xezzon.tao.retrieval;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.jetbrains.annotations.Nullable;

/**
 * 初级查询组件
 * @author xezzon
 * @see <a href="https://github.com/microsoft/api-guidelines">Microsoft REST API Guidelines</a>
 */
public class CommonQuery {

  /**
   * 字段范围 默认为全选
   */
  private Set<String> select = null;
  /**
   * 排序表达式<br/>
   * 结构为 field1:asc,field2:desc
   */
  private List<String> sort = Collections.emptyList();
  /**
   * 过滤表达式 <br/>
   * 结构为 ((field1 op value1) OR (field2 op value2)) AND (field3 op value31,value32)
   */
  private String filter = null;
  /**
   * 搜索关键字
   */
  private String searchKey = null;
  /**
   * 页码
   */
  private int pageNum = 0;
  /**
   * 页容量
   * 为 0 时不分页
   */
  private int pageSize = 0;

  /**
   * 表达式不合规导致抛出异常
   * @return 表达式不合规的异常
   */
  public static UnsupportedOperationException uoe(String expression) {
    return new UnsupportedOperationException("表达式异常: " + expression);
  }

  /**
   * 字段不存在导致抛出异常
   * @param field 不存在的字段
   * @return 表达式不合规的异常
   */
  public static UnsupportedOperationException nonexistentField(String field) {
    return new UnsupportedOperationException("不支持的字段: " + field);
  }

  /**
   * 不支持的操作符导致抛出异常
   * @param operator 不支持的操作符
   * @return 表达式不合规的异常
   */
  public static UnsupportedOperationException unsupportedOperator(String operator) {
    return new UnsupportedOperationException("不支持的操作符: " + operator);
  }

  /**
   * 转换为抽象语法树
   * @param bst 数据库实体类
   * @param initQuery 初始查询条件
   * @param <I> 抽象语法树的具体实现类
   * @return 抽象语法树
   * @throws UnsupportedOperationException 不合规的表达式
   * @see CommonQuery#getPageNum()
   */
  public <I> I toAst(ICommonQueryAst<I> bst, I initQuery) {
    return bst.toAst(this, initQuery);
  }

  /**
   * 解析排序表达式
   * @return 排序字段、方式
   */
  public List<CommonQuerySorter> parseSort() {
    if (this.sort == null) {
      return Collections.emptyList();
    }
    return this.sort.parallelStream()
        .map(CommonQuerySorter::new)
        .toList();
  }

  /**
   * 解析筛选表达式
   */
  @Nullable
  public ParseTree parseFilter() {
    if (this.filter == null) {
      return null;
    }
    try {
      io.github.xezzon.tao.retrieval.CommonQueryFilterLexer lexer =
          new io.github.xezzon.tao.retrieval.CommonQueryFilterLexer(
              CharStreams.fromString(this.filter)
          );
      CommonTokenStream tokens = new CommonTokenStream(lexer);
      return new io.github.xezzon.tao.retrieval.CommonQueryFilterParser(tokens)
          .clause();
    } catch (Exception e) {
      throw uoe(this.filter);
    }
  }

  public int getPageNum() {
    return this.pageNum;
  }

  public void setPageNum(int pageNum) {
    this.pageNum = pageNum;
  }

  public int getPageSize() {
    return this.pageSize;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  public Set<String> getSelect() {
    return this.select;
  }

  public void setSelect(Set<String> select) {
    this.select = select;
  }

  public List<String> getSort() {
    return this.sort;
  }

  public void setSort(List<String> sort) {
    this.sort = sort;
  }

  public String getFilter() {
    return filter;
  }

  public void setFilter(String filter) {
    this.filter = filter;
  }

  public String getSearchKey() {
    return searchKey;
  }

  public void setSearchKey(String searchKey) {
    this.searchKey = searchKey;
  }
}
