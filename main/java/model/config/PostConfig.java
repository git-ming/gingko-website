package model.config;

import net.server.serverSolver.RequestSolver;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by xlo on 2015/8/28.
 * it's config post's solver
 */
public class PostConfig implements ConfigInterface {
    protected List<PostInfo> postInfo;
    private Map<String, Map<String, Map<Class[], Method>>> method;
    private Map<String, Constructor<?>> manager;
    private ReadWriteLock readWriteLock;

    protected PostConfig() {
        this.postInfo = new LinkedList<>();
        this.method = new HashMap<>();
        this.manager = new HashMap<>();
        this.readWriteLock = new ReentrantReadWriteLock();
    }

    public static PostConfig getConfig() {
        return (PostConfig) ConfigManager.getConfigManager().getConfig(PostConfig.class);
    }

    @Override
    public void init() throws DocumentException {
        Element root = ConfigInterface.getRootElement(ConfigManager.configPathConfig.getConfigFilePath(this.getClass()));
        List node = root.elements();
        for (Object now : node) {
            Element element = (Element) now;
            PostInfo post = new PostInfo();

            post.methodData = new ArrayList<>();
            post.defaultValue = new ArrayList<>();

            post.name = element.attributeValue("name");
            post.url = element.attributeValue("url");
            post.manager = element.attributeValue("manager");
            post.access = element.attributeValue("access").equals("true");
            for (Object kid : element.elements()) {
                Element data = (Element) kid;
                post.method = data.attributeValue("name");
                post.dataType = data.attributeValue("model", "object");
                for (Object methodKid : data.elements()) {
                    Element methodData = (Element) methodKid;
                    String defaultValue = methodData.attributeValue("default");
                    post.defaultValue.add(defaultValue);
                    post.methodData.add(methodData.getText());
                }
            }
            postInfo.add(post);
        }

        this.readWriteLock.writeLock().lock();
        this.method.clear();
        this.manager.clear();
        this.readWriteLock.writeLock().unlock();
    }

    @Override
    public void reload() throws DocumentException {
        this.postInfo.clear();
        init();
    }

    public List<PostInfo> getPostInfo() {
        return postInfo;
    }

    public Constructor<?> getManagerConstructor(String className) throws ClassNotFoundException, NoSuchMethodException {
        if (!this.manager.containsKey(className)) {
            Class[] paramTypes = {RequestSolver.class};
            this.manager.put(className, Class.forName(className).getConstructor(paramTypes));
        }
        return this.manager.get(className);
    }

    public Method getMethod(String className, String methodName, Class[] methodParamType) throws ClassNotFoundException, NoSuchMethodException {
        if (!this.method.containsKey(className)) {
            this.method.put(className, new HashMap<>());
        }
        Map<String, Map<Class[], Method>> map = this.method.get(className);
        if (!map.containsKey(methodName)) {
            map.put(methodName, new HashMap<>());
        }
        Map<Class[], Method> methodMap = map.get(methodName);
        if (!methodMap.containsKey(methodParamType)) {
            methodMap.put(methodParamType, Class.forName(className).getMethod(methodName, methodParamType));
        }
        return methodMap.get(methodParamType);
    }

    public class PostInfo {
        private String name;
        private String url;
        private String manager;
        private boolean access;
        private String method;
        private String dataType;
        private List<String> methodData;
        private List<String> defaultValue;

        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
        }

        public String getManager() {
            return manager;
        }

        public boolean needAccess() {
            return access;
        }

        public String getMethod() {
            return method;
        }

        public String getDataType() {
            return dataType;
        }

        public List<String> getMethodData() {
            return new ArrayList<>(methodData);
        }

        public List<String> getDefaultValue() {
            return new ArrayList<>(defaultValue);
        }
    }
}