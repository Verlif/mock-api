package idea.verlif.test.mock;

import idea.verlif.mockapi.MockItem;
import idea.verlif.mockapi.ObjectMocker;
import idea.verlif.mockapi.RequestPack;

public class MyObjectMocker implements ObjectMocker {
    @Override
    public Object mock(MockItem item, RequestPack pack) {
        Class<?> returnType = pack.getOldMethod().getReturnType();
        try {
            return returnType.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
