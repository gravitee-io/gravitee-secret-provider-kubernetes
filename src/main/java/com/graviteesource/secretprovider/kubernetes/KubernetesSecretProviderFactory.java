package com.graviteesource.secretprovider.kubernetes;

import com.graviteesource.secretprovider.kubernetes.client.K8sClientImpl;
import com.graviteesource.secretprovider.kubernetes.config.K8sConfig;
import io.gravitee.node.api.secrets.SecretProvider;
import io.gravitee.node.api.secrets.SecretProviderFactory;

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
