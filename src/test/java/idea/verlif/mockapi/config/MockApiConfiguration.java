package idea.verlif.mockapi.config;

import idea.verlif.mock.data.config.MockDataConfig;
import idea.verlif.mock.data.config.SizeCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Configuration
public class MockApiConfiguration {

    @Bean
    public MockDataConfig a() {
        return new MockDataConfig().autoCascade(true).arraySize(5);
    }

    @Bean
    public MockDataConfig b() {
        return (MockDataConfig) new MockDataConfig()
                .autoCascade(true)
                .arraySize(new SizeCreator() {
                    private final Random random = new Random();
                    @Override
                    public int getSize(Class<?> aClass) {
                        return random.nextInt(20) + 2;
                    }
                })
                .fieldObject(String.class, "固定String");
    }
}
