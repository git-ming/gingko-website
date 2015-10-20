package model.db;

import org.bson.Document;

import java.util.Map;

/**
 * Created by xlo on 15-8-23.
 * it's handle mongo client
 */
public abstract class BlogDBCollection extends DBTable {

    public synchronized static void init() {
        DBTable.init();
    }

    private static VirtualDB getDatabase(String name) {
        return databaseMap.get(name);
    }

    @Override
    protected void updateUsing() {
        using.stream().filter(now -> !now.object.equals(now.past)).forEach(now
                -> collection.updateOne(new Document("_id", now.id),
                new Document("$set", now.object)));
    }

    @Override
    protected Map<String, Object> buildNewDocument() {
        return new Document();
    }

    @Override
    protected String getIdObjectKey() {
        return "_id";
    }

}
