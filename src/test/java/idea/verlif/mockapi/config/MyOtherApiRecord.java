package idea.verlif.mockapi.config;

import idea.verlif.mock.data.MockDataCreator;
import idea.verlif.mockapi.anno.MockResult;
import idea.verlif.mockapi.core.MockApiBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;

@Component
@AutoConfigureBefore(MockApiBuilder.class)
public class MyOtherApiRecord {

    @Autowired
    private PathRecorder pathRecorder;
    @Autowired
    private MockDataCreator mockDataCreator;

    @PostConstruct
    public void otherRecord() {
        pathRecorder.add(PathRecorder.Path.EMPTY, PathRecorder.Path.generate(this, MyOtherApiRecord::wuhu));
        pathRecorder.add(PathRecorder.Path.EMPTY, PathRecorder.Path.generate(this, MyOtherApiRecord::mock));
    }

    @MockResult
    @ResponseBody
    public String wuhu() {
        return "123";
    }

    @MockResult
    @ResponseBody
    public String mock() {
        return mockDataCreator.mock(String.class);
    }
}
