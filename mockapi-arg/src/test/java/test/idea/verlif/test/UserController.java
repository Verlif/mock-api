package test.idea.verlif.test;

import idea.verlif.mockapi.arg.MockArg;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import test.idea.verlif.test.arg.MyArgMocker;

import java.util.ArrayList;
import java.util.List;

@RestController
public class UserController {

    @GetMapping("id")
    public String id(@MockArg(mocker = MyArgMocker.class) @RequestParam("id") String id, @MockArg @RequestParam("name") String name) {
        return id + " - " + name;
    }

    @GetMapping("id2")
    public String[] id2(String[] ids) {
        return ids;
    }

    @MockArg
    @GetMapping("user")
    public User user(User user) {
        return user;
    }

    @GetMapping("user2")
    public User user2(@MockArg @RequestBody User user) {
        return user;
    }
}
