package model.config;

import org.dom4j.DocumentException;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.*;

/**
 * Created by xlo on 2015/9/18.
 * it's test the const
 */
public class ConstConfigTest {
    @Test
    public void testLoad() throws DocumentException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        ConstConfig constConfig = ConstConfig.getConfig();
        constConfig.init();
        assertEquals(Integer.class, constConfig.getConst("preview default").getClass());
        assertEquals(100, constConfig.getConst("preview default"));
    }
}