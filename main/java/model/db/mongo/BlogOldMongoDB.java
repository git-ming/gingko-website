package model.db.mongo;

import com.mongodb.DB;
import model.db.VirtualDB;
import model.db.VirtualTable;

/**
 * Created by xlo on 2015/9/16.
 * it's the db of mongo
 */
public class BlogOldMongoDB implements VirtualDB {
    DB mongoDatabase;

    protected BlogOldMongoDB(DB mongoDatabase) {
        this.mongoDatabase = mongoDatabase;
    }

    @Override
    public VirtualTable getTable(String name) {
        return new ImageMongoTable(mongoDatabase.getCollection(name));
    }
}
