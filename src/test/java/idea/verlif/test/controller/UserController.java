package idea.verlif.test.controller;

import idea.verlif.mockapi.anno.MockParams;
import idea.verlif.mockapi.anno.MockResult;
import idea.verlif.test.global.domain.User;
import idea.verlif.test.global.domain.query.UserQuery;
import idea.verlif.test.global.result.BaseResult;
import idea.verlif.test.global.result.ext.OkResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@MockParams
@MockResult
@RestController
@RequestMapping("user")
public class UserController {

    @MockResult(config = "a")
    @GetMapping
    public BaseResult<User> getById(Integer id) {
        return new OkResult<>(new User());
    }

    @MockResult(path = "list", config = "b")
    @GetMapping("list")
    public BaseResult<List<User>> getList(@Validated UserQuery query) {
        return new OkResult<>(new ArrayList<>());
    }

    @MockParams(config = "a")
    @PutMapping
    public BaseResult<User> update(User user) {
        return new OkResult<>(user);
    }

    @DeleteMapping
    public BaseResult<String> deleteById(Integer id) {
        return new OkResult<>("OK");
    }

}
