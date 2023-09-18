package io.gravitee.secretprovider.kubernetes;

import io.gravitee.node.api.secrets.SecretProvider;
import io.gravitee.node.api.secrets.SecretProviderFactory;
import io.gravitee.secretprovider.kubernetes.client.K8sClientImpl;
import io.gravitee.secretprovider.kubernetes.config.K8sConfig;

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
