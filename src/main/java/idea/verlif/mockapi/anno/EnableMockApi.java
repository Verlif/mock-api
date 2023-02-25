package idea.verlif.mockapi.anno;

import idea.verlif.mockapi.config.MockApiConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Verlif
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Configuration
@Documented
@Import({MockApiConfig.class})
@ConditionalOnProperty(prefix = "mockapi", value = "enabled", matchIfMissing = true)
public @interface EnableMockApi {
}
