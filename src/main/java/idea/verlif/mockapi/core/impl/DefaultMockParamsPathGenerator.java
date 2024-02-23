package idea.verlif.mockapi.core.impl;

import idea.verlif.mockapi.core.creator.MockParamsPathGenerator;

public class DefaultMockParamsPathGenerator implements MockParamsPathGenerator {
    @Override
    public String urlGenerate(String targetUrl) {
        return "/params" + targetUrl;
    }
}
