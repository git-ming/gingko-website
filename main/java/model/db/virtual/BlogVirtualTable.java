package model.db.virtual;

import model.db.VirtualDataTable;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by xlo on 2015/9/1.
 * it's the virtual collection
 */
public class BlogVirtualTable implements VirtualDataTable {
    protected Map<String, Document> value = new HashMap<>();

    @Override
    public long count(Map<String, Object> map) {
        return find(map).size();
    }

    @Override
    public List<Map<String, Object>> find() {
        List<Map<String, Object>> ans = new LinkedList<>();
        for (Map.Entry<String, Document> entry : this.value.entrySet()) {
            Document document = new Document();
            document.putAll(entry.getValue());
            ans.add(document);
        }
        return ans;
    }

    @Override
    public List<Map<String, Object>> find(Map<String, Object> map) {
        Document document = new Document();
        document.putAll(map);
        List<Map<String, Object>> ret = find(document);
        List<Map<String, Object>> ans = new LinkedList<>();
        for (Map<String, Object> now : ret) {
            HashMap<String, Object> e = new HashMap<>(now);
            ans.add(e);
        }
        return ans;
    }

    @Override
    public void insertOne(Map<String, Object> map) {
        if (!map.containsKey("_id")) {
            map.put("_id", new ObjectId());
        }
        this.value.put(map.get("_id").toString(), (Document) map);
    }

    @Override
    public void deleteOne(Map<String, Object> map) {
        List<Map<String, Object>> ans = find(map);
        if (ans.size() == 0) return;
        this.value.remove(ans.get(0).get("_id").toString());
    }

    @Override
    public void updateOne(Map<String, Object> filter, Map<String, Object> update) {
        Document filterDocument = new Document();
        filterDocument.putAll(filter);
        List<Map<String, Object>> ans = find(filterDocument);
        if (ans.size() == 0) return;
        update = (Document) update.get("$set");
        Map<String, Object> document = ans.get(0);
        for (Map.Entry<String, Object> entry : update.entrySet()) {
            document.put(entry.getKey(), entry.getValue());
        }
    }

    protected List<Map<String, Object>> find(Document filter) {
        List<Map<String, Object>> ans = new LinkedList<>();
        for (Map.Entry<String, Document> nowEntry : value.entrySet()) {
            Document now = nowEntry.getValue();
            boolean flag = true;
            for (Map.Entry<String, Object> entry : filter.entrySet()) {
                if (!now.containsKey(entry.getKey()) || !now.get(entry.getKey()).equals(entry.getValue())) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                ans.add(now);
            }
        }
        return ans;
    }
}
