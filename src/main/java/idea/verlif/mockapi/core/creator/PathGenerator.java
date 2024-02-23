package idea.verlif.mockapi.core.creator;

/**
 * 接口地址生成器
 */
public interface PathGenerator {

    /**
     * 生成对应的模拟地址
     *
     * @param targetUrl 目标地址
     * @return 生成后的模拟地址
     */
    String urlGenerate(String targetUrl);
}
