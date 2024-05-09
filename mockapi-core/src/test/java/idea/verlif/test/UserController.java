package idea.verlif.test;

import idea.verlif.test.mock.MyMockApi;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@MyMockApi
@RestController("user")
public class UserController {

    @GetMapping
    public User getById(String id) {
        return new User();
    }

    @GetMapping("batch")
    public List<User> add(User user) {
        return new ArrayList<>();
    }
}
