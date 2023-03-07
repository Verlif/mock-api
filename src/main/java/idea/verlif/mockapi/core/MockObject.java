package idea.verlif.mockapi.core;

import idea.verlif.mock.data.MockDataCreator;
import idea.verlif.mock.data.config.MockDataConfig;

public interface MockObject {

    /**
     * 进行参数数据构造
     *
     * @param pack    请求参数
     * @param creator 全局数据构建器
     * @param config  当前mock的配置
     */
    Object mock(RequestPack pack, MockDataCreator creator, MockDataConfig config);
}
