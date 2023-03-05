package idea.verlif.mockapi.config;

import idea.verlif.mock.data.MockDataCreator;
import idea.verlif.mockapi.core.MockApi;
import idea.verlif.mockapi.core.creator.MockParamsCreator;
import idea.verlif.mockapi.core.creator.MockResultCreator;
import idea.verlif.mockapi.core.impl.DefaultMockParamsCreator;
import idea.verlif.mockapi.core.impl.DefaultMockResultCreator;
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
     * 结果地址
     */
    private Path resultPath = new Path("mock", POSITION.PREFIX);

    /**
     * 参数地址
     */
    private Path paramsPath = new Path("params", POSITION.PREFIX);

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

    @Bean
    @ConditionalOnMissingBean(MockParamsCreator.class)
    public MockParamsCreator getMockParamsCreator() {
        return new DefaultMockParamsCreator();
    }

    public Path getResultPath() {
        return resultPath;
    }

    public void setResultPath(Path resultPath) {
        this.resultPath = resultPath;
    }

    public Path getParamsPath() {
        return paramsPath;
    }

    public void setParamsPath(Path paramsPath) {
        this.paramsPath = paramsPath;
    }

    /**
     * 路径地址
     */
    public static final class Path {

        private String value;

        private POSITION position;

        public Path() {
        }

        public Path(String value, POSITION position) {
            this.value = value;
            this.position = position;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public POSITION getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = POSITION.valueOf(position);
        }

        public void setPosition(POSITION position) {
            this.position = position;
        }
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
        ;
    }
}
