package idea.verlif.mockapi.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MockParams {

    /**
     * 使用缓存。在第一次构建后，后续请求会直接返回第一次构建的数据。
     */
    boolean cacheable() default false;

}
