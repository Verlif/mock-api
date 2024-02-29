package idea.verlif.mockapi.config;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "mockapi.swagger", value = "enabled", matchIfMissing = true)
public class OpenApiRegister {

    @Bean
    public GroupedOpenApi defaultApi() {
        GroupedOpenApi.Builder aDefault = GroupedOpenApi.builder()
                .group("mockapi.default")
                .displayName("default");
        String[] paths = new String[2];
        paths[0] = "/mock/**";
        paths[1] = "/prams/**";
        return aDefault.pathsToExclude(paths).build();
    }

    @Bean
    public GroupedOpenApi resultApi() {
        GroupedOpenApi.Builder mockGroupBuilder = GroupedOpenApi.builder()
                .group("mockapi.result")
                .displayName("result");
        mockGroupBuilder.pathsToMatch("/mock/**");
        return mockGroupBuilder.build();
    }

    @Bean
    public GroupedOpenApi paramsApi() {
        ;
        GroupedOpenApi.Builder mockGroupBuilder = GroupedOpenApi.builder()
                .group("mockapi.params")
                .displayName("params");
        mockGroupBuilder.pathsToMatch("/params/**");
        return mockGroupBuilder.build();
    }
}
