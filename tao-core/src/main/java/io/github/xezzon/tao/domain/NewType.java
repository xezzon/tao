package io.github.xezzon.tao.domain;

/**
 * 参考 Rust 的 NewType 机制
 * @author xezzon
 * @deprecated {@link io.github.xezzon.tao.trait.NewType}
 */
@Deprecated(since = "1.0.0")
public interface NewType<T> {

  /**
   * 获取原类型以使用其方法
   * @return 原类型的一个对象
   */
  T get();
}
