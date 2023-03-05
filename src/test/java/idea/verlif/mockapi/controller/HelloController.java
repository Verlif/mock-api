package idea.verlif.mockapi.controller;

import idea.verlif.mockapi.anno.MockParams;
import idea.verlif.mockapi.anno.MockResult;
import idea.verlif.mockapi.global.domain.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("hello")
public class HelloController {

    @MockResult(cacheable = true)
    @GetMapping("echo/{str}")
    public String echo(@PathVariable String str) {
        return str;
    }

    @MockResult
    @GetMapping("hi")
    public String hi() {
        return "hi";
    }

    @MockParams
    @GetMapping("test")
    public String test(@RequestParam(name = "a") int a, User b, User c) {
        return "";
    }
}
