package idea.verlif.mockapi.config;

import idea.verlif.mockapi.core.MockItem;
import idea.verlif.reflection.domain.SFunction;
import idea.verlif.reflection.util.MethodUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Predicate;

/**
 * 路径记录
 */
@Component
public class PathRecorder implements Iterable<PathRecorder.Path> {

    private final List<Path> keys;
    private final List<Path> values;

    public PathRecorder() {
        keys = new ArrayList<>();
        values = new ArrayList<>();
    }

    @Override
    public Iterator<Path> iterator() {
        return keys.listIterator();
    }

    public synchronized void add(Path key, Path value) {
        keys.add(key);
        values.add(value);
    }

    public synchronized void add(Path key, Path[] paths) {
        for (Path path : paths) {
            keys.add(key);
            values.add(path);
        }
    }

    public synchronized void append(PathRecorder argValues) {
        this.keys.addAll(argValues.keys);
        this.values.addAll(argValues.values);
    }

    public int getSize() {
        return keys.size();
    }

    public Path getKey(int index) {
        return keys.get(index);
    }

    public Path getValue(Path key) {
        int i = keys.indexOf(key);
        if (i > -1) {
            return values.get(i);
        } else {
            return null;
        }
    }

    public Path getValue(int index) {
        return values.get(index);
    }

    public int size() {
        return keys.size();
    }

    @Override
    public String toString() {
        if (!keys.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("PathRecorder[");
            for (int i = 0; i < keys.size(); i++) {
                Path key = keys.get(i);
                sb.append("{\"key\":\"").append(key).append("\",\"value\":");
                Path value = getValue(i);
                if (value == null) {
                    sb.append("null");
                } else {
                    sb.append("\"").append(value).append("\"");
                }
                sb.append("},");
            }
            sb.setLength(sb.length() - 1);
            sb.append("]");
            return sb.toString();
        } else {
            return "PathRecorder[]";
        }
    }

    public enum MethodSign {
        /**
         * 方法结果
         */
        RESULT,
        /**
         * 方法入参
         */
        PARAMETER,
    }

    public static class Path {

        public static final Path EMPTY = new Path("", new RequestMethod[]{});
        private static final RequestMethod[] ALL_REQUEST_METHODS = RequestMethod.values();

        private RequestMethod[] requestMethods;

        private String path;

        private Object handle;

        private Method method;

        private MethodSign methodSign;

        private MockItem mockItem;

        public Path(String path) {
            this(path, ALL_REQUEST_METHODS);
        }

        public Path(String path, RequestMethod[] requestMethods) {
            this.path = path;
            this.requestMethods = requestMethods;
            this.methodSign = MethodSign.RESULT;
        }

        public Path(String path, Collection<RequestMethod> requestMethods) {
            this.path = path;
            this.requestMethods = requestMethods.toArray(new RequestMethod[0]);
            this.methodSign = MethodSign.RESULT;
        }

        public Object getHandle() {
            return handle;
        }

        public Method getMethod() {
            return method;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public void setMethod(Method method, Object handle, MethodSign methodSign) {
            this.method = method;
            this.handle = handle;
            this.methodSign = methodSign;
        }

        public Path method(Method method, Object handle, MethodSign methodSign) {
            setMethod(method, handle, methodSign);
            return this;
        }

        public MockItem getMockItem() {
            return mockItem;
        }

        public void setMockItem(MockItem mockItem) {
            this.mockItem = mockItem;
        }

        public void setRequestMethods(Set<RequestMethod> requestMethods) {
            this.requestMethods = requestMethods.toArray(new RequestMethod[0]);
        }

        public void setRequestMethods(RequestMethod[] requestMethods) {
            this.requestMethods = requestMethods;
        }

        public Path requestMethods(RequestMethod... requestMethods) {
            this.setRequestMethods(requestMethods);
            return this;
        }

        public RequestMethod[] getRequestMethods() {
            return requestMethods;
        }

        public String getPath() {
            return path;
        }

        public MethodSign getMethodSign() {
            return methodSign;
        }

        /**
         * 由对象的类生成访问路径信息
         *
         * @param handle   目标类的实例对象
         * @param function 目标方法lambda表达式
         * @return 访问路径数组
         */
        public static <T, R> Path generate(Object handle, SFunction<T, R> function, MethodSign methodSign) {
            Method method = MethodUtil.getMethodFromLambda(function);
            return generate(handle, method, methodSign);
        }

        /**
         * 由对象的类生成访问路径信息
         *
         * @param handle 目标类的实例对象
         * @param method 目标方法
         * @return 访问路径数组
         */
        public static Path generate(Object handle, Method method, MethodSign methodSign) {
            Path path = new Path(method.getName());
            path.setMethod(method, handle, methodSign);
            return path;
        }

        /**
         * 对类的所有方法生成对应的访问路径
         *
         * @param handle 目标类的实例对象
         * @param filter 类方法过滤器
         * @return 访问路径数组
         */
        public static Path[] generate(Object handle, Predicate<Method> filter, MethodSign methodSign) {
            Class<?> cla = handle.getClass();
            List<Method> methods = MethodUtil.getAllMethods(cla, filter);
            Path[] paths = new Path[methods.size()];
            for (int i = 0; i < methods.size(); i++) {
                paths[i] = generate(handle, methods.get(i), methodSign);
            }
            return paths;
        }

        /**
         * 对类的所有自定义方法生成对应的访问路径
         *
         * @param handle 目标类的实例对象
         * @return 访问路径数组
         */
        public static Path[] generate(Object handle, MethodSign methodSign) {
            Class<?> cla = handle.getClass();
            return generate(handle, (Predicate<Method>) m -> Modifier.isPublic(m.getModifiers()) && m.getDeclaringClass() == cla, methodSign);
        }

    }

}
