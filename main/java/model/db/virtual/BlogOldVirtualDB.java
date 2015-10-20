package model.db.virtual;

import model.db.VirtualDB;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xlo on 2015/9/16.
 * it's the db of mongo
 */
public class BlogOldVirtualDB implements VirtualDB {
    protected Map<String, ImageVirtualTable> collectionMap = new HashMap<>();

    @Override
    public synchronized ImageVirtualTable getTable(String collectionName) {
        if (!this.collectionMap.containsKey(collectionName)) {
            this.collectionMap.put(collectionName, new ImageVirtualTable());
        }
        return this.collectionMap.get(collectionName);
    }
}
