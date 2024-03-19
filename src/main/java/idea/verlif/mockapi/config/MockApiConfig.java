package idea.verlif.mockapi.config;

import idea.verlif.mock.data.MockDataCreator;
import idea.verlif.mockapi.core.MockApiBuilder;
import idea.verlif.mockapi.core.creator.MockParamsCreator;
import idea.verlif.mockapi.core.creator.MockParamsPathGenerator;
import idea.verlif.mockapi.core.creator.MockResultCreator;
import idea.verlif.mockapi.core.creator.MockResultPathGenerator;
import idea.verlif.mockapi.core.impl.DefaultMockParamsCreator;
import idea.verlif.mockapi.core.impl.DefaultMockParamsPathGenerator;
import idea.verlif.mockapi.core.impl.DefaultMockResultCreator;
import idea.verlif.mockapi.core.impl.DefaultMockResultPathGenerator;
import idea.verlif.mockapi.log.MockLogger;
import idea.verlif.mockapi.log.impl.NoMockLogger;
import idea.verlif.mockapi.pool.YamlDataPool;
import idea.verlif.parser.ParamParserService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 接口构造配置
 */
@Configuration
@ConfigurationProperties(prefix = "mockapi")
@Import({MockApiBuilder.class, YamlDataPool.class})
public class MockApiConfig {

    @Bean
    public MockDataCreator getMockDataCreator() {
        MockDataCreator creator = new MockDataCreator();
        creator.getConfig()
                .autoCascade(true);
        return creator;
    }

    /**
     * 结果数据生成器
     */
    @Bean
    @ConditionalOnMissingBean(MockResultCreator.class)
    public MockResultCreator mockResultCreator() {
        return new DefaultMockResultCreator();
    }

    /**
     * 入参数据生成器
     */
    @Bean
    @ConditionalOnMissingBean(MockParamsCreator.class)
    public MockParamsCreator mockParamsCreator() {
        return new DefaultMockParamsCreator();
    }

    /**
     * 参数解析服务器
     */
    @Bean
    @ConditionalOnMissingBean(ParamParserService.class)
    public ParamParserService paramParserService() {
        return new ParamParserService();
    }

    /**
     * 入参数据接口地址生成器
     */
    @Bean
    @ConditionalOnMissingBean(MockParamsPathGenerator.class)
    public MockParamsPathGenerator mockParamsPathGenerator() {
        return new DefaultMockParamsPathGenerator();
    }

    /**
     * 结果数据接口地址生成器
     */
    @Bean
    @ConditionalOnMissingBean(MockResultPathGenerator.class)
    public MockResultPathGenerator mockResultPathGenerator() {
        return new DefaultMockResultPathGenerator();
    }

    @Bean
    @ConditionalOnMissingBean(MockLogger.class)
    public MockLogger mockLogger() {
        return new NoMockLogger();
    }
}
