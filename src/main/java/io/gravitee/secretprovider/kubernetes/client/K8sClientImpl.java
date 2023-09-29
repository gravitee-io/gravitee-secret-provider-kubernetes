package io.gravitee.secretprovider.kubernetes.client;

import io.gravitee.kubernetes.client.KubernetesClient;
import io.gravitee.kubernetes.client.api.ResourceQuery;
import io.gravitee.kubernetes.client.api.WatchQuery;
import io.gravitee.kubernetes.client.config.KubernetesConfig;
import io.gravitee.kubernetes.client.impl.KubernetesClientV1Impl;
import io.gravitee.kubernetes.client.model.v1.Event;
import io.gravitee.kubernetes.client.model.v1.Secret;
import io.gravitee.secretprovider.kubernetes.client.api.K8sClient;
import io.gravitee.secretprovider.kubernetes.config.K8sConfig;
import io.gravitee.secretprovider.kubernetes.config.K8sSecretLocation;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Benoit BORDIGONI (benoit.bordigoni at graviteesource.com)
 * @author GraviteeSource Team
 */
@Slf4j
public class K8sClientImpl implements K8sClient {

    private final K8sConfig k8sConfig;
    private final KubernetesConfig config;
    private final KubernetesClient client;

    public K8sClientImpl(K8sConfig k8sConfig) {
        this.k8sConfig = k8sConfig;
        if (k8sConfig.isClusterBased()) {
            this.config = KubernetesConfig.newInstance(null);
        } else {
            this.config = KubernetesConfig.newInstance(k8sConfig.getKubeConfigFile());
        }
        if (k8sConfig.getTimeoutMs() > 0) {
            this.config.setApiTimeout(k8sConfig.getTimeoutMs());
        }
        if (isNotBlank(k8sConfig.getNamespace())) {
            this.config.setCurrentNamespace(k8sConfig.getNamespace());
        }
        this.client = new KubernetesClientV1Impl(this.config);
    }

    private static boolean isNotBlank(String namespace) {
        return namespace != null && !namespace.isBlank();
    }

    @Override
    public K8sConfig config() {
        return k8sConfig;
    }

    public Maybe<Secret> getSecret(K8sSecretLocation location) {
        return client.get(ResourceQuery.secret(getNamespace(location), location.secret()).build());
    }

    public Flowable<Event<Secret>> watchSecret(K8sSecretLocation location) {
        return client.watch(WatchQuery.secret(getNamespace(location), location.secret()).build());
    }

    private String getNamespace(K8sSecretLocation location) {
        return isNotBlank(location.namespace()) ? location.namespace() : this.config.getCurrentNamespace();
    }
}
