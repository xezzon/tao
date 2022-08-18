package indi.xezzon.tao.domain;

import java.util.List;
import java.util.Objects;
import java.util.Set;
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
    List<T> children = this.getChildren();
    return children == null || children.isEmpty();
  }
}
