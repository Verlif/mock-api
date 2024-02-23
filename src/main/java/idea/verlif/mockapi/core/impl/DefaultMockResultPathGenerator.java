package idea.verlif.mockapi.core.impl;

import idea.verlif.mockapi.core.creator.MockResultPathGenerator;

public class DefaultMockResultPathGenerator implements MockResultPathGenerator {
    @Override
    public String urlGenerate(String targetUrl) {
        return "/mock" + targetUrl;
    }
}
