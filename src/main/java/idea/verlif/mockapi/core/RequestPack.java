package idea.verlif.mockapi.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 构造数据请求信息包
 */
public class RequestPack {

    /**
     * 路径参数
     */
    private final Map<String, String> pathVars;

    /**
     * 请求参数
     */
    private final Map<String, Object> params;

    /**
     * 当前的请求对象
     */
    private final HttpServletRequest request;

    /**
     * 当前请求的响应对象
     */
    private final HttpServletResponse response;

    /**
     * 原调用的方法对象
     */
    private final Method oldMethod;

    public RequestPack(Map<String, String> pathVars, Map<String, Object> params, HttpServletRequest request, HttpServletResponse response, Method oldMethod) {
        this.pathVars = pathVars;
        this.params = params;
        this.request = request;
        this.response = response;
        this.oldMethod = oldMethod;
    }

    public Object getParam(String key) {
        return params.get(key);
    }

    public int paramCount() {
        return params.size();
    }

    public String getPathVar(String key) {
        return pathVars.get(key);
    }

    public int pathVarCount() {
        return pathVars.size();
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public Method getOldMethod() {
        return oldMethod;
    }
}
