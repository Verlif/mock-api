package idea.verlif.mockapi.anno;

import idea.verlif.mockapi.config.MockApiConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@ConditionalOnBean(MockApiConfig.class)
public @interface ConditionalOnMockEnabled {
}
