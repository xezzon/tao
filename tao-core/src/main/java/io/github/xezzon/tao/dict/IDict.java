package io.github.xezzon.tao.dict;

/**
 * 字典接口
 * @author xezzon
 */
public interface IDict {

  /**
   * 字典目
   * @return 字典目
   */
  String getTag();

  /**
   * 字典值
   * @return 字典值
   */
  String getCode();

  /**
   * 字典描述
   * @return 字典描述
   */
  String getLabel();

  /**
   * 排序号
   * @return 排序号
   */
  int getOrdinal();
}
