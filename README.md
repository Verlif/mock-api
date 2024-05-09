# MockApi

模拟接口，能够在真实接口或是任何方法上生成对应的模拟接口，不触发业务逻辑而直接返回模拟值。

适合以下场景：

- 产品DEMO，需要大量随机数据，并会随时更新。
- 临时接口，用于做联通测试与数据格式对齐。
- 接口文档生成，直接提供入参与出参格式及格式样例数据，更加直观。

特点：

- 使用方便，使用注解的**侵入**方式，或是使用**非侵入**的手动注入方式来生成虚拟接口。
- 动态结构，生成的数据随着原始方法的更新而自动更新，不需要手动维护。
- 学习成本低，模拟接口被注入到`RequestMappingHandlerMapping`中，允许开发者按照默认的方式管理生成的模拟接口，而不需要学习新的规范规则。

以下内容基于**3.x**版本，以前版本请移步[2.x](/README_2.x.md)介绍。

## 举例

例如你有一个这样的接口方法：

```java
@GetMapping
public BaseResult<User> getById(int id) {
    return null;
}
```

在访问时因为没有实际业务代码，只会返回`null`，而使用**MockApi**则会返回如下内容：

![生成数据](/docs/3.x/imgs/数据对比.png)

*这里使用了mockapi-mock组件*

相比较与一般的数据构造器，**MockApi**是自适应的，当接口返回值发生变化时不需要开发者进行任何调整，模拟接口会自动返回对应结构数据，几乎实现一劳永逸。

## 简单使用

### 侵入式

侵入式的方式最为简单，只需要新建一个`MyObjectMocker`类去实现`ObjectMocker`：

```java
public class MyObjectMocker implements ObjectMocker {
    @Override
    public Object mock(MockItem item, RequestPack pack) {
        // 我们根据请求目标方法的返回值生成对应的对象，并返回实例
        Class<?> returnType = pack.getOldMethod().getReturnType();
        try {
            return returnType.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
```

随后就可以在`MockApi`接口中通过`mocker`参数使用它了：

```java
@RestController("user")
public class UserController {

    @MockApi(mocker = MyObjectMocker.class)
    @GetMapping
    public User getById(String id) {
        return null;
    }

    @GetMapping("batch")
    public List<User> add(User user) {
        return new ArrayList<>();
    }
}
```

此时我们访问`/mock`就会返回一个`User`实例而不是`null`。

### 非侵入式

非侵入式需要用到`PathRecorder`，这是需要构造虚拟地址的记录，只需要向其中`add`方法即可由**MockApi**生成对于的虚拟接口。

例如：

```java
@Component
@AutoConfigureBefore(MockApiRegister.class)
public class OtherRecorder {

    @Autowired
    private PathRecorder pathRecorder;

    @PostConstruct
    public void init() {
        pathRecorder.add(PathRecorder.Path.generate(Test.class));
    }

    interface Test {
        int i();
    }

}
```

这里注意要在`MockApiRegister`注册前进行添加。

这样就可以通过`/mock/test/i`来访问对`Test`接口生成的`i`虚拟方法了。

### 其他更优雅的方式

请移步[开发文档](/docs/3.x/开发文档.md)

当然，你也可以直接使用已经定义好的`mockapi-mock`。

## 使用

**目前的版本基于SpringBoot-2.7.18版本开发**

1. 添加依赖

   添加**Jitpack**仓库，这里不做赘述。

2. 添加依赖

   ```xml
   <dependency>
       <groupId>com.github.verlif.mockapi</groupId>
       <artifactId>mockapi-core</artifactId>
       <!--<artifactId>mockapi-mock</artifactId>-->
       <version>${last-version}</version>
   </dependency>
   ```

   **last-version**  [![](https://jitpack.io/v/Verlif/mockapi.svg)](https://jitpack.io/#Verlif/mockapi)

3. 在需要**mock**的**controller类**上或是**接口方法**上添加`@MockApi`注解

   注解采用就近原则的方式生效。

**完成**
