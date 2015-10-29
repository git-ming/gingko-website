package model.config;

import javafx.util.Pair;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by xlo on 15-8-30.
 * it's the test of access config
 */
public class AccessConfigTest {
    @Test
    public void testLoad() throws Exception {
        ConfigManager.getConfigManager().init();

        AccessConfig accessConfig = new AccessConfig();
        accessConfig.init();
        Set<Pair<String, Integer>> value = accessConfig.checkClassAndMethod("control.BlogManager", "addDocument");
        value.stream().filter(now -> now.getKey().equals("access")).forEach(now -> {
            int ans = now.getValue();
            assertEquals(-1, ans);
        });
    }
}