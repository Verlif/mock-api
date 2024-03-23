package idea.verlif.mockapi.core;

public class MockItem {

    private static final String EMPTY = "";

    /**
     * 是否打印mock日志
     */
    private boolean log;

    /**
     * 配置名称
     */
    private String config;

    /**
     * 直返数据，不进行mock，直接返回参数值
     */
    private String result;

    /**
     * 直返数据的类型
     */
    private Class<?> resultType;

    public MockItem() {
        this.log = false;
        this.config = EMPTY;
        this.result = EMPTY;
        this.resultType = Object.class;
    }

    public MockItem(boolean log, String config, String result, Class<?> resultType) {
        setLog(log);
        setConfig(config);
        setResult(result);
        setResultType(resultType);
    }

    public boolean isLog() {
        return log;
    }

    public void setLog(boolean log) {
        this.log = log;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        if (config == null) {
            config = EMPTY;
        }
        this.config = config;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        if (result == null) {
            result = EMPTY;
        }
        this.result = result;
    }

    public Class<?> getResultType() {
        return resultType;
    }

    public void setResultType(Class<?> resultType) {
        this.resultType = resultType;
    }
}
