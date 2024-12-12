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
