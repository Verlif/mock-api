package idea.verlif.mockapi.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import idea.verlif.mock.data.MockDataCreator;
import idea.verlif.mock.data.config.MockDataConfig;
import idea.verlif.mockapi.anno.MockParams;
import idea.verlif.mockapi.anno.MockResult;
import idea.verlif.mockapi.core.creator.MockParamsPathGenerator;
import idea.verlif.mockapi.core.creator.MockResultCreator;
import idea.verlif.mockapi.core.creator.MockResultPathGenerator;
import idea.verlif.mockapi.core.creator.PathGenerator;
import idea.verlif.parser.ParamParserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

@Component
public class MockApi implements InitializingBean {

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

    private final RequestMappingInfo.BuilderConfiguration builderConfiguration;
    private final Method resultMethod;
    private final Method paramsMethod;
    private final Map<String, MockDataConfig> configMap;

    public MockApi() throws NoSuchMethodException {
        builderConfiguration = new RequestMappingInfo.BuilderConfiguration();
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
    public void register() {
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> methodEntry : handlerMethods.entrySet()) {
            HandlerMethod handlerMethod = methodEntry.getValue();
            RequestMappingInfo mappingInfo = methodEntry.getKey();
            Method method = methodEntry.getValue().getMethod();

            // 结果mock
            MockResult result = getAnnotation(handlerMethod, MockResult.class);
            if (result != null) {
                MockResultMethodHolder mockMethodHolder = new MockResultMethodHolder(handlerMethod, method, result);
                RequestMappingInfo extraInfo = buildResultRequestMappingInfo(mappingInfo);
                requestMappingHandlerMapping.registerMapping(extraInfo, mockMethodHolder, resultMethod);
            }
            // 入参mock
            MockParams params = getAnnotation(handlerMethod, MockParams.class);
            if (params != null) {
                MockParamsMethodHolder mockMethodHolder = new MockParamsMethodHolder(handlerMethod, method, params);
                RequestMappingInfo extraInfo = buildParamsRequestMappingInfo(mappingInfo);
                requestMappingHandlerMapping.registerMapping(extraInfo, mockMethodHolder, paramsMethod);
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

    private RequestMappingInfo buildParamsRequestMappingInfo(RequestMappingInfo source) {
        // 构造调用地址
        Set<String> pathSets = pathSets(source.getPatternValues(), paramsPathGenerator);
        return buildRequestMappingInfo(source, pathSets);
    }

    private RequestMappingInfo buildResultRequestMappingInfo(RequestMappingInfo source) {
        // 构造调用地址
        Set<String> pathSets = pathSets(source.getPatternValues(), resultPathGenerator);
        return buildRequestMappingInfo(source, pathSets);
    }

    private Set<String> pathSets(Set<String> source, PathGenerator generator) {
        Set<String> pathSets = new HashSet<>();
        for (String value : source) {
            pathSets.add(generator.urlGenerate(value));
        }
        return pathSets;
    }

    /**
     * 构建新的请求处理信息
     *
     * @param source 源请求处理信息
     */
    private RequestMappingInfo buildRequestMappingInfo(RequestMappingInfo source, Set<String> pathSets) {
        // 获取调用方法
        Set<RequestMethod> set = source.getMethodsCondition().getMethods();
        Set<RequestMethod> newSet = new HashSet<>(set);
        // 填充方法
        if (newSet.isEmpty()) {
            newSet.addAll(Arrays.asList(RequestMethod.values()));
        }
        return RequestMappingInfo.paths(pathSets.toArray(new String[0]))
                .methods(newSet.toArray(new RequestMethod[0]))
                .options(builderConfiguration)
                .build();
    }

    @Override
    public void afterPropertiesSet() {
        builderConfiguration.setTrailingSlashMatch(requestMappingHandlerMapping.useTrailingSlashMatch());
        builderConfiguration.setContentNegotiationManager(requestMappingHandlerMapping.getContentNegotiationManager());
        if (requestMappingHandlerMapping.getPatternParser() != null) {
            builderConfiguration.setPatternParser(requestMappingHandlerMapping.getPatternParser());
        } else {
            builderConfiguration.setPathMatcher(requestMappingHandlerMapping.getPathMatcher());
        }
        collectMockConfig();
        register();
    }

    /**
     * 构建方法数据类
     */
    public class MockMethodHolder<T extends Annotation> {

        protected final HandlerMethod methodHolder;
        protected final Method oldMethod;
        protected final T annotation;
        protected final Class<?> controllerClass;

        /**
         * 数据缓存
         */
        private Object object;

        public MockMethodHolder(HandlerMethod handlerMethod, Method oldMethod, T annotation) {
            this.methodHolder = handlerMethod;
            this.oldMethod = oldMethod;
            this.annotation = annotation;
            this.controllerClass = oldMethod.getDeclaringClass();
        }

        public T getAnnotation() {
            return annotation;
        }

        protected Object mockObject(MockItem mockItem, RequestPack pack) {
            if (!mockItem.getResult().isEmpty()) {
                return paramParserService.parse(mockItem.getResultType(), mockItem.getResult());
            }
            Object o = mockObjectWithCache(mockResultCreator, pack, getMockConfig(mockItem.getConfig()), mockItem.isCacheable());
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

        protected Object mockObjectWithCache(ObjectMocker objectMocker, RequestPack pack, MockDataConfig config, boolean cacheable) {
            pack.setOldMethod(oldMethod);
            pack.setHandlerMethod(methodHolder);
            Object o;
            if (object == null) {
                o = objectMocker.mock(pack, creator, config);
            } else {
                o = object;
            }
            // 缓存数据
            if (cacheable) {
                object = o;
            } else {
                object = null;
            }
            return o;
        }

        public class MockItem {

            /**
             * 使用缓存。在第一次构建后，后续请求会直接返回第一次构建的数据。
             */
            private boolean cacheable;

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

            public MockItem(boolean cacheable, boolean log, String config, String result, Class<?> resultType) {
                this.cacheable = cacheable;
                this.log = log;
                this.config = config;
                this.result = result;
                this.resultType = resultType;
            }

            public boolean isCacheable() {
                return cacheable;
            }

            public void setCacheable(boolean cacheable) {
                this.cacheable = cacheable;
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

        @ResponseBody
        public Object mockResult(Map<String, String> pathVar, Map<String, Object> param, HttpServletRequest request, HttpServletResponse response) {
            RequestPack pack = new RequestPack(pathVar, param, request, response);
            MockResult mockResult = getAnnotation();
            return mockObject(new MockItem(mockResult.cacheable(), mockResult.log(), mockResult.config(), mockResult.result(), mockResult.resultType()), pack);
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
            return mockObject(new MockItem(mockParams.cacheable(), mockParams.log(), mockParams.config(), mockParams.result(), mockParams.resultType()), pack);
        }
    }
}
