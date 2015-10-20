package model.cache;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xlo on 15-9-16.
 * it's the image cache manager
 */
public class ImageCacheManager {
    protected Map<String, File> fileMap;

    private static WaitDeleteCacheManager waitDeleteCacheManager;

    private ImageCacheManager() {
        this.fileMap = new HashMap<>();
    }

    public static WaitDeleteCacheManager getImageCacheManager() {
        if (waitDeleteCacheManager == null) {
            synchronized (ImageCacheManager.class) {
                if (waitDeleteCacheManager == null) {
                    waitDeleteCacheManager = new WaitDeleteCacheManager(5000, (object) -> {
                        File file = new File(object.toString());
                        while (file.exists() && !file.delete()) {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException ignored) {
                            }
                        }
                    });
                }
            }
        }
        return waitDeleteCacheManager;
    }
}
