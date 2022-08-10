package indi.xezzon.tao.domain;

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
   * 设置下级对象集合
   * @param children 下级对象集合
   */
  void setChildren(List<T> children);

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
