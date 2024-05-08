package idea.verlif.mockapi.config;

import idea.verlif.mockapi.anno.ConditionalOnMockEnabled;
import idea.verlif.mockapi.MockItem;
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
@ConditionalOnMockEnabled
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

    public synchronized void add(Path path) {
        keys.add(Path.EMPTY);
        values.add(path);
    }

    public synchronized void add(Path[] paths) {
        for (Path path : paths) {
            keys.add(Path.EMPTY);
            values.add(path);
        }
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

    public static class Path {

        public static final Path EMPTY = new Path("", new RequestMethod[]{});
        private static final RequestMethod[] ALL_REQUEST_METHODS = RequestMethod.values();

        private RequestMethod[] requestMethods;

        private String path;

        private Object handle;

        private Method method;

        private MockItem mockItem;

        public Path(String path) {
            this(path, ALL_REQUEST_METHODS);
        }

        public Path(String path, RequestMethod[] requestMethods) {
            this.path = path;
            this.requestMethods = requestMethods;
        }

        public Path(String path, Collection<RequestMethod> requestMethods) {
            this.path = path;
            this.requestMethods = requestMethods.toArray(new RequestMethod[0]);
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

        public void setMethod(Method method, Object handle) {
            this.method = method;
            this.handle = handle;
        }

        public Path method(Method method, Object handle) {
            setMethod(method, handle);
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

        public void setRequestMethods(RequestMethod requestMethod) {
            this.requestMethods = new RequestMethod[]{requestMethod};
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

        /**
         * 由对象的类生成访问路径信息
         *
         * @param function 目标方法lambda表达式
         * @return 访问路径数组
         */
        public static <T, R> Path generate(SFunction<T, R> function) {
            Method method = MethodUtil.getMethodFromLambda(function);
            return generate(method);
        }

        /**
         * 由对象的类生成访问路径信息
         *
         * @param method 目标方法
         * @return 访问路径数组
         */
        public static Path generate(Method method) {
            Path path = new Path(method.getName());
            // 构建方法访问地址：定义类名 + 方法名
            String className = method.getDeclaringClass().getSimpleName();
            className = className.substring(0, 1).toLowerCase() + className.substring(1);
            path.setPath(className + "/" + method.getName());
            path.setMethod(method, null);
            return path;
        }

        /**
         * 对类的所有方法生成对应的访问路径
         *
         * @param targetClass 目标类
         * @param filter      类方法过滤器
         * @return 访问路径数组
         */
        public static Path[] generate(Class<?> targetClass, Predicate<Method> filter) {
            List<Method> methods = MethodUtil.getAllMethods(targetClass, filter);
            Path[] paths = new Path[methods.size()];
            for (int i = 0; i < methods.size(); i++) {
                paths[i] = generate(methods.get(i));
            }
            return paths;
        }

        /**
         * 对类的所有自定义方法生成对应的访问路径
         *
         * @param targetClass 目标类
         * @return 访问路径数组
         */
        public static Path[] generate(Class<?> targetClass) {
            return generate(targetClass, m -> Modifier.isPublic(m.getModifiers()) && m.getDeclaringClass() == targetClass);
        }

        /**
         * 对类的所有方法生成对应的访问路径
         *
         * @param handle 目标类的实例对象
         * @param filter 类方法过滤器
         * @return 访问路径数组
         */
        public static Path[] generate(Object handle, Predicate<Method> filter) {
            Class<?> cla = handle.getClass();
            return generate(cla, filter);
        }

        /**
         * 对类的所有自定义方法生成对应的访问路径
         *
         * @param handle 目标类的实例对象
         * @return 访问路径数组
         */
        public static Path[] generate(Object handle) {
            Class<?> cla = handle.getClass();
            return generate(handle, m -> Modifier.isPublic(m.getModifiers()) && m.getDeclaringClass() == cla);
        }

    }

}
