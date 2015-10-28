package control;

import model.db.BlogCollection;
import model.db.BlogDBCollection;
import net.sf.json.JSONObject;
import org.bson.Document;
import org.junit.After;
import org.junit.Test;
import testTool.Counter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by xlo on 2015/9/2.
 * it's the testing code for blog manager
 */
public class BlogManagerTest {

    public static void addDocument(String author, String title, String body, Counter counter, String type) throws InterruptedException {
        BlogManagerNoSend blogManagerNoSend = new BlogManagerNoSend(counter);
        blogManagerNoSend.addDocument(author, "pass", title, body, type, "100");

        while (counter.get() != 0) {
            Thread.sleep(500);
        }
    }

    public static void addReply(Document document, String author, String reply, Counter counter) throws InterruptedException {
        BlogCollection collection = new BlogCollection();
        BlogDBCollection.DBData data = collection.findDocumentListData(document).get(0);
        if (data.object.get("_id") == null) {
            fail();
        }

        BlogManagerNoSend blogManagerNoSend = new BlogManagerNoSend(counter);
        blogManagerNoSend.addReply(author, "pass", data.object.get("_id").toString(), reply);

        while (counter.get() != 0) {
            Thread.sleep(500);
        }
    }

    public static void addReader(Document document, Counter counter) throws InterruptedException {
        BlogManagerNoSend blogManagerNoSend = new BlogManagerNoSend(counter);
        BlogCollection collection = new BlogCollection();
        BlogDBCollection.DBData data = collection.findDocumentListData(document).get(0);
        blogManagerNoSend.addReader(data.object.get("_id").toString());

        while (counter.get() != 0) {
            Thread.sleep(500);
        }
    }

    @After
    public void tearDown() throws InterruptedException {
        Counter counter = new Counter(1);
        UserManager userManager = new UserManagerNoSend(counter);
        userManager.removeUser("test user", "pass");
        while (counter.get() != 0) {
            Thread.sleep(500);
        }

        for (int i = 0; i < 10; i++) {
            counter = new Counter(1);
            userManager = new UserManagerNoSend(counter);
            userManager.removeUser("test user " + i, "pass");
            while (counter.get() != 0) {
                Thread.sleep(500);
            }
        }
    }

    @Test
    public void testAddDocument() throws Exception {
        UserManagerTest.register("test user");

        addDocument("test user", "title", "body", new Counter(1), "default");

        BlogCollection collection = new BlogCollection();
        List<BlogDBCollection.DBData> data = collection.findDocumentListData(new Document().append("author", "test user"));
        assertEquals(1, data.size());
    }

    @Test
    public void testAddDocumentAgain() throws InterruptedException {
        int n = 10;

        for (int i = 0; i < n; i++) {
            UserManagerTest.register("test user " + i);
        }

        Counter counter = new Counter(n);
        for (int i = 0; i < n; i++) {
            final int finalI = i;
            new Thread() {
                @Override
                public void run() {
                    try {
                        addDocument("test user " + finalI, "title " + finalI, "body " + finalI, counter, "default");
                    } catch (Exception e) {
                        fail();
                    }
                }
            }.start();
        }

        while (counter.get() != 0) {
            Thread.sleep(500);
        }

        for (int i = 0; i < n; i++) {
            BlogCollection collection = new BlogCollection();
            List<BlogDBCollection.DBData> data = collection.findDocumentListData(new Document().append("author", "test user " + i));
            assertEquals(1, data.size());
        }
    }

    @Test
    public void testAddReply() throws Exception {
        testAddDocument();
        int n = 10;
        for (int i = 0; i < n; i++) {
            addReply(new Document().append("author", "test user"), "test user", "reply", new Counter(1));
        }

        BlogCollection collection = new BlogCollection();
        List<BlogDBCollection.DBData> listData = collection.findDocumentListData(new Document().append("author", "test user"));
        assertEquals(1, listData.size());
        BlogDBCollection.DBData data = listData.get(0);
        assertEquals(n, ((List) data.object.get("reply")).size());
        collection.submit();
    }

