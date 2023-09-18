package io.gravitee.secretprovider.kubernetes.test;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author Benoit BORDIGONI (benoit.bordigoni at graviteesource.com)
 * @author GraviteeSource Team
 */
public class TestUtils {

    public static Map<String, Object> newConfig(Map<String, Object> additionalConfig) {
        HashMap<String, Object> map = new HashMap<>(Map.of("enabled", true));
        map.putAll(additionalConfig);
        return map;
    }
}
