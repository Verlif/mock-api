package idea.verlif.mockapi.anno;

import idea.verlif.mockapi.ObjectMocker;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MockApi {

    /**
     * 自定义路径，有值时会跳过路径生成器生成
     */
    String path() default "";

    /**
     * 是否打印mock日志
     */
    boolean log() default false;

    /**
     * 配置数据
     */
    String data() default "";

    /**
     * 数据生成类
     */
    Class<? extends ObjectMocker> mocker() default ObjectMocker.DefaultObjectMocker.class;

    /**
     * 请求方法，默认使用目标方法所使用的请求方法，若没有则使用全部方法
     */
    RequestMethod[] methods() default {};
}
