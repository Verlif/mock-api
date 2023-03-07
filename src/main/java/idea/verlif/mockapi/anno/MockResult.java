package idea.verlif.mockapi.anno;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MockResult {

    /**
     * 使用缓存。在第一次构建后，后续请求会直接返回第一次构建的数据。
     */
    boolean cacheable() default false;

    /**
     * 是否打印mock日志
     */
    boolean log() default true;

    /**
     * 配置名称
     */
    String config() default "";
}
