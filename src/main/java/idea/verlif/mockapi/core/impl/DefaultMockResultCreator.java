package idea.verlif.mockapi.core.impl;

import idea.verlif.mock.data.MockDataCreator;
import idea.verlif.mock.data.config.MockDataConfig;
import idea.verlif.mockapi.core.RequestPack;
import idea.verlif.mockapi.core.creator.MockResultCreator;
import idea.verlif.reflection.domain.MethodGrc;
import idea.verlif.reflection.util.MethodUtil;

/**
 * 默认结果构造器
 */
public class DefaultMockResultCreator implements MockResultCreator {

    @Override
    public Object mock(RequestPack pack, MockDataCreator creator, MockDataConfig config) {
        MethodGrc methodGrc;
        try {
            methodGrc = MethodUtil.getMethodGrc(pack.getOldMethod(), pack.getHandlerMethod().getClass());
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        return creator.mock(methodGrc.getResult(), config);
    }

}
