# 树形数据结构

`TreeNode` 接口定义了三个属性： `id`、`parentId`、`children`。不过实际上并不关心实现类的属性叫什么，只需要实现对应的接口 `TreeNode#getId()`、`TreeNode#getParentId()`、`TreeNode#getChildren()`、`TreeNode#setChildren(List)` 这些接口就好。

## Example

```java
import java.util.Collection;
import java.util.List;

class Department implements TreeNode<Department, String> {

  private String id;
  private String parentId;
  private List<Department> children;

  // 省略 getter setter
}

class DepartmentServiceImpl {

  private DepartmentDAO departmentDAO;

  /**
   * 自上而下遍历
   */
  public List<Department> topDown(String id) {
    return Tree.topDown(Collections.singleton(id), 1,
        departmentDAO::findByParentIdIn
    );
  }

  /**
   * 自下而上遍历
   */
  public List<Department> bottomUp(Collection<String> ids) {
    return Tree.fold(Tree.bottomUp(ids, -1,
        departmentDAO::findByIdIn
    ));
  }
}
```

## 自上而下遍历（topDown）

通常用于关联查询本级与下级关联数据（如查询部门与子级部门关联的员工）和查询树形数据列表（需要与 [fold](#平铺结构转树形结构fold) 结合使用）。
第一个参数是起始数据 ID；第二个参数是递归层数，在需要懒加载节点时很有用；第三个参数是根据 parentId 查询对象的函数引用。
返回的是一个平铺的对象列表。注意，列表中并不包括起始 ID 对应的对象。

## 自下而上遍历（bottomUp）

通常用于含筛选的列表查询（需要结合 [fold](#平铺结构转树形结构fold) 使用），先筛选出符合条件的节点，然后逐级向上建立树形结构。
该函数的第一个参数是起始 ID；第二个参数是递归层数；第三个参数是根据 ID 查询对象的函数引用。
返回的是一个平铺的对象列表。与 topDown 不同的是，列表中会包括起始 ID 对应的对象。

## 平铺结构转树形结构（fold）

该函数常与 [topDown](#自上而下遍历topdown) 或 [bottomUp](#自下而上遍历bottomup) 组合使用。与 topDown 组合使用常用于支持懒加载的查询树形数据列表，与 bottomUp 组合用于含筛选的树形数据列表查询。
