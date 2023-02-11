package io.github.xezzon.tao.domain;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

/**
 * 树形结构
 * @param <T> 实现类类型
 * @param <I> ID 类型
 * @author xezzon
 */
public interface TreeNode<T extends TreeNode<T, I>, I> {

  /**
   * 获取 ID
   * @return ID
   */
  I getId();

  /**
   * 上级 ID
   * @return 上级 ID
   */
  I getParentId();

  /**
   * 设置下级对象集合
   * @param children 下级对象集合
   */
  void setChildren(List<T> children);

  /**
   * 递归设置下级节点
   * @param tree 所有对象（或所有对象的局部）
   */
  default void setChildrenNested(@NotNull Set<T> tree) {
    List<T> children = tree.parallelStream()
        .filter(node -> Objects.equals(node.getParentId(), this.getId()))
        .toList();
    if (children.isEmpty()) {
      return;
    }
    this.setChildren(children);
    children.forEach(child -> child.setChildrenNested(tree));
  }

  /**
   * 获取下一级对象列表
   * @return 下一级对象列表
   */
  List<T> getChildren();

  /**
   * 判断是否叶子节点
   * @return 是否叶子节点 true:是;false:否;
   */
  default boolean isLeaf() {
    return this.getChildren() == null;
  }

  /**
   * 递归查询子节点并展平
   * @param initial 上级ID
   * @param nested 最大递归次数 -1表示不限
   * @param function 通过上级ID获取下一级对象的函数
   * @param <T> 对象类型
   * @param <I> ID 类型
   * @return 平铺对象集合
   */
  static <T extends TreeNode<T, I>, I> Set<T> flat(
      final Collection<I> initial,
      int nested,
      @NotNull Function<Collection<I>, Collection<T>> function
  ) {
    if (nested == 0) {
      return Collections.emptySet();
    }
    if (initial.isEmpty()) {
      return Collections.emptySet();
    }
    Collection<T> children = function.apply(initial);
    Set<T> results = new CopyOnWriteArraySet<>(children);
    Set<I> childIds = children.parallelStream()
        .map(TreeNode::getId)
        .collect(Collectors.toSet());
    Set<T> grandChildren = flat(childIds, nested - 1, function);
    results.addAll(grandChildren);
    return results;
  }

  /**
   * 递归获取树形结构数据
   * @param initial 上级ID
   * @param nested 最大递归次数 -1表示不限
   * @param function 通过上级ID获取下一级对象的函数
   * @param <T> 对象类型
   * @param <I> ID 类型
   * @return 对象树型结构
   */
  static <T extends TreeNode<T, I>, I> List<T> nest(
      I initial,
      int nested,
      @NotNull Function<Collection<I>, Collection<T>> function
  ) {
    List<T> children = new CopyOnWriteArrayList<>(function.apply(Collections.singleton(initial)));
    Set<I> childIds = children.parallelStream()
        .map(TreeNode::getId)
        .collect(Collectors.toSet());
    Set<T> tree = flat(childIds, nested, function);
    return children.parallelStream()
        .peek(t -> t.setChildrenNested(tree))
        .toList();
  }
}
