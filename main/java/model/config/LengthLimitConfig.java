package model.config;

import org.dom4j.DocumentException;
import org.dom4j.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xlo on 2015/8/31.
 * it's config length Limit
 */
public class LengthLimitConfig implements ConfigInterface {
    protected Map<String, Long> limit;

    protected LengthLimitConfig() {
        this.limit = new HashMap<>();
    }

    public static LengthLimitConfig getConfig() {
        return (LengthLimitConfig) ConfigManager.getConfigManager().getConfig(LengthLimitConfig.class);
    }

    @Override
    public void init() throws DocumentException {
        Element root = ConfigInterface.getRootElement(ConfigManager.configPathConfig.getConfigFilePath(this.getClass()));
        List node = root.elements();
        for (Object now : node) {
            Element element = (Element) now;
            this.limit.put(element.attributeValue("name"), Long.valueOf(element.getText()));
        }
    }

    @Override
    public void reload() throws DocumentException {
        this.limit.clear();
        init();
    }

    public long getLimit(String type) {
        if (!this.limit.containsKey(type)) return Long.MAX_VALUE;
        return this.limit.get(type);
    }
}
