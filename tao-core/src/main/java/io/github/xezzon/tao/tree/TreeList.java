package io.github.xezzon.tao.tree;

import io.github.xezzon.tao.trait.Into;
import io.github.xezzon.tao.trait.NewType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

public class TreeList<T extends TreeNode<T, ?>>
    implements NewType<List<T>>, Into<List<T>> {

  @NotNull
  private final List<T> value;

  public TreeList() {
    this.value = new ArrayList<>();
  }

  public TreeList(@NotNull List<T> value) {
    this.value = value;
  }

  /**
   * 列表转树
   * 该方法会修改元素
   * @param list 列表
   * @param <T> 树形结构节点
   * @return 树
   */
  public static <T extends TreeNode<T, ?>> TreeList<T> from(List<T> list) {
    List<T> roots = top(list);
    List<T> nodes = roots;
    while (!nodes.isEmpty()) {
      for (T node : nodes) {
        List<T> children = list.parallelStream()
            .filter(item -> Objects.equals(item.getParentId(), node.getId()))
            .collect(Collectors.toList());
        node.setChildren(children.isEmpty() ? null : children);
      }
      nodes = nodes.parallelStream()
          .map(TreeNode::getChildren)
          .filter(Objects::nonNull)
          .flatMap(List::stream)
          .toList();
    }
    return new TreeList<>(roots);
  }

  @Override
  public List<T> get() {
    return this.value;
  }


  /**
   * 树转列表
   * @return 列表
   */
  @Override
  public List<T> into() {
    List<T> list = new ArrayList<>();
    List<T> children = this.value;
    while (!children.isEmpty()) {
      list.addAll(children);
      children = children.parallelStream()
          .map(TreeNode::getChildren)
          .filter(Objects::nonNull)
          .flatMap(List::stream)
          .toList();
    }
    return list;
  }

  /**
   * 筛选树中的顶级节点
   * @return 列表
   */
  private static <T extends TreeNode<T, ?>> List<T> top(List<T> list) {
    Set<?> ids = list.parallelStream()
        .map(TreeNode::getId)
        .collect(Collectors.toSet());
    return list.parallelStream()
        .filter(node -> !ids.contains(node.getParentId()))
        .collect(Collectors.toList());
  }
}
