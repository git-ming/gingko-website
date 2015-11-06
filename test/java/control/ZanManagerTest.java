package control;

import model.db.BlogCollection;
import model.db.ZanCollection;
import org.bson.Document;
import org.junit.Test;
import testTool.Counter;

import static org.junit.Assert.assertEquals;

/**
 * Created by xlo on 2015/11/6.
 * it's the zan manager
 */
public class ZanManagerTest {

    public static void zan(String username, String id) throws InterruptedException {
        Counter counter = new Counter(1);
        ZanManager zanManager = new ZanManagerNoSend(counter);
        zanManager.zan(username, "pass", id);
        while (counter.get() != 0) {
            Thread.sleep(500);
        }
    }

    public static void noZan(String username, String id) throws InterruptedException {
        Counter counter = new Counter(1);
        ZanManager zanManager = new ZanManagerNoSend(counter);
        zanManager.noZan(username, "pass", id);
        while (counter.get() != 0) {
            Thread.sleep(500);
        }
    }

    public static void isZan(String username, String id) throws InterruptedException {
        Counter counter = new Counter(1);
        ZanManager zanManager = new ZanManagerNoSend(counter);
        zanManager.isZan(username, "pass", id);
        while (counter.get() != 0) {
            Thread.sleep(500);
        }
    }

    @Test
    public void testZan() throws Exception {
        UserManagerTest.register("test user");
        BlogManagerTest.addDocument("test user", "title", "body", new Counter(1), "default");

        BlogCollection blogCollection = new BlogCollection();
        String id = blogCollection.findDocumentListData(new Document("author", "test user")).get(0).object.get("_id").toString();

        zan("test user", id);
        zan("test user", id);

        ZanCollection zanCollection = new ZanCollection();
        assertEquals(1, zanCollection.findZanListData(new Document().append("username", "test user").append("document", id)).size());
    }

    @Test
    public void testNoZan() throws Exception {
        UserManagerTest.register("test user");
        BlogManagerTest.addDocument("test user", "title", "body", new Counter(1), "default");

        BlogCollection blogCollection = new BlogCollection();
        String id = blogCollection.findDocumentListData(new Document("author", "test user")).get(0).object.get("_id").toString();

        zan("test user", id);
        noZan("test user", id);

        ZanCollection zanCollection = new ZanCollection();
        assertEquals(1, zanCollection.findZanListData(new Document().append("username", "test user").append("document", id)).size());
    }

    @Test
    public void testIsZan() throws Exception {
        UserManagerTest.register("test user");
        BlogManagerTest.addDocument("test user", "title", "body", new Counter(1), "default");

        BlogCollection blogCollection = new BlogCollection();
        String id = blogCollection.findDocumentListData(new Document("author", "test user")).get(0).object.get("_id").toString();

        zan("test user", id);
        Counter counter = new Counter(1);
        ZanManagerNoSend zanManager = new ZanManagerNoSend(counter);
        zanManager.isZan("test user", "pass", id);
        while (counter.get() != 0) {
            Thread.sleep(500);
        }
        assertEquals(1, counter.getSuccess());
    }
}