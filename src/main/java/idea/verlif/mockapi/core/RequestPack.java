package idea.verlif.mockapi.core;

import org.springframework.web.method.HandlerMethod;

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
    private Object methodHandle;

    /**
     * 原调用的方法对象
     */
    private Method oldMethod;

    public RequestPack(Map<String, String> pathVars, Map<String, Object> params,
                       HttpServletRequest request, HttpServletResponse response) {
        this.pathVars = pathVars;
        this.params = params;
        this.request = request;
        this.response = response;
    }

    public RequestPack(Map<String, String> pathVars, Map<String, Object> params,
                       HttpServletRequest request, HttpServletResponse response,
                       HandlerMethod methodHandle, Method oldMethod) {
        this.pathVars = pathVars;
        this.params = params;
        this.request = request;
        this.response = response;
        this.methodHandle = methodHandle;
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

    public Object getMethodHandle() {
        return methodHandle;
    }

    public Method getOldMethod() {
        return oldMethod;
    }

    public void setMethodHandle(Object methodHandle) {
        this.methodHandle = methodHandle;
    }

    public void setOldMethod(Method oldMethod) {
        this.oldMethod = oldMethod;
    }
}
