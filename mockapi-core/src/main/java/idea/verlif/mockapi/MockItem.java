package idea.verlif.mockapi;

import org.springframework.web.bind.annotation.RequestMethod;

public class MockItem {

    private static final String EMPTY = "";
    private static final RequestMethod[] EMPTY_METHODS = new RequestMethod[0];

    /**
     * 是否打印mock日志
     */
    private boolean log;

    /**
     * 直返数据，不进行mock，直接返回参数值
     */
    private String data;

    private String path;

    private RequestMethod[] methods;

    private ObjectMocker objectMocker;

    public MockItem() {
        this.log = false;
        this.data = EMPTY;
        this.path = EMPTY;
        this.methods = EMPTY_METHODS;
        this.objectMocker = ObjectMocker.DEFAULT;
    }

    public MockItem(boolean log, String data, String path, RequestMethod[] methods, ObjectMocker objectMocker) {
        setLog(log);
        setData(data);
        setPath(path);
        setMethods(methods);
        setObjectMocker(objectMocker);
    }

    public boolean isLog() {
        return log;
    }

    public void setLog(boolean log) {
        this.log = log;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public RequestMethod[] getMethods() {
        return methods;
    }

    public void setMethods(RequestMethod[] methods) {
        if (methods != null) {
            this.methods = methods;
        }
    }

    public ObjectMocker getObjectMocker() {
        return objectMocker;
    }

    public void setObjectMocker(ObjectMocker objectMocker) {
        if (objectMocker != null) {
            this.objectMocker = objectMocker;
        }
    }
}
