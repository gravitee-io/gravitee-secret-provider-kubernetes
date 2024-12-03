package io.gravitee.secretprovider.kubernetes;

import static io.gravitee.secretprovider.kubernetes.test.TestUtils.newConfig;
import static org.assertj.core.api.Assertions.assertThat;

import io.gravitee.kubernetes.client.model.v1.Event;
import io.gravitee.kubernetes.client.model.v1.Secret;
import io.gravitee.secretprovider.kubernetes.client.api.K8sClient;
import io.gravitee.secretprovider.kubernetes.config.K8sConfig;
import io.gravitee.secretprovider.kubernetes.config.K8sSecretLocation;
import io.gravitee.secrets.api.core.SecretEvent;
import io.gravitee.secrets.api.core.SecretMap;
import io.gravitee.secrets.api.core.SecretMount;
import io.gravitee.secrets.api.core.SecretURL;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
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
    }

    @Nested
    class WithMockClient {

        private Secret secret;
        private Flowable<Event<Secret>> flowable;

        class MockClient implements K8sClient {

            @Override
            public Maybe<io.gravitee.kubernetes.client.model.v1.Secret> getSecret(K8sSecretLocation location) {
                return secret == null ? Maybe.empty() : Maybe.just(secret);
            }

            @Override
            public Flowable<Event<Secret>> watchSecret(K8sSecretLocation location) {
                return flowable;
            }

            @SneakyThrows
            @Override
            public K8sConfig config() {
                return new K8sConfig(newConfig(Map.of()));
            }
        }

        @Test
        void should_resolve_with_mock_client_and_key_map() {
            this.secret = new Secret();
            secret.setData(Map.of("pwd", base64("changeme"), "usr", base64("admin")));
            KubernetesSecretProvider cut = new KubernetesSecretProvider(new MockClient());
            Maybe<SecretMap> result = cut.resolve(
                cut.fromURL(SecretURL.from("secret://kubernetes/secret/foo?keymap=username:usr&keymap=password:pwd"))
            );
            SecretMap secretMap = result.blockingGet();
            assertThat(secretMap).isNotNull();
            assertThat(secretMap.wellKnown(SecretMap.WellKnownSecretKey.USERNAME))
                .isPresent()
                .get()
                .extracting(io.gravitee.secrets.api.core.Secret::asString)
                .isEqualTo("admin");
            assertThat(secretMap.wellKnown(SecretMap.WellKnownSecretKey.PASSWORD))
                .isPresent()
                .get()
                .extracting(io.gravitee.secrets.api.core.Secret::asString)
                .isEqualTo("changeme");
        }

        @Test
        void should_watch_with_mock_client_and_key_map() {
            this.secret = new Secret();
            secret.setData(Map.of("key", base64("changeme"), "pub", base64("admin")));
            this.flowable = Flowable.fromIterable(List.of(new Event<>("CREATED", secret)));
            KubernetesSecretProvider cut = new KubernetesSecretProvider(new MockClient());

            Flowable<SecretEvent> result = cut.watch(
                cut.fromURL(SecretURL.from("secret://kubernetes/secret/foo?keymap=certificate:pub&keymap=private_key:key"))
            );
            List<SecretEvent> events = result.toList().blockingGet();

            assertThat(events).hasSize(1);
            SecretMap secretMap = events.get(0).secretMap();
            assertThat(secretMap).isNotNull();
            assertThat(secretMap.wellKnown(SecretMap.WellKnownSecretKey.CERTIFICATE))
                .isPresent()
                .get()
                .extracting(io.gravitee.secrets.api.core.Secret::asString)
                .isEqualTo("admin");
            assertThat(secretMap.wellKnown(SecretMap.WellKnownSecretKey.PRIVATE_KEY))
                .isPresent()
                .get()
                .extracting(io.gravitee.secrets.api.core.Secret::asString)
                .isEqualTo("changeme");
        }

        @Test
        void should_resolve_basic_with_mock_client_and_no_keymap() {
            this.secret = new Secret();
            secret.setData(Map.of("password", base64("changeme"), "username", base64("admin")));
            KubernetesSecretProvider cut = new KubernetesSecretProvider(new MockClient());

            Maybe<SecretMap> result = cut.resolve(cut.fromURL(SecretURL.from("secret://kubernetes/secret/foo")));

            SecretMap secretMap = result.blockingGet();
            assertThat(secretMap).isNotNull();
            assertThat(secretMap.wellKnown(SecretMap.WellKnownSecretKey.USERNAME))
                .isPresent()
                .get()
                .extracting(io.gravitee.secrets.api.core.Secret::asString)
                .isEqualTo("admin");
            assertThat(secretMap.wellKnown(SecretMap.WellKnownSecretKey.PASSWORD))
                .isPresent()
                .get()
                .extracting(io.gravitee.secrets.api.core.Secret::asString)
                .isEqualTo("changeme");
        }

        @Test
        void should_watch_tls_with_mock_client_and_no_keymap() {
            this.secret = new Secret();
            this.secret.setData(Map.of("tls.key", base64("changeme"), "tls.crt", base64("admin")));
            this.flowable = Flowable.fromIterable(List.of(new Event<>("CREATED", secret)));
            KubernetesSecretProvider cut = new KubernetesSecretProvider(new MockClient());
            Flowable<SecretEvent> result = cut.watch(cut.fromURL(SecretURL.from("secret://kubernetes/secret/foo")));
            List<SecretEvent> events = result.toList().blockingGet();
            assertThat(events).hasSize(1);
            SecretMap secretMap = events.get(0).secretMap();
            assertThat(secretMap).isNotNull();
            assertThat(secretMap.wellKnown(SecretMap.WellKnownSecretKey.CERTIFICATE))
                .isPresent()
                .get()
                .extracting(io.gravitee.secrets.api.core.Secret::asString)
                .isEqualTo("admin");
            assertThat(secretMap.wellKnown(SecretMap.WellKnownSecretKey.PRIVATE_KEY))
                .isPresent()
                .get()
                .extracting(io.gravitee.secrets.api.core.Secret::asString)
                .isEqualTo("changeme");
        }

        @Test
        void should_be_able_to_watch_when_no_secret() {
            this.secret = null;
            this.flowable = Flowable.empty();
            KubernetesSecretProvider cut = new KubernetesSecretProvider(new MockClient());
            Maybe<SecretMap> resolve = cut.resolve(cut.fromURL(SecretURL.from("secret://kubernetes/secret/foo")));
            Flowable<SecretEvent> watch = cut.watch(cut.fromURL(SecretURL.from("secret://kubernetes/secret/foo")));
            resolve.test().assertComplete();
            watch.elementAt(0).test().assertComplete();
        }
    }

    private String base64(String string) {
        return Base64.getEncoder().encodeToString(string.getBytes(StandardCharsets.UTF_8));
    }
}
