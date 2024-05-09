package idea.verlif.mockapi.mock;

import idea.verlif.mock.data.MockDataCreator;
import idea.verlif.mock.data.config.MockDataConfig;
import idea.verlif.mockapi.config.PathRecorder;
import idea.verlif.mockapi.MockItem;
import idea.verlif.mockapi.ObjectMocker;
import idea.verlif.mockapi.RequestPack;
import idea.verlif.mockapi.convert.MockAnnotationConvertor;
import idea.verlif.mockapi.mock.config.MockDataConfigCollector;
import idea.verlif.reflection.domain.ClassGrc;
import idea.verlif.reflection.domain.MethodGrc;
import idea.verlif.reflection.util.MethodUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

@Component
public class ParamsMocker implements ObjectMocker, MockAnnotationConvertor<MockParams> {

    @Autowired
    private MockDataCreator mockDataCreator;
    @Autowired
    private MockDataConfigCollector mockDataConfigCollector;

    @Override
    public MockItem convert(MockParams mockParams) {
        MockItem mockItem = new MockItem();
        mockItem.setLog(mockParams.log());
        mockItem.setPath(mockParams.path());
        mockItem.setMethods(mockParams.methods());
        mockItem.setData(mockParams.data());
        mockItem.setObjectMocker(this);
        return mockItem;
    }

    @Override
    public Class<MockParams> convertType() {
        return MockParams.class;
    }

    @Override
    public void resetPath(PathRecorder.Path path) {
        String oldPath = path.getPath();
        if (oldPath.isEmpty() || oldPath.charAt(0) == '/') {
            path.setPath("/params" + oldPath);
        } else {
            path.setPath("/params/" + oldPath);
        }
    }

    @Override
    public Object mock(MockItem item, RequestPack pack) {
        Method oldMethod = pack.getOldMethod();
        MethodGrc methodGrc;
        ClassGrc[] arguments;
        try {
            methodGrc = MethodUtil.getMethodGrc(oldMethod);
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        arguments = methodGrc.getArguments();
        Map<String, Object> map = new HashMap<>();
        if (arguments.length > 0) {
            Parameter[] parameters = oldMethod.getParameters();
            for (int i = 0; i < arguments.length; i++) {
                ClassGrc argument = arguments[i];
                String name = getName(parameters[i]);
                map.put(name, mockDataCreator.mock(argument, getMockDataConfig(item.getData())));
            }
        }
        return map;
    }

    private MockDataConfig getMockDataConfig(String configName) {
        MockDataConfig config = mockDataConfigCollector.getMockDataConfig(configName);
        if (config == null) {
            config = mockDataCreator.getConfig();
        }
        return config;
    }

    private String getName(Parameter parameters) {
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
