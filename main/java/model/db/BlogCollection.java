package model.db;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.*;

/**
 * Created by xlo on 2015/8/28.
 * it's the collection of blog
 */
public class BlogCollection extends BlogDBCollection {

    public void addDocument(String author, String title, String body, Date date, String type, int preview) {
        lockCollection();
        Document document = new Document();
        document.put("author", author);
        document.put("title", title);
        document.put("body", body);
        document.put("time", date);
        document.put("type", type);
        document.put("reader", 0);
        document.put("preview", preview);
        this.insert(document);
        unlockCollection();
    }

    public void removeDocument(String id) {
        lockCollection();
        List<Map<String, Object>> iterable = collection.find(new Document("_id", new ObjectId(id)));
        Iterator<Map<String, Object>> cursor = iterable.iterator();
        if (!cursor.hasNext()) return;

        Map<String, Object> document = cursor.next();
        this.remove(new Document("_id", document.get("_id")));
    }

    public DBData getDocument(String id) {
        lockCollection();
        List<Map<String, Object>> iterable = collection.find(new Document("_id", new ObjectId(id)));
        Iterator<Map<String, Object>> cursor = iterable.iterator();
        if (!cursor.hasNext()) return null;

        Map<String, Object> document = cursor.next();
        return addDocumentToUsing(document);
    }

    public DBData getDocumentData(String id) {
        lockCollection();
        List<Map<String, Object>> iterable = collection.find(new Document("_id", new ObjectId(id)));
        Iterator<Map<String, Object>> cursor = iterable.iterator();
        if (!cursor.hasNext()) return null;

        Map<String, Object> document = cursor.next();
        DBData ans = getDocumentNotUsing(document);
        unlockCollection();
        return ans;
    }

    public List<DBData> findDocumentListData(Document message) {
        lockCollection();
        List<Map<String, Object>> iterable = collection.find(message);
        Iterator<Map<String, Object>> cursor = iterable.iterator();

        List<DBData> ans = new LinkedList<>();
        while (cursor.hasNext()) {
            Map<String, Object> document = cursor.next();
            ans.add(getDocumentNotUsing(document));
        }
        unlockCollection();
        return ans;
    }
}
