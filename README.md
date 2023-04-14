# 道(Tao)

> 道生一，一生二，二生三，三生万物。

tao 是一组针对 Java Web 开发定义的接口和组件。它向开发者提供了以下接口，并基于这些接口实现了一些常用的功能：

* [初级查询组件](./docs/retrieval.md)
* [日志注解](./docs/logger.md)
* [树形数据结构](./docs/tree.md)
* [事件总线](./docs/observer.md)
* [NewType 代理接口](./docs/NewType.md)
* [基础异常类](./docs/exception.md)

## 项目结构

- [tao-core](./tao-core): 核心包，定义抽象接口，几乎没有大型的外部依赖。
- [tao-spring-boot-starter](./tao-spring-boot-starter): 依赖于 spring-boot，对核心组件中定义的接口进行扩展和实现。
- [tao-addon-jpa](https://github.com/xezzon/tao-addon-jpa): 依赖于 JPA，对核心组件中定义的接口进行扩展和实现。

## 快速开始

### Maven

```xml
<project>
  <dependencies>
    <!-- 如果需要spring-boot扩展 那么添加如下依赖 -->
    <dependency>
      <groupId>io.github.xezzon</groupId>
      <artifactId>tao-spring-boot-starter</artifactId>
      <version>1.0-SNAPSHOT</version>
    </dependency>
    <!-- 如果仅需要核心包 那么添加如下依赖（tao-spring-boot-starter 会间接引入 tao-core） -->
    <dependency>
      <groupId>io.github.xezzon</groupId>
      <artifactId>tao-core</artifactId>
      <version>1.0-SNAPSHOT</version>
    </dependency>
  </dependencies>
</project>
```
