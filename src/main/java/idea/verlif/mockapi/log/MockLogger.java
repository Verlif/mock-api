package idea.verlif.mockapi.log;

import idea.verlif.mockapi.core.RequestPack;

import java.lang.reflect.Method;

public interface MockLogger {

    void log(RequestPack pack, Object methodHandle, Method method, Object result);
}
