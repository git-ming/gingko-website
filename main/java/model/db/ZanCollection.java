package model.db;

import org.bson.Document;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by xlo on 2015/11/6.
 * it's the zan collection
 */
public class ZanCollection extends BlogDBCollection {

    public void addZanDocument(String username, String documentID) {
        this.lockCollection();
        Document document = new Document();
        document.put("username", username);
        document.put("document", documentID);
        this.insert(document);
    }

    public DBData getZan(String username, String documentID) {
        lockCollection();
        DBData ans;
        List<Map<String, Object>> iterable = collection.find(new Document("username", username).append("document", documentID));
        if (iterable.size() == 0) {
            ans = null;
        } else {
            ans = getDocumentNotUsing(iterable.get(0));
        }
        unlockCollection();
        return ans;
    }

    public List<DBData> findZanListData(Document document) {
        lockCollection();
        List<DBData> ans = new LinkedList<>();
        List<Map<String, Object>> iterable = collection.find(document);

        ans.addAll(iterable.stream().map(this::getDocumentNotUsing).collect(Collectors.toList()));
        unlockCollection();
        return ans;
    }

    public void removeZan(String username, String documentID) {
        this.remove(new Document().append("username", username).append("document", documentID));
    }
}
