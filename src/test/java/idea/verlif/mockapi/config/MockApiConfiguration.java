package idea.verlif.mockapi.config;

import idea.verlif.mock.data.config.MockDataConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MockApiConfiguration {

    @Bean
    public MockDataConfig a() {
        return new MockDataConfig().autoCascade(true).forceNew(true);
    }

    @Bean
    public MockDataConfig b() {
        return (MockDataConfig) new MockDataConfig().autoCascade(true).forceNew(true).fieldValue(String.class, "123");
    }
}
