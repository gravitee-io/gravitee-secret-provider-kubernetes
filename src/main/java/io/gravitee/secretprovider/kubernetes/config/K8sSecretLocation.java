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

import io.gravitee.secrets.api.core.SecretURL;

/**
 * @author Benoit BORDIGONI (benoit.bordigoni at graviteesource.com)
 * @author GraviteeSource Team
 */
public record K8sSecretLocation(String namespace, String secret, String key) {
    public static K8sSecretLocation fromURL(SecretURL url, K8sConfig k8sConfig) {
        String namespace = url.query().get(SecretURL.WellKnownQueryParam.NAMESPACE).stream().findFirst().orElse(k8sConfig.getNamespace());
        return new K8sSecretLocation(namespace, url.path(), url.key());
    }
}
