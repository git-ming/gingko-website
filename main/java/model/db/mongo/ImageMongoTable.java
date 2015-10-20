package model.db.mongo;

import com.mongodb.DBCollection;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import model.db.VirtualFileTable;
import org.bson.types.ObjectId;

import java.io.File;
import java.io.IOException;

/**
 * Created by xlo on 2015/9/16.
 * it's the image table
 */
public class ImageMongoTable implements VirtualFileTable {
    protected DBCollection collection;
    protected GridFS gridFS;

    protected ImageMongoTable(DBCollection collection) {
        this.collection = collection;
        this.gridFS = new GridFS(collection.getDB(), collection.getName());
    }

    @Override
    public long count() {
        return this.collection.count();
    }

    @Override
    public File find(String s) throws IOException {
        GridFSDBFile gridFSDBFile = this.gridFS.findOne(s);
        File ans = new File("./" + s);
        if (ans.exists()) {
            return ans;
        }
        if (!ans.createNewFile()) throw new IOException();
        gridFSDBFile.writeTo(ans);
        return ans;
    }

    @Override
    public String insert(byte[] bytes, String type) {
        GridFSInputFile gridFSInputFile = this.gridFS.createFile(bytes);
        String id = new ObjectId().toString() + "." + type;
        gridFSInputFile.setFilename(id);
        gridFSInputFile.save();
        return id;
    }

    @Override
    public void delete(String s) {
        this.gridFS.remove(s);
    }

    @Override
    public Long getFileLength(String s) {
        return this.gridFS.find(new ObjectId(s)).getLength();
    }
}
