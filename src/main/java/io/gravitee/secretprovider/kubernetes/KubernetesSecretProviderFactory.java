package io.gravitee.secretprovider.kubernetes;

import io.gravitee.secretprovider.kubernetes.client.K8sClientImpl;
import io.gravitee.secretprovider.kubernetes.config.K8sConfig;
import io.gravitee.secrets.api.plugin.SecretProvider;
import io.gravitee.secrets.api.plugin.SecretProviderFactory;

/**
 * @author Benoit BORDIGONI (benoit.bordigoni at graviteesource.com)
 * @author GraviteeSource Team
 */
public class KubernetesSecretProviderFactory implements SecretProviderFactory<K8sConfig> {

    @Override
    public SecretProvider create(K8sConfig kubernetesConfiguration) {
        return new KubernetesSecretProvider(new K8sClientImpl(kubernetesConfiguration));
    }
}
