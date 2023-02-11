package io.github.xezzon.tao.dict;

import java.util.List;
import java.util.Objects;

/**
 * @author xezzon
 */
public interface IDictService {

  /**
   * 查询字典 （数据库）
   * @param tag 字典目
   * @return 字典
   */
  List<? extends IDict> findByTag(String tag);

  /**
   * 查询字典 （含数据库与枚举类，数据库优先级更高）
   * @param tag 字典目
   * @return 字典
   */
  default List<? extends IDict> getByTag(String tag) {
    List<? extends IDict> dict = findByTag(tag);
    if (dict.isEmpty()) {
      dict = DictFactory.get(tag);
    }
    return dict;
  }

  /**
   * 查找字典值
   * @param tag 字典目
   * @param code 字典编码
   * @return 字典值
   */
  default IDict getByTagAndCode(String tag, String code) {
    List<? extends IDict> dictionaries = this.getByTag(tag);
    if (dictionaries == null) {
      return null;
    }
    return dictionaries.stream()
        .filter(d -> Objects.equals(code, d.getCode()))
        .findFirst()
        .orElse(null);
  }
}
