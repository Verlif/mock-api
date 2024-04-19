package idea.verlif.test.config;

import idea.verlif.mockapi.anno.ConditionalOnMockEnabled;
import idea.verlif.mockapi.anno.MockResult;
import idea.verlif.mockapi.config.PathRecorder;
import idea.verlif.test.controller.HelloController;
import idea.verlif.mockapi.core.MockApiBuilder;
import idea.verlif.mockapi.core.MockItem;
import idea.verlif.test.global.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.lang.reflect.Modifier;
import java.util.Arrays;

@Component
@ConditionalOnMockEnabled
@AutoConfigureBefore(MockApiBuilder.class)
public class MyOtherApiRecord implements ApplicationRunner {

    @Autowired
    private PathRecorder pathRecorder;
    @Autowired
    private HelloController helloController;

    @PostConstruct
    public void otherRecord() {
        // 将当前类定义的所有公共方法添加到构建目录
        pathRecorder.add(PathRecorder.Path.EMPTY, PathRecorder.Path.generate(this, PathRecorder.MethodSign.RESULT));
        // 手动将controller接口添加到构建目录，实现非侵入式构建
        PathRecorder.Path[] paths = PathRecorder.Path.generate(
                helloController,
                m -> Modifier.isPublic(m.getModifiers()) && m.getDeclaringClass() == HelloController.class,
                PathRecorder.MethodSign.RESULT);
        // 对所有helloController下的模拟接口进行配置
        for (PathRecorder.Path path : paths) {
            // 增加接口前缀
            path.setPath("hello/" + path.getPath());
            // 开启模拟接口访问日志
            path.setMockItem(new MockItem(true, null, null, null));
            // 只提供GET方式的访问
            path.setRequestMethods(RequestMethod.GET);
        }
        pathRecorder.add(PathRecorder.Path.EMPTY, paths);
        // 增加接口的虚拟接口
        PathRecorder.Path[] apiPaths = PathRecorder.Path.generate(UserApi.class, PathRecorder.MethodSign.RESULT);
        pathRecorder.add(PathRecorder.Path.EMPTY, apiPaths);
    }

    @MockResult(methods = RequestMethod.GET)
    @ResponseBody
    public String wuhu() {
        return "123";
    }

    @MockResult(path = "list", methods = RequestMethod.GET)
    @ResponseBody
    public String mock() {
        return "mockTest";
    }

    public interface UserApi {

        @MockResult(result = "123", path = "312")
        User getById(String id);

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
