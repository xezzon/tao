package io.github.xezzon.tao.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

/**
 * @author xezzon
 */
public class Tree {

  /**
   * 从 initial 开始 逐级向下遍历 nested 级节点（不包括 initial）
   * @param initial 初始ID
   * @param nested 最大递归次数 不限递归次数可设为-1
   * @param findByParentIdIn 通过父级ID获取子级对象的函数
   * @param <T> 对象类型
   * @param <ID> ID 类型
   * @return 平铺的对象集合
   */
  public static <T extends TreeNode<T, ID>, ID> List<T> topDown(
      final Collection<ID> initial,
      int nested,
      @NotNull Function<Collection<ID>, Collection<T>> findByParentIdIn
  ) {
    if (nested == 0) {
      return Collections.emptyList();
    }
    if (initial.isEmpty()) {
      return Collections.emptyList();
    }
    Collection<T> children = findByParentIdIn.apply(initial);
    List<T> results = new CopyOnWriteArrayList<>(children);
    Set<ID> childIds = children.parallelStream()
        .map(TreeNode::getId)
        .collect(Collectors.toSet());
    List<T> grandChildren = topDown(childIds, nested - 1, findByParentIdIn);
    results.addAll(grandChildren);
    return results;
  }

  /**
   * 从 initial 开始 逐级向上遍历 nested 级节点（包括 initial）
   * @param initial 初始对象
   * @param nested 最大递归次数 不限递归次数可设为-1
   * @param findByIdIn 通过ID获取对象的函数
   * @param <T> 对象类型
   * @param <ID> ID 类型
   * @return 平铺对象集合
   */
  public static <T extends TreeNode<T, ID>, ID> List<T> bottomUp(
      final Collection<T> initial,
      int nested,
      @NotNull Function<Collection<ID>, Collection<T>> findByIdIn
  ) {
    List<T> result = new CopyOnWriteArrayList<>();
    Collection<T> incremental = initial;
    do {
      if (nested == 0 || nested < Byte.MIN_VALUE) {
        break;
      }
      result.addAll(incremental);
      Set<ID> idSet = result.parallelStream()
          .map(TreeNode::getId)
          .collect(Collectors.toSet());
      Set<ID> parentsId = incremental.parallelStream()
          .map(TreeNode::getParentId)
          .filter(parentId -> !idSet.contains(parentId))
          .collect(Collectors.toSet());
      if (parentsId.isEmpty()) {
        break;
      }
      incremental = findByIdIn.apply(parentsId);
    } while (!incremental.isEmpty());
    return result;
  }

  /**
   * 筛选树中最顶级的节点（在树是不完整的情况下，最顶级节点并不一定是根节点）
   * @param tree 树型数据
   * @return 顶级节点
   */
  public static <T extends TreeNode<T, ?>> List<T> top(Collection<T> tree) {
    Set<?> ids = tree.parallelStream()
        .map(TreeNode::getId)
        .collect(Collectors.toSet());
    return tree.parallelStream()
        .filter(node -> !ids.contains(node.getParentId()))
        .collect(Collectors.toList());
  }

  /**
   * 递归设置下级节点（面向过程写法）
   * @param tree 所有对象（或所有对象的局部）
   */
  public static <T extends TreeNode<T, ?>> void build(
      @NotNull final Collection<T> roots,
      @NotNull final Collection<T> tree
  ) {
    for (T root : roots) {
      List<T> children = tree.parallelStream()
          .filter(node -> Objects.equals(node.getParentId(), root.getId()))
          .toList();
      if (children.isEmpty()) {
        continue;
      }
      build(children, tree);
      root.setChildren(children);
    }
  }

  /**
   * 平铺结构转树形结构
   * @param list 平铺结构数据集
   * @param <T> 元素类型
   * @return 树形结构数据集
   */
  public static <T extends TreeNode<T, ?>> List<T> fold(Collection<T> list) {
    List<T> roots = top(list);
    build(roots, list);
    return roots;
  }

  /**
   * 树形结构转平铺结构
   * @param roots 树形结构数据集
   * @param <T> 元素类型
   * @return 平铺结构数据集
   */
  public static <T extends TreeNode<T, ?>> List<T> flatten(Collection<T> roots) {
    if (roots.isEmpty()) {
      return Collections.emptyList();
    }
    List<T> children = roots.parallelStream()
        .map(TreeNode::getChildren)
        .filter(Objects::nonNull)
        .flatMap(List::parallelStream)
        .toList();
    for (T root : roots) {
      root.setChildren(null);
    }
    List<T> grandchild = flatten(children);
    List<T> result = new ArrayList<>(grandchild);
    result.addAll(roots);
    return result;
  }
}
