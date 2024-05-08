package idea.verlif.mockapi;

import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;

public class ObjectMockerCollector {

    private final Map<Class<? extends ObjectMocker>, ObjectMocker> mockerMap;

    public ObjectMockerCollector(ApplicationContext context) {
        mockerMap = new HashMap<>();
        // 预先载入开发者注入的对象
        Map<String, ObjectMocker> beansOfType = context.getBeansOfType(ObjectMocker.class);
        for (Map.Entry<String, ObjectMocker> mockerEntry : beansOfType.entrySet()) {
            ObjectMocker mocker = mockerEntry.getValue();
            putObjectMocker(mocker);
        }
    }

    public void putObjectMocker(ObjectMocker objectMocker) {
        mockerMap.put(objectMocker.getClass(), objectMocker);
    }

    public ObjectMocker getObjectMocker(Class<? extends ObjectMocker> clazz) {
        ObjectMocker objectMocker = mockerMap.get(clazz);
        if (objectMocker == null) {
            try {
                objectMocker = clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new MockApiException(e);
            }
            mockerMap.put(clazz, objectMocker);
        }
        return objectMocker;
    }

}
