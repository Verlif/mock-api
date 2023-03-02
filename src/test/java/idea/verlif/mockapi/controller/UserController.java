package idea.verlif.mockapi.controller;

import idea.verlif.mockapi.anno.MockResult;
import idea.verlif.mockapi.global.domain.User;
import idea.verlif.mockapi.global.result.BaseResult;
import idea.verlif.mockapi.global.result.ext.OkResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@MockResult
@RestController
@RequestMapping("user")
public class UserController {

    @GetMapping
    public BaseResult<User> getById(Integer id) {
        return new OkResult<>(new User());
    }

    @GetMapping("list")
    public BaseResult<List<User>> getList() {
        return new OkResult<>(new ArrayList<>());
    }

    @MockResult(cacheable = true)
    @PutMapping
    public BaseResult<User> update(User user) {
        return new OkResult<>(user);
    }

    @DeleteMapping
    public BaseResult<String> deleteById(Integer id) {
        return new OkResult<>("OK");
    }

}