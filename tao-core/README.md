# 标准接口定义

## 日志接口

## 字典接口

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

## 全局异常类

## **事件总线**

参照 Google Guava 的 [EevntBus](https://github.com/google/guava/wiki/EventBusExplained) ，实现了一个简易的事件总线。不同于 EventBus 的注解 + 反射的实现机制，这里使用的是函数式范式 + 手动注册的方式（可以自行实现通过代理模式或其他方式自动注册）。

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

class MessageServiceImpl {
  @PostConstruct
  public void init() {
    // 注册事件观察者
    ObserverContext.register(RegisterObservation.class, this::handleRegisterObservation);
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
