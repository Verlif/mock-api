package idea.verlif.mockapi.arg;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.METHOD, ElementType.TYPE})
public @interface MockArg {

    /**
     * 是否生效
     */
    boolean enabled() default true;

    /**
     * 自定义数据
     */
    String data() default "";

    /**
     * 是否忽略传入的值，总是构建参数
     */
    boolean force() default false;

    /**
     * 参数构建器
     */
    Class<? extends ArgMocker> mocker() default DefaultArgMocker.class;
}
