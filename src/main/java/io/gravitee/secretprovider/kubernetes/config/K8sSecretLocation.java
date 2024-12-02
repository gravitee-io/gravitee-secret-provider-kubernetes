package io.gravitee.secretprovider.kubernetes.config;

import io.gravitee.secrets.api.core.SecretLocation;
import io.gravitee.secrets.api.core.SecretURL;
import java.util.Map;
import java.util.Objects;

/**
 * @author Benoit BORDIGONI (benoit.bordigoni at graviteesource.com)
 * @author GraviteeSource Team
 */
public record K8sSecretLocation(String namespace, String secret, String key) {
    static final String LOCATION_NAMESPACE = "namespace";
    static final String LOCATION_SECRET = "secret";
    static final String LOCATION_KEY = "key";

    public Map<String, Object> asMap() {
        if (key == null) {
            return Map.of(LOCATION_NAMESPACE, namespace, LOCATION_SECRET, secret);
        }
        return Map.of(LOCATION_NAMESPACE, namespace, LOCATION_SECRET, secret, LOCATION_KEY, key);
    }

    public static K8sSecretLocation fromLocation(SecretLocation location) {
        return new K8sSecretLocation(
            Objects.requireNonNull(location.get(LOCATION_NAMESPACE)),
            Objects.requireNonNull(location.get(LOCATION_SECRET)),
            location.get(LOCATION_KEY)
        );
    }

    public static K8sSecretLocation fromURL(SecretURL url, K8sConfig k8sConfig) {
        String namespace = url.query().get(SecretURL.WellKnownQueryParam.NAMESPACE).stream().findFirst().orElse(k8sConfig.getNamespace());
        return new K8sSecretLocation(namespace, url.path(), url.key());
    }
}
