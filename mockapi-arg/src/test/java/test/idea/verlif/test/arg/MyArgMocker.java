package test.idea.verlif.test.arg;

import idea.verlif.mockapi.arg.ArgMocker;
import org.springframework.core.MethodParameter;

public class MyArgMocker implements ArgMocker {
    @Override
    public Object mock(MethodParameter parameter, String data) {
        return "xixi";
    }
}
