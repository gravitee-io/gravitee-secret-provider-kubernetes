package io.gravitee.secretprovider.kubernetes;

import static io.gravitee.secretprovider.kubernetes.test.TestUtils.newConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import io.gravitee.node.api.secrets.SecretProvider;
import io.gravitee.node.api.secrets.model.SecretURL;
import io.gravitee.secretprovider.kubernetes.config.K8sConfig;
import java.util.Map;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

/**
 * @author Benoit BORDIGONI (benoit.bordigoni at graviteesource.com)
 * @author GraviteeSource Team
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class KubernetesSecretProviderFactoryTest {

    @Test
    void should_create_a_new_instance() {
        SecretProvider secretProvider = new KubernetesSecretProviderFactory()
            .create(new K8sConfig(newConfig(Map.of("kubeConfigFile", "src/test/resources/config.yaml"))));
        assertThat(secretProvider).isInstanceOf(KubernetesSecretProvider.class);
        assertThatCode(() -> secretProvider.fromURL(SecretURL.from("secret://kubernetes/foo"))).doesNotThrowAnyException();
    }
}
