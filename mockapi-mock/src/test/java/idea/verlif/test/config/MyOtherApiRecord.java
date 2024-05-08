package idea.verlif.test.config;

import idea.verlif.mockapi.MockApiRegister;
import idea.verlif.mockapi.anno.ConditionalOnMockEnabled;
import idea.verlif.mockapi.config.PathRecorder;
import idea.verlif.mockapi.MockApiBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@ConditionalOnMockEnabled
@AutoConfigureBefore(MockApiRegister.class)
public class MyOtherApiRecord implements ApplicationRunner {

    @Autowired
    private PathRecorder pathRecorder;

    //    @PostConstruct
    public void otherRecord() {
        // 将当前类定义的所有公共方法添加到构建目录
        pathRecorder.add(PathRecorder.Path.EMPTY, PathRecorder.Path.generate(this));
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        for (PathRecorder.Path sourcePath : pathRecorder) {
            PathRecorder.Path targetPath = pathRecorder.getValue(sourcePath);
            System.out.println(
                    sourcePath.getPath() + Arrays.toString(sourcePath.getRequestMethods()) + " -> "
                            + targetPath.getPath() + Arrays.toString(targetPath.getRequestMethods()));
        }
    }
}
