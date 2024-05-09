# MockApi

模拟接口，能够在真实接口或是任何方法上生成对应的模拟接口，不触发业务逻辑而直接返回模拟值。

适合以下场景：

- 产品DEMO，需要大量随机数据，并会随时更新。
- 临时接口，用于做联通测试与数据格式对齐。
- 接口文档生成，直接提供入参与出参格式及格式样例数据，更加直观。

特点：

- 使用方便，使用最简单的**侵入**方式，只需要一个注解即可生成模拟接口并直接调用。亦或是使用**非侵入**方式，直接使用`PathRecorder`来添加目标对象或方法。
- 专注业务开发而非数据生成。在完成方法注册后，**MockApi**会在每次运行时动态匹配对应的数据格式，不需要开发者更改结构后手动调整。
- 多样的数据控制，使用 [数据池配置](docs/2.x/配置文件.md) 能非常快速方便地控制模拟数据的数据内容，也可以通过代码的方式精准控制。
- 学习成本低，模拟接口被注入到`RequestMappingHandlerMapping`中，允许开发者按照默认的方式管理生成的模拟接口，而不需要学习新的规范规则。

## 举例

例如你有一个这样的接口方法：

```java
@GetMapping
public BaseResult<User> getById(Integer id) {
    return null;
}
```

在访问时因为没有实际业务代码，只会返回`null`，而使用**MockApi**则会返回如下内容：

![生成数据](/docs/2.x/imgs/数据对比.png)

相比较与一般的数据构造器，**MockApi**是自适应的，当接口返回值发生变化时不需要开发者进行任何调整，模拟接口会自动返回对应结构数据，几乎实现一劳永逸。

## 工具原理

**MockApi**通过扫描接口注解获取接口信息，根据获得的信息注册新接口到请求路由表，并指向新方法。
因此在访问模拟接口时，服务将不会调用到您的实际方法代码，而是使用**MockApi**的数据生成逻辑，这对您的业务不会造成任何影响。

也正因此，开发者甚至可以直接添加普通方法，例如这样：

```java
@Component
@ConditionalOnMockEnabled
@AutoConfigureBefore(MockApiBuilder.class)
public class MyOtherApiRecord {

   @Autowired
   private PathRecorder pathRecorder;
   @Autowired
   private HelloController helloController;

   @PostConstruct
   public void otherRecord() {
      // 将当前类定义的所有公共方法添加到构建目录
      pathRecorder.add(PathRecorder.Path.EMPTY, PathRecorder.Path.generate(this, PathRecorder.MethodSign.RESULT));
      // 手动将controller接口添加到构建目录，实现非侵入式构建
      PathRecorder.Path[] paths = PathRecorder.Path.generate(
              helloController,
              m -> Modifier.isPublic(m.getModifiers()) && m.getDeclaringClass() == HelloController.class,
              PathRecorder.MethodSign.RESULT);
      // 对所有helloController下的模拟接口进行配置
      for (PathRecorder.Path path : paths) {
         // 增加接口前缀
         path.setPath("hello/" + path.getPath());
         // 开启模拟接口访问日志
         path.setMockItem(new MockItem(true, null, null, null));
         // 只提供GET方式的访问
         path.setRequestMethods(RequestMethod.GET);
      }
      pathRecorder.add(PathRecorder.Path.EMPTY, paths);
      // 增加接口的虚拟接口
      PathRecorder.Path[] apiPaths = PathRecorder.Path.generate(UserApi.class, PathRecorder.MethodSign.RESULT);
      pathRecorder.add(PathRecorder.Path.EMPTY, apiPaths);
   }

   @MockResult(methods = RequestMethod.GET)
   @ResponseBody
   public String wuhu() {
      return "123";
   }

   @MockResult
   @ResponseBody
   public String mock() {
      return "mockTest";
   }
   
   public interface UserApi {

      @MockResult(result = "123", path = "312")
      User getById(String id);

   }
}
```

## 简单使用

### 侵入式

侵入式的方式最为简单，只需要在接口上配置一个注解，即可生成此接口的**mock接口**用于返回测试数据：

- `@MockParams` - 生成入参数据模拟，访问此接口可返回接口入参随机数据。
- `@MockResult` - 生成出参数据模拟，访问此接口可返回方法返回值模拟数据。

例如前面提到的接口：

```java
@MockParams
@MockResult
@GetMapping("id")
public BaseResult<User> getById(Integer id) {
    return null;
}
```

此时访问`/id`则会访问方法而返回`null`，访问生成的`/params/id`则会返回一个`Map`，其中的包括**key**为**id**的随机数，访问`/mock/id`则会返回以下数据：

```json
{
   "code": 200,
   "msg": "有意思的",
   "data": {
      "userId": 861,
      "nickname": "小羊",
      "roleKeys": [
         "USER",
         "VISITOR",
         "ADMIN"
      ],
      "favorites": [
         {
            "name": "梨子",
            "type": "错误的"
         },
         {
            "name": "苹果",
            "type": "有意思的"
         },
         {
            "name": "梨子",
            "type": "有意思的"
         }
      ]
   }
}
```

### 非侵入式

非侵入式需要用到`PathRecorder`，这是需要构造虚拟地址的记录，只需要向其中`add`方法即可由**MockApi**生成对于的虚拟接口，例如在[工具原理](#工具原理)中提到的方式。

## 配置文件

配置文件说明请参考 [配置文件](docs/2.x/配置文件.md)

推荐使用配置文件的方式进行数据池配置进行随机数据控制。

## 开发文档

需要自定义构造或是更高级的功能请参考 [开发文档](docs/2.x/开发文档.md)

## 使用

**目前的版本基于SpringBoot-2.7.18版本开发**

1. 添加依赖

   添加**Jitpack**仓库，这里不做赘述。

2. 添加依赖

   ```xml
   <dependency>
       <groupId>com.github.Verlif</groupId>
       <artifactId>mock-api</artifactId>
       <version>2.7.18-2.1</version>
   </dependency>
   ```

3. 在需要**mock**的**controller类**上或是**接口方法**上添加`@MockResult`注解

   注解采用就近原则的方式生效。

   ```java
   @MockParams
   @MockResult
   @RestController
   @RequestMapping("user")
   public class UserController {
   
       @GetMapping
       public BaseResult<User> getById(Integer id) {
           return new OkResult<>(new User());
       }
   
       @GetMapping("list")
       public BaseResult<List<User>> getList() {
           return new OkResult<>(new ArrayList<>());
       }
   
       @MockResult
       @PutMapping
       public BaseResult<User> update(User user) {
           return new OkResult<>(user);
       }
   
       @DeleteMapping
       public BaseResult<String> deleteById(Integer id) {
           return new OkResult<>("OK");
       }
   
   }
   ```

4. 进行结果**mock**

   使用**GET**方式访问`127.0.0.1:8080/mock/user`（在默认虚拟地址生成器情况下，也可自定义地址生成器），并会返回以下结果：
   
   ```json
   {
       "code": 201,
       "msg": "nk-ww",
       "data": {
           "userId": 4,
           "nickname": "小红",
           "roleKey": "VISITOR"
       }
   }
   ```

**完成**

更多说明请参阅[使用说明](docs/2.x/使用说明.md)
