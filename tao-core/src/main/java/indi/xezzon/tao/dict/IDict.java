package indi.xezzon.tao.dict;

/**
 * 字典接口
 */
public interface IDict {
  /**
   * @return 字典目
   */
  String getTag();

  /**
   * @return 字典值
   */
  String getCode();

  /**
   * @return 字典描述
   */
  String getLabel();

  /**
   * @return 排序号
   */
  int getOrdinal();
}
