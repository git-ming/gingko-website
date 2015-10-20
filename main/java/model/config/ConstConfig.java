package model.config;

import org.dom4j.DocumentException;
import org.dom4j.Element;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xlo on 2015/9/18.
 * it's the const config
 */
public class ConstConfig implements ConfigInterface {
    protected Map<String, Object> values;

    protected ConstConfig() {
        this.values = new HashMap<>();
    }

    public static ConstConfig getConfig() {
        return (ConstConfig) ConfigManager.getConfigManager().getConfig(ConstConfig.class);
    }

    @Override
    public void init() throws DocumentException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Element root = ConfigInterface.getRootElement(ConfigManager.configPathConfig.getConfigFilePath(this.getClass()));
        List node = root.elements();
        for (Object now : node) {
            Element element = (Element) now;
            String name = element.attributeValue("name");
            Class<?> type = Class.forName(element.attributeValue("type"));
            Method method = type.getMethod("valueOf", String.class);
            String value = element.getText();
            this.values.put(name, method.invoke(type, value));
        }
    }

    @Override
    public void reload() throws DocumentException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        this.values.clear();
        init();
    }

    public Object getConst(String type) {
        if (!this.values.containsKey(type)) return Long.MAX_VALUE;
        return this.values.get(type);
    }
}
