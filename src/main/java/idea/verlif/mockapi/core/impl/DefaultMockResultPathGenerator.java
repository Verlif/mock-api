package idea.verlif.mockapi.core.impl;

import idea.verlif.mockapi.core.creator.MockResultPathGenerator;

public class DefaultMockResultPathGenerator implements MockResultPathGenerator {
    @Override
    public String urlGenerate(String targetUrl) {
        if (!targetUrl.isEmpty() && targetUrl.charAt(0) == '/') {
            return "/mock" + targetUrl;
        } else {
            return "/mock/" + targetUrl;
        }
    }
}
