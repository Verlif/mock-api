package idea.verlif.mockapi.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import idea.verlif.mock.data.MockDataCreator;
import idea.verlif.mock.data.config.MockDataConfig;
import idea.verlif.mockapi.anno.MockParams;
import idea.verlif.mockapi.anno.MockResult;
import idea.verlif.mockapi.config.MockApiConfig;
import idea.verlif.mockapi.core.creator.MockParamsCreator;
import idea.verlif.mockapi.core.creator.MockResultCreator;
import idea.verlif.parser.ParamParserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.api.AbstractOpenApiResource;
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
    private RequestMappingHandlerMapping handlerMapping;

    @Autowired
    private MockApiConfig mockApiConfig;

    @Autowired
    private MockDataCreator creator;

    @Autowired
    private MockResultCreator mockResultCreator;

    @Autowired
    private MockParamsCreator mockParamsCreator;

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
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = handlerMapping.getHandlerMethods();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> methodEntry : handlerMethods.entrySet()) {
            HandlerMethod handlerMethod = methodEntry.getValue();
            RequestMappingInfo mappingInfo = methodEntry.getKey();
            Method method = methodEntry.getValue().getMethod();

            // 结果mock
            MockResult result = getAnnotation(handlerMethod, MockResult.class);
            if (result != null) {
                MockResultMethodHolder mockMethodHolder = new MockResultMethodHolder(handlerMethod, method, result);
                RequestMappingInfo extraInfo = buildRequestMappingInfo(mappingInfo, mockApiConfig.getResultPath());
                handlerMapping.registerMapping(extraInfo, mockMethodHolder, resultMethod);
            }
            // 入参mock
            MockParams params = getAnnotation(handlerMethod, MockParams.class);
            if (params != null) {
                MockParamsMethodHolder mockMethodHolder = new MockParamsMethodHolder(handlerMethod, method, params);
                RequestMappingInfo extraInfo = buildRequestMappingInfo(mappingInfo, mockApiConfig.getParamsPath());
                handlerMapping.registerMapping(extraInfo, mockMethodHolder, paramsMethod);
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

    /**
     * 构建新的请求处理信息
     *
     * @param source 源请求处理信息
     * @param path   附加地址信息
     */
    private RequestMappingInfo buildRequestMappingInfo(RequestMappingInfo source, MockApiConfig.Path path) {
        // 构造调用地址
        Set<String> pathSets = new HashSet<>();
        Set<String> patternValues = source.getPatternValues();
        for (String value : patternValues) {
            if (path.getPosition() == MockApiConfig.POSITION.PREFIX) {
                pathSets.add(path.getValue() + value);
            } else {
                pathSets.add(value + "/" + path.getValue());
            }
        }
        // 获取调用方法
        Set<RequestMethod> set = source.getMethodsCondition().getMethods();
        Set<RequestMethod> newSet = new HashSet<>(set);
        // 填充方法
        if (newSet.size() == 0) {
            newSet.addAll(Arrays.asList(RequestMethod.values()));
        }
        return RequestMappingInfo.paths(pathSets.toArray(new String[0]))
                .methods(newSet.toArray(new RequestMethod[0]))
                .options(builderConfiguration)
                .build();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        builderConfiguration.setTrailingSlashMatch(handlerMapping.useTrailingSlashMatch());
        builderConfiguration.setContentNegotiationManager(handlerMapping.getContentNegotiationManager());
        if (handlerMapping.getPatternParser() != null) {
            builderConfiguration.setPatternParser(handlerMapping.getPatternParser());
        } else {
            builderConfiguration.setPathMatcher(handlerMapping.getPathMatcher());
        }

        // 向OpenApi中添加额外的类
        AbstractOpenApiResource.addRestControllers(MockParamsMethodHolder.class);
        AbstractOpenApiResource.addRestControllers(MockResultMethodHolder.class);
        collectMockConfig();
        register();
    }

    /**
     * 构建方法数据类
     */
    public class MockMethodHolder<T extends Annotation> {

        private final HandlerMethod methodHolder;
        private final Method oldMethod;
        private final T annotation;

        /**
         * 数据缓存
         */
        private Object object;

        public MockMethodHolder(HandlerMethod handlerMethod, Method oldMethod, T annotation) {
            this.methodHolder = handlerMethod;
            this.oldMethod = oldMethod;
            this.annotation = annotation;
        }

        public T getAnnotation() {
            return annotation;
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

    }

    public final class MockResultMethodHolder extends MockMethodHolder<MockResult> {

        private final Class<?> controllerClass;

        public MockResultMethodHolder(HandlerMethod handlerMethod, Method oldMethod, MockResult mockResult) {
            super(handlerMethod, oldMethod, mockResult);
            this.controllerClass = oldMethod.getDeclaringClass();
        }

        @ResponseBody
        public Object mockResult(Map<String, String> pathVar, Map<String, Object> param, HttpServletRequest request, HttpServletResponse response) {
            RequestPack pack = new RequestPack(pathVar, param, request, response);
            MockResult mockResult = getAnnotation();
            if (mockResult.result().length() > 0) {
                return paramParserService.parse(mockResult.resultType(), mockResult.result());
            }
            Object o = mockObjectWithCache(mockResultCreator, pack, getMockConfig(mockResult.config()), mockResult.cacheable());
            // 日志输出
            if (mockResult.log()) {
                Logger logger = LoggerFactory.getLogger(controllerClass);
                String result;
                try {
                    result = OBJECT_MAPPER.writeValueAsString(o);
                } catch (JsonProcessingException e) {
                    result = o.toString();
                }
                logger.debug(request.getMethod() + " - " + request.getRequestURL() + " - " + result);
            }
            return o;
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
            return mockObjectWithCache(mockParamsCreator, pack, getMockConfig(mockParams.config()), mockParams.cacheable());
        }
    }
}
