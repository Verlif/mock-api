package idea.verlif.mockapi.core.creator;

import idea.verlif.mock.data.MockDataCreator;
import idea.verlif.mockapi.core.MockResultCreator;
import idea.verlif.mockapi.core.RequestPack;
import idea.verlif.reflection.domain.MethodGrc;
import idea.verlif.reflection.util.ReflectUtil;

/**
 * 默认结果构造器
 */
public class DefaultMockResultCreator implements MockResultCreator {

    @Override
    public Object mock(RequestPack pack, MockDataCreator creator) {
        try {
            MethodGrc methodGrc = ReflectUtil.getMethodGrc(pack.getOldMethod(), pack.getMethodHolder().getClass());
            return creator.mock(methodGrc.getResult());
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

}
