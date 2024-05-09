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

@Component
public class ResultMocker implements ObjectMocker, MockAnnotationConvertor<MockResult> {

    @Autowired
    private MockDataCreator mockDataCreator;
    @Autowired
    private MockDataConfigCollector mockDataConfigCollector;

    @Override
    public MockItem convert(MockResult mockResult) {
        MockItem mockItem = new MockItem();
        mockItem.setLog(mockResult.log());
        mockItem.setPath(mockResult.path());
        mockItem.setMethods(mockResult.methods());
        mockItem.setData(mockResult.data());
        mockItem.setObjectMocker(this);
        return mockItem;
    }

    @Override
    public Class<MockResult> convertType() {
        return MockResult.class;
    }

    @Override
    public void resetPath(PathRecorder.Path path) {
        String oldPath = path.getPath();
        if (oldPath.isEmpty() || oldPath.charAt(0) == '/') {
            path.setPath("/result" + oldPath);
        } else {
            path.setPath("/result/" + oldPath);
        }
    }

    @Override
    public Object mock(MockItem item, RequestPack pack) {
        MethodGrc methodGrc;
        try {
            methodGrc = MethodUtil.getMethodGrc(pack.getOldMethod());
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        ClassGrc result = methodGrc.getResult();
        if (result.getTarget() == void.class) {
            return null;
        }
        MockDataConfig config = mockDataConfigCollector.getMockDataConfig(item.getData());
        if (config != null) {
            return mockDataCreator.mock(result, config);
        } else {
            return mockDataCreator.mock(result);
        }
    }
}
