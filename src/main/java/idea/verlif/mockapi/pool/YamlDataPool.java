package idea.verlif.mockapi.pool;

import idea.verlif.mock.data.MockDataCreator;
import idea.verlif.mock.data.config.FieldDataPool;
import idea.verlif.mockapi.MockApiException;
import idea.verlif.parser.ParamParserService;
import idea.verlif.reflection.domain.ClassGrc;
import idea.verlif.reflection.domain.FieldGrc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Yaml数据加载池
 */
@Configuration
@ConfigurationProperties(prefix = "mockapi.data")
@ConditionalOnProperty(prefix = "mockapi.data", value = "enabled", matchIfMissing = true)
public class YamlDataPool extends FieldDataPool {

    private List<DataInfo> pool;
    private final Map<Class<?>, FieldDataPool> fieldDataPoolMap;

    @Autowired
    private MockDataCreator mockDataCreator;
    @Autowired
    private ParamParserService paramParserService;

    public YamlDataPool() {
        fieldDataPoolMap = new HashMap<>();
    }

    public List<DataInfo> getPool() {
        return pool;
    }

    public void setPool(List<DataInfo> pool) {
        this.pool = pool;
    }

    @PostConstruct
    public void initData() throws Exception {
        mockDataCreator.fieldDataPool(this);
        mockDataCreator.getConfig().fieldDataPool(this);
        for (DataInfo dataInfo : pool) {
            if (!dataInfo.isEnabled()) {
                continue;
            }
            String belongs = dataInfo.belongs;
            // 所属类为空则进行通用配置
            if (belongs == null || belongs.isEmpty()) {
                toDataPool(this, dataInfo);
            } else {
                // 聚集类型数据
                String[] belongClasses = belongs.split(",");
                for (String belongClass : belongClasses) {
                    Class<?> cla = parseClass(belongClass);
                    FieldDataPool fieldDataPool = fieldDataPoolMap.computeIfAbsent(cla, cl -> new FieldDataPool());
                    toDataPool(fieldDataPool, dataInfo);
                }
            }
        }
    }

    private void toDataPool(FieldDataPool dataPool, DataInfo dataInfo) {
        String types = dataInfo.types;
        if (types == null) {
            types = "String";
        }
        for (String typeStr : types.split(",")) {
            if (typeStr.isEmpty()) {
                continue;
            }
            Class<?> target = parseClass(typeStr.trim());
            String nameStr = dataInfo.getNames() == null ? "" : dataInfo.getNames();
            PatternValues<Object> pv = new PatternValues<>();
            for (String name : nameStr.split(",")) {
                if (name.isEmpty()) {
                    continue;
                }
                pv.values(parseValues(dataInfo.getValues(), target), name.trim(), Pattern.CASE_INSENSITIVE);
            }
            dataPool.addPatternValues(target, pv);
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
            throw new MockApiException(e);
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

    @Override
    public <T> T[] getValues(ClassGrc classGrc, String key) {
        // 优先判断是否是属性取值
        if (classGrc instanceof FieldGrc) {
            // 获取属性的所属类是否在配置定义类中
            Field field = ((FieldGrc) classGrc).getField();
            Class<?> declaringClass = field.getDeclaringClass();
            FieldDataPool fieldDataPool = fieldDataPoolMap.get(declaringClass);
            if (fieldDataPool != null) {
                // 当存在对应名称的属性池时返回值
                T[] values = fieldDataPool.getValues(classGrc, key);
                if (values != null) {
                    return values;
                }
            }
        }
        // 默认路径
        return super.getValues(classGrc, key);
    }

    /**
     * 数据池信息
     */
    public static final class DataInfo {

        /**
         * 所属类
         */
        private String belongs;

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

        /**
         * 改数据池是否启用
         */
        private boolean enabled = true;

        public String getBelongs() {
            return belongs;
        }

        public void setBelongs(String belongs) {
            this.belongs = belongs;
        }

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

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

}
