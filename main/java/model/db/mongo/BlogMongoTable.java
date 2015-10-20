package model.db.mongo;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import model.db.VirtualDataTable;
import org.bson.Document;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by xlo on 2015/9/14.
 * it's the mongo collection
 */
public class BlogMongoTable implements VirtualDataTable {
    MongoCollection<Document> mongoCollection;

    protected BlogMongoTable(MongoCollection<Document> mongoCollection) {
        this.mongoCollection = mongoCollection;
    }

    @Override
    public long count(Map<String, Object> map) {
        return this.mongoCollection.count(new Document(map));
    }

    @Override
    public List<Map<String, Object>> find() {
        List<Map<String, Object>> ans = new LinkedList<>();
        FindIterable<Document> iterable = this.mongoCollection.find();
        for (Document now : iterable) {
            ans.add(now);
        }
        return ans;
    }

    @Override
    public List<Map<String, Object>> find(Map<String, Object> map) {
        List<Map<String, Object>> ans = new LinkedList<>();
        FindIterable<Document> iterable = this.mongoCollection.find(new Document(map));
        for (Document now : iterable) {
            ans.add(now);
        }
        return ans;
    }

    @Override
    public void insertOne(Map<String, Object> map) {
        this.mongoCollection.insertOne(new Document(map));
    }

    @Override
    public void deleteOne(Map<String, Object> map) {
        this.mongoCollection.deleteOne(new Document(map));
    }

    @Override
    public void updateOne(Map<String, Object> filter, Map<String, Object> update) {
        this.mongoCollection.updateOne(new Document(filter), new Document(update));
    }
}
