package model.config;

import org.dom4j.DocumentException;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by xlo on 2015/8/21.
 * it's test return code config
 */
public class ReturnCodeConfigTest {
    @Test
    public void testLoad() throws DocumentException {
        ReturnCodeConfig returnCodeConfig = new ReturnCodeConfig();
        returnCodeConfig.init();
        assertEquals("200", returnCodeConfig.getCode("accept"));
    }
}