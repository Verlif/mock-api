package idea.verlif.mockapi.convert;

import idea.verlif.mockapi.MockItem;
import idea.verlif.mockapi.ObjectMockerCollector;
import idea.verlif.mockapi.anno.MockApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MockApiConvertor implements MockAnnotationConvertor<MockApi> {

    @Autowired
    private ObjectMockerCollector objectMockerCollector;

    @Override
    public MockItem convert(MockApi mockApi) {
        MockItem mockItem = new MockItem();
        mockItem.setLog(mockApi.log());
        mockItem.setPath(mockApi.path());
        mockItem.setMethods(mockApi.methods());
        mockItem.setData(mockApi.data());
        mockItem.setObjectMocker(objectMockerCollector.getObjectMocker(mockApi.mocker()));
        return mockItem;
    }

}
