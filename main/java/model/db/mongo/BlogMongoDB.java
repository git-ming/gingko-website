package model.db.mongo;

import com.mongodb.client.MongoDatabase;
import model.db.VirtualDB;
import model.db.VirtualTable;

/**
 * Created by xlo on 2015/9/14.
 * it's the db of mongo
 */
public class BlogMongoDB implements VirtualDB {
    MongoDatabase mongoDatabase;

    protected BlogMongoDB(MongoDatabase mongoDatabase) {
        this.mongoDatabase = mongoDatabase;
    }

    @Override
    public VirtualTable getTable(String name) {
        return new BlogMongoTable(mongoDatabase.getCollection(name));
    }
}
