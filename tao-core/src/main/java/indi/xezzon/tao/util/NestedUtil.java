package indi.xezzon.tao.util;

import indi.xezzon.tao.domain.TreeNode;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * 递归工具
 * @author xezzon
 */
public class NestedUtil {

  /**
   * 递归获取对象树形结构
   * @param initial 上级ID
   * @param nested 最大递归次数 -1表示不限
   * @param function 通过上级ID获取下一级对象的函数
   * @param getId 通过对象获取id
   * @param callback 获取到本级对象集合后的回调
   * @param <T> 对象类型
   * @param <I> ID 类型
   * @return 对象树型结构
   */
  public static <T, I> List<T> nest(I initial, int nested, Function<I, List<T>> function,
      Function<T, I> getId, BiConsumer<T, List<T>> callback) {
    if (nested > Byte.MAX_VALUE || nested <= Byte.MIN_VALUE) {
      throw new RuntimeException("递归次数过大");
    }
    if (nested == 0) {
      return Collections.emptyList();
    }
    List<T> results = function.apply(initial);
    for (T result : results) {
      List<T> children = nest(getId.apply(result), nested - 1, function, getId, callback);
      callback.accept(result, children);
    }
    return results;
  }

  /**
   * 为 TreeNode 接口重载 {@link NestedUtil#nest(Object, int, Function, Function, BiConsumer)} 接口
   * @param initial 上级ID
   * @param nested 最大递归次数 -1表示不限
   * @param function 通过上级ID获取下一级对象的函数
   * @param <T> 对象类型
   * @param <I> ID 类型
   * @return 对象树型结构
   */
  public static <T extends TreeNode<T, I>, I> List<T> nest(I initial, int nested,
      Function<I, List<T>> function) {
    return nest(initial, nested, function, TreeNode::getId, TreeNode::setChildren);
  }

  /**
   * 递归获取下级所有对象（平铺）
   * @param initial 上级ID
   * @param nested 最大递归次数 -1表示不限
   * @param function 通过上级ID获取下一级对象的函数
   * @param getId 通过对象获取id
   * @param <T> 对象类型
   * @param <I> ID 类型
   * @return 平铺对象集合
   */
  public static <T, I> Set<T> flat(I initial, int nested, Function<I, Set<T>> function,
      Function<T, I> getId) {
    if (nested > Byte.MAX_VALUE || nested <= Byte.MIN_VALUE) {
      throw new RuntimeException("递归次数过大");
    }
    if (nested == 0) {
      return Collections.emptySet();
    }
    Set<T> apply = function.apply(initial);
    Set<T> results = new CopyOnWriteArraySet<>(apply);
    for (T t : apply) {
      Set<T> children = flat(getId.apply(t), nested - 1, function, getId);
      results.addAll(children);
    }
    return results;
  }

  /**
   * 为 TreeNode 接口重载 {@link NestedUtil#flat(Object, int, Function, Function)} 接口
   * @param initial 上级ID
   * @param nested 最大递归次数 -1表示不限
   * @param function 通过上级ID获取下一级对象的函数
   * @param <T> 对象类型
   * @param <I> ID 类型
   * @return 平铺对象集合
   */
  public static <T extends TreeNode<T, I>, I> Set<T> flat(I initial, int nested,
      Function<I, Set<T>> function) {
    return flat(initial, nested, function, TreeNode::getId);
  }
}
