package idea.verlif.mockapi.core;

import idea.verlif.mock.data.MockDataCreator;

public interface MockResultCreator {

    /**
     * 进行结果数据构造
     *
     * @param pack    请求参数
     * @param creator 全局数据构建器
     * @param target  结果类型
     */
    <T> T mock(RequestPack pack, MockDataCreator creator, Class<T> target);

}
