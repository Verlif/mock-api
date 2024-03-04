package idea.verlif.mockapi.config;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.*;

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

    public synchronized void append(PathRecorder argValues) {
        this.keys.addAll(argValues.keys);
        this.values.addAll(argValues.values);
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

        private final Set<RequestMethod> methods;

        private final String path;

        public Path(String path) {
            this.path = path;
            this.methods = new HashSet<>(Arrays.asList(RequestMethod.values()));
        }

        public Path(String path, Set<RequestMethod> methods) {
            this.path = path;
            this.methods = methods;
        }

        public Set<RequestMethod> getMethods() {
            return methods;
        }

        public String getPath() {
            return path;
        }

    }
}
