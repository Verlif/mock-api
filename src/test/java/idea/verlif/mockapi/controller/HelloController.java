package idea.verlif.mockapi.controller;

import idea.verlif.mockapi.anno.MockResult;
import idea.verlif.mockapi.global.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("hello")
public class HelloController {

    @Operation(summary = "echo接口")
    @GetMapping("echo/{str}")
    public String echo(@PathVariable String str) {
        return str;
    }

    @Operation(summary = "hi接口")
    @GetMapping("hi")
    @MockResult
    public String hi() {
        return "hi";
    }

    @GetMapping("test")
    public String test(@RequestParam(name = "aInt") int a, User b, User c) {
        return "";
    }
}
