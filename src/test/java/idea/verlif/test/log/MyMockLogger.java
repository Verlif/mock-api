package idea.verlif.test.log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import idea.verlif.mockapi.core.RequestPack;
import idea.verlif.mockapi.log.MockLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
public class MyMockLogger implements MockLogger {

    private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public void log(RequestPack pack, Method method, Object result) {
        Logger logger = LoggerFactory.getLogger(method.getDeclaringClass());
        String s = null;
        if (result != null) {
            try {
                s = OBJECT_MAPPER.writeValueAsString(result);
            } catch (JsonProcessingException e) {
                s = result.toString();
            }
        }
        logger.info(pack.getRequest().getMethod() + " - " + pack.getRequest().getRequestURL() + " - " + s);
    }
}
