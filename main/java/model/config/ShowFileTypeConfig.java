package model.config;

import org.dom4j.DocumentException;
import org.dom4j.Element;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by xlo on 2015/8/20.
 * it's the class config show file or download file
 */
public class ShowFileTypeConfig implements ConfigInterface {
    protected Set<String> showFileNameEndWith = new HashSet<>();

    protected ShowFileTypeConfig() {

    }

    public static ShowFileTypeConfig getConfig() {
        return (ShowFileTypeConfig) ConfigManager.getConfigManager().getConfig(ShowFileTypeConfig.class);
    }

    @Override
    public void init() throws DocumentException {
        Element root = ConfigInterface.getRootElement(ConfigManager.configPathConfig.getConfigFilePath(this.getClass()));
        List node = root.elements();
        for (Object now : node) {
            Element element = (Element) now;
            this.showFileNameEndWith.add(element.getText());
        }
    }

    @Override
    public void reload() throws DocumentException {
        this.showFileNameEndWith.clear();
        init();
    }

    public boolean isShow(String path) {
        return this.showFileNameEndWith.contains(path.substring(path.lastIndexOf('.') + 1));
    }
}
