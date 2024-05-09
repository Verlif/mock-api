package idea.verlif.mockapi.log;

import idea.verlif.mockapi.RequestPack;

import java.lang.reflect.Method;

/**
 * 虚拟接口日志
 */
public interface MockLogger {

    /**
     * 日志记录
     *
     * @param pack   请求信息包
     * @param method 访问方法
     * @param result 请求返回数据对象
     */
    void log(RequestPack pack, Method method, Object result);
}
