package com.graviteesource.secretprovider.kubernetes.config;

import static com.graviteesource.secretprovider.kubernetes.test.TestUtils.newConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import io.gravitee.node.api.secrets.model.SecretLocation;
import io.gravitee.node.api.secrets.model.SecretURL;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * @author Benoit BORDIGONI (benoit.bordigoni at graviteesource.com)
 * @author GraviteeSource Team
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class K8sSecretLocationTest {

    @Test
    void should_return_location_as_map() {
        Map<String, Object> map = new K8sSecretLocation("foo", "bar", "baz").asMap();
        assertThat(map).containsValues("foo", "bar", "baz");
    }

    @Test
    void should_return_location_as_map_no_key() {
        Map<String, Object> map = new K8sSecretLocation("foo", "bar", null).asMap();
        assertThat(map).containsValues("foo", "bar");
    }

    public static Stream<Arguments> goodLocation() {
        return Stream.of(
            arguments(
                "full",
                Map.of(
                    K8sSecretLocation.LOCATION_NAMESPACE,
                    "foo",
                    K8sSecretLocation.LOCATION_SECRET,
                    "bar",
                    K8sSecretLocation.LOCATION_KEY,
                    "baz"
                ),
                true
            ),
            arguments("key less", Map.of(K8sSecretLocation.LOCATION_NAMESPACE, "foo", K8sSecretLocation.LOCATION_SECRET, "bar"), false)
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("goodLocation")
    void should_build_from_location(String _name, Map<String, Object> location, boolean hasKey) {
        K8sSecretLocation k8sSecretLocation = K8sSecretLocation.fromLocation(new SecretLocation(location));
        assertThat(k8sSecretLocation.namespace()).isNotBlank();
        assertThat(k8sSecretLocation.secret()).isNotBlank();
        if (hasKey) {
            assertThat(k8sSecretLocation.key()).isNotBlank();
        }
    }

    public static Stream<Arguments> badLocation() {
        return Stream.of(
            arguments("empty", Map.of()),
            arguments("no namespace", Map.of(K8sSecretLocation.LOCATION_SECRET, "bar", K8sSecretLocation.LOCATION_KEY, "baz")),
            arguments("no secret", Map.of(K8sSecretLocation.LOCATION_NAMESPACE, "bar", K8sSecretLocation.LOCATION_KEY, "baz"))
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("badLocation")
    void should_fail_building_from_location(String _name, Map<String, Object> location) {
        SecretLocation secretLocation = new SecretLocation(location);
        assertThatCode(() -> K8sSecretLocation.fromLocation(secretLocation)).isInstanceOf(NullPointerException.class);
    }

    public static Stream<Arguments> urls() {
        return Stream.of(
            arguments(SecretURL.from("secret://kubernetes/foo"), "default", null),
            arguments(SecretURL.from("secret://kubernetes/foo:bar"), "default", "bar"),
            arguments(SecretURL.from("secret://kubernetes/foo?namespace=buzz"), "buzz", null),
            arguments(SecretURL.from("secret://kubernetes/foo:bar?namespace=buzz"), "buzz", "bar")
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("urls")
    void should_build_from_URL(SecretURL url, String namespace, String key) {
        Map<String, Object> token = newConfig(Map.of());
        K8sConfig k8sConfig = new K8sConfig(token);
        K8sSecretLocation cut = K8sSecretLocation.fromURL(url, k8sConfig);
        assertThat(cut.key()).isEqualTo(key);
        assertThat(cut.namespace()).isEqualTo(namespace);
        assertThat(cut.secret()).isEqualTo("foo");
    }
}
