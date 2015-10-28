package control;

import model.cache.ImageCacheManager;
import model.db.ImageCollection;
import net.sf.json.JSONObject;
import org.junit.After;
import org.junit.Test;
import testTool.Counter;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by xlo on 2015/9/16.
 * it's the testing code for image manager
 */
public class ImageManagerTest {

    protected static Set<String> images = new HashSet<>();

    public static String uploadImage(String username, byte[] file, Counter counter) throws InterruptedException {
        ImageManagerNoSend imageManager = new ImageManagerNoSend(counter);
        imageManager.uploadImage(username, "pass", "jpg", file);

        while (counter.get() != 0) {
            Thread.sleep(500);
        }
        JSONObject object = imageManager.getManagerNoSend().getMessage();
        assertNotNull(object);
        String path = object.getString("return");
        images.add(path);
        return path;
    }

    public static String uploadImage(String username, byte[] file) throws InterruptedException {
        Counter counter = new Counter(1);
        return uploadImage(username, file, counter);
    }

    public static byte[] getImage(String path) throws InterruptedException {
        Counter counter = new Counter(1);
        return getImage(path, counter);
    }

    public static byte[] getImage(String path, Counter counter) throws InterruptedException {
        ImageManagerNoSend imageManager = new ImageManagerNoSend(counter);
        imageManager.getImage(path);

        while (counter.get() != 0) {
            Thread.sleep(500);
        }
        return imageManager.getManagerNoSend().getFile();
    }

    @After
    public void tearDown() throws Exception {
        UserManagerTest.remove("test user");
        ImageCollection imageCollection = new ImageCollection();
        images.forEach(imageCollection::delete);
        ImageCacheManager.getImageCacheManager().clear();
    }

    @Test
    public void testImage() throws Exception {
        UserManagerTest.register("test user");
        String path = uploadImage("test user", "abc".getBytes());
        byte[] value = getImage(path);
        assertEquals("abc", new String(value));
    }

    @Test
    public void testImageAgain() throws Exception {
        UserManagerTest.register("test user");
        int n = 10;
        String[] path = new String[n];
        Counter counter = new Counter(n);
        for (int i = 0; i < n; i++) {
            final int finalI = i;
            new Thread() {
                @Override
                public void run() {
                    try {
                        path[finalI] = uploadImage("test user", ("body " + finalI).getBytes());
                        counter.add(-1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        while (counter.get() != 0) {
            Thread.sleep(500);
        }

        int m = 5;
        for (int i = 0; i < n; i++) {
            final int finalI = i;
            for (int j = 0; j < m; j++) {
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            byte[] ans = getImage(path[finalI]);
                            assertEquals("body " + finalI, new String(ans));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        }
    }
}