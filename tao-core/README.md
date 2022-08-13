# 标准接口定义

## **事件总线**

参照 Google Guava 的 [EventBus](https://github.com/google/guava/wiki/EventBusExplained)
，实现了一个简易的事件总线。不同于 EventBus 的注解 + 反射的实现机制，这里使用的是函数式范式 +
手动注册的方式（可以自行实现通过代理模式或其他方式自动注册）。

在 Spring 体系下，可以使用 [Spring Event](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/event/EventListener.html) 进行替代。

demo 如下：

```java
import indi.xezzon.tao.observer.Observation;
import indi.xezzon.tao.observer.ObserverContext;

class RegisterObservation implements Observation {

  private String username;
  private String email;
}

class UserServiceImpl {

  public void register(User user) {
    // 用户注册逻辑（略）
    RegisterObservation observation = new RegisterObservation();
    ObserverContext.post(observation);
  }
}

class MessageServiceImpl implements MessageService {

  @Resource
  private MessageService service;

  @PostConstruct
  public void init() {
    // 注册事件观察者
    ObserverContext.register(RegisterObservation.class, this::handleRegisterObservation);
    // 必要时需要通过注入自身来注册，否则可能会导致事务/异步等机制失效
    //ObserverContext.register(RegisterObservation.class, service::handleRegisterObservation);
  }

  public void handleRegisterObservation(RegisterObservation observation) {
    // 处理用户注册后发送消息的逻辑（略）
  }
}

class TeamServiceImpl {

  @PostConstruct
  public void init() {
    // 注册事件观察者
    ObserverContext.register(RegisterObservation.class, this::handleRegisterObservation);
  }

  public void handleRegisterObservation(RegisterObservation observation) {
    // 处理用户注册后创建团队的逻辑（略）
  }
}
```

## 通用查询组件

通用查询组件是将查询参数转换为 DSL 然后进行查询。这里只定义了接口，需要根据具体使用的 DSL 框架实现具体类。

以 QueryDSL 实现的 demo 如下：

```java
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Dict;
import com.querydsl.sql.SQLQuery;
import indi.xezzon.tao.domain.CommonQuery;
import indi.xezzon.tao.domain.ICommonQueryAst;

@PersistenceCapable
class DictDO implements ICommonQueryAst<SQLQuery<QDict>> {

  @Override
  public SQLQuery<QDict> toAst(CommonQuery commonQuery, SQLQuery<QDict> query) {
    // 组装查询参数逻辑（略）
    return query;
  }
}

@Repository
@Transactional(rollbackFor = Exception.class)
class DictDAOImpl {

  private static final QDict Q_DICT = QDict.dict;
  @Resource
  private SQLQueryFactory queryFactory;

  public Page<Dict> commonQuery(CommonQuery commonQuery) {
    SQLQuery<QDict> query = commonQuery.toAst(new DictDO(), queryFactory.selectFrom(Q_DICT));
    List<QDict> records = query.fetch();

    Page<Dict> page = new Page<>();
    page.setRecords(BeanUtil.copyToList(records, Dict.class));
  }
}
```

## 字典接口

字典可以分为两类，枚举字典与用户配置的字典。将两者实现统一的 IDict 接口，可以有效减少字典项的配置工作。

在实体类中，使用枚举类是优于使用 String 类型的。首先，使用枚举类可以保证值一定出现在枚举类之间，如果出现了意外值，会直接抛出错误，从而实现
fail-fast 机制。其次，虽然数据库存储使用的是 VARCHAR 类型，但是许多 ORM
框架是可以自动的调用 `valueOf()` 方法将其转换为对应的枚举类的。 SpringMVC
亦是同理，接收参数时返回自动转换为枚举类，返回值可以通过序列化方法，或者返回  `IDict`
接口，做到枚举的自动翻译，`{ "id": "0", "auditStatus": { "tag": "AuditStatusEnum", "code": "PASS", "label": "通过", "ordinal": 2 } }`
。另外，枚举类型还可以简化状态机，在此不做详述。总之，遵循行业内的共识以及使用语法糖是可以享受很多技术红利的。

demo 如下：

```java
import indi.xezzon.tao.dict.DictFactory;
import indi.xezzon.tao.dict.IDict;
import indi.xezzon.tao.domain.CommonQuery;
import java.util.List;

class Bill {

  private String id;
  private AuditStatusEnum auditStatus;
}

enum AuditStatusEnum implements IDict {
  UNAUDITED("待审核"),
  REFUSE("驳回"),
  PASS("通过"),
  ;

  private final String label;

  AuditStatusEnum(String label) {
    this.label = label;
  }

  static {
    DictFactory.register(AuditStatusEnum.class.getSimpleName(), List.of(values()));
  }

  public String getTag() {
    return this.getClass().getSimpleName();
  }

  public String getCode() {
    return this.name();
  }

  public String getLabel() {
    return this.label;
  }

  public int getOrdinal() {
    return this.ordinal();
  }
}

class BillVO {

  private String id;
  private IDict auditStatus;
}

@RestController
@RequestMapping("/bill")
class BillController {

  @GetMapping("/list")
  public Page<BillVO> list(@RequestParam CommonQuery commonQuery) {
    // 略
  }

  @PutMapping("/{id}/audit-status/{status}")
  public void audit(@PathVariable String id, @PathVariable AuditStatusEnum status) {
    // 略
  }
}
```

## 日志接口

## 全局异常类

## 树形结构接口

为以组织架构为例的树形结构定义接口。并为树形结构封装了递归搜索和平铺递归搜索的工具。

以递归搜索工具为例：

```java
import indi.xezzon.tao.domain.TreeNode;
import indi.xezzon.tao.util.NestedUtil;
import java.util.List;

class Department implements TreeNode<Department, String> {

  private String id;
  private String parentId;
  private List<Department> children;

  // 省略 getter setter
}

class DepartmentServiceImpl {

  private DepartmentDAO departmentDAO;

  public List<Department> listNested(String id) {
    return NestedUtil.nest(id, (byte) -1,
        (o) -> departmentDAO.list(new Department().setParentId(o)));
  }
}
```
