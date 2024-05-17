# 入参模拟

## 简述

还在为测试接口时构造入参数据苦恼吗？**mockapi-arg**能自动为您的接口入参构建数据，不再需要反复手动传入参数了。

## 示例

**mockapi-arg**是一个用于测试的组件，能自动填充接口参数，比如有下面这样的接口：

```java
@GetMapping("id")
public String id(@RequestParam("id") String id, @RequestParam("name") String name) {
    return id + " - " + name;
}
```

调用方只能填写`id`和`name`才可以对方法进行调用，而使用了**mockapi-arg**，像这样：

```java
@GetMapping("id")
public String id(
        @MockArg(mocker = MyArgMocker.class) @RequestParam("id") String id,
        @MockArg @RequestParam("name") String name) {
    return id + " - " + name;
}
```

调用方则可以不需要传入任何参数，方法则会自动为`id`和`name`赋值，并调用方法。

## 使用

**目前的版本基于SpringBoot-2.7.18版本开发**

1. 添加依赖

   添加**Jitpack**仓库，这里不做赘述。

2. 添加依赖

   ```xml
   <dependency>
       <groupId>com.github.verlif.mockapi</groupId>
       <artifactId>mockapi-arg</artifactId>
       <version>${last-version}</version>
   </dependency>
   ```

   **last-version**  [![](https://jitpack.io/v/Verlif/mockapi.svg)](https://jitpack.io/#Verlif/mockapi)
