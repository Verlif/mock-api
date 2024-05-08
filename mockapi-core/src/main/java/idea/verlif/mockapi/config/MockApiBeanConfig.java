package idea.verlif.mockapi.config;

import idea.verlif.mockapi.ObjectMockerCollector;
import idea.verlif.mockapi.anno.ConditionalOnMockEnabled;
import idea.verlif.mockapi.MockApiBuilder;
import idea.verlif.mockapi.MockApiRegister;
import idea.verlif.mockapi.convert.MockAnnotationHandler;
import idea.verlif.mockapi.log.MockLogger;
import idea.verlif.mockapi.log.impl.NoMockLogger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 接口构造配置
 */
@Configuration
@ConditionalOnMockEnabled
@Import({MockApiBuilder.class, MockApiRegister.class, MockAnnotationHandler.class})
public class MockApiBeanConfig {

    @Bean
    @ConditionalOnMissingBean(MockLogger.class)
    public MockLogger mockLogger() {
        return new NoMockLogger();
    }

    @Bean
    @ConditionalOnMissingBean(ObjectMockerCollector.class)
    public ObjectMockerCollector objectMockerCollector(ApplicationContext context) {
        return new ObjectMockerCollector(context);
    }
}
