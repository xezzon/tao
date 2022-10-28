package indi.xezzon.tao.retrieval;

/**
 * 过滤表达式中 op 允许值
 * @author xezzon
 */
public enum FilterOperatorEnum {
  /**
   * 等于
   */
  EQ,
  /**
   * 不等于
   */
  NE,
  /**
   * 字符串以 ... 开头
   */
  LLIKE,
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
  /**
   * 是否为 null 值
   */
  NULL,
  ;
}
