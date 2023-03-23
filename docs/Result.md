# 可恢复异常

有些时候我们遇到异常，并不想让程序中断。尤其是遍历操作的时候，我们希望遍历完所有的项，将其中错误的结果告知用户，而不是抛出异常后触发事务回滚。此时 Result 就是一个好的选择。

demo 如下:

```java
public void TemplateService {
  
  @Resource
  private OssService ossService;
    
  public void generate(List<Template> templates) {
    List<Result<Void, BaseException>> results = templates.stream()
      .map((template) -> {
        try {
          ossService.save(template);
          return Result.ok(null);
        } catch (BaseException e) {
          return Result.err(e)
        }
      })
    // 处理 result
  }
}
```

注意：不建议直接将 Result 向上抛，而是经由调用方处理。
