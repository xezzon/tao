package io.github.xezzon.tao.tree;

import java.util.List;

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
   * 父级 ID
   * @return 父级 ID
   */
  I getParentId();

  /**
   * 设置子级
   * @param children 子级对象集合
   */
  void setChildren(List<T> children);

  /**
   * 获取子级
   * @return 子级对象列表
   */
  List<T> getChildren();

  /**
   * 判断是否叶子节点
   * @return 是否叶子节点
   */
  default boolean isLeaf() {
    return this.getChildren() == null;
  }
}
