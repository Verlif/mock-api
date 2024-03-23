package idea.verlif.mockapi.core;

import idea.verlif.mock.data.MockDataCreator;
import idea.verlif.mock.data.config.MockDataConfig;
import idea.verlif.mockapi.anno.ConditionalOnMockEnabled;
import idea.verlif.mockapi.anno.MockParams;
import idea.verlif.mockapi.anno.MockResult;
import idea.verlif.mockapi.config.MockApiConfig;
import idea.verlif.mockapi.config.PathRecorder;
import idea.verlif.mockapi.core.creator.MockParamsCreator;
import idea.verlif.mockapi.core.creator.MockParamsPathGenerator;
import idea.verlif.mockapi.core.creator.MockResultCreator;
import idea.verlif.mockapi.core.creator.MockResultPathGenerator;
import idea.verlif.mockapi.log.MockLogger;
import idea.verlif.parser.ParamParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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
import java.util.HashMap;
import java.util.Map;

@Configuration
@ConditionalOnMockEnabled
public class MockApiRegister {

    private final RequestMappingInfo.BuilderConfiguration builderConfiguration;
    private final RequestMappingHandlerMapping requestMappingHandlerMapping;
    private final Map<String, MockDataConfig> configMap;
    private final Method mockMethod;

    @Autowired
    private MockApiConfig mockApiConfig;
    @Autowired
    private MockDataCreator creator;
    @Autowired
    private PathRecorder pathRecorder;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private MockResultCreator mockResultCreator;
    @Autowired
    private MockParamsCreator mockParamsCreator;
    @Autowired
    private ParamParserService paramParserService;
    @Autowired
    private MockResultPathGenerator resultPathGenerator;
    @Autowired
    private MockParamsPathGenerator paramsPathGenerator;
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
        configMap = new HashMap<>();
    }

    @PostConstruct
    public void register() {
        collectMockConfig();
        for (int i = 0, size = pathRecorder.getSize(); i < size; i++) {
            PathRecorder.Path targetPath = pathRecorder.getValue(i);
            Method method = targetPath.getMethod();
            if (targetPath.getHandle() != null && method != null) {
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

    public void collectMockConfig() {
        configMap.putAll(applicationContext.getBeansOfType(MockDataConfig.class));
    }

    public MockDataConfig getMockConfig(String name) {
        return configMap.getOrDefault(name, creator.getConfig());
    }

    private RequestMappingInfo buildRequestMappingInfo(PathRecorder.Path path) {
        if (path.getMethodSign() == PathRecorder.MethodSign.RESULT) {
            resetResultPath(path);
        } else {
            resetParamsPath(path);
        }
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
            MockResult mockResult = getAnnotation(path.getMethod(), MockResult.class);
            if (mockResult == null) {
                mockItem = new MockItem();
            } else {
                mockItem = new MockItem(mockResult.log(), mockResult.config(), mockResult.result(), mockResult.resultType());
                if (mockResult.methods().length > 0) {
                    requestMethods = mockResult.methods();
                }
                // 覆盖地址
                if (!mockResult.path().isEmpty()) {
                    path.setPath(mockResult.path());
                }
            }
        }
        // 重构路径
        path.setPath(resultPathGenerator.urlGenerate(path.getPath()));
        path.setRequestMethods(requestMethods);
        path.setMethod(mockMethod, new MockMethodHolder(path.getHandle(), path.getMethod(), mockItem, mockResultCreator), path.getMethodSign());
    }

    /**
     * 重置虚拟参数接口路径
     */
    private void resetParamsPath(PathRecorder.Path path) {
        MockItem mockItem = path.getMockItem();
        RequestMethod[] requestMethods = path.getRequestMethods();
        if (mockItem == null) {
            MockParams mockParams = getAnnotation(path.getMethod(), MockParams.class);
            if (mockParams == null) {
                mockItem = new MockItem();
            } else {
                mockItem = new MockItem(mockParams.log(), mockParams.config(), mockParams.result(), mockParams.resultType());
                if (mockParams.methods().length > 0) {
                    requestMethods = mockParams.methods();
                }
                // 覆盖地址
                if (!mockParams.path().isEmpty()) {
                    path.setPath(mockParams.path());
                }
            }
        }
        // 重构路径
        path.setPath(paramsPathGenerator.urlGenerate(path.getPath()));
        path.setRequestMethods(requestMethods);
        path.setMethod(mockMethod, new MockMethodHolder(path.getHandle(), path.getMethod(), mockItem, mockParamsCreator), path.getMethodSign());
    }

    /**
     * 从方法上获取注释
     * @param method 方法对象
     * @param annotationCla 目标注释
     * @return 方法标记的注释，可能为空
     */
    private <T extends Annotation> T getAnnotation(Method method, Class<T> annotationCla) {
        T annotation = method.getAnnotation(annotationCla);
        if (annotation == null) {
            annotation = method.getClass().getAnnotation(annotationCla);
        }
        return annotation;
    }

    /**
     * 构建方法数据类
     */
    public class MockMethodHolder {

        protected final Object methodHolder;
        protected final Method oldMethod;
        protected final MockItem mockItem;
        protected final ObjectMocker objectMocker;

        public MockMethodHolder(Object methodHandle, Method oldMethod, MockItem mockItem, ObjectMocker objectMocker) {
            this.methodHolder = methodHandle;
            this.oldMethod = oldMethod;
            this.mockItem = mockItem;
            this.objectMocker = objectMocker;
        }

        @ResponseBody
        public Object mock(Map<String, String> pathVar, Map<String, Object> param, HttpServletRequest request, HttpServletResponse response) {
            RequestPack pack = new RequestPack(pathVar, param, request, response);
            return mockObject(pack);
        }

        protected Object mockObject(RequestPack pack) {
            if (!mockItem.getResult().isEmpty()) {
                return paramParserService.parse(mockItem.getResultType(), mockItem.getResult());
            }
            Object o = mockObject(objectMocker, pack, getMockConfig(mockItem.getConfig()));
            // 日志输出
            if (mockItem.isLog()) {
                mockLogger.log(pack, methodHolder, oldMethod, o);
            }
            return o;
        }

        protected Object mockObject(ObjectMocker objectMocker, RequestPack pack, MockDataConfig config) {
            pack.setOldMethod(oldMethod);
            pack.setMethodHandle(methodHolder);
            return objectMocker.mock(pack, creator, config);
        }

    }

}
