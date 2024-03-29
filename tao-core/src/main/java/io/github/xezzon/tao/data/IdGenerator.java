package io.github.xezzon.tao.data;

/**
 * 全局 ID 生成器
 * @author xezzon
 */
@FunctionalInterface
public interface IdGenerator {

  /**
   * 生成一个全局 ID
   * @return ID
   */
  String nextId();
}
