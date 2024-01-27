package io.github.xezzon.tao.rs;

/**
 * 类型转换
 * @author xezzon
 * @param <S> 源类型
 * @param <T> 目标类型
 */
public interface From<S, T> {

  T from(S source);
}
