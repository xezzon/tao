package io.github.xezzon.tao.util;

/**
 * @author xezzon
 */
public class ObjectUtil extends cn.hutool.core.util.ObjectUtil {

  /**
   * 返回对象本身<br>
   * 匿名函数 o -> o 的语法糖
   * 
   * @param object 任意对象
   * @param <T> 对象类型
   * @return 对象本身
   */
  public static <T> T self(T object) {
    return object;
  }
}
