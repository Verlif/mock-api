package idea.verlif.mockapi.config;

import idea.verlif.mock.data.MockDataCreator;
import idea.verlif.mockapi.core.MockResultCreator;
import idea.verlif.mockapi.core.RequestPack;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * 接口构造配置
 */
@Configuration
@ConfigurationProperties(prefix = "mockapi")
public class MockApiConfig {

    /**
     * 地址名称
     */
    private String path = "mock";

    /**
     * 地址新增方式
     */
    private POSITION position = POSITION.PREFIX;

    public MockApiConfig() {
    }

    @Bean
    @ConditionalOnMissingBean(MockDataCreator.class)
    public MockDataCreator getMockDataCreator() {
        return new MockDataCreator();
    }

    @Bean
    @ConditionalOnMissingBean(MockResultCreator.class)
    public MockResultCreator getMockResultCreator() {
        return new MockResultCreator() {
            @Override
            public <T> T mock(RequestPack pack, MockDataCreator creator, Class<T> target) {
                return creator.mock(target);
            }
        };
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
