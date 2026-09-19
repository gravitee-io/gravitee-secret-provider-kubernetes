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

import static io.gravitee.secretprovider.kubernetes.test.TestUtils.newConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import io.gravitee.secrets.api.core.SecretURL;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * @author Benoit BORDIGONI (benoit.bordigoni at graviteesource.com)
 * @author GraviteeSource Team
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class K8sSecretLocationTest {

    public static Stream<Arguments> urls() {
        return Stream.of(
            arguments(SecretURL.from("secret://kubernetes/foo"), "from_config", null),
            arguments(SecretURL.from("secret://kubernetes/foo:bar"), "from_config", "bar"),
            arguments(SecretURL.from("secret://kubernetes/foo?namespace=buzz"), "buzz", null),
            arguments(SecretURL.from("secret://kubernetes/foo:bar?namespace=buzz"), "buzz", "bar")
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("urls")
    void should_build_from_URL(SecretURL url, String namespace, String key) {
        Map<String, Object> token = newConfig(Map.of("namespace", "from_config"));
        K8sConfig k8sConfig = new K8sConfig(token);
        K8sSecretLocation cut = K8sSecretLocation.fromURL(url, k8sConfig);
        assertThat(cut.key()).isEqualTo(key);
        assertThat(cut.namespace()).isEqualTo(namespace);
        assertThat(cut.secret()).isEqualTo("foo");
    }
}
