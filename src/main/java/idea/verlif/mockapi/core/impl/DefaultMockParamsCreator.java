package idea.verlif.mockapi.core.impl;

import idea.verlif.mock.data.MockDataCreator;
import idea.verlif.mockapi.core.RequestPack;
import idea.verlif.mockapi.core.creator.MockParamsCreator;
import idea.verlif.reflection.domain.ClassGrc;
import idea.verlif.reflection.domain.MethodGrc;
import idea.verlif.reflection.util.ReflectUtil;

import java.util.HashMap;
import java.util.Map;

public class DefaultMockParamsCreator implements MockParamsCreator {

    @Override
    public Object mock(RequestPack pack, MockDataCreator creator) {
        try {
            MethodGrc methodGrc = ReflectUtil.getMethodGrc(pack.getOldMethod(), pack.getMethodHolder().getClass());
            ClassGrc[] arguments = methodGrc.getArguments();
            if (arguments.length == 0) {
                return null;
            } else if (arguments.length == 1) {
                return creator.mock(arguments[0]);
            } else {
                Map<String, Object> map = new HashMap<>();
                for (int i = 0; i < arguments.length; i++) {
                    ClassGrc argument = arguments[i];
                    String name = argument.getTarget().getName();
                    if (map.containsKey(name)) {
                        name += i;
                    }
                    map.put(name, creator.mock(argument));
                }
                return map;
            }
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
