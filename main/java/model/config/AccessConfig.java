package model.config;

import javafx.util.Pair;
import model.db.BlogDBCollection;
import model.db.UserCollection;
import model.event.SendEvent;
import org.dom4j.Element;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by xlo on 15-8-30.
 * it's the config of access
 */
public class AccessConfig implements ConfigInterface {
    protected Map<String, Map<String, Set<Pair<String, Integer>>>> access;

    protected AccessConfig() {
        this.access = new HashMap<>();
    }

    public static AccessConfig getConfig() {
        return (AccessConfig) ConfigManager.getConfigManager().getConfig(AccessConfig.class);
    }

    @Override
    public void init() throws Exception {
        Element root = ConfigInterface.getRootElement(ConfigManager.configPathConfig.getConfigFilePath(this.getClass()));
        for (Object now : root.elements()) {
            Element element = (Element) now;
            for (Object kidObject : element.elements()) {
                Element method = (Element) kidObject;
                addAccess(element.attributeValue("name"), method.attributeValue("name"), method.attributeValue("filed"),
                        Integer.valueOf(method.getText()));
            }
        }
    }

    private void addAccess(String name, String method, String filed, Integer value) {
        if (!this.access.containsKey(name)) {
            this.access.put(name, new HashMap<>());
        }
        if (!this.access.get(name).containsKey(method)) {
            this.access.get(name).put(method, new HashSet<>());
        }
        this.access.get(name).get(method).add(new Pair<>(filed, value));
    }

    @Override
    public void reload() throws Exception {
        this.access.clear();
        init();
    }

    public Set<Pair<String, Integer>> getAccessNeed(SendEvent sendEvent) {
        return checkClassAndMethod(sendEvent.getClassName(), sendEvent.getMethodName());
    }

    public boolean isAccept(BlogDBCollection.DBData data, SendEvent sendEvent) {
        Set<Pair<String, Integer>> need = getAccessNeed(sendEvent);
        if (need == null) return true;
        for (Pair<String, Integer> now : need) {
            int have;
            if (data.object.containsKey(now.getKey())) {
                have = Integer.valueOf(data.object.get(now.getKey()).toString());
            } else {
                have = 0;
            }
            if (have >= now.getValue()) return true;
        }
        return false;
    }

    public boolean isAccept(String username, String password, SendEvent sendEvent) {
        if (username == null || password == null) return false;
        UserCollection userCollection = new UserCollection();
        BlogDBCollection.DBData data = userCollection.getUserData(username);
        return data != null && data.object.get("password").equals(password) && isAccept(data, sendEvent);
    }

    protected Set<Pair<String, Integer>> checkClassAndMethod(String name, String method) {
        if (!this.access.containsKey(name)) {
            return null;
        } else if (!this.access.get(name).containsKey(method)) {
            return null;
        } else {
            return this.access.get(name).get(method);
        }
    }
}
