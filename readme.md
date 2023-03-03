# MockApi

模拟接口，用来做调用测试。比较典型的场景就是前后端分离开发时构建的接口文档，`MockApi`就是用来给前端返回模拟数据的。

## 简单说明

不需要前端使用**mock.js**，只需要在接口上配置一个注解，即可生成此接口的**mock接口**用于返回测试数据，例如：

```java
@RestController
@RequestMapping("hello")
public class HelloController {

    @MockResult
    @RequestMapping("echo/{str}")
    public String echo(@PathVariable String str) {
        return str;
    }

    @RequestMapping("hi")
    public String hi() {
        return "hi";
    }
}
```

在`echo`接口上有`MockResult`注解，此时会产生一个新的接口`mock/hello/echo/{str}`用来返回测试数据而不必调用`echo`方法。

例如访问`/hello/echo/你好`会返回`你好`，而访问`/mock/hello/echo/你好`则会返回`fNh`这样的随机字符串。

下方的`hi`接口没有`MockResult`注解，并不会产生新接口，如果访问`/mock/hello/hi`，则会返回**404**错误。

## 配置文件

配置文件说明请参考 [配置文件](docs/配置文件.md)

## 开发文档

需要自定义构造或是更高级的功能请参考 [开发文档](docs/开发文档.md)

## 使用

**！！！注意：目前的版本是可用性测试版本，仅适合学习和试验**
**！！！注意：目前的版本基于SpringBoot 2.6.14版本开发，其他版本会在稳定后适配**

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

**完成**
