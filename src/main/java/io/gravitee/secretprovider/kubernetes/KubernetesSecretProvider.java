package io.gravitee.secretprovider.kubernetes;

import io.gravitee.secretprovider.kubernetes.client.api.K8sClient;
import io.gravitee.secretprovider.kubernetes.config.K8sSecretLocation;
import io.gravitee.secrets.api.core.SecretEvent;
import io.gravitee.secrets.api.core.SecretMap;
import io.gravitee.secrets.api.core.SecretURL;
import io.gravitee.secrets.api.errors.SecretManagerException;
import io.gravitee.secrets.api.plugin.SecretProvider;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Benoit BORDIGONI (benoit.bordigoni at graviteesource.com)
 * @author GraviteeSource Team
 */
@Slf4j
public class KubernetesSecretProvider implements SecretProvider {

    private static final Map<String, SecretMap.WellKnownSecretKey> DEFAULT_WELL_KNOW_KEY_MAP = Map.of(
        "tls.crt",
        SecretMap.WellKnownSecretKey.CERTIFICATE,
        "tls.key",
        SecretMap.WellKnownSecretKey.PRIVATE_KEY,
        "username",
        SecretMap.WellKnownSecretKey.USERNAME,
        "password",
        SecretMap.WellKnownSecretKey.PASSWORD
    );
    public static final String PLUGIN_ID = "kubernetes";

    private final K8sClient client;

    public KubernetesSecretProvider(K8sClient client) {
        this.client = client;
    }

    @Override
    public Maybe<SecretMap> resolve(SecretURL secretURL) {
        K8sSecretLocation k8sLocation = fromURL(secretURL);
        return client
            .getSecret(k8sLocation)
            .map(k8sSecret -> {
                SecretMap sm = SecretMap.ofBase64(k8sSecret.getData());
                handleWellKnownSecretKeys(sm, secretURL);
                return sm;
            });
    }

    @Override
    public Flowable<SecretEvent> watch(SecretURL secretURL) {
        K8sSecretLocation k8sLocation = fromURL(secretURL);
        return client
            .watchSecret(k8sLocation)
            .map(event -> {
                if (event.getType().equals("ERROR")) {
                    throw new SecretManagerException("Kubernetes watch on %s return a error: %s".formatted(k8sLocation, event));
                }
                if (event.getType().equals("DELETED")) {
                    return new SecretEvent(SecretEvent.Type.DELETED, new SecretMap(null));
                }
                SecretMap secretMap = SecretMap.ofBase64(event.getObject().getData());
                handleWellKnownSecretKeys(secretMap, secretURL);
                return new SecretEvent(SecretEvent.Type.UPDATED, secretMap);
            });
    }

    K8sSecretLocation fromURL(SecretURL url) {
        return K8sSecretLocation.fromURL(url, client.config());
    }

    private void handleWellKnownSecretKeys(SecretMap secretMap, SecretURL secretURL) {
        secretMap.handleWellKnownSecretKeys(
            Optional.ofNullable(secretURL).map(SecretURL::wellKnowKeyMap).filter(map -> !map.isEmpty()).orElse(DEFAULT_WELL_KNOW_KEY_MAP)
        );
    }
}
