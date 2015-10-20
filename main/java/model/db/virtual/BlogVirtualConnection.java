package model.db.virtual;

import model.config.ConfigManager;
import model.config.DBConfig;
import model.db.VirtualDB;
import model.db.VirtualDBConnection;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xlo on 2015/9/14.
 * it's the virtual connection
 */
public class BlogVirtualConnection implements VirtualDBConnection {
    protected static DBConfig dbConfig = (DBConfig) ConfigManager.getConfigManager().getConfig(DBConfig.class);

    private static Map<String, VirtualDB> dbMap = new HashMap<>();

    public synchronized VirtualDB getDatabase(String name) {
        if (dbConfig.getDBType(name).equals("default")) {
            if (!dbMap.containsKey(name)) {
                dbMap.put(name, new BlogVirtualDB());
            }
        } else {
            if (!dbMap.containsKey(name)) {
                dbMap.put(name, new BlogOldVirtualDB());
            }
        }
        return dbMap.get(name);
    }
}
