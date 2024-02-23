package idea.verlif.mockapi.config;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Configuration
@ConditionalOnProperty(prefix = "mockapi.swagger", value = "enabled", matchIfMissing = true)
public class OpenApiRegister {

    private final List<String> paramsPath;
    private final List<String> resultPath;

    public OpenApiRegister() {
        paramsPath = new ArrayList<>();
        resultPath = new ArrayList<>();
    }

    @Bean
    public GroupedOpenApi defaultApi() {
        GroupedOpenApi.Builder aDefault = GroupedOpenApi.builder()
                .group("mockapi.default")
                .displayName("default");
        String[] paths = new String[paramsPath.size() + resultPath.size()];
        int paramsSize = paramsPath.size();
        for (int i = 0; i < paramsSize; i++) {
            paths[i] = paramsPath.get(i);
        }
        for (int i = 0, size = resultPath.size(); i < size; i++) {
            paths[i + paramsSize] = resultPath.get(i);
        }
        return aDefault.pathsToExclude(paths).build();
    }

    @Bean
    public GroupedOpenApi resultApi() {
        GroupedOpenApi.Builder mockGroupBuilder = GroupedOpenApi.builder()
                .group("mockapi.result")
                .displayName("result");
        String[] paths = new String[resultPath.size()];
        for (int i = 0, size = resultPath.size(); i < size; i++) {
            paths[i] = resultPath.get(i);
        }
        mockGroupBuilder.pathsToMatch(paths);
        return mockGroupBuilder.build();
    }

    @Bean
    public GroupedOpenApi paramsApi() {
        GroupedOpenApi.Builder mockGroupBuilder = GroupedOpenApi.builder()
                .group("mockapi.params")
                .displayName("params");
        String[] paths = new String[paramsPath.size()];
        for (int i = 0, size = paramsPath.size(); i < size; i++) {
            paths[i] = paramsPath.get(i);
        }
        mockGroupBuilder.pathsToMatch(paths);
        return mockGroupBuilder.build();
    }

    public void addParamsPath(String path) {
        this.paramsPath.add(path);
    }

    public void addParamsPaths(Collection<String> paths) {
        this.paramsPath.addAll(paths);
    }

    public void addResultPath(String path) {
        this.resultPath.add(path);
    }

    public void addResultPaths(Collection<String> paths) {
        this.resultPath.addAll(paths);
    }
}
