package idea.verlif.mockapi.core;

import idea.verlif.mockapi.anno.MockResult;

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
     * 调用方法的所属对象
     */
    private final Object methodHolder;

    /**
     * 原调用的方法对象
     */
    private final Method oldMethod;

    /**
     * 使用的MockResult注解
     */
    private final MockResult mockResult;

    public RequestPack(Map<String, String> pathVars, Map<String, Object> params,
                       HttpServletRequest request, HttpServletResponse response,
                       Object methodHolder, Method oldMethod, MockResult mockResult) {
        this.pathVars = pathVars;
        this.params = params;
        this.request = request;
        this.response = response;
        this.methodHolder = methodHolder;
        this.oldMethod = oldMethod;
        this.mockResult = mockResult;
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

    public Object getMethodHolder() {
        return methodHolder;
    }

    public Method getOldMethod() {
        return oldMethod;
    }

    public MockResult getMockResult() {
        return mockResult;
    }
}