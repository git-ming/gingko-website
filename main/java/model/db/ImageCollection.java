package model.db;

import model.cache.ImageCacheManager;

import java.io.File;
import java.io.IOException;

/**
 * Created by xlo on 2015/9/16.
 * it's the image collection
 */
public class ImageCollection extends DBFileTable {
    @Override
    public long count() {
        lockCollection();
        long ans = super.count();
        unlockCollection();
        return ans;
    }

    @Override
    public File find(String s) throws IOException {
        lockCollection();
        File ans = super.find(s);
        unlockCollection();
        ImageCacheManager.getImageCacheManager().cache(ans.getAbsoluteFile());
        return ans;
    }

    @Override
    public String insert(byte[] bytes, String type) {
        lockCollection();
        String ans = super.insert(bytes, type);
        unlockCollection();
        return ans;
    }

    @Override
    public void delete(String s) {
        lockCollection();
        super.delete(s);
        unlockCollection();
    }

    @Override
    public Long getFileLength(String s) {
        lockCollection();
        Long ans = super.getFileLength(s);
        unlockCollection();
        return ans;
    }
}
