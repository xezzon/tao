package indi.xezzon.tao.dict;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 字典工程 用于存储枚举类字典
 */
public class DictFactory {

  private static final Map<String, List<IDict>> DICT_MAP = new HashMap<>();

  public static void register(String tag, List<IDict> dict) {
    if (tag != null && !tag.isEmpty()) {
      DICT_MAP.put(tag, dict);
    }
  }

  public static List<IDict> get(String tag) {
    return DICT_MAP.get(tag);
  }
}
