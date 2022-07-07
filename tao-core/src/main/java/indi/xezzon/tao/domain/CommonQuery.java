package indi.xezzon.tao.domain;

/**
 * 通用查询组件
 * @author xezzon
 */
public class CommonQuery {

  /**
   * 排序表达式<br/>
   * 结构为 field1|asc,field2|desc
   */
  private String sort;
  /**
   * 过滤表达式 <br/>
   * 结构为 (field1 op value1 OR field2 op value2) AND field3 op value31,value32<br/>
   * op 支持 eq(等于), nq(不等于), in(在某范围内,支持枚举、数值、日期时间), out(不在某范围内,支持枚举、数值、日期时间), gt(大于,支持数值、日期时间), lt(小于,支持数值、日期时间), ge(大于等于,支持数值、日期时间), le(小于等于,支持数值、日期时间)
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
}
