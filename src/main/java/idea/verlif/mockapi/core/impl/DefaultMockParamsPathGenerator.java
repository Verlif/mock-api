package idea.verlif.mockapi.core.impl;

import idea.verlif.mockapi.core.creator.MockParamsPathGenerator;

public class DefaultMockParamsPathGenerator implements MockParamsPathGenerator {
    @Override
    public String urlGenerate(String targetUrl) {
        if (!targetUrl.isEmpty() && targetUrl.charAt(0) == '/') {
            return "/params" + targetUrl;
        } else {
            return "/params/" + targetUrl;
        }
    }
}
