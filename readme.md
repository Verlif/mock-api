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

## 注意

目前是测试项目，并不提供依赖服务。有需要可以将此项目**clone**下来。