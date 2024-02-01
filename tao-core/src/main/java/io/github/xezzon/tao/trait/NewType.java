package io.github.xezzon.tao.trait;

/**
 * 参考 Rust 的 NewType 机制
 * @author xezzon
 */
public interface NewType<T> {

  /**
   * 获取原类型以使用其方法
   * @return 原类型的一个对象
   */
  T get();
}
