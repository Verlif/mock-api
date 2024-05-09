package idea.verlif.test.mock;

import idea.verlif.mockapi.MockApiRegister;
import idea.verlif.mockapi.config.PathRecorder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@AutoConfigureBefore(MockApiRegister.class)
public class OtherRecorder {

    @Autowired
    private PathRecorder pathRecorder;

    @PostConstruct
    public void init() {
        pathRecorder.add(PathRecorder.Path.generate(Test.class));
    }

    interface Test {

        int i();
    }

}
