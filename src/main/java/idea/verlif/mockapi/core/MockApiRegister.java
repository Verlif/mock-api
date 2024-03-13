package idea.verlif.mockapi.core;

import idea.verlif.mockapi.config.PathRecorder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.PostConstruct;

@Configuration
@AutoConfigureAfter(MockApiBuilder.class)
public class MockApiRegister {

    private final RequestMappingInfo.BuilderConfiguration builderConfiguration;
    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Autowired
    private PathRecorder pathRecorder;

    public MockApiRegister(RequestMappingHandlerMapping requestMappingHandlerMapping) {
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
        builderConfiguration = new RequestMappingInfo.BuilderConfiguration();
        builderConfiguration.setTrailingSlashMatch(requestMappingHandlerMapping.useTrailingSlashMatch());
        builderConfiguration.setContentNegotiationManager(requestMappingHandlerMapping.getContentNegotiationManager());
        if (requestMappingHandlerMapping.getPatternParser() != null) {
            builderConfiguration.setPatternParser(requestMappingHandlerMapping.getPatternParser());
        } else {
            builderConfiguration.setPathMatcher(requestMappingHandlerMapping.getPathMatcher());
        }
    }

    @PostConstruct
    public void register() {
        for (int i = 0, size = pathRecorder.getSize(); i < size; i++) {
            PathRecorder.Path targetPath = pathRecorder.getValue(i);
            if (targetPath.getHandle() != null && targetPath.getMethod() != null) {
                RequestMappingInfo requestMappingInfo = buildRequestMappingInfo(targetPath);
                requestMappingHandlerMapping.registerMapping(requestMappingInfo, targetPath.getHandle(), targetPath.getMethod());
            }
        }
    }

    private RequestMappingInfo buildRequestMappingInfo(PathRecorder.Path path) {
        return RequestMappingInfo.paths(path.getPath())
                .methods(path.getRequestMethods().toArray(new RequestMethod[0]))
                .options(builderConfiguration)
                .build();
    }
}
