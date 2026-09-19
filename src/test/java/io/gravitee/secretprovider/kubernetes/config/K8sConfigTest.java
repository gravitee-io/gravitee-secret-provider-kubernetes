/*
 * Copyright © 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
    void should_load_full_config_with_strings_only() {
        K8sConfig config = new K8sConfig(
            Map.of("enabled", "true", "kubeConfigFile", "/opt/config.json", "namespace", "foo", "timeoutMs", "1555")
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
        assertThat(config.getNamespace()).isEmpty();
    }
}
