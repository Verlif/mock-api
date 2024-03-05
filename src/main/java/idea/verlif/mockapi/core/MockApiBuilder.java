package idea.verlif.mockapi.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import idea.verlif.mock.data.MockDataCreator;
import idea.verlif.mock.data.config.MockDataConfig;
import idea.verlif.mockapi.anno.MockParams;
import idea.verlif.mockapi.anno.MockResult;
import idea.verlif.mockapi.config.PathRecorder;
import idea.verlif.mockapi.core.creator.MockParamsPathGenerator;
import idea.verlif.mockapi.core.creator.MockResultCreator;
import idea.verlif.mockapi.core.creator.MockResultPathGenerator;
import idea.verlif.parser.ParamParserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Configuration
public class MockApiBuilder {

    private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;
    @Autowired
    private MockResultPathGenerator resultPathGenerator;
    @Autowired
    private MockParamsPathGenerator paramsPathGenerator;
    @Autowired
    private MockDataCreator creator;
    @Autowired
    private MockResultCreator mockResultCreator;
    @Autowired
    private ParamParserService paramParserService;
    @Autowired
    private PathRecorder pathRecorder;

    private final Method resultMethod;
    private final Method paramsMethod;
    private final Map<String, MockDataConfig> configMap;

    public MockApiBuilder() throws NoSuchMethodException {
        resultMethod = MockResultMethodHolder.class.getMethod("mockResult", Map.class, Map.class, HttpServletRequest.class, HttpServletResponse.class);
        paramsMethod = MockParamsMethodHolder.class.getMethod("mockParams", Map.class, Map.class, HttpServletRequest.class, HttpServletResponse.class);
        configMap = new HashMap<>();
    }

    public void collectMockConfig() {
        configMap.putAll(applicationContext.getBeansOfType(MockDataConfig.class));
    }

    public MockDataConfig getMockConfig(String name) {
        return configMap.getOrDefault(name, creator.getConfig());
    }

