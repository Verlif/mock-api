package idea.verlif.mockapi.controller;

import idea.verlif.mockapi.anno.MockResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("hello")
public class HelloController {

    @MockResult
    @RequestMapping("echo/{str}")
    public String echo(@PathVariable String str) {
        return str;
    }

    @RequestMapping("hi")
    public String hi() {
        return "hi";
    }
}
