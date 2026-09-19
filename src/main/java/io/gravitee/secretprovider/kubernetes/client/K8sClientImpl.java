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
package io.gravitee.secretprovider.kubernetes.client;

import io.gravitee.kubernetes.client.KubernetesClient;
import io.gravitee.kubernetes.client.api.ResourceQuery;
import io.gravitee.kubernetes.client.api.WatchQuery;
import io.gravitee.kubernetes.client.config.KubernetesConfig;
import io.gravitee.kubernetes.client.exception.ResourceNotFoundException;
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
        return client
            .get(ResourceQuery.secret(getNamespace(location), location.secret()).build())
            .onErrorResumeNext(err -> {
                if (err instanceof ResourceNotFoundException) {
                    return Maybe.empty();
                }
                return Maybe.error(err);
            });
    }

    public Flowable<Event<Secret>> watchSecret(K8sSecretLocation location) {
        return client.watch(WatchQuery.secret(getNamespace(location), location.secret()).build());
    }

    private String getNamespace(K8sSecretLocation location) {
        return isNotBlank(location.namespace()) ? location.namespace() : this.config.getCurrentNamespace();
    }
}
