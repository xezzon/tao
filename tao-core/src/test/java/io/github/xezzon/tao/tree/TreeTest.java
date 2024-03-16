package io.github.xezzon.tao.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author xezzon
 */
class TreeTest {

  public static final List<Menu> DATA_SET = new ArrayList<>();

  @BeforeEach
  void setUp() {
    DATA_SET.clear();
    DATA_SET.add(new Menu("1", "0"));
    DATA_SET.add(new Menu("2", "0"));
    DATA_SET.add(new Menu("3", "0"));
    DATA_SET.add(new Menu("11", "1"));
    DATA_SET.add(new Menu("12", "1"));
    DATA_SET.add(new Menu("13", "1"));
    DATA_SET.add(new Menu("21", "2"));
    DATA_SET.add(new Menu("22", "2"));
    DATA_SET.add(new Menu("121", "11"));
    DATA_SET.add(new Menu("122", "11"));
    DATA_SET.add(new Menu("131", "13"));
    DATA_SET.add(new Menu("1211", "121"));
    DATA_SET.add(new Menu("1221", "122"));
    DATA_SET.add(new Menu("1222", "122"));
    DATA_SET.add(new Menu("1311", "131"));
  }

  static List<Menu> listWithParentId(Collection<String> parentIds) {
    return DATA_SET.parallelStream()
        .filter(menu -> parentIds.contains(menu.getParentId()))
        .collect(Collectors.toList());
  }

  static List<Menu> listById(Collection<String> ids) {
    return DATA_SET.parallelStream()
        .filter(menu -> ids.contains(menu.getId()))
        .collect(Collectors.toList());
  }

  @Test
  void topDown() {
    List<Menu> menus1 = Tree.topDown(Collections.singleton("0"), -1, TreeTest::listWithParentId);
    Assertions.assertEquals(15, menus1.size());
    List<Menu> menus2 = Tree.topDown(Collections.singleton("1"), -1, TreeTest::listWithParentId);
    Assertions.assertEquals(10, menus2.size());
    List<Menu> menus3 = Tree.topDown(Collections.singleton("0"), 2, TreeTest::listWithParentId);
    Assertions.assertEquals(8, menus3.size());
  }

  @Test
  void bottomUp() {
    Set<Menu> bottomMenus = DATA_SET.parallelStream()
        .filter(menu -> Set.of("1211", "1222", "131", "22").contains(menu.getId()))
        .collect(Collectors.toSet());
    List<Menu> menus = Tree.bottomUp(bottomMenus, -1, TreeTest::listById);
    Assertions.assertEquals(10, menus.size());
  }

  @Test
  void fold_flatten() {
    TreeList<Menu> menuTree = TreeList.from(DATA_SET);
    int i = 0;
    List<Menu> nodes = menuTree.get();
    while (!nodes.isEmpty()) {
      List<Menu> children = new ArrayList<>();
      for (Menu menu : nodes) {
        Assertions.assertEquals(DATA_SET.get(i), menu);
        if (menu.getChildren() != null) {
          children.addAll(menu.getChildren());
        }
        i++;
      }
      nodes = children;
    }

    List<Menu> menuList = menuTree.into();
    Assertions.assertIterableEquals(DATA_SET, menuList);
  }
}

class Menu implements TreeNode<Menu, String> {

  String id;
  String parentId;
  List<Menu> children;

  Menu(String id, String parentId) {
    super();
    this.id = id;
    this.parentId = parentId;
  }

  @Override
  public String getId() {
    return this.id;
  }

  @Override
  public String getParentId() {
    return this.parentId;
  }

  @Override
  public List<Menu> getChildren() {
    return this.children;
  }

  @Override
  public void setChildren(List<Menu> children) {
    this.children = children;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Menu menu = (Menu) o;
    return Objects.equals(id, menu.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
