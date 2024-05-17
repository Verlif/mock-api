package idea.verlif.mockapi.arg;

import org.springframework.core.MethodParameter;

public interface ArgMocker {

    Object mock(MethodParameter parameter, String data);
}
