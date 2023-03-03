package idea.verlif.mockapi.config;

import idea.verlif.mock.data.MockDataCreator;
import idea.verlif.mockapi.core.MockApi;
import idea.verlif.mockapi.core.MockResultCreator;
import idea.verlif.mockapi.core.creator.DefaultMockResultCreator;
import idea.verlif.mockapi.pool.YamlDataPool;
import org.springframework.beans.factory.annotation.Autowired;
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
@Import({MockApi.class, YamlDataPool.class})
public class MockApiConfig {

    /**
     * 地址名称
     */
    private String path = "mock";

    /**
     * 地址新增方式
     */
    private POSITION position = POSITION.PREFIX;

    private final MockDataCreator creator;

    public MockApiConfig(@Autowired(required = false) YamlDataPool yamlDataPool) {
        creator = new MockDataCreator();
        creator.fieldDataPool(yamlDataPool);
        creator.getConfig()
                .autoCascade(true);
    }

    public MockDataCreator getMockDataCreator() {
        return creator;
    }

    @Bean
    @ConditionalOnMissingBean(MockResultCreator.class)
    public MockResultCreator getMockResultCreator() {
        return new DefaultMockResultCreator();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public POSITION getPosition() {
        return position;
    }

    public void setPosition(POSITION position) {
        this.position = position;
    }

    public enum POSITION {

        /**
         * 前缀方式
         */
        PREFIX,

        /**
         * 后缀方式
         */
        SUFFIX,
    }
}
