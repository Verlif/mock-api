package idea.verlif.test.mock;

import idea.verlif.mockapi.anno.MockApi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@MockApi
public @interface MyMockApi {
}
