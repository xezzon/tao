package indi.xezzon.tao.manager;

/**
 * 全局 ID 生成器
 * @author xezzon
 */
public interface IdGenerator {

  /**
   * 生成一个全局 ID
   * @return ID
   */
  String nextId();
}
