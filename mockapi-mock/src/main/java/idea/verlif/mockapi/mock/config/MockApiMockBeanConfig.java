package idea.verlif.mockapi.mock.config;

import idea.verlif.mock.data.MockDataCreator;
import idea.verlif.mockapi.anno.ConditionalOnMockEnabled;
import idea.verlif.mockapi.mock.pool.YamlDataPool;
import idea.verlif.parser.ParamParserService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnMockEnabled
@Import({MockDataConfigCollector.class, YamlDataPool.class})
public class MockApiMockBeanConfig {

    @Bean
    @ConditionalOnMissingBean(MockDataCreator.class)
    public MockDataCreator getMockDataCreator() {
        return new MockDataCreator();
    }

    @Bean
    @ConditionalOnMissingBean(ParamParserService.class)
    public ParamParserService getParamParserService() {
        return new ParamParserService();
    }
}
