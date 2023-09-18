package io.gravitee.secretprovider.kubernetes.config;

import static org.assertj.core.api.Assertions.assertThat;

import io.gravitee.secretprovider.kubernetes.test.TestUtils;
import java.util.Map;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

/**
 * @author Benoit BORDIGONI (benoit.bordigoni at graviteesource.com)
 * @author GraviteeSource Team
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class K8sConfigTest {

    @Test
    void should_load_disabled_config() {
        K8sConfig config = new K8sConfig(Map.of());
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getKubeConfigFile()).isNull();
        assertThat(config.getNamespace()).isNull();
        assertThat(config.getTimeoutMs()).isZero();
    }

    @Test
    void should_load_full_config() {
        K8sConfig config = new K8sConfig(
            TestUtils.newConfig(Map.of("kubeConfigFile", "/opt/config.json", "namespace", "foo", "timeoutMs", 1555))
        );
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.getKubeConfigFile()).isEqualTo("/opt/config.json");
        assertThat(config.isClusterBased()).isFalse();
        assertThat(config.getNamespace()).isEqualTo("foo");
        assertThat(config.getTimeoutMs()).isEqualTo(1555);
    }

    @Test
    void should_load_full_in_cluster() {
        K8sConfig config = new K8sConfig(TestUtils.newConfig(Map.of()));
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.getKubeConfigFile()).isBlank();
        assertThat(config.isClusterBased()).isTrue();
        assertThat(config.getNamespace()).isEqualTo("default");
    }
}
