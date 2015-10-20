package control;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by xlo on 2015/9/10.
 * it's the manager logic
 */
public class ManagerLogic {
    protected static final Map<String, Object> logicObject = new HashMap<>();
    protected static final Map<String, Method> logicFun = new HashMap<>();
    protected static ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public static void put(String name, Object object, int parameterNum) throws NoSuchMethodException {
        lock.writeLock().lock();
        logicObject.put(name, object);
        Class<?>[] objects = new Class[parameterNum];
        for (int i = 0; i < parameterNum; i++) {
            objects[i] = Object.class;
        }
        Method method = object.getClass().getMethod("invoke", objects);
        method.setAccessible(true);
        logicFun.put(name, method);
        lock.writeLock().unlock();
    }

    public static Object invoke(String name, Object... objects) throws Exception {
        lock.readLock().lock();
        Method method;
        Object object;
        method = logicFun.get(name);
        object = logicObject.get(name);
        lock.readLock().unlock();
        return method.invoke(object, objects);
    }
}
