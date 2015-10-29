package model.config;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by xlo on 2015/8/20.
 * it's test config manager
 */
public class ConfigManagerTest {
    @Test
    public void testLoad() {
        assertNotNull(ConfigManager.getConfigManager().getConfig(ShowFileTypeConfig.class));
        assertTrue(((ShowFileTypeConfig)ConfigManager.getConfigManager().getConfig(ShowFileTypeConfig.class)).isShow("abc.html"));
    }
}