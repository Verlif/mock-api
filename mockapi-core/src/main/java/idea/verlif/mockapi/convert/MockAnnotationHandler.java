package idea.verlif.mockapi.convert;

import idea.verlif.mockapi.MockItem;
import idea.verlif.mockapi.anno.ConditionalOnMockEnabled;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

@Component
@ConditionalOnMockEnabled
@Import(MockApiConvertor.class)
public class MockAnnotationHandler {

    private final Map<Class<?>, MockAnnotationConvertor> mockAnnotationConvertors;

    public MockAnnotationHandler(ApplicationContext applicationContext) {
        this.mockAnnotationConvertors = new HashMap<>();
        for (MockAnnotationConvertor<?> convertor : applicationContext.getBeansOfType(MockAnnotationConvertor.class).values()) {
            putConvertor(convertor);
        }
    }

    public void putConvertor(MockAnnotationConvertor<?> convertor) {
        mockAnnotationConvertors.put(convertor.convertType(), convertor);
    }

    public MockItem convert(Annotation annotation) {
        MockAnnotationConvertor<Annotation> convertor = mockAnnotationConvertors.get(annotation.annotationType());
        if (convertor != null) {
            return convertor.convert(annotation);
        }
        return null;
    }

}
