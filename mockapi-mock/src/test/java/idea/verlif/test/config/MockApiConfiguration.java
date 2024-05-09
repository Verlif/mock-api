package idea.verlif.test.config;

import idea.verlif.mock.data.MockDataCreator;
import idea.verlif.mock.data.config.MockDataConfig;
import idea.verlif.mock.data.util.RandomUtil;
import idea.verlif.mock.data.virtualpool.template.ContinuousIntPool;
import idea.verlif.mock.data.virtualpool.template.IdNumberStringPool;
import idea.verlif.test.global.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class MockApiConfiguration {

    @Autowired
    private MockDataCreator mockDataCreator;

    @PostConstruct
    public void resetMockDataCreator() {
        // 对用户ID进行递增id分配
        mockDataCreator.fieldValue(User::getUserId, new ContinuousIntPool());
        mockDataCreator.getConfig().arraySize(cla -> RandomUtil.range(1, 5));
    }

    @Bean
    public MockDataConfig a() {
        return new MockDataConfig().arraySize(cla -> RandomUtil.range(1, 5));
    }

    @Bean
    public MockDataConfig b() {
        return (MockDataConfig) new MockDataConfig()
                .arraySize(cla -> RandomUtil.nextInt(3) + 2)
                .fieldObject(String.class, "固定String");
    }
}