    /**
     * 注册新的请求处理
     */
    @PostConstruct
    public void build() {
        collectMockConfig();
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> methodEntry : handlerMethods.entrySet()) {
            HandlerMethod handlerMethod = methodEntry.getValue();
            RequestMappingInfo mappingInfo = methodEntry.getKey();
            Method method = methodEntry.getValue().getMethod();

            // 结果mock
            MockResult result = getAnnotation(handlerMethod, MockResult.class);
            if (result != null) {
                MockResultMethodHolder mockMethodHolder = new MockResultMethodHolder(handlerMethod, method, result);
                recordPath(mappingInfo, mockMethodHolder, resultMethod);
            }
            // 入参mock
            MockParams params = getAnnotation(handlerMethod, MockParams.class);
            if (params != null) {
                MockParamsMethodHolder mockMethodHolder = new MockParamsMethodHolder(handlerMethod, method, params);
                recordPath(mappingInfo, mockMethodHolder, paramsMethod);
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

    private void recordPath(RequestMappingInfo source, MockMethodHolder<?> mockMethodHolder, Method method) {
        // 获取调用方法
        Set<RequestMethod> set = source.getMethodsCondition().getMethods();
        for (String value : source.getPatternValues()) {
            String path = mockMethodHolder.path(value);
            PathRecorder.Path targetPath = new PathRecorder.Path(path, set);
            targetPath.setHandle(mockMethodHolder);
            targetPath.setMethod(method);
            pathRecorder.add(new PathRecorder.Path(value, set), targetPath);
        }
    }

    /**
     * 构建方法数据类
     */
    public abstract class MockMethodHolder<T extends Annotation> {

        protected final HandlerMethod methodHolder;
        protected final Method oldMethod;
        protected final T annotation;
        protected final Class<?> controllerClass;

        public MockMethodHolder(HandlerMethod handlerMethod, Method oldMethod, T annotation) {
            this.methodHolder = handlerMethod;
            this.oldMethod = oldMethod;
            this.annotation = annotation;
            this.controllerClass = oldMethod.getDeclaringClass();
        }

        public T getAnnotation() {
            return annotation;
        }

        public abstract String path(String sourcePath);

        protected Object mockObject(MockItem mockItem, RequestPack pack) {
            if (!mockItem.getResult().isEmpty()) {
                return paramParserService.parse(mockItem.getResultType(), mockItem.getResult());
            }
            Object o = mockObject(mockResultCreator, pack, getMockConfig(mockItem.getConfig()));
            // 日志输出
            if (mockItem.isLog()) {
                Logger logger = LoggerFactory.getLogger(controllerClass);
                String result;
                try {
                    result = OBJECT_MAPPER.writeValueAsString(o);
                } catch (JsonProcessingException e) {
                    result = o.toString();
                }
                logger.debug(pack.getRequest().getMethod() + " - " + pack.getRequest().getRequestURL() + " - " + result);
            }
            return o;
        }

        protected Object mockObject(ObjectMocker objectMocker, RequestPack pack, MockDataConfig config) {
            pack.setOldMethod(oldMethod);
            pack.setHandlerMethod(methodHolder);
            return objectMocker.mock(pack, creator, config);
        }

        public class MockItem {

            /**
             * 是否打印mock日志
             */
            private boolean log;

            /**
             * 配置名称
             */
            private String config;

            /**
             * 直返数据，不进行mock，直接返回参数值
             */
            private String result;

            /**
             * 直返数据的类型
             */
            private Class<?> resultType;

            public MockItem(boolean log, String config, String result, Class<?> resultType) {
                this.log = log;
                this.config = config;
                this.result = result;
                this.resultType = resultType;
            }

            public boolean isLog() {
                return log;
            }

            public void setLog(boolean log) {
                this.log = log;
            }

            public String getConfig() {
                return config;
            }

            public void setConfig(String config) {
                this.config = config;
            }

            public String getResult() {
                return result;
            }

            public void setResult(String result) {
                this.result = result;
            }

            public Class<?> getResultType() {
                return resultType;
            }

            public void setResultType(Class<?> resultType) {
                this.resultType = resultType;
            }
        }

    }

    public final class MockResultMethodHolder extends MockMethodHolder<MockResult> {

        public MockResultMethodHolder(HandlerMethod handlerMethod, Method oldMethod, MockResult mockResult) {
            super(handlerMethod, oldMethod, mockResult);
        }

        @Override
        public String path(String sourcePath) {
            String path = getAnnotation().path();
            if (path.isEmpty()) {
                return resultPathGenerator.urlGenerate(sourcePath);
            }
            return path;
        }

        @ResponseBody
        public Object mockResult(Map<String, String> pathVar, Map<String, Object> param, HttpServletRequest request, HttpServletResponse response) {
            RequestPack pack = new RequestPack(pathVar, param, request, response);
            MockResult mockResult = getAnnotation();
            return mockObject(new MockItem(mockResult.log(), mockResult.config(), mockResult.result(), mockResult.resultType()), pack);
        }

    }

    public final class MockParamsMethodHolder extends MockMethodHolder<MockParams> {

        public MockParamsMethodHolder(HandlerMethod handlerMethod, Method oldMethod, MockParams annotation) {
            super(handlerMethod, oldMethod, annotation);
        }

        @ResponseBody
        public Object mockParams(Map<String, String> pathVar, Map<String, Object> param, HttpServletRequest request, HttpServletResponse response) {
            RequestPack pack = new RequestPack(pathVar, param, request, response);
            MockParams mockParams = getAnnotation();
            return mockObject(new MockItem(mockParams.log(), mockParams.config(), mockParams.result(), mockParams.resultType()), pack);
        }

        @Override
        public String path(String sourcePath) {
            String path = getAnnotation().path();
            if (path.isEmpty()) {
                return paramsPathGenerator.urlGenerate(sourcePath);
            }
            return path;
        }
    }
}
