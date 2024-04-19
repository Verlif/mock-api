package idea.verlif.test.api;

import idea.verlif.mockapi.anno.MockResult;
import idea.verlif.test.global.domain.User;

public interface TestApi {

    @MockResult(result = "123", path = "312")
    User getById(String id);

}
