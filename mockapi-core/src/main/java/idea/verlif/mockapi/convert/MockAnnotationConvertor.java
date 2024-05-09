package idea.verlif.mockapi.convert;

import idea.verlif.mockapi.MockItem;

import java.lang.annotation.Annotation;

public interface MockAnnotationConvertor<A extends Annotation> {

    MockItem convert(A a);

    Class<A> convertType();
}
