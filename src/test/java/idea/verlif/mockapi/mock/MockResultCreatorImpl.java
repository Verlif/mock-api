package idea.verlif.mockapi.mock;

import idea.verlif.mock.data.MockDataCreator;
import idea.verlif.mockapi.core.MockResultCreator;
import idea.verlif.mockapi.core.RequestPack;
import org.springframework.stereotype.Component;

@Component
public class MockResultCreatorImpl implements MockResultCreator {

    @Override
    public <T> T mock(RequestPack pack, MockDataCreator creator, Class<T> target) {
        System.out.println("正在构建...");
        return creator.mock(target);
    }

}
