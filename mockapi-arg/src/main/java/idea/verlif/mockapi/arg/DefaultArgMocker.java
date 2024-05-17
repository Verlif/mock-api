package idea.verlif.mockapi.arg;

import idea.verlif.mock.data.MockDataCreator;
import idea.verlif.parser.ParamParserService;
import org.springframework.core.MethodParameter;

public class DefaultArgMocker implements ArgMocker {

    public static final DefaultArgMocker DEFAULT = new DefaultArgMocker();

    private final MockDataCreator creator;
    private final ParamParserService parserService;

    public DefaultArgMocker() {
        creator = new MockDataCreator();
        parserService = new ParamParserService();
    }

    @Override
    public Object mock(MethodParameter parameter, String data) {
        if (data.isEmpty()) {
            return creator.mock(parameter.getParameterType());
        } else {
            return parserService.parse(parameter.getParameterType(), data);
        }
    }
}
