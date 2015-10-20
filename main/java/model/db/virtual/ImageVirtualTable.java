package model.db.virtual;

import model.db.VirtualFileTable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xlo on 2015/9/16.
 * it's the image virtual table
 */
public class ImageVirtualTable implements VirtualFileTable {
    protected int count = 0;
    protected Map<String, byte[]> value = new HashMap<>();

    @Override
    public long count() {
        return value.size();
    }

    @Override
    public File find(String s) throws IOException {
        File ans = new File("./" + s);
        if (ans.exists()) {
            return ans;
        }
        if (!ans.createNewFile()) throw new IOException();
        FileOutputStream outputStream = new FileOutputStream(ans);
        outputStream.write(value.get(s));
        outputStream.close();
        return ans;
    }

    @Override
    public String insert(byte[] bytes, String type) {
        String path = "file" + count + "." + type;
        count++;
        value.put(path, bytes);
        return path;
    }

    @Override
    public void delete(String s) {
        value.remove(s);
    }

    @Override
    public Long getFileLength(String s) {
        return (long) value.get(s).length;
    }
}
