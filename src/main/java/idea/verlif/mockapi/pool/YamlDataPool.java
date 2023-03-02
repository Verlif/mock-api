package idea.verlif.mockapi.pool;

import idea.verlif.mock.data.config.FieldDataPool;
import idea.verlif.parser.ParamParserService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Array;
import java.util.List;

/**
 * Yaml数据加载池
 */
@Configuration
@ConfigurationProperties(prefix = "mockapi.data")
@ConditionalOnProperty(prefix = "mockapi.data", value = "enabled", matchIfMissing = true)
public class YamlDataPool extends FieldDataPool implements InitializingBean {

    private List<DataInfo> pool;

    private final ParamParserService paramParserService;

    public YamlDataPool() {
        this.paramParserService = new ParamParserService();
    }

    public ParamParserService getParamParserService() {
        return paramParserService;
    }

    public List<DataInfo> getPool() {
        return pool;
    }

    public void setPool(List<DataInfo> pool) {
        this.pool = pool;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        for (DataInfo dataInfo : pool) {
            String types = dataInfo.types;
            if (types == null) {
                types = "String";
            }
            for (String typeStr : types.split(",")) {
                if (typeStr.length() == 0) {
                    continue;
                }
                Class<?> target = parseClass(typeStr.trim());
                String nameStr = dataInfo.getNames() == null ? "" : dataInfo.getNames();
                PatternValues<Object> pv = new PatternValues<>();
                for (String name : nameStr.split(",")) {
                    if (name.length() == 0) {
                        continue;
                    }
                    pv.values(parseValues(dataInfo.getValues(), target), name.trim());
                }
                addPatternValues(target, pv);
            }
        }
    }

    private Class<?> parseClass(String pkgName) {
        switch (pkgName.toLowerCase()) {
            case "int":
                return Integer.class;
            case "short":
                return Short.class;
            case "long":
                return Long.class;
            case "byte":
                return Byte.class;
            case "char":
                return Character.class;
            case "boolean":
                return Boolean.class;
            case "float":
                return Float.class;
            case "double":
                return Double.class;
            case "string":
                return String.class;
        }
        if (pkgName.indexOf('.') == -1) {
            pkgName = "java.lang." + pkgName;
        }
        // 获取类型
        try {
            return Class.forName(pkgName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T[] parseValues(String values, Class<T> type) {
        String[] split = values.split(",");
        T[] arr = (T[]) Array.newInstance(type, split.length);
        for (int i = 0; i < split.length; i++) {
            String s = split[i].trim();
            arr[i] = paramParserService.parse(type, s);
        }
        return arr;
    }

    /**
     * 数据池信息
     */
    public static final class DataInfo {

        /**
         * 数据类型
         */
        private String types;

        /**
         * 数据池对应属性名称正则表达式
         */
        private String names;

        /**
         * 数据池内容
         */
        private String values;

        public String getTypes() {
            return types;
        }

        public void setTypes(String types) {
            this.types = types;
        }

        public String getNames() {
            return names;
        }

        public void setNames(String names) {
            this.names = names;
        }

        public String getValues() {
            return values;
        }

        public void setValues(String values) {
            this.values = values;
        }
    }
}
