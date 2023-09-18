package io.gravitee.secretprovider.kubernetes;

import static io.gravitee.secretprovider.kubernetes.test.TestUtils.newConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import io.gravitee.node.api.secrets.errors.SecretManagerConfigurationException;
import io.gravitee.node.api.secrets.model.SecretEvent;
import io.gravitee.node.api.secrets.model.SecretMap;
import io.gravitee.node.api.secrets.model.SecretMount;
import io.gravitee.node.api.secrets.model.SecretURL;
import io.gravitee.secretprovider.kubernetes.client.K8sSecretWatchResult;
import io.gravitee.secretprovider.kubernetes.client.api.K8sClient;
import io.gravitee.secretprovider.kubernetes.config.K8sConfig;
import io.gravitee.secretprovider.kubernetes.config.K8sSecretLocation;
import io.kubernetes.client.openapi.models.V1Secret;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;

/**
 * @author Benoit BORDIGONI (benoit.bordigoni at graviteesource.com)
 * @author GraviteeSource Team
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class KubernetesSecretProviderTest {

    @Nested
    class WithFactory {

        KubernetesSecretProvider cut;

        @BeforeEach
        void setup() {
            cut =
                (KubernetesSecretProvider) new KubernetesSecretProviderFactory()
                    .create(new K8sConfig(newConfig(Map.of("namespace", "myapp", "kubeConfigFile", "src/test/resources/config.yaml"))));
        }

        @Test
        void should_return_secret_mount_from_URL_with_key() {
            SecretURL url = SecretURL.from("secret://kubernetes/secret/foo:bar");
            SecretMount secretMount = cut.fromURL(url);
            assertThat(secretMount.secretURL()).isEqualTo(url);
            assertThat(secretMount.provider()).isEqualTo("kubernetes");
            assertThat(K8sSecretLocation.fromLocation(secretMount.location()).secret()).isEqualTo("secret/foo");
            assertThat(K8sSecretLocation.fromLocation(secretMount.location()).namespace()).isEqualTo("myapp");
            assertThat(K8sSecretLocation.fromLocation(secretMount.location()).key()).isEqualTo("bar");
            assertThat(secretMount.key()).isEqualTo("bar");
        }

        @Test
        void should_return_secret_mount_from_URL_with_slash_and_key() {
            SecretURL url = SecretURL.from("secret://kubernetes/secret/foo/foo:bar");
            SecretMount secretMount = cut.fromURL(url);
            assertThat(secretMount.secretURL()).isEqualTo(url);
            assertThat(secretMount.provider()).isEqualTo("kubernetes");
            assertThat(K8sSecretLocation.fromLocation(secretMount.location()).secret()).isEqualTo("secret/foo/foo");
            assertThat(K8sSecretLocation.fromLocation(secretMount.location()).namespace()).isEqualTo("myapp");
            assertThat(K8sSecretLocation.fromLocation(secretMount.location()).key()).isEqualTo("bar");
            assertThat(secretMount.key()).isEqualTo("bar");
        }

        @Test
        void should_return_secret_mount_from_URL_without_key_but_namespace() {
            SecretURL url = SecretURL.from("secret://kubernetes/secret/foo?namespace=buzz");
            SecretMount secretMount = cut.fromURL(url);
            assertThat(secretMount.secretURL()).isEqualTo(url);
            assertThat(secretMount.provider()).isEqualTo("kubernetes");
            assertThat(K8sSecretLocation.fromLocation(secretMount.location()).secret()).isEqualTo("secret/foo");
            assertThat(K8sSecretLocation.fromLocation(secretMount.location()).namespace()).isEqualTo("buzz");
            assertThat(K8sSecretLocation.fromLocation(secretMount.location()).key()).isNull();
            assertThat(secretMount.key()).isNull();
            assertThat(secretMount.isKeyEmpty()).isTrue();
        }

        @Test
        void should_fail_returning_secret_mount() {
            SecretURL url = SecretURL.from("secret://foo/secret/bar");
            assertThatCode(() -> cut.fromURL(url)).isInstanceOf(SecretManagerConfigurationException.class);
        }
    }

    @Nested
    class WithMockClient {

        private V1Secret secret;
        private Flowable<K8sSecretWatchResult> flowable;

        class MockClient implements K8sClient {

            @SneakyThrows
            @Override
            public K8sConfig config() {
                return new K8sConfig(newConfig(Map.of()));
            }

            @Override
            public Optional<V1Secret> getSecret(K8sSecretLocation location) {
                return Optional.of(secret);
            }

            @Override
            public Flowable<K8sSecretWatchResult> watchSecret(String namespace, String secret) {
                return flowable;
            }
        }

        @Test
        void should_resolve_with_mock_client_and_key_map() {
            this.secret = new V1Secret();
            secret.data(Map.of("pwd", "changeme".getBytes(StandardCharsets.UTF_8), "usr", "admin".getBytes()));
            KubernetesSecretProvider cut = new KubernetesSecretProvider(new MockClient());
            Maybe<SecretMap> result = cut.resolve(
                cut.fromURL(SecretURL.from("secret://kubernetes/secret/foo?keymap=username:usr&keymap=password:pwd"))
            );
            SecretMap secretMap = result.blockingGet();
            assertThat(secretMap).isNotNull();
            assertThat(secretMap.wellKnown(SecretMap.WellKnownSecretKey.USERNAME)).isPresent();
            assertThat(secretMap.wellKnown(SecretMap.WellKnownSecretKey.USERNAME).get().asString()).isEqualTo("admin");
            assertThat(secretMap.wellKnown(SecretMap.WellKnownSecretKey.PASSWORD)).isPresent();
            assertThat(secretMap.wellKnown(SecretMap.WellKnownSecretKey.PASSWORD).get().asString()).isEqualTo("changeme");
        }

        @Test
        void should_watch_with_mock_client_and_key_map() {
            V1Secret v1Secret = new V1Secret();
            v1Secret.data(Map.of("key", "changeme".getBytes(), "pub", "admin".getBytes()));
            this.flowable = Flowable.fromIterable(List.of(new K8sSecretWatchResult(SecretEvent.Type.CREATED, v1Secret)));
            KubernetesSecretProvider cut = new KubernetesSecretProvider(new MockClient());

            Flowable<SecretEvent> result = cut.watch(
                cut.fromURL(SecretURL.from("secret://kubernetes/secret/foo?keymap=certificate:pub&keymap=private_key:key"))
            );
            List<SecretEvent> events = result.toList().blockingGet();

            assertThat(events).hasSize(1);
            SecretMap secretMap = events.get(0).secretMap();
            assertThat(secretMap).isNotNull();
            assertThat(secretMap.wellKnown(SecretMap.WellKnownSecretKey.CERTIFICATE)).isPresent();
            assertThat(secretMap.wellKnown(SecretMap.WellKnownSecretKey.CERTIFICATE).get().asString()).isEqualTo("admin");
            assertThat(secretMap.wellKnown(SecretMap.WellKnownSecretKey.PRIVATE_KEY)).isPresent();
            assertThat(secretMap.wellKnown(SecretMap.WellKnownSecretKey.PRIVATE_KEY).get().asString()).isEqualTo("changeme");
        }

        @Test
        void should_resolve_basic_with_mock_client_and_no_keymap() {
            this.secret = new V1Secret();
            secret.data(Map.of("password", "changeme".getBytes(StandardCharsets.UTF_8), "username", "admin".getBytes()));
            KubernetesSecretProvider cut = new KubernetesSecretProvider(new MockClient());

            Maybe<SecretMap> result = cut.resolve(cut.fromURL(SecretURL.from("secret://kubernetes/secret/foo")));

            SecretMap secretMap = result.blockingGet();
            assertThat(secretMap).isNotNull();
            assertThat(secretMap.wellKnown(SecretMap.WellKnownSecretKey.USERNAME)).isPresent();
            assertThat(secretMap.wellKnown(SecretMap.WellKnownSecretKey.USERNAME).get().asString()).isEqualTo("admin");
            assertThat(secretMap.wellKnown(SecretMap.WellKnownSecretKey.PASSWORD)).isPresent();
            assertThat(secretMap.wellKnown(SecretMap.WellKnownSecretKey.PASSWORD).get().asString()).isEqualTo("changeme");
        }

        @Test
        void should_watch_tls_with_mock_client_and_no_keymap() {
            V1Secret v1Secret = new V1Secret();
            v1Secret.data(Map.of("tls.key", "changeme".getBytes(StandardCharsets.UTF_8), "tls.crt", "admin".getBytes()));

            this.flowable = Flowable.fromIterable(List.of(new K8sSecretWatchResult(SecretEvent.Type.CREATED, v1Secret)));
            KubernetesSecretProvider cut = new KubernetesSecretProvider(new MockClient());
            Flowable<SecretEvent> result = cut.watch(cut.fromURL(SecretURL.from("secret://kubernetes/secret/foo")));
            List<SecretEvent> events = result.toList().blockingGet();
            assertThat(events).hasSize(1);
            SecretMap secretMap = events.get(0).secretMap();
            assertThat(secretMap).isNotNull();
            assertThat(secretMap.wellKnown(SecretMap.WellKnownSecretKey.CERTIFICATE)).isPresent();
            assertThat(secretMap.wellKnown(SecretMap.WellKnownSecretKey.CERTIFICATE).get().asString()).isEqualTo("admin");
            assertThat(secretMap.wellKnown(SecretMap.WellKnownSecretKey.PRIVATE_KEY)).isPresent();
            assertThat(secretMap.wellKnown(SecretMap.WellKnownSecretKey.PRIVATE_KEY).get().asString()).isEqualTo("changeme");
        }
    }
}
