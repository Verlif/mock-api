package idea.verlif.mockapi.convert;

import idea.verlif.mockapi.MockApiException;
import idea.verlif.mockapi.MockItem;
import idea.verlif.reflection.domain.ClassGrc;
import idea.verlif.reflection.util.ReflectUtil;

import java.lang.annotation.Annotation;
import java.util.Map;

public interface MockAnnotationConvertor<A extends Annotation> {

    String GENERICS_KEY = "A";

    /**
     * 将注解信息转义到MckItem对象中
     *
     * @param a 注解对象
     * @return 虚拟接口信息
     */
    MockItem convert(A a);

    /**
     * 转义器能转义的类型
     */
    default Class<?> convertType() {
        Map<String, ClassGrc> genericsMap;
        try {
            genericsMap = ReflectUtil.getGenericsMap(getClass());
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            throw new MockApiException(exception);
        }
        ClassGrc grc = genericsMap.get(GENERICS_KEY);
        if (grc != null) {
            return grc.getTarget();
        }
        return null;
    }
}
