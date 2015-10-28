package model.config;

import model.db.UserCollection;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by xlo on 2015/8/27.
 * it's db config test
 */
public class BlogDBConfigTest {
    @Test
    public void testLoad() throws Exception {
        BlogDBConfig blogDbConfig = new BlogDBConfig();
        blogDbConfig.init();
        assertEquals("user", blogDbConfig.getCollectionName(UserCollection.class));
        assertEquals("blog", blogDbConfig.getDBofCollection("user"));
    }
}