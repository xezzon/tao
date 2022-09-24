package indi.xezzon.tao.util;

import indi.xezzon.tao.domain.TreeNode;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

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
  public static <T, I> List<T> nest(
      I initial,
      int nested,
      @NotNull Function<I, List<T>> function,
      @NotNull Function<T, I> getId,
      @NotNull BiConsumer<T, List<T>> callback
  ) {
    if (nested > Byte.MAX_VALUE || nested <= Byte.MIN_VALUE) {
      throw tooDeepRecursionLevels();
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
  public static <T extends TreeNode<T, I>, I> List<T> nest(
      I initial,
      int nested,
      @NotNull Function<Collection<I>, Collection<T>> function
  ) {
    List<T> apply = new CopyOnWriteArrayList<>(function.apply(Collections.singleton(initial)));
    Set<T> tree = flat(Collections.singleton(initial), nested, function);
    apply.forEach(t -> t.setChildrenNested(tree));
    return apply;
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
  public static <T, I> Set<T> flat(
      I initial,
      int nested,
      @NotNull Function<I, Collection<T>> function,
      @NotNull Function<T, I> getId
  ) {
    if (nested > Byte.MAX_VALUE || nested <= Byte.MIN_VALUE) {
      throw tooDeepRecursionLevels();
    }
    if (nested == 0) {
      return Collections.emptySet();
    }
    Collection<T> apply = function.apply(initial);
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
  public static <T extends TreeNode<T, I>, I> Set<T> flat(
      final Collection<I> initial,
      int nested,
      @NotNull Function<Collection<I>, Collection<T>> function
  ) {
    if (nested > Byte.MAX_VALUE || nested <= Byte.MIN_VALUE) {
      throw tooDeepRecursionLevels();
    }
    if (nested == 0) {
      return Collections.emptySet();
    }
    if (initial.isEmpty()) {
      return Collections.emptySet();
    }
    Collection<T> apply = function.apply(initial);
    Set<T> results = new CopyOnWriteArraySet<>(apply);
    Set<I> applyIds = apply.parallelStream()
        .map(TreeNode::getId)
        .collect(Collectors.toSet());
    Set<T> children = flat(applyIds, nested - 1, function);
    results.addAll(children);
    return results;
  }

  private static IndexOutOfBoundsException tooDeepRecursionLevels() {
    return new IndexOutOfBoundsException("递归次数过大");
  }
}
