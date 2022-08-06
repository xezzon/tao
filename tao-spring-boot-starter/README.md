# 基于 SpringBoot 的增强工具

## 基于日志注解 + AOP + SpEL 表达式的日志

参照 [如何优雅地记录操作日志？](https://tech.meituan.com/2021/09/16/operational-logbook.html) 实现的简易日志切面。

demo 如下：

```java
import indi.xezzon.tao.logger.LogRecord;

class UserServiceImpl {

  @LogRecord(value = "用户 #{#user.username} 登陆")
  void login(User user) {

  }
}
```

## 全局 ID 生成器

定义全局 ID 生成器接口，具体实现类可以以不同方式（Redis、雪花、UUID等）实现该接口。使用时可以通过指定类或条件注入的方式指定具体的实现类。通常用于一个系统内，不同表/不同环境使用不同逻辑的 ID 生成器。

例如，生产环境使用 Redis ，而单元测试时，为了减少外部系统的依赖，可以使用雪花替代。 demo 如下：

```java
import indi.xezzon.tao.manager.IdGenerator;

// 方式一： 条件注入
@Component
@ConditionalOnProperty(name = "this.bean.id-generator", havingValue = "redis", matchIfMissing = true)
class RedisIdGenerator implements IdGenerator {

  @Override
  public String nextId() {
    // Redis 的 incr 命令 实现逻辑略
    return null;
  }
}

class UserServiceImpl {

  // 方式二： 指定实现类
  @Resource(type = RedisIdGenerator.class)
  private IdGenerator idGenerator;
}

class DictServiceImpl {

}
```
