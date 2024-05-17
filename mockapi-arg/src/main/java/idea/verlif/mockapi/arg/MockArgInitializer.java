package idea.verlif.mockapi.arg;

import idea.verlif.mockapi.MockApiException;
import idea.verlif.reflection.util.FieldUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolverComposite;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class MockArgInitializer implements InitializingBean {

    @Autowired
    private RequestMappingHandlerAdapter requestMappingHandlerAdapter;
    @Autowired
    private ArgMockerCollector argMockerCollector;

    @Override
    public void afterPropertiesSet() throws Exception {
        // 这里通过反射将MockArgResolver添加到第一位
        Field argumentResolvers = RequestMappingHandlerAdapter.class.getDeclaredField("argumentResolvers");
        HandlerMethodArgumentResolverComposite resolverComposite = (HandlerMethodArgumentResolverComposite) FieldUtil.getFieldValue(requestMappingHandlerAdapter, argumentResolvers);
        if (resolverComposite != null) {
            List<HandlerMethodArgumentResolver> resolvers = (List<HandlerMethodArgumentResolver>) FieldUtil.getFieldValue(resolverComposite, "argumentResolvers");
            if (resolvers != null) {
                MockArgResolver argResolver = new MockArgResolver(argMockerCollector, new ArrayList<>(resolvers));
                resolvers.add(0, argResolver);
                return;
            }
        }
        throw new MockApiException("Cannot add MockArgResolver to RequestMappingHandlerAdapter");
    }
}
