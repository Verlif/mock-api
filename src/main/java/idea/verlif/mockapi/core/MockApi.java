package idea.verlif.mockapi.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import idea.verlif.mock.data.config.MockDataConfig;
import idea.verlif.mockapi.anno.MockParams;
import idea.verlif.mockapi.anno.MockResult;
import idea.verlif.mockapi.config.MockApiConfig;
import idea.verlif.mockapi.core.creator.MockParamsCreator;
import idea.verlif.mockapi.core.creator.MockResultCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

@Component
public class MockApi implements InitializingBean {

    private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private RequestMappingHandlerMapping handlerMapping;

    @Resource
    private MockApiConfig mockApiConfig;

    @Resource
    private MockResultCreator mockResultCreator;

    @Resource
    private MockParamsCreator mockParamsCreator;

    private final RequestMappingInfo.BuilderConfiguration builderConfiguration;
    private final Method resultMethod;
    private final Method paramsMethod;
    private final Map<String, MockDataConfig> configMap;

    public MockApi() throws NoSuchMethodException {
        builderConfiguration = new RequestMappingInfo.BuilderConfiguration();
        resultMethod = MockMethodHolder.class.getMethod("mockResult", Map.class, Map.class, HttpServletRequest.class, HttpServletResponse.class);
        paramsMethod = MockMethodHolder.class.getMethod("mockParams", Map.class, Map.class, HttpServletRequest.class, HttpServletResponse.class);
        configMap = new HashMap<>();
    }

    public void collectMockConfig() {
        configMap.putAll(applicationContext.getBeansOfType(MockDataConfig.class));
    }

    public MockDataConfig getMockConfig(String name) {
        return configMap.getOrDefault(name, mockApiConfig.getMockDataCreator().getConfig());
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
            // 获取注解
            MockResult result = getAnnotation(handlerMethod, MockResult.class);
            MockParams params = getAnnotation(handlerMethod, MockParams.class);
            MockMethodHolder mockMethodHolder = new MockMethodHolder(handlerMethod.getBean(), method, result, params);
            if (result != null) {
                RequestMappingInfo extraInfo = buildRequestMappingInfo(mappingInfo, mockApiConfig.getResultPath());
                handlerMapping.registerMapping(extraInfo, mockMethodHolder, resultMethod);
            }
            if (params != null) {
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

        collectMockConfig();
        register();
    }

    /**
     * 构建方法数据类
     */
    public final class MockMethodHolder {

        private final Object methodHolder;
        private final Method oldMethod;
        private final MockResult mockResult;
        private final MockParams mockParams;

        /**
         * 数据缓存
         */
        private Object object;
        private final Class<?> controllerClass;

        public MockMethodHolder(Object methodHolder, Method oldMethod, MockResult mockResult, MockParams mockParams) {
            this.methodHolder = methodHolder;
            this.oldMethod = oldMethod;
            this.mockResult = mockResult;
            this.mockParams = mockParams;
            this.controllerClass = oldMethod.getDeclaringClass();
        }

        @ResponseBody
        public Object mockResult(Map<String, String> pathVar, Map<String, Object> param, HttpServletRequest request, HttpServletResponse response) {
            Object o = mockObjectWithCache(mockResultCreator, pathVar, param, request, response, getMockConfig(mockResult.config()));
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
            // 缓存数据
            if (mockResult.cacheable()) {
                object = o;
            } else {
                object = null;
            }
            return o;
        }

        @ResponseBody
        public Object mockParams(Map<String, String> pathVar, Map<String, Object> param, HttpServletRequest request, HttpServletResponse response) {
            Object o = mockObjectWithCache(mockParamsCreator, pathVar, param, request, response, getMockConfig(mockParams.config()));
            // 缓存数据
            if (mockParams.cacheable()) {
                object = o;
            } else {
                object = null;
            }
            return o;
        }

        private Object mockObjectWithCache(MockObject mockObject, Map<String, String> pathVar, Map<String, Object> param, HttpServletRequest request, HttpServletResponse response, MockDataConfig config) {
            Object o;
            if (object == null) {
                RequestPack pack = new RequestPack(pathVar, param, request, response, methodHolder, oldMethod);
                o = mockObject.mock(pack, mockApiConfig.getMockDataCreator(), config);
            } else {
                o = object;
            }
            return o;
        }
    }

}
