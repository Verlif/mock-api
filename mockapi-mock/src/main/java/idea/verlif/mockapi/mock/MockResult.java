package idea.verlif.mockapi.mock;

import idea.verlif.mockapi.anno.MockApi;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 结果mock
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@MockApi
public @interface MockResult {

    /**
     * 自定义路径，有值时会跳过路径生成器生成
     */
    String path() default "";

    /**
     * 是否打印mock日志
     */
    boolean log() default false;

    /**
     * 配置名称
     */
    String config() default "";

    /**
     * 配置数据
     */
    String data() default "";

    /**
     * 请求方法，默认使用目标方法所使用的请求方法，若没有则使用全部方法
     */
    RequestMethod[] methods() default {};
}
