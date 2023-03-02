package idea.verlif.mockapi.mock;

import idea.verlif.mock.data.MockDataCreator;
import idea.verlif.mock.data.creator.InstanceCreator;
import idea.verlif.mock.data.creator.data.IntegerRandomCreator;
import idea.verlif.mockapi.config.MockApiConfig;
import idea.verlif.mockapi.core.MockResultCreator;
import idea.verlif.mockapi.core.RequestPack;
import idea.verlif.mockapi.global.domain.User;
import idea.verlif.mockapi.global.result.BaseResult;
import idea.verlif.mockapi.global.result.ResultCode;
import idea.verlif.mockapi.global.result.ext.FailResult;
import idea.verlif.mockapi.global.result.ext.OkResult;
import idea.verlif.reflection.domain.MethodGrc;
import idea.verlif.reflection.util.ReflectUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class MockResultCreatorImpl implements MockResultCreator, InitializingBean {

    @Autowired
    private MockApiConfig config;

    @Override
    public Object mock(RequestPack pack, MockDataCreator creator) {
        try {
            MethodGrc methodGrc = ReflectUtil.getMethodGrc(pack.getOldMethod(), pack.getMethodHolder().getClass());
            return creator.mock(methodGrc.getResult());
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        config.getMockDataCreator()
                .fieldValue(User::getUserId, new IntegerRandomCreator(0, 1000))
                // 对返回结果对象进行随机化
                .instanceCreator(new InstanceCreator<BaseResult<?>>() {

                    private final Random random = new Random();

                    @Override
                    public Class<?> matched() {
                        return BaseResult.class;
                    }

                    @Override
                    public BaseResult<?> newInstance() {
                        if (random.nextInt(10) > 1) {
                            return new OkResult<>();
                        } else return new FailResult<>(ResultCode.FAILURE.getMsg(), "");
                    }
                });
        config.getMockDataCreator().getConfig()
                .arraySize(10);
    }
}
