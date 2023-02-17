package io.github.xezzon.tao.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
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
    DATA_SET.add(new Menu().setId("1").setParentId("0"));
    DATA_SET.add(new Menu().setId("2").setParentId("0"));
    DATA_SET.add(new Menu().setId("3").setParentId("0"));
    DATA_SET.add(new Menu().setId("11").setParentId("1"));
    DATA_SET.add(new Menu().setId("12").setParentId("1"));
    DATA_SET.add(new Menu().setId("13").setParentId("1"));
    DATA_SET.add(new Menu().setId("21").setParentId("2"));
    DATA_SET.add(new Menu().setId("22").setParentId("2"));
    DATA_SET.add(new Menu().setId("121").setParentId("11"));
    DATA_SET.add(new Menu().setId("122").setParentId("11"));
    DATA_SET.add(new Menu().setId("131").setParentId("13"));
    DATA_SET.add(new Menu().setId("1211").setParentId("121"));
    DATA_SET.add(new Menu().setId("1221").setParentId("122"));
    DATA_SET.add(new Menu().setId("1222").setParentId("122"));
    DATA_SET.add(new Menu().setId("1311").setParentId("131"));
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
  void top() {
    Assertions.assertEquals(3, Tree.top(DATA_SET).size());
  }
}

class Menu implements TreeNode<Menu, String> {
  String id;
  String parentId;
  List<Menu> children;

  public Menu setId(String id) {
    this.id = id;
    return this;
  }

  public Menu setParentId(String parentId) {
    this.parentId = parentId;
    return this;
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
  public String toString() {
    return "Menu{" + "id='" + id + '\''
        + ", parentId='" + parentId + '\''
        + ", children=" + children
        + '}';
  }
}