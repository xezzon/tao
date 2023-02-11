# 道(Tao)

> 道生一，一生二，二生三，三生万物。

## 动机

通过定义抽象，以定式完成大部分场景的编码工作，减少程序员水平、习惯的参差带来的心智负担，将注意力更加集中在代码与架构的设计上。

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
      <groupId>io.github.xezzon</groupId>
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
