package idea.verlif.mockapi.config;

import idea.verlif.mockapi.core.MockApiBuilder;
import org.springdoc.api.AbstractOpenApiResource;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "mockapi.swagger", value = "enabled", matchIfMissing = true)
public class OpenApiRegister {

    public OpenApiRegister() {
        AbstractOpenApiResource.addRestControllers(MockApiBuilder.MockParamsMethodHolder.class);
        AbstractOpenApiResource.addRestControllers(MockApiBuilder.MockResultMethodHolder.class);
    }

    @Bean
    public GroupedOpenApi defaultApi() {
        GroupedOpenApi.Builder aDefault = GroupedOpenApi.builder()
                .group("mockapi.default")
                .displayName("default");
        String[] paths = new String[2];
        paths[0] = "/mock/**";
        paths[1] = "/params/**";
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
        GroupedOpenApi.Builder mockGroupBuilder = GroupedOpenApi.builder()
                .group("mockapi.params")
                .displayName("params");
        mockGroupBuilder.pathsToMatch("/params/**");
        return mockGroupBuilder.build();
    }
}
