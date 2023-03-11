# MockApi

模拟接口，用来做调用测试。比较典型的场景就是前后端分离开发时构建的接口文档，`MockApi`就是用来给前端返回模拟数据的。

有什么会比真实返回值更直观呢，`MockApi`能提供真实的数据结构，并随着接口自动更新。

## 简单说明

只需要在接口上配置一个注解，即可生成此接口的**mock接口**用于返回测试数据。

**MockApi**目前支持入参数据构建与返回值数据构建，例如有一个接口
`public BaseResult<List<User>> getList(UserQuery query)`，地址是`/list`。
在添加了`@MockParams`后，可以通过访问`/params/list`地址返回以下数据：

```json
{
    "nickname": "小猪"
}
```

这就是入参的`UserQuery`对象json。

在添加了`@MockResult`后，可以通过访问`/mock/list`地址返回以下数据：

```json
{
    "code": 201,
    "msg": "123",
    "data": [
        {
            "userId": 681,
            "nickname": "小羊",
            "roleKey": "USER"
        },
        {
            "userId": 416,
            "nickname": "小羊",
            "roleKey": "ADMIN"
        },
        {
            "userId": 18,
            "nickname": "小猫",
            "roleKey": "ADMIN"
        },
        {
            "userId": 147,
            "nickname": "小明",
            "roleKey": "USER"
        },
        {
            "userId": 946,
            "nickname": "小猫",
            "roleKey": "VISITOR"
        }
    ]
}
```

这就是`BaseResult<List<User>>`的数据。

## 配置文件

配置文件说明请参考 [配置文件](docs/配置文件.md)

## 开发文档

需要自定义构造或是更高级的功能请参考 [开发文档](docs/开发文档.md)

## 使用

**！！！注意：目前的版本是可用性测试版本，仅适合学习和试验**

**！！！注意：目前的版本基于SpringBoot2.6.14版本开发，理论上支持2.6.x，其他版本会在稳定后适配**

1. 添加依赖

   添加**Jitpack**仓库，这里不做赘述。

2. 添加依赖

   ```xml
   <dependency>
       <groupId>com.github.Verlif</groupId>
       <artifactId>mock-api</artifactId>
       <version>${mockapi.version}</version>
   </dependency>
   ```

   **version**  [![](https://jitpack.io/v/Verlif/mock-api.svg)](https://jitpack.io/#Verlif/mock-api)

3. 添加`@EnableMockApi`注解启用

   ```java
   @EnableMockApi
   @SpringBootApplication
   public class Application extends SpringBootServletInitializer {
   
       public static void main(String[] args) {
           SpringApplication.run(Application.class, args);
       }
   }
   ```

4. 在需要**mock**的**controller类**上或是**接口方法**上添加`@MockResult`注解

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
   
       @MockResult(cacheable = true)
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

5. 进行结果**mock**

访问`127.0.0.1:8080/mock/user`，则调用`getById`的**mock**方法，并返回以下结果：

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

更多说明请参阅[使用说明](docs/使用说明.md)