    @Test
    public void testReplyAgain() throws Exception {
        testAddDocument();
        int n = 20;
        Counter counter = new Counter(n);
        for (int i = 0; i < n; i++) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        addReply(new Document().append("author", "test user"), "test user", "reply", counter);
                    } catch (InterruptedException e) {
                        fail();
                    }
                }
            }.start();
        }

        while (counter.get() != 0) {
            Thread.sleep(500);
        }

        BlogCollection collection = new BlogCollection();
        List<BlogDBCollection.DBData> listData = collection.findDocumentListData(new Document().append("author", "test user"));
        assertEquals(1, listData.size());
        BlogDBCollection.DBData data = listData.get(0);
        assertEquals(n, ((List) data.object.get("reply")).size());
    }

    @Test
    public void testAddReader() throws Exception {
        testAddDocument();
        int n = 10;
        for (int i = 0; i < n; i++) {
            addReader(new Document().append("author", "test user"), new Counter(1));
        }

        BlogCollection collection = new BlogCollection();
        BlogDBCollection.DBData data = collection.findDocumentListData(new Document().append("author", "test user")).get(0);
        assertEquals(n, ((Document) data.object).getInteger("reader", 0));
    }


    @Test
    public void testAddReaderAgain() throws Exception {
        testAddDocument();
        int n = 20;
        Counter counter = new Counter(n);
        for (int i = 0; i < n; i++) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        addReader(new Document().append("author", "test user"), counter);
                    } catch (InterruptedException e) {
                        fail();
                    }
                }
            }.start();
        }

        while (counter.get() != 0) {
            Thread.sleep(500);
        }

        BlogCollection collection = new BlogCollection();
        BlogDBCollection.DBData data = collection.findDocumentListData(new Document().append("author", "test user")).get(0);
        assertEquals(n, ((Document) data.object).getInteger("reader", 0));
    }

    @Test
    public void testGetDocument() throws Exception {
        testAddDocument();
        BlogCollection collection = new BlogCollection();
        List<BlogDBCollection.DBData> data = collection.findDocumentListData(new Document().append("author", "test user"));
        String id = data.get(0).object.get("_id").toString();

        Counter counter = new Counter(1);
        BlogManagerNoSend blogManager = new BlogManagerNoSend(counter);
        blogManager.getDocument(id);

        while (counter.get() != 0) {
            Thread.sleep(500);
        }
        assertEquals("body", blogManager.getManagerNoSend().getMessage().getString("body"));
    }

    @Test
    public void testGetDocumentList() throws InterruptedException {
        UserManagerTest.register("test user");
        noDocument();
        testTime();
    }

    private void testTime() throws InterruptedException {
        Counter counter;
        BlogManagerNoSend blogManager;Date from = new Date();
        Thread.sleep(1000);
        addDocument("test user", "1", "2", new Counter(1), "default");
        Thread.sleep(1000);
        Date to = new Date();
        Thread.sleep(1000);
        addDocument("test user", "2", "3", new Counter(1), "default");
        counter = new Counter(1);
        blogManager = new BlogManagerNoSend(counter);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        blogManager.getDocumentList("test user", "default", simpleDateFormat.format(from), simpleDateFormat.format(to), "1");
        while (counter.get() != 0) {
            Thread.sleep(500);
        }
        assertEquals(1, blogManager.getManagerNoSend().getArray().size());
    }

    private void noDocument() throws InterruptedException {
        Counter counter;
        BlogManagerNoSend blogManager;
        counter = new Counter(1);
        blogManager = new BlogManagerNoSend(counter);
        blogManager.getDocumentList("test user", "default", "null", "null,", "1");
        while (counter.get() != 0) {
            Thread.sleep(500);
        }
        assertEquals(0, blogManager.getManagerNoSend().getArray().size());
    }

//    @Test
//    public void testGetAuthorTypeDocumentList() throws InterruptedException {
//        UserManagerTest.register("test user");
//        addDocument("test user", "1", "2", new Counter(1), "default");
//        addDocument("test user", "2", "3", new Counter(1), "default");
//
//        Counter counter = new Counter(1);
//        BlogManagerNoSend blogManager = new BlogManagerNoSend(counter);
//        blogManager.getAuthorTypeDocumentList("test user", "default", "1");
//        while (counter.get() != 0) {
//            Thread.sleep(500);
//        }
//        assertEquals(2, blogManager.getManagerNoSend().getArray().size());
//
//        counter = new Counter(1);
//        blogManager = new BlogManagerNoSend(counter);
//        blogManager.getAuthorTypeDocumentListSize("test user", "default");
//        while (counter.get() != 0) {
//            Thread.sleep(500);
//        }
//        assertEquals(1, counter.getSuccess());
//        assertEquals(1, blogManager.getManagerNoSend().getMessage().getInt("return"));
//    }
//
//    @Test
//    public void testGetAuthorDocumentList() throws InterruptedException {
//        UserManagerTest.register("test user");
//        UserManagerTest.register("test user 2");
//        addDocument("test user", "1", "2", new Counter(1), "default");
//        addDocument("test user 2", "2", "3", new Counter(1), "default");
//
//        Counter counter = new Counter(1);
//        BlogManagerNoSend blogManager = new BlogManagerNoSend(counter);
//        blogManager.getAuthorDocumentList("test user", "1");
//        while (counter.get() != 0) {
//            Thread.sleep(500);
//        }
//        assertEquals(1, blogManager.getManagerNoSend().getArray().size());
//
//        counter = new Counter(1);
//        blogManager = new BlogManagerNoSend(counter);
//        blogManager.getAuthorDocumentListSize("test user");
//        while (counter.get() != 0) {
//            Thread.sleep(500);
//        }
//        assertEquals(1, counter.getSuccess());
//        assertEquals(1, blogManager.getManagerNoSend().getMessage().getInt("return"));
//    }
//
//    @Test
//    public void testGetTypeDocumentList() throws InterruptedException {
//        UserManagerTest.register("test user");
//        UserManagerTest.register("test user 2");
//        addDocument("test user", "1", "2", new Counter(1), "default");
//        addDocument("test user", "1", "2", new Counter(1), "type2");
//        addDocument("test user 2", "2", "3", new Counter(1), "type2");
//
//        Counter counter = new Counter(1);
//        BlogManagerNoSend blogManager = new BlogManagerNoSend(counter);
//        blogManager.getTypeDocumentList("type2", "1");
//        while (counter.get() != 0) {
//            Thread.sleep(500);
//        }
//        assertEquals(2, blogManager.getManagerNoSend().getArray().size());
//
//        counter = new Counter(1);
//        blogManager = new BlogManagerNoSend(counter);
//        blogManager.getTypeDocumentListSize("default");
//        while (counter.get() != 0) {
//            Thread.sleep(500);
//        }
//        assertEquals(1, counter.getSuccess());
//        assertEquals(1, blogManager.getManagerNoSend().getMessage().getInt("return"));
//    }
}