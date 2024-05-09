package idea.verlif.mockapi;

import idea.verlif.mockapi.anno.ConditionalOnMockEnabled;
import idea.verlif.mockapi.anno.MockApi;
import idea.verlif.mockapi.config.PathRecorder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

@Configuration
@ConditionalOnMockEnabled
public class MockApiBuilder {

    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;
    @Autowired
    private PathRecorder pathRecorder;

    public MockApiBuilder() {
    }

    /**
     * 构建新的请求处理
     */
    @PostConstruct
    public void build() {
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> methodEntry : handlerMethods.entrySet()) {
            HandlerMethod handlerMethod = methodEntry.getValue();
            RequestMappingInfo mappingInfo = methodEntry.getKey();
            Method method = methodEntry.getValue().getMethod();

            // 结果mock
            MockApi mockApi = handlerMethod.getMethodAnnotation(MockApi.class);
            if (mockApi == null) {
                mockApi = handlerMethod.getBeanType().getAnnotation(MockApi.class);
            }
            if (mockApi != null) {
                recordPath(mappingInfo, handlerMethod.getBean(), method);
            }
        }
    }

    private void recordPath(RequestMappingInfo source, Object handler, Method method) {
        // 获取调用方法
        Set<RequestMethod> set = source.getMethodsCondition().getMethods();
        for (String value : source.getPatternValues()) {
            PathRecorder.Path targetPath = new PathRecorder.Path(value, set);
            targetPath.setMethod(method, handler);
            pathRecorder.add(new PathRecorder.Path(value, set), targetPath);
        }
    }

}
