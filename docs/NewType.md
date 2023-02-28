# NewType 代理机制

这个组件的起源是我想用 QueryDSL 扩展 JPA 接口，却又不希望侵入 Service 层（指的就是 MybatisPlus）。后来受到了 [Rust 的 NewType 机制](https://course.rs/advance/into-types/custom-type.html) 的启发（我是 Rust 的脑残粉，除了 NewType，[Result](./Result.md) 也是受 Rust 的启发）。

NewType 接口其实并没有做任何事情，这也符合 tao 的定位 —— 关心抽象超过实现。如上所言，[tao-addon-jpa](https://github.com/xezzon/tao-addon-jpa) 中的 `JpaWrapper` 继承了 `NewType` 接口，该接口的实现类定义 QueryDSL 的扩展方法，并且可以调用 `NewType#get()` 方法拿到原生的 JPA 接口并调用其方法。

如果用户也有类似这样的需求，便可以考虑使用 NewType 接口。
