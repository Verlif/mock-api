# 虚拟接口-mock分支

mock分支是为了能更快速地使用虚拟接口，并生成规范化的数据而提供的一个组件分支。

## 使用

使用方法与2.x版本相同，只需要使用这两个注解即可生效：

- `@MockResult` - 生成方法返回值的虚拟数据
- `@MockParams` - 生成方法入参的虚拟数据

开发者只需要在接口上下文后，方法接口前分别增加`/result`和`/params`作为地址装饰接口访问对应的虚拟接口。

例如有一个上下文是`/demo`的项目，其中一个`controller`是这样的：

```java
@RequestMapping("test")
@RestController
public class TestController implements ApplicationRunner {

    @MockResult
    @GetMapping
    public String hello() {
        return "Hello world";
    }

}
```

这里访问`/demo/test`会返回`Hello world`，而访问`/demo/result/test`则会返回一个随机字符串。

## 数据格式化

数据格式化推荐使用[配置文件](../docs/3.x/配置文件.md)的方式。
