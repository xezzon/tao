package indi.xezzon.tao.domain;

/**
 * 通用查询组件 参考 OData 规范
 * @author xezzon
 */
public class CommonQuery {

  /**
   * 字段范围 默认为全选
   */
  private String select;
  /**
   * 排序表达式<br/>
   * 结构为 field1|asc,field2|desc
   */
  private String sort;
  /**
   * 过滤表达式 <br/>
   * 结构为 (field1 op value1 OR field2 op value2) AND field3 op value31,value32
   */
  private String filter;
  /**
   * 搜索关键字
   */
  private String searchKey;
  /**
   * 页码
   */
  private int pageNum;
  /**
   * 页容量
   */
  private int pageSize;

  /**
   * 表达式不合规导致抛出异常
   * @return 表达式不合规的异常
   */
  public static UnsupportedOperationException uoe() {
    return new UnsupportedOperationException("表达式异常");
  }

  /**
   * 字段不存在导致抛出异常
   * @param field 不存在的字段
   * @return 表达式不合规的异常
   */
  public static UnsupportedOperationException nonexistentField(String field) {
    return new UnsupportedOperationException("不存在的字段: " + field);
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
   */
  public <I> I toAst(ICommonQueryAst<I> bst, I initQuery) {
    return bst.toAst(this, initQuery);
  }

  /**
   * 过滤表达式中 op 允许值
   */
  public enum FilterOperator {
    /**
     * 等于
     */
    EQ,
    /**
     * 不等于
     */
    NE,
    /**
     * 在某范围内 支持枚举、数值、日期时间
     */
    IN,
    /**
     * 不在某范围内 支持枚举、数值、日期时间
     */
    OUT,
    /**
     * 大于 支持数值、日期时间
     */
    GT,
    /**
     * 小于 支持数值、日期时间
     */
    LT,
    /**
     * 大于等于 支持数值、日期时间
     */
    GE,
    /**
     * 小于等于 支持数值、日期时间
     */
    LE,
    ;
  }
}
