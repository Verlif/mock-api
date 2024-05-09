package idea.verlif.test.api;

import idea.verlif.mockapi.anno.MockApi;
import idea.verlif.test.global.domain.User;

public interface TestApi {

    @MockApi(data = "123", path = "312")
    User getById(String id);

}
