package model.db.virtual;

import model.db.VirtualDB;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xlo on 2015/9/1.
 * it's the virtual db
 */
public class BlogVirtualDB implements VirtualDB {

    protected Map<String, BlogVirtualTable> collectionMap = new HashMap<>();

    @Override
    public synchronized BlogVirtualTable getTable(String collectionName) {
        if (!this.collectionMap.containsKey(collectionName)) {
            this.collectionMap.put(collectionName, new BlogVirtualTable());
        }
        return this.collectionMap.get(collectionName);
    }
}
