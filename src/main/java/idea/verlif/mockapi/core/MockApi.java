package idea.verlif.mockapi.core;

import idea.verlif.mock.data.MockDataCreator;
import idea.verlif.mockapi.anno.MockResult;
import idea.verlif.mockapi.config.MockApiConfig;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class MockApi implements InitializingBean {

    @Resource
    private RequestMappingHandlerMapping handlerMapping;

    @Resource
    private MockApiConfig mockApiConfig;

    @Resource
    private MockDataCreator mockDataCreator;

    @Resource
    private MockResultCreator mockResultCreator;

    private final RequestMappingInfo.BuilderConfiguration config;
    private final Method mockMethod;

    public MockApi() throws NoSuchMethodException {
        config = new RequestMappingInfo.BuilderConfiguration();
        mockMethod = MockMethodHolder.class.getMethod("mock", Map.class, Map.class, HttpServletRequest.class, HttpServletResponse.class);
    }

    /**
     * 注册新的请求处理
     */
    public void register() {
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = handlerMapping.getHandlerMethods();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> methodEntry : handlerMethods.entrySet()) {
            HandlerMethod handlerMethod = methodEntry.getValue();
            MockResult result = handlerMethod.getMethodAnnotation(MockResult.class);
            if (result == null) {
                result = handlerMethod.getBeanType().getAnnotation(MockResult.class);
            }
            if (result != null) {
                RequestMappingInfo mappingInfo = methodEntry.getKey();
                RequestMappingInfo extraInfo = buildRequestMappingInfo(mappingInfo);
                Method method = methodEntry.getValue().getMethod();
                MockMethodHolder mockMethodHolder = new MockMethodHolder(method);
                handlerMapping.registerMapping(extraInfo, mockMethodHolder, mockMethod);
            }
        }
    }

    /**
     * 构建新的请求处理信息
     *
     * @param source 源请求处理信息
     */
    private RequestMappingInfo buildRequestMappingInfo(RequestMappingInfo source) {
        // 构造调用地址
        Set<String> pathSets = new HashSet<>();
        Set<String> patternValues = source.getPatternValues();
        for (String value : patternValues) {
            if (mockApiConfig.getPosition() == MockApiConfig.POSITION.PREFIX) {
                pathSets.add(mockApiConfig.getPath() + value);
            } else {
                pathSets.add(value + "/" + mockApiConfig.getPath());
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
                .options(config)
                .build();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        config.setTrailingSlashMatch(handlerMapping.useTrailingSlashMatch());
        config.setContentNegotiationManager(handlerMapping.getContentNegotiationManager());
        if (handlerMapping.getPatternParser() != null) {
            config.setPatternParser(handlerMapping.getPatternParser());
        } else {
            config.setPathMatcher(handlerMapping.getPathMatcher());
        }

        register();
    }

    public final class MockMethodHolder {

        private final Method oldMethod;

        public MockMethodHolder(Method oldMethod) {
            this.oldMethod = oldMethod;
        }

        @ResponseBody
        public Object mock(Map<String, String> pathVar, Map<String, Object> param, HttpServletRequest request, HttpServletResponse response) {
            RequestPack pack = new RequestPack(pathVar, param, request, response, oldMethod);
            return mockResultCreator.mock(pack, mockDataCreator, oldMethod.getReturnType());
        }
    }
}
