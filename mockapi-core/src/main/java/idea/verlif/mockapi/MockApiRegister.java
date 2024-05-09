package idea.verlif.mockapi;

import idea.verlif.mockapi.anno.ConditionalOnMockEnabled;
import idea.verlif.mockapi.anno.MockApi;
import idea.verlif.mockapi.config.MockApiConfig;
import idea.verlif.mockapi.config.PathRecorder;
import idea.verlif.mockapi.convert.MockAnnotationHandler;
import idea.verlif.mockapi.log.MockLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

@Configuration
@ConditionalOnMockEnabled
public class MockApiRegister {

    private final RequestMappingInfo.BuilderConfiguration builderConfiguration;
    private final RequestMappingHandlerMapping requestMappingHandlerMapping;
    private final Method mockMethod;

    @Autowired
    private MockAnnotationHandler mockAnnotationHandler;
    @Autowired
    private MockApiConfig mockApiConfig;
    @Autowired
    private PathRecorder pathRecorder;
    @Autowired
    private MockLogger mockLogger;

    public MockApiRegister(RequestMappingHandlerMapping requestMappingHandlerMapping) throws NoSuchMethodException {
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
        builderConfiguration = new RequestMappingInfo.BuilderConfiguration();
        builderConfiguration.setTrailingSlashMatch(requestMappingHandlerMapping.useTrailingSlashMatch());
        builderConfiguration.setContentNegotiationManager(requestMappingHandlerMapping.getContentNegotiationManager());
        if (requestMappingHandlerMapping.getPatternParser() != null) {
            builderConfiguration.setPatternParser(requestMappingHandlerMapping.getPatternParser());
        } else {
            builderConfiguration.setPathMatcher(requestMappingHandlerMapping.getPathMatcher());
        }
        mockMethod = MockMethodHolder.class.getMethod("mock", Map.class, Map.class, HttpServletRequest.class, HttpServletResponse.class);
    }

    @PostConstruct
    public void register() {
        for (int i = 0, size = pathRecorder.getSize(); i < size; i++) {
            PathRecorder.Path targetPath = pathRecorder.getValue(i);
            Method method = targetPath.getMethod();
            if (method != null) {
                RequestMappingInfo requestMappingInfo = buildRequestMappingInfo(targetPath);
                if (mockApiConfig.getPathStrategy() == MockApiConfig.PathStrategy.REPLACE) {
                    requestMappingHandlerMapping.unregisterMapping(requestMappingInfo);
                }
                try {
                    requestMappingHandlerMapping.registerMapping(requestMappingInfo, targetPath.getHandle(), targetPath.getMethod());
                } catch (IllegalStateException ignored) {
                }
            }
        }
    }

    private RequestMappingInfo buildRequestMappingInfo(PathRecorder.Path path) {
        resetResultPath(path);
        String pathUrl = path.getPath();
        return RequestMappingInfo.paths(pathUrl)
                .methods(path.getRequestMethods())
                .options(builderConfiguration)
                .build();
    }

    /**
     * 重置虚拟参数接口路径
     */
    private void resetResultPath(PathRecorder.Path path) {
        MockItem mockItem = path.getMockItem();
        RequestMethod[] requestMethods = path.getRequestMethods();
        if (mockItem == null) {
            mockItem = getMockItem(path.getMethod());
            if (mockItem == null) {
                mockItem = new MockItem();
            } else {
                // 覆盖方法
                if (mockItem.getMethods().length > 0) {
                    requestMethods = mockItem.getMethods();
                }
                // 覆盖地址
                if (!mockItem.getPath().isEmpty()) {
                    path.setPath(mockItem.getPath());
                }
            }
        }
        path.setRequestMethods(requestMethods);
        path.setMethod(mockMethod, new MockMethodHolder(path.getMethod(), mockItem));
        ObjectMocker objectMocker = mockItem.getObjectMocker();
        if (objectMocker != null) {
            objectMocker.resetPath(path);
        }
    }

    private MockItem getMockItem(Method method) {
        MockItem mockItem = null;
        // 处理注解
        for (Annotation annotation : method.getAnnotations()) {
            mockItem = mockAnnotationHandler.convert(annotation);
            if (mockItem != null) {
                break;
            }
        }
        // 没有可用注解则从定义类上寻找
        if (mockItem == null) {
            for (Annotation annotation : method.getDeclaringClass().getAnnotations()) {
                mockItem = mockAnnotationHandler.convert(annotation);
                if (mockItem != null) {
                    break;
                }
            }
        }
        return mockItem;
    }

    /**
     * 构建方法数据类
     */
    public class MockMethodHolder {

        protected final Method oldMethod;
        protected final MockItem mockItem;

        public MockMethodHolder(Method oldMethod, MockItem mockItem) {
            this.oldMethod = oldMethod;
            this.mockItem = mockItem;
        }

        @ResponseBody
        public Object mock(Map<String, String> pathVar, Map<String, Object> param, HttpServletRequest request, HttpServletResponse response) {
            RequestPack pack = new RequestPack(pathVar, param, request, response);
            return mockObject(pack);
        }

        protected Object mockObject(RequestPack pack) {
            Object o = mockObject(mockItem.getObjectMocker(), pack);
            // 日志输出
            if (mockItem.isLog()) {
                mockLogger.log(pack, oldMethod, o);
            }
            return o;
        }

        protected Object mockObject(ObjectMocker objectMocker, RequestPack pack) {
            pack.setOldMethod(oldMethod);
            return objectMocker.mock(mockItem, pack);
        }

    }

}
