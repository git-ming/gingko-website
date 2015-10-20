package model.db;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.*;

/**
 * Created by xlo on 2015/8/31.
 * it's the message collection
 */
public class MessageCollection extends BlogDBCollection {

    public void addMessage(String username, String author, String message, Date date, String type, int preview) {
        lockCollection();
        Document document = new Document();
        document.put("username", username);
        document.put("author", author);
        document.put("time", date);
        document.put("message", message);
        document.put("read", false);
        document.put("type", type);
        document.put("preview", preview);
        this.insert(document);
        unlockCollection();
    }

    public void removeMessage(String id) {
        lockCollection();
        List<Map<String, Object>> iterable = collection.find(new Document("_id", new ObjectId(id)));
        Iterator<Map<String, Object>> cursor = iterable.iterator();
        if (!cursor.hasNext()) return;

        Map<String, Object> document = cursor.next();
        this.remove(new Document("_id", document.get("_id")));
    }

    public DBData getMessage(String id) {
        lockCollection();
        List<Map<String, Object>> iterable = collection.find(new Document("_id", new ObjectId(id)));
        Iterator<Map<String, Object>> cursor = iterable.iterator();
        if (!cursor.hasNext()) return null;

        Map<String, Object> document = cursor.next();
        return addDocumentToUsing(document);
    }

    public DBData getMessageData(String id) {
        lockCollection();
        List<Map<String, Object>> iterable = collection.find(new Document("_id", new ObjectId(id)));
        Iterator<Map<String, Object>> cursor = iterable.iterator();
        if (!cursor.hasNext()) return null;

        Map<String, Object> document = cursor.next();
        DBData data = getDocumentNotUsing(document);
        unlockCollection();
        return data;
    }

    public List<DBData> findMessageData(Document document) {
        lockCollection();
        List<Map<String, Object>> iterable = collection.find(document);
        Iterator<Map<String, Object>> cursor = iterable.iterator();

        List<DBData> ans = new LinkedList<>();
        while (cursor.hasNext()) {
            ans.add(getDocumentNotUsing(cursor.next()));
        }
        unlockCollection();
        return ans;
    }
}
