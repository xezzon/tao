# 事件总线

起源于 [Google Guava EventBus](https://github.com/google/guava/wiki/EventBusExplained) 和 [Spring Event](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/event/EventListener.html)，区别于前两者使用的反射机制，tao 提供的事件总线 ObserverContext 是基于函数式范式定义的。 
虽然个人并不喜欢反射，但不得不承认，在 Spring 的语境下，`Spring Event` 使用起来会很爽。

事件总线的本质是观察者模式，通常用于一个事件的后置处理。

## Example

```java
import io.github.xezzon.tao.Observation;
import io.github.xezzon.tao.ObserverContext;

class RegisterObservation implements Observation {

  private String username;
  private String email;
}

class UserServiceImpl {

  public void register(User user) {
    // ... 用户注册逻辑
    // 后置处理
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
    ObserverContext.register(RegisterObservation.class, this::handle);
    // 必要时需要通过注入自身来注册，否则可能会导致事务/异步等机制失效
    //ObserverContext.register(RegisterObservation.class, service::handle);
  }

  public void handle(RegisterObservation observation) {
    // 处理用户注册后发送消息的逻辑（略）
  }
}

class TeamServiceImpl {

  @PostConstruct
  public void init() {
    // 注册事件观察者
    ObserverContext.register(RegisterObservation.class, this::handle);
  }

  public void handle(RegisterObservation observation) {
    // 处理用户注册后创建团队的逻辑（略）
  }
}
```
