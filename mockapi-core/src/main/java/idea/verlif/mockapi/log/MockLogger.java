package idea.verlif.mockapi.log;

import idea.verlif.mockapi.RequestPack;

import java.lang.reflect.Method;

public interface MockLogger {

    void log(RequestPack pack, Method method, Object result);
}
