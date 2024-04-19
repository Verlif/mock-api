package idea.verlif.mockapi.log.impl;

import idea.verlif.mockapi.core.RequestPack;
import idea.verlif.mockapi.log.MockLogger;

import java.lang.reflect.Method;

public class NoMockLogger implements MockLogger {

    @Override
    public void log(RequestPack pack, Method method, Object result) {
    }
}
