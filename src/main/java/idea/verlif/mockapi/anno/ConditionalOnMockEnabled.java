package idea.verlif.mockapi.anno;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@ConditionalOnProperty(prefix = "mock-api", name = "enabled", havingValue = "true", matchIfMissing = true)
public @interface ConditionalOnMockEnabled {
}
