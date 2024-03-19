package idea.verlif.mockapi.core;

import idea.verlif.mockapi.anno.MockParams;
import idea.verlif.mockapi.anno.MockResult;
import idea.verlif.mockapi.config.PathRecorder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.PostConstruct;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

@Configuration
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
            MockResult result = getAnnotation(handlerMethod, MockResult.class);
            if (result != null) {
                MockItem mockItem = new MockItem(result.log(), result.config(), result.result(), result.resultType());
                recordPath(mappingInfo, handlerMethod.getBean(), method, PathRecorder.MethodSign.RESULT, mockItem);
            }
            // 入参mock
            MockParams params = getAnnotation(handlerMethod, MockParams.class);
            if (params != null) {
                MockItem mockItem = new MockItem(params.log(), params.config(), params.result(), params.resultType());
                recordPath(mappingInfo, handlerMethod.getBean(), method, PathRecorder.MethodSign.PARAMETER, mockItem);
            }
        }
    }

    /**
     * 从执行方法上获取注解
     *
     * @param method     执行方法对象
     * @param annotation 目标注解
     * @return 目标注解
     */
    private <T extends Annotation> T getAnnotation(HandlerMethod method, Class<T> annotation) {
        T result = method.getMethodAnnotation(annotation);
        if (result == null) {
            result = method.getBeanType().getAnnotation(annotation);
        }
        return result;
    }

    private void recordPath(RequestMappingInfo source, Object handler, Method method, PathRecorder.MethodSign methodSign, MockItem mockItem) {
        // 获取调用方法
        Set<RequestMethod> set = source.getMethodsCondition().getMethods();
        for (String value : source.getPatternValues()) {
            PathRecorder.Path targetPath = new PathRecorder.Path(value, set);
            targetPath.setMethod(method, handler, methodSign);
            targetPath.setMockItem(mockItem);
            pathRecorder.add(new PathRecorder.Path(value, set), targetPath);
        }
    }

}
