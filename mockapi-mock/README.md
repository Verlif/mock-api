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
public class TestController {

    @MockResult
    @GetMapping
    public String hello() {
        return "Hello world";
    }

}
```

这里访问`/demo/test`会返回`Hello world`，而访问`/demo/result/test`则会返回一个随机字符串。

## 数据配置

数据配置有三种方式，一种是通过直接更改全局`MockDataCreator`，这种方式更改对所有的数据生成都会生效：

```java
@Configuration
public class MockApiConfiguration {

    @Autowired
    private MockDataCreator mockDataCreator;

    @PostConstruct
    public void resetMockDataCreator() {
        // 对用户ID进行递增id分配
        mockDataCreator.fieldValue(User::getUserId, new ContinuousIntPool());
        mockDataCreator.getConfig().arraySize(cla -> RandomUtil.range(1, 5));
    }
}
```

一种是通过分类配置文件，这里使用`@MockResult(data = "a")`就可以使用下面的`a`方法返回的配置：

```java
@Configuration
public class MockApiConfiguration {

    @Bean
    public MockDataConfig a() {
        return new MockDataConfig().arraySize(cla -> RandomUtil.range(1, 5));
    }

    @Bean
    public MockDataConfig b() {
        return (MockDataConfig) new MockDataConfig()
                .arraySize(cla -> RandomUtil.nextInt(3) + 2)
                .fieldObject(String.class, "固定String");
    }
}
```

一种是配置文件的方式：

```yaml
mock-api:
  data:
    enabled: true
    pool:
      - types: String
        values: 这里是mock数据, it's the mocked data.
```

具体内容可以参考[配置文件](../docs/3.x/配置文件.md)。
