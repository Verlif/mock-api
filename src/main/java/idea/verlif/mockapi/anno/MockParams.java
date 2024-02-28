package idea.verlif.mockapi.anno;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Operation
public @interface MockParams {

    /**
     * 使用缓存。在第一次构建后，后续请求会直接返回第一次构建的数据。
     */
    boolean cacheable() default true;

    /**
     * 配置名称
     */
    String config() default "";

    @AliasFor(
            annotation = Operation.class
    )
    String method() default "";

    @AliasFor(
            annotation = Operation.class
    )
    String[] tags() default {};

    @AliasFor(
            annotation = Operation.class
    )
    String summary() default "";

    @AliasFor(
            annotation = Operation.class
    )
    String description() default "";

    @AliasFor(
            annotation = Operation.class
    )
    RequestBody requestBody() default @RequestBody;

    @AliasFor(
            annotation = Operation.class
    )
    ExternalDocumentation externalDocs() default @ExternalDocumentation;

    @AliasFor(
            annotation = Operation.class
    )
    String operationId() default "";

    @AliasFor(
            annotation = Operation.class
    )
    Parameter[] parameters() default {};

    @AliasFor(
            annotation = Operation.class
    )
    ApiResponse[] responses() default {};

    @AliasFor(
            annotation = Operation.class
    )
    boolean deprecated() default false;

    @AliasFor(
            annotation = Operation.class
    )
    SecurityRequirement[] security() default {};

    @AliasFor(
            annotation = Operation.class
    )
    Server[] servers() default {};

    @AliasFor(
            annotation = Operation.class
    )
    Extension[] extensions() default {};

    @AliasFor(
            annotation = Operation.class
    )
    boolean hidden() default false;

    @AliasFor(
            annotation = Operation.class
    )
    boolean ignoreJsonView() default false;
}
