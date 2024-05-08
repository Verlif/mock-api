package idea.verlif.mockapi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "mock-api")
public class MockApiConfig {

    /**
     * 是否开启MockApi的虚拟接口服务
     */
    private boolean enabled;

    /**
     * 地址重复策略
     */
    private PathStrategy pathStrategy = PathStrategy.REPLACE;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public PathStrategy getPathStrategy() {
        return pathStrategy;
    }

    public void setPathStrategy(PathStrategy pathStrategy) {
        this.pathStrategy = pathStrategy;
    }

    public enum PathStrategy {
        /**
         * 忽略重复地址
         */
        IGNORED,
        /**
         * 替换已有的重复地址
         */
        REPLACE
    }
}
