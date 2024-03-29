package idea.verlif.test.mock;

import idea.verlif.mock.data.MockDataCreator;
import idea.verlif.mock.data.config.MockDataConfig;
import idea.verlif.mock.data.creator.InstanceCreator;
import idea.verlif.mock.data.creator.data.IntegerRandomCreator;
import idea.verlif.mockapi.anno.ConditionalOnMockEnabled;
import idea.verlif.mockapi.core.RequestPack;
import idea.verlif.mockapi.core.creator.MockResultCreator;
import idea.verlif.test.global.domain.User;
import idea.verlif.test.global.result.BaseResult;
import idea.verlif.test.global.result.ext.FailResult;
import idea.verlif.test.global.result.ext.OkResult;
import idea.verlif.reflection.domain.ClassGrc;
import idea.verlif.reflection.domain.MethodGrc;
import idea.verlif.reflection.util.MethodUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@ConditionalOnMockEnabled
public class MockResultCreatorImpl implements MockResultCreator, InitializingBean {

    @Autowired
    private MockDataCreator creator;

    @Override
    public Object mock(RequestPack pack, MockDataCreator creator, MockDataConfig config) {
        try {
            MethodGrc methodGrc = MethodUtil.getMethodGrc(pack.getOldMethod(), pack.getMethodHandle().getClass());
            ClassGrc result = methodGrc.getResult();
            if (result.getTarget() == void.class) {
                return null;
            }
            return creator.mock(result, config);
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        creator.fieldValue(User::getUserId, new IntegerRandomCreator(0, 1000))
                // 对返回结果对象进行随机化
                .instanceCreator(new InstanceCreator<BaseResult<?>>() {

                    private final Random random = new Random();

                    @Override
                    public BaseResult<?> newInstance(MockDataCreator mockDataCreator) {
                        if (random.nextInt(10) > 1) {
                            return new OkResult<>();
                        } else return FailResult.empty();
                    }
                });
        creator.getConfig()
                .useSetter(true)
                .arraySize(10);
    }
}
