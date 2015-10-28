package model.config;

import org.dom4j.DocumentException;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by xlo on 2015/8/31.
 * it's the testing of length limit
 */
public class LengthLimitConfigTest {
    @Test
    public void testLoad() throws DocumentException {
        LengthLimitConfig lengthLimitConfig = LengthLimitConfig.getConfig();
        lengthLimitConfig.init();
        assertEquals(123, lengthLimitConfig.getLimit("username"));
    }
}