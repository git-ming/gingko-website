package model.config;

import org.dom4j.DocumentException;
import org.dom4j.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xlo on 2015/8/21.
 * it's the return code config
 * warning it's should not be the http reply code config
 */
public class ReturnCodeConfig implements ConfigInterface {
    protected Map<String, String> codes;

    protected ReturnCodeConfig() {
        this.codes = new HashMap<>();
    }

    public static ReturnCodeConfig getConfig() {
        return (ReturnCodeConfig) ConfigManager.getConfigManager().getConfig(ReturnCodeConfig.class);
    }

    @Override
    public void init() throws DocumentException {
        Element root = ConfigInterface.getRootElement(ConfigManager.configPathConfig.getConfigFilePath(this.getClass()));
        List node = root.elements();
        for (Object now : node) {
            Element element = (Element) now;
            this.codes.put(element.attributeValue("id"), element.getText());
        }
    }

    @Override
    public void reload() throws DocumentException {
        this.codes.clear();
        init();
    }

    public String getCode(String type) {
        return this.codes.get(type);
    }
}
