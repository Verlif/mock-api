package idea.verlif.mockapi.mock.config;

import idea.verlif.mock.data.config.MockDataConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * mock配置收集器
 */
@Component
public class MockDataConfigCollector {

    private final Map<String, MockDataConfig> configMap;

    public MockDataConfigCollector(ApplicationContext applicationContext) {
        configMap = applicationContext.getBeansOfType(MockDataConfig.class);
    }

    /**
     * 获取mock配置对象
     *
     * @param mockName 配置名称
     * @return 配置名称对应的mock配置对象
     */
    public MockDataConfig getMockDataConfig(String mockName) {
        return configMap.get(mockName);
    }

    /**
     * 添加或替换mock配置
     *
     * @param mockName       配置名称
     * @param mockDataConfig 配置对象
     */
    public void putMockDataConfig(String mockName, MockDataConfig mockDataConfig) {
        configMap.put(mockName, mockDataConfig);
    }
}
