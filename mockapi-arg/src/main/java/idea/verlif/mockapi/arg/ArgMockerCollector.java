package idea.verlif.mockapi.arg;

import idea.verlif.mockapi.MockApiException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ArgMockerCollector {

    private final Map<Class<? extends ArgMocker>, ArgMocker> mockerMap;

    public ArgMockerCollector(ApplicationContext context) {
        this.mockerMap = new HashMap<>();
        // 加载默认构建器
        putArgMocker(DefaultArgMocker.DEFAULT);
        // 预先载入开发者注入的构建器
        Map<String, ArgMocker> beansOfType = context.getBeansOfType(ArgMocker.class);
        for (Map.Entry<String, ArgMocker> mockerEntry : beansOfType.entrySet()) {
            ArgMocker mocker = mockerEntry.getValue();
            putArgMocker(mocker);
        }
    }

    public void putArgMocker(ArgMocker mocker) {
        this.mockerMap.put(mocker.getClass(), mocker);
    }

    public ArgMocker getArgMocker(Class<? extends ArgMocker> clazz) {
        ArgMocker argMocker = mockerMap.get(clazz);
        if (argMocker == null) {
            try {
                argMocker = clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new MockApiException(e);
            }
            mockerMap.put(clazz, argMocker);
        }
        return argMocker;
    }

}
