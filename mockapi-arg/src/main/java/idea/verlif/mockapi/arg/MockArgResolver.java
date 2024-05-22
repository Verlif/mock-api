package idea.verlif.mockapi.arg;

import idea.verlif.mockapi.MockApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolverComposite;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockArgResolver extends HandlerMethodArgumentResolverComposite {

    private static final Logger LOGGER = LoggerFactory.getLogger(MockArgResolver.class);

    private final ArgMockerCollector argMockerCollector;
    private final Map<MethodParameter, MockArg> mockArgCache;

    public MockArgResolver(ArgMockerCollector argMockerCollector, List<HandlerMethodArgumentResolver> resolvers) {
        addResolvers(resolvers);
        this.argMockerCollector = argMockerCollector;
        this.mockArgCache = new HashMap<>();
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        MockArg mockArg = getMockArg(parameter);
        if (mockArg != null) {
            mockArgCache.put(parameter, mockArg);
            return mockArg.enabled();
        }
        return false;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        MockArg mockArg = mockArgCache.get(parameter);
        if (mockArg == null) {
            throw new MockApiException("Unsupported parameter - " + parameter.getParameterName());
        }
        Object value = null;
        if (!mockArg.force()) {
            value = getObject(parameter, mavContainer, webRequest, binderFactory);
        }
        if (value == null) {
            ArgMocker argMocker = argMockerCollector.getArgMocker(mockArg.mocker());
            value = argMocker.mock(parameter, mockArg.data());
        }
        return value;
    }

    private MockArg getMockArg(MethodParameter parameter) {
        MockArg mockArg = parameter.getParameterAnnotation(MockArg.class);
        if (mockArg == null) {
            mockArg = parameter.getMethodAnnotation(MockArg.class);
        }
        if (mockArg == null) {
            mockArg = parameter.getDeclaringClass().getAnnotation(MockArg.class);
        }
        return mockArg;
    }

    private Object getObject(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        try {
            return super.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
        } catch (Exception e) {
            // 当遇到参数问题时，直接构造
            LOGGER.warn(e.getMessage());
        }
        return null;
    }
}
