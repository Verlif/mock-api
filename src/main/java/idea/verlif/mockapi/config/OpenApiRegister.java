package idea.verlif.mockapi.config;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "mockapi.swagger", value = "enabled", matchIfMissing = true)
public class OpenApiRegister {

    @Autowired
    private MockApiConfig config;

    @Bean
    public GroupedOpenApi defaultApi() {
        GroupedOpenApi.Builder aDefault = GroupedOpenApi.builder()
                .group("mockapi.default")
                .displayName("default");
        MockApiConfig.Path path = config.getResultPath();
        String[] paths = new String[2];
        if (path.getPosition() == MockApiConfig.POSITION.PREFIX) {
            paths[0] = "/" + path.getValue() + "/**";
        } else {
            paths[0] = "/**/" + path.getValue();
        }
        path = config.getParamsPath();
        if (path.getPosition() == MockApiConfig.POSITION.PREFIX) {
            paths[1] = "/" + path.getValue() + "/**";
        } else {
            paths[1] = "/**/" + path.getValue();
        }
        return aDefault.pathsToExclude(paths).build();
    }

    @Bean
    public GroupedOpenApi resultApi() {
        MockApiConfig.Path path = config.getResultPath();
        GroupedOpenApi.Builder mockGroupBuilder = GroupedOpenApi.builder()
                .group("mockapi.result")
                .displayName("result");
        if (path.getPosition() == MockApiConfig.POSITION.PREFIX) {
            mockGroupBuilder.pathsToMatch("/" + path.getValue() + "/**");
        } else {
            mockGroupBuilder.pathsToMatch("/**/" + path.getValue());
        }
        return mockGroupBuilder.build();
    }

    @Bean
    public GroupedOpenApi paramsApi() {
        MockApiConfig.Path path = config.getParamsPath();
        GroupedOpenApi.Builder mockGroupBuilder = GroupedOpenApi.builder()
                .group("mockapi.params")
                .displayName("params");
        if (path.getPosition() == MockApiConfig.POSITION.PREFIX) {
            mockGroupBuilder.pathsToMatch("/" + path.getValue() + "/**");
        } else {
            mockGroupBuilder.pathsToMatch("/**/" + path.getValue());
        }
        return mockGroupBuilder.build();
    }
}
