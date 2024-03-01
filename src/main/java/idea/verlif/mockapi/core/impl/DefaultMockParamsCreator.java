package idea.verlif.mockapi.core.impl;

import idea.verlif.mock.data.MockDataCreator;
import idea.verlif.mock.data.config.MockDataConfig;
import idea.verlif.mockapi.core.RequestPack;
import idea.verlif.mockapi.core.creator.MockParamsCreator;
import idea.verlif.reflection.domain.ClassGrc;
import idea.verlif.reflection.domain.MethodGrc;
import idea.verlif.reflection.util.MethodUtil;
import idea.verlif.reflection.util.ReflectUtil;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

public class DefaultMockParamsCreator implements MockParamsCreator {

    @Override
    public Object mock(RequestPack pack, MockDataCreator creator, MockDataConfig config) {
        Method oldMethod = pack.getOldMethod();
        MethodGrc methodGrc;
        ClassGrc[] arguments;
        try {
            methodGrc = MethodUtil.getMethodGrc(oldMethod, pack.getHandlerMethod().getClass());
            arguments = methodGrc.getArguments();
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

        Map<String, Object> map = new HashMap<>();
        if (arguments.length > 0) {
            Parameter[] parameters = oldMethod.getParameters();
            for (int i = 0; i < arguments.length; i++) {
                ClassGrc argument = arguments[i];
                String name = getName(parameters[i]);
                map.put(name, creator.mock(argument));
            }
        }
        return map;
    }

    private static String getName(Parameter parameters) {
        String name = null;
        // 兼容RequestParam注解
        RequestParam param = parameters.getAnnotation(RequestParam.class);
        if (param != null) {
            if (!param.name().isEmpty()) {
                name = param.name();
            } else if (!param.value().isEmpty()) {
                name = param.value();
            }
        } else {
            name = parameters.getName();
        }
        return name;
    }
}
