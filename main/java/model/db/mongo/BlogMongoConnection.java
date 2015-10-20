package model.db.mongo;

import com.mongodb.MongoClient;
import model.config.ConfigManager;
import model.config.DBConfig;
import model.db.VirtualDB;
import model.db.VirtualDBConnection;

/**
 * Created by xlo on 2015/9/14.
 * it's the connection of mongoDB
 */
public class BlogMongoConnection implements VirtualDBConnection {
    protected static DBConfig dbConfig = (DBConfig) ConfigManager.getConfigManager().getConfig(DBConfig.class);

    protected static MongoClient mongoClient = null;

    @SuppressWarnings("deprecation")
    @Override
    public VirtualDB getDatabase(String name) {
        if (mongoClient == null) {
            synchronized (BlogMongoConnection.class) {
                if (mongoClient == null) {
                    mongoClient = new MongoClient(dbConfig.getHost(), dbConfig.getPort());
                }
            }
        }
        if (dbConfig.getDBType(name).equals("default")) {
            return new BlogMongoDB(mongoClient.getDatabase(name));
        } else {
            return new BlogOldMongoDB(mongoClient.getDB(name));
        }
    }
}
