package indi.xezzon.tao.dict;

import java.util.List;
import java.util.Objects;

public interface IDictService {

  /**
   * 查询字典 （数据库）
   * @param tag 字典目
   * @return 字典
   */
  List<IDict> findByTag(String tag);

  /**
   * 查询字典 （含数据库与枚举类，数据库优先级更高）
   * @param tag 字典目
   * @return 字典
   */
  default List<IDict> getByTag(String tag) {
    List<IDict> dict = findByTag(tag);
    if (dict == null) {
      dict = DictFactory.get(tag);
    }
    return dict;
  }

  default IDict getByTagAndCode(String tag, String code) {
    List<IDict> dictionaries = this.getByTag(tag);
    if (dictionaries == null) {
      return null;
    }
    return dictionaries.stream()
        .filter(d -> Objects.equals(code, d.getCode()))
        .findFirst()
        .orElse(null);
  }
}
