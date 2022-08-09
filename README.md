# 

> 道(Tao)生一，一生二，二生三，三生万物。

## 动机

受 spring-data-jdbc 和 [Tokio](https://github.com/tokio-rs/tokio) 等项目的启发，我尝试定义一些 Web 开发的“标准”接口，然后为不同选型实现这些接口（例如 ID 生成器，可以用 Redis、雪花、UUID 等方式实现），从而可以以插件组装的方式组织项目。

## 代码结构

### [tao-core](./tao-core/README.md)

通用 Web 开发接口定义。

- `observer`: 事件总线
- `dict`: 字典接口
- `domain.ICommonQueryAst`: 通用查询接口
- `exception.BaseException`: 业务异常基类
- `logger.LogRecord`: 日志注解

### [tao-spring-boot-starter](./tao-spring-boot-starter/README.md)

依赖于 SpringBoot 的功能增强。

- `logger`: 依据 [如何优雅地记录操作日志？](https://tech.meituan.com/2021/09/16/operational-logbook.html) 实现的日志切面。
- `manager.Idgenerator`: 全局 ID 生成器接口定义。

## 快速开始

### Maven

```xml
<project>
  <!-- ... -->
  <dependencies>
    <dependency>
      <groupId>indi.xezzon</groupId>
      <artifactId>tao-spring-boot-starter</artifactId>
      <version>[VERSION]</version><!-- 这里替换为对应的版本 -->
    </dependency>
  </dependencies>
  <!-- 因为制品暂时没有发布到官方的中央仓库，所以需要添加 repository 到 pom.xml 或 settings.xml -->
  <repositories>
    <repository>
      <id>tao</id>
      <name>tao-spring-boot-starter GitHub repository</name>
      <url>https://maven.pkg.github.com/xezzon/tao-spring-boot-starter</url>
    </repository>
  </repositories>
</project>
```
