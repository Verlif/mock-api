package idea.verlif.mockapi;

import idea.verlif.mockapi.config.MyOtherApiRecord;
import idea.verlif.mockapi.config.PathRecorder;
import idea.verlif.mockapi.core.RequestPack;
import idea.verlif.reflection.util.MethodUtil;
import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

public class TestMain {

    @Test
    public void test() {
        MyOtherApiRecord myOtherApiRecord = new MyOtherApiRecord();
        Class<? extends MyOtherApiRecord> cla = myOtherApiRecord.getClass();
        List<Method> methods = MethodUtil.getAllMethods(cla);
        for (Method method : methods) {
            System.out.println(method.getName() + " - " + Modifier.isPublic(method.getModifiers()) + " - " + method.getDeclaringClass());
        }
    }
}
